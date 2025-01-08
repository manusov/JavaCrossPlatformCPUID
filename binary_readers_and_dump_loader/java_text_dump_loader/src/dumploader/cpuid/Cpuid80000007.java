/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Extended Function 80000007h = 
Advanced power management information.

*/

package dumploader.cpuid;

import java.util.ArrayList;

class Cpuid80000007 extends ParameterFunctionCpuid
{
Cpuid80000007()
    { setFunction( 0x80000007 ); }

@Override String getLongName()
    { return "Advanced power management information"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Maximum wrap time, ms"               , 31 , 16 } ,
      { "Version"                             , 15 , 8  } ,
      { "Number of monitors MSR C001_008[01]" , 7 , 0 } };
private final static String[][] DECODER_EBX =
    { { "MCAOVR"   , "MCA overflow recovery" } ,
      { "SUCCOR"   , "Software uncorrectable error containment and recovery" } ,
      { "HWA"      , "Hardware assert MSR C001_10[DF...C0]h" } ,
      { "SCMCA"    , "Scalable MCA" } ,
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } };
private final static Object[][] DECODER_ECX =
    { { "Ratio of power accumulator sample period to GTSC" , 31 , 0 } };
private final static String[][] DECODER_EDX =
    { { "TS"        , "Temperature sensor" } ,
      { "FID"       , "Frequency ID control" } ,
      { "VID"       , "Voltage ID control"   } ,
      { "TTP"       , "THERMTRIP"            } ,
      { "TM"        , "Hardware thermal control" } ,
      { "STC"       , "Software thermal control" } ,
      { "100 MHz"   , "100 MHz steps for multiplier control" } ,
      { "HwPstate"  , "Hardware P-state control" } ,
      { "INV TSC"   , "TSC invariant for P-States and C-States" } ,
      { "CPB"       , "Core performance boost" } ,
      { "EffFreqRO" , "Read only effective frequency interface" } ,
      { "PFI"       , "Processor feedback interface (deprecated)" } ,
      { "PPR"       , "Processor core power reporting interface" } ,
      { "CSB"       , "Connected standby" } ,
      { "RAPL"      , "Running average power limit" } ,
      { "FastCPPC"  , "Fast collaborative processor performance control" } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EDX
        strings = decodeBitmap( "EDX", DECODER_EDX, entries[0].edx );
        a.addAll( strings );
        a.add( interval );
        // EAX
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        a.addAll( dr.strings );
        a.add( interval );
        // EBX
        strings = decodeBitmap( "EBX", DECODER_EBX, entries[0].ebx );
        a.addAll( strings );
        a.add( interval );
        // ECX
        dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
