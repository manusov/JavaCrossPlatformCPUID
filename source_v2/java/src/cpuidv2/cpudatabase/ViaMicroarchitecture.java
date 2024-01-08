/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
This file contains Processors and Hypervisors
data exported from Todd Allen CPUID project.
Some variables and functions names not compliant with java
naming conventions, this fields using original C/C++ naming.
-----------------------------------------------
Processor microarchitecture and physical parameters detection 
by signature and additional flags, for VIA processors. 
tfms = Standard Type, Family, Model, Stepping, 
       CPUID standard function 00000001h, register EAX
bi   = Brand Index, CPUID function 00000001h, register EBX,
       only bits[7-0] must be selected by AND mask inside 
       CriteriaDescriptor.detector() method called by detectorHelper.
*/

package cpuidv2.cpudatabase;

import static cpuidv2.cpudatabase.VendorDetectPhysical.VENDOR_T.VENDOR_VIA;

class ViaMicroarchitecture extends Microarchitecture
{
/*
** Query macros are used in the synth tables to disambiguate multiple chips
** with the same family, model, and/or stepping.
*/
private boolean is_via = false;
private boolean vZ = false;

ViaMicroarchitecture( DatabaseStash stash ) 
    {
    super( stash );
    }

@Override MData detect( int tfms, int bi )
    {
    is_via = stash.vendor == VENDOR_VIA;
    
    /*
    ** VIA major query
    */
    vZ = is_via && stash.br.zhaoxin;
        
    final CriteriaDescriptor[] VIA_MICROARCHITECTURE = {
    new F    ( 0, 5,                ()-> { u = "WinChip"; c = true; } ),
    new FM   ( 0, 6, 0, 6,          ()-> { u = "C3"; c = true; p = ".18um"; } ),
    new FM   ( 0, 6, 0, 7,          ()-> { u = "C3"; c = true; } ), // *p depends on core
    new FM   ( 0, 6, 0, 8,          ()-> { u = "C3"; c = true; p = ".13um"; } ),
    new FM   ( 0, 6, 0, 9,          ()-> { u = "C3"; c = true; p = ".13um"; } ),
    new FM   ( 0, 6, 0, 10,         ()-> { u = "C7"; p = "90nm"; } ),
    new FM   ( 0, 6, 0, 13,         ()-> { u = "C7"; p = "90nm"; } ),
    new FMSQ ( 0, 6, 0, 15, 14, vZ, ()-> { u = "ZhangJiang"; p = "28nm"; } ),
    new FM   ( 0, 6, 0, 15,         ()-> { u = "C7"; } ),                      // *p depends on core
    new FM   ( 0, 7, 0, 11,         ()-> { u = "ZhangJiang"; p = "28nm"; } ) };
    return detectorHelper( tfms, bi, VIA_MICROARCHITECTURE );
    }
}
