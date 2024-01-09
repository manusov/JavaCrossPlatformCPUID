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
Class for support CPUID Standard Function 00000016h =
Processor frequency information.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

class Cpuid00000016 extends ParameterFunctionCpuid
{
Cpuid00000016()
    { setFunction( 0x00000016 ); }

@Override String getLongName()
    { return "Processor frequency information"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Processor base frequency" , 15 , 0 } };
private final static Object[][] DECODER_EBX =
    { { "Maximum frequency" , 15 , 0 } };
private final static Object[][] DECODER_ECX =
    { { "Bus (reference) frequency" , 15 , 0 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX, subfunction 0
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        dr.strings.get(0)[4] = dr.values[0] + "MHz";
        a.addAll( dr.strings );
        // EBX, subfunction 0
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        dr.strings.get(0)[4] = dr.values[0] + "MHz";
        a.addAll( dr.strings );
        // ECX, subfunction 0
        dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
        dr.strings.get(0)[4] = dr.values[0] + "MHz";
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
