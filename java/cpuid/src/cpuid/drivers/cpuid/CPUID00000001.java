//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID standard function 00000001h declared as CPR.COMMAND.

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID00000001 extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME =
    "Type, family, model, stepping, standard features bitmap";

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
        { "x"      , "Reserved" } ,  // bit 16 reserved
        { "PCID"   , "Processor Context identifiers" } ,
        { "DCA"    , "Direct cache access" } ,
        { "SSE41"  , "Streaming SIMD extension 4.1" } ,
        { "SSE42"  , "Streaming SIMD extension 4.2" } ,
        { "x2APIC" , "x2APIC (extended xAPIC) support" } ,
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
        { "x"      , "Reserved" } ,  // bit 10 reserved
        { "SEP"    , "SYSENTER/SYSEXIT, fast system call instructions" } ,
        { "MTRR"   , "Memory type range registers" } ,
        { "PGE"    , "Page global enable" } ,
        { "MCA"    , "Machine check architecture" } ,
        { "CMOV"   , "Conditional move instruction" } ,
        { "PAT"    , "Page attribute table" } ,
        { "PSE36"  , "36-bit address page size extension (2/4MB pages)" } ,
        { "PSN"    , "Processor serial number (present and enabled)" } ,
        { "CLFSH"  , "CLFLUSH instruction" } ,
        { "x"      , "Reserved" } ,  // bit 20 reserved
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
    int x = CPUID.findFunction( array, 0x00000001 );
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
    // Start build result, decode EAX from binary to text
    int p=0;  // Pointer for sequentally store result text strings
    int y = (int) ( array[x+2] & (((long)((long)(-1)>>>32))) );      // y = EAX
    int[] z = CPUID.decodeBitfields ( "EAX" , DECODER_EAX , y , result , p );
    // Extended family comments, use z = binary results of EAX decode
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
    // Extended model comments (addition with shift)
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
    // Parameters = EBX
    String s="";  // s = scratch
    p = NY1;      // p = current result position at entire result array
    y = (int) ( array[x+2] >>> 32 );                                 // y = EBX
    z = CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    if ( z[1] != 0 ) { s = String.format( "%d", z[1] ); }
    result[p+1][4] = s;
    y = z[2]*8;
    result[p+2][4] = String.format( "%d Bytes", y );
    // Features bitmap 1 = ECX
    p = NY1+NY2;
    y = (int) ( array[x+3] & (((long)((long)(-1)>>>32))) );          // y = ECX
    CPUID.decodeBitmap( "ECX", DECODER_ECX, y , result , p );
    // Features bitmap 2 = EDX
    p = NY1+NY2+NY3;
    y = (int) ( array[x+3] >>> 32 );                                 // y = EDX
    CPUID.decodeBitmap( "EDX", DECODER_EDX, y , result , p );
    // Result is ready, all strings filled
    return result;
    }

}
