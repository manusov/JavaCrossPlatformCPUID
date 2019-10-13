/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for convert OS environment array to array of viewable text strings.
Argument is OS environment array.
Result is array of text strings consumed by GUI and text reports.
*/

package cpuidrefactoring.deviceosinfo;

import cpuidrefactoring.CpuidRefactoring;
import cpuidrefactoring.system.Device;
import cpuidrefactoring.system.Registry;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DeviceOsInfo extends Device
{
@Override public String[][][] getScreensLists()  // get result as text
    {
    Registry r = CpuidRefactoring.getRegistry();
    Map < String, String > map = r.getJvmEnvironment();  // System.getenv();
    Set set = map.keySet();                   // get keys
    int n = set.size();                       // n = length
    Iterator it = set.iterator();             // iterator for access array
    String[][][] text = new String[1][n][2];  // Primary model: String[][]
    // Cycle for Environment strings list
    for ( int i=0; i<n; i++ )
        {
        text[0][i][0] = ( String )it.next();
        text[0][i][1] = ( String )map.get( text[0][i][0] );
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
