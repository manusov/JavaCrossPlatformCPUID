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

package cpuidv3.sal;

import cpuidv3.pal.PAL;
import cpuidv3.pal.PAL.OS_TYPE;

class ServiceTopology extends Service
{
    ServiceTopology( SAL s ) { super( s ); }
    
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
                OS_TYPE osType = pal.getOsType();
                if ( ( osType == OS_TYPE.WIN32 )||( osType == OS_TYPE.WIN64 ) )
                {
    // Try WinAPI GetLogicalProcessorInformationEx().
                    long[] temp = pal.getExtendedTopology();
                    if( temp != null )
                    {
                        binaryData = new long[][]{ temp , null };
                        status = true;
                    }
    // Try WinAPI GetLogicalProcessorInformation() if previous failed.
                    if( !status )
                    {
                        temp = pal.getTopology();
                        binaryData = new long[][]{ null , temp };
                        status = true;
                    }
                }
                else if ( ( osType == OS_TYPE.LINUX32 )||
                          ( osType == OS_TYPE.LINUX64 ) )
                {
                    status = true;
                }
            }
        }
        return status;
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
