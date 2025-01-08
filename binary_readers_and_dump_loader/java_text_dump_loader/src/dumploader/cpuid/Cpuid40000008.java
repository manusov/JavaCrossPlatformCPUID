/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Virtual Function 40000008h =
Virtual CPUID: hypervisor SVM information (Microsoft-Specific).
Here SVM = Shared Virtual Memory.

*/

package dumploader.cpuid;

import dumploader.cpudata.VendorDetectVirtual.HYPERVISOR_T;
import static dumploader.cpudata.VendorDetectVirtual.HYPERVISOR_T.*;
import java.util.ArrayList;

class Cpuid40000008 extends ParameterFunctionCpuid
{
Cpuid40000008()
    { setFunction( 0x40000008 ); }

@Override String getLongName()
    { 
    HYPERVISOR_T t = container.getVmmVendor();
    if ( ( t == HYPERVISOR_ORACLE )||( t == HYPERVISOR_MICROSOFT ) )
        return "Hypervisor shared virtual memory features";
    else
        return super.getLongName();
    }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "MaxPasidSpacePasidCount" , 31 , 11 } ,
      { "SvmSupported"            ,  0 ,  0 } };

@Override String[][] getParametersList()
    {
    HYPERVISOR_T t = container.getVmmVendor();
    if ( ( t == HYPERVISOR_ORACLE )||( t == HYPERVISOR_MICROSOFT ) )
        {
        DecodeReturn dr;
        String s;
        ArrayList<String[]> a = new ArrayList<>();
        if ( ( entries != null )&&( entries.length > 0 ) )
            {
            // EAX
            dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
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
