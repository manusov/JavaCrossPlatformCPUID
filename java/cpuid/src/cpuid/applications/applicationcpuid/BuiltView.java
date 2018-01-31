/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
MVC "View" module for ApplicationCpuid,
note MVC is Model, View, Controller paradigm.
Note, variable, initialized in constructor p(panel) located at parent class.
OPTIMIZATION REQUIRED: MAKE PARENT CLASS FOR BuiltView?
OR METHODS SUBROUTINES BECAUSE DIFFERENT MODES?
*/

package cpuid.applications.applicationcpuid;

import cpuid.applications.guimodels.ChangeableTableModel;
import cpuid.applications.guimodels.ViewableModel;
import cpuid.applications.guimodels.VM2;
import cpuid.applications.guimodels.VM3;
import cpuid.applications.guimodels.VM4;
import cpuid.applications.guipanels.SetupButtons;
import cpuid.applications.guipanels.SetupLayout;
import cpuid.applications.guipanels.ViewPanel;
import cpuid.applications.guipanels.Viewer2;
import cpuid.applications.guipanels.Viewer3;
import cpuid.applications.guipanels.Viewer4;
import cpuid.applications.mvc.BM;
import cpuid.applications.mvc.BVA;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;

public class BuiltView extends BVA // implements BV
{
private final static String[] BUTTONS_NAMES = 
    { "Redetect", "Load binary", "Save binary", 
      "Report full", "Report this",
      "About", "Cancel" };
private final static String[] BUTTONS_TIPS =
    { "Repeat CPUID restores current platform data after load binary",
      "Load binary file with CPUID data instead current platform results",
      "Save binary file with CPUID data, can be loaded later",
      "CPUID instruction results full report",
      "CPUID instruction this leaf only report",
      null, null };
private final static int[] BUTTONS_KEYS =
    { 'D', 'L', 'S', 'F', 'R', 'A', 'C' }; 
private final static int NB = BUTTONS_NAMES.length;

public BuiltView( BM z , int x, int y )
    {
    bm = z;
    xsize = x;
    ysize = y;
    // Built GUI
    t = new JTabbedPane();
    builtGUI();
    // Built buttons, add listeners, common for all tabs
    actionsListeners = new ActionListener[] 
        {
        new BRedetect()   , 
        new BLoadBinary() , 
        new BSaveBinary() ,
        new BReportFull() ,
        new BReportThis() ,
        new BAbout()      ,
        new BCancel()      
        };
    sl1 = new SpringLayout();
    p = new JPanel(sl1);
    p.add(t);
    SetupLayout.springCenter ( sl1, p, t, T_UP, T_DOWN, T_LEFT, T_RIGHT );
    buttons = new JButton[NB];
    // BUTTONS_TIPS include overloaded methods downButtons, added at v0.51
    SetupButtons.downButtons
        ( p, buttons, BUTTONS_NAMES, BUTTONS_TIPS, BUTTONS_KEYS, NB, 
          actionsListeners, sl1, 
          B_DOWN, B_RIGHT, B_INTERVAL, DB );
    }

// Built JTabbedPane, make callable because redetect

@Override protected final void builtGUI()
    {
    int n = bm.getModel().getCount();
    vms = new ViewableModel[n];
    viewers = new ViewPanel[n];
    int j=0;
    for( int i=0; i<n; i++ )
        {
        ViewableModel x = (ViewableModel) bm.getModel().getValue(i);
        
        if ( x instanceof VM2 )                     // tree panel
            {
            Object[] y = ((ViewableModel)(x)).getValue();
            String z1 = (String) y[0];
            DefaultTreeModel z2 = (DefaultTreeModel) y[1];
            Viewer2 v = new Viewer2( xsize, ysize, z2 ); // , z3, z4 );
            t.add( v.getP(), z1 );
            vms[j] = x;
            viewers[j++] = v;
            }

        if ( x instanceof VM3 )                     // summary, dump panels
            {
            Object[] y = ((ViewableModel)(x)).getValue();
            String z1 = (String) y[0];
            ChangeableTableModel z2 = (ChangeableTableModel) y[1];
            Viewer3 v = new Viewer3( xsize, ysize, z2, true );
            t.add( v.getP(), z1 );
            vms[j] = x;
            viewers[j++] = v;
            }

        if ( x instanceof VM4 )                     // cpuid functions panels
            {
            Object[] y = ((ViewableModel)(x)).getValue();
            String z1 = (String) y[0];
            ChangeableTableModel z2 = (ChangeableTableModel) y[1];
            ChangeableTableModel z3 = (ChangeableTableModel) y[2];
            Viewer4 v = new Viewer4( xsize, ysize, z2, z3 );
            t.add( v.getP(), z1 );
            vms[j] = x;
            viewers[j++] = v;
            }
        }
    }
    
}
