/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
8000001Ah = AMD performance optimization identifiers.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

class Cpuid8000001A extends ParameterFunctionCpuid
{
Cpuid8000001A()
    { setFunction( 0x8000001A ); }

@Override String getLongName()
    { return "AMD performance optimization identifiers"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "FP128, Hardware SSE 128-bit instead 2 x 64 emulation"   , 0 , 0 } ,
      { "MOVU,  Prefer unaligned 128 bit instead MOVL/MOVH"      , 1 , 1 } ,
      { "FP256, Hardware AVX 256-bit instead 2 x 128 emulation"  , 2 , 2 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
