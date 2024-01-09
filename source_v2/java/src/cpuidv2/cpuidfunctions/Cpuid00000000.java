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
Class for support CPUID Standard Function 00000000h =
Maximum standard function and vendor string.
*/

package cpuidv2.cpuidfunctions;

class Cpuid00000000 extends ReservedFunctionCpuid
{
Cpuid00000000()
    { setFunction( 0x00000000 ); }

@Override String getLongName()
    { return "Maximum standard function and vendor string"; }

@Override String[][] getParametersList()
    {
    String[][] table = new String[][]
        { { "Maximum standard CPUID level" , "n/a" } ,
          { "CPU vendor string"            , "n/a" } };
    if ( ( entries != null )&&( entries.length == 1 ) )
        {
        table[0][1] = String.format( "%08Xh" , entries[0].eax );
        String s = extractVendorString( entries[0], false );
        if ( s != null ) table[1][1] = s;
        }
    return table;
    }
}
