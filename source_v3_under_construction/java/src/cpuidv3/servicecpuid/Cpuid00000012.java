/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 00000012h =
Intel security guard extensions information.

*/

package cpuidv3.servicecpuid;

import java.util.ArrayList;

class Cpuid00000012 extends ParameterFunctionCpuid
{
Cpuid00000012()
    { setFunction( 0x00000012 ); }

@Override String getLongName()
    { return "Intel security guard extensions information"; }

// Control tables for results decoding
private final static String[][] DECODER_EAX_SUBFUNCTION_0 =
    { { "SGX1"       , "SGX1 instruction set" } , 
      { "SGX2"       , "SGX2 instruction set" } ,
      { "x"          , "Reserved" } ,   // bit 2
      { "x"          , "Reserved" } , 
      { "x"          , "Reserved" } ,   // bit 4
      { "ENCLV"      , "ENCLV instruction leaves" } , 
      { "ENCLS"      , "ENCLS instruction leaves" } ,
      { "ENCLU R2"   , "EVERIFYREPORT2 leaf of ENCLU instruction" } ,
      { "x"          , "Reserved" } ,
      { "x"          , "Reserved" } ,
      { "EUPDATESVN" , "EUPDATESVN leaf of ENCLS instruction" } ,            // bit 10
      { "EDECCSSA"   , "EDECCSSA leaf of ENCLU instruction" } ,              // bit 11
      { "SGX 256"    , "Intel SGX leaf functions EGETKEY256, EREPORT2" } };  // bit 12
private final static Object[][] DECODER_EBX_SUBFUNCTION_0 =
    { { "MISCSELECT extended features bit vector" , 31 , 0 } };
private final static Object[][] DECODER_EDX_SUBFUNCTION_0 =
    { { "Enclave size bits count in non-64 bit mode" ,  7 , 0 } , 
      { "Enclave size bits count in 64-bit mode"     , 15 , 8 } };
private final static Object[][] DECODER_EAX_SUBFUNCTION_1 =
    { { "Validity bitmap for SECS.ATTRIBUTES[31-0]" ,  31 , 0 } };
private final static Object[][] DECODER_EBX_SUBFUNCTION_1 =
    { { "Validity bitmap for SECS.ATTRIBUTES[63-32]" ,  31 , 0 } };
private final static Object[][] DECODER_ECX_SUBFUNCTION_1 =
    { { "Validity bitmap for SECS.ATTRIBUTES[95-64]" ,  31 , 0 } };
private final static Object[][] DECODER_EDX_SUBFUNCTION_1 =
    { { "Validity bitmap for SECS.ATTRIBUTES[127-96]" ,  31 , 0 } };
private final static Object[][] DECODER_EAX_SUBFUNCTION_2 =
    { { "Sub-leaf tag"          ,  3 ,  0 } ,
      { "Physical address bits" , 31 , 12 } };
private final static Object[][] DECODER_EBX_SUBFUNCTION_2 =
    { { "Physical address bits" ,  19 , 0 } };
private final static Object[][] DECODER_ECX_SUBFUNCTION_2 =
    { { "EPC section tag"  ,  3 ,  0 } ,
      { "Size bits"        , 31 , 12 } };
private final static Object[][] DECODER_EDX_SUBFUNCTION_2 =
    { { "Size bits" ,  19 , 0 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX, subfunction 0
        strings = decodeBitmap
            ( "EAX", DECODER_EAX_SUBFUNCTION_0, entries[0].eax );
        a.addAll( strings );
        // EBX, subfunction 0
        dr = decodeBitfields
            ( "EBX", DECODER_EBX_SUBFUNCTION_0, entries[0].ebx );
        a.addAll( dr.strings );
        // EDX, subfunction 0
        dr = decodeBitfields
            ( "EDX", DECODER_EDX_SUBFUNCTION_0, entries[0].edx );
        dr.strings.get(0)[4] = writeSize( dr.values[0] );
        dr.strings.get(1)[4] = writeSize( dr.values[1] );
        a.addAll( dr.strings );
        if ( entries.length > 1 )
            {
            a.add( interval );
            // EAX, subfunction 1
            dr = decodeBitfields
                ( "EAX", DECODER_EAX_SUBFUNCTION_1, entries[1].eax );
            a.addAll( dr.strings );
            // EBX, subfunction 1
            dr = decodeBitfields
                ( "EBX", DECODER_EBX_SUBFUNCTION_1, entries[1].ebx );
            a.addAll( dr.strings );
            // ECX, subfunction 1
            dr = decodeBitfields
                ( "ECX", DECODER_ECX_SUBFUNCTION_1, entries[1].ecx );
            a.addAll( dr.strings );
            // EDX, subfunction 1
            dr = decodeBitfields
                ( "EDX", DECODER_EDX_SUBFUNCTION_1, entries[1].edx );
            a.addAll( dr.strings );
            if ( entries.length > 2 )
                {
                for( int i=2; i<entries.length; i++ )
                    {
                    a.add( interval );
                    // EAX, subfunction 2+
                    dr = decodeBitfields
                        ( "EAX", DECODER_EAX_SUBFUNCTION_2, entries[2].eax );
                    a.addAll( dr.strings );
                    // EBX, subfunction 2+
                    dr = decodeBitfields
                        ( "EBX", DECODER_EBX_SUBFUNCTION_2, entries[2].ebx );
                    a.addAll( dr.strings );
                    // ECX, subfunction 2+
                    dr = decodeBitfields
                        ( "ECX", DECODER_ECX_SUBFUNCTION_2, entries[2].ecx );
                    a.addAll( dr.strings );
                    // EDX, subfunction 2+
                    dr = decodeBitfields
                        ( "EDX", DECODER_EDX_SUBFUNCTION_2, entries[2].edx );
                    a.addAll( dr.strings );
                    }
                }
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
