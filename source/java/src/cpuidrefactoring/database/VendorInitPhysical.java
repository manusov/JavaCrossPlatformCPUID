/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
This file contains Processors and Hypervisors
data exported from Todd Allen CPUID project.
Some variables and functions names not compliant with java
naming conventions, this fields using original C/C++ naming.
-----------------------------------------------
Perform vendor-specific initialization for physical procesors.
Plus, data base management methods.
*/

package cpuidrefactoring.database;

class VendorInitPhysical 
{

void buildStash( DatabaseStash stash )
    {
    VendorParse vp = new VendorParse();
    vp.decodeBrandString( stash );
        
    switch( stash.vendor )
        {
            case VENDOR_UNKNOWN:
                break;
                
            case VENDOR_INTEL:
                IntelStashBrand isb = new IntelStashBrand();
                IntelStashCache isc = new IntelStashCache();
                isb.detect( stash );
                isc.detect( stash );
                break;
            
            case VENDOR_AMD:
                break;
            
            case VENDOR_CYRIX:
            case VENDOR_VIA:
            case VENDOR_TRANSMETA:
            case VENDOR_UMC:
            case VENDOR_NEXGEN:
            case VENDOR_RISE:
            case VENDOR_SIS:
            case VENDOR_NSC:
            case VENDOR_VORTEX:
            case VENDOR_RDC:
            case VENDOR_HYGON:
            case VENDOR_ZHAOXIN:
                break;
        }
    }
}
