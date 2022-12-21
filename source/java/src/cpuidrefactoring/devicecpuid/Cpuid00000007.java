/*
CPUID Utility. (C)2022 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
00000007h = Structured extended feature enumeration.
*/

package cpuidrefactoring.devicecpuid;

import java.util.ArrayList;

class Cpuid00000007 extends ParameterFunctionCpuid
{
Cpuid00000007()
    { setFunction( 0x00000007 ); }

@Override String getLongName()
    { return "Structured extended feature enumeration"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX_SUBFUNCTION_0 =
    { { "Maximum sub-leaf number" , 31 , 0 } }; 
private final static String[][] DECODER_EBX_SUBFUNCTION_0 =
    { { "FSGSBASE"   , "FS,GS base addressing modes" } ,
      { "TSCADJ"     , "IA32_TSC_ADJUST MSR" } ,
      { "SGX"        , "Software guard extensions" } ,
      { "BMI1"       , "Bit manipulation instruction set #1" } ,
      { "HLE"        , "Hardware lock ellision" } ,
      { "AVX2"       , "Advanced vector extension #2" } ,
      { "FEOx87"     , "x87 FPU data pointer updated only on x87 exceptions" } ,
      { "SMEP"       , "Supervisor mode execution prevention" } ,
      { "BMI2"       , "Bit manipulation instruction set #2" } ,
      { "EMOVSSTOS"  , "Enhanced REP MOVSB/STOSB" } ,
      { "INVPCID"    , "Invalidate process context INVPCID instruction" } ,
      { "RTM"        , "Restricted transactional memory" } ,
      { "PQM"        , "Platform quality of service (RDT) monitoring" } ,  // bit 12
      { "DFPUCSDS"   , "Deprecates FPU CS and FPU DS if 1" } ,
      { "MPX"        , "Memory protection extensions" } ,
      { "PQE"        , "Platform quality of service (RDT) enforcement" } ,  // bit 15
      { "AVX512F"    , "AVX512 foundation" } ,
      { "AVX512DQ"   , "AVX512 doublewords and quadwords operations" } ,
      { "RDSEED"     , "Instruction RDSEED, alternative access to RND" } ,
      { "ADX"        , "Instruction set ADX" } ,
      { "SMAP"       , "Supervisor mode access prevention" } ,
      { "AVX512IFMA" , "AVX512 integer fused multiply and add" } ,
      { "PCOMMIT"    , "Instruction PCOMMIT for cache-NVRAM coherency" } ,
      { "CLFLUSHOPT" , "Instruction CLFLUSHOPT, optimized cache flush" } ,
      { "CLWB"       , "Instruction CLWB, cache line writevack without flush" } ,
      { "IPT"        , "Intel processor trace" } ,
      { "AVX512PF"   , "AVX512 prefetch" } ,
      { "AVX512ER"   , "AVX512 exponential and reciprocal" } ,
      { "AVX512CD"   , "AVX512 conflict detection" } ,
      { "SHA"        , "Secure hash algorithm" } ,
      { "AVX512BW"   , "AVX512 bytes and words operations" } ,
      { "AVX512VL"   , "AVX512 vector length control" } };
private final static String[][] DECODER_ECX_SUBFUNCTION_0 =
    { { "PWT1"         , "Instruction PREFETCHWT1" } ,
      { "AVX512VBMI"   , "AVX512 vector byte manipulation" } ,
      { "UMIP"         , "User mode instruction prevention" } ,
      { "PKU"          , "Protection keys for user-mode pages" } ,
      { "OSPKE"        , "OS has set CR4.PKE to enable prot. keys, RDPKRU/WRPKRU" } ,
      { "WaitPKG"      , "Wait and pause enhancements" } ,
      { "AV512VBMI2"   , "AVX512 vector byte manipulation v2" } ,
      { "CET SS"       , "Control Flow Enforcement: Shadow Stacks" } ,
      { "GFNI"         , "Galois field numeric instructions" } ,
      { "VAES"         , "Vector advanced encryption standard" } ,
      { "VPCLMULQDQ"   , "Carry-less multiplication quadword instruction" } ,
      { "AVXV512VNNI"  , "AVX512 vector neural network instructions" } ,
      { "AVX512BITALG" , "AVX512 bit algorithms" } ,
      { "TME"          , "Total Memory Encryption" } ,
      { "AVX512PDQ"    , "AVX512 VPOPCNTDQ instruction, count number of set bits" } ,
      { "x"            , "Reserved" } , // bit 15 reserved
      { "5LP"          , "5-level paging and 57-bit linear address" } ,
      { "MAWAU[0]"     , "MPX address width adjust" } ,
      { "MAWAU[1]"     , "MPX address width adjust" } ,
      { "MAWAU[2]"     , "MPX address width adjust" } ,
      { "MAWAU[3]"     , "MPX address width adjust" } ,
      { "MAWAU[4]"     , "MPX address width adjust" } ,
      { "RDPID"        , "Read processor ID" } ,
      { "AES KL"       , "AES Key Locker instructions" } ,    // bit 23
      { "BUSLOCK"      , "Bus lock debug exception" } ,
      { "CLDEMOTE"     , "Cache line demote instruction" } ,  // bit 25 reserved
      { "x"            , "Reserved" } ,
      { "MOVDIRI"      , "Direct stores by MOVDIRI instruction" } ,    // 27
      { "MOVDIR64B"    , "Direct stores by MOVDIR64B instruction" } ,  // 28
      { "ENQCMD"       , "Enqueue stores by ENQCMD and ENQCMDS instructions" } ,
      { "SGX LC"       , "SGX launch configuration" } ,
      { "PKS"          , "Protection keys for supervisor-mode pages" } };   // bit 31
private final static String[][] DECODER_EDX_SUBFUNCTION_0 =
    { { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "4VNNIW"       , "AVX512_4VNNIW 4-iteration VNNI, word mode" } ,
      { "4FMAPS"       , "AVX512_4FMAPS 4-iteration FMA, single precision" } ,
      { "FS REP MOV"   , "Fast short REP MOV" } ,
      { "UINTR"        , "User interrupts" } ,      // bit 5
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "AVX512VP2IS"  , "AVX512 compute intersection instructions" } ,
      { "SRBDS"        , "Special Register Buffer Data Sampling mitigation MSR" } ,  // bit 9
      { "MD CLEAR"     , "CPU is not affected by microarch. data sampling (MDS)" } ,
      { "RTM AA"       , "RTM always abort mode" } ,  // bit 11
      { "x"            , "Reserved" } ,               // bit 12
      { "TSX FA MSR"   , "TSX force abort MSR" } ,    // bit 13
      { "SERIALIZE"    , "SERIALIZE instruction" } ,  // bit 14
      { "HYBRID"       , "Processor is identified as a hybrid part" } ,  // bit 15
      { "TSXLDTRK"     , "XRESLDTRK and XSUSLDTRK instructions" } ,      // bit 16
      { "x"            , "Reserved" } ,
      { "PCONFIG"      , "PCONFIG for MK-TME" } ,  // bit 18
      { "ARCH LBR"     , "Architectural Last Branch Records (LBRs)" } ,
      { "CET IBT"      , "Control Flow Enforcement: Indirect Branch Tracking" } ,
      { "x"            , "Reserved" } ,            // bit 21
      { "AMX BF16"     , "Advanced Matrix Extensions, operations on BFLOAT16 numbers" } ,
      { "FP16"         , "Floating point 16-bit format" } ,  // bit 23
      { "AMX TILE"     , "Advanced Matrix Extensions, supports Tile Architecture" } ,
      { "AMX INT8"     , "Advanced Matrix Extensions, operations on INT8 numbers" } ,
      { "IBRS IBPB"    , "Indirect branch restricted speculation and predictor barrier" } ,  // bit 26
      { "STIBP"        , "Single thread indirect branch predictor" } ,  // bit 27
      { "L1D FLUSH"    , "L1 Data Cache (L1D) flush, IA32_FLUSH_CMD MSR" } ,
      { "ACAP MSR"     , "IA32_ARCH_CAPABILITIES MSR" } ,        // bit 29
      { "CCAP MSR"     , "IA32_CORE_CAPABILITIES MSR" } ,
      { "SSBD"         , "Speculative Store Bypass Disable" } };
private final static String[][] DECODER_EAX_SUBFUNCTION_1 =
    { { "x"            , "Reserved" } ,  // bit 0
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "RAO INT"      , "Register atomical operations for integer" } ,
      { "AVX VNNI"     , "AVX versions of Vector Neural Network instructions" } ,  // bit 4
      { "AVX512BF16"   , "VNNI instructions supports BFLOAT16 format" } ,
      { "x"            , "Reserved" } ,  // bit 6
      { "CMPccXADD"    , "Compare and add if condition (cc) is met" } ,
      { "ARC PM EXT"   , "Architectural performance monitoring extended leaf 23h" } ,
      { "x"            , "Reserved" } ,
      { "FAST M"       , "Fast zero-length MOVSB" } ,   // bit 10
      { "FAST S"       , "Fast short STOSB" } ,         // bit 11
      { "FAST C S"     , "Fast short CMPSB, SCASB" } ,  // bit 12
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,  // bit 16
      { "FRED"         , "Flexible return and event delivery" } ,
      { "LKGS"         , "Load into IA32 kernel GS base, LKGS instruction" } ,
      { "WRMSRNS"      , "Non serializing write to MSR" } ,
      { "x"            , "Reserved" } ,
      { "AMX FP16"     , "Advanced Matrix Extensions, operations on FP16 numbers" } ,
      { "HRESET"       , "Processor history reset CPUID leaf 20h" } ,  // bit 22
      { "AVX IFMA"     , "AVX (128/256) version for IFMA instructions" } ,
      { "x"            , "Reserved" } ,  // bit 24
      { "x"            , "Reserved" } ,
      { "LAM"          , "Linear Address Masking" } ,  // bit 26
      { "MSRLIST"      , "Read and write MSR by lists" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } };  // bit 31
private final static String[][] DECODER_EBX_SUBFUNCTION_1 =
    { { "PPIN"         , "Protected Processor Inventory Number" } };  // bit 0
private final static String[][] DECODER_EDX_SUBFUNCTION_1 =
    { { "x"            , "Reserved" } ,  // bit 0
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "AVX VNNI 8"   , "AVX (128/256) version for VNNI INT8 instructions" } ,
      { "AVX CONV"     , "AVX (128/256) version for convert BF16, FP16, FP32" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "PREFETCHIT"   , "Prefetch hints for instruction cache" } ,
      { "x"            , "Reserved" } };  // bit 15
private final static String[][] DECODER_EDX_SUBFUNCTION_2 =
    { { "PSFD"         , "Can disable FSFP without disabling SSB" } ,  // bit 0
      { "IPRED"        , "Can disable indirect predictor"         } ,  // bit 1
      { "RRSBA"        , "Can disable restricted RSB alternate enumeration" } ,
      { "x"            , "Reserved" } ,
      { "BHI"          , "Can prevent branch history injection attack"      } ,
      { "MCDT"         , "Not exhibit MXCSR configuration dependent timing" } ,
      { "x"            , "Reserved" } ,  // bit 6
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } };  // bit 15

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
        dr.strings.get(0)[4] = String.format( "%d", dr.values[0] );
        int maxSubFunction = dr.values[0];
        a.addAll( dr.strings );
        a.add( interval );
        // EBX, subfunction 0
        strings = decodeBitmap
            ( "EBX", DECODER_EBX_SUBFUNCTION_0, entries[0].ebx );
        a.addAll( strings );
        a.add( interval );
        // ECX, subfunction 0
        strings = decodeBitmap
            ( "ECX", DECODER_ECX_SUBFUNCTION_0, entries[0].ecx );
        a.addAll( strings );
        a.add( interval );
        // EDX, subfunction 0
        strings = decodeBitmap
            ( "EDX", DECODER_EDX_SUBFUNCTION_0, entries[0].edx );
        a.addAll( strings );
        // subfunction 1
        if ( ( entries.length > 1 )&&( maxSubFunction > 0 )&&
             ( entries[1].subfunction == 1 ) )
            {
            // EAX, subfunction 1
            strings = decodeBitmap
                ( "EAX", DECODER_EAX_SUBFUNCTION_1, entries[1].eax );
            a.add( interval );
            a.addAll( strings );
            // EBX, subfunction 1
            strings = decodeBitmap
                ( "EBX", DECODER_EBX_SUBFUNCTION_1, entries[1].ebx );
            a.add( interval );
            a.addAll( strings );
            // EDX, subfunction 1
            strings = decodeBitmap
                ( "EDX", DECODER_EDX_SUBFUNCTION_1, entries[1].edx );
            a.add( interval );
            a.addAll( strings );
            }
        if ( ( entries.length > 2 )&&( maxSubFunction > 1 )&&
             ( entries[2].subfunction == 2 ) )
            {
            // EDX, subfunction 2
            strings = decodeBitmap
                ( "EDX", DECODER_EDX_SUBFUNCTION_2, entries[2].edx );
            a.add( interval );
            a.addAll( strings );
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
