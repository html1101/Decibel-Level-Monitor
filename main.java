import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;

import javax.swing.JButton;
import javax.swing.JLabel;

import java.time.format.DateTimeFormatter;
import java.time.LocalTime;


public class main {
    // JFrame
    static JFrame f;

    // JButton
    static JButton b, b1, b2;
    
    // Label to display text
    static JLabel l;

    public static void main(String[] args) {
        // Display text
        DateTimeFormatter formatMethod = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime t = LocalTime.parse("05:13", formatMethod);
        System.out.println(t);
    }
}
