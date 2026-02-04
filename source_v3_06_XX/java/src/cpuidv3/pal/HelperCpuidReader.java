/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Helper for read CPUID information from native platform.
This operation based on CPUID instruction.

*/

package cpuidv3.pal;

import static cpuidv3.pal.PAL.REQUEST_GET_CPUID;
import static cpuidv3.pal.PAL.REQUEST_GET_CPUID_AFFINIZED;
import static cpuidv3.pal.PAL.REQUEST_GET_CPUID_SUBFUNCTION;
import static cpuidv3.pal.PAL.REQUEST_GET_CPUID_SUBFUNCTION_AFFINIZED;

class HelperCpuidReader
{
    private final PAL pal;
    HelperCpuidReader( PAL p )
    {
        pal = p;
    }
    
    long[] getCpuid()
    {
        long[] data = null;
        if ( pal != null )
        {
            final int IPB_SIZE = 4;         // 4 qwords = 32 bytes.
            final int OPB_SIZE = 2048;      // 2048 qwords = 16384 bytes.
            long[] ipb = new long[IPB_SIZE];
            long[] opb = new long[OPB_SIZE];
            ipb[0] = REQUEST_GET_CPUID;
            ipb[1] = OPB_SIZE * 8;
            
            int jniStatus = pal.requestBinary( ipb, opb, IPB_SIZE, OPB_SIZE );
            if( jniStatus > 0 )
            {
                int cpuidStatus = (int)( opb[0] >>> 32 );
                int longsCount = ((int)( opb[0] & 0xFFFFFFFFL )) * 4;
                if(( cpuidStatus == 0 )&&( longsCount > 0 ))
                {
                    long[] tempData = new long[longsCount];
                    System.arraycopy( opb, 4, tempData, 0, longsCount );
                    data = tempData;
                }
            }
        }
        return data;
    }
    
    long[] getCpuidSubfunction( int function, int subfunction )
    {
        long[] data = null;
        if ( pal != null )
        {
            final int IPB_SIZE = 4;         // 4 qwords = 32 bytes.
            final int OPB_SIZE = 2048;      // 2048 qwords = 16384 bytes.
            long[] ipb = new long[IPB_SIZE];
            long[] opb = new long[OPB_SIZE];
            ipb[0] = REQUEST_GET_CPUID_SUBFUNCTION;
            
            long fnc = function;
            long sfnc = subfunction;
            ipb[1] = ( fnc & 0xFFFFFFFFL ) | ( sfnc << 32 );
            
            int jniStatus = pal.requestBinary( ipb, opb, IPB_SIZE, OPB_SIZE );
            if( jniStatus > 0 )
            {
                int cpuidStatus = (int)( opb[0] >>> 32 );
                int longsCount = ((int)( opb[0] & 0xFFFFFFFFL )) * 4;
                if(( cpuidStatus == 0 )&&( longsCount == 4 ))
                {
                    long[] tempData = new long[longsCount];
                    System.arraycopy( opb, 4, tempData, 0, longsCount );
                    data = tempData;
                }
            }
        }
        return data;
    }
    
    long[] getCpuidAffinized( int cpuNumber )
    {
        long[] data = null;
        if ( pal != null )
        {
            final int IPB_SIZE = 4;         // 4 qwords = 32 bytes.
            final int OPB_SIZE = 2048;      // 2048 qwords = 16384 bytes.
            long[] ipb = new long[IPB_SIZE];
            long[] opb = new long[OPB_SIZE];
            ipb[0] = REQUEST_GET_CPUID_AFFINIZED;
            ipb[1] = OPB_SIZE * 8;
            ipb[2] = cpuNumber;
            
            int jniStatus = pal.requestBinary( ipb, opb, IPB_SIZE, OPB_SIZE );
            if( jniStatus > 0 )
            {
                int cpuidStatus = (int)( opb[0] >>> 32 );
                int longsCount = ((int)( opb[0] & 0xFFFFFFFFL )) * 4;
                if(( cpuidStatus == 0 )&&( longsCount > 0 ))
                {
                    long[] tempData = new long[longsCount];
                    System.arraycopy( opb, 4, tempData, 0, longsCount );
                    data = tempData;
                }
            }
        }
        return data;
    }
    
    long[] getCpuidSubfunctionAffinized
        ( int function, int subfunction, int cpuNumber )
    {
        long[] data = null;
        if ( pal != null )
        {
            final int IPB_SIZE = 4;         // 4 qwords = 32 bytes.
            final int OPB_SIZE = 2048;      // 2048 qwords = 16384 bytes.
            long[] ipb = new long[IPB_SIZE];
            long[] opb = new long[OPB_SIZE];
            ipb[0] = REQUEST_GET_CPUID_SUBFUNCTION_AFFINIZED;
            
            long fnc = function;
            long sfnc = subfunction;
            ipb[1] = ( fnc & 0xFFFFFFFFL ) | ( sfnc << 32 );
            ipb[2] = cpuNumber;
            
            int jniStatus = pal.requestBinary( ipb, opb, IPB_SIZE, OPB_SIZE );
            if( jniStatus > 0 )
            {
                int cpuidStatus = (int)( opb[0] >>> 32 );
                int longsCount = ((int)( opb[0] & 0xFFFFFFFFL )) * 4;
                if(( cpuidStatus == 0 )&&( longsCount == 4 ))
                {
                    long[] tempData = new long[longsCount];
                    System.arraycopy( opb, 4, tempData, 0, longsCount );
                    data = tempData;
                }
            }
        }
        return data;
    }
}
