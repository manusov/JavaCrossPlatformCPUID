//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Root menu, variant #2, full-functional with left tree menu.

package arch1.applications.rootmenus;

import arch1.About;
// import arch1.applications.guimodels.ListEntryApplications;
// import arch1.applications.mvc.BCA;
// import java.util.ArrayList;
// import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
// import javax.swing.tree.DefaultMutableTreeNode;
// import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
// import javax.swing.event.TreeSelectionListener;
// import javax.swing.event.TreeSelectionEvent;
// import javax.swing.tree.TreePath;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class RootMenu2 extends RootMenu   // Note RootMenu extends JFrame 
{
/*
// Replace to strings array at evolution
private static final String PLATFORM = "Platform";
private static final String CPU      = "CPU";
private static final String JVM      = "JVM";
private static final String CPUID    = "CPUID";
private static final String CPUCLK   = "CPUCLK";
private static final String JVMINFO  = "JVM info";
private static final String JVMENV   = "Environment";
*/

/*    
private final JPanel[] pApps;
private final JSplitPane split;
private final JScrollPane scrollApp;
private final JTree tRoot;
private final DefaultTreeModel mRoot;
*/
    
public RootMenu2()
    {

/*    
    //--- Root node: PLATFORM ---
    ListEntryApplications le1 = new ListEntryApplications(PLATFORM, false, -1);
    DefaultMutableTreeNode dmtn1 = new DefaultMutableTreeNode(le1, true);
    // ArrayList<DefaultMutableTreeNode> al1 = new ArrayList();
    // al1.add(dmtn1);
    
    //--- Child node: PLATFORM\CPU ---
    ListEntryApplications le2 = new ListEntryApplications(CPU, false, 4);  // OLD=-1
    DefaultMutableTreeNode dmtn2 = new DefaultMutableTreeNode(le2, true);
    dmtn1.add(dmtn2);
    
    //--- Child node: PLATFORM\JVMINFO ---
    ListEntryApplications le5 = new ListEntryApplications(JVM, false, 5);  // OLD=-1
    DefaultMutableTreeNode dmtn5 = new DefaultMutableTreeNode(le5, true);
    dmtn1.add(dmtn5);
    
    //--- Child leaf 1: PLATFORM\CPU\CPUID ---
    ListEntryApplications le3 = new ListEntryApplications(CPUID, true, 0);
    DefaultMutableTreeNode dmtn3 = new DefaultMutableTreeNode(le3, false);
    dmtn2.add(dmtn3);
    
    //--- Child leaf 2: PLATFORM\CPU\CPUCLK ---
    ListEntryApplications le4 = new ListEntryApplications(CPUCLK, true, 1);
    DefaultMutableTreeNode dmtn4 = new DefaultMutableTreeNode(le4, false);
    dmtn2.add(dmtn4);
    
    //--- Child leaf 3: PLATFORM\JVM\JVMINFO ---
    ListEntryApplications le6 = new ListEntryApplications(JVMINFO, true, 2);
    DefaultMutableTreeNode dmtn6 = new DefaultMutableTreeNode(le6, false);
    dmtn5.add(dmtn6);

    //--- Child leaf 4: PLATFORM\JVM\ENVIRONMENT ---
    ListEntryApplications le7 = new ListEntryApplications(JVMENV, true, 3);
    DefaultMutableTreeNode dmtn7 = new DefaultMutableTreeNode(le7, false);
    dmtn5.add(dmtn7);
    
    //--- Model=f(List) ---
    // mRoot = new DefaultTreeModel( al1.get(0) , true );
    mRoot = new DefaultTreeModel( dmtn1 , true );
*/

    SystemTreeBuilder stb = new SystemTreeBuilder();
    pApps = stb.getApps();
    mRoot = stb.getTree();
        
    //--- Tree=f(Model) ---
    tRoot = new JTree(mRoot);
    tRoot.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);
    tRoot.addTreeSelectionListener(new RootListener());
    scrollApp = new JScrollPane(tRoot);
    
    //--- Built split panel ---
    split = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true );
    split.setOneTouchExpandable(true);
    split.setDividerSize(8);
    split.setDividerLocation( About.getX1size() / 7 + 5 );
    split.setLeftComponent(scrollApp);
    split.setRightComponent( pApps[0] );
    }

/*
//--- Listener for Left Tree, select right component = f(tree selection) ---
private class RootListener implements TreeSelectionListener
    {
    @Override public void valueChanged(TreeSelectionEvent tse)
        {
        if ( (tse==null)||(pApps==null)||(split==null) ) return;
        
        Object tree = tse.getSource(); 
        if ( !(tree instanceof JTree ) ) return;
        
        TreePath[] tp = ((JTree)tree).getSelectionPaths();
        if ( (tp==null)||(tp.length==0) ) return;
        
        TreePath tp0 = tp[0];
        if (tp0==null) return;
        
        Object dmtn = tp0.getLastPathComponent();
        if ( !(dmtn instanceof DefaultMutableTreeNode) ) return;
        
        Object le = ((DefaultMutableTreeNode)dmtn).getUserObject();
        if ( !(le instanceof ListEntryApplications) ) return;
        
        int n = ((ListEntryApplications)le).getID();
        if ( (n<0)||(n>=pApps.length) ) return;
        
        JPanel p = pApps[n];
        if (p==null) return;
        
        //--- Change right component without change split position ---
        int m = split.getDividerLocation();  // save current divider location
        split.setRightComponent(p);          // change component, can change d.
        split.setDividerLocation(m);         // restore old divider location
        }
    }
*/

//--- Show panel of root menu --- 
@Override public void showGUI()
    {
    //--- Built panel, set mode, size, strings, enable visual ---
    add(split);
    setDefaultCloseOperation( EXIT_ON_CLOSE );
    setSize( About.getX1size(), About.getY1size() );
    setTitle( About.getLongName() );
    setLocationByPlatform(true);
    setVisible(true);
    }

}
