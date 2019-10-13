/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
Abstract class: template for Root Menu variants.
*/

package cpuid.applications.rootmenus;

import cpuid.applications.guimodels.ChangeableTableModel;
import cpuid.applications.guimodels.ListEntryApplications;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

abstract public class RootMenu extends JFrame 
{
protected JPanel[] pApps;
protected ChangeableTableModel[] rApps;
protected JSplitPane split;
protected JScrollPane scrollApp;
protected JTree tRoot;
protected DefaultTreeModel mRoot;
    
abstract public void showGUI();

// Listener for Left Tree, select right component = f(tree selection)
protected class RootListener implements TreeSelectionListener
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
        
        // Change right component without change split position
        int m = split.getDividerLocation();  // save current divider location
        split.setRightComponent(p);          // change component, can change d.
        split.setDividerLocation(m);         // restore old divider location
        }
    }


}
