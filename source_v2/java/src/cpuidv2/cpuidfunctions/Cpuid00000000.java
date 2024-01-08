/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
00000000h = Maximum standard function and vendor string.
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
