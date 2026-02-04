/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Virtual Function 40000X05h =
Xen hypervisor machine address maximum width
(Xen hypervisor-specific Virtual CPUID).
Note this class must be dynamically add to functions list if hypervisor
vendor = Xen, otherwise function number can conflict with existed.
Note function number can be selected from base 40000005h with 100h increments,
for example 40000105h, 40000205h, ...
to prevent conflict with other hypervisors.

*/

package cpuidv3.servicecpuid;

import java.util.ArrayList;

class Cpuid40000X05 extends ParameterFunctionCpuid
{
Cpuid40000X05( int index ) { setFunction( 0x40000005 + ( index << 8 ) ); }

@Override String getLongName()
    { return "Xen hypervisor machine address maximum width"; }

// Control tables for results decoding.
private final static Object[][] DECODER_EAX =
    { { "Maximum available sub-leaf"   , 31 , 0 } };
private final static Object[][] DECODER_EBX =
    { { "Maximum machine address width"   , 7 , 0 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX.
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        a.addAll( dr.strings );
        // EBX.
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
