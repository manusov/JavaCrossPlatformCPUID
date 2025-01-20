/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

This file contains Processors and Hypervisors data exported from
Todd Allen CPUID project. Some variables and functions names not compliant
with java naming conventions, this fields using original C/C++ naming.

Processors and Hypervisors data base public interface class:
access methods set.

*/

package cpuidv3.servicecpudata;

import static cpuidv3.servicecpudata.VendorDetectPhysical.P_SIGN;
import static cpuidv3.servicecpudata.VendorDetectPhysical.VENDOR_T.*;

public class DatabaseManager 
{
private final DatabaseStash stash;
    
private final String   physicalVendor;
private final String   virtualVendor;
private final Phandler handler;

private final Brand brand;
private final Microarchitecture microarchitecture;
private final Synth synth;
private final Model model;

// p = physical CPU vendor string, for example "GenuineIntel", "AuthenticAMD"
// v = virtual CPU vendor string, for example "VBoxVBoxVBox"
public DatabaseManager( String p, String v )
    {
    stash                    = new DatabaseStash();
    VendorDetectPhysical vdp = new VendorDetectPhysical( stash );
    VendorDetectVirtual  vdv = new VendorDetectVirtual();
    physicalVendor           = vdp.detectPhysical( p );
    virtualVendor            = vdv.detectVirtual( v );
    stash.vendor             = vdp.getPvendor();
    handler                  = vdp.getPhandler();
    
    brand             = ( handler != null )&&( handler.gbr != null ) ?
                        handler.gbr.gBR() : null;
    microarchitecture = ( handler != null )&&( handler.gma != null ) ?
                        handler.gma.gMA() : null;
    synth             = ( handler != null )&&( handler.gsy != null ) ?
                        handler.gsy.gSY() : null;
    model             = ( handler != null )&&( handler.gmd != null ) ?
                        handler.gmd.gMD() : null;
    }

// between this class constructor call and this method call,
// database caller must initialize public fields of DatabaseStash class
// as arguments for generation private fields = F ( public fields ).
public void buildStash()
    {
    VendorInitPhysical vip = new VendorInitPhysical();
    vip.buildStash( stash );
    }

public DatabaseStash getStash()
    {
    return stash;
    }

public String getPhysicalVendor()
    {
    String s = physicalVendor;
    if ( ( stash.vendor == VENDOR_VIA )&&( stash.br.zhaoxin ) )
        {  // support special case VIA vs Zhaoxin
        s = P_SIGN[ VENDOR_ZHAOXIN.ordinal() ][1];
        }
    else if ( ( stash.vendor == VENDOR_INTEL )&&( stash.br.montage ) )
        {
        s = P_SIGN[ VENDOR_MONTAGE.ordinal() ][1];
        }
    return s; 
    }

public String getVirtualVendor()
    { 
    return virtualVendor; 
    }

public String getBrand()
    { 
    int tfms = stash.val_1_eax;
    int bi   = stash.val_1_ebx;
    return ( brand != null ) ?
            brand.detect( tfms, bi ) : null; 
    }

public String[] getSynth()
    {
    int stdTfms = stash.val_1_eax;
    int extTfms = stash.val_80000001_eax;
    int bi      = stash.val_1_ebx;
    return ( synth != null ) ?  
        synth.detect( stdTfms, extTfms, bi ) : null; 
    }

public MData getMicroarchitecture()
    { 
    int tfms = stash.val_1_eax;
    int bi   = stash.val_1_ebx;
    return ( microarchitecture != null ) ?  
        microarchitecture.detect( tfms, bi ) : null;
    }

public String[] getModel()
    { 
    return ( model != null ) ? 
        model.detect( stash ) : null;
    }

}
