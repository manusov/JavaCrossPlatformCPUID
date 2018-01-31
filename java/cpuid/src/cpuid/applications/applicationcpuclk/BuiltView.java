/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
MVC "View" module for ApplicationCpuclk,
note MVC is Model, View, Controller paradigm.
Note, variable, initialized in constructor p(panel) located at parent class.
OPTIMIZATION REQUIRED: MAKE PARENT CLASS FOR BuiltView?
OR METHODS SUBROUTINES BECAUSE DIFFERENT MODES?
*/

package cpuid.applications.applicationcpuclk;

import cpuid.applications.mvc.BVA;
import cpuid.applications.mvc.BM;
import cpuid.applications.guimodels.ChangeableTableModel;
import cpuid.applications.guimodels.ViewableModel;
import cpuid.applications.guipanels.SetupButtons;
import cpuid.applications.guipanels.SetupLayout;
import cpuid.applications.guipanels.Viewer3;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class BuiltView extends BVA
{
private final static String[] BUTTONS_NAMES =
    { "Report this", "About", "Cancel" };
private final static String[] BUTTONS_TIPS =
    { "Save processor clock information in the text report" , null, null };
private final static int[] BUTTONS_KEYS = 
      { 'R', 'A', 'C' }; 
private final static int NB = BUTTONS_NAMES.length;

public BuiltView( BM z , int x, int y )
    {
    // Store input parameters
    bm = z;
    xsize = x;
    ysize = y;
    // Central object is Table
    Object ob1 = z.getModel().getValue(0);
    vms = new ViewableModel[1];
    vms[0] = (ViewableModel)ob1;
    Object[] ob2 = ((ViewableModel)(ob1)).getValue();
    ChangeableTableModel z2 = (ChangeableTableModel) ob2[1];
    Viewer3 v = new Viewer3( xsize, ysize, z2, true );
    tp = v.getP();
    // Built GUI
    sl1 = new SpringLayout();
    p = new JPanel(sl1);
    p.add(tp);
    SetupLayout.springCenter ( sl1, p, tp, T_UP, T_DOWN, T_LEFT, T_RIGHT );
    // Buttons
    actionsListeners = new ActionListener[] 
        {
        new BReportThis() ,
        new BAbout()      ,
        new BCancel()      
        };
    buttons = new JButton[NB];
    // BUTTONS_TIPS include overloaded methods downButtons, added at v0.51
    SetupButtons.downButtons
        ( p, buttons, BUTTONS_NAMES, BUTTONS_TIPS, BUTTONS_KEYS, NB,
          actionsListeners, sl1, 
          B_DOWN, B_RIGHT, B_INTERVAL, DB );
    }

}
