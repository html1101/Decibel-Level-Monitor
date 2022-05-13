import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.*;

public class Profile extends JFrame {
    String name;
    String profile_path;
    String description;
    String[] classes;

    public Profile(String n, String p, String desc) {
        // Draw name
        name = n;
        profile_path = p;
        description = desc;
    }

    public void draw(JFrame j) {
        // Start by writing name
        Container container= getContentPane();
        
        JLabel o = new JLabel(name);

        Dimension size = o.getPreferredSize();
        //You can change 100(x) and 100(y) for your likes, so you can put that JLabel wherever you want
        o.setBounds(100, 100, size.width, size.height);

        container.add(o);

        j.add(container);
        
        // return j;
    }
}
