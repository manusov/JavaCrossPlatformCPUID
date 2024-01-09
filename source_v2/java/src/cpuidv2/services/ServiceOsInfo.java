/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2024.
-------------------------------------------------------------------------------
Class for get OS environment array and convert OS environment array to
array of viewable text strings. Argument is OS environment array.
Result is array of text strings consumed by GUI and text reports.
*/

package cpuidv2.services;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ServiceOsInfo 
{
public String[][] getOsInfo()
    {
    Map < String, String > map = System.getenv();
    Set set = map.keySet();                   // Get keys.
    int n = set.size();                       // n = length.
    Iterator it = set.iterator();             // Iterator for access array.
    String[][] text = new String[n][2];  // Primary model: String[][].
    // Cycle for Environment strings list.
    for ( int i=0; i<n; i++ )
        {
        text[i][0] = ( String )it.next();
        text[i][1] = ( String )map.get( text[i][0] );
        // Limit too long left string.
        if ( text[i][0].length() > 40 )
            {
            text[i][0] = text[i][0].substring( 0, 39 ) + "...";
            }
        // Limit too long right string.
        if ( text[i][1].length() > 100 )
            {
            text[i][1] = text[i][1].substring( 0, 99 ) + "...";
            }
        }
    return text;
    }
}
