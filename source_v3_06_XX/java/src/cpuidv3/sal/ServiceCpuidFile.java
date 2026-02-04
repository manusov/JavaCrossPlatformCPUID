/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class provides storage for CPUID dump binary data from loadable files (text
and binary dumps) include SMP support.

*/

package cpuidv3.sal;

class ServiceCpuidFile extends ServiceCpuidPhysical
{
    ServiceCpuidFile( SAL s ) { super( s ); }
    
    private long[][] binaryData = null;
    
    @Override boolean internalLoadBinaryData()
    {
        return true;
    }
    
    @Override boolean internalLoadNonAffinized()
    {
        return true;
    }

    @Override void clearBinaryData()
    {
        binaryData = null;
    }
    
    @Override long[][] getBinaryData()
    {
        return binaryData;
    }

    @Override void setBinaryData( long[][] data )
    {
        binaryData = data;
    }
    
    @Override void printSummaryReport()
    {
        // RESERVED.
    }
}
