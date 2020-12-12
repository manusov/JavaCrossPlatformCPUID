/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Java CPUID application GUI main screen JFrame 
with root menu and items handlers.
*/

package cpuidrefactoring.rootmenu;

import cpuidrefactoring.About;
import static cpuidrefactoring.CpuidRefactoring.createRegistry;
import cpuidrefactoring.applications.ApplicationCpuid;
import cpuidrefactoring.tools.ActionAbout;
import cpuidrefactoring.tools.ActionReport;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class RootMenu extends JFrame
{
private JFrame thisFrame;         // application root frame
private final JMenuBar jmenu;     // root menu bar
private final JMenu[] m1;         // array of sub-menus
private final JMenuItem[][] m2;   // array of sub-menus end items
protected JPanel[] pApps;
protected ChangeableTableModel[] rApps;
protected JSplitPane split;
protected JScrollPane scrollApp;
protected JTree tRoot;
protected DefaultTreeModel mRoot;

// used for load text
private ApplicationCpuid appCpuid;

// root menu sub-menus names strings
private final static String[] NAMES_M1 = { "File" , "Target" , "Info" };
// mnemonics keys for root menu sub-menus
private final static char[] MNEMONICS_M1 =  { 'F' , 'T' , 'I' };
// root menu items names strings
private final static String[][] NAMES_M2 = 
    { { "System report" , "Exit" } ,
      { "Local redetect" , "InstLatx64 CPUID file" ,
        "Remote deserialize" , "File deserialize" ,
        "Serialize remote"   , "Serialize file" } ,
        { "About" } };
// mnemonics keys for root menu items
private final static char[][] MNEMONICS_M2 =
    { { 'S' , 'X' } , 
      { 'L' , 'I' , 'R' , 'F' , 'S' , 'E' } ,
      { 'A' } };
// accelerator keys for root menu sub-menus
private final static KeyStroke[][] ACCELERATORS_M2 =
    { { KeyStroke.getKeyStroke( 'S' , KeyEvent.ALT_MASK )  , 
        KeyStroke.getKeyStroke( 'X' , KeyEvent.ALT_MASK ) } ,
        { null , null , null , null , null , null } ,
        { null } };
// root menu separators
private final static boolean[][] SEPARATORS_M2 =
    { { false , false } , 
      { false , true , false , true , false , false } ,
      { false } };
// root menu items status: true = supported, false = not supported (gray)
private final static boolean[][] ITEM_ACTIVE =
    { { true  , true } , 
      { true  , true , false , false , false , false } ,
      { true } };
// listeners (handlers) for service buttons press
protected final AbstractAction[] listeners = 
    { new HandlerSystemReport() ,
      new HandlerExit() ,
      new HandlerLocalRedetect() ,
      new HandlerInstLatx64CpuidFile() ,
      new HandlerRemoteDeserialize() ,
      new HandlerFileDeserialize() ,
      new HandlerSerializeRemote() ,
      new HandlerSerializeFile() ,
      new HandlerAbout() };

// Frame and applications system tree constructor
// Called from main class to build GUI
public RootMenu()
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
    pApps = stb.getApplicationPanels();   // array of applications panels
    rApps = stb.getApplicationReports();  // array of applications table models
    mRoot = stb.getApplicationTrees();    // default tree model
    // Store application CPUID for special usage: load text
    appCpuid = stb.getAppCpuid();
    // Tree=f(Model)
    tRoot = new JTree( mRoot );
    tRoot.getSelectionModel().setSelectionMode
        ( TreeSelectionModel.SINGLE_TREE_SELECTION );
    // Listener for tree events
    tRoot.addTreeSelectionListener( new RootListener()) ;
    // Scroll panel for tree
    scrollApp = new JScrollPane( tRoot );
    // Built split panel
    split = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true );
    split.setOneTouchExpandable(true);
    split.setDividerSize(8);
    split.setDividerLocation( About.getX1size() / 7 + 5 );
    split.setLeftComponent( scrollApp );
    split.setRightComponent( pApps[0] );
    }

// Show panel of root menu
// Called from main class to show GUI
public void showGUI()
    {
    thisFrame = this;    // this = JFrame
    setJMenuBar( jmenu );
    add( split );
    setDefaultCloseOperation( EXIT_ON_CLOSE );
    setSize( About.getX1size(), About.getY1size() );
    setTitle( About.getLongName() );
    setLocationByPlatform( true );
    setVisible( true );
    }

// Listeners classes for this root menu items

class HandlerSystemReport extends AbstractAction
    {
    @Override public void actionPerformed( ActionEvent e )
        {
        AbstractTableModel[] atma1 = rApps;
        AbstractTableModel[] atma2 = null;
        ActionReport report = new ActionReport();
        report.reportFullDialogue
            ( null , atma1 , atma2 ,
              About.getShortName() , About.getVendorName() );
        }
    }

class HandlerExit extends AbstractAction
    {
    @Override public void actionPerformed( ActionEvent e )
        {
        System.exit(0);
        }
    }

class HandlerLocalRedetect extends AbstractAction
    {
    @Override public void actionPerformed( ActionEvent e )
        {
        int status = createRegistry();
        if ( status <= 0 )
            {  // Message box
            JOptionPane.showMessageDialog
                ( null, String.format
                ( "PAL reinitialization failed\n Status = %d", status ),
                About.getShortName(), JOptionPane.ERROR_MESSAGE );
            }
        else
            {  // Message box
            JOptionPane.showMessageDialog
                ( null, String.format
                ( "PAL reinitialized successfully" ),
                About.getShortName(), JOptionPane.WARNING_MESSAGE ); 
            // System tree initializing
            SystemTreeBuilder stb = new SystemTreeBuilder();
            pApps = stb.getApplicationPanels();
            rApps = stb.getApplicationReports();
            mRoot = stb.getApplicationTrees();
            // Store application CPUID for special usage: load text
            appCpuid = stb.getAppCpuid();
            // Select CPUID application panel, restore split panel divider
            split.setRightComponent( pApps[0] );
            split.setDividerLocation( About.getX1size() / 7 + 5 );  // v1.04.00, fix split relocation bug after redetect
            // Unselect tree node
            TreePath tp = tRoot.getPathForRow( 0 );
            tRoot.setSelectionPath( tp );
            }
        }
    }

class HandlerInstLatx64CpuidFile extends AbstractAction
    {
    @Override public void actionPerformed( ActionEvent e )
        {
        if ( appCpuid != null )
            {
            ApplicationView av = appCpuid.getView();
            if ( av != null )
                {
                av.entryBLoadText();
                // Select CPUID application panel, restore split panel divider
                split.setRightComponent( pApps[0] );
                split.setDividerLocation( About.getX1size() / 7 + 5 );
                // Unselect tree node
                TreePath tp = tRoot.getPathForRow( 0 );
                tRoot.setSelectionPath( tp );
                }
            }
        }
    }

class HandlerRemoteDeserialize extends AbstractAction
    {
    @Override public void actionPerformed( ActionEvent e )
        {  // reserved yet
        }
    }

class HandlerFileDeserialize extends AbstractAction
    {
    @Override public void actionPerformed( ActionEvent e )
        {  // reserved yet
        }
    }

class HandlerSerializeRemote extends AbstractAction
    {
    @Override public void actionPerformed( ActionEvent e )
        {  // reserved yet
        }
    }

class HandlerSerializeFile extends AbstractAction
    {
    @Override public void actionPerformed( ActionEvent e )
        {  // reserved yet
        }
    }

class HandlerAbout extends AbstractAction
    {
    @Override public void actionPerformed( ActionEvent e )
        {
        ActionAbout about = new ActionAbout();
        final JDialog dialog = about.createDialog
        ( null , About.getShortName() , About.getVendorName() );
        dialog.setLocationRelativeTo( thisFrame );
        dialog.setVisible( true );    
        }
    }

// Listener for Left Tree, select right component = f(tree selection)
class RootListener implements TreeSelectionListener
    {
    @Override public void valueChanged( TreeSelectionEvent tse )
        {
        if ( ( tse == null )||( pApps == null )||( split == null ) ) return;
        
        Object tree = tse.getSource(); 
        if ( !( tree instanceof JTree ) ) return;
        
        TreePath[] tp = ( (JTree)tree ).getSelectionPaths();
        if ( ( tp == null )||( tp.length==0 ) ) return;
        
        TreePath tp0 = tp[0];
        if ( tp0 == null ) return;
        
        Object dmtn = tp0.getLastPathComponent();
        if ( !( dmtn instanceof DefaultMutableTreeNode ) ) return;
        
        Object le = ( (DefaultMutableTreeNode)dmtn ).getUserObject();
        if ( !( le instanceof ListEntryApplication ) ) return;
        
        int n = (( ListEntryApplication )le).getID();
        if ( ( n < 0 )||( n >= pApps.length ) ) return;
        
        JPanel p = pApps[n];
        if ( p == null ) return;
        
        // Change right component without change split position
        int m = split.getDividerLocation();  // save current divider location
        split.setRightComponent( p );        // change component, can change d.
        split.setDividerLocation( m );       // restore old divider location
        }
    }
}
