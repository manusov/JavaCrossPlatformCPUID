//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID standard function 00000001h declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID00000001 extends CommandAdapter
{
private static final String 
        F_NAME = "Type, family, model, stepping, standard features bitmap";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }

    
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };
private final static Object[][] DECODER_EAX =
    { 
        { "Extended Family" , 27 , 20 } ,
        { "Extended Model"  , 19 , 16 } ,
        { "Type"            , 13 , 12 } ,
        { "Base Family"     , 11 ,  8 } ,
        { "Base Model"      ,  7 ,  4 } ,
        { "Stepping"        ,  3 ,  0 }
    };
private final static Object[][] DECODER_EBX =
    {
        { "Initial local APIC ID"     , 31 , 24 } ,
        { "Allocated IDs per package" , 23 , 16 } ,
        { "CLFLUSH size"              , 15 , 8  } ,
        { "Brand ID"                  , 7  , 0  }
    };
private final static String[][] DECODER_ECX =
    {
        { "SSE3"   , "Streaming SIMD extension 3" } ,
        { "PCLM"   , "PCLMULQDQ instruction"      } ,
        { "DS64"   , "Debug store for 64-bit branches history" } ,
        { "MON"    , "MONITOR/MWAIT instructions" } ,
        { "DSCPL"  , "CPL qualified debug store" } ,
        { "VMX"    , "Virtual machine extension" } ,
        { "SMX"    , "Safer mode extension" } ,
        { "EIST"   , "Enhanced Intel SpeedStep technology" } ,
        { "TM2"    , "Thermal monitor 2" } ,
        { "SSSE3"  , "Supplemental streaming SIMD extension 3" } ,
        { "CXTID"  , "L1 context ID" } ,
        { "SDBG"   , "Debug Interface MSR for silicon debug" } ,
        { "FMA"    , "Fused Multiply and Addition instructions" } ,
        { "CX16"   , "CMPXCHG16B instruction" } ,
        { "xTPR"   , "xTPR update control" } ,
        { "PDCM"   , "Performance monitoring and debug capability" } ,
        { "x"      , "Reserved" } ,
        { "PCID"   , "Processor Context identifiers" } ,
        { "DCA"    , "Direct cache access" } ,
        { "SSE41"  , "Streaming SIMD extension 4.1" } ,
        { "SSE42"  , "Streaming SIMD extension 4.2" } ,
        { "x2AP"   , "x2APIC (extended xAPIC) support" } ,
        { "MOVBE"  , "MOVBE instruction" } ,
        { "PCNT"   , "POPCNT instruction" } ,
        { "TSCDL"  , "TSC deadline interrupt" } ,
        { "AESNI"  , "Advanced Encryption Standard new instructions" } ,
        { "XSAVE"  , "XSAVE/XRSTOR states, XSETBV/XGETBV instructions" } ,
        { "OSXSV"  , "OS has enabled XSETBV/XGETBV instructions" } ,
        { "AVX"    , "Advanced Vector Extension" } ,
        { "F16C"   , "16-bit Floating Point conversion instructions" } ,
        { "RDRAND" , "RDRAND instruction, random number generator" } ,
        { "HPGS"   , "(AMD) Reserved for Hypervisor to indicate Guest status" }
    };
private final static String[][] DECODER_EDX =
    {
        { "FPU"    , "x87 floating point unit on chip" } ,
        { "VME"    , "Virtual mode extension" } ,
        { "DE"     , "Debugging extension" } ,
        { "PSE"    , "Page size extension" } ,
        { "TSC"    , "Time stamp counter" } ,
        { "MSR"    , "Model-specific registers" } ,
        { "PAE"    , "Physical address extension" } ,
        { "MCE"    , "Machine check extension" } ,
        { "CX8"    , "CMPXCHG8B instruction" } ,
        { "APIC"   , "On-chip local APIC" } ,
        { "x"      , "Reserved" } ,
        { "SEP"    , "SYSENTER/SYSEXIT, fast system call instructions" } ,
        { "MTRR"   , "Memory type range registers" } ,
        { "PGE"    , "Page global enable" } ,
        { "MCA"    , "Machine check architecture" } ,
        { "CMOV"   , "Conditional move instruction" } ,
        { "PAT"    , "Page attribute table" } ,
        { "PSE36"  , "36-bit address page size extension (2/4MB pages)" } ,
        { "PSN"    , "Processor serial number (present and enabled)" } ,
        { "CLFSH"  , "CLFLUSH instruction" } ,
        { "x"      , "Reserved" } ,
        { "DS"     , "Debug store" } ,
        { "ACPI"   , "Thermal monitor and software controlled clock" } ,
        { "MMX"    , "Multimedia extension" } ,
        { "FXSR"   , "FXSAVE/FXRSTOR instructions" } ,
        { "SSE"    , "Streaming SIMD extension" } ,
        { "SSE2"   , "Streaming SIMD extension 2" } ,
        { "SS"     , "Self-snoop" } ,
        { "HTT"    , "Hyper-Threading technology" } ,
        { "TM"     , "Thermal monitor" } ,
        { "IA64"   , "Intel Architecture 64 (Itanium)" } ,
        { "PBE"    , "Pending break enable" } ,
    };

private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX.length + 1;
private final static int NY2 = DECODER_EBX.length + 1;
private final static int NY3 = DECODER_ECX.length + 1;
private final static int NY4 = DECODER_EDX.length + 1;
private final static int NY  = NY1+NY2+NY3+NY4;

@Override public String[] getCommandUp1( long[] array )
    { return COMMAND_UP_1; }

@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }
    int x = CPUID.findFunction( array, 0x00000001 );
    if (x<0) { return result; }
    
    String s="";
    int y=0;
    int[] z;
    int p=0;
    y = (int) ( array[x+2] & (((long)((long)(-1)>>>32))) );              // EAX
    z = CPUID.decodeBitfields ( "EAX" , DECODER_EAX , y , result , p );
    
    //--- Extended family comments (addition only) ---
    // result[p][4] =
    //    String.format("%02Xh + %02Xh = %02Xh", z[0],z[3],z[0]+z[3]);
    int extendedFamily = z[0];
    int baseFamily = z[3];
    int displayFamily;
    if ( baseFamily == 0xF )  // special criteria
       {
        displayFamily = baseFamily + extendedFamily; 
        result[p][4] = String.format
         ( "%02Xh + %02Xh = %02Xh", extendedFamily, baseFamily, displayFamily );
       }
    else
        {
        displayFamily = baseFamily;
        result[p][4] = String.format
            ( "%02Xh", displayFamily );
        }
    
    //--- Extended model comments (addition with shift) ---
    // result[p+1][4] = 
    //    String.format("%02Xh + %02Xh = %02Xh", z[1],z[4],z[1]+z[4]);
    int extendedModel = z[1] << 4;
    int baseModel = z[4];
    int displayModel;
    if ( ( baseFamily == 0x6 ) | ( baseFamily == 0xF ) )  // special criteria
        {
        displayModel = baseModel + extendedModel; 
        result[p+1][4] = String.format
            ( "%02Xh + %02Xh = %02Xh", extendedModel, baseModel, displayModel );
        }
    else
        {
        displayModel = baseModel;
        result[p+1][4] = String.format
            ( "%02Xh", displayModel );
        }
        
    //--- Parameters ---
    p = NY1;
    y = (int) ( array[x+2] >>> 32 );                                     // EBX
    z = CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    if ( z[1] != 0 ) { s = String.format( "%d", z[1] ); }
    result[p+1][4] = s;
    y = z[2]*8;
    result[p+2][4] = String.format( "%d Bytes", y );
    
    //--- Features bitmap 1 = ECX ---
    p = NY1+NY2;
    y = (int) ( array[x+3] & (((long)((long)(-1)>>>32))) );              // ECX
    CPUID.decodeBitmap( "ECX", DECODER_ECX, y , result , p );

    //--- Features bitmap 2 = EDX ---
    p = NY1+NY2+NY3;
    y = (int) ( array[x+3] >>> 32 );                                     // EDX
    CPUID.decodeBitmap( "EDX", DECODER_EDX, y , result , p );
    
    return result;
    }

}
