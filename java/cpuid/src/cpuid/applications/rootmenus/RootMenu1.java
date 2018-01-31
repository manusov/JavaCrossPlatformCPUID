/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
Root Menu, variant #1, simple without left tree, debug-oriented.
This root menu variant unused at CPUID v0.52. Debug purpose.
*/

package cpuid.applications.rootmenus;

import cpuid.About;
import cpuid.applications.applicationcpuid.BuiltController;  // FIXED YET
import cpuid.applications.mvc.BCA;
import javax.swing.JPanel;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class RootMenu1 extends RootMenu   // Note RootMenu extends JFrame
{
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
