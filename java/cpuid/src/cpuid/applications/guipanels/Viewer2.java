/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
Viewer model 2, one tree.
*/

package cpuid.applications.guipanels;

import cpuid.applications.guimodels.ChangeableTableModel;
import cpuid.applications.guimodels.ListEntryTables;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.*;
import javax.swing.tree.*;

public class Viewer2 extends ViewPanel 
{
private final JPanel p;                           // complex panel
private final JSplitPane pp1, pp2;                // split panels
private final JScrollPane sp1, sp2, sp3;          // scroll panels
private final JTree tree;                         // trees
private final JTable table1, table2;              // tables
private final DefaultTreeModel dtm;               // trees models
private final AbstractTableModel defaultatm;      // default for no selections
private final BoxLayout b;                        // layout managers
private final DefaultTableCellRenderer
        mRenderer1, mRenderer2;                   // this for centering
private final int startX, startY;

private int xTable()
    {
    int x1 = startX * 2 / 3 - 27;
    if ((pp2==null)|(p==null)) { return x1; }
    double a = p.getWidth();  // xsize    
    double b = pp2.getDividerLocation();
    if (b==0) { b=1; }
    double c = a/b;
    double d = a - a/c - 27.0;
    return (int)d;
    }

private void centeringTables()
    {
    for (int i=0; i<table1.getColumnCount(); i++)
        { table1.getColumnModel().getColumn(i).setCellRenderer(mRenderer1); }
    for (int i=0; i<table2.getColumnCount(); i++)
        { table2.getColumnModel().getColumn(i).setCellRenderer(mRenderer2); }
    }

public Viewer2
    ( int x, int y, DefaultTreeModel z1 )
    {
    dtm=z1; // atm1=z2; atm2=z3;
    startX = x;
    startY = y;
    // Built panel components
    tree = new JTree(dtm);
    tree.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);
    defaultatm = new ChangeableTableModel();
    table1 = new JTable(defaultatm);
    table2 = new JTable(defaultatm);
    sp1 = new JScrollPane(table1);
    sp2 = new JScrollPane(table2);
    // Centering table(s)
    mRenderer1 = new DefaultTableCellRenderer();
    mRenderer2 = new DefaultTableCellRenderer();
    mRenderer1.setHorizontalAlignment(SwingConstants.CENTER);
    mRenderer2.setHorizontalAlignment(SwingConstants.CENTER);
    centeringTables();
    // Built vertical split panel
    pp1 = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true );
    pp1.setOneTouchExpandable(true);
    pp1.setDividerSize(11);
    pp1.setDividerLocation( startY/2-40 );
    pp1.setTopComponent(sp1);
    pp1.setBottomComponent(sp2);
    // Built horizontal split panel
    pp2 = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true );
    sp3 = new JScrollPane(tree);
    pp2.setOneTouchExpandable(true);
    pp2.setDividerSize(8);
    pp2.setDividerLocation( startX/3 );
    pp2.setLeftComponent(sp3);
    pp2.setRightComponent(pp1);
    // Optimize table widths
    SetupTableSize.optimizeColumnsWidths( table1, xTable() );
    SetupTableSize.optimizeColumnsWidths( table2, xTable() );
    // Add selection listener
    tree.addTreeSelectionListener( new SelectionL() );
    // Built panel and set layout
    p = new JPanel();
    b = new BoxLayout(p, BoxLayout.X_AXIS);
    p.setLayout(b);
    p.add(pp2);
    }

// Return panel
@Override public JPanel getP()
    { return p; }

// Return components array
@Override public JComponent[] getComponents()
    {
    JComponent[] x = new JComponent[3];
    x[0] = tree;
    return x;    
    }

// Selection listener for tree

private class SelectionL implements TreeSelectionListener
    {
    @Override public void valueChanged( TreeSelectionEvent e )
        {
        Object t1 = (JTree)e.getSource();
        if (!(t1 instanceof JTree )) return;
        Object r1 = ((JTree)t1).getSelectionPath();
        if (!(r1 instanceof TreePath)) return;
        Object o1 = ((TreePath)r1).getLastPathComponent();
        if (o1 instanceof DefaultMutableTreeNode )
            {
            Object o2 = ( (DefaultMutableTreeNode)(o1) ).getUserObject();
            if (o2 instanceof ListEntryTables )
                {
                table1.setModel( ((ListEntryTables)o2).getAtm1() );
                table2.setModel( ((ListEntryTables)o2).getAtm2() );
                SetupTableSize.optimizeColumnsWidths( table1, xTable() );
                SetupTableSize.optimizeColumnsWidths( table2, xTable() );
                centeringTables();
                
                // REQUIRED TABLE REPAINT ?
                }
            }
        }
    }

}
