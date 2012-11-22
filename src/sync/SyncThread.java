package sync;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * Synchronizes the LibraryDatabase with a given directory. Each song whose
 * sync-flag is set is synchronized.
 * 
 * @author Peter Oltmann
 * 
 */
public class SyncThread implements Runnable {
    
    private static SyncThread sync; // singleton
    
    private Thread tp; // thread pointer
    private GUI gui; // the controller for callback
    private String srcPath;
    private String dstPath;
    private List<Song> lib;
    
    /**
     * Constructs and starts a new sync thread (runnable) to copy the files.
     * 
     * @param controller the GUI to tell when ready
     */
    private SyncThread(GUI controller, String srcPath, String dstPath, List<Song> lib) {
        this.gui = controller;
        this.srcPath = srcPath;
        this.dstPath = dstPath;
        this.lib = lib;
        this.tp = new Thread(this);
        this.tp.start();
    }
    
    /**
     * Start synchronization-thread (runnable) if not already running an get an
     * instance (singleton).
     * 
     * @param gui the gui to tell when ready
     * @return the instance of the sync thread (runnable)
     */
    public static SyncThread startSync(GUI controller, String srcPath, String dstPath, List<Song> lib) {
        if (sync == null) {
            sync = new SyncThread(controller, srcPath, dstPath, lib);
        }
        return sync;
    }
    
    /**
     * Deletes all hidden files in the specified directory.
     * @param dir the directory from where the hidden files are supposed to be
     * deleted.
     */
    private void deleteHiddenFiles(File dir) {
        if (!dir.isDirectory()) {
            return;
        }
        
        File[] hidden = dir.listFiles(
            new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isHidden();
                }
            });
            
        if (hidden != null) {
            for (File f : hidden) f.delete();
        }
    }
    
    /**
     * Copy a file
     * @param src the absolute filepath of the file to be copied
     * @param dst the absolute filepath of the destionation file
     */
    private void copyFiles(File src, File dst) {
        FileChannel srcChannel = null;
        FileChannel dstChannel = null;
        try {
            dst.createNewFile();
            
            srcChannel = new FileInputStream(src).getChannel();
            dstChannel = new FileOutputStream(dst).getChannel();
            srcChannel.transferTo(0, srcChannel.size(), dstChannel);
        } catch (IOException e) {
            System.out.println("Copying files failed (see stack trace for details)");
            dst.delete();
            e.printStackTrace();
        } finally {
            try {
                if (srcChannel != null)
                    srcChannel.close();
                if (dstChannel != null)
                    dstChannel.close();
            } catch (IOException e) {}
        }
    }

    @Override
    public void run() {
        
        File syncDir = new File(dstPath);
        if (!syncDir.isDirectory()) {
            sync = null; // "destroy instance"
            gui.setStatus("no such directory: " + syncDir);
            gui.signalSyncReady();
            
            return; // error
        }
        
        gui.setStatus("syncing");
        
        File dir, src, dst;
        for (Song s : lib) {
            dir = new File(dstPath + s.getFilepath());
            src = new File(srcPath + s.getFilepath() + s.getFilename());
            dst = new File(dstPath + s.getFilepath() + s.getFilename());
            
//            System.out.println("dir: " + dir);
//            System.out.println("src: " + src);
//            System.out.println("dst: " + dst);
//            
//            sync = null; // "destroy instance"
//            gui.setStatus("ready");
//            gui.signalSyncReady();
//            if (true) return;
            
            // to be synced ----------------------------------------------------
            if (s.isSync()) {
                if (!dir.exists()) { // create filepath it doesn't exists
                    dir.mkdirs();
                }
                
//                System.out.println(new Date(src.lastModified()));
//                System.out.println(new Date(dst.lastModified()));
                
                // setModified has only two seconds accuracy on FAT32,
                // sameTime takes place for: !s.getModdate().equals(dstTimestamp)
                boolean sameTime = Math.abs(s.getModdate().getTime()
                        - dst.lastModified()) <= 2000;
                
                // check existance, moddate and size
                if (!dst.exists() || !sameTime || s.getSize() != dst.length()) {
                    gui.setStatus("copying " + s.getFilename());
                    copyFiles(src, dst);
                    dst.setLastModified(src.lastModified());
//                    System.out.printf("src: %s    dst: %s", s.getModdate(), dstTimestamp);
//                    System.out.println();
                }
            // not to be synced ------------------------------------------------   
            } else {
                // delete file
                if (dst.exists()) {
                    gui.setStatus("deleting " + s.getFilename());
                    dst.delete();
                }
                
                // delete all hidden files
                deleteHiddenFiles(dir);
                deleteHiddenFiles(dir.getParentFile());
                
                // only if empty: delete directories
                dir.delete();
                dir.getParentFile().delete();
            }
        }
        
        sync = null; // "destroy instance"
        gui.setStatus("ready");
        gui.signalSyncReady();
    }
    
}
