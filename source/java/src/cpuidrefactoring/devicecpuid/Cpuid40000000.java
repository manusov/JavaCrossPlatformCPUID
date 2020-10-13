/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
40000000h = Virtual CPUID vendor string.
*/

package cpuidrefactoring.devicecpuid;

class Cpuid40000000 extends ReservedFunctionCpuid
{
Cpuid40000000()
    { setFunction( 0x40000000 ); }

@Override String getLongName()
    { return "Virtual CPUID vendor string"; }

@Override String[][] getParametersList()
    {
    String[][] table = new String[][]
        { { "Virtual CPU vendor string" , "n/a" } };
    if ( ( entries != null )&&( entries.length == 1 ) )
        {
        String s = extractVendorString( entries[0] );
        if ( s != null ) table[0][1] = s;
        }
    return table;
    }
}
