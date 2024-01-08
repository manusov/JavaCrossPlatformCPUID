/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
40000000h = Virtual CPUID: hypervisor vendor string and maximum function number.
*/

package cpuidv2.cpuidfunctions;

class Cpuid40000000 extends ReservedFunctionCpuid
{
Cpuid40000000()
    { setFunction( 0x40000000 ); }

@Override String getLongName()
    { return "Virtual CPUID leaf range and vendor string"; }

@Override String[][] getParametersList()
    {
    String[][] table = new String[][]
          { { "Maximum virtual CPUID level"  , "n/a" } ,
            { "Virtual CPU vendor string"    , "n/a" } };
    if ( ( entries != null )&&( entries.length == 1 )&&
       ( ( entries[0].eax & 0xC0000000 ) == 0x40000000 ) )
        {
        table[0][1] = String.format( "%08Xh" , entries[0].eax );
        String s = extractVendorString( entries[0], true );
        if ( s != null ) table[1][1] = s;
        }
    else if( ( entries != null )&&( entries.length == 1 ) )
        {
        table[0][1] = "?";
        table[1][1] = "?";
        }
    return table;
    }
}
