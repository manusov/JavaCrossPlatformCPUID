/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Helper for read CPU context management bitmaps from native platform.
This operation based on CPUID instruction plus
read XCR0 register by XGETBV instruction.

Note. Supported context features bitmap = results of CPUID function 0Dh.
Note. Enabled context features bitmap = XCR0 register loaded by OS.

*/

package cpuidv3.pal;

import static cpuidv3.pal.PAL.REQUEST_GET_OS_CONTEXT;

class HelperOsContextReader 
{
    private final PAL pal;
    HelperOsContextReader( PAL p )
    {
        pal = p;
    }

    long[] getOsContext()
    {
        long[] data = null;
        if ( pal != null )
        {
            final int IPB_SIZE = 1;          // 1 qword = 8 bytes.
            final int OPB_SIZE = 4;          // 4 qwords = 32 bytes.
            long[] ipb = new long[IPB_SIZE];
            long[] opb = new long[OPB_SIZE];
            ipb[0] = REQUEST_GET_OS_CONTEXT;
            
            int jniStatus = pal.requestBinary( ipb, opb, IPB_SIZE, OPB_SIZE );
            if( jniStatus > 0 )
            {
                int contextStatus = (int)( opb[0] >>> 32 );
                int longsCount = (int)( opb[0] & 0xFFFFFFFFL );
                if(( contextStatus == 0 )&&( longsCount == 2 ))
                {
                    long[] tempData = new long[longsCount];
                    System.arraycopy( opb, 2, tempData, 0, longsCount );
                    data = tempData;
                }
            }
        }
        return data;
    }
}
