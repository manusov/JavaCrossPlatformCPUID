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
Class for support CPUID Function, specific for Intel Xeon Phi:
20000000h = Maximum device-specific function for Intel Xeon Phi.
*/

package cpuidv2.cpuidfunctions;

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
