/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2024.
-------------------------------------------------------------------------------
Class for support CPUID Extended Function 8000000Ah = 
AMD secure virtual machine revision and features.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

class Cpuid8000000A extends ParameterFunctionCpuid
{
Cpuid8000000A()
    { setFunction( 0x8000000A ); }

@Override String getLongName()
    { return "AMD secure virtual machine revision and features"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "SVM revision" , 7 , 0 } ,
      { "Hypervisor present and intercepting this bit" , 8 , 8 } };
private final static Object[][] DECODER_EBX =
    { { "NASID: number of address space identifiers (ASID)" , 31 , 0 } };
private final static String[][] DECODER_ECX =
    { { "x"        , "Reserved" } ,    // bit 0
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } ,                          // bit 5
      { "AVIC EXT" , "x2AVIC extension for 4096 vCPU" } };   // bit 6
private final static String[][] DECODER_EDX =
    { { "NP"       , "Nested paging" } ,
      { "LBR VIRT" , "Last branch record virtualization" } ,
      { "SVML"     , "SVM lock" } ,
      { "NRIPS"    , "Next RIP save" } ,
      { "TSC RATE" , "TSC rate control MSR" } ,
      { "VMCB CL"  , "VMCB clean bits" } ,
      { "FLASID"   , "TLB flush selectable by ASID" } ,
      { "DASSIST"  , "Decode assists" } ,
      { "PMC VIRT" , "PMC counter virtualization" } ,  // bit 8
      { "SSE35D"   , "SSSE3 and SSE5A disable" } ,
      { "PAUSE FL" , "Pause intercept filter" } ,     // bit 10
      { "x"        , "Reserved" } ,  // bit 11 reserved
      { "PAUSE FT" , "Pause intercept filter threshold" } ,
      { "AVIC"     , "AMD advanced virtual interrupt controller" } ,
      { "x"        , "Reserved" } ,  // bit 14 reserved
      { "VVMLS"    , "Virtualized VMLOAD and VMSAVE" } ,
      { "VGIF"     , "Virtualized global interrupt flag" } ,
      { "GMET"     , "Guest mode execute trap extension" } ,  // bit 17
      { "x2AVIC"   , "Advanced virtual interrupt controller for x2APIC" } ,
      { "SSS CHK"  , "Supervisor shadow stack restrictions check" } ,  // bit 19
      { "SPEC CTL" , "Guest Spec Ctl, hardware handled MSR update" } , // bit 20
      { "ROGPT"    , "Read-only guest page table" } ,
      { "x"        , "Reserved" } ,
      { "HMCOV"    , "Host MCE override" } ,                                   // bit 23
      { "TLBICTL"  , "TLB instruction control, broadcast synchronization" } ,  // bit 24
      { "VNMI"     , "NMI virtualization" } ,  // bit 25
      { "IBS VIRT" , "Instruction based sampling virtualization" } ,
      { "EILVTR"   , "Extended interrupt local vector table registers" } ,
      { "SGPFIX"   , "Hypervisor spurious GP fault fixed" } ,  // bit 28
      { "LCK THR"  , "Bus lock threshold" } ,
      { "IDL HLT"  , "Idle HLT intercept" } ,
      { "ESHDINT"  , "Enhanced shutdown intercept" } };  // bit 31

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        a.addAll( dr.strings );
        dr.strings.get(0)[4] = "" + dr.values[0];
        a.add( interval );
        // EBX
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        a.addAll( dr.strings );
        dr.strings.get(0)[4] = "" + dr.values[0];
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
