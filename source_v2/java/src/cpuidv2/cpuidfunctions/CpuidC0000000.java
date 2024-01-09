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
Class for support CPUID Vendor Specific Function C0000000h =
VIA (Centaur) vendor-specific: VIA (Centaur) maximum vendor-specific function
and optional vendor string.
*/

package cpuidv2.cpuidfunctions;

public class CpuidC0000000 extends ReservedFunctionCpuid
{
CpuidC0000000()
    { setFunction( 0xC0000000 ); }

@Override String getLongName()
    { return "VIA (Centaur) maximum vendor function and vendor string"; }

@Override String[][] getParametersList()
    {
    String[][] table = new String[][]
        { { "Maximum vendor CPUID level" , "n/a" } ,
          { "Vendor specific string"     , "n/a" } };
    if ( ( entries != null )&&( entries.length == 1 ) )
        {
        table[0][1] = String.format( "%08Xh" , entries[0].eax );
        String s = extractVendorString( entries[0], false );
        if ( s != null ) table[1][1] = s;
        }
    return table;
    }


}
