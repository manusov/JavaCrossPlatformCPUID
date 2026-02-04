/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 00000015h =
TSC/Core crystal clock ratio.

*/

package cpuidv3.servicecpuid;

import java.util.ArrayList;

class Cpuid00000015 extends ParameterFunctionCpuid
{
Cpuid00000015() { setFunction( 0x00000015 ); }

@Override String getLongName()
    { return "TSC/Core crystal clock ratio"; }

// Control tables for results decoding.
private final static Object[][] DECODER_EAX =
    { { "Denominator of the TSC/core crystal clock ratio" , 31 , 0 } };
private final static Object[][] DECODER_EBX =
    { { "Numerator of the TSC/core crystal clock ratio" , 31 , 0 } };
private final static Object[][] DECODER_ECX =
    { { "Nominal frequency of the core crystal" , 31 , 0 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        dr.strings.get(0)[4] = String.format( "%d" , dr.values[0] );
        a.addAll( dr.strings );
        double denominator = (( (long)dr.values[0] ) & 0xFFFFFFFFL );
        // EBX
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        dr.strings.get(0)[4] = String.format( "%d" , dr.values[0] );
        a.addAll( dr.strings );
        double numerator = (( (long)dr.values[0] ) & 0xFFFFFFFFL );
        // ECX
        dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
        if ( dr.values[0] != 0 )
            {  // Required conversion to unsigned value, note bit 31 can be "1".
            long hz = (long)(( (long)dr.values[0] ) & 0xFFFFFFFFL );
            double mHz = hz / 1000000.0; 
            dr.strings.get(0)[4] = String.format( "%.3f MHz", mHz );
            }
        else
            {
            dr.strings.get(0)[4] = "n/a";
            }
        a.addAll( dr.strings );
        double frequency = (( (long)dr.values[0] ) & 0xFFFFFFFFL );
        if(( numerator > 0 )&&( denominator > 0 )&&( frequency > 0 ))
            {
            double tscMhz = frequency * numerator / denominator / 1000000.0;
            String[] s = new String[] 
                { "TSC calculated (not measured) clock", "-", "-", "-",
                  String.format( "%.1f MHz", tscMhz ) };
            a.add( s );
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
