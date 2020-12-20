/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
00000001h = Type, family, model, stepping, standard features bitmap.
*/

package cpuidrefactoring.devicecpuid;

import java.util.ArrayList;

class Cpuid00000001 extends ParameterFunctionCpuid
{
Cpuid00000001()
    { setFunction( 0x00000001 ); }

@Override String getLongName()
    { return "Type, family, model, stepping, standard features bitmap"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Extended Family" , 27 , 20 } ,
      { "Extended Model"  , 19 , 16 } ,
      { "Type"            , 13 , 12 } ,
      { "Base Family"     , 11 ,  8 } ,
      { "Base Model"      ,  7 ,  4 } ,
      { "Stepping"        ,  3 ,  0 } };
private final static Object[][] DECODER_EBX =
    { { "Initial local APIC ID"     , 31 , 24 } ,
      { "Allocated IDs per package" , 23 , 16 } ,
      { "CLFLUSH size"              , 15 , 8  } ,
      { "Brand ID"                  , 7  , 0  } };
private final static String[][] DECODER_ECX =
    { { "SSE3"   , "Streaming SIMD extension 3" } ,
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
      { "HPGS"   , "(AMD) Reserved for Hypervisor to indicate Guest status" } };
private final static String[][] DECODER_EDX =
    { { "FPU"    , "x87 floating point unit on chip" } ,
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
      { "PBE"    , "Pending break enable" } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX, with extended family comments
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        int extendedFamily = dr.values[0];  // values = EAX decode results
        int baseFamily = dr.values[3];
        int displayFamily;
        if ( baseFamily == 0xF )  // special criteria
            {
            displayFamily = baseFamily + extendedFamily; 
            dr.strings.get(0)[4] = String.format( "%02Xh + %02Xh = %02Xh",
                extendedFamily, baseFamily, displayFamily );
            }
        else
            {
            displayFamily = baseFamily;
            dr.strings.get(0)[4] = String.format( "%02Xh", displayFamily );
            }
        
        int extendedModel = dr.values[1] << 4; // Extended model comments
        int baseModel = dr.values[4];
        int displayModel;
        if ( ( baseFamily == 0x6 ) | ( baseFamily == 0xF ) )  // spec. criteria
            {
            displayModel = baseModel + extendedModel; 
            dr.strings.get(1)[4] = String.format( "%02Xh + %02Xh = %02Xh", 
                extendedModel, baseModel, displayModel );
            }
        else
            {
            displayModel = baseModel;
            dr.strings.get(1)[4] = String.format( "%02Xh", displayModel );
            }
        a.addAll( dr.strings );
        a.add( interval );
        // EBX
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        if ( dr.values[1] != 0 ) 
            { 
            dr.strings.get(1)[4] = String.format( "%d IDs", dr.values[1] );
            }
        if ( dr.values[2] != 0 )
            {
            dr.strings.get(2)[4] = String.format
                ( "%d Bytes", dr.values[2] * 8 );
            }
        a.addAll( dr.strings );
        a.add( interval );
        // ECX
        strings = decodeBitmap( "ECX", DECODER_ECX, entries[0].ecx );
        a.addAll( strings );
        a.add( interval );
        // EDX
        strings = decodeBitmap( "EDX", DECODER_EDX, entries[0].edx );
        a.addAll( strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
