package sync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Playlist {
    
    // =========================================================================
    // = Declarations                                                          =
    // =========================================================================
    
    private File file;
    private List<String> list;
    
    // =========================================================================
    // = Constructors                                                          =
    // =========================================================================
    
    public Playlist(String fileName) {
        file = new File(fileName);
        list = new ArrayList<String>();
    }
    
    // =========================================================================
    // = Methods                                                               =
    // =========================================================================
    
    void readIn() {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            list.add(in.readLine());
        } catch (FileNotFoundException e) { // new BufferedReader
            e.printStackTrace();
        } catch (IOException e) { // readLine
            e.printStackTrace();
        } finally {
            if (in != null)
                try { in.close(); } catch (IOException e) {}
        }
    }
    
}
