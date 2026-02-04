/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class with static helper methods for text table show with
adjust columns width by visualized text strings widths.

*/

package cpuidv3.gui;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class HelperTable 
{
    public static void optimizeSingleTable
        ( JPanel panel, JTable table, int xSize, boolean centeringOption )
    {
        // Built panel components
        optimizeColumnsWidths( table, xSize );
        JScrollPane scrollPane = new JScrollPane( table );
        // Centering table(s)
        if( centeringOption )
        {
            DefaultTableCellRenderer mRenderer = 
                new DefaultTableCellRenderer();
            mRenderer.setHorizontalAlignment( SwingConstants.CENTER );
            for ( int i=0; i<table.getColumnCount(); i++ )
                { table.getColumnModel().getColumn(i).
                    setCellRenderer( mRenderer ); }
        }
        // Build panel and set layout.
        BoxLayout layout = new BoxLayout( panel, BoxLayout.X_AXIS );
        panel.setLayout( layout );
        panel.add( scrollPane );
    }

    public static void optimizeColumnsWidths( JTable table, int xSize )
    {
        TableModel tm = table.getModel();
        int n = tm.getColumnCount();
        int m = tm.getRowCount();
        double[] max = new double[n];
        // cycle for find maximum column string length in the table up.
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
        scale = xSize / scale;
        // table for set required preferred width of column
        for ( int i=0; i<n; i++ ) 
        { 
            TableColumn tc = table.getColumnModel().getColumn(i);
            tc.setPreferredWidth( (int)( max[i] * scale ) );
        }
    }
}
