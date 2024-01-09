/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2024.
-------------------------------------------------------------------------------
This file contains Processors and Hypervisors data exported from
Todd Allen CPUID project. Some variables and functions names not compliant
with java naming conventions, this fields using original C/C++ naming.
-------------------------------------------------------------------------------
Processor name detection by signature and additional flags,
for Transmeta processors. 
stdTfms = Standard Type, Family, Model, Stepping, 
          CPUID standard function 00000001h, register EAX
extTfms = Extended Type, Family, Model, Stepping, 
          CPUID extended function 80000001h, register EAX
bi      = Brand Index, CPUID function 00000001h, register EBX,
          only bits[7-0] must be selected by AND mask inside 
          CriteriaDescriptor.detector() method called by detectorHelper.
*/

package cpuidv2.cpudatabase;

import static 
        cpuidv2.cpudatabase.VendorDetectPhysical.VENDOR_T.VENDOR_TRANSMETA;

class TransmetaSynth extends Synth
{
/*
** Query macros are used in the synth tables to disambiguate multiple chips
** with the same family, model, and/or stepping.
*/
private boolean is_transmeta = false;

private boolean t2 = false;
private boolean t4 = false;
private boolean t5 = false;
private boolean t6 = false;
private boolean t8 = false;

TransmetaSynth ( DatabaseStash stash )
    {
    super( stash );
    }

private boolean tm_rev( int rev )
    {
    return is_transmeta && ( stash.transmeta_proc_rev & 0xFFFF0000 ) == rev;
    }

@Override String[] detect( int stdTfms, int extTfms, int bi )
    {
    is_transmeta = stash.vendor == VENDOR_TRANSMETA;
    
    /*
    ** Transmeta major queries
    **
    ** t2 = TMx200
    ** t4 = TMx400
    ** t5 = TMx500
    ** t6 = TMx600
    ** t8 = TMx800
    */
    /* TODO: Add cases for Transmeta Crusoe TM5700/TM5900 */
    /* TODO: Add cases for Transmeta Efficeon */
    t2 = tm_rev( 0x01010000 );
    t4 = tm_rev( 0x01020000 ) || ( tm_rev( 0x01030000 ) && stash.L2_4w_256K );
//  t5 = ( tm_rev( 0x01040000 ) || tm_rev( 0x01040000 ) ) && stash.L2_4w_256K;
    t5 = ( tm_rev( 0x01040000 ) || tm_rev( 0x01050000 ) ) && stash.L2_4w_256K;   // changed
    t6 = tm_rev( 0x01030000 ) && stash.L2_4w_512K;
//  t8 = ( tm_rev(0x01040000 ) || tm_rev( 0x01040000 ) ) && stash.L2_4w_512K;
    t8 = ( tm_rev(0x01040000 ) || tm_rev( 0x01050000 ) ) && stash.L2_4w_512K;    // changed
        
    final CriteriaDescriptor[] TRANSMETA_DATA = {
    new FMSQ( 0, 5,  0,4,  2, t2, "Transmeta Crusoe TM3200" ),
    new FMS ( 0, 5,  0,4,  2,     "Transmeta Crusoe TM3x00 (unknown model)" ),
    new FMSQ( 0, 5,  0,4,  3, t4, "Transmeta Crusoe TM5400" ),
    new FMSQ( 0, 5,  0,4,  3, t5, "Transmeta Crusoe TM5500 / Crusoe SE TM55E" ),
    new FMSQ( 0, 5,  0,4,  3, t6, "Transmeta Crusoe TM5600" ),
    new FMSQ( 0, 5,  0,4,  3, t8, "Transmeta Crusoe TM5800 / Crusoe SE TM58E" ),
    new FMS ( 0, 5,  0,4,  3,     "Transmeta Crusoe TM5x00 (unknown model)" ),
    new FM  ( 0, 5,  0,4,         "Transmeta Crusoe" ),
    new F   ( 0, 5,               "Transmeta Crusoe (unknown model)" ),
    new FM  ( 0,15,  0,2,         "Transmeta Efficeon TM8x00" ),
    new FM  ( 0,15,  0,3,         "Transmeta Efficeon TM8x00" ),
    new F   ( 0,15,               "Transmeta Efficeon (unknown model)" ) };
    String s1 = detectorHelper( stdTfms, bi, TRANSMETA_DATA );
    return new String[] { s1, null };
    }
}
