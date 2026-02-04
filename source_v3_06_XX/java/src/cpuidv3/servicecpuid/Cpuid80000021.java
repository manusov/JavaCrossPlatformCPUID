/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Extended Function 80000021h =
AMD architectural differences.

*/

package cpuidv3.servicecpuid;

import java.util.ArrayList;

class Cpuid80000021 extends ParameterFunctionCpuid
{
Cpuid80000021() { setFunction( 0x80000021 ); }

@Override String getLongName()
    { return "AMD architectural differences"; }

// Control tables for results decoding.
private final static String[][] DECODER_EAX =
    { { "NNDBP"     , "Processor ignores nested data breakpoints"  } , // bit 0
      { "FSGSNS"    , "FS,GS bases MSR writes are non serializing" } ,
      { "LAS"       , "LFENCE always serializing"                  } , // bit 2
      { "SPCL"      , "SMM page configuration lock"                } , // bit 3
      { "x"         , "Reserved"                                   } ,
      { "VERW CLR"  , "VERW is useable to mitigate TSA"            } ,
      { "NSCB"      , "Null selector clear base"                   } , // bit 6
      { "UAIGN"     , "Upper address ignore"                       } ,
      { "AIBRS"     , "Automatic IBRS"                             } , // bit 8
      { "NOSCM"     , "No SMM control MSR (MSR C001_0116h is absent)"  } ,
      { "FSRSTS"    , "Fast short REP STOSB"                       } ,
      { "FSRCMP"    , "Fast short REPE CMPSB"                      } ,
      { "PMC2LEG"   , "Performance Legacy CTL2 precise retire"     } , // bit 12
      { "PCMSR"     , "Prefetch control MSR"                       } , // bit 13
      { "L2TX32"    , "L2 TLB size encoded as x32 units"           } , // bit 14
      { "ERMSB"     , "AMD enhanced REP MOVSB/STOSB"               } , // bit 15
      { "OP0F017"   , "AMD reservation opcodes 0F 01/7"            } , // bit 16
      { "CPUIDUD"   , "CPUID disable for non-privileged software"  } ,
      { "EPSF"      , "Enhanced predictive store forwarding"       } ,
      { "FSRSCS"    , "Fast short REP SCASB"                       } ,
      { "ICPREF"    , "Instruction cache prefetch"                 } , // bit 20
      { "FP512D"    , "FP512 supports downgrading to FP256"        } ,
      { "WLHEUR"    , "Workload based heuristic to OS"             } , // bit 22
      { "A512 BMM"  , "AVX512 bit matrix multiply and bit reversal"  } ,
      { "ERAPS"     , "Enhanced return address predictor security" } , // bit 24
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "SBPB"      , "Selective branch predictor barrier"  } ,       // bit 27
      { "BRTYPE"    , "IBPB flushes all branch type predictions" } ,  // bit 28
      { "SRSO NO"   , "SRSO vulnerability absent"                } ,  // bit 29
      { "SRSO NK"   , "SRSO at user/kernel boundaries absent"    } ,  // bit 30
      { "SRSO MF"   , "Can use MSR BP_CFG to mitigate SRSO"      } }; // bit 31
private final static String[][] DECODER_ECX =
    { { "x"         , "Reserved"                                 } ,  // bit 0
      { "TSA SQ N"  , "CPU is not vulnerable to TSA-SQ"          } ,  // bit 1
      { "TSA L1 N"  , "CPU is not vulnerable to TSA-L1"          } ,  // bit 2
      { "x"         , "Reserved"                                 } }; // bit 3
private final static Object[][] DECODER_EBX =
    { { "Microcode patch size, 16-byte units" ,  15 ,   0 } ,
      { "Return address predictor size (x8)"  ,  23 ,  16 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX.
        strings = decodeBitmap( "EAX", DECODER_EAX, entries[0].eax );
        a.addAll( strings );
        a.add( interval );
        // ECX.
        strings = decodeBitmap( "ECX", DECODER_ECX, entries[0].ecx );
        a.addAll( strings );
        a.add( interval );
        // EBX.
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
