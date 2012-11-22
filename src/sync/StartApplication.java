package sync;

import java.awt.EventQueue;
import javax.swing.UIManager;

public class StartApplication {

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
        			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    GUI window = new GUI();
                    LibraryTableModel model = new LibraryTableModel(window);
                    window.setModel(model);
                    window.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
}
