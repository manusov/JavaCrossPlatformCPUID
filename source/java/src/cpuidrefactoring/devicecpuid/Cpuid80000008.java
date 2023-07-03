/*
CPUID Utility. (C)2023 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000008h = Address size and physical core information.
*/

package cpuidrefactoring.devicecpuid;

import java.util.ArrayList;

class Cpuid80000008 extends ParameterFunctionCpuid
{
Cpuid80000008()
    { setFunction( 0x80000008 ); }

@Override String getLongName()
    { return "Address size and physical core information"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Physical address size"       ,  7 , 0 } ,
      { "Linear address size"         , 15 , 8 } ,
      { "Guest physical address size" , 23 , 16 } };
private final static String[][] DECODER_EBX =
    { { "CLZERO"    , "CLZERO instruction"          } ,
      { "IRC"       , "Instruction Retired Counter" } ,
      { "EPZR"      , "Error Pointer Zero-Restore"  } ,
      { "INVLPGB"   , "INVLPGB and TLBSYNC instructions" } ,         // bit 3
      { "RDPRU"     , "Read processor privileged registers at user mode" } ,
      { "x"         , "Reserved"                    } ,              // bit 5
      { "BE"        , "AMD Bandwidth Enforcement"   } ,
      { "x"         , "Reserved"                    } ,
      { "MCOMMIT"   , "MCOMMIT instruction"         } ,              // bit 8
      { "WBNOINVD"  , "Writeback with no invalidation" } ,
      { "x"         , "Reserved"                    } ,
      { "x"         , "Reserved"                    } ,
      { "IBPB"      , "Indirect branch prediction barrier" } ,       // bit 12
      { "INT WBINV" , "Interruptible WBINVD, WBNOINVD instructions" } ,
      { "IBRS"      , "Indirect branch restricted speculation"   } , // bit 14
      { "STIBP"     , "Single thread indirect branch predictor"  } , // bit 15
      { "IBRS AON"  , "Indirect branch restricted speculation always on"  } ,
      { "STIBP AON" , "Single thread indirect branch predictor always on" } ,
      { "IBRS PREF" , "Indirect branch restricted speculation is preferred" } ,
      { "IBRS SM"   , "IBRS provides same mode protection" } ,  // bit 19
      { "NO LMSLE"  , "Deprecate 64-bit segment limit, EFER.LMSLE is absent" } ,
      { "INVLPGB-N" , "INVLPGB instruction for nested pages translation" } ,
      { "x"         , "Reserved"                    } ,
      { "PPIN"      , "Protected processor inventory number" } ,
      { "SSBD"      , "Speculative Store Bypass Disable" } ,  // 24
      { "SSBD KL"   , "Speculative Store Bypass Disable, keep loads" } ,  // 25
      { "SSBD NO"   , "SSBD no longer need"              } ,              // 26
      { "CPPC"      , "Collaborative processor performance control"  } ,  // 27
      { "PSFD"      , "Predicted store forward disable"  } ,
      { "BTC NO"    , "Processor is not affected by branch type confusion" } ,
      { "IBPB RET"  , "Clear return address predictor by MSR"              } ,
      { "BR SAMP"   , "Branch sampling"                  } };         // bit 31
private final static Object[][] DECODER_ECX =
    { { "Number of threads per processor - 1" ,  7 ,  0 } ,
      { "APIC ID size"                        , 15 , 12 } ,
      { "Performance TSC size"                , 17 , 16 } };
private final static Object[][] DECODER_EDX =
    { { "Maximum page count for INVLPGB instruction" ,  15 ,  0  } ,
      { "RDPRU maximum register number"              ,  31 ,  16 } };
// Additional decoders
private final static String[] DECODER_PERF_TSC =
    { "40 bits" , "48 bits" , "56 bits" , "64 bits" , "Unknown" };
private final static String[] DECODER_RDPRU =
    { "MPERF" , "APERF" , "Unknown" };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        dr.strings.get(0)[4] = writeSize( dr.values[0] );
        dr.strings.get(1)[4] = writeSize( dr.values[1] );
        dr.strings.get(2)[4] = writeSize( dr.values[2] );
        a.addAll( dr.strings );
        a.add( interval );
        // EBX
        boolean rdpruFlag = ( entries[0].ebx & 0x10 ) > 0;
        strings = decodeBitmap( "EBX", DECODER_EBX, entries[0].ebx );
        a.addAll( strings );
        a.add( interval );
        // ECX
        dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
        if ( dr.values[0] > 0 ) 
            {
            dr.strings.get(0)[4] = ( dr.values[0] + 1 ) + " threads"; 
            }
        else
            {
            dr.strings.get(0)[4] = "n/a"; 
            }
        if ( dr.values[1] > 0 ) 
            {
            dr.strings.get(1)[4] = dr.values[1] + "-bit"; 
            }
        else
            {
            dr.strings.get(1)[4] = "n/a"; 
            }
        int index = dr.values[2];
        if ( index > 3 ) index = 4;
        dr.strings.get(2)[4] = DECODER_PERF_TSC[index];
        a.addAll( dr.strings );
        a.add( interval );
        // EDX
        dr = decodeBitfields( "EDX", DECODER_EDX, entries[0].edx );
        a.addAll( dr.strings );
        index = dr.values[1];
        if ( index > 1 ) { index = 2; }
        if   ( rdpruFlag )
            {
            dr.strings.get(1)[4] = DECODER_RDPRU[index]; 
            }
        else
            {
            dr.strings.get(1)[4] = "n/a"; 
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
