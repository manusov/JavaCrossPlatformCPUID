//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID standard function 0000000Ch declared as CPR.COMMAND.

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID0000000C extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME = 
    "Reserved";
    
// Return CPUID this function full name
// INPUT:   Reserved array
// OUTPUT:  String, CPUID function full name
@Override public String getCommandLongName(long[] dummy ) 
    { return F_NAME; }
    
// Build and return CPUID this function detail information table
// INPUT:   Binary array = CPUID dump data
// OUTPUT:  Array of strings = CPUID this function detail information table
@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[1][2];
    result[0][0] = "This function reserved";
    result[0][1] = "n/a";
    return result;
    }
}