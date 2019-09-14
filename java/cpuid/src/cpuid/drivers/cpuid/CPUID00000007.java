/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
CPUID driver component:
CPUID standard function 00000007h declared as CPR.COMMAND.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID00000007 extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME = 
    "Structured extended feature enumeration";
    
// Parameters table up string
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };

// Return if function not found
private final static String[][] NO_RESULT =
    {
        { "n/a" , "" , "" , "" , "" }
    };

// Control tables for results decoding
private final static Object[][] DECODER_EAX0 =
    {
        { "Maximum sub-leaf number" , 31 , 0 }
    }; 
private final static String[][] DECODER_EBX0 =
    { 
        { "FSGSBASE"   , "FS,GS base addressing modes" } ,
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
        { "AVX512VL"   , "AVX512 vector length control" } ,
    };
private final static String[][] DECODER_ECX0 =
    {
        { "PWT1"         , "Instruction PREFETCHWT1" } ,
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
        { "x"            , "Reserved" }   // bit 31 reserved
    };

private final static String[][] DECODER_EDX0 =
    {
        { "x"            , "Reserved" } ,
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
        { "x"            , "Reserved" } ,
        { "x"            , "Reserved" } ,
        { "x"            , "Reserved" } ,
        { "x"            , "Reserved" } ,
        { "x"            , "Reserved" } ,
        { "PCONFIG"      , "PCONFIG for MK-TME" } ,
        { "x"            , "Reserved" } ,
        { "CET IBT"      , "Control Flow Enforcement: Indirect Branch Tracking" } ,
        { "x"            , "Reserved" } ,
        { "x"            , "Reserved" } ,
        { "x"            , "Reserved" } ,
        { "x"            , "Reserved" } ,
        { "x"            , "Reserved" } ,
        { "IBRS IBPB"    , "Indirect branch restricted speculation and predictor barrier" } ,  // bit 26
        { "STIBP"        , "Single thread indirect branch predictor" } ,  // bit 27
        { "L1D flush"    , "L1 Data Cache (L1D) flush" } ,
        { "ACAP MSR"     , "IA32_ARCH_CAPABILITIES MSR" } ,        // bit 29
        { "x"            , "Reserved" } ,                          // note limit 100/100+ strings, TODO: fix this bug
        { "SSBD"         , "Speculative Store Bypass Disable" } ,  // note limit 100/100+ strings, TODO: fix this bug
    };

private final static String[][] DECODER_EAX1 =
    {
        { "x"            , "Reserved" } ,  // bit 0
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
        { "x"            , "Reserved" } ,  // bit 31
    };

// Calculate control data total size for output formatting
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX0.length + 1;
private final static int NY2 = DECODER_EBX0.length + 1;
private final static int NY3 = DECODER_ECX0.length + 1;
private final static int NY4 = DECODER_EDX0.length + 1;
private final static int NY5 = DECODER_EAX1.length + 0;
private final static int NY  = NY1+NY2+NY3+NY4+NY5;

// Return CPUID this function full name
// INPUT:   Reserved array
// OUTPUT:  String, CPUID function full name
@Override public String getCommandLongName(long[] dummy ) 
    { return F_NAME; }

// Return CPUID this function parameters table up string
// INPUT:   Reserved array
// OUTPUT:  String, CPUID function details table up string
@Override public String[] getCommandUp1( long[] dummy )
    { return COMMAND_UP_1; }

// Build and return CPUID this function detail information table
// INPUT:   Binary array = CPUID dump data
// OUTPUT:  Array of strings = CPUID this function detail information table
@Override public String[][] getCommandText1( long[] array )
    {
    // Scan binary dump, find entry for this function
    int x = CPUID.findFunction( array, 0x00000007 );
    // Return "n/a" if this function entry not found
    if (x<0) { return NO_RESULT; }
    // Build and pre-blank result text array
    String[][] result = new String[NY][NX];  // Text formatted by control data
    for (int i=0; i<NY; i++)  // Cycle for rows 
        { 
        for(int j=0; j<NX; j++)  // Cycle for columns
            {
            result[i][j]=""; 
            } 
        }
    // Parameters from CPUID dump, EAX register
    int p=0;  // pointer for sequentally store strings in the table
    
    // subfunction 0
    int y = (int) ( array[x+2] & (((long)((long)(-1)>>>32))) );    // y = EAX
    int maxSubFunction = y;
    int[] z = CPUID.decodeBitfields ( "EAX" , DECODER_EAX0 , y , result , p );
    result[p][4] = String.format( "%d", z[0] );
    // Parameters from CPUID dump, EBX register
    p = NY1;
    y = (int) ( array[x+2] >>> 32 );                               // y = EBX
    CPUID.decodeBitmap ( "EBX" , DECODER_EBX0 , y , result , p );
    // Parameters from CPUID dump, ECX register
    p = NY1+NY2;
    y = (int) ( array[x+3] & (((long)((long)(-1)>>>32))) );        // y = ECX
    CPUID.decodeBitmap ( "ECX" , DECODER_ECX0 , y , result , p );
    // Parameters from CPUID dump, ECX register
    p = NY1+NY2+NY3;
    y = (int) ( array[x+3] >>> 32 );                               // y = EDX
    CPUID.decodeBitmap ( "EDX" , DECODER_EDX0 , y , result , p );
    
    // subfunction 1
    p = NY1+NY2+NY3+NY4;
    if ( maxSubFunction == 0 )  // check EAX of subfunction 0
        {
        y = 0;
        }
    else
        {
        // Get number of entries per CPUID dump, field from dump header
        int maxEntries = (int) ( array[0] & (((long)((long)(-1)>>>32))) );
        maxEntries = (maxEntries+1)*4;  // Calculate x2 = limit at units = long
        x = x + 4;                      // set pointer to subfunction 1
        if ( x >= maxEntries )  // check array size limit before get subfunction entry 
            { 
            y = 0;
            }        // Return 0 if dump size limit
        else
            {
            int function = (int) ( array[x] >>> 32 );
            int subFunction = (int) ( array[x+1] & (((long)((long)(-1)>>>32))) );
            if ( ( function == 0x00000007 ) & ( subFunction == 0x00000001 ) )
                {  // this for success detected function=7, subfunction=1
                y = (int) ( array[x+2] & (((long)((long)(-1)>>>32))) );   // y = EAX
                }
            else
                {  // this for function or/and subfunction mismatch
                y = 0;
                }
            }
        }
    // EAX for subfunction 1
    CPUID.decodeBitmap ( "EAX" , DECODER_EAX1 , y , result , p );
    
    // Result is ready, all strings filled
    return result;
    }
}