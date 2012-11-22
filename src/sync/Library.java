package sync;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data structure for the iTunes Library, which holds Song objects.
 * 
 * @author Peter Oltmann
 * 
 */
public class Library {
    
    public static List<Song> fill(String libPath) {
        List<Song> library = new ArrayList<Song>();
        File libDir = new File(libPath);
        
        File[] firstLevel = libDir.listFiles();
        if (firstLevel == null) {
            return library;
        }
        for (File elem1 : firstLevel) { // first level -> interpret
            if (!elem1.isHidden() && !elem1.getName().equals("Movies")) {
                assert elem1.isDirectory();
                String interpret = elem1.getName();
                
                File[] secondLevel = elem1.listFiles();
                for (File elem2 : secondLevel) { // second level -> album
                    if (!elem2.isHidden()) {
                        assert elem2.isDirectory();
                        String album = elem2.getName();
                    
                        File[] thirdLevel = elem2.listFiles();
                        for (File elem3 : thirdLevel) { // third level -> title
                            if (!elem3.isHidden()) {
                                assert elem3.isFile();
                                String title = elem3.getName();
                                long moddate = elem3.lastModified();
                                int trackno;
                                String filepath = elem3.getAbsolutePath();
                                try {                                    
                                    trackno = Integer.parseInt(title.substring(0, 2));
                                    title = title.substring(3, title.length()-4); // trackno and .mp3
                                } catch (NumberFormatException e) {
                                    trackno = 0;
                                    title = title.substring(0, title.length()-4); // just .mp3
                                }
                                
                                Song song = new Song();
                                song.setTrackno(trackno);
                                song.setTitle(title);
                                song.setInterpret(interpret);
                                song.setAlbum(album);
                                song.setSize(elem3.length());
                                song.setModdate(new Timestamp(moddate));
                                song.setSync(false);
                                song.setFilepath(filepath.substring(libPath.length(), filepath.length()-elem3.getName().length()));
                                song.setFilename(elem3.getName());
                                
                                library.add(song);
                            }
                        }
                    }
                    
                }
                
            }
        }
        return library;
    }
    
    public static void printToFile(List<Song> songs, String fileName) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(new File(fileName));

        for (Song song : songs) {
            out.println(song.prettyToString());
            out.println();
        }

        out.printf("Number of songs: %d", songs.size());
        out.flush();
        out.close();
    }
    
    public static void printToFileTiny(List<Song> songs, String fileName) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(new File(fileName));

        out.printf("%3s    %-30s    %-30s    %-30s", "#", "Title", "Interpret", "Album");
        out.println();
        for (Song song : songs) {
            out.println(song.tinyToString());
        }

        out.println();
        out.printf("Number of songs: %d", songs.size());
        out.flush();
        out.close();
    }
    
    public static void saveToFile(List<Song> songs, String fileName) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
        oos.writeObject(songs);
        try { oos.close(); } catch (IOException e) {}
    }
    
    public static List<Song> readFromFile(String fileName) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
        @SuppressWarnings("unchecked")
        List<Song> songs = (List<Song>) ois.readObject();
        try { ois.close(); } catch (IOException e) {}
        return songs;
    }
    
    public static void makeSaveFile(String fileName) {
        File file = new File(fileName);
        File save = new File(fileName + ".save");
        file.renameTo(save);
    }
    
    public static void main(String[] args) {
        List<Song> songs = Library.fill("C:\\Users\\Peter\\Music\\iTunes\\iTunes Media\\Music");
        
        try {
            Library.printToFile(songs, "C:\\Users\\Peter\\Desktop\\lib.txt");
                    
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
