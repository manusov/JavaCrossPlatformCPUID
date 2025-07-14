/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 0000000Fh =
Platform quality of service enumeration.
This function also known as Intel Resource Director Technology (RDT) monitoring.

*/

package cpuidv3.servicecpuid;

import java.util.ArrayList;

class Cpuid0000000F extends ParameterFunctionCpuid
{
Cpuid0000000F()
    { setFunction( 0x0000000F ); }

@Override String getLongName()
    { return "Platform quality of service enumeration"; }

// Control tables for results decoding
private final static Object[][] DECODER_EBX_SUBFUNCTION_0 =
    { { "Maximum range of RMID within this physical CPU" , 31 , 0 } };
private final static String[][] DECODER_EDX_SUBFUNCTION_0 =
    { { "x"      , "Reserved" } ,
      { "L3 QoS" , "L3 cache quality of service monitoring" } };
private final static Object[][] DECODER_EAX_SUBFUNCTION_1 =
    { { "Encode counter width in bits, offset from 24"    , 7 , 0 } ,
      { "Bit 61 of IA32_QM_CTR MSR is overflow indicator" , 8 , 8 } ,
      { "Non-CPU agent RDT CMT" , 9 , 9 } ,
      { "Non-CPU agent RDT MBM" , 10 , 10 } };
private final static Object[][] DECODER_EBX_SUBFUNCTION_1 =
    { { "Conversion factor from IA32_QM_CTR to metric" , 31 , 0 } };
private final static Object[][] DECODER_ECX_SUBFUNCTION_1 =
    { { "Maximum range of RMID of this resource type" , 31 , 0 } };
private final static String[][] DECODER_EDX_SUBFUNCTION_1 =
    { { "L3 OM" , "L3 occupancy monitoring"       } ,
      { "L3 TB" , "L3 total bandwidth monitoring" } ,
      { "L3 LB" , "L3 local bandwidth monitoring" } ,
      { "x"     , "Reserved" }                    };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EBX, subfunction 0
        dr = decodeBitfields
            ( "EBX", DECODER_EBX_SUBFUNCTION_0, entries[0].ebx );
        a.addAll( dr.strings );
        // EDX, subfunction 0
        strings = decodeBitmap
            ( "EDX", DECODER_EDX_SUBFUNCTION_0, entries[0].edx );
        a.addAll( strings );
        if( entries.length > 1 )
            {
            for( int i=1; i<entries.length; i++ )
                {
                a.add( interval );
                // EAX, subfunction 1
                dr = decodeBitfields
                    ( "EAX", DECODER_EAX_SUBFUNCTION_1, entries[i].eax );
                int n = dr.values[0] + 24;
                if( ( entries[0].edx & 2 ) == 0 )
                    {
                    dr.strings.get(0)[4] = "n/a";
                    }
                else if( ( n == 24 )||( n == 25 )||( n == 44 )||( n == 61 ) )
                    {
                    dr.strings.get(0)[4] = String.format( "%d Bits", n );    
                    }
                else
                    {
                    dr.strings.get(0)[4] = "Unknown value";
                    }
                dr.strings.get(1)[4] = 
                        ( dr.values[1] == 0 ) ? "not supported" : "supported";
                a.addAll( dr.strings );
                // EBX, subfunction 1
                dr = decodeBitfields
                    ( "EBX", DECODER_EBX_SUBFUNCTION_1, entries[i].ebx );
                a.addAll( dr.strings );
                // ECX, subfunction 1
                dr = decodeBitfields
                    ( "ECX", DECODER_ECX_SUBFUNCTION_1, entries[i].ecx );
                a.addAll( dr.strings );
                // EDX, subfunction 1
                strings = decodeBitmap
                    ( "EDX", DECODER_EDX_SUBFUNCTION_1, entries[i].edx );
                a.addAll( strings );
                }
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
