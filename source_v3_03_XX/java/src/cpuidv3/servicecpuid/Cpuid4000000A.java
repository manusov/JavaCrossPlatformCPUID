/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Virtual Function 4000000Ah =
Virtual CPUID: hypervisor enlightened VMCS information (Microsoft-Specific).

*/

package cpuidv3.servicecpuid;

import cpuidv3.servicecpudata.VendorDetectVirtual.HYPERVISOR_T;
import static cpuidv3.servicecpudata.VendorDetectVirtual.HYPERVISOR_T.HYPERVISOR_MICROSOFT;
import static cpuidv3.servicecpudata.VendorDetectVirtual.HYPERVISOR_T.HYPERVISOR_ORACLE;
import java.util.ArrayList;

class Cpuid4000000A extends ParameterFunctionCpuid
{
Cpuid4000000A()
    { setFunction( 0x4000000A ); }

@Override String getLongName()
    { 
    HYPERVISOR_T t = container.getVmmVendor();
    if ( ( t == HYPERVISOR_ORACLE )||( t == HYPERVISOR_MICROSOFT ) )
        return "Hypervisor nested virtualization features";
    else
        return super.getLongName();
    }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Combining virtualization exceptions in the PF class" , 20 , 20 } ,
      { "Support enlightened MSR bitmap"                      , 19 , 19 } ,
      { "Support HvFlushGuestPhysicalAddress hypercalls"      , 18 , 18 } ,
      { "Support for direct virtual flush hypercalls"         , 17 , 17 } ,
      { "Enlightened VMCS version (high)"                     , 15 ,  8 } ,
      { "Enlightened VMCS version (low)"                      ,  7 ,  0 } };

@Override String[][] getParametersList()
    {
    HYPERVISOR_T t = container.getVmmVendor();
    if ( ( t == HYPERVISOR_ORACLE )||( t == HYPERVISOR_MICROSOFT ) )
        {
        DecodeReturn dr;
        ArrayList<String[]> a = new ArrayList<>();
        if ( ( entries != null )&&( entries.length > 0 ) )
            {
            // EAX
            dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
            dr.strings.get(4)[4] = String.format( "Version %d.%d", 
                                                  dr.values[4], dr.values[5] );
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
