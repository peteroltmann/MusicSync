package sync;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Data structure for a Song from the iTunes Library
 * 
 * TODO: new serialVersionUID when possible...
 *       (only removed id from constructors and added pathEquals method)
 * 
 * @author Peter Oltmann
 */
public class Song implements Serializable, Comparable<Song> {

    private static final long serialVersionUID = -393471836602869617L;
    
    ////////////////////////////////////////////////////////////////////////////
    // Members                                                                //
    ////////////////////////////////////////////////////////////////////////////
    
    private int trackno_;
    private String title_;
    private String interpret_;
    private String album_;
    private long size_; // bytes
    private Timestamp moddate_; // modification date
    private boolean sync_; // flag if this song is supposed to be synced
    private String filepath_;
    private String filename_;

    ////////////////////////////////////////////////////////////////////////////
    // Constructors                                                           //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a new Song object, which represents a song in the iTunes
     * Library.
     */
    public Song() {
        this(0, "", "", "", 0, new Timestamp(0), false, "", "");
    }

    /**
     * Constructs a new Song object, which represents a song in the iTunes
     * Library.
     * 
     * @param trackno the number of the song
     * @param title the title of the song
     * @param interpret the interpret's name
     * @param album the name of the album
     * @param size the size in bytes
     * @param moddate the modification date
     * @param sync if the song is supposed to be synchronized
     */
    public Song(int trackno, String title, String interpret, String album, long size, Timestamp moddate, boolean sync, String filepath, String filename) {
        trackno_ = trackno;
        title_ = title;
        interpret_ = interpret;
        album_ = album;
        size_ = size;
        moddate_ = moddate;
        sync_ = sync;
        filepath_ = filepath;
        filename_ = filename;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Getters and setters                                                    //
    ////////////////////////////////////////////////////////////////////////////
    
    public int getTrackno() {
        return trackno_;
    }

    public void setTrackno(int trackno) {
        trackno_ = trackno;
    }

    public String getTitle() {
        return title_;
    }

    public void setTitle(String title) {
        title_ = title;
    }

    public String getInterpret() {
        return interpret_;
    }

    public void setInterpret(String interpret) {
        interpret_ = interpret;
    }

    public String getAlbum() {
        return album_;
    }

    public void setAlbum(String album) {
        album_ = album;
    }

    public long getSize() {
        return size_;
    }

    public void setSize(long size) {
        size_ = size;
    }

    public Timestamp getModdate() {
        return moddate_;
    }

    public void setModdate(Timestamp moddate) {
        moddate_ = moddate;
    }

    public boolean isSync() {
        return sync_;
    }

    public void setSync(boolean sync) {
        sync_ = sync;
    }

    public String getFilepath() {
        return filepath_;
    }

    public void setFilepath(String filepath) {
        filepath_ = filepath;
    }
    
    public void setFilename(String filename) {
        filename_ = filename;
    }
    
    public String getFilename() {
        return filename_;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // methods                                                                //
    ////////////////////////////////////////////////////////////////////////////
    
    public boolean pathEquals(Song other) {
        String loc1 = this.getFilepath() + this.getFilename();
        String loc2 = other.getFilepath() + other.getFilename();
        return loc1.equals(loc2);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // compareTo, equals, hashCode and toString                               //
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public int compareTo(Song o) {
        return (filepath_ + filename_).compareTo(o.filepath_ + o.filename_);
    }
    
    public String tinyToString() {
        int noc = 30;   // display - width (number of chars)
        int len = 0;
        String format = "%3d"           + "    " +
                        "%-"+noc+"s"    + "    " +
                        "%-"+noc+"s"    + "    " +
                        "%-"+noc+"s";
        
        String title = title_;
        String interpret = interpret_;
        String album = album_;
        
        // cut strings
        len = title.length();
        title = title.substring(0, len < noc ? len : noc);
        len = interpret.length();
        interpret = interpret.substring(0, len < noc ? len : noc);
        len = album.length();
        album = album.substring(0, len < noc ? len : noc);
        
        return String.format(format, trackno_, title, interpret, album);
        
    }
    
    public String prettyToString() {
        final String LF = System.getProperty("line.separator");
        final String format = "%-9s : %s";
        
        String str = "Song" + LF;
        str += "=============================================================" +
               "===================" + LF;
        str += String.format(format + LF, "trackno", trackno_);
        str += String.format(format + LF, "title", title_);
        str += String.format(format + LF, "interpret", interpret_);
        str += String.format(format + LF, "album", album_);
        str += String.format(format + LF, "size", size_);
        str += String.format(format + LF, "moddate", moddate_);
        str += String.format(format + LF, "sync", sync_);
        str += String.format(format + LF, "filepath", filepath_);
        str += String.format(format, "filename", filename_);
        
        return str;
    }

    @Override
    public String toString() {
        return "Song [trackno=" + trackno_ + ", title=" + title_
                + ", interpret=" + interpret_ + ", album=" + album_ + ", size="
                + size_ + ", moddate=" + moddate_ + ", sync=" + sync_
                + ", filepath=" + filepath_ + ", filename=" + filename_ + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((album_ == null) ? 0 : album_.hashCode());
        result = prime * result
                + ((filename_ == null) ? 0 : filename_.hashCode());
        result = prime * result
                + ((filepath_ == null) ? 0 : filepath_.hashCode());
        result = prime * result
                + ((interpret_ == null) ? 0 : interpret_.hashCode());
        result = prime * result + ((moddate_ == null) ? 0 : moddate_.hashCode());
        result = prime * result + (int) (size_ ^ (size_ >>> 32));
        result = prime * result + (sync_ ? 1231 : 1237);
        result = prime * result + ((title_ == null) ? 0 : title_.hashCode());
        result = prime * result + trackno_;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Song other = (Song) obj;
        if (album_ == null) {
            if (other.album_ != null)
                return false;
        } else if (!album_.equals(other.album_))
            return false;
        if (filename_ == null) {
            if (other.filename_ != null)
                return false;
        } else if (!filename_.equals(other.filename_))
            return false;
        if (filepath_ == null) {
            if (other.filepath_ != null)
                return false;
        } else if (!filepath_.equals(other.filepath_))
            return false;
        if (interpret_ == null) {
            if (other.interpret_ != null)
                return false;
        } else if (!interpret_.equals(other.interpret_))
            return false;
        if (moddate_ == null) {
            if (other.moddate_ != null)
                return false;
        } else if (!moddate_.equals(other.moddate_))
            return false;
        if (size_ != other.size_)
            return false;
        if (sync_ != other.sync_)
            return false;
        if (title_ == null) {
            if (other.title_ != null)
                return false;
        } else if (!title_.equals(other.title_))
            return false;
        if (trackno_ != other.trackno_)
            return false;
        return true;
    }
    
}
