/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
Driver: Get Java Virtual Machine (JVM) properties strings.
*/

package cpuid.drivers.jvmcontrol;

import cpuid.CpuId;
import cpuid.drivers.cpr.DeviceAdapter;
import cpuid.kernel.Registry;
import java.util.Enumeration;
import java.util.Properties;

public class DeviceJVMcontrol extends DeviceAdapter 
{
    
@Override public int getCommandsCount()  // get number of supported commands
    {
    return 1;
    }

@Override public String[][] getSummaryText()  // get result as text
    {
    Registry r = CpuId.getRegistry();      // get system registry
    Properties p = r.getJvmProperties();   // access target info by registry
    int n = p.size();                      // n = length of properties list
    String[][] text = new String[n][2];    // create array for text table
    Enumeration keys = p.keys();           // enumeration for properties
    // Cycle for properties list elements
    for ( int i=0; i<n; i++ )
        {
        if ( keys.hasMoreElements() )
            {
            text[i][0] = (String)keys.nextElement();
            if (text[i][0].equals("line.separator"))
                {  // special support for correct print separator char(s)
                text[i][1] = (String)p.get( text[i][0] );
                int n1 = text[i][1].length();
                String s = "";
                for (int j=0; j<n1; j++)
                    {
                    s = s + (int)(text[i][1].charAt(j));
                    if (j<n1-1) { s = s + ", "; }
                    }
                text[i][1] = s;
                }
            else
                {  // this used if not a separator char(s)
                text[i][1] = (String)p.get( text[i][0] );
                }
            }
        else    // Unexpected ( because i<n ) absence of property
            {
            text[i][0] = "?";
            text[i][1] = "?";
            }
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
