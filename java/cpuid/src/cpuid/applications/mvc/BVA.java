/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
Built View Adapter. Include unified listeners.
Implements public methods:
get GUI sub-application panel with GUI components,
get x-size, get y-size of GUI object
Note, constructor implemented at child class, for flexibility.
*/

/*
This class required optimization, can remove dataBackup secondary array usage.
*/

package cpuid.applications.mvc;

import cpuid.About;
import cpuid.applications.applicationcpuid.BuiltModel;
import cpuid.applications.guimodels.ViewableModel;
import cpuid.applications.guimodels.VM3;
import cpuid.applications.guimodels.VM4;
import cpuid.applications.guipanels.ViewPanel;
import cpuid.applications.tools.ActionAbout;
import cpuid.applications.tools.ActionBinary;
import cpuid.applications.tools.ActionReport;
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
protected ActionListener[] actionsListeners = null;
protected JButton[] buttons = null;

// Revisualization handler template

protected void builtGUI() { }

// Objects and parameters getters

@Override public JPanel getP() { return p; }
@Override public int getXsize() { return xsize; }
@Override public int getYsize() { return ysize; }

// Buttons listeners, handlers for press buttons events

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
        long[] dataBackup = new long[size];
        for( int i=0; i<size; i++ ) 
            {
            dataBackup[i] = data[i];
            data[i] = 0; 
            }
        
        boolean loaded = fileBinary.createDialogLB( null, data );
        
        if ( loaded==false ) 
            { 
            System.arraycopy( dataBackup, 0, data, 0, size );
            return; 
            }
        
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
