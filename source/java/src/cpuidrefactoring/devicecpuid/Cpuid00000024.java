/*
CPUID Utility. (C)2023 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
00000024h = Intel AVX10 extension supported level information.
*/

package cpuidrefactoring.devicecpuid;

import java.util.ArrayList;

class Cpuid00000024 extends ParameterFunctionCpuid
{
Cpuid00000024()
    { setFunction( 0x00000024 ); }

@Override String getLongName()
    { return "Intel AVX10 extension supported level leaf"; }

// Control tables for results decoding, subfunction 0
private final static Object[][] DECODER_EBX_SUBFUNCTION_0 =
    { { "Intel AVX10 extension supported level" , 7 , 0  } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX, subfunction 0
        dr = decodeBitfields
            ( "EBX", DECODER_EBX_SUBFUNCTION_0, entries[0].ebx );
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}