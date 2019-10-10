/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
Viewer model 1, one tree and one table.
*/

package cpuid.applications.guipanels;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;

public class Viewer1 extends ViewPanel 
{
private final JPanel p;                   // complex panel
private final JSplitPane pp;              // split panel
private final JScrollPane sp1, sp2;       // scroll panels
private final JTree tree;                 // trees
private final JTable table;               // tables
private final DefaultTreeModel dtm;       // trees models
private final AbstractTableModel atm;     // tables models
private final BoxLayout bl;               // layout manager
private static DefaultTableCellRenderer mRenderer;  // this for centering

public Viewer1
    ( int x, int y, DefaultTreeModel z1, AbstractTableModel z2 )
    {
    dtm=z1; atm=z2;
    // Built panel components
    tree = new JTree(dtm);
    tree.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);
    table = new JTable(atm);
    SetupTableSize.optimizeColumnsWidths( table, x/2 );
    sp1 = new JScrollPane(tree);
    sp2 = new JScrollPane(table);
    // Centering table(s)
    mRenderer = new DefaultTableCellRenderer();
    mRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    for (int i=0; i<table.getColumnCount(); i++)
        { table.getColumnModel().getColumn(i).setCellRenderer(mRenderer); }
    // Built split panel
    pp = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true );
    pp.setOneTouchExpandable(true);
    pp.setDividerSize(8);
    pp.setDividerLocation(x/2-27);
    pp.setLeftComponent(sp1);
    pp.setRightComponent(sp2);
    // Built panel and set layout
    p = new JPanel();
    bl = new BoxLayout(p, BoxLayout.X_AXIS);
    p.setLayout(bl);
    p.add(pp);
    }

// Return panel    
@Override public JPanel getP()
    { return p; }

// Return components array
@Override public JComponent[] getComponents()
    {
    JComponent[] x = new JComponent[2];
    x[0] = tree;
    x[1] = table;
    return x;    
    }

}
