/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 00000009h =
Direct Cache Access information.

*/

package cpuidv3.servicecpuid;

import java.util.ArrayList;

class Cpuid00000009 extends ParameterFunctionCpuid
{
Cpuid00000009() { setFunction( 0x00000009 ); }

@Override String getLongName()
    { return "Direct Cache Access information"; }

// Control tables for results decoding.
private final static Object[][] DECODER_EAX =
    { { "IA32 PLATFORM DCA CAP MSR, bits [31-00]" , 31 , 0 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
