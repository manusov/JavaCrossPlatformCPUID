/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
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
private final static Object[][] DECODER_EAX =
    {
        { "Maximum sub-leaf number" , 31 , 0 }
    }; 
private final static String[][] DECODER_EBX =
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
private final static String[][] DECODER_ECX =
    {
        { "PWT1"         , "Instruction PREFETCHWT1" } ,
        { "AVX512VBMI"   , "AVX512 vector byte manipulation" } ,
        { "UMIP"         , "User mode instruction prevention" } ,
        { "PKU"          , "Protection keys for user-mode pages" } ,
        { "OSPKE"        , "OS has set CR4.PKE to enable prot. keys, RDPKRU/WRPKRU" } ,
        { "x"            , "Reserved" } ,  // bit 5 reserved
        { "AV512VBMI2"   , "AVX512 vector byte manipulation v2" } ,
        { "CET SS"       , "Control Flow Enforcement: Shadow Stacks" } ,
        { "GFNI"         , "Galois field numeric instructions" } ,
        { "VAES"         , "Vector advanced encryption standard" } ,
        { "VPCLMULQDQ"   , "Carry-less multiplication quadword instruction" } ,
        { "AVXV512VNNI"  , "AVX512 4-iteration vector neural network instructions" } ,
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
        { "x"            , "Reserved" } ,
        { "x"            , "Reserved" } ,
        { "x"            , "Reserved" } ,
        { "x"            , "Reserved" } ,  // ...
        { "x"            , "Reserved" } ,  // bit 29 reserved
        { "SGX LC"       , "SGX launch configuration" } ,
        { "x"            , "Reserved" } 
    };

private final static String[][] DECODER_EDX =
    {
        { "x"            , "Reserved" } ,
        { "x"            , "Reserved" } ,
        { "AVX512VNNIW"  , "AVX512 4-iteration vector neural network instruction, word mode" } ,
        { "AVX512FMAPS"  , "AVX512 4-iteration fused multiply-add, single precision" } ,
        { "x"            , "Reserved" } ,
        { "x"            , "Reserved" } ,
        { "x"            , "Reserved" } ,
        { "x"            , "Reserved" } ,
        { "x"            , "Reserved" } ,
        { "x"            , "Reserved" } ,
        { "x"            , "Reserved" } ,
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
        { "IBRS_IBPB"    , "Reserved" } ,
        { "STIBP"        , "Reserved" } ,
        { "x"            , "Reserved" } ,
        { "ACP MSR"      , "ARCH_CAPABILITIES MSR" } ,
        { "x"            , "Reserved" } ,  // note limit 100/100+ strings, TODO: fix this bug
        { "x"            , "Reserved" } ,  // note limit 100/100+ strings, TODO: fix this bug
    };

// Calculate control data total size for output formatting
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX.length + 1;
private final static int NY2 = DECODER_EBX.length + 1;
private final static int NY3 = DECODER_ECX.length + 1;
private final static int NY4 = DECODER_EDX.length + 0;
private final static int NY  = NY1+NY2+NY3+NY4;

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
    int y = (int) ( array[x+2] & (((long)((long)(-1)>>>32))) );      // y = EAX
    int[] z = CPUID.decodeBitfields ( "EAX" , DECODER_EAX , y , result , p );
    result[p][4] = String.format( "%d", z[0] );
    // Parameters from CPUID dump, EBX register
    p = NY1;
    y = (int) ( array[x+2] >>> 32 );                                 // y = EBX
    CPUID.decodeBitmap ( "EBX" , DECODER_EBX , y , result , p );
    // Parameters from CPUID dump, ECX register
    p = NY1+NY2;
    y = (int) ( array[x+3] & (((long)((long)(-1)>>>32))) );          // y = ECX
    CPUID.decodeBitmap ( "ECX" , DECODER_ECX , y , result , p );
    // Parameters from CPUID dump, ECX register
    p = NY1+NY2+NY3;
    y = (int) ( array[x+3] >>> 32 );                                 // y = EDX
    CPUID.decodeBitmap ( "EDX" , DECODER_EDX , y , result , p );
    // Result is ready, all strings filled
    return result;
    }
}