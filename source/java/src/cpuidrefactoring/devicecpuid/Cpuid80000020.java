/*
CPUID Utility. (C)2022 IC Book Labs
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
    { { "Reserved" , "x" } ,  // bit 0
      { "L3BE"     , "L3 external read bandwidth enforcement" } ,
      { "L3SBE"    , "L3 external slow memory bandwidth enforcement" } ,
      { "BMEC"     , "Bandwidth monitoring event configuration" } ,
      { "L3RR"     , "L3 cache range reservation"               } ,           // bit 4
      { "ABMC"     , "Assignable bandwidth monitoring counters" } ,           // bit 5
      { "SDCIAE"   , "Smart data cache injection allocation enforcement" } ,  // bit 6
    };
private final static Object[][] DECODER_EAX_SUBFUNCTION_1 =
    { { "L3 external read bandwidth enforcement bit range length" , 31 , 0 } };
private final static Object[][] DECODER_EDX_SUBFUNCTION_1 =
    { { "Maximum COS number for L3 external read bandwidth enforcement" , 31 , 0 } };
private final static Object[][] DECODER_EAX_SUBFUNCTION_2 =
    { { "Slow memory bandwidth enforcement bit range length" , 31 , 0 } };
private final static Object[][] DECODER_EDX_SUBFUNCTION_2 =
    { { "Maximum COS number for memory bandwidth enforcement" , 31 , 0 } };
private final static Object[][] DECODER_EBX_SUBFUNCTION_3 =
    { { "Number of bandwidth events that can be configured" , 7 , 0 } };
private final static String[][] DECODER_ECX_SUBFUNCTION_3 =
    { { "LOCRD"    , "Reads from local memory" } ,                    // bit 0
      { "REMRD"    , "Reads from remote memory" } ,
      { "LOCNTW"   , "Non-temporal writes to local memory" } ,
      { "REMLTW"   , "Non-temporal writes to remote memory" } ,
      { "LOCRDSL"  , "Reads from local memory identified as slow memory" } ,
      { "REMRDSL"  , "Reads from remote memory identified as slow memory" } ,
      { "DVICT"    , "Dirty victims to all types of memory" } };      // bit 6
private final static Object[][] DECODER_EAX_SUBFUNCTION_5 =
    { { "QM CTR, counter width, offset from 24"    , 7 , 0 } ,
      { "QM OVF, QM CTR bit 61 is an overflow bit" , 8 , 8 } };
private final static Object[][] DECODER_EBX_SUBFUNCTION_5 =
    { { "MAX ABMC, Maximum supported ABMC counter ID"    , 15 , 0 } };
private final static String[][] DECODER_ECX_SUBFUNCTION_5 =
    { { "SEL COS"  , "Bandwidth counter for COS instead RMID" } };   // bit 0

    

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
        if ( entries.length > 1 )  // check subfunction 1 present
            {
            a.add( interval );
            // EAX, subfunction 1
            dr = decodeBitfields
                ( "EAX", DECODER_EAX_SUBFUNCTION_1, entries[1].eax );
            a.addAll( dr.strings );
            // EDX, subfunction 1
            dr = decodeBitfields
                ( "EDX", DECODER_EDX_SUBFUNCTION_1, entries[1].edx );
            a.addAll( dr.strings );
            }
        if ( entries.length > 2 )  // check subfunction 2 present
            {
            a.add( interval );
            // EAX, subfunction 2
            dr = decodeBitfields
                ( "EAX", DECODER_EAX_SUBFUNCTION_2, entries[2].eax );
            a.addAll( dr.strings );
            // EDX, subfunction 2
            dr = decodeBitfields
                ( "EDX", DECODER_EDX_SUBFUNCTION_2, entries[2].edx );
            a.addAll( dr.strings );
            }
        if ( entries.length > 3 )  // check subfunction 3 present
            {
            a.add( interval );
            // EBX, subfunction 3
            dr = decodeBitfields
                ( "EBX", DECODER_EBX_SUBFUNCTION_3, entries[3].ebx );
            a.addAll( dr.strings );
            // ECX, subfunction 3
            strings = decodeBitmap
                ( "ECX", DECODER_ECX_SUBFUNCTION_3, entries[3].ecx );
            a.addAll( strings );
            }
        if ( entries.length > 5 )  // check subfunction 5 present
            {
            a.add( interval );
            // EAX, subfunction 5
            dr = decodeBitfields
                ( "EAX", DECODER_EAX_SUBFUNCTION_5, entries[5].eax );
            int counters = dr.values[0];
            if (counters > 0)
                {
                counters += 24;
                dr.strings.get(0)[4] = String.format( "%d bits", counters );    
                }
            a.addAll( dr.strings );
            // EBX, subfunction 5
            dr = decodeBitfields
                ( "EBX", DECODER_EBX_SUBFUNCTION_5, entries[5].ebx );
            a.addAll( dr.strings );
            // ECX, subfunction 5
            strings = decodeBitmap
                ( "ECX", DECODER_ECX_SUBFUNCTION_5, entries[5].ecx );
            a.addAll( strings );
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
