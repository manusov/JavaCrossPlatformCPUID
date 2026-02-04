/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Extended Function 8000001Eh =
AMD multiprocessing topology.

*/

package cpuidv3.servicecpuid;

import java.util.ArrayList;

class Cpuid8000001E extends ParameterFunctionCpuid
{
Cpuid8000001E() { setFunction( 0x8000001E ); }

@Override String getLongName()
    { return "AMD multiprocessing topology"; }

// Control tables for results decoding.
private final static Object[][] DECODER_EAX =
    { { "Current extended APIC ID" ,  31 ,  0 } };
private final static Object[][] DECODER_EBX =
    { { "Current compute unit ID"    ,  7 , 0 } ,
      { "Threads per core - 1"       , 15 , 8 } };
private final static Object[][] DECODER_ECX =
    { { "Current node ID"          ,   7 , 0 } ,
      { "Nodes per processor - 1"  ,  10 , 8 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX.
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        a.addAll( dr.strings );
        a.add( interval );
        // EBX.
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        dr.strings.get(1)[4] = ( dr.values[1] + 1 ) + " threads per core";
        a.addAll( dr.strings );
        a.add( interval );
        // ECX.
        dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
        int n = dr.values[1];
        String s = " nodes per processor";
        switch ( n )
            {
            case 0:
                s = "1" + s;
                break;
            case 1:
                s = "2" + s;
                break;
            case 3:
                s = "4" + s;
                break;
            default:
                s = "Reserved";
                break;
            }
        dr.strings.get(1)[4] = s;
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
