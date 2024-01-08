/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Vendor Specific Function
80860002h = Transmeta vendor-specific: 
            Transmeta processor CMS (Code Morphing Software) information.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

public class Cpuid80860002 extends ParameterFunctionCpuid
{
Cpuid80860002()
    { setFunction( 0x80860002 ); }

@Override String getLongName()
    { return "Transmeta Code Morphing Software (CMS) information"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Transmeta processor revision ID"           , 31 ,  0 } };
private final static Object[][] DECODER_EBX =
    { { "Transmeta CMS revision ID, part 1, major"  , 31 , 24 } ,
      { "Minor revision"                            , 23 , 16 } ,
      { "Major mask"                                , 15 ,  8 } ,
      { "Minor mask"                                ,  7 ,  0 } };
private final static Object[][] DECODER_ECX =
    { { "Transmeta CMS revision ID, part 2, major"  , 31 , 24 } ,
      { "Minor revision"                            , 23 , 16 } ,
      { "Major mask"                                , 15 ,  8 } ,
      { "Minor mask"                                ,  7 ,  0 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    String s;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX: Transmeta processor revision ID
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        int rev = dr.values[0];
        if ( rev == 0 )
            s = "n/a";
        else
            s = String.format( "rev %08X", rev );
        dr.strings.get(0)[4] = s;
        a.addAll( dr.strings );
        a.add( interval );
        // EBX: First part of the Transmeta CMS revision ID
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        s = String.format( "%d.%d.%d-%d", 
            dr.values[0], dr.values[1], dr.values[2], dr.values[3] );
        dr.strings.get(0)[4] = s;
        a.addAll( dr.strings );
        a.add( interval );
        // ECX: Second part of the Transmeta CMS revision ID
        dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
        s = String.format( "%d.%d.%d-%d", 
            dr.values[0], dr.values[1], dr.values[2], dr.values[3] );
        dr.strings.get(0)[4] = s;
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
