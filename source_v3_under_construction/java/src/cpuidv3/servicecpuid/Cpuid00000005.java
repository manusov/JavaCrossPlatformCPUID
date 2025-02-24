/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 00000005h = MONITOR/MWAIT features.

*/

package cpuidv3.servicecpuid;

import java.util.ArrayList;

class Cpuid00000005 extends ParameterFunctionCpuid
{
Cpuid00000005()
    { setFunction( 0x00000005 ); }

@Override String getLongName()
    { return "MONITOR/MWAIT features"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Smallest monitor line size" , 15 , 0 } };
private final static Object[][] DECODER_EBX =
    { { "Largest monitor line size" , 15 , 0 } };
private final static Object[][] DECODER_ECX =
    { { "Enumeration of MONITOR-MWAIT extensions flag"       , 0 , 0 } ,
      { "Interrupt break event for MWAIT (even if disabled)" , 1 , 1 } ,
      { "Monitorless MWAIT instruction feature"              , 3 , 3 } };
private final static Object[][] DECODER_EDX =
    { { "Number of C0 sub C-states supported using MWAIT" ,  3 ,  0 } ,
      { "Number of C1 sub C-states supported using MWAIT" ,  7 ,  4 } ,
      { "Number of C2 sub C-states supported using MWAIT" , 11 ,  8 } ,
      { "Number of C3 sub C-states supported using MWAIT" , 15 , 12 } ,
      { "Number of C4 sub C-states supported using MWAIT" , 19 , 16 } ,
      { "Number of C5 sub C-states supported using MWAIT" , 23 , 20 } ,
      { "Number of C6 sub C-states supported using MWAIT" , 27 , 24 } ,
      { "Number of C7 sub C-states supported using MWAIT" , 31 , 28 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        dr.strings.get(0)[4] = String.format( "%d Bytes", dr.values[0] );
        a.addAll( dr.strings );
        // EBX
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        dr.strings.get(0)[4] = String.format( "%d Bytes", dr.values[0] );
        a.addAll( dr.strings );
        // ECX
        dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
        for( int i=0; i<dr.values.length; i++ )
            {
            dr.strings.get(i)[4] = String.format( "%d", dr.values[i] );
            }
        a.addAll( dr.strings );
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
