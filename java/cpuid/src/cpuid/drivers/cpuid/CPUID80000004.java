/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
CPUID driver component:
CPUID extended function 80000004h declared as CPR.COMMAND.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID80000004 extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME = 
    "Processor name string [3 of 3]";

// Return CPUID this function full name
// INPUT:   Reserved array
// OUTPUT:  String, CPUID function full name    
@Override public String getCommandLongName(long[] dummy ) 
    { return F_NAME; }
    
}
