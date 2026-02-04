/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Special thanks to Todd Allen CPUID project
https://etallen.com/cpuid.html
http://www.etallen.com/

This file contains Processors and Hypervisors data exported from
Todd Allen CPUID project. Some variables and functions names not compliant
with java naming conventions, this fields using original C/C++ naming.

Vendor detector for virtual machine monitors (hypervisors).
Plus, data base management methods.

Note. Early hypervisor vendor detection required for:
1) Re-load dump with vendor-specific functions try, note unconditional
   use of vendor-specific functions can cause hardware failures.
2) Select or control vendor-specific leafs of CPUID instruction, note
   vendor type argument required for some parsings and must be valid
   before paesings.
pattern = hypervisor vendor signature.

*/

package cpuidv3.servicecpudata;

import cpuidv3.servicecpudata.ServiceCpudata.HYPERVISOR_T;
import static cpuidv3.servicecpudata.ServiceCpudata.V_SIGN;
import static cpuidv3.servicecpudata.VendorDetectPhysical.VIRTUAL_KEY;

class VendorDetectVirtual extends VendorDetectPhysical
{
    HYPERVISOR_T hVendor = null;
    HYPERVISOR_T getHvendor() { return hVendor; }
    
/*
    @Override void setCpuidDump( EntryCpuidSubfunction[][] cpuidDump )
    {
        super.setCpuidDump( cpuidDump );
        hVendor = null;
    }
*/
    @Override void setCpuidDump( EntryCpuidSubfunction[] oneCpuid )
    {
        super.setCpuidDump( oneCpuid );
        hVendor = null;
    }

/*    
    HYPERVISOR_T earlyVMM( int index )
    {
        HYPERVISOR_T result = null;
        if (( cpuidDump != null )&&( cpuidDump.length > index ))
        {
            HYPERVISOR_T[] hv = HYPERVISOR_T.values();
            String signature = 
                helperSignature( VIRTUAL_KEY, cpuidDump[index] );
*/
    HYPERVISOR_T earlyVMM()
    {
        HYPERVISOR_T result = null;
        if ( oneCpuid != null )
        {
            HYPERVISOR_T[] hv = HYPERVISOR_T.values();
            String signature = 
                helperSignature( VIRTUAL_KEY, oneCpuid );

            for ( HYPERVISOR_T value : hv ) 
            {
                String pattern = V_SIGN[ value.ordinal()][0];
                if ( pattern == null )
                {
                    if ( signature == null )
                    {
                        result = null;
                        break;
                    }
                }
                else if ( pattern.equals( signature ) ) 
                {
                    result = value;
                    break;
                }
            }
        }
        return result;
    }
    
    // Note. Method detectCPU at class VendorDetectPhysical.java makes
    // internal initializations, but here return signature string only.
    // This plave reserved for internal initializations.
/*
    String detectVMM( int index )
    {
        String result = null;
        if(( cpuidDump != null )&&( cpuidDump.length > index ))
        {
            EntryCpuidSubfunction[] entries = cpuidDump[index];
            
*/            
    String detectVMM()
    {
        String result = null;
        if( oneCpuid != null )
        {
            //EntryCpuidSubfunction[] entries = oneCpuid;  // Redundant ?

            String signature = helperSignature( VIRTUAL_KEY, oneCpuid );
            if( signature != null )
            {
                for ( int i=0; i<V_SIGN.length; i++ ) 
                {
                    String[] vsign = V_SIGN[i];
                    if ( vsign[0] == null ) 
                    {
                        result = vsign[1];
                        hVendor = HYPERVISOR_T.HYPERVISOR_UNKNOWN;
                    } 
                    else if ( vsign[0].equals( signature ) ) 
                    {
                        result = vsign[1];
                        hVendor = HYPERVISOR_T.values()[i];
                        break;
                    }
                }
            }
        }
        return result;
    }
}
