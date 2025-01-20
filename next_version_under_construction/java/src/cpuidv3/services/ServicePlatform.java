/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class provides platform multiprocessing topology detection by OS API and
CPUID instruction information. include topology information, required
for CPUs enumeration.
Windows and Linux supported with decoder selection fork.

*/

package cpuidv3.services;

import static cpuidv3.services.PAL.REQUEST_GET_EXTENDED_TOPOLOGY;
import static cpuidv3.services.PAL.REQUEST_GET_TOPOLOGY;
import cpuidv3.services.SAL.OSTYPE;


class ServicePlatform extends Service
{
    ServicePlatform( SAL s ) { super( s ); }
    
    private long[][] binaryData = null;
    
    @Override boolean internalLoadBinaryData()
    {
        boolean status = false;
        if ( binaryData != null )
        {   // Use cached binary data, if already loaded.
            status = true;
        }
        else
        {   // Create new binary data if yet not loaded or cleared.
            PAL pal = sal.getPal();
            if( pal != null )
            {
                OSTYPE osType = pal.getPlatformType();
                if ( ( osType == OSTYPE.WIN32 )||( osType == OSTYPE.WIN64 ) )
                {
                    final int IPB_SIZE = 4;        // 4 qwords = 32 bytes.
                    final int OPB_SIZE = 2048;     // 2048 qwords = 16384 bytes.
                    long[] ipb = new long[IPB_SIZE];
                    long[] opb = new long[OPB_SIZE];
                    
            // Try WinAPI GetLogicalProcessorInformationEx().
                    ipb[0] = REQUEST_GET_EXTENDED_TOPOLOGY;
                    ipb[1] = OPB_SIZE * 8;
                    int jniStatus = pal.platformRequest
                        ( ipb, opb, IPB_SIZE, OPB_SIZE );
                    if( jniStatus > 0 )
                    {
                        long[] temp = helperOutputArray( opb );
                        if( temp != null )
                        {
                            binaryData = new long[][]{ temp , null };
                            status = true;
                        }
                    }
            
            // Try WinAPI GetLogicalProcessorInformation() if previous failed.
                    if( !status )
                    {
                        ipb[0] = REQUEST_GET_TOPOLOGY;
                        ipb[1] = OPB_SIZE * 8;
                        jniStatus = pal.platformRequest
                            ( ipb, opb, IPB_SIZE, OPB_SIZE );
                        if( jniStatus > 0 )
                        {
                            long[] temp = helperOutputArray( opb );
                            if( temp != null )
                            {
                                binaryData = new long[][]{ null , temp };
                                status = true;
                            }
                        }
                    }
                }
                else if ( ( osType == OSTYPE.LINUX32 )||
                          ( osType == OSTYPE.LINUX64 ) )
                {
                    status = true;
                }
            }
        }
        return status;
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
    
    @Override void clearBinaryData()
    {
        binaryData = null;
    }
    
    @Override long[][] getBinaryData()
    {
        return binaryData;
    }

}
