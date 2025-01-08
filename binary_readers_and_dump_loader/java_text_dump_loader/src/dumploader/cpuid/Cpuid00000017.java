/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 00000017h =
System-On-Chip vendor attribute enumeration.

*/

package dumploader.cpuid;

import java.util.ArrayList;

class Cpuid00000017 extends ParameterFunctionCpuid
{
Cpuid00000017()
    { setFunction( 0x00000017 ); }

@Override String getLongName()
    { return "System-On-Chip vendor attribute enumeration"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Maximum SOCID index" , 31 , 0 } };
private final static Object[][] DECODER_EBX =
    { { "SOC Vendor ID" , 15 , 0     } ,
      { "Is Vendor Scheme" , 16 , 16 } };
private final static Object[][] DECODER_ECX =
    { { "Project ID" , 31 , 0 } };
private final static Object[][] DECODER_EDX =
    { { "Stepping ID" , 31 , 0 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX, subfunction 0
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        int maxSubFunction = dr.values[0];
        a.addAll( dr.strings );
        // EBX, subfunction 0
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        dr.strings.get(1)[4] = ( dr.values[1] == 0 ) ? 
                "Assigned by Intel" : "Industry standard enumeration";
        a.addAll( dr.strings );
        // ECX, subfunction 0
        dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
        a.addAll( dr.strings );
        // EDX, subfunction 0
        dr = decodeBitfields( "EDX", DECODER_EDX, entries[0].edx );
        a.addAll( dr.strings );
        // Support text string
        if ( ( maxSubFunction >= 1 ) && ( entries.length >= 2 ) )
            {
            a.add( interval );
            ArrayList<Integer> ia = new ArrayList<>();
            for( int i=1; i<entries.length; i++ )
                {
                if ( entries[i].subfunction != i ) break;
                ia.add( entries[i].eax );
                ia.add( entries[i].ebx );
                ia.add( entries[i].ecx );
                ia.add( entries[i].edx );
                }
            String[] s = new String[] 
                { "SOC Vendor Brand String", "-", "-", "-", "n/a" };
            if ( ! ia.isEmpty() )
                {
                StringBuilder sb = new StringBuilder( "" );
                stringloop:
                for( int num : ia )
                    {
                    for( int i=0; i<4; i++ )
                        {
                        char c = (char)( num & 0xFF );
                        if ( c == 0 ) break stringloop;
                        sb.append( c );
                        num = num >>> 8;
                        }
                    }
                if ( sb.length() > 0 ) s[4] = sb.toString();
                }
            a.add( s );
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
