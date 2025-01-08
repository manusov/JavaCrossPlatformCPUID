/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Virtual Function 40000007h =
Virtual CPUID: hypervisor logical processor information (Microsoft-Specific).

*/

package dumploader.cpuid;

import dumploader.cpudata.VendorDetectVirtual.HYPERVISOR_T;
import static dumploader.cpudata.VendorDetectVirtual.HYPERVISOR_T.*;
import java.util.ArrayList;

class Cpuid40000007 extends ParameterFunctionCpuid
{
Cpuid40000007()
    { setFunction( 0x40000007 ); }

@Override String getLongName()
    { 
    HYPERVISOR_T t = container.getVmmVendor();
    if ( ( t == HYPERVISOR_ORACLE )||( t == HYPERVISOR_MICROSOFT ) )
        return "Hypervisor CPU management features";
    else
        return super.getLongName();
    }

// Control tables for results decoding
private final static String[][] DECODER_EAX =
    { { "STRTLP", "StartLogicalProcessor"      } ,
      { "CRTRVP", "CreateRootVirtualProcessor" } ,
      { "PCSYNC", "PerformanceCounterSync"     } ,  // bit 2
      { "x"      , "Reserved"                  } ,  // bit 3
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,
      { "x"      , "Reserved"                  } ,   // bit 30
      { "RSIDB"  , "ReservdIdentityBit"        } };  // bit 31
private final static String[][] DECODER_EBX =
    { { "PPMT"   , "ProcessorPowerManagement"  } ,
      { "MWIDS"  , "MwaitIdleStates"           } ,
      { "LPIDL"  , "LogicalProcessorIdling"    } };
private final static String[][] DECODER_ECX =
    { { "RMPGU"  , "RemapGuestUncached"        } };

@Override String[][] getParametersList()
    {
    HYPERVISOR_T t = container.getVmmVendor();
    if ( ( t == HYPERVISOR_ORACLE )||( t == HYPERVISOR_MICROSOFT ) )
        {
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
            strings = decodeBitmap( "EBX", DECODER_EBX, entries[0].ebx );
            a.addAll( strings );
            a.add( interval );
            // ECX
            strings = decodeBitmap( "ECX", DECODER_ECX, entries[0].ecx );
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
