package sync;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.JSplitPane;
import javax.swing.JList;
import javax.swing.JTextArea;

import playlists.Playlist;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class GUI {
    
//    public static final String SYNC_DIR = "/Users/Peter/Desktop/Sync/";
//    public static final String SYNC_DIR = "/Volumes/NO NAME/MUSIC/";
//    public static final String SYNC_DIR = "/Volumes/NO NAME/MUSIC_TEST/";
    
    public static final String LF = System.getProperty("line.separator");

    public static final String CONFIRM_RESET = "This will reset the whole library database. "
            + "All 'sync-checks' will be lost."
            + LF
            + "You should nerver do this unless serious problems occur or you"
            + LF + "have fun with rechecking your music for synchronisation.";
    
    public static final String CONFIRM_RESCAN = "This will rescan your iTunes library. "
            + LF + LF
            + "Note: if a song was removed from iTunes, it will be removed from database "
            + LF + "and will stay in your sync-folder."
            + LF + LF
            + "If you want to remove it from your sync-folder, too (which is the usual way),"
            + LF
            + "you should uncheck and sync before rescanning.";
    
    private static boolean isScanning = false; // flag to only start one rescan-thread
    
    private LibraryTableModel libModel;
    private PlaylistListModel playlistsModel;

    private JFrame frame;
    private JTextField txtSearch;
    private JTable libTable;
    private JTextField txtSyncdir;
    private JButton btnRescan;
    private JButton btnUncheck;
    private JButton btnCheck;
    private JButton btnSync;

    private JButton btnReset;
    private JButton btnApplyConfiguration;
    private JButton btnChooseDir;
    private JLabel lblState;
    private JLabel lblLibraryDirectory;
    private JTextField txtLibDir;
    private JLabel lblSize;
    private JButton btnChooseLib;
    private JLabel lblPlaylistsDirectory;
    private JTextField txtListsDir;
    private JButton btnChooseLists;
    private JLabel lblPlaylistsSyncDirectory;
    private JTextField txtListsSyncDir;
    private JButton btnChooseListsSync;
    private JPanel playlistsPanel;
    private JPanel cvtPanel;
    private JButton btnSyncLists;
    private JButton btnRefresh;
    private JTextArea playlistArea;
    private JScrollPane playlistsScrollPane;
    private JList<Playlist> playlistsList;

    /**
     * Create the application.
     */
    public GUI() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        
        frame = new JFrame();
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // make frame central
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int left = (screenSize.width - frame.getBounds().width) / 2;
        int top = (screenSize.height - frame.getBounds().height) / 2;
        frame.setBounds(left, top, frame.getBounds().width, frame.getBounds().height);
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        
        JPanel syncPanel = new JPanel();
        syncPanel.setOpaque(false);
        tabbedPane.addTab("Sync", null, syncPanel, null);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setOpaque(false);
        scrollPane.setBorder(null);
        
        libTable = new JTable();
        scrollPane.setViewportView(libTable);
        
        JPanel ctrlPanel = new JPanel();
        ctrlPanel.setOpaque(false);
        ctrlPanel.setLayout(new BoxLayout(ctrlPanel, BoxLayout.Y_AXIS));
        
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        ctrlPanel.add(btnPanel);
        
        btnCheck = new JButton("Check");
        btnCheck.addActionListener(new BtnCheckActionListener());
        btnPanel.add(btnCheck);
        
        btnUncheck = new JButton("Uncheck");
        btnUncheck.addActionListener(new BtnUncheckActionListener());
        btnPanel.add(btnUncheck);
        
        btnSync = new JButton("Sync");
        btnSync.addActionListener(new BtnSyncActionListener());
        btnPanel.add(btnSync);
        
        btnRescan = new JButton("Rescan");
        btnRescan.addActionListener(new BtnRescanActionListener());
        btnPanel.add(btnRescan);
        
        btnReset = new JButton("Reset");
        btnReset.addActionListener(new BtnResetActionListener());
        btnPanel.add(btnReset);
        
        JPanel searchPanel = new JPanel();
        searchPanel.setOpaque(false);
        ctrlPanel.add(searchPanel);
        
        JLabel lblSearch = new JLabel("Search:");
        searchPanel.add(lblSearch);
        
        txtSearch = new JTextField();
        txtSearch.addKeyListener(new TxtSearchKeyListener());
        searchPanel.add(txtSearch);
        txtSearch.setColumns(40);
        
        lblSize = new JLabel("Size: ");
        searchPanel.add(lblSize);
        
        JPanel statusPanel = new JPanel();
        statusPanel.setOpaque(false);
        ctrlPanel.add(statusPanel);
        
        JLabel lblStatus = new JLabel("Status:");
        statusPanel.add(lblStatus);
        
        lblState = new JLabel("ready");
        statusPanel.add(lblState);
        GroupLayout gl_syncPanel = new GroupLayout(syncPanel);
        gl_syncPanel.setHorizontalGroup(
            gl_syncPanel.createParallelGroup(Alignment.LEADING)
                .addComponent(ctrlPanel, GroupLayout.DEFAULT_SIZE, 753, Short.MAX_VALUE)
                .addGroup(Alignment.TRAILING, gl_syncPanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 729, Short.MAX_VALUE)
                    .addContainerGap())
        );
        gl_syncPanel.setVerticalGroup(
            gl_syncPanel.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_syncPanel.createSequentialGroup()
                    .addGap(11)
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(ctrlPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(1))
        );
        syncPanel.setLayout(gl_syncPanel);
        GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE)
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE)
                    .addContainerGap())
        );
        
        playlistsPanel = new JPanel();
        playlistsPanel.setOpaque(false);
        tabbedPane.addTab("Playlists", null, playlistsPanel, null);
        
        cvtPanel = new JPanel();
        cvtPanel.setOpaque(false);
        
        btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(new BtnRefreshActionListener());
        cvtPanel.add(btnRefresh);
        
        btnSyncLists = new JButton("Sync");
        btnSyncLists.addActionListener(new BtnSyncListsActionListener());
        cvtPanel.add(btnSyncLists);
        
        JSplitPane splitPane = new JSplitPane();
        GroupLayout gl_playlistsPanel = new GroupLayout(playlistsPanel);
        gl_playlistsPanel.setHorizontalGroup(
            gl_playlistsPanel.createParallelGroup(Alignment.LEADING)
                .addComponent(cvtPanel, GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
                .addGroup(gl_playlistsPanel.createSequentialGroup()
                    .addGap(10)
                    .addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE)
                    .addContainerGap())
        );
        gl_playlistsPanel.setVerticalGroup(
            gl_playlistsPanel.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_playlistsPanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(cvtPanel, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
        );
        
        JScrollPane singleListScrollPane = new JScrollPane();
        splitPane.setRightComponent(singleListScrollPane);
        
        playlistArea = new JTextArea();
        playlistArea.setEditable(false);
        singleListScrollPane.setViewportView(playlistArea);
        
        playlistsScrollPane = new JScrollPane();
        splitPane.setLeftComponent(playlistsScrollPane);
        
        playlistsList = new JList<Playlist>();
        playlistsList.addListSelectionListener(new PlaylistsListListSelectionListener());
        playlistsScrollPane.setViewportView(playlistsList);
        playlistsPanel.setLayout(gl_playlistsPanel);
        
        JPanel configPanel = new JPanel();
        configPanel.setOpaque(false);
        tabbedPane.addTab("Configuration", null, configPanel, null);
        
        JLabel lblSyncDirectory = new JLabel("Sync directory:");
        
        txtSyncdir = new JTextField();
        txtSyncdir.setColumns(10);
        
        btnChooseDir = new JButton("Choose...");
        btnChooseDir.addActionListener(new BtnChooseDirActionListener());
        
        btnApplyConfiguration = new JButton("Apply Configuration");
        btnApplyConfiguration.addActionListener(new BtnApplyConfigurationActionListener());
        
        lblLibraryDirectory = new JLabel("Library directory:");
        
        txtLibDir = new JTextField();
        txtLibDir.setText("%HOMEPATH%\\Music\\iTunes\\iTunes Media\\Music");
        txtLibDir.setColumns(10);
        
        btnChooseLib = new JButton("Choose...");
        btnChooseLib.addActionListener(new BtnChooseLibActionListener());
        
        lblPlaylistsDirectory = new JLabel("Playlists directory:");
        
        txtListsDir = new JTextField();
        txtListsDir.setColumns(10);
        
        btnChooseLists = new JButton("Choose...");
        btnChooseLists.addActionListener(new BtnChooseListsActionListener());
        
        lblPlaylistsSyncDirectory = new JLabel("Playlists sync directory:");
        
        txtListsSyncDir = new JTextField();
        txtListsSyncDir.setColumns(10);
        
        btnChooseListsSync = new JButton("Choose...");
        btnChooseListsSync.addActionListener(new BtnChooseListsSyncActionListener());

        GroupLayout gl_configPanel = new GroupLayout(configPanel);
        gl_configPanel.setHorizontalGroup(
            gl_configPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_configPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_configPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(btnApplyConfiguration, Alignment.TRAILING)
                        .addGroup(Alignment.TRAILING, gl_configPanel.createSequentialGroup()
                            .addGroup(gl_configPanel.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblPlaylistsDirectory)
                                .addComponent(lblSyncDirectory)
                                .addComponent(lblLibraryDirectory))
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addGroup(gl_configPanel.createParallelGroup(Alignment.TRAILING)
                                .addComponent(txtSyncdir, GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                                .addComponent(txtLibDir, GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                                .addComponent(txtListsDir, GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE))
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addGroup(gl_configPanel.createParallelGroup(Alignment.LEADING)
                                .addComponent(btnChooseLists)
                                .addComponent(btnChooseDir)
                                .addComponent(btnChooseLib)))
                        .addGroup(gl_configPanel.createSequentialGroup()
                            .addComponent(lblPlaylistsSyncDirectory, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
                            .addGap(12)
                            .addComponent(txtListsSyncDir, GroupLayout.PREFERRED_SIZE, 514, GroupLayout.PREFERRED_SIZE)
                            .addGap(12)
                            .addComponent(btnChooseListsSync, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
        );
        gl_configPanel.setVerticalGroup(
            gl_configPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_configPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_configPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblSyncDirectory)
                        .addComponent(btnChooseDir)
                        .addComponent(txtSyncdir, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_configPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(txtLibDir, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnChooseLib)
                        .addComponent(lblLibraryDirectory))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_configPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblPlaylistsDirectory)
                        .addComponent(txtListsDir, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnChooseLists))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_configPanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_configPanel.createSequentialGroup()
                            .addGap(4)
                            .addComponent(lblPlaylistsSyncDirectory))
                        .addGroup(gl_configPanel.createSequentialGroup()
                            .addGap(1)
                            .addComponent(txtListsSyncDir, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addComponent(btnChooseListsSync))
                    .addPreferredGap(ComponentPlacement.RELATED, 307, Short.MAX_VALUE)
                    .addComponent(btnApplyConfiguration)
                    .addContainerGap())
        );
        configPanel.setLayout(gl_configPanel);
        frame.getContentPane().setLayout(groupLayout);
    }
    
    //
    // delegate methods
    //
    
    public void setVisible(boolean b) {
        frame.setVisible(b);
    }
    
    public void setLibraryModel(LibraryTableModel dataModel) {
        libTable.setModel(dataModel);
        libModel = dataModel;
        String size = String.format("%1.2f", libModel.getCheckedSize() / (1024.0*1024.0));
        String free = String.format("%1.2f", libModel.getFreeSpace() / (1024.0*1024.0));
        lblSize.setText("Size: " + size + "MB - Free: " + free + " MB");
        
        // TODO: do not logical belong here:
        txtSyncdir.setText(libModel.prefs.getSyncDir());
        txtLibDir.setText(libModel.prefs.getLibraryDir());
        txtListsDir.setText(libModel.prefs.getListsDir());
        txtListsSyncDir.setText(libModel.prefs.getListsSyncDir());
    }
    
    public void setPlaylistsModel(PlaylistListModel model) {
        playlistsModel = model;
        playlistsList.setModel(model);
    }
    
    public void setStatus(String status) {
        lblState.setText(status);
    }
    
    //
    // signal ready
    //
    
    /**
     * Has to be called from the file-copying-thread, when its ready, to
     * actualize the GUI.
     */
    public void signalSyncReady() {
        setButtonsEnabled(true);
    }
    
    //
    // private helper methods
    //
    
    private void setButtonsEnabled(boolean b) {
        btnCheck.setEnabled(b);
        btnUncheck.setEnabled(b);
        btnSync.setEnabled(b);
        btnRescan.setEnabled(b);
        btnReset.setEnabled(b);
    }
    
    //
    // action listeners
    //

    private class BtnCheckActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            libModel.checkSync(libTable.getSelectedRows(), true);
            String size = String.format("%1.2f", libModel.getCheckedSize() / (1024.0*1024.0));
            String free = String.format("%1.2f", libModel.getFreeSpace() / (1024.0*1024.0));
            lblSize.setText("Size: " + size + "MB - Free: " + free + " MB");
        }
    }
    
    private class BtnUncheckActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            libModel.checkSync(libTable.getSelectedRows(), false);
            String size = String.format("%1.2f", libModel.getCheckedSize() / (1024.0*1024.0));
            String free = String.format("%1.2f", libModel.getFreeSpace() / (1024.0*1024.0));
            lblSize.setText("Size: " + size + "MB - Free: " + free + " MB");
        }
    }
    
    private class BtnSyncActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            setButtonsEnabled(false);
            libModel.sync();
        }
    }

    private class BtnRescanActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int rc = JOptionPane.showConfirmDialog(frame, CONFIRM_RESCAN,
                    "Confirm Rescan", JOptionPane.OK_CANCEL_OPTION);
            if (rc != JOptionPane.OK_OPTION) {
                return;
            }
            
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (isScanning) {
                        return; // do nothing, already scanning
                    }
                    
                    isScanning = true;
                    setButtonsEnabled(false);
                    txtSearch.setEnabled(false);
                    setStatus("rescanning library...");
                    
                    try {
                        libModel.rescan();
                        setStatus("ready");
                    } catch (FileNotFoundException e) {
                        setStatus("File not found: " + e.getMessage());
                        e.printStackTrace();
                    }
                    
                    txtSearch.setEnabled(true);
                    setButtonsEnabled(true);
                    isScanning = false;
                }
            }).start();
//            libModel.rescan();
        }
    }
    
    private class BtnResetActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int rc = JOptionPane.showConfirmDialog(frame, CONFIRM_RESET,
                    "Confirm Reset", JOptionPane.OK_CANCEL_OPTION);
            if (rc == JOptionPane.OK_OPTION) {
                libModel.reset();
            }
        }
    }

    private class TxtSearchKeyListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            libModel.search(txtSearch.getText());
        }
    }
    
    //
    // playlists listener
    //
    

    private class PlaylistsListListSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            if (playlistsList.isSelectionEmpty()) {
                return;
            }
            playlistArea.setText(playlistsModel.getElementAt(playlistsList.getSelectedIndex()).fileNamesToString());
        }
    }
    
    private class BtnRefreshActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            playlistsList.clearSelection();
            playlistsModel.clear();
            File[] playlistFiles = new File(libModel.prefs.getListsDir()).listFiles();
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
                playlistsModel.add(pl);
            }
        }
    }
    

    private class BtnSyncListsActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            setButtonsEnabled(false);
            playlistsModel.convertAndSync();
            setButtonsEnabled(true);
        }
    }
    
    //
    // preferences listeners
    //
    
    private class BtnChooseDirActionListener implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int rc = chooser.showOpenDialog(frame);
            if (rc == JFileChooser.APPROVE_OPTION) {
                String syncDir = chooser.getSelectedFile().getAbsolutePath();
                txtSyncdir.setText(syncDir);
            }
        }
    }
    private class BtnChooseLibActionListener implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int rc = chooser.showOpenDialog(frame);
            if (rc == JFileChooser.APPROVE_OPTION) {
                String libDir = chooser.getSelectedFile().getAbsolutePath();
                txtLibDir.setText(libDir);
            }
        }
    }
    private class BtnChooseListsActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int rc = chooser.showOpenDialog(frame);
            if (rc == JFileChooser.APPROVE_OPTION) {
                String listsDir = chooser.getSelectedFile().getAbsolutePath();
                txtListsDir.setText(listsDir);
            }
        }
    }
    private class BtnChooseListsSyncActionListener implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int rc = chooser.showOpenDialog(frame);
            if (rc == JFileChooser.APPROVE_OPTION) {
                String listsSyncDir = chooser.getSelectedFile().getAbsolutePath();
                txtListsSyncDir.setText(listsSyncDir);
            }
        }
    }
    private class BtnApplyConfigurationActionListener implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            libModel.prefs.setSyncDir(txtSyncdir.getText());
            libModel.prefs.setLibraryDir(txtLibDir.getText());
            libModel.prefs.setListsDir(txtListsDir.getText());
            libModel.prefs.setListsSyncDir(txtListsSyncDir.getText());
            try {
                Preferences.saveToFile(libModel.prefs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
