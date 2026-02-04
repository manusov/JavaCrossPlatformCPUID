/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Helper for read system information from native platform.

*/

package cpuidv3.pal;

import static cpuidv3.pal.PAL.REQUEST_GET_PLATFORM_INFO;

public class HelperPlatformReader 
{
    private final PAL pal;
    HelperPlatformReader( PAL p )
    {
        pal = p;
    }

    public long[] getPlatformInfo()
    {
        long[] data = null;
        if ( pal != null )
        {
            final int IPB_SIZE = 2;          // 2 qwords = 16 bytes.
            final int OPB_SIZE = 512;        // 512 qwords = 4096 bytes.
            long[] ipb = new long[IPB_SIZE];
            long[] opb = new long[OPB_SIZE];
            ipb[0] = REQUEST_GET_PLATFORM_INFO;
            ipb[1] = OPB_SIZE * 8;
            
            int jniStatus = pal.requestBinary( ipb, opb, IPB_SIZE, OPB_SIZE );
            if( jniStatus > 0 )
            {
                int infoStatus = (int)( opb[0] & 0xFFFFFFFFL );
                int cpuCount = (int)( opb[1] >>> 32 );
                int longsCount = (int)( opb[2] & 0xFFFFFFFFL ) + 1;
                if(( infoStatus == 0 )&&( cpuCount > 0 )&&( longsCount > 1 ))
                {
                    long[] tempData = new long[longsCount];
                    tempData[0] = cpuCount;
                    System.arraycopy( opb, 3, tempData, 1, longsCount - 1 );
                    data = tempData;
                }
            }
        }
        return data;
    }
}
