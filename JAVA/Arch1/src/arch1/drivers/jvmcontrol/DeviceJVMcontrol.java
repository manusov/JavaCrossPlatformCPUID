//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Driver: Get Java Virtual Machine (JVM) properties strings.

package arch1.drivers.jvmcontrol;

import arch1.Arch1;
import arch1.drivers.cpr.DeviceAdapter;
import arch1.kernel.Registry;
import java.util.Enumeration;
import java.util.Properties;

public class DeviceJVMcontrol extends DeviceAdapter 
{
    
@Override public int getCommandsCount()
    {
    return 1;
    }

@Override public String[][] getSummaryText()
    {
//  Properties p = System.getProperties();
//
    Registry r = Arch1.getRegistry();
    Properties p = r.getJvmProperties();
//    
    int n = p.size();
    String[][] text = new String[n][2];
    Enumeration keys = p.keys();
    
    for ( int i=0; i<n; i++ )
        {
        if ( keys.hasMoreElements() )
            {
            text[i][0] = (String)keys.nextElement();
            if (text[i][0].equals("line.separator"))
                {
                text[i][1] = (String)p.get( text[i][0] );
                // int num = text[i][1].charAt(0);
                // text[i][1] = ""+num;
                
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
                {
                text[i][1] = (String)p.get( text[i][0] );
                }
            }
        else
            {
            text[i][0] = "?";
            text[i][1] = "?";
            }
        
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
