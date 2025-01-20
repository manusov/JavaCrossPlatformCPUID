/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Extended Function 80000027h =
Hetero workload classification.

*/

package cpuidv3.servicecpuid;

import java.util.ArrayList;

class Cpuid80000027 extends ParameterFunctionCpuid
{
Cpuid80000027()
    { setFunction( 0x80000027 ); }

@Override String getLongName()
    { return "Hetero workload classification"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Number of workload class IDs" ,  3 ,  0 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        dr.strings.get(0)[4] = String.format( "%d", dr.values[0] );
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
