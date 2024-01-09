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
Class for support CPUID Extended Function 80000000h =
Maximum extended CPUID function number.
*/

package cpuidv2.cpuidfunctions;

class Cpuid80000000 extends ReservedFunctionCpuid
{
Cpuid80000000()
    { setFunction( 0x80000000 ); }

@Override String getLongName()
    { return "Maximum extended CPUID function number"; }

@Override String[][] getParametersList()
    {
    String[][] table = new String[][]
        { { "Maximum extended CPUID level" , "n/a" } ,
          { "CPU extended vendor string"   , "n/a" } };
    if ( ( entries != null )&&( entries.length == 1 )&&
            ( ( entries[0].eax & 0x80000000 ) != 0 ) )
        {
        table[0][1] = String.format( "%08Xh" , entries[0].eax );
        String s = extractVendorString( entries[0], false );
        if ( s != null ) table[1][1] = s;
        }
    else if( ( entries != null )&&( entries.length == 1 ) )
        {
        table[1][1] = "n/a";
        }
    return table;
    }
}
