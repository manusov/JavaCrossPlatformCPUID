/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 0000001Ah = 
Hybrid processor topology information.

*/

package cpuidv3.servicecpuid;

import static cpuidv3.servicecpuid.ReservedFunctionCpuid.container;
import cpuidv3.sal.EntryCpuidSubfunction;
import java.util.ArrayList;

class Cpuid0000001A extends ParameterFunctionCpuid implements IHybrid
{
Cpuid0000001A()
    { setFunction( 0x0000001A ); }

@Override String getLongName()
    { return "Hybrid processor information"; }

// Control tables for results decoding, subfunction 0, only 0 subf. supported
private final static Object[][] DECODER_EAX_SUBFUNCTION_0 =
    { { "Enumerates the native model ID"  , 23 , 0  } ,
      { "Enumerates the native core type" , 31 , 24 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX, subfunction 0
        dr = decodeBitfields
            ( "EAX", DECODER_EAX_SUBFUNCTION_0, entries[0].eax );
        switch( dr.values[1] )
            {
            case 0:
                dr.strings.get(1)[4] = "n/a";
                break;
            case 0x10:
            case 0x30:
                dr.strings.get(1)[4] = "Reserved";
                break;
            case 0x20:
                dr.strings.get(1)[4] = "Intel Atom";
                break;
            case 0x40:
                dr.strings.get(1)[4] = "Intel Core";
                break;
            default:
                dr.strings.get(1)[4] = "Unknown";
                break;
            }
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }

    @Override public HybridReturn getHybrid()
    {
        HYBRID_CPU resultType = HYBRID_CPU.DEFAULT;
        String resultName = "n/a";
        boolean cacheL3present = false;
        int smtThreads = 1;
        
        EntryCpuidSubfunction[] fn4 = container.buildEntries( 0x00000004 );
        if( fn4 != null )
        {
            for ( EntryCpuidSubfunction e : fn4 )
            {
                if ( ( e.eax & 0xFF ) == 0x63) 
                {
                    cacheL3present = true;
                    break;
                }
            }
            
            for ( EntryCpuidSubfunction e : fn4) 
            {
                if ( ( e.eax & 0xFF) == 0x21 ) 
                {
        //          smtThreads = (( e.eax >>> 14) & 0xFFF ) + 1;
                    break;
                }
            }
        }
        
        EntryCpuidSubfunction[] fn1F = container.buildEntries( 0x0000001F );
        if( fn1F != null )
        {
            for ( EntryCpuidSubfunction e : fn1F ) 
            {
                if ( ( e.ecx & 0xFF00 ) == 0x0100) 
                {
                    int a = e.ebx & 0xFFFF;
                    if ( a > 0 )
                    {
                        smtThreads = a;
                    }
                }
            }
        }
       
        EntryCpuidSubfunction[] fn7 = container.buildEntries( 0x00000007 );
        if ( ( fn7 != null )&&( fn7.length > 0 )&&
             ( ( fn7[0].edx & 0x00008000 ) != 0 )&&
             ( entries != null )&&( entries.length > 0 )&&
             ( entries[0].eax != 0 ) )
        {
            int hybridType = entries[0].eax >>> 24;
            switch ( hybridType )
            {
                case 0:
                    resultType = HYBRID_CPU.UNKNOWN;
                    resultName = "?";
                    break;
                case 0x10:
                case 0x30:
                    resultType = HYBRID_CPU.RESERVED;
                    resultName = "Reserved";
                    break;
                case 0x20:
                    if ( cacheL3present )
                    {
                        resultType = HYBRID_CPU.E_CORE;
                        resultName = "E-Core ( Intel Atom )";
                    }
                    else
                    {
                        resultType = HYBRID_CPU.LP_E_CORE;
                        resultName = "LP E-Core ( Intel Atom )";
                    }
                    break;
                case 0x40:
                    resultType = HYBRID_CPU.P_CORE;
                    resultName = "P-Core ( Intel Core )";
                    break;
                default:
                    resultType = HYBRID_CPU.UNKNOWN;
                    resultName = "Unknown";
                    break;
            }
        }
        return new HybridReturn( resultType, resultName, smtThreads );
    }
}
