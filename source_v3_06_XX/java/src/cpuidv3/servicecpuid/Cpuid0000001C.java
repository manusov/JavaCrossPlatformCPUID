/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 0000001Ch =
Enumeration of Architectural LBR Capabilities.

*/

package cpuidv3.servicecpuid;

import java.util.ArrayList;

class Cpuid0000001C extends ParameterFunctionCpuid
{
Cpuid0000001C() { setFunction( 0x0000001C ); }

@Override String getLongName()
    { return "Enumeration of Architectural LBR Capabilities"; }

// Control tables for results decoding.
private final static Object[][] DECODER_EAX =
    { { "IP values contain LIP"                       , 31 , 31 } ,
      { "Deep C-state reset"                          , 30 , 30 } ,
      { "Supported LBR depth values, x8 units bitmap" ,  7 ,  0 } };
private final static String[][] DECODER_EBX =
    { { "CPL FLTR"   , "CPL filtering" } ,
      { "BRN FLTR"   , "Branch filtering" } ,
      { "CST MODE"   , "Call-stack mode" } };
private final static String[][] DECODER_ECX =
    { { "MISP BIT"   , "Indication of branch misprediction" } ,        // bit 0
      { "TIME LBR"   , "Hold CPU cycles since last LBR entry" } ,
      { "BR TYPE"    , "Hold indication of the recorded operation" } , // bit 2
      { "x"          , "Reserved" } ,  // bit 3
      { "x"          , "Reserved" } ,
      { "x"          , "Reserved" } ,
      { "x"          , "Reserved" } ,
      { "x"          , "Reserved" } ,
      { "x"          , "Reserved" } ,  // bit 8
      { "x"          , "Reserved" } ,  // bit 9
      { "x"          , "Reserved" } ,
      { "x"          , "Reserved" } ,
      { "x"          , "Reserved" } ,
      { "x"          , "Reserved" } ,
      { "x"          , "Reserved" } ,
      { "x"          , "Reserved" } ,  // bit 15
      { "EVTLOG[0]"  , "Event logging supported bitmap [0]" } ,  // bit 16
      { "EVTLOG[1]"  , "Event logging supported bitmap [1]" } ,
      { "EVTLOG[2]"  , "Event logging supported bitmap [2]" } ,
      { "EVTLOG[3]"  , "Event logging supported bitmap [3]" } };  // bit 19

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        a.addAll( dr.strings );
        // EBX
        strings = decodeBitmap( "EBX", DECODER_EBX, entries[0].ebx );
        a.addAll( strings );
        // ECX
        strings = decodeBitmap( "ECX", DECODER_ECX, entries[0].ecx );
        a.addAll( strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
