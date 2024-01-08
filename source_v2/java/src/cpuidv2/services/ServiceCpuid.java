/* 
CPUID Utility. Refactoring 2024. (C)2024 Manusov I.V.
--------------------------------------------------------------------------
Class for get CPUID instruction based information. Also converts binary
data to array of viewable text strings. Argument is binary data.
Result is array of text strings consumed by GUI and text reports.
*/

package cpuidv2.services;

import cpuidv2.platforms.Detector;
import cpuidv2.CPUIDv2;
import cpuidv2.cpudatabase.VendorDetectPhysical;
import cpuidv2.cpudatabase.VendorDetectPhysical.*;
import cpuidv2.cpudatabase.VendorDetectVirtual;
import cpuidv2.cpudatabase.VendorDetectVirtual.*;
import cpuidv2.cpuidfunctions.DeviceCpuid;
import static cpuidv2.cpudatabase.VendorDetectPhysical.VENDOR_T.*;
import static cpuidv2.cpudatabase.VendorDetectVirtual.HYPERVISOR_T.*;

public class ServiceCpuid 
{
private final static int OPB_SIZE = 2048;  // 2048 qwords = 16384 bytes.
private final static int FN_CPUID = 0;
private final long[] opb;
private final DeviceCpuid device;

public ServiceCpuid()
    {
    opb = new long[ OPB_SIZE ]; 
    device = new DeviceCpuid();
    }

public boolean loadBinary()
    {
    Detector detector = CPUIDv2.getDetector();
    int jniStatus = detector.entryBinary( null, opb, FN_CPUID, OPB_SIZE );
    device.setBinary( opb );
    
    boolean initStatus = false;
    if( jniStatus > 0 )
        {
        initStatus = device.initBinary();
        }
    
    boolean parseStatus = false;
    if ( initStatus )    
        {
        earlyVendorDetect( true );
        parseStatus = device.parseBinary(); 
        }
    
    return parseStatus;
    }

public Device getCpuidDevice()
    {
    return device;
    }

public long[] getOpb()
    {
    return opb;
    }

public boolean setOpb( long[] data )
    {  // This call for load dump, no physical platform, physical flag = false.
    return setBinaryHelper( data, false );
    }

// Helper method for redetect, include early detect vendor by database.
private boolean setBinaryHelper( long [] x, boolean physical )
    {
    boolean b1, b2 = false;
    device.setBinary( x );
    b1 = device.initBinary();
    if ( b1 )
        {
        earlyVendorDetect( physical );
        b2 = device.parseBinary(); 
        }
    return b1 & b2;
    }

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
private void earlyVendorDetect( boolean physical )
    {
    String       scpu = earlyExtractVendorString( STANDARD_KEY, opb );
    String       svmm = earlyExtractVendorString( VIRTUAL_KEY, opb );
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
    
private final static int STANDARD_KEY = 0;
private final static int VIRTUAL_KEY  = 0x40000000;
private String earlyExtractVendorString( int key, long[] data )
    {
    boolean b = false;
    StringBuilder sb = new StringBuilder( "" );
    int n = data.length / 4;
    int m = ( int )( data[0] & 0x3FF );
    for( int i=1; ( i<m )&&( i<n ); i++ )
        {
        int function = (int)( data[ i*4 ] >> 32 );
        if ( function == key )
            {
            int ebx = (int)( data[ i*4+2 ] >> 32 );
            int ecx = (int)( data[ i*4+3 ] & (long)0xFFFFFFFF );
            int edx = (int)( data[ i*4+3 ] >> 32 );
            int[] signature;
            // for virtual function 40000000h order is EBX-ECX-EDX
            if ( key == VIRTUAL_KEY )
                signature = new int[]{ ebx, ecx, edx };
            // for functions 00000000h, 80000000h order is EBX-EDX-ECX
            else
                signature = new int[]{ ebx, edx, ecx };
            // cycle for convert 3 integer numbers to 12-char string
            for( int j=0; j<3; j++ )
                {
                int d = signature[j];
                for( int k=0; k<4; k++ )  // cycle convert int to 4 chars
                    {
                    char c = (char)( d & 0xFF );
                    if ( c != 0 )
                        {
                        if ( ( c < ' ' )||( c > '}' ) ) c = '.';
                        sb.append( c );
                        b = true;
                        }
                    d = d >>> 8;
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
