/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Extended Function 80000023h =
AMD multi-key encrypted memory capabilities.

*/

package cpuidv3.servicecpuid;

import java.util.ArrayList;

class Cpuid80000023 extends ParameterFunctionCpuid
{
Cpuid80000023() { setFunction( 0x80000023 ); }

@Override String getLongName()
    { return "AMD multi-key encrypted memory capabilities"; }

// Control tables for results decoding.
private final static String[][] DECODER_EAX =
    { { "MEM-HMK" , "Secure host multi key memory encryption mode" } }; // bit 0
private final static Object[][] DECODER_EBX =
    { { "Number of simultaneously available host encryption key ID" , 15 , 0 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX.
        strings = decodeBitmap( "EAX", DECODER_EAX, entries[0].eax );
        a.addAll( strings );
        // EBX.
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        dr.strings.get(0)[4] = String.format( "%d", dr.values[0] );
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
