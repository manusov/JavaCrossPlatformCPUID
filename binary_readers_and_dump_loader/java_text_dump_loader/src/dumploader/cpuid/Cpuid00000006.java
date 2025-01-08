/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 00000006h =
Thermal and power management features.

*/

package dumploader.cpuid;

import java.util.ArrayList;

class Cpuid00000006 extends ParameterFunctionCpuid
{
Cpuid00000006()
    { setFunction( 0x00000006 ); }

@Override String getLongName()
    { return "Thermal and power management features"; }

// Control tables for results decoding
private final static String[][] DECODER_EAX =
    { { "DTS"      , "Digital temperature sensor" } ,
      { "TB"       , "Intel Turbo Boost technology" } ,
      { "ARAT"     , "APIC timer always running" } ,
      { "x"        , "Reserved" } ,  // bit 3 reserved
      { "PLN"      , "Power limit notification controls" } ,
      { "ECMD"     , "Clock modulation duty cycle extension" } ,
      { "PTM"      , "Package thermal management" } ,
      { "HWP-B"    , "Hardware ctrl. perf. states base registers" } ,
      { "HWP-N"    , "HWP notification" } ,
      { "HWP-W"    , "HWP activity window" } ,
      { "HWP-E"    , "HWP energy performance preference" } ,
      { "HWP-P"    , "HWP package level request" } ,
      { "x"        , "Reserved" } ,  // bit 12 reserved
      { "HDC"      , "Hardware duty cycling base register" } ,
      { "TB30"     , "Intel Turbo Boost Max Technology 3.0 available" } ,
      { "HWP"      , "Highest performance change" } ,  // bit 15
      { "HWP PECI" , "HWP PECI override" } ,
      { "FLEX HWP" , "Flexible HWP" } ,  // bit 17
      { "FAST HWP" , "Fast access mode for the IA32_HWP_REQUEST MSR" } ,
      { "HW FB"    , "HW Feedback with thermal status" } ,  // bit 18
      { "IGN HWP"  , "Ignoring idle logical processor HWP request" } ,
      { "x"        , "Reserved" } ,  // bit 21
      { "HWP MSR"  , "IA32_HWP_CTL MSR available" } ,            // bit 22
      { "EHFI"     , "Enhanced hardware feedback interface" } ,  // bit 23
      { "HFN"      , "Hardware feedback notification" } ,        // bit 24
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } };  // bit 31
private final static Object[][] DECODER_EBX =
    { { "Number of interrupt thresholds in digital thermal sensor" , 3 , 0 } }; 
private final static int SUPPORT_COUNT_ECX = 3;
private final static Object[][] DECODER_ECX =
    { { "Hardware coordination feedback capability, MPERF, APERF"  , 0 , 0 } ,
      { "ACNT2"                                                    , 1 , 1 } ,
      { "Performance-energy bias preference, ENERGY_PERF_BIAS MSR" , 3 , 3 } ,
      { "EHFI classes count"                                     , 15 ,  8 } ,
      { "Reserved"                                               , 31 , 16 } };
private final static Object[][] DECODER_EDX =
    { { "Performance capability reporting"                       ,  0 , 0  } ,
      { "Energy efficiency capability reporting"                 ,  1 , 1  } ,
      { "Size of the hardware feedback interface structure"      , 11 , 8  } ,
      { "Index row in the hardware feedback interface structure" , 31 , 16 } };

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
        dr = decodeBitfields ( "EBX", DECODER_EBX, entries[0].ebx );
        dr.strings.get(0)[4] = String.format( "%d", dr.values[0] );
        a.addAll( dr.strings );
        a.add( interval );
        // ECX
        dr = decodeBitfields ( "ECX", DECODER_ECX, entries[0].ecx );
        for( int i=0; i<SUPPORT_COUNT_ECX; i++ )
            {
            dr.strings.get(i)[4] = 
                ( dr.values[i] != 0 ) ? "supported" : "not supported";
            }
        a.addAll( dr.strings );
        a.add( interval );
        // EDX
        dr = decodeBitfields ( "EDX", DECODER_EDX, entries[0].edx );
        dr.strings.get(0)[4] = 
            ( dr.values[0] != 0 ) ? "supported" : "not supported";
        dr.strings.get(1)[4] = 
            ( dr.values[1] != 0 ) ? "supported" : "not supported";
        dr.strings.get(2)[4] = "" + ( dr.values[2] + 1 ) * 4 + "K";
        dr.strings.get(3)[4] = "" + dr.values[3];
        a.addAll( dr.strings );
        }
        return a.isEmpty() ? 
            super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
