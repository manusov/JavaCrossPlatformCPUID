/*
CPUID Utility. (C)2021 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000022h = Reserved (undocumented) function.
*/

package cpuidrefactoring.devicecpuid;

import java.util.ArrayList;

class Cpuid80000022 extends ParameterFunctionCpuid
{
Cpuid80000022()
    { setFunction( 0x80000022 ); }

@Override String getLongName()
    { return "AMD extended performance monitoring and debug"; }

// Control tables for results decoding
private final static Object[][] DECODER_EBX =
    { { "Number of core performance monitor counters"        ,   3 ,  0 } ,
      { "Number of northbridge performance monitor counters" ,  15 , 10 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EBX
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        dr.strings.get(0)[4] = String.format( "%d", dr.values[0] );
        dr.strings.get(1)[4] = String.format( "%d", dr.values[1] );
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
