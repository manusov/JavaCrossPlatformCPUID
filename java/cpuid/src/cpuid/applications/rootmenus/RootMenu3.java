/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
Root menu, variant #3, full-functional with left tree menu and root menu,
Add new functions as root menu items:
File:   Report entire system, Exit.
Target: Redetect, Deserialize from LAN, Deserialize from file,
        Serialize to LAN, Serialize to file.
Info:   About.
This class constructor called from main class.
*/

package cpuid.applications.rootmenus;

import cpuid.About;
import static cpuid.CpuId.createRegistry;
import cpuid.applications.tools.ActionAbout;
import cpuid.applications.tools.ActionReport;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

public class RootMenu3 extends RootMenu   // Note RootMenu extends JFrame 
{
private JFrame thisFrame;         // application root frame
    
private final JMenuBar jmenu;     // root menu bar
private final JMenu[] m1;         // array of sub-menus
private final JMenuItem[][] m2;   // array of sub-menus end items

// root menu sub-menus names strings
private final static String[] NAMES_M1 = { "File" , "Target" , "Info" };

// mnemonics keys for root menu sub-menus
private final static char[] MNEMONICS_M1 =  { 'F' , 'T' , 'I' };

// root menu items names strings
private final static String[][] NAMES_M2 = 
    {   
        { "System report" , "Exit" } ,
        { "Local redetect" , 
          "Remote deserialize" , "File deserialize" ,
          "Serialize remote"   , "Serialize file" } ,
        { "About" }                                                 
    };

// mnemonics keys for root menu items
private final static char[][] MNEMONICS_M2 =
    {
        { 'S' , 'X' } , 
        { 'L' , 'R' , 'F' , 'S' , 'E' } ,
        { 'A' }
    };

// accelerator keys for root menu sub-menus
private final static KeyStroke[][] ACCELERATORS_M2 =
    {
        { KeyStroke.getKeyStroke( 'S' , KeyEvent.ALT_MASK )  , 
          KeyStroke.getKeyStroke( 'X' , KeyEvent.ALT_MASK ) } ,
        { null , null , null , null , null } ,
        { null }
    };

// root menu separators
private final static boolean[][] SEPARATORS_M2 =
    {
        { false , false } , 
        { false , false , true , false , false } ,
        { false }
    };

// root menu items status: true = supported, false = not supported (gray)
private final static boolean[][] ITEM_ACTIVE =
    {
        { true  , true } , 
        { true  , false , false , false , false } ,
        { true }
    };

// listeners (handlers) for service buttons press
protected final AbstractAction[] listeners = 
    {
    new HandlerSystemReport() ,
    new HandlerExit() ,
    new HandlerLocalRedetect() ,
    new HandlerRemoteDeserialize() ,
    new HandlerFileDeserialize() ,
    new HandlerSerializeRemote() ,
    new HandlerSerializeFile() ,
    new HandlerAbout()
    };

// Frame and applications system tree constructor
// Called from main class to build GUI

public RootMenu3()
    {
    // Root menu initializing
    jmenu = new JMenuBar();
    // Root menu: cycle for vertical root menus
    int n1 = NAMES_M1.length;
    m1 = new JMenu[n1];
    for( int i=0; i<n1; i++ ) 
        {
        m1[i] = new JMenu( NAMES_M1[i] );
        m1[i].setMnemonic( MNEMONICS_M1[i] );
        }
    // Root menu: cycle for items in vertical root menus
    m2 = new JMenuItem[n1][];
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
        jmenu.add( m1[i] );
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
    // System tree initializing
    SystemTreeBuilder stb = new SystemTreeBuilder();
    pApps = stb.getApps();      // array of applications panels
    rApps = stb.getReports();   // array of applications table models
    mRoot = stb.getTree();      // default tree model
    // Tree=f(Model)
    tRoot = new JTree(mRoot);
    tRoot.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);
    // Listener for tree events
    tRoot.addTreeSelectionListener(new RootListener());
    // Scroll panel for tree
    scrollApp = new JScrollPane(tRoot);
    // Built split panel
    split = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true );
    split.setOneTouchExpandable(true);
    split.setDividerSize(8);
    split.setDividerLocation( About.getX1size() / 7 + 5 );
    split.setLeftComponent(scrollApp);
    split.setRightComponent( pApps[0] );
    }

// Show panel of root menu
// Called from main class to show GUI

@Override public void showGUI()
    {
    thisFrame = this;    // this = JFrame
    setJMenuBar(jmenu);
    add(split);
    setDefaultCloseOperation( EXIT_ON_CLOSE );
    setSize( About.getX1size(), About.getY1size() );
    setTitle( About.getLongName() );
    setLocationByPlatform(true);
    setVisible(true);
    }

// Listeners classes for this root menu items

class HandlerSystemReport extends AbstractAction
    {
    @Override public void actionPerformed(ActionEvent e)
        {
        AbstractTableModel[] atma1 = rApps;
        AbstractTableModel[] atma2 = null;
        ActionReport report = new ActionReport();
        report.createDialogRF
            ( null , atma1 , atma2 ,
              About.getShortName() , About.getVendorName() );
        }
    }

class HandlerExit extends AbstractAction
    {
    @Override public void actionPerformed(ActionEvent e)
        {
        System.exit(0);
        }
    }

class HandlerLocalRedetect extends AbstractAction
    {
    @Override public void actionPerformed(ActionEvent e)
        {
        int status = createRegistry();
        if ( status <= 0 )
            {
            // Message box
            JOptionPane.showMessageDialog
                ( null, String.format
                ( "PAL reinitialization failed\n Status = %d", status ),
                About.getShortName(), JOptionPane.ERROR_MESSAGE );
            }
        else
            {
            // Message box
            JOptionPane.showMessageDialog
                ( null, String.format
                ( "PAL reinitialized successfully" ),
                About.getShortName(), JOptionPane.WARNING_MESSAGE ); 
            // System tree initializing
            SystemTreeBuilder stb = new SystemTreeBuilder();
            pApps = stb.getApps();
            rApps = stb.getReports();
            mRoot = stb.getTree();
            split.setRightComponent( pApps[0] );
            // Unselect tree node
            TreePath tp = tRoot.getPathForRow(0);
            tRoot.setSelectionPath(tp);
            }
        }
    }

class HandlerRemoteDeserialize extends AbstractAction
    {
    @Override public void actionPerformed(ActionEvent e)
        {
        }
    }

class HandlerFileDeserialize extends AbstractAction
    {
    @Override public void actionPerformed(ActionEvent e)
        {
        }
    }

class HandlerSerializeRemote extends AbstractAction
    {
    @Override public void actionPerformed(ActionEvent e)
        {
        }
    }

class HandlerSerializeFile extends AbstractAction
    {
    @Override public void actionPerformed(ActionEvent e)
        {
        }
    }

class HandlerAbout extends AbstractAction
    {
    @Override public void actionPerformed(ActionEvent e)
        {
        ActionAbout about = new ActionAbout();
        final JDialog dialog = about.createDialog
        ( null , About.getShortName() , About.getVendorName() );
        dialog.setLocationRelativeTo( thisFrame );
        dialog.setVisible(true);    
        }
    }

}
