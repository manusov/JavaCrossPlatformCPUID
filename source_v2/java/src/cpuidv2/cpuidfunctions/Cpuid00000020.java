/*
CPUID Utility. (C)2022 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
00000020h = Processor history reset information.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

class Cpuid00000020 extends ParameterFunctionCpuid
{
Cpuid00000020()
    { setFunction( 0x00000020 ); }

@Override String getLongName()
    { return "Processor history reset information"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX_SUBFUNCTION_0 =
    { { "Maximum number of sub-leaves" , 31 , 0 } };
private final static String[][] DECODER_EBX_SUBFUNCTION_0 =
    { { "TD RES" , "Enable reset of Intel Thread Director history" } };  // bit 0

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX, subfunction 0
        dr = decodeBitfields
            ( "EAX", DECODER_EAX_SUBFUNCTION_0, entries[0].eax );
        a.addAll( dr.strings );
        // EBX, subfunction 0
        strings = decodeBitmap
            ( "EBX", DECODER_EBX_SUBFUNCTION_0, entries[0].ebx );
        a.addAll( strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}