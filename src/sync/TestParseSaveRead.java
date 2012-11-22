package sync;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;

public class TestParseSaveRead {
    
    public static void main(String[] args) {
        
        final String LIBRARY_PATH = "/Users/Peter/Music/iTunes/iTunes Music/";
        final String LIB_IN = "/Users/Peter/Desktop/library_in.txt";
        final String LIB_IN_TINY = "/Users/Peter/Desktop/library_in_tiny.txt";
        final String LIB_OUT = "/Users/Peter/Desktop/library_out.txt";
        final String LIB_OUT_TINY = "/Users/Peter/Desktop/library_out_tiny.txt";
        final String SONG_DATA = "/Users/Peter/Desktop/songs.data";
        
        List<Song> songs = null;
        
        ////////////////////////////////////////////////////////////////////////
        // PARSE LIB AND SAVE OBJECT                                          //
        ////////////////////////////////////////////////////////////////////////
        
        // /*
        
        ObjectOutputStream oos = null;
        try {
            // parse library
            System.out.print("Parsing library... ");
            songs = Library.fill(LIBRARY_PATH);
            Collections.sort(songs);
            System.out.println("done.");
            
            // print to file
            System.out.print("Printing to file '" + LIB_IN + "'... ");
            Library.printToFile(songs, LIB_IN);
            Library.printToFileTiny(songs, LIB_IN_TINY);
            System.out.println("done.");
            
            // write object to file
            System.out.print("Writing object to '" + SONG_DATA + "'...");
            Library.saveToFile(songs, SONG_DATA);
            System.out.println("done.");
        } catch (FileNotFoundException e1) { // parse, printLibToFile, stream
            e1.printStackTrace();
            System.exit(1);
        } catch (IOException e) { // wirteObject
            e.printStackTrace();
            System.exit(1);
        } finally {
            try { if (oos != null) oos.close(); } catch (IOException e) {}
        }
        
        // */
        
        
        ////////////////////////////////////////////////////////////////////////
        // READ OBJECT                                                        //
        ////////////////////////////////////////////////////////////////////////

        // /*
        
        ObjectInputStream ois = null;
        try {
            // read object from file
            System.out.print("Reading object from '" + SONG_DATA + "'...");
            songs = Library.readFromFile(SONG_DATA);
            Collections.sort(songs);
            System.out.println(" done.");
            
            // print to file
            System.out.print("Printing to file '" + LIB_OUT + "'... ");
            Library.printToFile(songs, LIB_OUT);
            Library.printToFileTiny(songs, LIB_OUT_TINY);
            System.out.println("done.");
        } catch (FileNotFoundException e) { // stream, printLibToFile
            e.printStackTrace();
        } catch (IOException e) { // readObject
            e.printStackTrace();
        } catch (ClassNotFoundException e) { // readObject
            e.printStackTrace();
        } finally {
            try { if (ois != null) ois.close(); } catch (IOException e) {}
        }
        
        // */
        
    }
    
}
