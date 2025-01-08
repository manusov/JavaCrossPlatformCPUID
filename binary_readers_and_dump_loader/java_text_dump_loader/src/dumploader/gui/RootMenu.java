/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Application screen - JFrame with root menu and main panel..

*/

package dumploader.gui;

import dumploader.DumpLoader;
import static dumploader.DumpLoader.getAllWeb;
import static dumploader.DumpLoader.getLongName;
import static dumploader.DumpLoader.getProjectWeb;
import static dumploader.DumpLoader.getShortName;
import static dumploader.DumpLoader.getVendorName1;
import static dumploader.DumpLoader.getVendorName2;
import dumploader.cpuenum.CpuRootEnumerator;
import dumploader.guipanels.ApplicationEnumerator;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.table.AbstractTableModel;

public class RootMenu extends JFrame
{
    private final static int APP_X = 1024;
    private final static int APP_Y = 768;
    
    private final ApplicationEnumerator enumerator;
    private final JFrame thisFrame;
    private final JMenuBar rootMenu;
    private final JTabbedPane tabbedPane;
    
    // Root menu sub-menus names strings.
    private final static String[] NAMES_M1 = { "File" , "Help" };
    
    // Mnemonics keys for root menu sub-menus.
    private final static char[] MNEMONICS_M1 =  { 'F' , 'H' };
    
    // Root menu items names strings.
    private final static String[][] NAMES_M2 = 
    { 
        { "Load InstLatX64 CPUID" , "Save report" ,  "Exit"  } ,
        { "About" } 
    };
    
    // Mnemonics keys for root menu items. This KEY at opened menu.
    private final static char[][] MNEMONICS_M2 =
    { { 'L', 'S', 'X' } , { 'A' } };
    
    // Accelerator keys for root menu sub-menus. ALT-KEY at any context.
    private final static KeyStroke[][] ACCELERATORS_M2 =
    { 
        { 
            KeyStroke.getKeyStroke( 'L' , KeyEvent.ALT_DOWN_MASK ) ,
            KeyStroke.getKeyStroke( 'S' , KeyEvent.ALT_DOWN_MASK ) ,
            KeyStroke.getKeyStroke( 'X' , KeyEvent.ALT_DOWN_MASK ) ,
        } ,
        { 
            KeyStroke.getKeyStroke( 'A' , KeyEvent.ALT_DOWN_MASK ) ,
        } 
    };
    
    // Root menu separators.
    private final static boolean[][] SEPARATORS_M2 =
    { 
        { false , true, false } , { false }
    };
    
    // Root menu items status: true = supported, false = not supported (gray).
    private final static boolean[][] ITEM_ACTIVE =
    { 
        { true  , true, true } , { true } 
    };
    
    // Listeners (handlers) for service buttons press.
    private final AbstractAction[] listeners = 
    { 
        new LoadInstLatX64Action() ,
        new SaveReportAction() ,
        new ExitAction() ,
        new AboutAction() 
    };
    
    public RootMenu()
    {
        thisFrame = this;
        rootMenu = new JMenuBar();
        // Root menu: cycle for vertical root menus.
        int n1 = NAMES_M1.length;
        JMenu[] m1 = new JMenu[n1];
        for( int i=0; i<n1; i++ ) 
        {
            m1[i] = new JMenu( NAMES_M1[i] );
            m1[i].setMnemonic( MNEMONICS_M1[i] );
        }
        // Root menu: cycle for items in vertical root menus.
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
        // Root menu: cycle for set activity and add listeners.
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

        // Tabbed pane with application functionality panels.
        tabbedPane = new JTabbedPane();
        enumerator = new ApplicationEnumerator();
        String[] names = enumerator.getTabNames();
        Icon[] icons = enumerator.getTabIcons();
        JPanel[] panels = enumerator.getTabPanels();
        boolean[] actives = enumerator.getTabActives();
        String[] tips = enumerator.getTabTips();
        
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
            enumerator.buildTabPanels( true );
        }
    }

    // Geometry definition for tabbed pane and down buttons.
    private final static int T_UP = -1;
    private final static int T_DOWN = -3;
    private final static int T_LEFT = 2;
    private final static int T_RIGHT = -2;
    
    public void showGui()
    {
        // Show root menu and other GUI objects.
        setJMenuBar( rootMenu );
        SpringLayout layout = new SpringLayout();
        JPanel panel = new JPanel( layout );
        panel.add( tabbedPane );
        getContentPane().add( panel );
        
        // Setup tabbed pane leafs location.
        HelperLayout.springCenter
            ( layout, panel, tabbedPane, T_UP, T_DOWN, T_LEFT, T_RIGHT );

        // Entire panel options, sizes and title string.
        setDefaultCloseOperation( EXIT_ON_CLOSE );
        setSize( APP_X, APP_Y );
        String titleString = DumpLoader.getLongName();

        setTitle( titleString );
        setLocationByPlatform( true );
        setVisible( true );
    }
    
    // Listeners classes for this root menu items.

    final class LoadInstLatX64Action extends AbstractAction
    {
        @Override public void actionPerformed( ActionEvent e )
        {
            ReportLoader reportLoader = new ReportLoader();
            int[] cpuDump = reportLoader.loadTextDialogue( thisFrame );
            if ( cpuDump != null )
            {  // If dump loaded, interpreting it and reinitializing CPUID results.
                CpuRootEnumerator cre = new CpuRootEnumerator( cpuDump );
                if ( cre.getStatus() )
                {
                    // Clear panels CPUID-depend panels at JTabbedPane.
                    // Rebuild CPUID-depend panels at JTabbedPane.
                    DumpLoader.setCpuRootEnumerator( cre );
                    enumerator.rebuildAfterCpuidReload( tabbedPane, false );
                }
                else
                {
                    JOptionPane.showMessageDialog( thisFrame, 
                        "CPUID dump interpreting failed.",
                        getShortName() + " - Load InstLatX64 dump", 
                        JOptionPane.ERROR_MESSAGE );
                }
            }
        }
    }
    
    final class SaveReportAction extends AbstractAction
    {
        @Override public void actionPerformed( ActionEvent e )
        {
//          boolean reportType = false;  // remove this from dump loader
            
            AbstractTableModel[] models = 
            enumerator.getTabModels();
            ReportSaver reportSaver = new ReportSaver();
            String appStr = "[ " + getLongName() + ". ]\r\n";
            String webStr = getProjectWeb() + "\r\n" + getAllWeb() +
                "\r\n" + getVendorName1() + " " + getVendorName2() + "\r\n";
/*
            reportSaver.reportSystemOrFullDialogue
                ( thisFrame, reportType, models, null, appStr, webStr );
*/
            reportSaver.reportDialogue
                ( thisFrame, models, null, appStr, webStr );
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
            final JDialog dialog = about.createDialog( null );
            dialog.setLocationRelativeTo( thisFrame );
            dialog.setVisible( true );    
        }
    }
}
