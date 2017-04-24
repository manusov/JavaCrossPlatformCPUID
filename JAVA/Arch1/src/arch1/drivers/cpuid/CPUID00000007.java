//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID standard function 00000007h declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID00000007 extends CommandAdapter
{
private static final String 
        F_NAME = "Structured extended feature enumeration";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }
    
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };
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
        { "x"          , "Reserved" } ,
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
        { "PWT1"       , "Instruction PREFETCHWT1" } ,
        { "AVX512VBMI" , "AVX512 vector byte manipulation" } ,
        { "x"          , "Reserved" } ,
        { "PKU"   , "Protection keys for user-mode pages" } ,
        { "OSPKE" , "OS has set CR4.PKE to enable prot. keys, RDPKRU/WRPKRU" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } ,
        { "x"          , "Reserved" } 
    };
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX.length + 1;
private final static int NY2 = DECODER_EBX.length + 1;
private final static int NY3 = DECODER_ECX.length + 1;
private final static int NY  = NY1+NY2+NY3;

@Override public String[] getCommandUp1( long[] array )
    { return COMMAND_UP_1; }

@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }
    int x = CPUID.findFunction( array, 0x00000007 );
    if (x<0) { return result; }
    
    int y=0;
    int p=0;
    int[] z;
    y = (int) ( array[x+2] & (((long)((long)(-1)>>>32))) );              // EAX
    z = CPUID.decodeBitfields ( "EAX" , DECODER_EAX , y , result , p );
    result[p][4] = String.format( "%d", z[0] );

    p = NY1;
    y = (int) ( array[x+2] >>> 32 );                                     // EBX
    CPUID.decodeBitmap ( "EBX" , DECODER_EBX , y , result , p );

    p = NY1+NY2;
    y = (int) ( array[x+3] & (((long)((long)(-1)>>>32))) );              // ECX
    CPUID.decodeBitmap ( "ECX" , DECODER_ECX , y , result , p );
    
    return result;
    }
}