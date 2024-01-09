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
for VIA processors. 
stdTfms = Standard Type, Family, Model, Stepping, 
          CPUID standard function 00000001h, register EAX
extTfms = Extended Type, Family, Model, Stepping, 
          CPUID extended function 80000001h, register EAX
bi      = Brand Index, CPUID function 00000001h, register EBX,
          only bits[7-0] must be selected by AND mask inside 
          CriteriaDescriptor.detector() method called by detectorHelper.
*/

package cpuidv2.cpudatabase;

import static cpuidv2.cpudatabase.VendorDetectPhysical.VENDOR_T.VENDOR_VIA;

class ViaSynth extends Synth
{
/*
** Query macros are used in the synth tables to disambiguate multiple chips
** with the same family, model, and/or stepping.
*/
private boolean is_via = false;
    
private boolean vM = false;
private boolean vD = false;
private boolean v7 = false;
private boolean vE = false;
private boolean vZ = false;

ViaSynth( DatabaseStash stash )
    {
    super( stash );
    }
    
@Override String[] detect( int stdTfms, int extTfms, int bi )
    {
    is_via = stash.vendor == VENDOR_VIA;
    
    /*
    ** VIA major query
    */
    v7 = is_via && stash.br.c7;
    vM = is_via && stash.br.c7m;
    vD = is_via && stash.br.c7d;
    vE = is_via && stash.br.eden;
    vZ = is_via && stash.br.zhaoxin;
        
    final CriteriaDescriptor[] VIA_DATA = {
    new FM  (0, 5,  0, 4,         "VIA WinChip (C6)" ),
    new FM  (0, 5,  0, 8,         "VIA WinChip 2 (C6-2)" ),
    new FM  (0, 6,  0, 6,         "VIA C3 (Samuel C5A)" ),
    new FMS (0, 6,  0, 7,  0,     "VIA C3 (Samuel 2 C5B) / Eden ESP 4000/5000/6000, .15um" ),
    new FMS (0, 6,  0, 7,  1,     "VIA C3 (Samuel 2 C5B) / Eden ESP 4000/5000/6000, .15um" ),
    new FMS (0, 6,  0, 7,  2,     "VIA C3 (Samuel 2 C5B) / Eden ESP 4000/5000/6000, .15um" ),
    new FMS (0, 6,  0, 7,  3,     "VIA C3 (Samuel 2 C5B) / Eden ESP 4000/5000/6000, .15um" ),
    new FMS (0, 6,  0, 7,  4,     "VIA C3 (Samuel 2 C5B) / Eden ESP 4000/5000/6000, .15um" ),
    new FMS (0, 6,  0, 7,  5,     "VIA C3 (Samuel 2 C5B) / Eden ESP 4000/5000/6000, .15um" ),
    new FMS (0, 6,  0, 7,  6,     "VIA C3 (Samuel 2 C5B) / Eden ESP 4000/5000/6000, .15um" ),
    new FMS (0, 6,  0, 7,  7,     "VIA C3 (Samuel 2 C5B) / Eden ESP 4000/5000/6000, .15um" ),
    new FM  (0, 6,  0, 7,         "VIA C3 (Ezra C5C), .13um" ),
    new FM  (0, 6,  0, 8,         "VIA C3 (Ezra-T C5N)" ),
    new FMS (0, 6,  0, 9,  0,     "VIA C3 / Eden ESP 7000/8000/10000 (Nehemiah C5XL)" ),
    new FMS (0, 6,  0, 9,  1,     "VIA C3 / Eden ESP 7000/8000/10000 (Nehemiah C5XL)" ),
    new FMS (0, 6,  0, 9,  2,     "VIA C3 / Eden ESP 7000/8000/10000 (Nehemiah C5XL)" ),
    new FMS (0, 6,  0, 9,  3,     "VIA C3 / Eden ESP 7000/8000/10000 (Nehemiah C5XL)" ),
    new FMS (0, 6,  0, 9,  4,     "VIA C3 / Eden ESP 7000/8000/10000 (Nehemiah C5XL)" ),
    new FMS (0, 6,  0, 9,  5,     "VIA C3 / Eden ESP 7000/8000/10000 (Nehemiah C5XL)" ),
    new FMS (0, 6,  0, 9,  6,     "VIA C3 / Eden ESP 7000/8000/10000 (Nehemiah C5XL)" ),
    new FMS (0, 6,  0, 9,  7,     "VIA C3 / Eden ESP 7000/8000/10000 (Nehemiah C5XL)" ),
    new FM  (0, 6,  0, 9,         "VIA C3 / C3-M / Eden-N (Antaur C5P)" ),
    // VIA unpublished BIOS Guide for C7-D.
    new FM  (0, 6,  0,10,         "VIA C7 / C7-M / C7-D / Eden (Esther C5J Model A)" ),
    // VIA unpublished BIOS Guide for C7-D.
    // Brand string can be used to differentiate model D CPUs.
    new FMQ (0, 6,  0,13,     vM, "VIA C7-M (Esther C5J Model D)" ),
    new FMQ (0, 6,  0,13,     vD, "VIA C7-D (Esther C5J Model D)" ),
    new FMQ (0, 6,  0,13,     v7, "VIA C7 (Esther C5J Model D)" ),
    new FMQ (0, 6,  0,13,     vE, "VIA Eden (Esther C5J Model D)" ),
    new FM  (0, 6,  0,13,         "VIA (unknown type) (Esther C5J Model D)" ),
    // VIA unpublished BIOS Guide for Nano, Eden (for steppings 3-14, other than
    // Zhaoxin).
    //
    // Steppings 0-2 for Isaiah come from this post by "redray", circa Apr 2013:
    //    https://forum.osdev.org/viewtopic.php?f=1&t=26351
    // It presents an excerpt from "VIA Nano Processor X2X4 BIOS Guide 2.47".
    // 
    // Die size depends on core, but it's unclear which cores are which:
    //    Isaiah    = 65nm, 40nm
    //    Isaiah II = 28nm
    //    ZhangJiang (Zhaoxin) = 28nm ?
    new FMS (0, 6,  0,15,  0,     "VIA Nano 1000/2000 (Isaiah CNA A0)" ), // redray; instlatx64 example
    new FMS (0, 6,  0,15,  1,     "VIA Nano 1000/2000 (Isaiah CNA A1)" ), // redray; model numbers assumed because of bracketing between 0 & 3
    new FMS (0, 6,  0,15,  2,     "VIA Nano 1000/2000 (Isaiah CNA A2)" ), // redray; model numbers assumed because of bracketing between 0 & 3
    new FMS (0, 6,  0,15,  3,     "VIA Nano 1000/2000 (Isaiah CNA A3)" ),
    new FMS (0, 6,  0,15,  8,     "VIA Nano 3000 (Isaiah CNB A1)" ),
    new FMS (0, 6,  0,15, 10,     "VIA Nano 3000 (Isaiah CNC A2)" ),
    new FMS (0, 6,  0,15, 12,     "VIA Nano X2 4000 / QuadCore 4000 (Isaiah CNQ A1)" ),
    new FMS (0, 6,  0,15, 13,     "VIA Nano X2 4000 / QuadCore 4000 (Isaiah CNQ A2)" ),
    new FMSQ(0, 6,  0,15, 14, vZ, "Zhaoxin KaiXian/Kaisheng ZX-C/ZX-C+" ),
    new FMS (0, 6,  0,15, 14,     "VIA Eden X4 4000 (Isaiah CNR)" ),
    new FM  (0, 6,  0,15,         "VIA Nano / Eden (unknown type) (Isaiah)" ),
    new F   (0, 6,                "VIA C3 / C3-M / C7 / C7-M / Eden / Eden ESP 7000/8000/10000 / Nano (unknown model)" ),
    new FM  (0, 7,  0,11,         "Zhaoxin KaiXian KX-5000 / Kaisheng KH-20000 (WuDaoKou)" ),   // geekbench.com example
    new FMQ (0, 7,  3,11,     vZ, "Zhaoxin KaiXian KX-6000 / Kaisheng KH-30000 (LuJiaZui)") }; // instlatx64 example with CentaurHauls vendor!
    
    final CriteriaDescriptor[] VIA_X_DATA = {
    new FM (0,6,  0,6,     "VIA C3 (WinChip C5A)" ),
    new FM (0,6,  0,6,     "VIA C3 (WinChip C5A)" ),
    new FMS(0,6,  0,7,  0, "VIA C3 (WinChip C5B)" ),
    new FMS(0,6,  0,7,  1, "VIA C3 (WinChip C5B)" ),
    new FMS(0,6,  0,7,  2, "VIA C3 (WinChip C5B)" ),
    new FMS(0,6,  0,7,  3, "VIA C3 (WinChip C5B)" ),
    new FMS(0,6,  0,7,  4, "VIA C3 (WinChip C5B)" ),
    new FMS(0,6,  0,7,  5, "VIA C3 (WinChip C5B)" ),
    new FMS(0,6,  0,7,  6, "VIA C3 (WinChip C5B)" ),
    new FMS(0,6,  0,7,  7, "VIA C3 (WinChip C5B)" ),
    new FM (0,6,  0,7,     "VIA C3 (WinChip C5C)" ),
    new FM (0,6,  0,8,     "VIA C3 (WinChip C5N)" ),
    new FM (0,6,  0,9,     "VIA C3 (WinChip C5XL)" ),
    new F  (0,6,           "VIA C3 (unknown model)") };

    String s1 = detectorHelper( stdTfms, bi, VIA_DATA );
    String s2 = detectorHelper( extTfms, bi, VIA_X_DATA );
    return new String[] { s1, s2 };
    }
}
