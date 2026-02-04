/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class provides JVM properties array and converts JVM properties array to
array of viewable text strings and AbstractTableModel. 
Argument is JVM properties array. Result is array of text strings and
AbstractTableModel consumed by GUI table and text reports.

*/

package cpuidv3.sal;

import static cpuidv3.sal.HelperString.helperSeparate;
import static cpuidv3.sal.HelperTableToReport.tableReport;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

class ServiceJvmInfo extends Service
{
    ServiceJvmInfo( SAL s ) { super( s ); }
    
    @Override String getTableName()
        { return "Java Virtual Machine information."; }
    
    @Override String[] getTableUp()
        { return new String[]{ "Parameter", "Value" }; }
    
    private final static int LINE_MAX = 100;
    private final static String PATH_TERMINATOR = ".";
    
@Override String[][] getTableData() 
{ 
        Properties p = System.getProperties();
        int count = p.size();
        Enumeration keys = p.keys();
        ArrayList<String[]> tableList = new ArrayList<>();
        String pathSeparator = (String)p.get( "path.separator" );
        if( pathSeparator == null )
        {
            pathSeparator = ";";
        }
        
        // Cycle for JVM properties list elements.
        for(int i=0; i<count; i++)
        {
            String[] line = new String[2];
            if ( keys.hasMoreElements() )
            {
                String keyString = (String)keys.nextElement();
                String valueString = (String)p.get(keyString);
                if ( keyString.equals( "line.separator" ) )
                {  // Special support for correct print separator char(s).
                    int valueLength = valueString.length();
                    StringBuilder sb = new StringBuilder ( "" );
                    for ( int j=0; j<valueLength; j++ )
                    {
                        sb.append( (int)( valueString.charAt(j) ) );
                        if ( j < valueLength - 1 ) { sb.append( ", " ); }
                    }
                    valueString = sb.toString();
                }
                String[] valueStrings = helperSeparate
                    ( valueString, pathSeparator, PATH_TERMINATOR, LINE_MAX );
                if( valueStrings.length == 1 )
                {
                    line[0] = keyString;
                    line[1] = valueString;
                    tableList.add( line );
                }
                else
                {
                    line[0] = keyString;
                    line[1] = valueStrings[0];
                    tableList.add( line );
                    for( int j=1; j<valueStrings.length; j++ )
                    {
                        String[] subLine = new String[2];
                        subLine[0] = "-//-";
                        subLine[1] = valueStrings[j];
                        tableList.add( subLine );
                    }
                }
            }
            else    // Unexpected ( because i<n ) absence of property.
            {
                line[0] = "?";
                line[1] = "?";
                tableList.add( line );
            }
        }
        
        return tableList.isEmpty() ? 
            new String[][] { { "No data", "No data" } } :
            tableList.toArray( new String[tableList.size()][] );
    }
    
    @Override void printSummaryReport()
    {
        String s = getTableName();
        System.out.println( "[ " + s + "]\r\n" );
        s = tableReport
            ( new ChangeableTableModel( getTableUp(), getTableData() ) );
        System.out.println( s );
    }
}
