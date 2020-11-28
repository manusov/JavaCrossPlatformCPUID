/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
40000002h = Virtual CPUID: hypervisor version.
*/

package cpuidrefactoring.devicecpuid;

import static cpuidrefactoring.database.VendorDetectVirtual.HYPERVISOR_T.*;
import java.util.ArrayList;

public class Cpuid40000002 extends ParameterFunctionCpuid
{
Cpuid40000002()
    { setFunction( 0x40000002 ); }

@Override String getLongName()
    { 
    if ( container.getVmmVendor() == HYPERVISOR_ORACLE_W )
        return "Hypervisor and service version";
    else
        return super.getLongName();
    }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Build number" , 31 , 0 } };
private final static Object[][] DECODER_EBX =
    { { "Major version" , 31 , 16 } ,
      { "Minor version" , 15 ,  0 } };
private final static Object[][] DECODER_ECX =
    { { "Service pack"  , 31 , 0 } };
private final static Object[][] DECODER_EDX =
    { { "Service branch"  , 31 , 24 } ,
      { "Service number"  , 23 ,  0 } };

@Override String[][] getParametersList()
    {
    if ( container.getVmmVendor() == HYPERVISOR_ORACLE_W )
        {
        DecodeReturn dr;
        ArrayList<String[]> a = new ArrayList<>();
        if ( ( entries != null )&&( entries.length > 0 ) )
            {
            // EAX
            dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
            dr.strings.get(0)[4] = String.format( "Build %d", dr.values[0] );
            a.addAll( dr.strings );
            // EBX
            dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
            dr.strings.get(0)[4] = String.format( "Version %d.%d", 
                                                  dr.values[0], dr.values[1] );
            a.addAll( dr.strings );
            // ECX
            dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
            dr.strings.get(0)[4] = String.format( "Service pack %d", 
                                                  dr.values[0] );
            a.addAll( dr.strings );
            // EDX
            dr = decodeBitfields( "EDX", DECODER_EDX, entries[0].edx );
            dr.strings.get(0)[4] = String.format( "Branch %d", dr.values[0] );
            dr.strings.get(1)[4] = String.format( "Number %d", dr.values[1] );
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