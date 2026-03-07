/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Extended Function 80000025h =
AMD reverse map table (RMP) information.

*/

package cpuidv3.servicecpuid;

import java.util.ArrayList;

class Cpuid80000025 extends ParameterFunctionCpuid
{
Cpuid80000025() { setFunction( 0x80000025 ); }

@Override String getLongName()
    { return "AMD reverse map table (RMP) information"; }

// Control tables for results decoding.
private final static Object[][] DECODER_EAX =
    { { "Minimum supported RMP segment size" ,  5 , 0 } ,
      { "Maximum supported RMP segment size" , 11 , 6 } };

private final static Object[][] DECODER_EBX =
    { { "Number of cacheable RMP segments"                ,  9 ,  0 } ,
      { "Flag: number of cached segments is a hard limit" , 10 , 10 } };

private final static String[][] DECODER_EDX =
    { { "RMPOPT"    , "RMPOPT feature and RMPOPT instruction"      } , // bit 0
      { "x"         , "Reserved"          } ,   // bit 1
      { "RMP DRT"   , "RMP dirty feature and RMPCHKD instruction"  } , // bit 2
      { "x"         , "Reserved"          } };  // bit 3

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX.
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        a.addAll( dr.strings );
        a.add( interval );
        // EBX.
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        a.addAll( dr.strings );
        a.add( interval );
        // EDX.
        strings = decodeBitmap( "EDX", DECODER_EDX, entries[0].edx );
        a.addAll( strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
