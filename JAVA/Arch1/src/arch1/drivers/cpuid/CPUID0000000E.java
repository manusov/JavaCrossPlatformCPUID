//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID standard function 0000000Eh declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID0000000E extends CommandAdapter
{
private static final String 
        F_NAME = "Reserved";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }
    
@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[1][2];
    result[0][0] = "This function reserved";
    result[0][1] = "n/a";
    return result;
    }
}