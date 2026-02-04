/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Virtual Function 40000005h =
Virtual CPUID: hypervisor multiprocessing information.

*/

package cpuidv3.servicecpuid;

import cpuidv3.servicecpudata.ServiceCpudata.HYPERVISOR_T;
import static cpuidv3.servicecpudata.ServiceCpudata.HYPERVISOR_T.*;
import java.util.ArrayList;

class Cpuid40000005 extends ParameterFunctionCpuid
{
Cpuid40000005() { setFunction( 0x40000005 ); }

@Override String getLongName()
    { 
    HYPERVISOR_T t = getVmmVendor();
    if ( ( t == HYPERVISOR_ORACLE )||( t == HYPERVISOR_MICROSOFT ) )
        return "Hypervisor multiprocessor limits";
    else
        return super.getLongName();
    }

// Control tables for results decoding.
private final static Object[][] DECODER_EAX =
    { { "Maximum supported virtual processors"  , 31 , 0 } };
private final static Object[][] DECODER_EBX =
    { { "Maximum supported logical processors"  , 31 , 0 } };
private final static Object[][] DECODER_ECX =
    { { "Maximum supported physical interrupt vectors for remapping"  , 31 , 0 } };

@Override String[][] getParametersList()
    {
    HYPERVISOR_T t = getVmmVendor();
    if ( ( t == HYPERVISOR_ORACLE )||( t == HYPERVISOR_MICROSOFT ) )
        {
        DecodeReturn dr;
        String s;
        ArrayList<String[]> a = new ArrayList<>();
        if ( ( entries != null )&&( entries.length > 0 ) )
            {
            // EAX.
            dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
            s = ( dr.values[0] == 0 ) ?
                "n/a" : String.format( "%d", dr.values[0] );
            dr.strings.get(0)[4] = s;
            a.addAll( dr.strings );
            // EBX.
            dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
            s = ( dr.values[0] == 0 ) ?
                "n/a" : String.format( "%d", dr.values[0] );
            dr.strings.get(0)[4] = s;
            a.addAll( dr.strings );
            // ECX.
            dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
            s = ( dr.values[0] == 0 ) ?
                "n/a" : String.format( "%d", dr.values[0] );
            dr.strings.get(0)[4] = s;
            a.addAll( dr.strings );
            }
        return a.isEmpty() ? 
            super.getParametersList() : a.toArray( new String[a.size()][] );
        }
    else
        {
        return super.getParametersList();
        }
    }
}
