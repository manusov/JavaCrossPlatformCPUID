/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
40000010h = Virtual CPUID: timing information
            (Generic Virtual CPUID).
*/

package cpuidrefactoring.devicecpuid;

import java.util.ArrayList;

public class Cpuid40000010 extends ParameterFunctionCpuid
{
Cpuid40000010()
    { setFunction( 0x40000010 ); }

@Override String getLongName()
    { return "Virtual timing information"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Virtual TSC frequency" , 31 , 0 } };
private final static Object[][] DECODER_EBX =
    { { "Virtual system bus frequency (local APIC timer)" , 31 , 0 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        dr.strings.get(0)[4] = frequencyHelper( entries[0].eax );
        a.addAll( dr.strings );
        // EBX
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        dr.strings.get(0)[4] = frequencyHelper( entries[0].ebx );
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }

// non-private because also used for Xen-specific CPUID functions classes
String frequencyHelper( int f )  // f = frequency in kHz
    {
    String s;
    if ( ( f > 100 )&&( f < 100_000_000 ) )  // valid if 100 kHz < f < 100 GHz
        {
        if ( f > 1000000 )
            {
            double fghz = f / 1000000.0;
            s = String.format( "%d kHz = %.3f GHz", f, fghz );
            }
        else if ( f > 1000 )
            {
            double fmhz = f / 1000.0;
            s = String.format( "%d kHz = %.3f MHz", f, fmhz );
            }
        else
            {
            s = String.format( "%d kHz", f );
            }
        }
    else if ( f == 0 )
        s = "n/a";
    else
        s = "?";
    return s;
    }

}
