/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2025.
-------------------------------------------------------------------------------
Class for support CPUID Standard Function 00000029h =
Intel APX (Advanced Performance Extensions) sub-features.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

class Cpuid00000029 extends ParameterFunctionCpuid
{
Cpuid00000029()
    { setFunction( 0x00000029 ); }

@Override String getLongName()
    { return "Intel APX sub-features"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX_SUBFUNCTION_0 =
    { { "Maximum sub-leaf number" , 31 , 0 } }; 
private final static String[][] DECODER_EBX_SUBFUNCTION_0 =
    { { "APX NI"  , "Intel APX controls for NCI, NDD, NF" } ,  // bit 0
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,  // bit 8
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,  // bit 16
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,  // bit 24
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } };  // bit 31

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    // subfunction 0
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX, subfunction 0
        dr = decodeBitfields
            ( "EAX", DECODER_EAX_SUBFUNCTION_0, entries[0].eax );
        a.addAll( dr.strings );
        a.add( interval );
        // EBX, subfunction 0
        strings = decodeBitmap
            ( "EBX", DECODER_EBX_SUBFUNCTION_0, entries[0].ebx );
        a.addAll( strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
