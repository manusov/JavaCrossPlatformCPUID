/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
40000004h = Virtual CPUID: hypervisor recommendations.
*/

package cpuidrefactoring.devicecpuid;

import static cpuidrefactoring.database.VendorDetectVirtual.HYPERVISOR_T.*;
import java.util.ArrayList;

public class Cpuid40000004 extends ParameterFunctionCpuid
{
Cpuid40000004()
    { setFunction( 0x40000004 ); }

@Override String getLongName()
    { 
    if ( container.getVmmVendor() == HYPERVISOR_ORACLE_W )
        return "Hypervisor optimal recommendations";
    else
        return super.getLongName();
    }

// Control tables for results decoding
private final static String[][] DECODER_EAX =
    { { "HYAS" , "Use hypercalls for AS switches"          } ,
      { "HYLTLB" , "Use hypercalls for local TLB flushes"  } ,
      { "HYRTLB" , "Use hypercalls for remote TLB flushes" } ,
      { "MSRINT" , "Use MSRs to access EOI, ICR, TPR"      } ,
      { "MSRRST" , "Use MSRs to initiate system RESET"     } ,
      { "RELTIM" , "Use relaxed timing"                    } ,
      { "DMAR"   , "Use DMA remapping"                     } ,
      { "INTR"   , "Use interrupt remapping"               } ,
      { "X2MSR"  , "Use x2APIC MSRs"                       } ,
      { "DAEOI"  , "Deprecate AutoEOI"                     } ,
      { "HYSCL"  , "Use SyntheticClusterIpi hypercall"     } ,
      { "EXPM"   , "Use ExProcessorMasks"                  } ,
      { "NEST"   , "Hypervisor is nested with Hyper-V"     } ,
      { "INTM"   , "Use INT for MBEC system calls"         } ,
      { "EVMCS"  , "Use enlightened VMCS interface"        } };
private final static Object[][] DECODER_EBX =
    { { "Recommended spinlock failure retries"  , 31 , 0 } };
        
@Override String[][] getParametersList()
    {
    if ( container.getVmmVendor() == HYPERVISOR_ORACLE_W )
        {
        DecodeReturn dr;
        String[] interval = new String[] { "", "", "", "", "" };
        ArrayList<String[]> strings;
        ArrayList<String[]> a = new ArrayList<>();
        if ( ( entries != null )&&( entries.length > 0 ) )
            {
            // EAX
            strings = decodeBitmap( "EAX", DECODER_EAX, entries[0].eax );
            a.addAll( strings );
            a.add( interval );
            // EBX
            dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
            int x = dr.values[0];
            String s;
            if      ( x > 0   ) s = String.format( "Count = %d", x );
            else if ( x == -1 ) s = "retries=never";
            else                s = "?";
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
