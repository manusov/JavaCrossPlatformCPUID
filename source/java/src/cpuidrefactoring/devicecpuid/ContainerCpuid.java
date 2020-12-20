/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Container class for communication between 
CPUID functions and summary screens classes.
*/

package cpuidrefactoring.devicecpuid;

import cpuidrefactoring.database.VendorDetectPhysical.VENDOR_T;
import cpuidrefactoring.database.VendorDetectVirtual.HYPERVISOR_T;
import java.util.ArrayList;

class ContainerCpuid 
{
private long[] binaryDump;
private final ReservedFunctionCpuid[] standardFunctions;
private final ReservedFunctionCpuid[] extendedFunctions;
private final ReservedFunctionCpuid[] virtualFunctions;
private final ReservedFunctionCpuid[] vendorFunctions;
private final SummaryCpuid[] summaryScreens;
private ReservedFunctionCpuid[] detectedFunctions;

ContainerCpuid( ReservedFunctionCpuid[] sf, ReservedFunctionCpuid[] ef,
                ReservedFunctionCpuid[] nf, ReservedFunctionCpuid[] vf, 
                SummaryCpuid[] ss )
    {
    standardFunctions = sf;
    extendedFunctions = ef;
    vendorFunctions   = nf;
    virtualFunctions  = vf;
    summaryScreens    = ss;
    }

long[] getBinaryDump()
    {
    return binaryDump;
    }

void setBinaryDump( long[] bd )
    {
    binaryDump = bd;
    }

ReservedFunctionCpuid[] getStandardFunctions()
    {
    return standardFunctions;
    }

ReservedFunctionCpuid[] getExtendedFunctions()
    {
    return extendedFunctions;
    }

ReservedFunctionCpuid[] getVendorFunctions()
    {
    return vendorFunctions;
    }

ReservedFunctionCpuid[] getVirtualFunctions()
    {
    return virtualFunctions;
    }

SummaryCpuid[] getSummaryScreens()
    {
    return summaryScreens;
    }

EntryCpuid[] buildEntries( int f )  // this for selected function f
    {
    return helperEntries( f, false );
    }

EntryCpuid[] buildEntries()  // this for all detected functions
    {
    return helperEntries( 0, true );
    }

private EntryCpuid[] helperEntries( int f, boolean b )
    {
    if ( binaryDump == null ) return null;
    int n = binaryDump.length;
    if ( ( n <=0 ) || ( n%4 != 0 ) ) return null;
    int m = (int)( ( binaryDump[0] ) & 0x000003FFL ) * 4 + 1;
    if ( ( m < 0 ) || ( m > 512 ) ) return null;
    
    ArrayList<EntryCpuid> a = new ArrayList<>();
    long f1 = f & 0xFFFFFFFFL;  // otherwise negative binary match is mismatch
    for( int i=4; i<m; i+=4 )   // number of iterations = n/4
        {   // high dword of qword 0 = dword 1
        if ( b || ( binaryDump[i] >>> 32 == f1 ) )
            {
            EntryCpuid e = new EntryCpuid( );
            e.id          = (int)( binaryDump[i]   & 0xFFFFFFFFL );
            e.function    = (int)( binaryDump[i]   >>> 32        );
            e.subfunction = (int)( binaryDump[i+1] & 0xFFFFFFFFL );
            e.pass        = (int)( binaryDump[i+1] >>> 32        );
            e.eax         = (int)( binaryDump[i+2] & 0xFFFFFFFFL );
            e.ebx         = (int)( binaryDump[i+2] >>> 32        );
            e.ecx         = (int)( binaryDump[i+3] & 0xFFFFFFFFL );
            e.edx         = (int)( binaryDump[i+3] >>> 32        );
            a.add( e );
            }
        }
    if ( ! a.isEmpty() ) return a.toArray( new EntryCpuid[a.size()] );
    else return null;
    }

ReservedFunctionCpuid[] getDetectedFunctions()
    {
    return detectedFunctions; 
    }

void setDetectedFunctions( ReservedFunctionCpuid[] x )
    {
    detectedFunctions = x;
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

private VENDOR_T     cpuVendor = null;
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
