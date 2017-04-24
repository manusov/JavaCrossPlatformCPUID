//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Driver: Get Java Virtual Machine (JVM) environment strings.

package arch1.drivers.jvmenvironment;

import arch1.Arch1;
import arch1.drivers.cpr.DeviceAdapter;
import arch1.kernel.Registry;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DeviceJVMenvironment extends DeviceAdapter
{

@Override public int getCommandsCount()
    {
    return 1;
    }

@Override public String[][] getSummaryText()
    {
//  Map < String, String > map = System.getenv();
//
    Registry r = Arch1.getRegistry();
    Map < String, String > map = r.getJvmEnvironment();
//    
    Set set = map.keySet();                        // get keys
    int n = set.size();                            // n = length
    Iterator it = set.iterator();                  // iterator for access array
    String[][] text = new String[n][2];   // Primary model: String[][]
    for ( int i=0; i<n; i++ )
        {
        text[i][0] = (String)it.next();
        text[i][1] = (String)map.get( text[i][0] );
        
        if (text[i][0].length() > 40)
            {
            text[i][0] = text[i][0].substring(0,39) + "...";
            }

        if (text[i][1].length() > 100)
            {
            text[i][1] = text[i][1].substring(0,99) + "...";
            }
        }
    return text;
    }
    
}
