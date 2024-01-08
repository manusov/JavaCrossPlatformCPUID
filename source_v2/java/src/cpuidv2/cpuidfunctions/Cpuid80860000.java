/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Vendor Specific Function
80860000h = Transmeta vendor-specific: 
            Transmeta maximum vendor-specific function and vendor string.
*/

package cpuidv2.cpuidfunctions;

public class Cpuid80860000 extends ReservedFunctionCpuid
{
Cpuid80860000()
    { setFunction( 0x80860000 ); }

@Override String getLongName()
    { return "Transmeta maximum vendor function and vendor string"; }

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
