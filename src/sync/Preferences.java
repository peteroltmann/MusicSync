package sync;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

import com.google.gson.Gson;

public class Preferences implements Serializable {

    private static final long serialVersionUID = -9058218290734003556L;
    
    // path and file/dir constants
    public static final String USER_HOME = System.getProperty("user.home");
    public static final String PREFS_NAME = "Sync";
    public static final String PREFS_FILENAME = "prefs.json";
    public static final String SONGS_FILENAME = "songs.json";
    
    // =========================================================================
    // Static methods                                                          =
    // =========================================================================
    
    public static void saveToFile(Preferences prefs) throws FileNotFoundException, IOException {
        FileWriter out = new FileWriter(new File(USER_HOME + "/" + PREFS_NAME + "/" + PREFS_FILENAME));
        Gson gson = new Gson();
        gson.toJson(prefs, out);
        out.close();
    }
    
    public static Preferences readFromFile(String fileName) throws IOException, ClassNotFoundException {
        FileReader in = new FileReader(new File(fileName));
        Gson gson = new Gson();
        Preferences prefs = gson.fromJson(in, Preferences.class);
        in.close();
        return prefs;
    }
    
    // preferences
    private String libraryDir;
    private String syncDir;
    private String listsDir;
    private String listsSyncDir;
    
    // =========================================================================
    // Constructors                                                            =
    // =========================================================================
    
    public Preferences() {
        libraryDir = "";
        syncDir = "";
        listsDir = "";
        listsSyncDir = "";
    }
    
    // =========================================================================
    // Getters and setters                                                     =
    // =========================================================================
    
    /**
     * Gets the library directory string.
     * 
     * @return  the absolute library path.
     */
    public String getLibraryDir() {
        return libraryDir;
    }

    /**
     * Sets the library directory.
     * 
     * @param   libraryDir  the absolute library path
     */
    public void setLibraryDir(String libraryDir) {
        this.libraryDir = libraryDir;
    }

    /**
     * Gets the sync directory.
     * 
     * @return  the absolute sync path.
     */
    public String getSyncDir() {
        return syncDir;
    }

    /**
     * Sets the sync directory.
     * @param   syncDir the absolute sync path.
     */
    public void setSyncDir(String syncDir) {
        this.syncDir = syncDir;
    }

    /**
     * Gets the playlists directory.
     * @return  the absolute playlists path.
     */
    public String getListsDir() {
        return listsDir;
    }

    /**
     * Sets the playlists directory.
     * @param   listsDir    the absolute playlists path.
     */
    public void setListsDir(String listsDir) {
        this.listsDir = listsDir;
    }

    /**
     * Gets the playlists sync directory.
     * @return  the absolute playlists path.
     */
    public String getListsSyncDir() {
        return listsSyncDir;
    }

    /**
     * Sets the playlists sync directory.
     * @param   listsSyncDir    the absolute playlists path.
     */
    public void setListsSyncDir(String listsSyncDir) {
        this.listsSyncDir = listsSyncDir;
    }
    
}
