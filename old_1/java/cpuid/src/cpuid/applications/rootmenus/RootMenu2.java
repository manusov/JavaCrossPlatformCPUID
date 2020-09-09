/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
Root menu, variant #2, full-functional with left tree menu.
This root menu variant unused at CPUID v0.52. Debug purpose.
*/

package cpuid.applications.rootmenus;

import cpuid.About;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class RootMenu2 extends RootMenu   // Note RootMenu extends JFrame 
{
public RootMenu2()
    {
    SystemTreeBuilder stb = new SystemTreeBuilder();
    pApps = stb.getApps();
    mRoot = stb.getTree();
    // Tree=f(Model)
    tRoot = new JTree(mRoot);
    tRoot.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);
    tRoot.addTreeSelectionListener(new RootListener());
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
@Override public void showGUI()
    {
    // Built panel, set mode, size, strings, enable visual
    add(split);
    setDefaultCloseOperation( EXIT_ON_CLOSE );
    setSize( About.getX1size(), About.getY1size() );
    setTitle( About.getLongName() );
    setLocationByPlatform(true);
    setVisible(true);
    }

}
