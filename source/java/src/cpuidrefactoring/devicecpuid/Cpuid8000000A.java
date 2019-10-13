/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
8000000Ah = AMD secure virtual machine revision and features.
*/

package cpuidrefactoring.devicecpuid;

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
private final static String[][] DECODER_EDX =
    { { "NP"       , "Nested paging" } ,
      { "LBR Virt" , "Last branch record virtualization" } ,
      { "SVML"     , "SVM lock" } ,
      { "NRIPS"    , "Next RIP save" } ,
      { "TSC Rate" , "TSC rate control MSR" } ,
      { "VMCB CL"  , "VMCB clean bits" } ,
      { "FLASID"   , "TLB flush selectable by ASID" } ,
      { "DASSIST"  , "Decode assists" } ,
      { "x"        , "Reserved" } ,  // bit 8 reserved
      { "SSE35D"   , "SSSE3 and SSE5A disable" } ,
      { "PAUSE FL" , "Pause intercept filter" } ,
      { "x"        , "Reserved" } ,  // bit 11 reserved
      { "PAUSE FT" , "Pause intercept filter threshold" } ,
      { "AVIC"     , "AMD advanced virtual interrupt controller" } ,
      { "x"        , "Reserved" } ,  // bit 14 reserved
      { "VVMLS"    , "Virtualized VMLOAD and VMSAVE" } ,
      { "VGIF"     , "Virtualized global interrupt flag" } ,
      { "GMET"     , "Guest mode execute trap extension" } ,  // bit 17
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } ,  // bit 24
      { "x"        , "Reserved" } ,  // bit 25
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } ,
      { "x"        , "Reserved" } };  // bit 31

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
        // EDX
        strings = decodeBitmap( "EDX", DECODER_EDX, entries[0].edx );
        a.addAll( strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
