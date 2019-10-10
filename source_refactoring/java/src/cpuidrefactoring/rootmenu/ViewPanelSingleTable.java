/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Set of GUI panels and components: single table.
*/

package cpuidrefactoring.rootmenu;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class ViewPanelSingleTable extends ViewPanel
{
private final JPanel p;                   // complex panel
private final JScrollPane sp1;            // scroll panel
private final JTable table;               // table
private final AbstractTableModel atm;     // tables models
private final BoxLayout bl;               // layout manager
private static DefaultTableCellRenderer mRenderer;  // this for centering

public ViewPanelSingleTable
    ( int x, int y, AbstractTableModel z1, boolean centeringOption )
    {
    atm = z1;
    // Built panel components
    table = new JTable( atm );
    HelperTable.optimizeColumnsWidths( table, x );
    sp1 = new JScrollPane( table );
    // Centering table(s)
    if(centeringOption)
        {
        mRenderer = new DefaultTableCellRenderer();
        mRenderer.setHorizontalAlignment( SwingConstants.CENTER );
        for ( int i=0; i<table.getColumnCount(); i++ )
            { table.getColumnModel().getColumn(i).
                    setCellRenderer( mRenderer ); }
        }
    // Built panel and set layout
    p = new JPanel();
    bl = new BoxLayout( p, BoxLayout.X_AXIS );
    p.setLayout( bl );
    p.add( sp1 );
    }

// Return panel
@Override public JPanel getP()
    { 
    return p; 
    }

// Return components array
@Override public JComponent[] getComponents()
    {
    return new JComponent[] { table };
    }
}
