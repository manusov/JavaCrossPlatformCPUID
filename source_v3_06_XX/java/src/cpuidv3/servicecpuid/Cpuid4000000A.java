/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Virtual Function 4000000Ah =
Virtual CPUID: hypervisor enlightened VMCS information (Microsoft-Specific).

*/

package cpuidv3.servicecpuid;

import cpuidv3.servicecpudata.ServiceCpudata.HYPERVISOR_T;
import static cpuidv3.servicecpudata.ServiceCpudata.HYPERVISOR_T.*;
import java.util.ArrayList;

class Cpuid4000000A extends ParameterFunctionCpuid
{
Cpuid4000000A() { setFunction( 0x4000000A ); }

@Override String getLongName()
    { 
    HYPERVISOR_T t = getVmmVendor();
    if ( ( t == HYPERVISOR_ORACLE )||( t == HYPERVISOR_MICROSOFT ) )
        return "Hypervisor nested virtualization features";
    else
        return super.getLongName();
    }

// Control tables for results decoding.
private final static Object[][] DECODER_EAX =
    { { "Enlightened VMCS version (low)"                      ,  7 ,  0 } ,
      { "Enlightened VMCS version (high)"                     , 15 ,  8 } ,
      { "Support for direct virtual flush hypercalls"         , 17 , 17 } ,  
      { "Support HvFlushGuestPhysicalAddress hypercalls"      , 18 , 18 } ,  
      { "Support enlightened MSR bitmap"                      , 19 , 19 } ,  
      { "Combining virtualization exceptions in the PF class" , 20 , 20 } ,
      { "Non-zero value of the GuestIa32DebugCtl"             , 21 , 21 } ,
      { "Enlightened TLB on AMD platforms"                    , 22 , 22 } };

private final static String[][] DECODER_EBX =
    { { "PERFGL" , "GuestPerfGlobalCtrl and HostPerfGlobalCtrl" } ,  // bit 0
      { "x"      , "Reserved"                                   } ,
      { "x"      , "Reserved"                                   } ,
      { "x"      , "Reserved"                                   } };

@Override String[][] getParametersList()
    {
    HYPERVISOR_T t = getVmmVendor();
    if ( ( t == HYPERVISOR_ORACLE )||( t == HYPERVISOR_MICROSOFT ) )
        {
        DecodeReturn dr;
        ArrayList<String[]> strings;
        String[] interval = new String[] { "", "", "", "", "" };
        ArrayList<String[]> a = new ArrayList<>();
        if ( ( entries != null )&&( entries.length > 0 ) )
            {
            // EAX
            dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
            dr.strings.get(0)[4] = 
                String.format( "Version %d.%d", dr.values[1], dr.values[0] );
            a.addAll( dr.strings );
            a.add( interval );
            // EBX
            strings = decodeBitmap( "EBX", DECODER_EBX, entries[0].ebx );
            a.addAll( strings );
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
