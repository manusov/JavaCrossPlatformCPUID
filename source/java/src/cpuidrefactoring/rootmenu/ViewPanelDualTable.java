/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Set of GUI panels and components: dual table.
*/

package cpuidrefactoring.rootmenu;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class ViewPanelDualTable extends ViewPanel
{
private final JPanel p;                        // complex panel
private final JSplitPane pp;                   // split panel
private final JScrollPane sp1, sp2;            // scroll panels
private final JTable table1, table2;           // tables
private final AbstractTableModel atm1, atm2;   // tables models
private final BoxLayout bl;                    // layout manager
private static DefaultTableCellRenderer 
        mRenderer1, mRenderer2;                // this for centering

public ViewPanelDualTable
    ( int x, int y, AbstractTableModel z1, AbstractTableModel z2 )
    {
    atm1 = z1; atm2 = z2;
    // Built panel components
    table1 = new JTable( atm1 );
    table2 = new JTable( atm2 );
    HelperTable.optimizeColumnsWidths( table1, x-35 );
    HelperTable.optimizeColumnsWidths( table2, x-35 );
    sp1 = new JScrollPane( table1 );
    sp2 = new JScrollPane( table2 );
    // Centering table(s)
    mRenderer1 = new DefaultTableCellRenderer();
    mRenderer2 = new DefaultTableCellRenderer();
    mRenderer1.setHorizontalAlignment( SwingConstants.CENTER );
    mRenderer2.setHorizontalAlignment( SwingConstants.CENTER );
    for (int i=0; i<table1.getColumnCount(); i++)
        { table1.getColumnModel().getColumn(i).setCellRenderer( mRenderer1 ); }
    for (int i=0; i<table2.getColumnCount(); i++)
        { table2.getColumnModel().getColumn(i).setCellRenderer( mRenderer2 ); }
    // Built split panel
    pp = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true );
    pp.setOneTouchExpandable( true );
    pp.setDividerSize( 8 );
    pp.setDividerLocation( y/2 );
    pp.setTopComponent( sp1 );
    pp.setBottomComponent( sp2 );
    // Built panel and set layout
    p = new JPanel();
    bl = new BoxLayout( p, BoxLayout.X_AXIS );
    p.setLayout( bl );
    p.add( pp );
    }

// Return panel
@Override public JPanel getP()
    { 
    return p; 
    }

// Return components array
@Override public JComponent[] getComponents()
    {
    return new JComponent[] { table1, table2 };
    }
}
