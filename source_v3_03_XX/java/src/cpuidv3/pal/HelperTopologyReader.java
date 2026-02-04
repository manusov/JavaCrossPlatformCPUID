/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Helper for read topology information from native platform.
This class used for Windows platforms only, based on WinAPI functions:
GetLogicalProcessorInformation(), GetLogicalProcessorInformationEx().
Note for Linux platforms other class used, based on /sysfs
virtual file system and not required native calls.
Caller must use fork for select appropriated code branch.

*/

package cpuidv3.pal;

import static cpuidv3.pal.PAL.REQUEST_GET_EXTENDED_TOPOLOGY;
import static cpuidv3.pal.PAL.REQUEST_GET_TOPOLOGY;

public class HelperTopologyReader 
{
    private final PAL pal;
    HelperTopologyReader( PAL p )
    {
        pal = p;
    }
    
    // Based on WinAPI GetLogicalProcessorInformation().
    public long[] getTopology()
    {
        return helperGetTopology( REQUEST_GET_TOPOLOGY );
    }
    
    // Based on WinAPI GetLogicalProcessorInformationEx().
    public long[] getExtendedTopology()
    {
        return helperGetTopology( REQUEST_GET_EXTENDED_TOPOLOGY );
    }
    
    private long[] helperGetTopology( int selector )
    {
        long[] data = null;
        if ( pal != null )
        {
            final int IPB_SIZE = 4;        // 4 qwords = 32 bytes.
            final int OPB_SIZE = 8192;     // 8192 qwords = 64K bytes.
            long[] ipb = new long[IPB_SIZE];
            long[] opb = new long[OPB_SIZE];
            ipb[0] = selector;
            ipb[1] = OPB_SIZE * 8;

            int jniStatus = pal.requestBinary( ipb, opb, IPB_SIZE, OPB_SIZE );
            if( jniStatus > 0 )
            {
                data = helperOutputArray( opb );
            }
       }
        return data;
    }
    
    private long[] helperOutputArray( long[] opbData )
    {
        long[] opbTemp = null;
        long bytesReturned = opbData[0] & 0xFFFFFFFFL;
        long statusReturned = opbData[0] >>> 32;
        if (( bytesReturned > 0 )&&( statusReturned == 0 ))
        {
            int tempSize = 16 + (int)( bytesReturned / 8 );
            if ( ( bytesReturned % 8 ) != 0 )
            {
                tempSize++;
            }
            opbTemp = new long[ tempSize ];
            System.arraycopy( opbData, 0, opbTemp, 0, tempSize );
        }    
        return opbTemp;
    }
}