/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
40000002h = Xen hypervisor configuration
            (Xen hypervisor-specific Virtual CPUID).

Note this class must be dynamically add to functions list if hypervisor
vendor = Xen, otherwise function number can conflict with existed.
Note function number can be selected from base 40000002h with 100h increments,
for example 40000102h, 40000202h, ...
to prevent conflict with other hypervisors.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

public class Cpuid40000X02 extends ParameterFunctionCpuid
{
Cpuid40000X02( int index )
    { 
    setFunction( 0x40000002 + ( index << 8 ) ); 
    }

@Override String getLongName()
    { return "Xen hypervisor configuration"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Number of hypercall transfer pages" , 31 , 0 } };
private final static Object[][] DECODER_EBX =
    { { "Base address of Xen specific MSR"   , 31 , 0 } };
private final static String[][] DECODER_ECX =
    { { "MMU UPD"  , "MMU_PT_UPDATE_PRESERVE_AD (Access + Dirty)" } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        a.addAll( dr.strings );
        // EBX
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        a.addAll( dr.strings );
        // ECX
        strings = decodeBitmap( "ECX", DECODER_ECX, entries[0].ecx );
        a.addAll( strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
