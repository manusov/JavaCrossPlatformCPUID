/*
CPUID Utility. (C)2020 IC Book Labs
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
      { "PQM"        , "Platform quality of service monitoring" } ,
      { "DFPUCSDS"   , "Deprecates FPU CS and FPU DS if 1" } ,
      { "MPX"        , "Memory protection extensions" } ,
      { "PQE"        , "Platform quality of service enforcement" } ,
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
      { "5LP"          , "5-level paging" } ,
      { "MAWAU[0]"     , "MPX address width adjust" } ,
      { "MAWAU[1]"     , "MPX address width adjust" } ,
      { "MAWAU[2]"     , "MPX address width adjust" } ,
      { "MAWAU[3]"     , "MPX address width adjust" } ,
      { "MAWAU[4]"     , "MPX address width adjust" } ,
      { "RDPID"        , "Read processor ID" } ,
      { "x"            , "Reserved" } ,  // bit 23 reserved
      { "x"            , "Reserved" } ,  // ...
      { "CLDEMOTE"     , "Cache line demote instruction" } ,
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
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "AVX512VP2IS"  , "AVX512 compute intersection instructions" } ,
      { "x"            , "Reserved" } ,
      { "MD CLEAR"     , "CPU is not affected by microarch. data sampling (MDS)" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,               // bit 13
      { "SERIALIZE"    , "SERIALIZE instruction" } ,  // bit 14
      { "HYBRID"       , "Processor is identified as a hybrid part" } ,  // bit 15
      { "TSXLDTRK"     , "XRESLDTRK and XSUSLDTRK instructions" } ,      // bit 16
      { "x"            , "Reserved" } ,
      { "PCONFIG"      , "PCONFIG for MK-TME" } ,  // bit 18
      { "x"            , "Reserved" } ,
      { "CET IBT"      , "Control Flow Enforcement: Indirect Branch Tracking" } ,
      { "x"            , "Reserved" } ,            // bit 21
      { "AMX BF16"     , "Advanced Matrix Extensions, operations on BFLOAT16 numbers" } ,
      { "x"            , "Reserved" } ,            // bit 23
      { "AMX TILE"     , "Advanced Matrix Extensions, supports Tile Architecture" } ,
      { "AMX INT8"     , "Advanced Matrix Extensions, operations on INT8 numbers" } ,
      { "IBRS IBPB"    , "Indirect branch restricted speculation and predictor barrier" } ,  // bit 26
      { "STIBP"        , "Single thread indirect branch predictor" } ,  // bit 27
      { "L1D flush"    , "L1 Data Cache (L1D) flush" } ,
      { "ACAP MSR"     , "IA32_ARCH_CAPABILITIES MSR" } ,        // bit 29
      { "CCAP MSR"     , "IA32_CORE_CAPABILITIES MSR" } ,
      { "SSBD"         , "Speculative Store Bypass Disable" } };
private final static String[][] DECODER_EAX_SUBFUNCTION_1 =
    { { "x"            , "Reserved" } ,  // bit 0
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "AVX512BF16"   , "VNNI instructions supports BFLOAT16 format" } ,
      { "x"            , "Reserved" } ,  // bit 6
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,  // bit 16
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,  // bit 24
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } ,
      { "x"            , "Reserved" } };  // bit 31
    
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
        a.add( interval );
        // EAX, subfunction 1
        if ( ( entries.length > 1 )&&( maxSubFunction > 0 )&&
             ( entries[1].subfunction == 1 ) )
            {
            strings = decodeBitmap
                ( "EAX", DECODER_EAX_SUBFUNCTION_1, entries[1].eax );
            a.addAll( strings );
            a.add( interval );
            }
        }
    return a.isEmpty() ? 
    super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}