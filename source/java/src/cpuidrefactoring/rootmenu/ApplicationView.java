/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
VIEW template class for build sub-applications
with MVC ( Model, View, Controller ) structure.
*/

package cpuidrefactoring.rootmenu;

import cpuidrefactoring.About;
import cpuidrefactoring.tools.ActionAbout;
import cpuidrefactoring.tools.ActionBinary;
import cpuidrefactoring.tools.ActionReport;
import cpuidrefactoring.tools.ActionText;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import javax.swing.table.AbstractTableModel;

public class ApplicationView 
{
protected ApplicationModel model;
protected int xsize = 0, ysize = 0;
protected JTabbedPane t = null;
protected JPanel p = null, tp = null;
protected ViewSet[] viewSets = null;
protected ViewPanel[] viewPanels = null;
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
protected void buildGUI() { }

// Helper for simple sub-application
protected final void helperSimpleBuildView
    ( ApplicationModel am , int x, int y, 
      String[] buttonsNames, String[] buttonsTips, int[] buttonsKeys,
      boolean centeringStrings )
    {
    // Store input parameters
    model = am;
    xsize = x;
    ysize = y;
    // Central object is Table
    Object ob1 = am.getSelectedModel( 0 );
    viewSets = new ViewSet[1];
    viewSets[0] = (ViewSet)ob1;
    Object[] ob2 = ((ViewSet)(ob1)).getGuiObjects();
    if ( ob2 != null )
        {
        ChangeableTableModel z2 = (ChangeableTableModel) ob2[1];
        ViewPanelSingleTable v = 
            new ViewPanelSingleTable( xsize, ysize, z2, centeringStrings );
        tp = v.getP();
        sl1 = new SpringLayout();
        p = new JPanel( sl1 );
        p.add(tp);
        HelperLayout.springCenter( sl1, p, tp, T_UP, T_DOWN, T_LEFT, T_RIGHT );
        actionsListeners = new ActionListener[] 
            { new BReportThis(), new BAbout(), new BCancel() };
        buttons = new JButton[buttonsNames.length];
        HelperButton.downButtons
            ( p, buttons, 
              buttonsNames, buttonsTips, buttonsKeys, buttonsNames.length, 
              actionsListeners, sl1, B_DOWN, B_RIGHT, B_INTERVAL, DB );
        }
    }

// getters
public final JPanel getPanel() { return p;     }
public final int getXsize()    { return xsize; }
public final int getYsize()    { return ysize; }

// Buttons listeners, handlers for press buttons events

public final class BRedetect implements ActionListener         // REDETECT
    {
    @Override public void actionPerformed ( ActionEvent e )
        { 
        // model = new ApplicationModel();  // reinitialize model
        boolean b = model.redetectPlatform();
        if ( b && ( t != null ) )
            {                            // revisual data
            t.setSelectedIndex( 0 );
            t.removeAll();
            }
        buildGUI();
        }
    }
    
public final class BLoadBinary implements ActionListener       // LOAD BINARY
    {
    @Override public void actionPerformed ( ActionEvent e )
        {
        ActionBinary fileBinary = new ActionBinary();  // class for load data dialogue
        long[] data = model.getBinary();
        int size = data.length;
        long[] dataBackup = new long[size];
        for( int i=0; i<size; i++ ) 
            {
            dataBackup[i] = data[i];  // backup element[i]
            data[i] = 0;              // clear element[i]
            }
        boolean loaded = fileBinary.loadBinaryDialogue( null, data );
        if ( loaded == false ) 
            {  // if dump not loaded, restore buffer from backup
            System.arraycopy( dataBackup, 0, data, 0, size );
            return; 
            }
        model.setBinary( data );  // if dump loaded, set data + redetect vendor
        if ( t != null )
            {  // revisual data
            t.setSelectedIndex( 0 );    
            t.removeAll();
            }
        buildGUI();
        }
    }

public void entryBLoadText()
    {
    BLoadText blt = new BLoadText();
    blt.actionPerformed( null );
    }

private final class BLoadText implements ActionListener       // LOAD TEXT, InstLatx64 compatible
    {
    @Override public void actionPerformed ( ActionEvent e )
        {
        ActionText fileText = new ActionText();  // class for load text dialogue
        long[] data = model.getBinary();
        int size = data.length;
        long[] dataBackup = new long[size];
        for( int i=0; i<size; i++ ) 
            {
            dataBackup[i] = data[i];  // backup element[i]
            data[i] = 0;              // clear element[i]
            }
        boolean loaded = fileText.loadTextDialogue( null, data );
        if ( loaded == false ) 
            {  // if dump not loaded, restore buffer from backup
            System.arraycopy( dataBackup, 0, data, 0, size );
            return; 
            }
        model.setBinary( data );  // if dump loaded, set data + redetect vendor
        if ( t != null )
            {  // revisual data
            t.setSelectedIndex( 0 );    
            t.removeAll();
            }
        buildGUI();
        }
    }


public final class BSaveBinary implements ActionListener       // SAVE BINARY
    {
    @Override public void actionPerformed ( ActionEvent e )
        { 
        ActionBinary fileBinary = new ActionBinary();
        fileBinary.saveBinaryDialogue( null, model.getBinary() );
        }
    }

public final class BReportFull implements ActionListener       // REPORT FULL
    {
    @Override public void actionPerformed ( ActionEvent e )
        {
        int n = viewSets.length;
        AbstractTableModel[] m1 = new AbstractTableModel[n];
        AbstractTableModel[] m2 = new AbstractTableModel[n];
        for ( int i=0; i<n; i++ )
            {
            ViewSet x = viewSets[i];
            if ( x instanceof ViewSetSingleTable )
                {
                Object[] y = x.getGuiObjects();
                m1[i] = ( AbstractTableModel ) y[1];
                m2[i] = null;
                }
            if ( x instanceof ViewSetDualTable )
                {
                Object[] y = x.getGuiObjects();
                m1[i] = ( AbstractTableModel ) y[1];
                m2[i] = ( AbstractTableModel ) y[2];
                }
            }
        ActionReport report = new ActionReport();
        report.reportFullDialogue
            ( null, m1, m2, About.getShortName(), About.getVendorName() );
        }
    }

public final class BReportThis implements ActionListener       // REPORT THIS
    {
    @Override public void actionPerformed ( ActionEvent e )
        { 
        AbstractTableModel m1 = null, m2 = null;
        int i=0;
        if ( t != null ) { i = t.getSelectedIndex(); }
        ViewSet x = viewSets[i];
        Object[] y = x.getGuiObjects();
        if ( x instanceof ViewSetSingleTable )
            {
            m1 = ( AbstractTableModel ) y[1];
            }
        if ( x instanceof ViewSetDualTable )
            {
            m1 = ( AbstractTableModel ) y[1];
            m2 = ( AbstractTableModel ) y[2];
            }
        ActionReport report = new ActionReport();
        report.reportThisDialogue
            ( null, m1, m2, About.getShortName() , About.getVendorName() );
        }
    }

public final class BAbout implements ActionListener                  // ABOUT
    {
    @Override public void actionPerformed ( ActionEvent e )
        { 
        ActionAbout about = new ActionAbout();
        final JDialog dialog = about.createDialog
            ( null, About.getShortName(), About.getVendorName() );
        dialog.setLocationRelativeTo( null );
        dialog.setVisible( true );
        }
    }

public final class BCancel implements ActionListener                // CANCEL
    {
    @Override public void actionPerformed ( ActionEvent e )
        { 
        System.exit(0);
        }
    }

}
