package playlists;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Playlist {

    // FEFF because this is the Unicode char represented by the UTF-8 byte order
    // mark (EF BB BF).
    public static final String UTF8_BOM = "\uFEFF";
    
    private String _name; // the name of the playlist
    private List<String> _elements;
    
    public Playlist(String name) {
        _name = name;
    }
    
    public Playlist() {
        this("Untitled Playlist");
    }
    
    public void readFromFile(String fileName) {
        _elements = new ArrayList<String>();
        BufferedReader in = null;
        String charsetName = "UTF-8";
        boolean firstLine = true;
        try {
            if (fileName.substring(fileName.length()-4, fileName.length()).equals("m3u8")) {
                charsetName = "UTF-8";
            } else if (fileName.substring(fileName.length()-3, fileName.length()).equals("m3u")) {
                charsetName = "ISO-8859-1";
            } else {
                return;
            }
            
            in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), charsetName));
            String line;
            while ((line = in.readLine()) != null) {
                if (firstLine) {
                    line = removeUTF8BOM(line); // remove BOM if present
                }
                if (line.contains("\\")) { // convert windows backslashes
                    line = line.replace("\\", "/");
                }
                _elements.add(line);
            }
            
        } catch (FileNotFoundException e) { // open file stream
            System.out.println("Could no open file: " + e.getMessage());
        } catch (UnsupportedEncodingException e) { // with encoding
            System.err.println("Not supported encoding: " + e.getMessage());
            return;
        } catch (IOException e) { // readLine
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (in != null) try { in.close(); } catch (IOException e) {}
        }
    }
    
    public void writeToFile(String fileName) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(fileName)), "UTF-8"));
            for (String elem : _elements) {
                out.println(elem);
            }
            out.flush();
        } catch (FileNotFoundException e) { // open file stream
            System.out.println("Could no open file: " + e.getMessage());
        } catch (UnsupportedEncodingException e) { // with encoding
            System.err.println("Not supported encoding: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (out != null) out.close();
        }
    }
    
    /**
     * Converts a playlist. Replaces the a specified an old pre-path with a
     * specified new pre-path.
     * 
     * @param   oldPrePath  the old pre-path
     * @param   newPrePath  the new pre-path
     * @param   windows     weather the destination ist a windows playlist.
     *                      Meaning: "\" is used in the path instead of "/".
     */
    public void convert(String oldPrePath, String newPrePath, boolean windows) {
        for (int i = 0; i < _elements.size(); i++) {
            String elem = _elements.get(i);
            elem = newPrePath + elem.substring(oldPrePath.length(), elem.length());
            if (windows) {
                elem = elem.replace("/", "\\");
            }
            _elements.set(i, elem);
        }
    }
    
    public String getName() {
        return _name;
    }
    
    public boolean isEmpty() {
        return _elements.isEmpty();
    }
    
    public void printList() {
        System.out.println("Playlist: \"" + _name + "\"");
        System.out.println("=================================================");
        for (String elem : _elements) {
            System.out.println(elem);
        }
    }

    public String fileNamesToString() {
        final String LF = System.getProperty("line.separator");
        String str = "";
        str += "Playlist: \"" + _name + "\"" + LF;
        str += "=================================================" + LF;
        for (String elem : _elements) {
            str += new File(elem).getName() + LF;
        }
        return str;
    }
    
    public String toString() {
        return _name;
    }
    
    private static String removeUTF8BOM(String str) {
        if (str.startsWith(UTF8_BOM)) {
            str = str.substring(1);
        }
        return str;
    }
    
    public static void main(String[] args) {
        
        if (args.length < 4 || args.length > 7) {
            System.out.println("Arguments: <old pre-path> <new pre-path>" +
            		           "<source directory> <destination directory>" +
            		           "<included name> [windows: <1|0>] [log: <1|0>]");
            return;
        }

        final String OLDPREPATH = args[0];
        final String NEWPREPATH = args[1];
        final String SRCDIR = args[2];
        final String DSTDIR = args[3];
        final String INCLUDEDNAME = args[4];
        final boolean WINDOWS = args.length >= 6 && args[5].equals("1");
        final boolean LOG = args.length == 7 && args[6].equals("1");
        
        List<Playlist> playlists = new ArrayList<Playlist>();
        File[] playlistFiles = new File(SRCDIR).listFiles();
        for (File file : playlistFiles) {
            String path = file.getAbsolutePath();
            path = path.replace("\\", "/"); // windows...
            if (!path.substring(path.length()-4, path.length()).equals("m3u8") &&
                !path.substring(path.length()-3, path.length()).equals("m3u")) {
                continue;
            }
            
            int nameBegin = path.lastIndexOf("/") + 1;
            int nameEnd = path.lastIndexOf(".");
            String name = path.substring(nameBegin, nameEnd);
            Playlist pl = new Playlist(name);
            pl.readFromFile(path);
            playlists.add(pl);
        }
        
        for (Playlist list : playlists) {
            list.convert(OLDPREPATH, NEWPREPATH, WINDOWS);
            String nameToAdd = "";
            if (!INCLUDEDNAME.equals("")) {
                nameToAdd = " (" + INCLUDEDNAME + ")";
            }
            list.writeToFile(DSTDIR + list.getName() + nameToAdd + ".m3u8");
            
            if (LOG) {
                list.printList();
                System.out.println();
            }
        }

        if (LOG) {
            System.out.println("Saved playlists to " + DSTDIR);
        }
        
//        Playlist playlist = new Playlist("Test");
//        playlist.readFromFile("C:/Users/Peter/Music/Playlists/Royal Fam etc.m3u8");
//        playlist.convert("C:/Users/Peter/Music/iTunes/iTunes Media", "..");
//        playlist.printList();
//        playlist.writeToFile("C:/Users/Peter/Sync/Playlists/" + playlist.getName() + " (Sync).m3u8");
    }
    
}
