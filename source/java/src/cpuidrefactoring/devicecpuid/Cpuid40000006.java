/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
40000006h = Virtual CPUID: hypervisor hardware features.
*/

package cpuidrefactoring.devicecpuid;

import static cpuidrefactoring.database.VendorDetectVirtual.HYPERVISOR_T.*;
import java.util.ArrayList;

public class Cpuid40000006 extends ParameterFunctionCpuid
{
Cpuid40000006()
    { setFunction( 0x40000006 ); }

@Override String getLongName()
    { 
    if ( container.getVmmVendor() == HYPERVISOR_ORACLE_W )
        return "Hypervisor hardware features in use";
    else
        return super.getLongName();
    }

// Control tables for results decoding
private final static String[][] DECODER_EAX =
    { { "APICOV" , "APIC overlay assist"                } ,
      { "MSRBMP" , "MSR bitmaps"                        } ,
      { "APCNT"  , "Architectural performance counters" } ,
      { "SLVAT"  , "Second-level address translation"   } ,
      { "DMAR"   , "DMA remapping"                      } ,
      { "INTR"   , "Interrupt remapping"                } ,
      { "MEMPS"  , "Memory patrol scrubber"             } ,
      { "DMAPT"  , "DMA protection"                     } ,
      { "HPETRQ" , "HPET requested"                     } ,
      { "STMVOL" , "Synthetic timers are volatile"      } };

@Override String[][] getParametersList()
    {
    if ( container.getVmmVendor() == HYPERVISOR_ORACLE_W )
        {
        DecodeReturn dr;
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
