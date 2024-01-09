/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2024.
-------------------------------------------------------------------------------
Class for support CPUID Vendor Specific Function 80860007h =
Transmeta vendor-specific:
Transmeta processor actual operational mode information.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

public class Cpuid80860007 extends ParameterFunctionCpuid
{
Cpuid80860007()
    { setFunction( 0x80860007 ); }

@Override String getLongName()
    { return "Transmeta processor actual operational mode information"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Transmeta processor actual clock frequency"  , 31 ,  0 } };
private final static Object[][] DECODER_EBX =
    { { "Transmeta processor actual voltage"          , 31 ,  0 } };
private final static Object[][] DECODER_ECX =
    { { "Transmeta processor actual performance"      , 31 ,  0 } };
private final static Object[][] DECODER_EDX =
    { { "Transmeta processor actual gate delay"       , 31 ,  0 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String s;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX: Transmeta processor actual clock frequency
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        int frequency = dr.values[0];
        s = String.format( "%d MHz", frequency );
        dr.strings.get(0)[4] = s;
        a.addAll( dr.strings );
        // EBX: Transmeta processor actual voltage
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        int voltage = dr.values[0];
        s = String.format( "%d mV", voltage );
        dr.strings.get(0)[4] = s;
        a.addAll( dr.strings );
        // ECX: Transmeta processor actual performance
        dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
        int performance = dr.values[0];
        s = String.format( "%d %%", performance );
        dr.strings.get(0)[4] = s;
        a.addAll( dr.strings );
        // EDX: Transmeta processor actual gate delay
        dr = decodeBitfields( "EDX", DECODER_EDX, entries[0].edx );
        int delay = dr.values[0];
        s = String.format( "%d fs", delay );
        dr.strings.get(0)[4] = s;
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
