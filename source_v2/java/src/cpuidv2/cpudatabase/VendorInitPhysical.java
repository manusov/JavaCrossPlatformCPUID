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

package cpuidv2.cpudatabase;

class VendorInitPhysical 
{

void buildStash( DatabaseStash stash )
    {
    // universal procedures
    VendorParse vp = new VendorParse();  // keywords data
    vp.decodeBrandString( stash );       // detect keywords at model name string
    
    VendorMp vm = new VendorMp();        // MP topology
    vm.decodeMp( stash );                // extract topology parameters
    
    // vendor-specific procedures
    switch( stash.vendor )
        {
            case VENDOR_UNKNOWN:
                break;
                
            case VENDOR_INTEL:
                IntelStashBrand isb = new IntelStashBrand();  // brand index
                IntelStashCache isc = new IntelStashCache();  // cache descs.
                isb.detect( stash );
                isc.detect( stash );
                break;
            
            case VENDOR_AMD:
            case VENDOR_TRANSMETA:
                AmdStashCache asc = new AmdStashCache();
                asc.detect( stash );
                break;
            
            case VENDOR_CYRIX:
            case VENDOR_VIA:
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
