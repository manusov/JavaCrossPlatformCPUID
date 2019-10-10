/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class with static helper method for text table show with
adjust columns width by visualized text strings widths.
*/

package cpuidrefactoring.rootmenu;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

class HelperTable 
{
static void optimizeColumnsWidths( JTable table, int x )
    {
    TableModel tm = table.getModel();
    int n = tm.getColumnCount();
    int m = tm.getRowCount();
    double[] max = new double[n];
    // cycle for find maximum column string length in the table up
    for ( int i=0; i<n; i++ ) { max[i] = tm.getColumnName(i).length(); }
    // cycle for find maximum column string length in the table content part
    for ( int i=0; i<m; i++ )
        {
        for ( int j=0; j<n; j++ )
            {
            int k = ( (String)( tm.getValueAt( i, j ) ) ).length();
            if ( max[j] < k ) { max[j] = k; }
            }
        }
    double scale = 0.0;
    for ( int i=0; i<n; i++ ) { scale = scale + max[i]; }
    scale = x / scale;
    // table for set required preferred width of column
    for ( int i=0; i<n; i++ ) 
        { 
        TableColumn tc = table.getColumnModel().getColumn(i);
        tc.setPreferredWidth( (int)( max[i] * scale ) );
        }
    }
}
