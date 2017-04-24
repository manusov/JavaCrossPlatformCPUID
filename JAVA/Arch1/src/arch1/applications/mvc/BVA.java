//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Built View Adapter. Include unified listeners.

package arch1.applications.mvc;

import arch1.About;
import arch1.applications.applicationcpuid.BuiltModel;
// import arch1.applications.guimodels.ChangeableTableModel;
import arch1.applications.guimodels.ViewableModel;
import arch1.applications.guimodels.VM3;
import arch1.applications.guimodels.VM4;
import arch1.applications.guipanels.ViewPanel;
// import arch1.applications.guipanels.SetupButtons;
// import arch1.applications.guipanels.SetupLayout;
// import arch1.applications.guipanels.Viewer3;
// import arch1.applications.mvc.*;
import arch1.applications.tools.ActionAbout;
import arch1.applications.tools.ActionBinary;
import arch1.applications.tools.ActionReport;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import javax.swing.table.AbstractTableModel;

public class BVA implements BV 
{
protected BM bm;
protected JTabbedPane t = null;

protected JPanel p = null, tp = null;
protected int xsize = 0, ysize = 0;
protected ViewableModel[] vms = null;
protected ViewPanel[] viewers = null;

protected final static Dimension DB = new Dimension( 102, 25 );

protected final static int B_INTERVAL = -2;
protected final static int B_DOWN = -2;
protected final static int B_RIGHT = -3;
        
protected final static int T_UP = 1;
protected final static int T_DOWN = B_DOWN - DB.height - 4;
protected final static int T_LEFT = 2;
protected final static int T_RIGHT = -2;

protected SpringLayout sl1 = null;

// protected final static String[] BUTTONS_NAMES = null;
// protected final static int NB = 0;

protected ActionListener[] actionsListeners = null;
protected JButton[] buttons = null;

//---------- Revisualization handler template ----------------------------------

protected void builtGUI() { }

//---------- Objects and parameters getters ------------------------------------

@Override public JPanel getP() { return p; }
@Override public int getXsize() { return xsize; }
@Override public int getYsize() { return ysize; }

//---------- Buttons listeners -------------------------------------------------

public class BRedetect implements ActionListener            // REDETECT
    {
    @Override public void actionPerformed (ActionEvent e)
        { 
        // reinitialize model
        bm = new BuiltModel();
        // revisual data
        if (t!=null) 
            {
            t.setSelectedIndex(0);
            t.removeAll();
            }
        builtGUI();
        }
    }
            
public class BLoadBinary implements ActionListener       // LOAD BINARY
    {
    @Override public void actionPerformed (ActionEvent e)
        {
        // load data
        ActionBinary fileBinary = new ActionBinary();
        long[] data = bm.getModel().getBinary();
        int size = data.length;
        for( int i=0; i<size; i++ ) { data[i] = 0; }
        boolean loaded = fileBinary.createDialogLB( null, data );
        if ( loaded==false ) { return; }
        bm.getModel().setBinary(data);
        // revisual data
        if (t!=null)
            {
            t.setSelectedIndex(0);    
            t.removeAll();
            }
        builtGUI();
        }
    }

public class BSaveBinary implements ActionListener       // SAVE BINARY
    {
    @Override public void actionPerformed (ActionEvent e)
        { 
        ActionBinary fileBinary = new ActionBinary();
        fileBinary.createDialogSB( null, bm.getModel().getBinary() );
        }
    }

public class BReportFull implements ActionListener       // REPORT FULL
    {
    @Override public void actionPerformed (ActionEvent e)
        {
        int n = vms.length;
        AbstractTableModel[] atma1 = new AbstractTableModel[n];
        AbstractTableModel[] atma2 = new AbstractTableModel[n];
        for ( int i=0; i<n; i++ )
            {
            ViewableModel x = vms[i];
            if ( x instanceof VM3 )
                {
                Object[] y = x.getValue();
                atma1[i] = (AbstractTableModel) y[1];
                atma2[i] = null;
                }
            if ( x instanceof VM4 )
                {
                Object[] y = x.getValue();
                atma1[i] = (AbstractTableModel) y[1];
                atma2[i] = (AbstractTableModel) y[2];
                }
            }
        ActionReport report = new ActionReport();
        report.createDialogRF
            ( null , atma1 , atma2 ,
              About.getShortName() , About.getVendorName() );
        }
    }

public class BReportThis implements ActionListener       // REPORT THIS
    {
    @Override public void actionPerformed (ActionEvent e)
        { 
        AbstractTableModel atm1 = null, atm2 = null;
        int i=0;
        if (t!=null) { i = t.getSelectedIndex(); }
        ViewableModel x = vms[i];
        Object[] y = x.getValue();
        if ( x instanceof VM3 )
            {
            atm1 = (AbstractTableModel) y[1];
            }
        if ( x instanceof VM4 )
            {
            atm1 = (AbstractTableModel) y[1];
            atm2 = (AbstractTableModel) y[2];
            }
        ActionReport report = new ActionReport();
        report.createDialogRT
            ( null , atm1 , atm2 ,
              About.getShortName() , About.getVendorName() );
        }
    }

public class BAbout implements ActionListener                  // ABOUT
    {
    @Override public void actionPerformed (ActionEvent e)
        { 
        ActionAbout about = new ActionAbout();
        final JDialog dialog = about.createDialog
            ( null , About.getShortName() , About.getVendorName() );
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        }
    }

public class BCancel implements ActionListener                // CANCEL
    {
    @Override public void actionPerformed (ActionEvent e)
        { 
        System.exit(0);
        }
    }


    
}
