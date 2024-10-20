/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2024.
-------------------------------------------------------------------------------
Java CPUID application GUI main screen JFrame 
with root menu and items handlers connection.
*/

package cpuidv2.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.JPanel;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.table.AbstractTableModel;
import javax.swing.JOptionPane;
import javax.swing.SpringLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import cpuidv2.applications.ApplicationsManager;
import cpuidv2.CPUIDv2;
import cpuidv2.services.ServiceCpuid;

public class RootMenu extends JFrame
{
private final static int APP_X = 790;
private final static int APP_Y = 590;
private final JFrame thisFrame;          // Application root frame.
private final JMenuBar rootMenu;         // Root menu bar.
private final JTabbedPane tabbedPane;    // Tabbed pane with applications.
// Root menu sub-menus names strings.
private final static String[] NAMES_M1 = { "File" ,  "Help" };
// Mnemonics keys for root menu sub-menus.
private final static char[] MNEMONICS_M1 =  { 'F' , 'H' };
// Root menu items names strings.
private final static String[][] NAMES_M2 = 
    { { "Save report" , "Load InstLatX64 CPUID" ,
        "Save binary" , "Load binary" , 
        "Redetect processor" , "Exit"  } ,
      { "About" } };
// Mnemonics keys for root menu items.
private final static char[][] MNEMONICS_M2 =
    { { 'S' , 'L' , 'A' , 'O' , 'R' , 'X' } , 
      { 'A' } };
// Accelerator keys for root menu sub-menus.
private final static KeyStroke[][] ACCELERATORS_M2 =
    { { KeyStroke.getKeyStroke( 'S' , KeyEvent.ALT_DOWN_MASK ) ,
        null , null , null ,
        KeyStroke.getKeyStroke( 'R' , KeyEvent.ALT_DOWN_MASK ) ,
        KeyStroke.getKeyStroke( 'X' , KeyEvent.ALT_DOWN_MASK ) } ,
      { null } };
// Root menu separators.
private final static boolean[][] SEPARATORS_M2 =
    { { false , true , false , true , false , false } , 
      { false } };
// Root menu items status: true = supported, false = not supported (gray).
private final static boolean[][] ITEM_ACTIVE =
    { { true  , true , true , true , true , true } , 
      { true } };
// Listeners (handlers) for service buttons press.
private final AbstractAction[] listeners = 
    { new SaveReportAction(false) ,
      new LoadInstLatX64Action() ,
      new SaveBinaryAction() ,
      new LoadBinaryAction() ,
      new RedetectProcessorAction() ,
      new ExitAction() ,
      new AboutAction() };
    
// Frame and application root menu constructor.
// Called from main class to build GUI.
public RootMenu()
    {
    thisFrame = this;
    rootMenu = new JMenuBar();
    // Root menu: cycle for vertical root menus
    int n1 = NAMES_M1.length;
    JMenu[] m1 = new JMenu[n1];
    for( int i=0; i<n1; i++ ) 
        {
        m1[i] = new JMenu( NAMES_M1[i] );
        m1[i].setMnemonic( MNEMONICS_M1[i] );
        }
    // Root menu: cycle for items in vertical root menus
    JMenuItem[][] m2 = new JMenuItem[n1][];
    for( int i=0; i<n1; i++ )
        {
        int n2 = NAMES_M2[i].length;
        m2[i] = new JMenuItem[n2];
        for( int j=0; j<n2; j++ )
            {
            m2[i][j] = new JMenuItem( NAMES_M2[i][j] );
            m2[i][j].setMnemonic( MNEMONICS_M2[i][j] );
            m2[i][j].setAccelerator( ACCELERATORS_M2[i][j] );
            m1[i].add( m2[i][j] );
            if ( SEPARATORS_M2[i][j] )
                {
                m1[i].addSeparator();
                }
            }
        rootMenu.add( m1[i] );
        }
    // Root menu: cycle for set activity and add listeners
    int k = 0;
    int nm1 = NAMES_M1.length;
    m:
    for ( int i=0; i<nm1; i++ )
        {
        int nm2 = NAMES_M2[i].length;
        for ( int j=0; j<nm2; j++ )
            {
            if (( listeners == null ) || ( k >= listeners.length )) break m;
            if (listeners[k] != null )
                {
                m2[i][j].setEnabled( ITEM_ACTIVE[i][j] );
                m2[i][j].addActionListener( listeners[k++] );
                }
            }
        }
    
    tabbedPane = new JTabbedPane();
    ApplicationsManager manager = CPUIDv2.getApplicationsManager();
    String[] names = manager.getTabNames();
    Icon[] icons = manager.getTabIcons();
    JPanel[] panels = manager.getTabPanels();
    Boolean[] actives = manager.getTabActives();
    String[] tips = manager.getTabTips();
    
    if(( names != null )&&( icons != null )&&( panels != null )&&
       ( actives != null )&&( tips != null )&&
       ( icons.length == names.length )&&( panels.length == names.length)&&
       ( actives.length == names.length)&&( tips.length == names.length))
        {
        for( int i=0; i<names.length; i++ )
            {
            tabbedPane.addTab( names[i], icons[i], panels[i], tips[i] );
            tabbedPane.setEnabledAt( i, actives[i] );
            }
        manager.buildTabPanels();
        }
    
    }
    
// Geometry definition for tabbed pane and down buttons.
private final static Dimension DB = new Dimension( 102, 27 );
private final static int B_INTERVAL = -2;
private final static int B_DOWN = -4;
private final static int B_RIGHT = -3;
private final static int T_UP = -1;
private final static int T_DOWN = B_DOWN - DB.height - 5;
private final static int T_LEFT = 2;
private final static int T_RIGHT = -2;
// Text strings definition for down buttons.
private final static String[] BUTTONS_NAMES = 
    { "Redetect", "Load binary", "Save binary", 
      "Report full", "Report this",
      "About", "Cancel" };
private final String[] BUTTONS_TIPS =
    { "Repeat CPUID restores current platform data after load binary",
      "Load binary file with CPUID data instead current platform results",
      "Save binary file with CPUID data, can be loaded later",
      "CPUID instruction results full report",
      "CPUID instruction this leaf only report",
      null, null };
private final int[] BUTTONS_KEYS = { 'D', 'L', 'S', 'F', 'R', 'A', 'C' }; 

// Show panel of root menu.
// Called from main class to show GUI.
public void showGUI()
    {
    setJMenuBar( rootMenu );
    
    SpringLayout layout = new SpringLayout();
    JPanel panel = new JPanel( layout );
    panel.add( tabbedPane );
    getContentPane().add( panel );
    HelperLayout.springCenter
        ( layout, panel, tabbedPane, T_UP, T_DOWN, T_LEFT, T_RIGHT );
    // Built buttons, add listeners, common for all tabs.
    JButton[] buttons = new JButton[BUTTONS_NAMES.length];
    ActionListener[] actionsListeners = new ActionListener[] 
        { new BRedetect(), new BLoadBinary(), new BSaveBinary(),
          new BReportFull(), new BReportThis(), new BAbout(),
          new BCancel() };
    // BUTTONS_TIPS include overloaded methods downButtons.
    HelperButton.downButtons
        ( panel, buttons, BUTTONS_NAMES, BUTTONS_TIPS, BUTTONS_KEYS,
          BUTTONS_NAMES.length, actionsListeners, layout, 
          B_DOWN, B_RIGHT, B_INTERVAL, DB );
    
    setDefaultCloseOperation( EXIT_ON_CLOSE );
    setSize( APP_X, APP_Y );
    String titleString = CPUIDv2.getLongName();
    String addString = CPUIDv2.getDetector().getString();
    if( ( addString != null )&&( !addString.equals("") ) )
        {
        titleString = String.format("%s  ( %s )", titleString, addString);
        }
    titleString = String.format("%s", titleString);
    setTitle( titleString );
    setLocationByPlatform( true );
    setVisible( true );
    }


// Listeners classes for this root menu items.

final class SaveReportAction extends AbstractAction
    {
    private final boolean reportType;
    public SaveReportAction(boolean repType)
    {
        reportType = repType;
    }
    @Override public void actionPerformed( ActionEvent e )
        {
        AbstractTableModel[] models = 
            CPUIDv2.getApplicationsManager().getTabModels(reportType);
        SaveReport saver = new SaveReport();
        String nameAndPoint = CPUIDv2.getShortName() + ".";
        saver.reportFullDialogue( null, models, null,
            nameAndPoint , CPUIDv2.getVendorName() );
        }
    }

final class LoadInstLatX64Action extends AbstractAction
    {
    @Override public void actionPerformed( ActionEvent e )
        {
        LoadInstLatX64 textLoader = new LoadInstLatX64();
        ServiceCpuid service = CPUIDv2.getServiceCpuid();
        long[] binaryData = service.getOpb();
        int size = binaryData.length;
        long[] backupData = new long[size];
        for( int i=0; i<size; i++ ) 
            {
            backupData[i] = binaryData[i];
            binaryData[i] = 0;
            }
        boolean loaded = textLoader.loadTextDialogue( null, binaryData );
        if ( loaded )
            {  // If dump loaded, reinitialize CPUID results.
            service.setOpb( binaryData );   // Set data + redetect vendor.
            // Select panel 0 at JTabbedPane.
            tabbedPane.setSelectedIndex( 0 );
            // Clear panels 0,1,2 (CPUID-depend) at JTabbedPane.
            // Rebuild panels 0,1,2 (CPUID-depend) at JTabbedPane.
            ApplicationsManager manager = CPUIDv2.getApplicationsManager();
            manager.rebuildAfterCpuidReload( false );
            }
        else
            {  // If dump not loaded, restore buffer from backup.
            System.arraycopy( backupData, 0, binaryData, 0, size );
            }
        }
    }

final class SaveBinaryAction extends AbstractAction
    {
    @Override public void actionPerformed( ActionEvent e )
        {
        SaveBinary binarySaver = new SaveBinary();
        long[] binaryData = CPUIDv2.getServiceCpuid().getOpb();
        if( binaryData != null )
            {
            binarySaver.saveBinaryDialogue( null, binaryData );
            }
        }
    }

final class LoadBinaryAction extends AbstractAction
    {
    @Override public void actionPerformed( ActionEvent e )
        {
        LoadBinary binaryLoader = new LoadBinary();
        ServiceCpuid service = CPUIDv2.getServiceCpuid();
        long[] binaryData = service.getOpb();
        int size = binaryData.length;
        long[] backupData = new long[size];
        for( int i=0; i<size; i++ ) 
            {
            backupData[i] = binaryData[i];
            binaryData[i] = 0;
            }
        boolean loaded = binaryLoader.loadBinaryDialogue( null, binaryData );
        if ( loaded ) 
            {  // If dump loaded, reinitialize CPUID results.
            service.setOpb( binaryData );   // Set data + redetect vendor.
            // Select panel 0 at JTabbedPane.
            tabbedPane.setSelectedIndex( 0 );
            // Clear panels 0,1,2 (CPUID-depend) at JTabbedPane.
            // Rebuild panels 0,1,2 (CPUID-depend) at JTabbedPane.
            ApplicationsManager manager = CPUIDv2.getApplicationsManager();
            manager.rebuildAfterCpuidReload( false );
            }
        else
            {  // If dump not loaded, restore buffer from backup.
            System.arraycopy( backupData, 0, binaryData, 0, size );
            }
        }
    }

final class RedetectProcessorAction extends AbstractAction
    {
    @Override public void actionPerformed( ActionEvent e )
        {
        ApplicationsManager manager = CPUIDv2.getApplicationsManager();
        manager.rebuildAfterCpuidReload( true );
        JOptionPane.showMessageDialog( null, "Processor redetected.",
                "Redetect processor", JOptionPane.WARNING_MESSAGE );
        tabbedPane.setSelectedIndex( 0 );
        }
    }

final class ExitAction extends AbstractAction
    {
    @Override public void actionPerformed( ActionEvent e )
        {
        System.exit(0);
        }
    }

final class AboutAction extends AbstractAction
    {
    @Override public void actionPerformed( ActionEvent e )
        {
        About about = new About();
        final JDialog dialog = about.createDialog
        ( null , CPUIDv2.getShortName() , CPUIDv2.getVendorName() );
        dialog.setLocationRelativeTo( thisFrame );
        dialog.setVisible( true );    
        }
    }

// Buttons listeners, handlers for press buttons events.

final class BRedetect implements ActionListener
    {
    @Override public void actionPerformed ( ActionEvent e )
        {
        RedetectProcessorAction redetectProcessor =
            new RedetectProcessorAction();
        redetectProcessor.actionPerformed( e );
        }
    }
    
final class BLoadBinary implements ActionListener
    {
    @Override public void actionPerformed ( ActionEvent e )
        {
        LoadBinaryAction loadBinary = new LoadBinaryAction();
        loadBinary.actionPerformed( e );
        }
    }

final class BLoadText implements ActionListener
    {
    @Override public void actionPerformed ( ActionEvent e )
        {
        LoadInstLatX64Action loadInstLatX64 = new LoadInstLatX64Action();
        loadInstLatX64.actionPerformed( e );
        }
    }

final class BSaveBinary implements ActionListener
    {
    @Override public void actionPerformed ( ActionEvent e )
        { 
        SaveBinaryAction saveBinary = new SaveBinaryAction();
        saveBinary.actionPerformed( e );
        }
    }

final class BReportFull implements ActionListener
    {
    @Override public void actionPerformed ( ActionEvent e )
        {
        SaveReportAction saveReport = new SaveReportAction(true);
        saveReport.actionPerformed( e );
        }
    }

final class BReportThis implements ActionListener
    {
    @Override public void actionPerformed ( ActionEvent e )
        { 
        int tabIndex = tabbedPane.getSelectedIndex();
        ApplicationsManager manager = CPUIDv2.getApplicationsManager();
        AbstractTableModel[] thisModels = 
            manager.getReportThisTableModels( tabIndex );
        
        AbstractTableModel evenTable = null;
        AbstractTableModel oddTable = null;
        if( ( thisModels != null )&&( thisModels.length >= 2) )
            {
            evenTable = thisModels[0];
            oddTable = thisModels[1];
            }
        if ( ( thisModels != null )&&( thisModels.length == 1) )
            {
            evenTable = thisModels[0];
            oddTable = null;
            }
        SaveReport saver = new SaveReport();
        String nameAndPoint = CPUIDv2.getShortName() + ".";
        saver.reportThisDialogue( null, evenTable, oddTable,
            nameAndPoint , CPUIDv2.getVendorName() );
        }
    }

final class BAbout implements ActionListener
    {
    @Override public void actionPerformed ( ActionEvent e )
        { 
        AboutAction about = new AboutAction();
        about.actionPerformed( e );
        }
    }

final class BCancel implements ActionListener
    {
    @Override public void actionPerformed ( ActionEvent e )
        { 
        System.exit(0);
        }
    }

}
