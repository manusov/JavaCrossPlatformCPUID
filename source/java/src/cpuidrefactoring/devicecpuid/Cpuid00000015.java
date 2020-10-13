/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
00000015h = TSC/Core crystal clock ratio.
*/

package cpuidrefactoring.devicecpuid;

import java.util.ArrayList;

class Cpuid00000015 extends ParameterFunctionCpuid
{
Cpuid00000015()
    { setFunction( 0x00000015 ); }

@Override String getLongName()
    { return "TSC/Core crystal clock ratio"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Denominator of the TSC/core crystal clock ratio" , 31 , 0 } };
private final static Object[][] DECODER_EBX =
    { { "Numerator of the TSC/core crystal clock ratio" , 31 , 0 } };
private final static Object[][] DECODER_ECX =
    { { "Nominal frequency of the core crystal" , 31 , 0 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX, subfunction 0
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        dr.strings.get(0)[4] = String.format( "%d" , dr.values[0] );
        a.addAll( dr.strings );
        // EBX, subfunction 0
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        dr.strings.get(0)[4] = String.format( "%d" , dr.values[0] );
        a.addAll( dr.strings );
        // ECX, subfunction 0
        dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
        if ( dr.values[0] != 0 )
            {  // required conversion to unsigned value, note bit 31 can be "1"
            long hz = ( (long) dr.values[0] ) & ( ((long)((long)(-1)>>>32)) );
            double mHz = hz / 1000000.0; 
            dr.strings.get(0)[4] = String.format( "%.3f MHz", mHz );
            }
        else
            {
            dr.strings.get(0)[4] = "n/a";
            }
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
