/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
Static helper library: optimize tables columns widths.
*/

package cpuid.applications.guipanels;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class SetupTableSize 
{
public static void optimizeColumnsWidths( JTable table, int x )
    {
    TableModel tm = table.getModel();
    int n = tm.getColumnCount();
    int m = tm.getRowCount();
    int k = 0;
    double[] max = new double[n];
    // cycle for find maximum column string length in the table up
    for ( int i=0; i<n; i++ ) { max[i] = tm.getColumnName(i).length(); }
    // cycle for find maximum column string length in the table content part
    for ( int i=0; i<m; i++ )
        {
        for ( int j=0; j<n; j++ )
            {
            k = ((String)(tm.getValueAt(i,j))).length();
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
