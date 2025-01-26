/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Java CPUID application main screen GUI JFrame 
with root menu and items handlers connection.

*/

package cpuidv3.gui;

import static cpuidv3.CPUIDv3.getShortName;
import static cpuidv3.CPUIDv3.getAllWeb;
import static cpuidv3.CPUIDv3.getLongName;
import static cpuidv3.CPUIDv3.getProjectWeb;
import static cpuidv3.CPUIDv3.getVendorName1;
import static cpuidv3.CPUIDv3.getVendorName2;
import cpuidv3.CPUIDv3;
import cpuidv3.guipanels.ApplicationEnumerator;
import cpuidv3.services.SAL;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
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
    private final static int APP_X = 1024 - 68;
    private final static int APP_Y = 768 - 50;
    private final JFrame thisFrame;
    private final JMenuBar rootMenu;
    private final JTabbedPane tabbedPane;
    private final ApplicationEnumerator enumerator;
    private final static String[] NAMES_M1 = { "File" , "Platform" , "Help" };
    private final static char[] MNEMONICS_M1 =  { 'F' , 'P' , 'H' };
    private final static String[][] NAMES_M2 = 
    { 
        { "Save system report" , "Save one cpu report" , "Save screen report" ,
          "Save hex all CPUs" , "Save hex one CPU" ,
          "Load InstLatX64 CPUID" , "Save binary" , "Load binary" ,
          "Exit" } ,
        { "Redetect all CPUs" , 
          "Redetect selected CPU", "Redetect any CPU" } ,
        { "About" } 
    };
    private final static char[][] MNEMONICS_M2 =
    {   // Usage: KEY at opened menu.
        { 'S' , 'O' , 'C' , 'E' , 'O' , 'L' , 'B' , 'I' , 'X' } , 
        { 'R' , 'S' , 'A' } , { 'A' } 
    };
        private final static KeyStroke[][] ACCELERATORS_M2 =
    {   // Usage: ALT-KEY at any context.
        { KeyStroke.getKeyStroke( 'S' , KeyEvent.ALT_DOWN_MASK ) ,
          null , null ,
          KeyStroke.getKeyStroke( 'H' , KeyEvent.ALT_DOWN_MASK ) ,
          KeyStroke.getKeyStroke( 'O' , KeyEvent.ALT_DOWN_MASK ) ,
          KeyStroke.getKeyStroke( 'L' , KeyEvent.ALT_DOWN_MASK ) ,
          null , null ,
          KeyStroke.getKeyStroke( 'X' , KeyEvent.ALT_DOWN_MASK ) } ,
        { KeyStroke.getKeyStroke( 'R' , KeyEvent.ALT_DOWN_MASK ) ,
          null , null } , 
        { null } 
    };
    private final static boolean[][] SEPARATORS_M2 =
    { { false , false , true , false , false , true , false , true , false },
      { false , false , false } ,
      { false } };
    private final static boolean[][] ITEM_ACTIVE =
    { { true , true , true , true , true , true , true , true , true } ,
      { true , true , true } , 
      { true } };
    private final AbstractAction[] listeners = 
    { new SaveSystemReportAction()    ,
      new SaveOneCpuReportAction()    ,
      new SaveScreenReportAction()    ,
      new SaveHexAllCpusAction()      ,
      new SaveHexOneCpuAction()       ,
      new LoadInstLatX64Action()      ,
      new SaveBinaryAction()          ,
      new LoadBinaryAction()          ,
      new ExitAction()                ,
      new RedetectAllCpusAction()     ,
      new RedetectSelectedCpuAction() ,
      new RedetectAnyCpuAction()      ,
      new AboutAction()              };
    
    public RootMenu()
    {
        thisFrame = this;
        rootMenu = new JMenuBar();
        int n1 = NAMES_M1.length;
        JMenu[] m1 = new JMenu[n1];
        for( int i=0; i<n1; i++ ) 
        {   // Root menu: cycle for horizontal root menus.
            m1[i] = new JMenu( NAMES_M1[i] );
            m1[i].setMnemonic( MNEMONICS_M1[i] );
        }
        JMenuItem[][] m2 = new JMenuItem[n1][];
        for( int i=0; i<n1; i++ )
        {   // Root menu: (i)-cycle for items in horizontal root menus.
            int n2 = NAMES_M2[i].length;
            m2[i] = new JMenuItem[n2];
            for( int j=0; j<n2; j++ )
            {   // Root menu: (j)-cycle for items in vertical child menus.
                m2[i][j] = new JMenuItem( NAMES_M2[i][j] );
                m2[i][j].setMnemonic( MNEMONICS_M2[i][j] );
                m2[i][j].setAccelerator( ACCELERATORS_M2[i][j] );
                m1[i].add( m2[i][j] );
                if ( SEPARATORS_M2[i][j] ) { m1[i].addSeparator(); }
            }
            rootMenu.add( m1[i] );
        }
        int k = 0;
        int nm1 = NAMES_M1.length;
        m:
        for ( int i=0; i<nm1; i++ )
        {   // Root menu: cycle for set activity and add listeners.
            int nm2 = NAMES_M2[i].length;
            for ( int j=0; j<nm2; j++ )
            {
                if (( listeners == null ) || ( k >= listeners.length )) break m;
                if ( listeners[k] != null )
                {
                    m2[i][j].setEnabled( ITEM_ACTIVE[i][j] );
                    m2[i][j].addActionListener( listeners[k++] );
                }
            }
        }

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
    
    private final static Dimension DB = new Dimension( 102, 27 );
    private final static int B_INTERVAL = -2;
    private final static int B_DOWN = -4;
    private final static int B_RIGHT = -3;
    private final static int T_UP = -1;
    private final static int T_DOWN = B_DOWN - DB.height - 5;
    private final static int T_LEFT = 2;
    private final static int T_RIGHT = -2;
    private final static String[] BUTTONS_NAMES = 
    { "Redetect"    , 
      "Save hex"    , "Load hex"    , 
      "Save binary" , "Load binary" ,
      "Report all"  , "Report CPU"  , "Report this" , 
      "Exit"        };
    private final static String[] BUTTONS_TIPS =
    { "Refresh CPUID information from the physical platform."           ,
      "Save hex dump file at InstLatX64 format, can be loaded later."   ,
      "Load hex dump file at InstLatX64 format."                        ,
      "Save binary dump file for one CPU, can be loaded later."         ,
      "Load binary dump file for one CPU."                              ,
      "Save text report with detail system information."                ,
      "Save text report with CPUID information for one CPU."            ,
      "Save text report for current visualized screen."                 ,  
      "Exit application."                                              };
    private final static int[] BUTTONS_KEYS = 
        { '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    public void showGui()
    {
        setJMenuBar( rootMenu );
        SpringLayout layout = new SpringLayout();
        JPanel panel = new JPanel( layout );
        panel.add( tabbedPane );
        getContentPane().add( panel );
        HelperLayout.springCenter
            ( layout, panel, tabbedPane, T_UP, T_DOWN, T_LEFT, T_RIGHT );
        
        JButton[] buttons = new JButton[BUTTONS_NAMES.length];
        ActionListener[] actionsListeners = new ActionListener[]
        {
            new BRedetect()   ,
            new BSaveHex()    , new BLoadHex()    , 
            new BSaveBinary() , new BLoadBinary() ,
            new BReportAll()  , new BReportCpu()  , new BReportThis() ,
            new BExit()
        };
        HelperButton.downButtons( panel, buttons, 
            BUTTONS_NAMES, BUTTONS_TIPS, BUTTONS_KEYS,  BUTTONS_NAMES.length, 
            actionsListeners, layout, B_DOWN, B_RIGHT, B_INTERVAL, DB );

        setDefaultCloseOperation( EXIT_ON_CLOSE );
        setSize( APP_X, APP_Y );
        String titleString = getLongName();
        String addString = CPUIDv3.getSal().getRuntimeName();
        if( ( addString != null )&&( !addString.equals("") ) )
        {
            titleString = String.format( "%s  ( %s )", titleString, addString );
        }
        setTitle( titleString );
        setLocationByPlatform( true );
        setVisible( true );
    }

    
    // Listeners classes for this root menu items.

    
    private final class SaveSystemReportAction extends AbstractAction
    {
        @Override public void actionPerformed( ActionEvent e )
        {
            SAL sal = CPUIDv3.getSal();
            AbstractTableModel[] models = enumerator.getTabModels( false );
            if ( ( models != null )&&( models.length > 0 ) )
            {
                ReportSaver reportSaver = new ReportSaver();
                reportSaver.saveReportDialogue( thisFrame, models, null,
                    "System report",
                    helperAppStr( sal ), helperWebStr() );
            }
            else
            {
                JOptionPane.showMessageDialog( thisFrame, 
                    "Context is not ready.",
                    getShortName() + " - System report", 
                    JOptionPane.ERROR_MESSAGE );
            }
        }
    }
    
    private final class SaveOneCpuReportAction extends AbstractAction
    {
        @Override public void actionPerformed( ActionEvent e )
        {
            SAL sal = CPUIDv3.getSal();
            AbstractTableModel[] models = enumerator.getTabModels( true );
            if ( ( models != null )&&( models.length > 0 ) )
            {
                ReportSaver reportSaver = new ReportSaver();
                reportSaver.saveReportDialogue( thisFrame, models, null,
                    "CPUID report",
                    helperAppStr( sal ), helperWebStr() );
            }
            else
            {
                JOptionPane.showMessageDialog( thisFrame, 
                    "Context is not ready.",
                    getShortName() + " - CPUID report", 
                    JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    private final class SaveScreenReportAction extends AbstractAction
    {
        @Override public void actionPerformed( ActionEvent e )
        {
            int tabIndex = tabbedPane.getSelectedIndex();
            if( tabIndex >= 0 )
            {
                SAL sal = CPUIDv3.getSal();
                AbstractTableModel[] thisModels = 
                    enumerator.getReportThisTabModels( tabIndex );
                AbstractTableModel evenTable = null;
                AbstractTableModel oddTable = null;
                if ( ( thisModels != null )&&( thisModels.length >0 ) )
                {
                    if( thisModels.length >= 2 ) 
                    {
                        evenTable = thisModels[0];
                        oddTable = thisModels[1];
                    }
                    else
                    {
                        evenTable = thisModels[0];
                        oddTable = null;
                    }
                    ReportSaver reportSaver = new ReportSaver();
                    reportSaver.saveReportDialogue( thisFrame, 
                        evenTable, oddTable,
                        "Current screen report",
                        helperAppStr( sal ), helperWebStr() );
                }
                else
                {
                    JOptionPane.showMessageDialog( thisFrame, 
                        "Context is not ready.",
                        getShortName() + " - this screen report", 
                        JOptionPane.ERROR_MESSAGE );
                }
            }
        }
    }

    private final class SaveHexAllCpusAction extends AbstractAction
    {
        @Override public void actionPerformed( ActionEvent e )
        {
            SAL sal = CPUIDv3.getSal();
            long[][] cpuDump = sal.getCpuidBinaryData();
            if( ( cpuDump != null )&&( cpuDump.length > 0 ) )
            {
                HexSaver hexSaver = new HexSaver();
                hexSaver.saveHexDialogue( thisFrame, cpuDump,
                    "CPUID hex dump file for all CPUs.",
                    helperAppStr( sal ), helperWebStr() );
            }
            else
            {
                JOptionPane.showMessageDialog( thisFrame, 
                    "Context is not ready.",
                    getShortName() + " - Save hex dump", 
                    JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    private final class SaveHexOneCpuAction extends AbstractAction
    {
        @Override public void actionPerformed( ActionEvent e )
        {
            SAL sal = CPUIDv3.getSal();
            long[][] cpuDump = sal.getCpuidBinaryData();
            if( ( cpuDump != null )&&( cpuDump.length > 0 )&&
                ( cpuDump[0] != null )&&( cpuDump[0].length > 0 ) )
            {
                HexSaver hexSaver = new HexSaver();
                hexSaver.saveHexDialogue( thisFrame, cpuDump[0],
                    "CPUID hex dump file for one CPU.",
                    helperAppStr( sal ), helperWebStr() );
            }
            else
            {
                JOptionPane.showMessageDialog( thisFrame, 
                    "Context is not ready.",
                    getShortName() + " - Save hex dump", 
                    JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    private String helperAppStr( SAL sal )
    {
        String appStr;
        String runtimeName = sal.getRuntimeName();
        if( runtimeName != null )
        {
            appStr = "[ " + getLongName() + ". ][ " + 
                     runtimeName + ". ]\r\n";
        }
        else
        {
            appStr = "[ " + getLongName() + ". ]\r\n";
        }
        return appStr;
    }
    
    private String helperWebStr()
    {
        String webStr = getProjectWeb() + "\r\n" + getAllWeb() +
            "\r\n" + getVendorName1() + " " + getVendorName2() +
            "\r\n";
        return webStr;
    }
    
    private final class LoadInstLatX64Action extends AbstractAction
    {
        @Override public void actionPerformed( ActionEvent e )
        {
            HexLoader hexLoader = new HexLoader();
            int[] cpuDump = hexLoader.loadTextDialogue( thisFrame );
            if ( cpuDump != null )
            {   // If dump loaded, interpreting it and re-init CPUID results.
                SAL sal = CPUIDv3.getSal();
                boolean loadStatus = sal.setCpuidBinaryData( cpuDump );
                if ( loadStatus )
                {   
                    sal.restartOverride( true );
                    // Clear all panels at JTabbedPane and 
                    // rebuild CPUID-depend panels at JTabbedPane.
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

    private final class SaveBinaryAction extends AbstractAction
    {
        @Override public void actionPerformed( ActionEvent e )
        {
            SAL sal = CPUIDv3.getSal();
            long[][] cpuDump = sal.getCpuidBinaryData();
            if( ( cpuDump != null )&&( cpuDump.length > 0 )&&
                ( cpuDump[0] != null )&&( cpuDump[0].length > 0 ) )
            {
                BinarySaver binarySaver = new BinarySaver();
                binarySaver.saveBinaryDialogue( thisFrame, cpuDump[0] );
            }
            else
            {
                JOptionPane.showMessageDialog( thisFrame, 
                    "Context is not ready.",
                    getShortName() + " - Save binary dump", 
                    JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    private final class LoadBinaryAction extends AbstractAction
    {
        @Override public void actionPerformed( ActionEvent e )
        {
            BinaryLoader binaryLoader = new BinaryLoader();
            long[] cpuDump = binaryLoader.loadBinaryDialogue( thisFrame );
            if ( cpuDump != null )
            {   // If dump loaded, interpreting it and re-init CPUID results.
                SAL sal = CPUIDv3.getSal();
                boolean loadStatus = sal.setCpuidBinaryData( cpuDump );
                if ( loadStatus )
                {   
                    sal.restartOverride( true );
                    // Clear all panels at JTabbedPane and 
                    // rebuild CPUID-depend panels at JTabbedPane.
                    enumerator.rebuildAfterCpuidReload( tabbedPane, false );
                }
                else
                {
                    JOptionPane.showMessageDialog( thisFrame, 
                        "CPUID dump interpreting failed.",
                        getShortName() + " - Load binary dump", 
                        JOptionPane.ERROR_MESSAGE );
                }
            }
        }
    }

    private final class ExitAction extends AbstractAction
    {
        @Override public void actionPerformed( ActionEvent e )
        {
            System.exit(0);
        }
    }

    private final class RedetectAllCpusAction extends AbstractAction
    {
        @Override public void actionPerformed( ActionEvent e )
        {
            SAL sal = CPUIDv3.getSal();
            sal.clearAllBinaryData();
            boolean operationStatus = sal.internalLoadAllBinaryData();
            if( operationStatus )
            {
                sal.restartOverride( false );
                enumerator.rebuildAfterCpuidReload( tabbedPane, true );
                JOptionPane.showMessageDialog( thisFrame, 
                    "Processor(s) redetected.",
                    "Redetect processor", JOptionPane.WARNING_MESSAGE );
            }
            else
            {
                JOptionPane.showMessageDialog( thisFrame,
                    "Get CPUID information failed.",
                    getShortName() + " - Redetect processor(s)", 
                    JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    private final class RedetectSelectedCpuAction extends AbstractAction
    {
        @Override public void actionPerformed( ActionEvent e )
        {
            SAL sal = CPUIDv3.getSal();
            sal.clearAllBinaryData();
            boolean operationStatus = sal.internalLoadAllBinaryData();
            if( operationStatus )
            {
                sal.restartOverride( false );
                long[][] allData = sal.getCpuidBinaryData();
                int cpuCount = allData.length;
                final int CPU_LIMIT = 64;  // TODO. Remove this limit.
                if ( cpuCount > CPU_LIMIT )
                {
                    cpuCount = CPU_LIMIT;
                }
                boolean affinization = false;
                String[] items = new String[cpuCount + 1];
                items[0] = 
                    String.format( "Non affinized (%d-%d)", 0, cpuCount - 1);
                for( int i=0; i<cpuCount; i++ )
                {
                    items[i + 1] = String.format( "Logical CPU # %d", i );
                }
                MpState mpState = new MpState
                    ( cpuCount, CPU_LIMIT, CPU_LIMIT, affinization, items );
                
                CpuSelector selector = new CpuSelector();
                boolean[] makeSelection = new boolean[]{ false };
                final JDialog dialog = selector.createDialog( null,
                        makeSelection, mpState  );
                dialog.setLocationRelativeTo( thisFrame );
                dialog.setVisible( true );
                // Make CPUID affinized redetect and binary date reload.
                if( makeSelection[0] )
                {
                    sal.clearAllBinaryData();
                    if( mpState.affinizationEnabled )
                    {
                        if( mpState.processorSelected < allData.length )
                        {
                            long[] singleData = 
                                allData[ mpState.processorSelected ];
                            long[][] setData = new long[][]{singleData};
                            operationStatus = sal.setCpuidBinaryData( setData );
                        }
                        else
                        {
                            operationStatus = false;
                        }
                            
                        if ( operationStatus )
                        {
                            enumerator.rebuildAfterCpuidReload
                                ( tabbedPane, true );
                            String statusString = String.format
                                ( "Processor redetected, affinity = %d.",
                                  mpState.processorSelected );
                            JOptionPane.showMessageDialog( thisFrame,
                                statusString ,
                                getShortName() + "- Redetect processor",
                                JOptionPane.WARNING_MESSAGE );
                        }
                        else
                        {
                            JOptionPane.showMessageDialog( thisFrame,
                                "Processor redetection failed.",
                                getShortName() + " - Redetect processor.", 
                                JOptionPane.ERROR_MESSAGE );
                        }
                    }
                    else
                    {
                        operationStatus = sal.internalLoadNonAffinizedCpuid();
                        if ( operationStatus )
                        {
                            enumerator.rebuildAfterCpuidReload
                                ( tabbedPane, true );
                            JOptionPane.showMessageDialog( thisFrame,
                                "Processor redetected without affinization",
                                getShortName() + "- Redetect processor",
                                JOptionPane.WARNING_MESSAGE );
                        }
                        else
                        {
                            JOptionPane.showMessageDialog( thisFrame,
                                "Processor redetection failed.",
                                getShortName() + " - Redetect processor.", 
                                JOptionPane.ERROR_MESSAGE );
                        }
                    }
                }
            }
            else
            {
                JOptionPane.showMessageDialog( thisFrame,
                    "Get CPUID information failed (single CPU mode).",
                    getShortName() + " - Redetect processor.", 
                    JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    private final class RedetectAnyCpuAction extends AbstractAction
    {
        @Override public void actionPerformed( ActionEvent e )
        {
            SAL sal = CPUIDv3.getSal();
            sal.clearAllBinaryData();
            boolean operationStatus = sal.internalLoadNonAffinizedCpuid();
            if( operationStatus )
            {
                sal.restartOverride( false );
                enumerator.rebuildAfterCpuidReload( tabbedPane, true );
                JOptionPane.showMessageDialog( thisFrame, 
                    "Processor redetected (non affinized mode)",
                    "Redetect processor", JOptionPane.WARNING_MESSAGE );
            }
            else
            {
                JOptionPane.showMessageDialog( thisFrame,
                    "Get CPUID information failed (non affinized mode).",
                    getShortName() + " - Redetect processor.", 
                    JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    private final class AboutAction extends AbstractAction
    {
        @Override public void actionPerformed( ActionEvent e )
        {
            About about = new About();
            final JDialog dialog = about.createDialog( null );
            dialog.setLocationRelativeTo( thisFrame );
            dialog.setVisible( true );    
        }
    }

    
    // Down-located buttons listeners, handlers for press buttons events.

    
    private final class BRedetect implements ActionListener
    {
        @Override public void actionPerformed ( ActionEvent e )
        {
            RedetectAllCpusAction redetector = new RedetectAllCpusAction();
            redetector.actionPerformed( e );
        }
    }
    
    private final class BSaveHex implements ActionListener
    {
        @Override public void actionPerformed ( ActionEvent e )
        {
            SaveHexAllCpusAction saver = new SaveHexAllCpusAction();
            saver.actionPerformed( e );
        }
    }
    
    private final class BLoadHex implements ActionListener
    {
        @Override public void actionPerformed ( ActionEvent e )
        {
            LoadInstLatX64Action loader = new LoadInstLatX64Action();
            loader.actionPerformed( e );
        }
    }

    private final class BSaveBinary implements ActionListener
    {
        @Override public void actionPerformed ( ActionEvent e )
        {
            SaveBinaryAction saver = new SaveBinaryAction();
            saver.actionPerformed( e );
        }
    }
    
    private final class BLoadBinary implements ActionListener
    {
        @Override public void actionPerformed ( ActionEvent e )
        {
            LoadBinaryAction loader = new LoadBinaryAction();
            loader.actionPerformed( e );
        }
    }

    private final class BReportAll implements ActionListener
    {
        @Override public void actionPerformed ( ActionEvent e )
        {
            SaveSystemReportAction saver = new SaveSystemReportAction();
            saver.actionPerformed( e );
        }
    }
    
    private final class BReportCpu implements ActionListener
    {
        @Override public void actionPerformed ( ActionEvent e )
        {
            SaveOneCpuReportAction saver = new SaveOneCpuReportAction();
            saver.actionPerformed( e );
        }
    }

    private final class BReportThis implements ActionListener
    {
        @Override public void actionPerformed ( ActionEvent e )
        {
            SaveScreenReportAction saver = new SaveScreenReportAction();
            saver.actionPerformed( e );
        }
    }

    private final class BExit implements ActionListener
    {
        @Override public void actionPerformed ( ActionEvent e )
        {
            System.exit(0);
        }
    }
    
}
