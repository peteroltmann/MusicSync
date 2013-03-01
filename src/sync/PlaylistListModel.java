package sync;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import playlists.Playlist;

public class PlaylistListModel extends AbstractListModel<Playlist> {
    
    private static final long serialVersionUID = 7835329540464689540L;
    
    private List<Playlist> _playlists;
    
    public PlaylistListModel() {
        _playlists = new ArrayList<Playlist>();
    }
    
    public void convertAndSync() {
        // TODO: Preferences global for all models, singleton
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                
            }
        }).start();
    }
    
    public boolean add(Playlist e) {
        boolean retVal = _playlists.add(e);
        fireContentsChanged(this, _playlists.size()-1, _playlists.size()-1);
        return retVal;
    }
    
    public void clear() {
        _playlists.clear();
        fireIntervalRemoved(this, 0, 0);
    }

    @Override
    public int getSize() {
        return _playlists.size();
    }

    @Override
    public Playlist getElementAt(int index) {
        return _playlists.get(index);
    }
    
}
