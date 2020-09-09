/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000020h = AMD bandwidth enforcement.
*/

package cpuidrefactoring.devicecpuid;

import java.util.ArrayList;

class Cpuid80000020 extends ParameterFunctionCpuid
{
Cpuid80000020()
    { setFunction( 0x80000020 ); }

@Override String getLongName()
    { return "AMD bandwidth enforcement"; }

// Control tables for results decoding
private final static String[][] DECODER_EBX_SUBFUNCTION_0 =
    { { "Reserved" , "x" } ,
      { "L3BE"     , "L3 external read bandwidth enforcement" } };
private final static Object[][] DECODER_EAX_SUBFUNCTION_1 =
    { { "L3 external read bandwidth enforcement bit range length" , 31 , 0 } };
private final static Object[][] DECODER_EDX_SUBFUNCTION_1 =
    { { "Maximum COS number for L3 external read bandwidth enforcement" , 31 , 0 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EBX, subfunction 0
        strings = decodeBitmap
            ( "EBX", DECODER_EBX_SUBFUNCTION_0, entries[0].ebx );
        a.addAll( strings );
        a.add( interval );
        if ( entries.length > 1 )
            {
            // EAX, subfunction 1
            dr = decodeBitfields
                ( "EAX", DECODER_EAX_SUBFUNCTION_1, entries[1].eax );
            a.addAll( dr.strings );
            // EDX, subfunction 1
            dr = decodeBitfields
                ( "EDX", DECODER_EDX_SUBFUNCTION_1, entries[1].edx );
            a.addAll( dr.strings );
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
