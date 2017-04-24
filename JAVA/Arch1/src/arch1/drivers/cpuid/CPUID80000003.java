//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID extended function 80000003h declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID80000003 extends CommandAdapter
{
private static final String 
        F_NAME = "Processor name string [2 of 3]";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }

}
