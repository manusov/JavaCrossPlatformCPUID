/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class provides OS environment array and converts OS environment array to
array of viewable text strings and AbstractTableModel.
Argument is OS environment array. Result is array of text strings and
AbstractTableModel consumed by GUI and text reports.

*/

package cpuidv3.sal;

import static cpuidv3.sal.HelperString.helperSeparate;
import static cpuidv3.sal.HelperTableToReport.tableReport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class ServiceOsInfo extends Service
{
    ServiceOsInfo( SAL s ) { super( s ); }
    
    @Override String getTableName() 
        { return "Operating System information."; }

    @Override String[] getTableUp()
        { return new String[]{ "Parameter", "Value" }; }
    
    private final static int LINE_MAX = 100;
    private final static String PATH_SEPARATOR = ";";
    private final static String PATH_TERMINATOR = ".";

    @Override String[][] getTableData() 
    {
        Map < String, String > map = System.getenv();
        Set set = map.keySet();
        int count = set.size();
        Iterator it = set.iterator();
        ArrayList<String[]> tableList = new ArrayList<>();
        
        // Cycle for OS properties list elements.
        for(int i=0; i<count; i++)
        {
            String[] line = new String[2];
            String keyString = (String)it.next();
            String valueString = (String)map.get(keyString);
            String[] valueStrings = helperSeparate
                    ( valueString, PATH_SEPARATOR, PATH_TERMINATOR, LINE_MAX );
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
