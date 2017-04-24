//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Model for tables visualization as GUI and tables save as REPORT.

package arch1.applications.guimodels;

import javax.swing.table.AbstractTableModel;

public class ChangeableTableModel extends AbstractTableModel 
{
private static final String[] DEFAULT_NAMES =
    { "Parameter", "Value", "Comments" };
private static final String EMPTY_VALUE   = " ";
private static final String DEFAULT_VALUE = "No data";
private static final int DEFAULT_ROWS = 15;
private String[][] dataValues = null;

public ChangeableTableModel()
    {
    int n = DEFAULT_ROWS;
    int m = DEFAULT_NAMES.length;
    dataValues = new String[n][m];
    System.arraycopy ( DEFAULT_NAMES, 0, dataValues[0], 0, m );
    dataValues[1][0] = DEFAULT_VALUE;
    }

public ChangeableTableModel( String[] sa1, String[][] sa2 )
    {
    this();
    int n = sa1.length;
    int m = sa2.length + 1;
    int k = sa2[0].length;
    if (n==k)
        {
        dataValues = new String[m][n];
        for( int i=0; i<n; i++ )
            {
            dataValues[0][i] = sa1[i];
            }
        for( int i=1; i<m; i++ )
            {
            for( int j=0; j<n; j++ )
                {
                dataValues[i][j] = sa2[i-1][j];
                }
            }
        
        }
    }

@Override public int getRowCount() 
    {
    return dataValues.length - 1; 
    }

@Override public int getColumnCount() 
    {
    return dataValues[0].length; 
    }

@Override public String getColumnName(int column) 
    {
    return " " + dataValues[0][column]; 
    }

@Override public Class getColumnClass(int column) 
    {
    return String.class; 
    }

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
        
@Override public boolean isCellEditable(int row, int column) { return false; }

@Override public void setValueAt(Object value, int row, int column) 
    {
    if ( row >= dataValues.length ) return;
    if ( column >= dataValues[0].length ) return;
    if ( dataValues == null ) return;
    if ( dataValues[row] == null ) return;
    dataValues[row][column] = (String)value;
    }

public void setDataValues( String[][] s )
    {
    dataValues = s;
    }

}
