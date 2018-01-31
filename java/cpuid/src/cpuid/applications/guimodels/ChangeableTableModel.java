/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
Model for tables visualization as GUI and tables save as REPORT.
*/

package cpuid.applications.guimodels;

import javax.swing.table.AbstractTableModel;

public class ChangeableTableModel extends AbstractTableModel 
{
private static final String[] DEFAULT_NAMES =
    { "Parameter", "Value", "Comments" };
private static final String EMPTY_VALUE   = " ";
private static final String DEFAULT_VALUE = "No data";
private static final int DEFAULT_ROWS = 15;
private String[][] dataValues = null;

// constructor without parameters creates default model
public ChangeableTableModel()
    {
    int n = DEFAULT_ROWS;
    int m = DEFAULT_NAMES.length;
    dataValues = new String[n][m];
    System.arraycopy ( DEFAULT_NAMES, 0, dataValues[0], 0, m );
    dataValues[1][0] = DEFAULT_VALUE;
    }

// constructor with up-string (sa1) and content text array (sa2)
public ChangeableTableModel( String[] sa1, String[][] sa2 )
    {
    this();
    int n = sa1.length;
    int m = sa2.length + 1;
    int k = sa2[0].length;
    if (n==k)
        {
        dataValues = new String[m][n];
        System.arraycopy( sa1, 0, dataValues[0], 0, n );
        for( int i=1; i<m; i++ )
            {
            System.arraycopy( sa2[i-1], 0, dataValues[i], 0, n );
            }
        }
    }

// get number of rows
@Override public int getRowCount() 
    { return dataValues.length - 1; }

// get number of columns
@Override public int getColumnCount() 
    { return dataValues[0].length; }

// get name string for selected column
@Override public String getColumnName(int column) 
    { return " " + dataValues[0][column];  }

// get model objects class = String
@Override public Class getColumnClass(int column) 
    { return String.class; }

// get text string from selected table position: row, column
@Override public Object getValueAt(int row, int column)
    {
    String s1 = EMPTY_VALUE;
    row++;
    if ( row >= dataValues.length ) return s1;
    if ( column >= dataValues[0].length ) return s1;
    if ( dataValues == null ) return s1;
    if ( dataValues[row] == null ) return s1;
    String s2 = dataValues[row][column];
    if ( s2 == null ) return s1;
    return " " + s2;
    }

// return editable possibility = set NO EDITABLE
@Override public boolean isCellEditable(int row, int column) { return false; }

// set text string at selected table position: row, column
@Override public void setValueAt(Object value, int row, int column) 
    {
    if ( row >= dataValues.length ) return;
    if ( column >= dataValues[0].length ) return;
    if ( dataValues == null ) return;
    if ( dataValues[row] == null ) return;
    dataValues[row][column] = (String)value;
    }

// specific method: update text array (pointer)
public void setDataValues( String[][] s )
    { dataValues = s; }

}
