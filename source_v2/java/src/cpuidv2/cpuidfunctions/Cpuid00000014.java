/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2024.
-------------------------------------------------------------------------------
Class for support CPUID Standard Function 00000014h =
Intel processor trace enumeration information.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

class Cpuid00000014 extends ParameterFunctionCpuid
{
Cpuid00000014()
    { setFunction( 0x00000014 ); }

@Override String getLongName()
    { return "Intel processor trace enumeration information"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX_SUBFUNCTION_0 =
    { { "Maximum sub-leaf number" , 31 , 0 } };
private final static String[][] DECODER_EBX_SUBFUNCTION_0 =
    { { "FILTER"    , "IA32_RTIT_CTL.CR3Filter can be set"            } ,
      { "CPSB"      , "Configurable PSB and Cycle-Accurate Mode"      } ,
      { "IP Filter" , "IP filtering, TraceStop filtering, warm pres." } ,
      { "MTC"       , "MTC timing packet and suppresion of COFI"      } ,
      { "PTWRITE"   , "PTWRITE can generate packets"                  } ,
      { "PET"       , "Power event trace"                             } ,
      { "PSB PMI"   , "PSB and PMI preservation"                      } ,
      { "PT ET"     , "PT event trace"                                } ,
      { "TNT DIS"   , "Disabling TNT (Taken Not Taken) packets"       } ,
      { "PTTT"      , "Processor trace trigger tracing"               } }; // bit 9
private final static String[][] DECODER_ECX_SUBFUNCTION_0 =
    { { "TR"        , "Tracing can be enabled with IA32_RTIT_CTL.ToPA" } ,
      { "ToPA"      , "ToPA tables can hold any number of output entries" } ,
      { "SROS"      , "Single-Range Output scheme" } ,
      { "OTTS"      , "Output to Trace transport subsystem" } ,
      { "x"         , "Reserved" } ,  // bit 4 reserved
      { "x"         , "Reserved" } ,  // ...
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,  // ...
      { "x"         , "Reserved" } ,  // bit 30 reserved
      { "LIP"       , "Generated packets include CS base component" } };
private final static Object[][] DECODER_EAX_SUBFUNCTION_1 =
    { { "Number of configurable address ranges for filtering" ,  2 , 0  } ,
      { "Number of IA32_RTIT_TRIGGERx_CFG MSRs"               , 10 , 8  } ,
      { "Bitmap of supported MTC period encodings"            , 31 , 16 } };
private final static Object[][] DECODER_EBX_SUBFUNCTION_1 =
    { { "Bitmap of supported Cycle threshold value encodings"      , 15 , 0  } ,
      { "Bitmap of supported Configurable PSB frequency encodings" , 31 , 16 } };
private final static String[][] DECODER_ECX_SUBFUNCTION_1 =
    { { "TAA"       , "Trigger action attribution" } ,                  // bit 0
      { "TAPR"      , "Trigger action TRACE_PAUSE and TRACE_RESUME" } , // bit 1
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,
      { "x"         , "Reserved" } ,                // bit 14
      { "TIDR"      , "Trigger input DR match" } }; // bit 15

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX, subfunction 0
        dr = decodeBitfields
            ( "EAX", DECODER_EAX_SUBFUNCTION_0, entries[0].eax );
        // EBX, subfunction 0
        a.addAll( dr.strings );
        strings = decodeBitmap
            ( "EBX", DECODER_EBX_SUBFUNCTION_0, entries[0].ebx );
        a.add( interval );
        a.addAll( strings );
        // ECX, subfunction 0
        strings = decodeBitmap
            ( "ECX", DECODER_ECX_SUBFUNCTION_0, entries[0].ecx );
        a.add( interval );
        a.addAll( strings );
        if ( ( entries.length > 1 )&&( entries[1].subfunction == 1 ) )
            {
            // EAX, subfunction 1
            dr = decodeBitfields
                ( "EAX", DECODER_EAX_SUBFUNCTION_1, entries[1].eax );
            a.add( interval );
            a.addAll( dr.strings );
            // EBX, subfunction 1
            dr = decodeBitfields
                ( "EBX", DECODER_EBX_SUBFUNCTION_1, entries[1].ebx );
            a.add( interval );
            a.addAll( dr.strings );
            strings = decodeBitmap
                ( "ECX", DECODER_ECX_SUBFUNCTION_1, entries[1].ecx );
            a.add( interval );
            a.addAll( strings );
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
