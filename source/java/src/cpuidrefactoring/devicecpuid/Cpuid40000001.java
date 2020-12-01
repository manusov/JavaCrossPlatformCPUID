/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
40000001h = Virtual CPUID: hypervisor interface information.
*/

package cpuidrefactoring.devicecpuid;

import cpuidrefactoring.database.VendorDetectVirtual.HYPERVISOR_T;
import static cpuidrefactoring.database.VendorDetectVirtual.HYPERVISOR_T.*;
import java.util.ArrayList;

public class Cpuid40000001 extends ParameterFunctionCpuid
{
Cpuid40000001()
    { setFunction( 0x40000001 ); }

@Override String getLongName()
    {
    HYPERVISOR_T t = container.getVmmVendor();
    if ( ( t == HYPERVISOR_ORACLE )||( t == HYPERVISOR_MICROSOFT ) )
        return "Hypervisor interface identification";
    else if ( t == HYPERVISOR_KVM )
        return "KVM CPUID features";
    else
        return super.getLongName();
    }

// Control tables for results decoding
private final static Object[][] DECODER_EAX_MS =
    { { "Interface signature" , 31 , 0 } };

private final static String[][] DECODER_EAX_KVM =
    { { "CLKSRC1" , "KVM clock available at MSR 0x11"          } ,  // bit 0
      { "FSTPIO"  , "Delays unnecessary for PIO ops"           } ,
      { "MMU"     , "MMU_op"                                   } ,
      { "CLKSRC2" , "KVM clock available at MSR 0x4b564d00"    } ,
      { "ASYNCPF" , "Async PF enable available by MSR"         } ,
      { "STLCLK"  , "Steal clock supported"                    } ,
      { "GSTEOI"  , "Guest EOI optimization enabled"           } ,
      { "GSTSPL"  , "Guest spinlock optimization enabled"      } ,
      { "x"       , "Reserved"                                 } ,  // bit 8
      { "GSTTLBF" , "Guest TLB flush optimization enabled"     } ,
      { "ASPFE"   , "Async PF VM exit enable available by MSR" } ,
      { "GSTIPI"  , "Guest send IPI optimization enabled"      } ,
      { "HLTPL"   , "Host HLT poll disable at MSR 0x4b564d05"  } ,
      { "GSTSCH"  , "Guest sched yield optimization enabled"   } ,
      { "GPGRD"   , "Guest uses intrs for page ready APF"      } ,
      { "GMSIEX" ,  "Guest MSI extended destination ID"        } ,
      { "x"      ,  "Reserved"                                 } ,  // bit 16
      { "x"      ,  "Reserved"                                 } ,
      { "x"      ,  "Reserved"                                 } ,
      { "x"      ,  "Reserved"                                 } ,
      { "x"      ,  "Reserved"                                 } ,
      { "x"      ,  "Reserved"                                 } ,
      { "x"      ,  "Reserved"                                 } ,
      { "x"      ,  "Reserved"                                 } ,
      { "STABLE" ,  "Stable: no guest per-cpu warps expected"  } };  // bit 24
private final static String[][] DECODER_EDX_KVM =
    { { "RTHNUP" , "Realtime hint: no unbound preemption"      } };  // bit 0

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    HYPERVISOR_T t = container.getVmmVendor();
    
    // Variant for Oracle and Microsoft virtual CPU
    if ( ( t == HYPERVISOR_ORACLE )||( t == HYPERVISOR_MICROSOFT ) )
        {
        if ( ( entries != null )&&( entries.length > 0 ) )
            {
            // EAX, with signature ASCII comments
            dr = decodeBitfields( "EAX", DECODER_EAX_MS, entries[0].eax );
            StringBuilder sb = new StringBuilder();
            int d = dr.values[0];
            for( int i=0; i<4; i++ )
                {
                char c = (char)( d & 0xFF );
                if ( c != 0 )
                    {
                    if ( ( c < ' ' )||( c > '}' ) ) c = '.';
                    sb.append( c );
                    }
                d = d >>> 8;
                }
            dr.strings.get(0)[4] = String.format("\"%s\"", sb.toString() );
            a.addAll( dr.strings );
            }
        return a.isEmpty() ? 
            super.getParametersList() : a.toArray( new String[a.size()][] );
        }
    
    // Variant for KVM virtual CPU
    else if ( t == HYPERVISOR_KVM )
        {
        if ( ( entries != null )&&( entries.length > 0 ) )
            {
            // EAX
            strings = decodeBitmap( "EAX", DECODER_EAX_KVM, entries[0].eax );
            a.addAll( strings );
            a.add( interval );
            // EDX
            strings = decodeBitmap( "EDX", DECODER_EDX_KVM, entries[0].edx );
            a.addAll( strings );
            }
        return a.isEmpty() ? 
            super.getParametersList() : a.toArray( new String[a.size()][] );
        }
    
    // Variant for detalization not supported for this virtual CPU 
    else
        {
        return super.getParametersList();
        }
    }
}
