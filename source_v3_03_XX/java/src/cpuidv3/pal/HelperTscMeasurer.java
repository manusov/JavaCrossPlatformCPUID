/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Helper for measure CPU TSC (Time Stamp Counter) frequency for native platform.

*/

package cpuidv3.pal;

import static cpuidv3.pal.PAL.REQUEST_MEASURE_TSC_FREQUENCY;

public class HelperTscMeasurer 
{
    private final PAL pal;
    HelperTscMeasurer( PAL p )
    {
        pal = p;
    }

    public long[] measureTscFrequency()
    {
        long[] data = null;
        if ( pal != null )
        {
            final int IPB_SIZE = 1;          // 1 qword = 8 bytes.
            final int OPB_SIZE = 2;          // 2 qwords = 16 bytes.
            long[] ipb = new long[IPB_SIZE];
            long[] opb = new long[OPB_SIZE];
            ipb[0] = REQUEST_MEASURE_TSC_FREQUENCY;
            
            int jniStatus = pal.requestBinary( ipb, opb, IPB_SIZE, OPB_SIZE );
            if( jniStatus > 0 )
            {
                int contextStatus = (int)( opb[0] >>> 32 );
                int longsCount = (int)( opb[0] & 0xFFFFFFFFL );
                if(( contextStatus == 0 )&&( longsCount == 1 ))
                {
                    long[] tempData = new long[longsCount];
                    System.arraycopy( opb, 1, tempData, 0, longsCount );
                    data = tempData;
                }
            }
        }
        return data;
    }
}
