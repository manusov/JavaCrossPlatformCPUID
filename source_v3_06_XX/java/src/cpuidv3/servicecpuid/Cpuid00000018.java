/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 00000018h =
Deterministic address translation parameters.

*/

package cpuidv3.servicecpuid;

import java.util.ArrayList;

class Cpuid00000018 extends ParameterFunctionCpuid
{
Cpuid00000018() { setFunction( 0x00000018 ); }

@Override String getLongName()
    { return "Deterministic address translation parameters"; }

// Control tables for results decoding.
private final static Object[][] DECODER_EAX =
    { { "Maximum sub-leaf index" , 31 , 0 } };
private final static Object[][] DECODER_EBX =
    { { "4KB page size entries supported by this structure" ,  0 , 0  } ,
      { "2MB page size entries supported by this structure" ,  1 , 1  } ,
      { "4MB page size entries supported by this structure" ,  2 , 2  } ,
      { "1GB page size entries supported by this structure" ,  3 , 3  } ,
      { "Reserved"                                          ,  7 , 4  } ,
      { "Partitioning"                                      , 10 , 8  } ,
      { "Reserved"                                          , 15 , 11 } ,
      { "Ways of associativity"                             , 31 , 16 } };
private final static Object[][] DECODER_ECX =
    { { "Number of sets" , 31 , 0 } };
private final static Object[][] DECODER_EDX =
    { { "Translation cache type"                             ,  4 ,  0 } ,
      { "Translation cache level"                            ,  7 ,  5 } ,
      { "Fully associative structure"                        ,  8 ,  8 } ,
      { "Reserved"                                           , 13 ,  9 } ,
      { "Maximum logical CPU sharing this translation cache" , 25 , 14 } ,
      { "Reserved"                                           , 31 , 26 } };
// Additional data for parameters decode.
private final static String[] DECODER_TYPE =
    { "Unknown" , "Data TLB" , "Instruction TLB" , "Unified TLB" , 
      "Load Only TLB" , "Store Only TLB"  };
// Limit for detect invalid values.
private final static int NDT = DECODER_TYPE.length - 1;

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX, subfunction 0.
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        a.addAll( dr.strings );
        int maxSubFunction = entries[0].eax;
        for( int i=0; i<entries.length; i++ )
            {
            if ( i > maxSubFunction ) break;
            // EDX, per subfunctions, detect subfunction validity and type.
            dr = decodeBitfields( "EDX", DECODER_EDX, entries[i].edx );
            int j = dr.values[0];    // j = TLB type.
            if ( j == 0 ) continue;  // Skip if not valid entry.
            if ( j > NDT ) j = 0;
            dr.strings.get(0)[4] = DECODER_TYPE[j];
            dr.strings.get(1)[4] = "L" + dr.values[1];
            a.add( interval );
            a.addAll( dr.strings );
            // ECX for valid subfunction.
            dr = decodeBitfields( "ECX", DECODER_ECX, entries[i].ecx );
            a.addAll( dr.strings );
            // EBX for valid subfunction.
            dr = decodeBitfields( "EBX", DECODER_EBX, entries[i].ebx );
            a.addAll( dr.strings );
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
