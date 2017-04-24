//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID extended function 80000001h declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID80000001 extends CommandAdapter
{
private static final String 
        F_NAME = "Extended/AMD family, model, stepping, features bitmap";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }
    
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };
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
        { "x"        , "Reserved" } , 
        { "LWP"      , "Lightweight profiling" } ,
        { "FMA4"     , "FMA 4 operand" } ,
        { "TCE"      , "Translation cache extension, EFER.TCE" } ,
        { "x"        , "Reserved" } , 
        { "NODEID"   , "Node ID: MSR C001_100Ch" } ,
        { "x"        , "Reserved" } , 
        { "TBM"      , "Trailing bit manipulation instruction" } , 
        { "TOPX"     , "Topology extensions: extended levels 8000_001Dh and 8000_001Eh" } ,
        { "PCX_CORE" , "Core perf counter extensions (MSRs C001_020[0...B]h)" } ,
        { "PCX_NB"   , "NB perf counter extensions (MSRs C001_024[0...7]h)" } , 
        { "x"        , "Reserved" } ,
        { "DBX"      , "Data breakpoint extensions (MSRs C001_1027h and C001_10[19...1B]h)" } ,
        { "PERFTSC"  , "Performance TSC (MSR C001_0280h)" } ,
        { "PCX_L2I"  , "L2I perf counter extensions (MSRs C001_023[0...7]h)" } ,
        { "MONX"     , "MONITORX/MWAITX" } , 
        { "x"        , "Reserved" } , 
        { "x"        , "Reserved" }
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
        { "x"       , "Reserved" } , 
        { "SEP"     , "SYSCALL/SYSRET instructions, EFER/STAR MSRs #1" } , 
        { "MTRR"    , "Memory type and range registers, MTRR MSRs" } , 
        { "PGE"     , "Page global extension, PDE/PTE.G, CR4.PGE" } , 
        { "MCA"     , "Machine check architecture MCG_*/MCn_* MSRs, CR4.MCE, #MC" } , 
        { "CMOV"    , "Conditional move instructions, CMOVcc" } , 
        { "PAT"     , "Page attribute table, PAT MSR, PDE/PTE.PAT" } , 
        { "PSE36"   , "Page size extension, 4 MB PDE bits 16...13, CR4.PSE" } , 
        { "x"       , "Reserved" } , 
        { "MP"      , "MP-capable" } , 
        { "NX"      , "No-execute page protection, EFER.NXE, PxE.NX, #PF(1xxxx)" } , 
        { "x"       , "Reserved" } , 
        { "MMX+"    , "AMD specific: MMX-SSE and SSE-MEM" } , 
        { "MMX"     , "MMX instruction set" } , 
        { "FXSR"    , "FXSAVE/FXRSTOR instructions, CR4.OSFXSR" } , 
        { "FFXSRO"  , "FXSAVE/FXRSTOR instructions optimizations, EFER.FFXSR" } , 
        { "PG1G"    , "1GB paging PML3E.PS" } , 
        { "TSCP"    , "TSC, TSC_AUX, RDTSCP, CR4.TSD" } , 
        { "x"       , "Reserved" } , 
        { "LM64"    , "AMD64/iEM64T, Long Mode 64-bit" } , 
        { "3DNow!+" , "Extended 3DNow! technology" } , 
        { "3DNow!"  , "3DNow! technology" } , 
    };
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EBX.length + 1;
private final static int NY2 = DECODER_ECX.length + 1;
private final static int NY3 = DECODER_EDX.length + 1;
private final static int NY  = NY1+NY2+NY3;

@Override public String[] getCommandUp1( long[] array )
    { return COMMAND_UP_1; }

@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }
    int x = CPUID.findFunction( array, 0x80000001 );
    if (x<0) { return result; }
    
    // String s="";
    int y=0;
    int[] z;
    int p=0;
    y = (int) ( array[x+2] >>> 32 );                                     // EBX
    z = CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    
    p = NY1;
    y = (int) ( array[x+3] & (((long)((long)(-1)>>>32))) );              // ECX
    CPUID.decodeBitmap( "ECX", DECODER_ECX, y , result , p );

    p = NY1+NY2;
    y = (int) ( array[x+3] >>> 32 );                                     // EDX
    CPUID.decodeBitmap( "EDX", DECODER_EDX, y , result , p );
    
    return result;
    }

}
