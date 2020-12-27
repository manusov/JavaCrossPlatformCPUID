/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
This file contains Processors and Hypervisors
data exported from Todd Allen CPUID project.
Some variables and functions names not compliant with java
naming conventions, this fields using original C/C++ naming.
-----------------------------------------------
Processor name detection by signature and additional flags,
for Cyrix processors. 
stdTfms = Standard Type, Family, Model, Stepping, 
          CPUID standard function 00000001h, register EAX
extTfms = Extended Type, Family, Model, Stepping, 
          CPUID extended function 80000001h, register EAX
bi      = Brand Index, CPUID function 00000001h, register EBX,
          only bits[7-0] must be selected by AND mask inside 
          CriteriaDescriptor.detector() method called by detectorHelper.
*/

package cpuidrefactoring.database;

import static cpuidrefactoring.database.VendorDetectPhysical.VENDOR_T.VENDOR_CYRIX;

class CyrixSynth extends Synth
{
/*
** Query macros are used in the synth tables to disambiguate multiple chips
** with the same family, model, and/or stepping.
*/
private boolean is_cyrix = false;
private boolean cm;

CyrixSynth( DatabaseStash stash )
    {
    super( stash );
    }
    
@Override String[] detect( int stdTfms, int extTfms, int bi )
    {
    is_cyrix = stash.vendor == VENDOR_CYRIX;
    
    /*
    ** Cyrix major query
    */
    cm = is_cyrix && stash.br.mediagx;
    
    final CriteriaDescriptor[] CYRIX_DATA = {
    new FM ( 0,4,  0,4,     "Cyrix Media GX / GXm" ),
    new FM ( 0,4,  0,9,     "Cyrix 5x86" ),
    new F  ( 0,4,           "Cyrix 5x86 (unknown model)" ),
    new FM ( 0,5,  0,2,     "Cyrix M1 6x86" ),
    new FM ( 0,5,  0,3,     "Cyrix M1 6x86" ),         // added
    new FMQ( 0,5,  0,4, cm, "Cyrix MediaGX (C6)" ),
    new FM ( 0,5,  0,4,     "Cyrix M1 WinChip (C6)" ),
    new FM ( 0,5,  0,8,     "Cyrix M1 WinChip 2 (C6-2)" ),
    new FM ( 0,5,  0,9,     "Cyrix M1 WinChip 3 (C6-2)" ),
    new F  ( 0,5,           "Cyrix M1 (unknown model)" ),
    new FM ( 0,6,  0,0,     "Cyrix M2 6x86MX" ),
    new FM ( 0,6,  0,5,     "Cyrix M2" ),
    new F  ( 0,6,           "Cyrix M2 (unknown model)") };
    String s1 = detectorHelper( stdTfms, bi, CYRIX_DATA );
    return new String[] { s1, null };
    }
}
