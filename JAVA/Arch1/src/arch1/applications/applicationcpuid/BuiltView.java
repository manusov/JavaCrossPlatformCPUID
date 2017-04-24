//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// MVC "View" module for ApplicationCpuid,
// Note MVC is Model, View, Controller paradigm.
// OPTIMIZATION REQUIRED: MAKE PARENT CLASS FOR BuiltView?
// OR METHODS SUBROUTINES BECAUSE DIFFERENT MODES?

package arch1.applications.applicationcpuid;

// import arch1.About;
import arch1.applications.guimodels.ChangeableTableModel;
import arch1.applications.guimodels.ViewableModel;
import arch1.applications.guimodels.VM2;
import arch1.applications.guimodels.VM3;
import arch1.applications.guimodels.VM4;
import arch1.applications.guipanels.SetupButtons;
import arch1.applications.guipanels.SetupLayout;
import arch1.applications.guipanels.ViewPanel;
import arch1.applications.guipanels.Viewer2;
import arch1.applications.guipanels.Viewer3;
import arch1.applications.guipanels.Viewer4;
import arch1.applications.mvc.BM;
// import arch1.applications.mvc.BV;
import arch1.applications.mvc.BVA;
// import arch1.applications.tools.ActionAbout;
// import arch1.applications.tools.ActionBinary;
// import arch1.applications.tools.ActionReport;
// import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
// import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultTreeModel;

public class BuiltView extends BVA // implements BV
{
// private BM bm;

// private final JPanel p;
// private final JTabbedPane t;

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

// private final ActionListener[] actionsListeners;

// private final static Dimension DB = new Dimension( 102, 25 );

// private final static int B_INTERVAL = -2;
// private final static int B_DOWN = -2;
// private final static int B_RIGHT = -3;
        
// private final static int T_UP = 1;
// private final static int T_DOWN = B_DOWN - DB.height - 4;
// private final static int T_LEFT = 2;
// private final static int T_RIGHT = -2;

// private final SpringLayout sl1;
// private final JButton[] buttons;
// private View[] views;
// private ViewPanel[] viewers;
// private final int xsize, ysize;

public BuiltView( BM z , int x, int y )
    {
    
    bm = z;
    xsize = x;
    ysize = y;
        
//---------- Built GUI ---------------------------------------------------------
    
    t = new JTabbedPane();
    builtGUI();
    
//---------- Built buttons, add listeners, common for all tabs -----------------

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

//---------- Objects and parameters getters ------------------------------------
/*
@Override public JPanel getP() { return p; }
@Override public int getXsize() { return xsize; }
@Override public int getYsize() { return ysize; }
*/
//---------- Built JTabbedPane, make callable because redetect -----------------

@Override protected void builtGUI()
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
