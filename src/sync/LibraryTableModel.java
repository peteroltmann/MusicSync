package sync;

import static sync.Preferences.*;

import javax.swing.table.AbstractTableModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class LibraryTableModel extends AbstractTableModel {
    
    private static final long serialVersionUID = 1L;
    
//    final String LIBRARY_PATH = "C:\\Users\\Peter\\Music\\iTunes\\iTunes Media\\Music";
//    final String SYNC_PATH = "/Users/Peter/Desktop/SYNC/";
//    final String SYNC_PATH = "/Volumes/NO NAME/Music/";
    Preferences prefs;
    
    private final GUI gui_;
    private String[] colNames;
    private List<Song> lib;
    private List<Song> displayed; // key: list index
    
    /**
     * Constructs a new {@link LibraryTableModel}.
     * @param gui the GUI that uses this model.
     * @throws FileNotFoundException.
     */
    public LibraryTableModel(final GUI gui) throws FileNotFoundException {
        gui_ = gui;
        colNames = new String[] {"Sync", "Size", "#", "Title", "Interpret", "Album"};
        
        File userData = new File(USER_HOME + "/" + PREFS_NAME);
        if (!userData.exists()) {
            userData.mkdir();
        }
        
        try {
            prefs = Preferences.readFromFile(USER_HOME + "/" + PREFS_NAME + "/" + PREFS_FILENAME);
        } catch (ClassNotFoundException | IOException e2) {
            prefs = new Preferences();
        }
        
        String songsPath = USER_HOME + "/" + PREFS_NAME + "/" + SONGS_FILENAME;
        try { // 1. try from file
            lib = Library.readFromFile(songsPath);
            Library.makeSaveFile(songsPath); // only if 1. succeeded
        } catch (IOException e) {
            try { // 2. try from save file
                lib = Library.readFromFile(songsPath + ".save");
            } catch (IOException e1) { // 3. (try) parse library
                lib = Library.fill(prefs.getLibraryDir()); // 4. throw (on fail)
                Collections.sort(lib);
            } catch (ClassNotFoundException e1) { // (Song)
                e1.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } // (Song)
        
        displayed = lib;
    }

    @Override
    public int getRowCount() {
        return displayed.size();
    }
    
    @Override
    public String getColumnName(int col) {
        return colNames[col];
    }

    @Override
    public int getColumnCount() {
        return colNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object retVal;
        switch (columnIndex) {
            case 0:
                retVal = displayed.get(rowIndex).isSync() ? "\u2713" : "-";
                // BALLOT BOX WITH CHECK: \u2611
                // HEAVY CHECK MARK \u2714
                // CHECK MARK \u2713
                break;
            case 1:
                double size = displayed.get(rowIndex).getSize()/(1024D*2024D);
                retVal = String.format("%1.2f MB", size);
                break;
            case 2:
                retVal = displayed.get(rowIndex).getTrackno();
                break;
            case 3:
                retVal = displayed.get(rowIndex).getTitle();
                break;
            case 4: 
                retVal = displayed.get(rowIndex).getInterpret();
                break;
            case 5:
                retVal = displayed.get(rowIndex).getAlbum();
                break;
            default:
                retVal = null;
                break;
        }
        return retVal;
    }
    
    /**
     * Update the sync field in database.
     * @param row the row of the entry
     * @param sync if entry is to be synced or not
     */
    public void checkSync(int[] row, boolean sync) {
        for (int i = 0; i < row.length; i++) {
            displayed.get(row[i]).setSync(sync); // update sync flag
            fireTableCellUpdated(row[i], 0); // tell gui
        }
        // save library
        startSaveThread();
    }
    
    public void sync() {
        SyncThread.startSync(gui_, prefs.getLibraryDir(), prefs.getSyncDir(), lib);
    }
    
    /**
     * Rescan library.
     * @throws FileNotFoundException
     */
    public void rescan() throws FileNotFoundException {
        // TODO 
        // Song ID no longer needed
        // pathEqual to Song
        // not done because of serializable
        
        List<Song> parsed = Library.fill(prefs.getLibraryDir());
        
        ListIterator<Song> libIt;
        ListIterator<Song> parsedIt;
        Song libElem, parsedElem;
        boolean found;
        
        // deleted all no more existing elements (check filepath + filename)
        // also update all still existing elements
        libIt = lib.listIterator();
        while (libIt.hasNext()) {
            libElem = libIt.next();
            found = false;
            parsedIt = parsed.listIterator();
            while (parsedIt.hasNext() && !found) {
                parsedElem = parsedIt.next();
                if (libElem.pathEquals(parsedElem)) { // libElem still exists
                    found = true;
                    // update libElem
                    libIt.set(
                        new Song(
                            parsedElem.getTrackno(),
                            parsedElem.getTitle(),
                            parsedElem.getInterpret(),
                            parsedElem.getAlbum(),
                            parsedElem.getSize(),
                            parsedElem.getModdate(),
                            libElem.isSync(), // keep sync flag
                            parsedElem.getFilepath(),
                            parsedElem.getFilename()
                        )
                    );
                }
            }
            if (!found) { // libElem does not exists anymore
                libIt.remove();
            }
        }
        
        // add the new elements from parsedLib
        for (Song parsedSong : parsed) {
            found = false;
            for (Song libSong : lib ) {
                if (parsedSong.pathEquals(libSong)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                lib.add(parsedSong);
            }
        }
        
        Collections.sort(lib);
        fireTableDataChanged();
        startSaveThread();
    }
    
    /**
     * Reset library: clear song list.
     */
    public void reset() {
        lib.clear();
        fireTableDataChanged();
        startSaveThread();
    }
    
    /**
     * Searches the library for songs that contains the given string
     * @param searchString the string to be searched
     */
    public void search(String searchString) {
        if (searchString.equals("")) {
            displayed = lib;
            fireTableDataChanged();
            return;
        }

        // split words
        int last = 0;
        char[] c = searchString.toCharArray();
        List<String> searchWords = new ArrayList<String>();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') { // whitespace found
//                System.out.println(searchString.substring(last, i));
                searchWords.add(searchString.substring(last, i));
                last = i + 1; // begin-index is inclusive
            } else if (i == c.length - 1) { // end of string
//                System.out.println(searchString.substring(last));
                searchWords.add(searchString.substring(last));
            }
        }
  
        // search lib to create new diplayed
        displayed = new ArrayList<Song>();
        for (int i = 0; i < lib.size(); i++) {
            // build string of fields to be searched
            String songString = String.format("%s-%s-%s", lib.get(i).getTitle(),
                    lib.get(i).getInterpret(), lib.get(i).getAlbum());
            
            // songString has to contain each word
            boolean found = true;
            for (String word : searchWords) {
                if (!songString.toLowerCase().contains(word.toLowerCase()))
                    found = false;
            }
            
            if (found)
                displayed.add(lib.get(i));
        }
        fireTableDataChanged();
    }
    /*
    private boolean pathEqual(Song s1, Song s2) {
        String loc1 = s1.getFilepath() + s1.getFilename();
        String loc2 = s2.getFilepath() + s2.getFilename();
        return loc1.equals(loc2);
    }
    */
    
    private void startSaveThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    gui_.setStatus("Updating file...");
                    Library.saveToFile(lib, USER_HOME + "/" + PREFS_NAME + "/" + SONGS_FILENAME);
                    gui_.setStatus("ready");
                } catch (FileNotFoundException e) {
                    gui_.setStatus("File not found: " + e.getMessage());
                    // e.printStackTrace();
                } catch (IOException e) {
                    gui_.setStatus("IO: " + e.getMessage());
                    // e.printStackTrace();
                }
            }
        }).start();
    }
    
    public long getCheckedSize() {
        long size = 0;
        for (Song s : lib) {
            if (s.isSync())
                size += s.getSize();
        }
        return size;
    }
    
    public long getFreeSpace() {
        File file = new File(prefs.getSyncDir());
        return file.getFreeSpace();
    }
}
