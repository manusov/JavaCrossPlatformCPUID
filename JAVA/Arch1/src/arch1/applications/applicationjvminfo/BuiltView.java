//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// MVC "View" module for ApplicationJVMinfo,
// Note MVC is Model, View, Controller paradigm.
// OPTIMIZATION REQUIRED: MAKE PARENT CLASS FOR BuiltView?
// OR METHODS SUBROUTINES BECAUSE DIFFERENT MODES?
// Application: Java Virtual Machine (JVM) info.
// View module contain GUI panel.

package arch1.applications.applicationjvminfo;

import arch1.applications.guimodels.ChangeableTableModel;
import arch1.applications.guimodels.ViewableModel;
import arch1.applications.guipanels.SetupButtons;
import arch1.applications.guipanels.SetupLayout;
import arch1.applications.guipanels.Viewer3;
import arch1.applications.mvc.*;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class BuiltView extends BVA // implements BV 
{

private final static String[] BUTTONS_NAMES = 
    { "Report this", "About", "Cancel" };
private final static String[] BUTTONS_TIPS =
    { "Save Java Virtual Machine information in the text report",
      null, null };
private final static int[] BUTTONS_KEYS =
    { 'R', 'A', 'C' }; 
private final static int NB = BUTTONS_NAMES.length;

public BuiltView( BM z , int x, int y )
    {
    //--- Store input parameters ---
    bm = z;
    xsize = x;
    ysize = y;

    //--- Central object is Table ---
    Object ob1 = z.getModel().getValue(0);
    vms = new ViewableModel[1];
    vms[0] = (ViewableModel)ob1;
    Object[] ob2 = ((ViewableModel)(ob1)).getValue();
    // String z1 = (String) ob2[0];
    ChangeableTableModel z2 = (ChangeableTableModel) ob2[1];
    Viewer3 v = new Viewer3( xsize, ysize, z2, false );
    tp = v.getP();
    
    //--- Built GUI ---
    sl1 = new SpringLayout();
    p = new JPanel(sl1);
    p.add(tp);
    SetupLayout.springCenter ( sl1, p, tp, T_UP, T_DOWN, T_LEFT, T_RIGHT );
    
    //--- Buttons ---
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
