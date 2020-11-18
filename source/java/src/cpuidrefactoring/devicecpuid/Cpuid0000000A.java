/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
0000000Ah = Architectural performance monitoring features.
*/

package cpuidrefactoring.devicecpuid;

import java.util.ArrayList;

class Cpuid0000000A extends ParameterFunctionCpuid
{
Cpuid0000000A()
    { setFunction( 0x0000000A ); }

@Override String getLongName()
    { return "Architectural performance monitoring features"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Version ID of architectural performance monitoring" , 7, 0 } ,
      { "Number of GP PMC per logical CPU" , 15 , 8 } ,
      { "Bit width of GP PMC" , 23 , 16 } ,
      { "Length of bit vector to enumerate architectural PM events" , 31 , 24 } };
private final static Object[][] DECODER_EBX =
    { { "Core cycle event not available flag", 0, 0 } ,
      { "Instruction retired event not available flag", 1, 1 } ,
      { "Reference cycles event not available flag", 2, 2 } ,
      { "Last level cache reference event not available flag", 3, 3 } ,
      { "Last level cache misses event not available flag", 4, 4 } ,
      { "Branch instruction retired event not available flag", 5, 5 } ,
      { "Branch mispredict retired event not available flag", 6, 6 } ,
      { "Top-down slots event not available flag", 7, 7 } };
private final static Object[][] DECODER_ECX =
    { { "Fixed counters bitmap" , 31, 0 } };
private final static Object[][] DECODER_EDX =
    { { "Number of fixed-function performance counters" , 4, 0 } ,
      { "Bit width of fixed-function performance counters" , 12, 5 } ,
      { "Any thread deprecation" , 15 , 15 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        for( int i=0; i<dr.values.length; i++ )
            {
            dr.strings.get(i)[4] = String.format( "%d", dr.values[i] );
            }
        a.addAll( dr.strings );
        a.add( interval );
        // EBX
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        a.addAll( dr.strings );
        a.add( interval );
        // ECX
        dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
        a.addAll( dr.strings );
        a.add( interval );
        // EDX
        dr = decodeBitfields( "EDX", DECODER_EDX, entries[0].edx );
        for( int i=0; i<dr.values.length; i++ )
            {
            dr.strings.get(i)[4] = String.format( "%d", dr.values[i] );
            }
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
