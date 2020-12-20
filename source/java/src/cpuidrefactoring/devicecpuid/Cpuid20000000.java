/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Function, specific for Intel Xeon Phi
20000000h = Maximum device-specific function for Intel Xeon Phi.
*/

package cpuidrefactoring.devicecpuid;

public class Cpuid20000000 extends ReservedFunctionCpuid
{
Cpuid20000000()
    { setFunction( 0x20000000 ); }

@Override String getLongName()
    { return "Maximum device-specific function for Intel Xeon Phi"; }

@Override String[][] getParametersList()
    {
    String[][] table = new String[][]
        { { "Maximum Intel Xeon Phi device-specific CPUID level" , "n/a" } };
          
    if ( ( entries != null )&&( entries.length == 1 ) )
        {
        table[0][1] = String.format( "%08Xh" , entries[0].eax );
        }
    return table;
    }
}
