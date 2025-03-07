/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Virtual Function 40000X01h =
Xen hypervisor version (Xen hypervisor-specific Virtual CPUID).
Note this class must be dynamically add to functions list if hypervisor
vendor = Xen, otherwise function number can conflict with existed.
Note function number can be selected from base 40000001h with 100h increments,
for example 40000101h, 40000201h, ...
to prevent conflict with other hypervisors.

*/

package cpuidv3.servicecpuid;

import java.util.ArrayList;

class Cpuid40000X01 extends ParameterFunctionCpuid
{
Cpuid40000X01( int index )
    { 
    setFunction( 0x40000001 + ( index << 8 ) ); 
    }

@Override String getLongName()
    { return "Xen hypervisor version"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Major version" , 31 , 16 } ,
      { "Minor version" , 15 ,  0 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String s;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        int major = dr.values[0];
        int minor = dr.values[1];
        if ( ( major != 0 )||( minor != 0 ) )
            s = String.format( "%d.%d" , major, minor );
        else
            s = "n/a";
        dr.strings.get(0)[4] = s;
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
