/*
CPUID Utility. (C)2022 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000022h = AMD core and northbridge performance monitoring.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

class Cpuid80000022 extends ParameterFunctionCpuid
{
Cpuid80000022()
    { setFunction( 0x80000022 ); }

@Override String getLongName()
    { return "AMD extended performance monitoring and debug"; }

// Control tables for results decoding
private final static String[][] DECODER_EAX =
    { { "PMV2"    , "Performance monitoring, v2" } ,    // bit 0
      { "LBRST"   , "Last branch record stack"   } ,
      { "LBRFRZ"  , "Freezing core performance counters and LBR stack"  } };   // bit 2
private final static Object[][] DECODER_EBX =
    { { "Number of core performance monitor counters"        ,   3 ,  0 } ,
      { "Number of last branch record stack entries"         ,   9 ,  4 } ,
      { "Number of northbridge performance monitor counters" ,  15 , 10 } ,
      { "Number of Unified Memory Controllers (UMC) PMCs"    ,  21 , 16 } };
private final static Object[][] DECODER_ECX =
    { { "Active Unified Memory Controllers (UMC) mask"       ,  31 ,  0 } };


@Override String[][] getParametersList()
    {
    DecodeReturn dr;
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
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        dr.strings.get(0)[4] = String.format( "%d", dr.values[0] );
        dr.strings.get(1)[4] = String.format( "%d", dr.values[1] );
        dr.strings.get(2)[4] = String.format( "%d", dr.values[2] );
        a.addAll( dr.strings );
        // ECX
        dr = decodeBitfields
            ( "ECX", DECODER_ECX, entries[0].ecx );
            a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
