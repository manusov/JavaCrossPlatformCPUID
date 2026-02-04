/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Table model for visualization by GUI and generating text reports.

*/

package cpuidv3.sal;

import javax.swing.table.AbstractTableModel;

public class ChangeableTableModel extends AbstractTableModel 
{
    private final static String[] DEFAULT_NAMES =
        { "Parameter", "Value", "Comments" };
    private final static String EMPTY_VALUE   = " ";
    private final static String DEFAULT_VALUE = "No data";
    private final static int DEFAULT_ROWS = 15;
    private String[][] data = null;

    // Constructor without parameters creates default model.
    public ChangeableTableModel()
    {
        data = new String[ DEFAULT_ROWS ][ DEFAULT_NAMES.length ];
        System.arraycopy ( DEFAULT_NAMES, 0, data[0], 0, DEFAULT_NAMES.length );
        data[1][0] = DEFAULT_VALUE;
    }

    // Constructor with up-string (sa1) and content text array (sa2).
    public ChangeableTableModel( String[] sa1, String[][] sa2 )
    {
        this();
        int n = sa1.length;
        int m = sa2.length + 1;
        int k = sa2[0].length;
        if ( n == k )
        {
            data = new String[m][n];
            System.arraycopy( sa1, 0, data[0], 0, n );
            for( int i=1; i<m; i++ )
            {
                System.arraycopy( sa2[i-1], 0, data[i], 0, n );
            }
        }
    }

    // Get number of rows.
    @Override public int getRowCount() { return data.length - 1; }

    // Get number of columns.
    @Override public int getColumnCount() { return data[0].length; }

    // Get name string for selected column.
    @Override public String getColumnName( int column )
        { return " " + data[0][column]; }

    // Get model objects class = String.
    @Override public Class getColumnClass( int column ) { return String.class; }

    // Get text string from selected table position: row, column.
    @Override public Object getValueAt( int row, int column )
    {
        row++;
        if( ( data != null )&&( row < data.length )&&
            ( column < data[row].length )&&( data[row] != null) )
        {
            String s = data[row][column];
            if( s != null )
            {
                return " " + s;
            }
            else
            {
                return EMPTY_VALUE;
            }
        }
        else
        {
            return EMPTY_VALUE;
        }
    }

    // Return editable possibility = set NO EDITABLE.
    @Override public boolean isCellEditable( int row, int column )
        { return false; }

    // Set text string at selected table position: row, column.
    @Override public void setValueAt(Object value, int row, int column) 
    {
        if( ( data != null )&&( row < data.length )&&
            ( column < data[row].length )&&( data[row] != null ) )
        {
            data[row][column] = ( String )value;
        }
    }

    // Specific (non inherited) method: update text array (pointer).
    public void setDataValues( String[][] s ) { data = s; }

}
