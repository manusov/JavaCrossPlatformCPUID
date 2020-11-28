/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Virtual Function
40000001h = Virtual CPUID: hypervisor interface information.
*/

package cpuidrefactoring.devicecpuid;

import static cpuidrefactoring.database.VendorDetectVirtual.HYPERVISOR_T.*;
import java.util.ArrayList;

public class Cpuid40000001 extends ParameterFunctionCpuid
{
Cpuid40000001()
    { setFunction( 0x40000001 ); }

@Override String getLongName()
    { 
    if ( container.getVmmVendor() == HYPERVISOR_ORACLE_W )
        return "Hypervisor interface signature";
    else
        return super.getLongName();
    }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Interface signature" , 31 , 0 } };

@Override String[][] getParametersList()
    {
    if ( container.getVmmVendor() == HYPERVISOR_ORACLE_W )
        {
        DecodeReturn dr;
        ArrayList<String[]> a = new ArrayList<>();
        if ( ( entries != null )&&( entries.length > 0 ) )
            {
            // EAX, with signature ASCII comments
            dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
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
    else
        {
        return super.getParametersList();
        }
    }
}
