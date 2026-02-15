/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Vendor Specific Function C0000006h =
VIA (Centaur) vendor-specific: VIA (Centaur) PAUSEOPT instruction support.

*/

package cpuidv3.servicecpuid;

import java.util.ArrayList;

class CpuidC0000006 extends ParameterFunctionCpuid
{
CpuidC0000006() { setFunction( 0xC0000006 ); }

@Override String getLongName()
    { return "VIA (Centaur) PAUSEOPT instruction support"; }

// Control tables for results decoding
private final static String[][] DECODER_EAX_SUBFUNCTION_0 =
    { { "PSOPT"        , "PAUSEOPT instruction" } ,  // bit 0
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } };               // bit 3

@Override String[][] getParametersList()
    {
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    // Subfunction 0.
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX, subfunction 0.
        strings = decodeBitmap
            ( "EAX", DECODER_EAX_SUBFUNCTION_0, entries[0].eax );
        a.addAll( strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}