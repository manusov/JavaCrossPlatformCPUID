/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Virtual Function 40000X03h =
Xen hypervisor virtual and physical TSC parameters
(Xen hypervisor-specific Virtual CPUID).
Note this class must be dynamically add to functions list if hypervisor
vendor = Xen, otherwise function number can conflict with existed.
Note function number can be selected from base 40000003h with 100h increments,
for example 40000103h, 40000203h, ... 
to prevent conflict with other hypervisors.

*/

package cpuidv3.servicecpuid;

import java.util.ArrayList;

class Cpuid40000X03 extends Cpuid40000010
{
Cpuid40000X03( int index )
    { 
    setFunction( 0x40000003 + ( index << 8 ) ); 
    }

@Override String getLongName()
    { return "Xen hypervisor virtual and physical TSC parameters"; }

// Control tables for results decoding
private final static String[][] DECODER_EAX_SUBFUNCTION_0 =
    { { "VTSC"  , "Virtual emulated TSC" } ,
      { "HTSC"  , "Host TSC is reliable" } ,
      { "TSCP"  , "Boot CPU has RDTSCP" } };
private final static Object[][] DECODER_EBX_SUBFUNCTION_0 =
    { { "Guest TSC mode"                            , 31 , 0 } };
private final static Object[][] DECODER_ECX_SUBFUNCTION_0 =
    { { "Guest TSC frequency"                       , 31 , 0 } };
private final static Object[][] DECODER_EDX_SUBFUNCTION_0 =
    { { "Guest TSC incarnation (migration count)"   , 31 , 0 } };

private final static Object[][] DECODER_EAX_SUBFUNCTION_1 =
    { { "Virtual TSC offset, low 32 bits"           , 31 , 0 } };
private final static Object[][] DECODER_EBX_SUBFUNCTION_1 =
    { { "Virtual TSC offset, high 32 bits"          , 31 , 0 } };
private final static Object[][] DECODER_ECX_SUBFUNCTION_1 =
    { { "VTSC multiplicator for tsc->ns conversion" , 31 , 0 } };
private final static Object[][] DECODER_EDX_SUBFUNCTION_1 =
    { { "VTSC shift amount for tsc->ns conversion"  , 31 , 0 } };

private final static Object[][] DECODER_EAX_SUBFUNCTION_2 =
    { { "Host TSC frequency"                        , 31 , 0 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String s;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // subfunction 0, EAX
        strings = decodeBitmap( "EAX", DECODER_EAX_SUBFUNCTION_0, entries[0].eax );
        a.addAll( strings );
        // EBX
        dr = decodeBitfields( "EBX", DECODER_EBX_SUBFUNCTION_0, entries[0].ebx );
        int mode = entries[0].ebx;
        if ( mode < TSC_MODES.length )
            s = TSC_MODES[mode];
        else
            s = "?";
        dr.strings.get(0)[4] = s;
        a.addAll( dr.strings );
        // ECX
        dr = decodeBitfields( "ECX", DECODER_ECX_SUBFUNCTION_0, entries[0].ecx );
        dr.strings.get(0)[4] = frequencyHelper( entries[0].ecx );
        a.addAll( dr.strings );
        // EDX
        dr = decodeBitfields( "EDX", DECODER_EDX_SUBFUNCTION_0, entries[0].edx );
        int incarnation = entries[0].edx;
        if ( incarnation > 0 )
            s = String.format( "%d", incarnation );
        else
            s = "?";
        dr.strings.get(0)[4] = s;
        a.addAll( dr.strings );
        
        // subfunction 1    
        if ( ( entries.length > 1 )&&( entries[1].subfunction == 1 ) )
            {
            a.add( interval );
            // EAX
            dr = decodeBitfields( "EAX", DECODER_EAX_SUBFUNCTION_1, entries[1].eax );
            s = String.format("Offset = %08X%08Xh", entries[1].ebx, entries[1].eax );
            dr.strings.get(0)[4] = s;
            a.addAll( dr.strings );
            // EBX
            dr = decodeBitfields( "EBX", DECODER_EBX_SUBFUNCTION_1, entries[1].ebx );
            a.addAll( dr.strings );
            // ECX
            dr = decodeBitfields( "ECX", DECODER_ECX_SUBFUNCTION_1, entries[1].ecx );
            dr.strings.get(0)[4] = String.format("%d", entries[1].ecx );
            a.addAll( dr.strings );
            // EDX
            dr = decodeBitfields( "EDX", DECODER_EDX_SUBFUNCTION_1, entries[1].edx );
            dr.strings.get(0)[4] = String.format("%d", entries[1].edx );
            a.addAll( dr.strings );
            }
        
        // subfunction 2
        if ( ( entries.length > 2 )&&( entries[2].subfunction == 2 ) )
            {
            a.add( interval );
            // EAX
            dr = decodeBitfields( "EAX", DECODER_EAX_SUBFUNCTION_2, entries[2].eax );
            dr.strings.get(0)[4] = frequencyHelper( entries[2].eax );
            a.addAll( dr.strings );
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }

private final static String[] TSC_MODES =
    {
    "default (auto)",
    "emulation",
    "no emulation",
    "no emulation + TSC_AUX" 
    };

}
