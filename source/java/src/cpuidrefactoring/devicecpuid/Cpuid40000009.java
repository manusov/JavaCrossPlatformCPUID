/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
40000009h = Virtual CPUID: hypervisor system calls information 
            (Microsoft-Specific). .
*/

package cpuidrefactoring.devicecpuid;

import cpuidrefactoring.database.VendorDetectVirtual.HYPERVISOR_T;
import static cpuidrefactoring.database.VendorDetectVirtual.HYPERVISOR_T.*;
import java.util.ArrayList;

public class Cpuid40000009 extends ParameterFunctionCpuid
{
Cpuid40000009()
    { setFunction( 0x40000009 ); }

@Override String getLongName()
    { 
    HYPERVISOR_T t = container.getVmmVendor();
    if ( ( t == HYPERVISOR_ORACLE )||( t == HYPERVISOR_MICROSOFT ) )
        return "Nested hypervisor feature identification";
    else
        return super.getLongName();
    }

// Control tables for results decoding
private final static String[][] DECODER_EAX =
    { { "x"      , "Reserved"                      } ,
      { "x"      , "Reserved"                      } ,
      { "ASYNIC" , "AccessSynicRegs"               } ,   // bit 2
      { "x"      , "Reserved"                      } ,
      { "AINTR"  , "AccessIntrCtrlRegs"            } ,   // bit 4
      { "AHMSR"  , "AccessHypercallMsrs"           } ,   // bit 5
      { "AVPI"   , "AccessVpIndex"                 } ,   // bit 6
      { "x"      , "Reserved"                      } ,
      { "x"      , "Reserved"                      } ,
      { "x"      , "Reserved"                      } ,
      { "x"      , "Reserved"                      } ,
      { "x"      , "Reserved"                      } ,
      { "ARENGC" , "AccessReenlightenmentControls" } };  // bit 12
private final static String[][] DECODER_EDX =
    { { "x"      , "Reserved"                              } ,
      { "x"      , "Reserved"                              } ,
      { "x"      , "Reserved"                              } ,
      { "x"      , "Reserved"                              } ,
      { "XMMHYP" , "XmmRegistersForFastHypercallAvailable" } ,  // bit 4
      { "x"      , "Reserved"                              } ,
      { "x"      , "Reserved"                              } ,
      { "x"      , "Reserved"                              } ,
      { "x"      , "Reserved"                              } ,
      { "x"      , "Reserved"                              } ,
      { "x"      , "Reserved"                              } ,
      { "x"      , "Reserved"                              } ,
      { "x"      , "Reserved"                              } ,
      { "x"      , "Reserved"                              } ,
      { "x"      , "Reserved"                              } ,
      { "FHYOUT" , "FastHypercallOutputAvailable"          } ,   // bit 15
      { "x"      , "Reserved"                              } ,
      { "SINTPM" , "SintPollingModeAvailable"              } };  // bit 17

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
            // EDX
            strings = decodeBitmap( "EDX", DECODER_EDX, entries[0].edx );
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
