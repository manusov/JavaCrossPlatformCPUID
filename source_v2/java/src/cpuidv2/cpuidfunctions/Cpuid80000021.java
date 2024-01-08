/*
CPUID Utility. (C)2022 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000021h = AMD architectural differences.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

class Cpuid80000021 extends ParameterFunctionCpuid
{
Cpuid80000021()
    { setFunction( 0x80000021 ); }

@Override String getLongName()
    { return "AMD architectural differences"; }

// Control tables for results decoding
private final static String[][] DECODER_EAX =
    { { "NNDBP"     , "Processor ignores nested data breakpoints"  } ,  // bit 0
      { "FSGSNS"    , "FS,GS bases MSR writes are non serializing" } ,
      { "LAS"       , "LFENCE always serializing"    } ,            // bit 2
      { "SPCL"      , "SMM page configuration lock"  } ,            // bit 3
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "NSCB"      , "Null selector clear base"     } ,            // bit 6
      { "UAIGN"     , "Upper address ignore"         } ,
      { "AIBRS"     , "Automatic IBRS"    } ,                       // bit 8
      { "NOSCM"     , "No SMM control MSR (MSR C001_0116h is absent)" } ,
      { "FSRS"      , "Fast short REP STOSB"  } ,
      { "FSRC"      , "Fast short REPE CMPSB" } ,
      { "x"         , "Reserved"          } ,
      { "PCMSR"     , "Prefetch control MSR"  } ,                    // bit 13
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,                        // bit 16
      { "CPUIDUD"   , "CPUID disable for non-privileged software" } ,
      { "EPSF"      , "Enhanced predictive store forwarding"      } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,                        // bit 24
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } };                       // bit 31
private final static Object[][] DECODER_EBX =
    { { "Microcode patch size, 16-byte units" ,  11 ,  0 } };

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
        int n = dr.values[0];
        if ( n == 0 )
            {
            dr.strings.get(0)[4] = "at most 5568 (15C0h) bytes";
            }
        else
            {
            n = n * 16;
            dr.strings.get(0)[4] =  String.format( "%d bytes", n );
            }
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
