/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for convert JVM properties array to array of viewable text strings.
Argument is JVM properties array.
Result is array of text strings consumed by GUI and text reports.
*/

package cpuidrefactoring.devicejvminfo;

import cpuidrefactoring.CpuidRefactoring;
import cpuidrefactoring.system.Device;
import cpuidrefactoring.system.Registry;
import java.util.Enumeration;
import java.util.Properties;

public class DeviceJvmInfo extends Device
{
@Override public String[][][] getScreensLists()  // get result as text
    {
    Registry r = CpuidRefactoring.getRegistry();      // get system registry
    Properties p = r.getJvmProperties();      // access target info by registry
    int n = p.size();                         // n = length of properties list
    String[][][] text = new String[1][n][2];  // create array for text table
    Enumeration keys = p.keys();              // enumeration for properties
    // Cycle for properties list elements
    for ( int i=0; i<n; i++ )
        {
        if ( keys.hasMoreElements() )
            {
            text[0][i][0] = (String)keys.nextElement();
            if ( text[0][i][0].equals( "line.separator" ) )
                {  // special support for correct print separator char(s)
                text[0][i][1] = (String)p.get( text[0][i][0] );
                int n1 = text[0][i][1].length();
                StringBuilder sb = new StringBuilder ( "" );
                for ( int j=0; j<n1; j++ )
                    {
                    sb.append( (int)( text[0][i][1].charAt(j) ) );
                    if ( j < n1-1 ) { sb.append( ", " ); }
                    }
                text[0][i][1] = sb.toString();
                }
            else
                {  // this used if not a separator char(s)
                text[0][i][1] = (String)p.get( text[0][i][0] );
                }
            }
        else    // Unexpected ( because i<n ) absence of property
            {
            text[0][i][0] = "?";
            text[0][i][1] = "?";
            }
        // limit too long left string
        if ( text[0][i][0].length() > 40 )
            {
            text[0][i][0] = text[0][i][0].substring( 0, 39 ) + "...";
            }
        // limit too long right string
        if ( text[0][i][1].length() > 100 )
            {
            text[0][i][1] = text[0][i][1].substring( 0, 99 ) + "...";
            }
        }
    return text;    
    }
}
