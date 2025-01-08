/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Container class for communication between CPUID functions and summary
screens classes.

*/

package dumploader.cpuid;

import dumploader.cpudata.VendorDetectPhysical.VENDOR_T;
import dumploader.cpudata.VendorDetectVirtual.HYPERVISOR_T;
import dumploader.cpuenum.EntryCpuidSubfunction;
import java.util.ArrayList;

class ContainerCpuid 
{
    private EntryCpuidSubfunction[] entriesDump;
    public void setEntriesDump( EntryCpuidSubfunction[] e )
    {
        entriesDump = e;
    }
    
    private ReservedFunctionCpuid[] detectedFunctions;
    
    ReservedFunctionCpuid[] getDetectedFunctions()
    {
        return detectedFunctions; 
    }

    void setDetectedFunctions( ReservedFunctionCpuid[] x )
    {
        detectedFunctions = x;
    }
    
    private CpuidSummary cpuidSummary;
    
    CpuidSummary getCpuidSummary()
    {
        return cpuidSummary;
    }

    void setCpuidSummary( CpuidSummary x )
    {
        cpuidSummary = x;
    }

    private CpuidDump cpuidDump;
    
    CpuidDump getCpuidDump()
    {
        return cpuidDump;
    }

    void setCpuidDump( CpuidDump x )
    {
        cpuidDump = x;
    }
    
    ReservedFunctionCpuid findFunction( int x )
    {
        ReservedFunctionCpuid f = null;
        if ( detectedFunctions != null )
        {
            int n = detectedFunctions.length;
            for( int i=0; i<n; i++ )
            {
                if ( detectedFunctions[i].getFunction() == x )
                {
                    f = detectedFunctions[i];
                    break;
                }
            }
        }
    return f;    
    }
    
    EntryCpuidSubfunction[] buildEntries( int f )  // this for selected function f
    {
        return helperEntries( f, false );
    }

    EntryCpuidSubfunction[] buildEntries()  // this for all detected functions
    {
        return helperEntries( 0, true );
    }
    
    private EntryCpuidSubfunction[] helperEntries( int f, boolean b )
    {
        ArrayList<EntryCpuidSubfunction> a = new ArrayList<>();
        if( entriesDump != null )
        {
            for ( EntryCpuidSubfunction entry : entriesDump ) 
            {
                if ( b || entry.function == f )
                {
                    a.add( entry );
                }
            }
        }
        return a.isEmpty() ? null :
                a.toArray( new EntryCpuidSubfunction[a.size()] );
    }
    
    private VENDOR_T cpuVendor = null;
    VENDOR_T getCpuVendor()
    {
        return cpuVendor; 
    }
    void setCpuVendor( VENDOR_T v ) 
    {
        cpuVendor = v; 
    }

    private HYPERVISOR_T vmmVendor = null;
    HYPERVISOR_T getVmmVendor()
    {
        return vmmVendor;
    }
    void setVmmVendor( HYPERVISOR_T h ) 
    {
        vmmVendor = h; 
    }
}
