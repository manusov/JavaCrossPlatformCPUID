/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
Driver: Get Java Virtual Machine (JVM) environment strings.
*/

package cpuid.drivers.jvmenvironment;

import cpuid.CpuId;
import cpuid.drivers.cpr.DeviceAdapter;
import cpuid.kernel.Registry;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DeviceJVMenvironment extends DeviceAdapter
{

@Override public int getCommandsCount()  // get number of supported commands
    {
    return 1;
    }

@Override public String[][] getSummaryText()  // get result as text
    {
    Registry r = CpuId.getRegistry();
    Map < String, String > map = r.getJvmEnvironment();  // System.getenv();
    Set set = map.keySet();                 // get keys
    int n = set.size();                     // n = length
    Iterator it = set.iterator();           // iterator for access array
    String[][] text = new String[n][2];     // Primary model: String[][]
    // Cycle for Environment strings list
    for ( int i=0; i<n; i++ )
        {
        text[i][0] = (String)it.next();
        text[i][1] = (String)map.get( text[i][0] );
        // limit too long left string
        if (text[i][0].length() > 40)
            {
            text[i][0] = text[i][0].substring(0,39) + "...";
            }
        // limit too long right string
        if (text[i][1].length() > 100)
            {
            text[i][1] = text[i][1].substring(0,99) + "...";
            }
        }
    return text;
    }
    
}
