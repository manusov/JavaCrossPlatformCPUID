//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Root Menu, variant #1, simple without left tree, debug-oriented. 

package arch1.applications.rootmenus;

import arch1.About;
import arch1.applications.applicationcpuid.BuiltController;  // FIXED YET
import arch1.applications.mvc.BCA;
import javax.swing.JPanel;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class RootMenu1 extends RootMenu   // Note RootMenu extends JFrame
{
// private static final int X_SIZE = 780, Y_SIZE = 580;
private final JPanel p;

public RootMenu1()
    {
    BCA c =    // AGENT SELECTION FIXED YET = CPUID
        new BuiltController();
    p = c.getView().getP();
    }

@Override public void showGUI()
    {
    add(p);
    setDefaultCloseOperation( EXIT_ON_CLOSE );
    setSize( About.getX2size(), About.getY2size() );
    setTitle( About.getLongName() );
    setLocationByPlatform(true);
    setVisible(true);
    }

}
