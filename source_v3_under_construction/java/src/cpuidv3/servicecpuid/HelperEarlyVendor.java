/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Helper class for early vendor detection (processor and hypervisor vendors).
Results can be used for vendor-specific operations.

*/

package cpuidv3.servicecpuid;

import cpuidv3.servicecpudata.VendorDetectPhysical;
import cpuidv3.servicecpudata.VendorDetectPhysical.VENDOR_T;
import static cpuidv3.servicecpudata.VendorDetectPhysical.VENDOR_T.VENDOR_CYRIX;
import static cpuidv3.servicecpudata.VendorDetectPhysical.VENDOR_T.VENDOR_TRANSMETA;
import cpuidv3.servicecpudata.VendorDetectVirtual;
import cpuidv3.servicecpudata.VendorDetectVirtual.HYPERVISOR_T;
import static cpuidv3.servicecpudata.VendorDetectVirtual.HYPERVISOR_T.HYPERVISOR_MICROSOFT;
import cpuidv3.sal.EntryCpuidSubfunction;

public class HelperEarlyVendor 
{
    private final static int STANDARD_KEY = 0;
    private final static int VIRTUAL_KEY  = 0x40000000;

/*
Support early CPU and VMM vendor detection.
Database usage 1 of 3 = Early vendor detection.
See also: DeviceCpuid.java , CpuidSummary.java.
Early detect CPU vendor, this operation reserved for repeat receive CPUID
binary data with added vendor-specific functions. Note cannot add this
functions for all CPUs at first pass, because hardware failures and wrong
results possible if some incompatible vendor functions used.
Additionally, static classes store detection results and results can be
read later without re-detection.
*/
public static void earlyVendorDetect
        ( EntryCpuidSubfunction[] entries, boolean physical )
    {
        String       scpu = earlyExtractVendorString( STANDARD_KEY, entries );
        String       svmm = earlyExtractVendorString( VIRTUAL_KEY, entries );
        VENDOR_T     vcpu = VendorDetectPhysical.earlyDetect( scpu );
        HYPERVISOR_T vvmm = VendorDetectVirtual.earlyDetect( svmm );
/*            
Reserved for secondary read binary data (with vendor-specific functions),
if vendors match patterns.
physical flag:
true  = means physical or virtual CPU detection, secondary read possible
false = means load dump from file, secondary read not possible.
*/
        if ( physical && vcpu == VENDOR_CYRIX )
        {
            // reserved for secondary read with vendor-specific additions
        }
        
        else if ( physical && vcpu == VENDOR_TRANSMETA )
        {
            // reserved for secondary read with vendor-specific additions
        }
          
        if ( physical && vvmm == HYPERVISOR_MICROSOFT )
        {
            // reserved for secondary read with vendor-specific additions
        }
    }
    
// Helper for extract CPU and VMM vendor strings from binary dump
// possible optimization, see also ReservedFunctionCpuid.java,
// same functionality duplication.

private static String earlyExtractVendorString
        ( int key, EntryCpuidSubfunction[] entries )
    {
        boolean b = false;
        StringBuilder sb = new StringBuilder( "" );

        if ( entries != null )
        {
            for ( EntryCpuidSubfunction entry : entries) 
            {
                if ( entry.function == key ) 
                {
                    int[] signature;
                    // for virtual function 40000000h order is EBX-ECX-EDX.
                    if ( key == VIRTUAL_KEY ) 
                    {
                        signature = new int[]{entry.ebx, entry.ecx, entry.edx};
                    }
                    // for functions 00000000h, 80000000h order is EBX-EDX-ECX.
                    else 
                    {
                        signature = new int[]{entry.ebx, entry.edx, entry.ecx};
                    }
                    // Cycle for convert 3 integer numbers to 12-char string.
                    for( int j=0; j<3; j++ )
                    {
                        int d = signature[j];
                        // Cycle convert int to 4 chars.
                        for( int k=0; k<4; k++ )
                        {
                            char c = (char)( d & 0xFF );
                            if ( c != 0 )
                            {
                                if ( ( c < ' ' )||( c > '}' ) )
                                {
                                    c = '.'; 
                                }
                                sb.append( c );
                                b = true;
                            }
                            d = d >>> 8;
                        }
                    }
                }
            }
        }
        
        return b ? sb.toString() : null;
    }
/*
End of database usage 1 of 3 = Early vendor detection.
*/                

}
