/*
CPUID Utility. (C)2023 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
00000023h = Architectural performance monitoring extended leaf.
*/

package cpuidrefactoring.devicecpuid;

import java.util.ArrayList;

class Cpuid00000023 extends ParameterFunctionCpuid
{
Cpuid00000023()
    { setFunction( 0x00000023 ); }

@Override String getLongName()
    { return "Architectural performance monitoring extended leaf"; }

// Control tables for results decoding, subfunction 0
private final static Object[][] DECODER_EAX_SUBFUNCTION_0 =
    { { "Intel architectural performance valid sub-leaves limit" , 31 , 0  } };
private final static String[][] DECODER_EBX_SUBFUNCTION_0 =
    { { "UNITMSK2"     , "UnitMask2 field in the IA32_PERFEVTSELx MSRs" } , // bit 0
      { "ZBIT"         , "Zero-bit in the IA32_PERFEVTSELx MSRs" } };       // bit 1
// subfunction 1
private final static Object[][] DECODER_EAX_SUBFUNCTION_1 =
    { { "General counters bitmap"   , 31 , 0 } };
private final static Object[][] DECODER_EBX_SUBFUNCTION_1 =
    { { "Fixed counters bitmap"   , 31 , 0 } };
// subfunction 3
private final static String[][] DECODER_EAX_SUBFUNCTION_3 =
    { { "x"            , "Core cycles monitoring event" } ,  // bit 0
      { "x"            , "Instructions retired monitoring event" } ,
      { "x"            , "Reference cycles monitoring event" } ,
      { "x"            , "Last level cache references monitoring event" } ,
      { "x"            , "Last level cache misses monitoring event" } ,
      { "x"            , "Branch instructions retired monitoring event" } ,
      { "x"            , "Branch mispredicts retired monitoring event" } ,
      { "x"            , "Topdown slots monitoring event" } ,
      { "x"            , "Topdown backend bound monitoring event" } ,
      { "x"            , "Topdown bad speculation monitoring event" } ,
      { "x"            , "Topdown frontend bound monitoring event" } ,
      { "x"            , "Topdown retiring monitoring event" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } };  // bit 15

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX, subfunction 0
        dr = decodeBitfields
            ( "EAX", DECODER_EAX_SUBFUNCTION_0, entries[0].eax );
        a.addAll( dr.strings );
        strings = decodeBitmap
            ( "EBX", DECODER_EBX_SUBFUNCTION_0, entries[0].ebx );
        a.addAll( strings );
        int maxSubFunction = dr.values[0];
        if ( ( entries.length > 1 )&&( maxSubFunction > 0 )&&
             ( entries[1].subfunction == 1 ) )
            {
            a.add( interval );
            // EAX, subfunction 1
            dr = decodeBitfields
                ( "EAX", DECODER_EAX_SUBFUNCTION_1, entries[1].eax );
            a.addAll( dr.strings );
            // EBX, subfunction 1
            dr = decodeBitfields
                ( "EBX", DECODER_EBX_SUBFUNCTION_1, entries[1].ebx );
            a.addAll( dr.strings );
            if ( ( entries.length > 3 )&&( maxSubFunction > 2 )&&
             ( entries[3].subfunction == 3 ) )
                {
                // EAX, subfunction 3
                a.add( interval );
                strings = decodeBitmap
                    ( "EAX", DECODER_EAX_SUBFUNCTION_3, entries[3].eax );
                a.addAll( strings );
                }
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
