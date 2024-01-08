/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
0000001Bh = PCONFIG instruction information.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

class Cpuid0000001B extends ParameterFunctionCpuid
{
Cpuid0000001B()
    { setFunction( 0x0000001B ); }

@Override String getLongName()
    { return "PCONFIG instruction information"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Sub-leaf type" , 11 , 0 } }; 
private final static Object[][] DECODER_EBX =
    { { "Identifier of target [3n+1]" , 31 , 0 } }; 
private final static Object[][] DECODER_ECX =
    { { "Identifier of target [3n+2]" , 31 , 0 } }; 
private final static Object[][] DECODER_EDX =
    { { "Identifier of target [3n+3]" , 31 , 0 } }; 

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        EntryCpuid[] entries1 = container.buildEntries( 0x00000007 );
        if ( ( entries1 != null )&&( entries1.length > 0 )&&
             ( ( entries1[0].edx & ( 1 << 18 ) ) != 0 ) )
            {
            for( int i=0; i<entries.length; i++ )
                {
                if ( i != 0 ) a.add( interval );
                // EAX, subfunction 0
                dr = decodeBitfields( "EAX", DECODER_EAX, entries[i].eax );
                dr.strings.get(0)[4] = String.format( "%d", dr.values[0] );
                a.addAll( dr.strings );
                // EBX, subfunction 0
                dr = decodeBitfields( "EBX", DECODER_EBX, entries[i].ebx );
                dr.strings.get(0)[4] = String.format( "%d", dr.values[0] );
                a.addAll( dr.strings );
                // ECX, subfunction 0
                dr = decodeBitfields( "ECX", DECODER_ECX, entries[i].ecx );
                dr.strings.get(0)[4] = String.format( "%d", dr.values[0] );
                a.addAll( dr.strings );
                // EDX, subfunction 0
                dr = decodeBitfields( "EDX", DECODER_EDX, entries[i].edx );
                dr.strings.get(0)[4] = String.format( "%d", dr.values[0] );
                a.addAll( dr.strings );
                }
            }
        else
            {
            a.add( new String[] 
                { "-" , "-" , "-" , "-" , "PCONFIG not supported" } );
                
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
