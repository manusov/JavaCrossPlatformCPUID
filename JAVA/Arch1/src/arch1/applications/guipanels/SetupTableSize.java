//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Static library: optimize tables columns widths.

package arch1.applications.guipanels;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class SetupTableSize 
{

// private final static int TAB_SIZE_CONSTANT = 0;

public static void optimizeColumnsWidths( JTable table, int x )
    {
    TableModel tm = table.getModel();
    int n = tm.getColumnCount();
    int m = tm.getRowCount();
    int k = 0;
    double[] max = new double[n];
    for ( int i=0; i<n; i++ ) { max[i] = tm.getColumnName(i).length(); }
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
    
        for ( int i=0; i<n; i++ ) 
            { 
            TableColumn tc = table.getColumnModel().getColumn(i);
            tc.setPreferredWidth( (int)( max[i] * scale ) );
            }
        // Removed at v0.44, 
        // otherwise tables sizes is non-optimal after GUI resizing
        // table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }
    
}
