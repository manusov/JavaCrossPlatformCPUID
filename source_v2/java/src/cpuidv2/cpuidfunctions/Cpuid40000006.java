/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
40000006h = Virtual CPUID: hypervisor hardware features.
*/

package cpuidv2.cpuidfunctions;

import cpuidv2.cpudatabase.VendorDetectVirtual.HYPERVISOR_T;
import static cpuidv2.cpudatabase.VendorDetectVirtual.HYPERVISOR_T.*;
import java.util.ArrayList;

public class Cpuid40000006 extends ParameterFunctionCpuid
{
Cpuid40000006()
    { setFunction( 0x40000006 ); }

@Override String getLongName()
    { 
    HYPERVISOR_T t = container.getVmmVendor();
    if ( ( t == HYPERVISOR_ORACLE )||( t == HYPERVISOR_MICROSOFT ) )
        return "Hypervisor hardware features in use";
    else
        return super.getLongName();
    }

// Control tables for results decoding
private final static String[][] DECODER_EAX =
    { { "APICOV" , "APIC overlay assist"                } ,  // bit 0
      { "MSRBMP" , "MSR bitmaps"                        } ,
      { "APCNT"  , "Architectural performance counters" } ,
      { "SLVAT"  , "Second-level address translation"   } ,
      { "DMAR"   , "DMA remapping"                      } ,
      { "INTR"   , "Interrupt remapping"                } ,
      { "MEMPS"  , "Memory patrol scrubber"             } ,
      { "DMAPT"  , "DMA protection"                     } ,
      { "HPETRQ" , "HPET requested"                     } ,
      { "STMVOL" , "Synthetic timers are volatile"      } };  // bit 9

@Override String[][] getParametersList()
    {
    HYPERVISOR_T t = container.getVmmVendor();
    if ( ( t == HYPERVISOR_ORACLE )||( t == HYPERVISOR_MICROSOFT ) )
        {
        ArrayList<String[]> strings;
        ArrayList<String[]> a = new ArrayList<>();
        if ( ( entries != null )&&( entries.length > 0 ) )
            {
            // EAX
            strings = decodeBitmap( "EAX", DECODER_EAX, entries[0].eax );
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
