//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID extended function 80000001h declared as CPR.COMMAND.

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID80000001 extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME = 
    "Extended/AMD family, model, stepping, features bitmap";
    
// Parameters table up string    
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };

// Return if function not found
private final static String[][] NO_RESULT =
    {
        { "n/a" , "" , "" , "" , "" }
    };

// Control tables for results decoding
private final static Object[][] DECODER_EBX =
    {
        { "Package type" , 31 , 28 } ,
        { "Brand Id"     , 15 ,  0 }
    };
private final static String[][] DECODER_ECX =
    {
        { "AHF64"    , "LAHF and SAHF in the Protected Mode 64" } , 
        { "CMP"      , "Core multiprocessing, HTT=1 indicates HTT (0) or CMP (1)" } , 
        { "SVM"      , "Secure virtual machine, EFER.SVME and SVM instructions" } ,
        { "EAS"      , "Extended APIC space (APIC_VER.EAS, EXT_APIC_FEAT, etc.)" } , 
        { "CR8D"     , "MOV from/to CR8D by means of LOCK-prefixed MOV from/to CR0" } ,
        { "LZCNT"    , "LZCNT instruction" } ,
        { "SSE4A"    , "SSE4A extension" } ,
        { "MSSE"     , "Misaligned SSE, MXCSR.MM" } , 
        { "3DNow!P"  , "PREFETCH and PREFETCHW (K8 Rev G and K8L+)" } ,
        { "OSVW"     , "OS-visible workaround" } ,
        { "IBS"      , "Instruction based sampling" } , 
        { "XOP"      , "Extended operation (was also used going to be used for SSE5A)" } ,
        { "SKINIT"   , "SKINIT, STGI, DEV instructions" } ,
        { "WDT"      , "Watchdog timer" } , 
        { "x"        , "Reserved" } ,  // bit 14 reserved
        { "LWP"      , "Lightweight profiling" } ,
        { "FMA4"     , "FMA 4 operand" } ,
        { "TCE"      , "Translation cache extension, EFER.TCE" } ,
        { "x"        , "Reserved" } ,  // bit 18 reserved
        { "NODEID"   , "Node ID: MSR C001_100Ch" } ,
        { "x"        , "Reserved" } ,  // bit 20 reserved
        { "TBM"      , "Trailing bit manipulation instruction" } , 
        { "TOPX"     , "Topology extensions: extended levels 8000_001Dh and 8000_001Eh" } ,
        { "PCX_CORE" , "Core perf counter extensions (MSRs C001_020[0...B]h)" } ,
        { "PCX_NB"   , "NB perf counter extensions (MSRs C001_024[0...7]h)" } , 
        { "x"        , "Reserved" } ,  // bit 25 reserved
        { "DBX"      , "Data breakpoint extensions (MSRs C001_1027h and C001_10[19...1B]h)" } ,
        { "PERFTSC"  , "Performance TSC (MSR C001_0280h)" } ,
        { "PCX_L2I"  , "L2I perf counter extensions (MSRs C001_023[0...7]h)" } ,
        { "MONX"     , "MONITORX/MWAITX" } , 
        { "x"        , "Reserved" } ,  // bit 30 reserved 
        { "x"        , "Reserved" }    // bit 31 reserved
    };
private final static String[][] DECODER_EDX =
    {
        { "FPU"     , "x87 Floating point unit, FPU" } , 
        { "VME"     , "Virtual mode enhancements, CR4.VME/PVI, EFLAGS.VIP/VIF, TSS32.IRB" } , 
        { "DE"      , "Debugging extension, CR4.DE, DR7.RW=10b, #UD on MOV from/to DR4/5" } , 
        { "PSE"     , "Page size extension, PSE PDE.PS, PDE/PTE.res, CR4.PSE, #PF(1xxxb)" } , 
        { "TSC"     , "Time stamp counter, RDTSC instruction, CR4.TSD (doesn't imply MSR=1)" } , 
        { "MSR"     , "Model-specific registers, MSRs, RDMSR/WRMSR instructions" } , 
        { "PAE"     , "Physical address extension, 64-bit PDPTE/PDE/PTEs, CR4.PAE" } , 
        { "MCE"     , "Machine check exception, MCAR/MCTR MSRs, CR4.MCE, #MC" } , 
        { "CX8"     , "CMPXCHG8B instruction" } , 
        { "APIC"    , "Advanced programmable interrupt controller, local APIC" } , 
        { "x"       , "Reserved" } ,  // bit 10 reserved
        { "SEP"     , "SYSCALL/SYSRET instructions, EFER/STAR MSRs #1" } , 
        { "MTRR"    , "Memory type and range registers, MTRR MSRs" } , 
        { "PGE"     , "Page global extension, PDE/PTE.G, CR4.PGE" } , 
        { "MCA"     , "Machine check architecture MCG_*/MCn_* MSRs, CR4.MCE, #MC" } , 
        { "CMOV"    , "Conditional move instructions, CMOVcc" } , 
        { "PAT"     , "Page attribute table, PAT MSR, PDE/PTE.PAT" } , 
        { "PSE36"   , "Page size extension, 4 MB PDE bits 16...13, CR4.PSE" } , 
        { "x"       , "Reserved" } ,  // bit 18 reserved
        { "MP"      , "MP-capable" } , 
        { "NX"      , "No-execute page protection, EFER.NXE, PxE.NX, #PF(1xxxx)" } , 
        { "x"       , "Reserved" } ,  // bit 21 reserved
        { "MMX+"    , "AMD specific: MMX-SSE and SSE-MEM" } , 
        { "MMX"     , "MMX instruction set" } , 
        { "FXSR"    , "FXSAVE/FXRSTOR instructions, CR4.OSFXSR" } , 
        { "FFXSRO"  , "FXSAVE/FXRSTOR instructions optimizations, EFER.FFXSR" } , 
        { "PG1G"    , "1GB paging PML3E.PS" } , 
        { "TSCP"    , "TSC, TSC_AUX, RDTSCP, CR4.TSD" } , 
        { "x"       , "Reserved" } ,  // bit 28 reserved
        { "LM64"    , "AMD64/iEM64T, Long Mode 64-bit" } , 
        { "3DNow!+" , "Extended 3DNow! technology" } , 
        { "3DNow!"  , "3DNow! technology" } , 
    };

// Calculate control data total size for output formatting
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EBX.length + 1;
private final static int NY2 = DECODER_ECX.length + 1;
private final static int NY3 = DECODER_EDX.length + 0;
private final static int NY  = NY1+NY2+NY3;

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
    int x = CPUID.findFunction( array, 0x80000001 );
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
    // Start build result, decode EBX from binary to text
    int p=0;  // Pointer for sequentally store result text strings
    int y = (int) ( array[x+2] >>> 32 );                             // y = EBX
    CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    // Decode ECX from binary to text
    p = NY1;
    y = (int) ( array[x+3] & (((long)((long)(-1)>>>32))) );          // y = ECX
    CPUID.decodeBitmap( "ECX", DECODER_ECX, y , result , p );
    // Decode EDX from binary to text
    p = NY1+NY2;
    y = (int) ( array[x+3] >>> 32 );                                 // y = EDX
    CPUID.decodeBitmap( "EDX", DECODER_EDX, y , result , p );
    // Result is ready, all strings filled
    return result;
    }

}
