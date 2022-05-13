import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;

import javax.swing.JButton;
import javax.swing.JLabel;

public class main extends JFrame {
    // JFrame
    static JFrame f;

    // JButton
    static JButton b, b1, b2;
    
    // Label to display text
    static JLabel l;

    public static void main(String[] args) {
        // Display text
        f = new JFrame();
        l = new JLabel("Decibel Level Monitor");

        // Set the size of the frame
        f.setSize(800, 800);

        // Create profile
        Profile p = new Profile("Hello", "Testing", "Bla");

        p.draw(f);
        
        f.show();
    }
}
