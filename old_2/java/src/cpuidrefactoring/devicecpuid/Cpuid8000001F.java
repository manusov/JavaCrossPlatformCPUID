/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
8000001Fh = AMD secure encrypted virtualization.
*/

package cpuidrefactoring.devicecpuid;

import java.util.ArrayList;

class Cpuid8000001F extends ParameterFunctionCpuid
{
Cpuid8000001F()
    { setFunction( 0x8000001F ); }

@Override String getLongName()
    { return "AMD secure encrypted virtualization"; }

// Control tables for results decoding
private final static String[][] DECODER_EAX =
    { { "SME"     , "Secure Memory Encryption" } ,
      { "SEV"     , "Secure Encrypted Virtualization" } ,
      { "PGFMSR"  , "Page Flush MSR" } ,
      { "SEV-ES"  , "Encrypted State" } ,
      { "SEV-SNP" , "Secure Nested Paging" } ,
      { "VMPL"    , "Virtual Machine Permission (Privilege) levels" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "HWCC ED" , "Hardware cache coherency across encryption domains" } ,
      { "HOST 64" , "SEV guest execution only for 64-bit hosts" } ,
      { "RES INJ" , "Restricted Injection" } ,
      { "ALT INJ" , "Alternate Injection" } ,
      { "DBG SW"  , "Debug state swap for SEV-ES guests" } ,
      { "PHIBS"   , "Prevent Host Instruction Based Sampling" } ,  // bit 15
      { "VTE"     , "Virtual Transparent Encryption" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,  // bit 24
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } ,
      { "x"       , "Reserved" } };  // bit 31
private final static Object[][] DECODER_EBX =
    { { "Page table bit used to enable protection"    ,   5 ,  0 } ,
      { "Reduction of physical address space"         ,  11 ,  6 } ,
      { "Number of Virtual Machine Permission Levels" ,  15 , 12 } };
private final static Object[][] DECODER_ECX =
    { { "Number of encrypted guests supported simultaneously" ,  31 ,  0 } };
private final static Object[][] DECODER_EDX =
    { { "Minimum SEV enabled, SEV-ES disabled ASID" ,  31 ,  0 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX
        strings = decodeBitmap( "EAX", DECODER_EAX, entries[0].eax );
        a.addAll( strings );
        a.add( interval );
        // EBX
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        dr.strings.get(0)[4] = String.format( "bit %d", dr.values[0] );
        if ( dr.values[1]==0 )
            {
            dr.strings.get(1)[4] = "no reduction";
            }
        else
            {
            dr.strings.get(1)[4] = 
                String.format( "minus %d bits", dr.values[1] );
            }
        a.addAll( dr.strings );
        a.add( interval );
        // ECX
        dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
        dr.strings.get(0)[4] = String.format( "%d guests", dr.values[0] );
        a.addAll( dr.strings );
        a.add( interval );
        // EDX
        dr = decodeBitfields( "EDX", DECODER_EDX, entries[0].edx );
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
