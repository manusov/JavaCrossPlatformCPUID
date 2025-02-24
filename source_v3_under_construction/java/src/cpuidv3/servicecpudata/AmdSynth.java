/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Special thanks to Todd Allen CPUID project
https://etallen.com/cpuid.html
http://www.etallen.com/

This file contains Processors and Hypervisors data exported from
Todd Allen CPUID project. Some variables and functions names not compliant
with java naming conventions, this fields using original C/C++ naming.

Processor name detection by signature and additional flags,
for AMD processors. 
stdTfms = Standard Type, Family, Model, Stepping, 
          CPUID standard function 00000001h, register EAX
extTfms = Extended Type, Family, Model, Stepping, 
          CPUID extended function 80000001h, register EAX
bi      = Brand Index, CPUID function 00000001h, register EBX,
          only bits[7-0] must be selected by AND mask inside 
          CriteriaDescriptor.detector() method called by detectorHelper.

*/

package cpuidv3.servicecpudata;

import static cpuidv3.servicecpudata.DefineArithmetic.*;
import static cpuidv3.servicecpudata.VendorDetectPhysical.VENDOR_T.VENDOR_AMD;

class AmdSynth extends Synth
{
/*
** Query macros are used in the synth tables to disambiguate multiple chips
** with the same family, model, and/or stepping.
*/
private boolean is_amd    = false;
private boolean is_mobile = false;

private boolean sA = false;
private boolean dA = false;
private boolean MA = false;
private boolean sD = false;
private boolean MD = false;
private boolean dD = false;
private boolean dX = false;
private boolean dS = false;
private boolean MX = false;
private boolean MG = false;
private boolean ML = false;
private boolean dt = false;
private boolean dF = false;
private boolean MS = false;
private boolean DO = false;
private boolean dm = false;
private boolean MT = false;
private boolean Mt = false;
private boolean MN = false;
private boolean Mn = false;
private boolean EO = false;
private boolean Tp = false;
private boolean Qp = false;
private boolean dR = false;
private boolean Dp = false;
private boolean dp = false;
private boolean DA = false;
private boolean TA = false;
private boolean QA = false;
private boolean Ms = false;
private boolean DS = false;
private boolean Mp = false;
private boolean SO = false;
private boolean Sp = false;
private boolean df = false;
private boolean sO = false;
private boolean s8 = false;
private boolean dr = false;
private boolean MU = false;
private boolean Sa = false;
private boolean Se = false;
private boolean Sc = false;
private boolean Sg = false;
private boolean Sz = false;
private boolean Sr = false;
private boolean dI = false;
private boolean Mr = false;
private boolean Ta = false;
private boolean Te = false;
private boolean EE = false;
private boolean sE = false;
private boolean ER = false;
private boolean AR = false;
private boolean dH = false;  // added
private boolean VR = false;  // added
private boolean RR = false;  // added

AmdSynth( DatabaseStash stash ) 
    {
    super( stash );
    }

/*
** AMD special cases
*/
private boolean is_amd_egypt_athens_8xx( DatabaseStash stash )
    {
   /*
   ** This makes its determination based on the Processor model
   ** logic from:
   **    Revision Guide for AMD Athlon 64 and AMD Opteron Processors 
   **    (25759 Rev 3.79), Constructing the Processor Name String.
   ** See also decode_amd_model().
   */

   if ( stash.vendor == VENDOR_AMD && stash.br.opteron ) 
        {
        int x = __FM( stash.val_1_eax );
        if ( ( x == _FM( 0, 15, 2, 1 ) ) ||  /* Italy/Egypt */
             ( x == _FM( 0, 15, 2, 5 ) ) )   /* Troy/Athens */
            {
            int bti;
            if ( __B( stash.val_1_ebx ) != 0 ) 
                {
                bti = BIT_EXTRACT_LE( __B( stash.val_1_ebx), 5, 8 ) << 2;
                } 
            else if ( BIT_EXTRACT_LE( stash.val_80000001_ebx, 0, 12 ) != 0 )
                {
                bti = BIT_EXTRACT_LE( stash.val_80000001_ebx, 6, 12 );
                } 
            else 
                {
                return false;
                }

            switch ( bti ) 
                {
                case 0x10:
                case 0x11:
                case 0x12:
                case 0x13:
                case 0x2a:
                case 0x30:
                case 0x31:
                case 0x39:
                case 0x3c:
                case 0x32:
                case 0x33:
                    /* It's a 2xx */
                    return true;
                case 0x14:
                case 0x15:
                case 0x16:
                case 0x17:
                case 0x2b:
                case 0x34:
                case 0x35:
                case 0x3a:
                case 0x3d:
                case 0x36:
                case 0x37:
                    /* It's an 8xx */
                    return true;
                }
            }
        }
    return false;
    }

@Override String[] detect( int stdTfms, int extTfms, int bi )
    {
    is_amd    = stash.vendor == VENDOR_AMD;
    is_mobile = stash.br.mobile;
    
    /*
    ** AMD major queries:
    **
    ** d? = think "desktop"
    ** s? = think "server" (MP)
    ** M? = think "mobile"
    ** S? = think "Series"
    ** T? = think "Tablet"
    ** A? = think "any"
    ** E? = think "Embedded"
    **
    ** ?A = think Athlon
    ** ?X = think Athlon XP
    ** ?L = think Athlon XP (LV)
    ** ?E = think EPYC
    ** ?f = think FX
    ** ?F = think Athlon FX
    ** ?D = think Duron
    ** ?S = think Sempron
    ** ?O = think Opteron
    ** ?T = think Turion
    ** ?U = think Turion Ultra
    ** ?p = think Phenom
    ** ?R = think Ryzen
    ** ?s = think ?-Series
    ** ?n = think Turion Neo
    ** ?N = think Neo
    */
    dA = is_amd && !is_mobile && stash.br.athlon;
    dX = is_amd && !is_mobile && stash.br.athlon_xp;
    dF = is_amd && !is_mobile && stash.br.athlon_fx;
    df = is_amd && !is_mobile && stash.br.fx;
    dD = is_amd && !is_mobile && stash.br.duron;
    dS = is_amd && !is_mobile && stash.br.sempron;
    dp = is_amd && !is_mobile && stash.br.phenom;
    dI = is_amd && !is_mobile && stash.br.firepro;
    dR = is_amd && !is_mobile && stash.br.ryzen;
    sO = is_amd && !is_mobile && stash.br.opteron;
    sA = is_amd && !is_mobile && stash.br.athlon_mp;
    sD = is_amd && !is_mobile && stash.br.duron_mp;
    sE = is_amd && !is_mobile && stash.br.epyc;
    MA = is_amd && is_mobile && stash.br.athlon;
    MX = is_amd && is_mobile && stash.br.athlon_xp;
    ML = is_amd && is_mobile && stash.br.athlon_lv;
    MD = is_amd && is_mobile && stash.br.duron;
    MS = is_amd && is_mobile && stash.br.sempron;
    Mp = is_amd && is_mobile && stash.br.phenom;
    Ms = is_amd && is_mobile && stash.br.series;
    Mr = is_amd && is_mobile && stash.br.r_series;
    MG = is_amd && stash.br.geode;
    MT = is_amd && stash.br.turion;
    MU = is_amd && stash.br.ultra;
    Mn = is_amd && stash.br.turion && stash.br.neo;
    MN = is_amd && stash.br.neo;
    Sa = is_amd && !is_mobile && stash.br.a_series;
    Sc = is_amd && !is_mobile && stash.br.c_series;
    Se = is_amd && !is_mobile && stash.br.e_series;
    Sg = is_amd && !is_mobile && stash.br.g_series;
    Sr = is_amd && !is_mobile && stash.br.r_series;
    Sz = is_amd && !is_mobile && stash.br.z_series;
    Ta = is_amd && stash.br.t_suffix && stash.br.a_series;
    Te = is_amd && stash.br.t_suffix && stash.br.e_series;
    AR = is_amd && stash.br.ryzen;
    ER = is_amd && stash.br.ryzen && stash.br.embedded;
    EE = is_amd && stash.br.epyc_3000;
    
    /* Embedded Opteron, distinguished from Opteron (Barcelona & Shanghai) */
    EO = sO && stash.br.embedded;
    /* Opterons, distinguished by number of processors */
    DO = sO && stash.br.cores == 2;
    SO = sO && stash.br.cores == 6;
    /* Athlons, distinguished by number of processors */
    DA = dA && stash.br.cores == 2;
    TA = dA && stash.br.cores == 3;
    QA = dA && stash.br.cores == 4;
    /* Phenoms distinguished by number of processors */
    Dp = dp && stash.br.cores == 2;
    Tp = dp && stash.br.cores == 3;
    Qp = dp && stash.br.cores == 4;
    Sp = dp && stash.br.cores == 6;
    /* Semprons, distinguished by number of processors */
    DS = dS  && stash.br.cores == 2;
    /* Egypt, distinguished from Italy; and
    Athens, distingushed from Troy */
    s8 = sO && is_amd_egypt_athens_8xx( stash );
    /* Thorton A2, distinguished from Barton A2 */
    dt = dX && stash.L2_256K;
    /* Manchester E6, distinguished from from Toledo E6 */
    dm = dA && stash.L2_512K;
    /* Propus, distinguished from Regor */
    dr = dA && stash.L2_512K;
    /* Trinidad, distinguished from Taylor */
    Mt = MT && stash.L2_512K;
    // added for Ryzen ThreadRipper
    dH = is_amd && !is_mobile && stash.br.threadripper;
    // added for Ryzen Embedded
    VR = is_amd && stash.br.ryzen && stash.br.embedded_V;
    RR = is_amd && stash.br.ryzen && stash.br.embedded_R;
        
    final CriteriaDescriptor[] AMD_DATA = {
    new FM  ( 0, 4,  0, 3,         "AMD 80486DX2" ),
    new FM  ( 0, 4,  0, 7,         "AMD 80486DX2WB" ),
    new FM  ( 0, 4,  0, 8,         "AMD 80486DX4" ),
    new FM  ( 0, 4,  0, 9,         "AMD 80486DX4WB" ),
    new FM  ( 0, 4,  0,10,         "AMD Elan SC400" ), // sandpile.org
    new FM  ( 0, 4,  0,14,         "AMD 5x86" ),
    new FM  ( 0, 4,  0,15,         "AMD 5xWB" ),
    new F   ( 0, 4,                "AMD 80486 / 5x (unknown model)" ),
    new FM  ( 0, 5,  0, 0,         "AMD SSA5 (PR75, PR90, PR100)" ),
    new FM  ( 0, 5,  0, 1,         "AMD 5k86 (PR120, PR133)" ),
    new FM  ( 0, 5,  0, 2,         "AMD 5k86 (PR166)" ),
    new FM  ( 0, 5,  0, 3,         "AMD 5k86 (PR200)" ),
    new FM  ( 0, 5,  0, 5,         "AMD Geode GX" ),
    new FM  ( 0, 5,  0, 6,         "AMD K6" ),
    new FM  ( 0, 5,  0, 7,         "AMD K6 (Little Foot)" ),
    new FMS ( 0, 5,  0, 8,  0,     "AMD K6-2 (Chomper A)" ),
    new FMS ( 0, 5,  0, 8, 12,     "AMD K6-2 (Chomper A)" ),
    new FM  ( 0, 5,  0, 8,         "AMD K6-2 (Chomper)" ),
    new FMS ( 0, 5,  0, 9,  1,     "AMD K6-III (Sharptooth B)" ),
    new FM  ( 0, 5,  0, 9,         "AMD K6-III (Sharptooth)" ),
    new FM  ( 0, 5,  0,10,         "AMD Geode LX" ),
    new FM  ( 0, 5,  0,13,         "AMD K6-2+, K6-III+" ),
    new F   ( 0, 5,                "AMD 5k86 / K6 / Geode (unknown model)" ),
    new FM  ( 0, 6,  0, 1,         "AMD Athlon (Argon)" ),
    new FMS ( 0, 6,  0, 2,  2,     "AMD Athlon (K75 / Pluto / Orion A2)"),
    new FM  ( 0, 6,  0, 2,         "AMD Athlon (K75 / Pluto / Orion)" ),
    new FMS ( 0, 6,  0, 3,  0,     "AMD Duron / mobile Duron (Spitfire A0)" ),
    new FMS ( 0, 6,  0, 3,  1,     "AMD Duron / mobile Duron (Spitfire A2)" ),
    new FM  ( 0, 6,  0, 3,         "AMD Duron / mobile Duron (Spitfire)" ),
    new FMS ( 0, 6,  0, 4,  2,     "AMD Athlon (Thunderbird A4-A7)" ),
    new FMS ( 0, 6,  0, 4,  4,     "AMD Athlon (Thunderbird A9)" ),
    new FM  ( 0, 6,  0, 4,         "AMD Athlon (Thunderbird)" ),
    new FMSQ( 0, 6,  0, 6,  0, sA, "AMD Athlon MP (Palomino A0)" ),
    new FMSQ( 0, 6,  0, 6,  0, dA, "AMD Athlon (Palomino A0)" ),
    new FMSQ( 0, 6,  0, 6,  0, MA, "AMD mobile Athlon 4 (Palomino A0)" ),
    new FMSQ( 0, 6,  0, 6,  0, sD, "AMD Duron MP (Palomino A0)" ),
    new FMSQ( 0, 6,  0, 6,  0, MD, "AMD mobile Duron (Palomino A0)" ),
    new FMS ( 0, 6,  0, 6,  0,     "AMD Athlon (unknown type)  (Palomino A0)" ),
    new FMSQ( 0, 6,  0, 6,  1, sA, "AMD Athlon MP (Palomino A2)" ),
    new FMSQ( 0, 6,  0, 6,  1, dA, "AMD Athlon (Palomino A2)" ),
    new FMSQ( 0, 6,  0, 6,  1, MA, "AMD mobile Athlon 4 (Palomino A2)" ),
    new FMSQ( 0, 6,  0, 6,  1, sD, "AMD Duron MP (Palomino A2)" ),
    new FMSQ( 0, 6,  0, 6,  1, MD, "AMD mobile Duron (Palomino A2)" ),
    new FMSQ( 0, 6,  0, 6,  1, dD, "AMD Duron (Palomino A2)" ),
    new FMS ( 0, 6,  0, 6,  1,     "AMD Athlon (unknown type) (Palomino A2)" ),
    new FMSQ( 0, 6,  0, 6,  2, sA, "AMD Athlon MP (Palomino A5)" ),
    new FMSQ( 0, 6,  0, 6,  2, dX, "AMD Athlon XP (Palomino A5)" ),
    new FMSQ( 0, 6,  0, 6,  2, MA, "AMD mobile Athlon 4 (Palomino A5)" ),
    new FMSQ( 0, 6,  0, 6,  2, sD, "AMD Duron MP (Palomino A5)" ),
    new FMSQ( 0, 6,  0, 6,  2, MD, "AMD mobile Duron (Palomino A5)" ),
    new FMSQ( 0, 6,  0, 6,  2, dD, "AMD Duron (Palomino A5)" ),
    new FMS ( 0, 6,  0, 6,  2,     "AMD Athlon (unknown type) (Palomino A5)" ),
    new FMQ ( 0, 6,  0, 6,     MD, "AMD mobile Duron (Palomino)" ),
    new FMQ ( 0, 6,  0, 6,     dD, "AMD Duron (Palomino)" ),
    new FMQ ( 0, 6,  0, 6,     MA, "AMD mobile Athlon (Palomino)" ),
    new FMQ ( 0, 6,  0, 6,     dX, "AMD Athlon XP (Palomino)" ),
    new FMQ ( 0, 6,  0, 6,     dA, "AMD Athlon (Palomino)" ),
    new FM  ( 0, 6,  0, 6,         "AMD Athlon (unknown type) (Palomino)" ),
    new FMSQ( 0, 6,  0, 7,  0, sD, "AMD Duron MP (Morgan A0)" ),
    new FMSQ( 0, 6,  0, 7,  0, MD, "AMD mobile Duron (Morgan A0)" ),
    new FMSQ( 0, 6,  0, 7,  0, dD, "AMD Duron (Morgan A0)" ),
    new FMS ( 0, 6,  0, 7,  0,     "AMD Duron (unknown type)  (Morgan A0)" ),
    new FMSQ( 0, 6,  0, 7,  1, sD, "AMD Duron MP (Morgan A1)" ),
    new FMSQ( 0, 6,  0, 7,  1, MD, "AMD mobile Duron (Morgan A1)" ),
    new FMSQ( 0, 6,  0, 7,  1, dD, "AMD Duron (Morgan A1)" ),
    new FMS ( 0, 6,  0, 7,  1,     "AMD Duron (unknown type)  (Morgan A1)" ),
    new FMQ ( 0, 6,  0, 7,     sD, "AMD Duron MP (Morgan)" ),
    new FMQ ( 0, 6,  0, 7,     MD, "AMD mobile Duron (Morgan)" ),
    new FMQ ( 0, 6,  0, 7,     dD, "AMD Duron (Morgan)" ),
    new FM  ( 0, 6,  0, 7,         "AMD Duron (unknown type)  (Morgan)" ),
    new FMSQ( 0, 6,  0, 8,  0, dS, "AMD Sempron (Thoroughbred A0)" ),
    new FMSQ( 0, 6,  0, 8,  0, sD, "AMD Duron MP (Applebred A0)" ),
    new FMSQ( 0, 6,  0, 8,  0, dD, "AMD Duron (Applebred A0)" ),
    new FMSQ( 0, 6,  0, 8,  0, MX, "AMD mobile Athlon XP (Thoroughbred A0)" ),
    new FMSQ( 0, 6,  0, 8,  0, sA, "AMD Athlon MP (Thoroughbred A0)" ),
    new FMSQ( 0, 6,  0, 8,  0, dX, "AMD Athlon XP (Thoroughbred A0)" ),
    new FMSQ( 0, 6,  0, 8,  0, dA, "AMD Athlon (Thoroughbred A0)" ),
    new FMS ( 0, 6,  0, 8,  0,     "AMD Athlon (unknown type) (Thoroughbred A0)" ),
    new FMSQ( 0, 6,  0, 8,  1, MG, "AMD Geode NX (Thoroughbred B0)" ),
    new FMSQ( 0, 6,  0, 8,  1, dS, "AMD Sempron (Thoroughbred B0)" ),
    new FMSQ( 0, 6,  0, 8,  1, sD, "AMD Duron MP (Applebred B0)" ),
    new FMSQ( 0, 6,  0, 8,  1, dD, "AMD Duron (Applebred B0)" ),
    new FMSQ( 0, 6,  0, 8,  1, sA, "AMD Athlon MP (Thoroughbred B0)" ),
    new FMSQ( 0, 6,  0, 8,  1, dX, "AMD Athlon XP (Thoroughbred B0)" ),
    new FMSQ( 0, 6,  0, 8,  1, dA, "AMD Athlon (Thoroughbred B0)" ),
    new FMS ( 0, 6,  0, 8,  1,     "AMD Athlon (unknown type) (Thoroughbred B0)" ),
    new FMQ ( 0, 6,  0, 8,     MG, "AMD Geode NX (Thoroughbred)" ),
    new FMQ ( 0, 6,  0, 8,     dS, "AMD Sempron (Thoroughbred)" ),
    new FMQ ( 0, 6,  0, 8,     sD, "AMD Duron MP (Thoroughbred)" ),
    new FMQ ( 0, 6,  0, 8,     dD, "AMD Duron (Thoroughbred)" ),
    new FMQ ( 0, 6,  0, 8,     MX, "AMD mobile Athlon XP (Thoroughbred)" ),
    new FMQ ( 0, 6,  0, 8,     sA, "AMD Athlon MP (Thoroughbred)" ),
    new FMQ ( 0, 6,  0, 8,     dX, "AMD Athlon XP (Thoroughbred)" ),
    new FMQ ( 0, 6,  0, 8,     dA, "AMD Athlon XP (Thoroughbred)" ),
    new FM  ( 0, 6,  0, 8,         "AMD Athlon (unknown type) (Thoroughbred)" ),
    new FMSQ( 0, 6,  0,10,  0, dS, "AMD Sempron (Barton A2)" ),
    new FMSQ( 0, 6,  0,10,  0, ML, "AMD mobile Athlon XP-M (LV) (Barton A2)" ),
    new FMSQ( 0, 6,  0,10,  0, MX, "AMD mobile Athlon XP-M (Barton A2)" ),
    new FMSQ( 0, 6,  0,10,  0, dt, "AMD Athlon XP (Thorton A2)" ),
    new FMSQ( 0, 6,  0,10,  0, sA, "AMD Athlon MP (Barton A2)" ),
    new FMSQ( 0, 6,  0,10,  0, dX, "AMD Athlon XP (Barton A2)" ),
    new FMS ( 0, 6,  0,10,  0,     "AMD Athlon (unknown type) (Barton A2)" ),
    new FMQ ( 0, 6,  0,10,     dS, "AMD Sempron (Barton)" ),
    new FMQ ( 0, 6,  0,10,     ML, "AMD mobile Athlon XP-M (LV) (Barton)" ),
    new FMQ ( 0, 6,  0,10,     MX, "AMD mobile Athlon XP-M (Barton)" ),
    new FMQ ( 0, 6,  0,10,     sA, "AMD Athlon MP (Barton)" ),
    new FMQ ( 0, 6,  0,10,     dX, "AMD Athlon XP (Barton)" ),
    new FM  ( 0, 6,  0,10,         "AMD Athlon (unknown type) (Barton)" ),
    new F   ( 0, 6,                "AMD Athlon (unknown model)" ),
    new F   ( 0, 7,                "AMD Opteron (unknown model)" ),
    new FMS ( 0,15,  0, 4,  0,     "AMD Athlon 64 (SledgeHammer SH7-B0)" ),
    new FMSQ( 0,15,  0, 4,  8, MX, "AMD mobile Athlon XP-M (SledgeHammer SH7-C0)" ),
    new FMSQ( 0,15,  0, 4,  8, MA, "AMD mobile Athlon 64 (SledgeHammer SH7-C0)" ),
    new FMSQ( 0,15,  0, 4,  8, dA, "AMD Athlon 64 (SledgeHammer SH7-C0)" ),
    new FMS ( 0,15,  0, 4,  8,     "AMD Athlon 64 (unknown type) (SledgeHammer SH7-C0)" ),
    new FMSQ( 0,15,  0, 4, 10, MX, "AMD mobile Athlon XP-M (SledgeHammer SH7-CG)" ),
    new FMSQ( 0,15,  0, 4, 10, MA, "AMD mobile Athlon 64 (SledgeHammer SH7-CG)" ),
    new FMSQ( 0,15,  0, 4, 10, dA, "AMD Athlon 64 (SledgeHammer SH7-CG)" ),
    new FMS ( 0,15,  0, 4, 10,     "AMD Athlon 64 (unknown type) (SledgeHammer SH7-CG)" ),
    new FMQ ( 0,15,  0, 4,     MX, "AMD mobile Athlon XP-M (SledgeHammer SH7)" ),
    new FMQ ( 0,15,  0, 4,     MA, "AMD mobile Athlon 64 (SledgeHammer SH7)" ),
    new FMQ ( 0,15,  0, 4,     dA, "AMD Athlon 64 (SledgeHammer SH7)" ),
    new FM  ( 0,15,  0, 4,         "AMD Athlon 64 (unknown type) (SledgeHammer SH7)" ),
    new FMS ( 0,15,  0, 5,  0,     "AMD Opteron (DP SledgeHammer SH7-B0)" ),
    new FMS ( 0,15,  0, 5,  1,     "AMD Opteron (DP SledgeHammer SH7-B3)" ),
    new FMSQ( 0,15,  0, 5,  8, sO, "AMD Opteron (DP SledgeHammer SH7-C0)" ),
    new FMSQ( 0,15,  0, 5,  8, dF, "AMD Athlon 64 FX (DP SledgeHammer SH7-C0)" ),
    new FMS ( 0,15,  0, 5,  8,     "AMD Athlon 64 (unknown type) (DP SledgeHammer SH7-C0)" ),
    new FMSQ( 0,15,  0, 5, 10, sO, "AMD Opteron (DP SledgeHammer SH7-CG)" ),
    new FMSQ( 0,15,  0, 5, 10, dF, "AMD Athlon 64 FX (DP SledgeHammer SH7-CG)" ),
    new FMS ( 0,15,  0, 5, 10,     "AMD Athlon 64 (unknown type) (DP SledgeHammer SH7-CG)" ),
    new FMQ ( 0,15,  0, 5,     sO, "AMD Opteron (SledgeHammer SH7)" ),
    new FMQ ( 0,15,  0, 5,     dF, "AMD Athlon 64 FX (SledgeHammer SH7)" ),
    new FM  ( 0,15,  0, 5,         "AMD Athlon 64 (unknown type) (SledgeHammer SH7) FX" ),
    new FMSQ( 0,15,  0, 7, 10, dF, "AMD Athlon 64 FX (DP SledgeHammer SH7-CG)" ),
    new FMSQ( 0,15,  0, 7, 10, dA, "AMD Athlon 64 (DP SledgeHammer SH7-CG)" ),
    new FMS ( 0,15,  0, 7, 10,     "AMD Athlon 64 (unknown type) (DP SledgeHammer SH7-CG)" ),
    new FMQ ( 0,15,  0, 7,     dF, "AMD Athlon 64 FX (DP SledgeHammer SH7)" ),
    new FMQ ( 0,15,  0, 7,     dA, "AMD Athlon 64 (DP SledgeHammer SH7)" ),
    new FM  ( 0,15,  0, 7,         "AMD Athlon 64 (unknown type) (DP SledgeHammer SH7)" ),
    new FMSQ( 0,15,  0, 8,  2, MS, "AMD mobile Sempron (ClawHammer CH7-CG)" ),
    new FMSQ( 0,15,  0, 8,  2, MX, "AMD mobile Athlon XP-M (ClawHammer CH7-CG)" ),
    new FMSQ( 0,15,  0, 8,  2, MA, "AMD mobile Athlon 64 (Odessa CH7-CG)" ),
    new FMSQ( 0,15,  0, 8,  2, dA, "AMD Athlon 64 (ClawHammer CH7-CG)" ),
    new FMS ( 0,15,  0, 8,  2,     "AMD Athlon 64 (unknown type) (ClawHammer/Odessa CH7-CG)" ),
    new FMQ ( 0,15,  0, 8,     MS, "AMD mobile Sempron (Odessa CH7)" ),
    new FMQ ( 0,15,  0, 8,     MX, "AMD mobile Athlon XP-M (Odessa CH7)" ),
    new FMQ ( 0,15,  0, 8,     MA, "AMD mobile Athlon 64 (Odessa CH7)" ),
    new FMQ ( 0,15,  0, 8,     dA, "AMD Athlon 64 (ClawHammer CH7)" ),
    new FM  ( 0,15,  0, 8,         "AMD Athlon 64 (unknown type) (ClawHammer/Odessa CH7)" ),
    new FMS ( 0,15,  0,11,  2,     "AMD Athlon 64 (ClawHammer CH7-CG)" ),
    new FM  ( 0,15,  0,11,         "AMD Athlon 64 (ClawHammer CH7)" ),
    new FMSQ( 0,15,  0,12,  0, MS, "AMD mobile Sempron (Dublin DH7-CG)" ),
    new FMSQ( 0,15,  0,12,  0, dS, "AMD Sempron (Paris DH7-CG)" ),
    new FMSQ( 0,15,  0,12,  0, MX, "AMD mobile Athlon XP-M (ClawHammer/Odessa DH7-CG)" ),
    new FMSQ( 0,15,  0,12,  0, MA, "AMD mobile Athlon 64 (ClawHammer/Odessa DH7-CG)" ),
    new FMSQ( 0,15,  0,12,  0, dA, "AMD Athlon 64 (NewCastle DH7-CG)" ),
    new FMS ( 0,15,  0,12,  0,     "AMD Athlon 64 (unknown type) (ClawHammer/Odessa/NewCastle/Paris/Dublin DH7-CG)" ),
    new FMQ ( 0,15,  0,12,     MS, "AMD mobile Sempron (Dublin DH7)" ),
    new FMQ ( 0,15,  0,12,     dS, "AMD Sempron (Paris DH7)" ),
    new FMQ ( 0,15,  0,12,     MX, "AMD mobile Athlon XP-M (NewCastle DH7)" ),
    new FMQ ( 0,15,  0,12,     MA, "AMD mobile Athlon 64 (ClawHammer/Odessa DH7)" ),
    new FMQ ( 0,15,  0,12,     dA, "AMD Athlon 64 (NewCastle DH7)" ),
    new FM  ( 0,15,  0,12,         "AMD Athlon 64 (unknown type) (ClawHammer/Odessa/NewCastle/Paris/Dublin DH7)" ),
    new FMSQ( 0,15,  0,14,  0, MS, "AMD mobile Sempron (Dublin DH7-CG)" ),
    new FMSQ( 0,15,  0,14,  0, dS, "AMD Sempron (Paris DH7-CG)" ),
    new FMSQ( 0,15,  0,14,  0, MX, "AMD mobile Athlon XP-M (ClawHammer/Odessa DH7-CG)" ),
    new FMSQ( 0,15,  0,14,  0, MA, "AMD mobile Athlon 64 (ClawHammer/Odessa DH7-CG)" ),
    new FMSQ( 0,15,  0,14,  0, dA, "AMD Athlon 64 (NewCastle DH7-CG)" ),
    new FMS ( 0,15,  0,14,  0,     "AMD Athlon 64 (unknown type) (ClawHammer/Odessa/NewCastle/Paris/Dublin DH7-CG)" ),
    new FMQ ( 0,15,  0,14,     dS, "AMD Sempron (Paris DH7)" ),
    new FMQ ( 0,15,  0,14,     MS, "AMD mobile Sempron (Dublin DH7)" ),
    new FMQ ( 0,15,  0,14,     MX, "AMD mobile Athlon XP-M (ClawHammer/Odessa DH7)" ),
    new FMQ ( 0,15,  0,14,     MA, "AMD mobile Athlon 64 (ClawHammer/Odessa DH7)" ),
    new FMQ ( 0,15,  0,14,     dA, "AMD Athlon 64 (NewCastle DH7)" ),
    new FM  ( 0,15,  0,14,         "AMD Athlon 64 (unknown type) (ClawHammer/Odessa/NewCastle/Paris/Dublin DH7)" ),
    new FMSQ( 0,15,  0,15,  0, dS, "AMD Sempron (Paris DH7-CG)" ),
    new FMSQ( 0,15,  0,15,  0, MA, "AMD mobile Athlon 64 (ClawHammer/Odessa DH7-CG)" ),
    new FMSQ( 0,15,  0,15,  0, dA, "AMD Athlon 64 (NewCastle DH7-CG)" ),
    new FMS ( 0,15,  0,15,  0,     "AMD Athlon 64 (unknown type) (ClawHammer/Odessa/NewCastle/Paris DH7-CG)" ),
    new FMQ ( 0,15,  0,15,     dS, "AMD Sempron (Paris DH7)" ),
    new FMQ ( 0,15,  0,15,     MA, "AMD mobile Athlon 64 (ClawHammer/Odessa DH7)" ),
    new FMQ ( 0,15,  0,15,     dA, "AMD Athlon 64 (NewCastle DH7)" ),
    new FM  ( 0,15,  0,15,         "AMD Athlon 64 (unknown type) (ClawHammer/Odessa/NewCastle/Paris DH7)" ),
    new FMSQ( 0,15,  1, 4,  0, MX, "AMD mobile Athlon XP-M (Oakville SH7-D0)" ),
    new FMSQ( 0,15,  1, 4,  0, MA, "AMD mobile Athlon 64 (Oakville SH7-D0)" ),
    new FMSQ( 0,15,  1, 4,  0, dA, "AMD Athlon 64 (Winchester SH7-D0)" ),
    new FMS ( 0,15,  1, 4,  0,     "AMD Athlon 64 (unknown type) (Winchester/Oakville SH7-D0)" ),
    new FMQ ( 0,15,  1, 4,     MX, "AMD mobile Athlon XP-M (Oakville SH7)" ),
    new FMQ ( 0,15,  1, 4,     MA, "AMD mobile Athlon 64 (Oakville SH7)" ),
    new FMQ ( 0,15,  1, 4,     dA, "AMD Athlon 64 (Winchester SH7)" ),
    new FM  ( 0,15,  1, 4,         "AMD Athlon 64 (unknown type) (Winchester/Oakville SH7)" ),
    new FMSQ( 0,15,  1, 5,  0, sO, "AMD Opteron (Winchester SH7-D0)" ),
    new FMSQ( 0,15,  1, 5,  0, dF, "AMD Athlon 64 FX (Winchester SH7-D0)" ),
    new FMS ( 0,15,  1, 5,  0,     "AMD Athlon 64 (unknown type) (Winchester SH7-D0)" ),
    new FMQ ( 0,15,  1, 5,     sO, "AMD Opteron (Winchester SH7)" ),
    new FMQ ( 0,15,  1, 5,     dF, "AMD Athlon 64 FX (Winchester SH7)" ),
    new FM  ( 0,15,  1, 5,         "AMD Athlon 64 (unknown type) (Winchester SH7)" ),
    new FMSQ( 0,15,  1, 7,  0, dF, "AMD Athlon 64 FX (Winchester SH7-D0)" ),
    new FMSQ( 0,15,  1, 7,  0, dA, "AMD Athlon 64 (Winchester SH7-D0)" ),
    new FMS ( 0,15,  1, 7,  0,     "AMD Athlon 64 (unknown type) (Winchester SH7-D0)" ),
    new FMQ ( 0,15,  1, 7,     dF, "AMD Athlon 64 FX (Winchester SH7)" ),
    new FMQ ( 0,15,  1, 7,     dA, "AMD Athlon 64 (Winchester SH7)" ),
    new FM  ( 0,15,  1, 7,         "AMD Athlon 64 (unknown type) (Winchester SH7)" ),
    new FMSQ( 0,15,  1, 8,  0, MS, "AMD mobile Sempron (Georgetown/Sonora CH-D0)" ),
    new FMSQ( 0,15,  1, 8,  0, MX, "AMD mobile Athlon XP-M (Oakville CH-D0)" ),
    new FMSQ( 0,15,  1, 8,  0, MA, "AMD mobile Athlon 64 (Oakville CH-D0)" ),
    new FMSQ( 0,15,  1, 8,  0, dA, "AMD Athlon 64 (Winchester CH-D0)" ),
    new FMS ( 0,15,  1, 8,  0,     "AMD Athlon 64 (unknown type) (Winchester/Oakville/Georgetown/Sonora CH-D0)" ),
    new FMQ ( 0,15,  1, 8,     MS, "AMD mobile Sempron (Georgetown/Sonora CH)" ),
    new FMQ ( 0,15,  1, 8,     MX, "AMD mobile Athlon XP-M (Oakville CH)" ),
    new FMQ ( 0,15,  1, 8,     MA, "AMD mobile Athlon 64 (Oakville CH)" ),
    new FMQ ( 0,15,  1, 8,     dA, "AMD Athlon 64 (Winchester CH)" ),
    new FM  ( 0,15,  1, 8,         "AMD Athlon 64 (unknown type) (Winchester/Oakville/Georgetown/Sonora CH)" ),
    new FMS ( 0,15,  1,11,  0,     "AMD Athlon 64 (Winchester CH-D0)" ),
    new FM  ( 0,15,  1,11,         "AMD Athlon 64 (Winchester CH)" ),
    new FMSQ( 0,15,  1,12,  0, MS, "AMD mobile Sempron (Georgetown/Sonora DH8-D0)" ),
    new FMSQ( 0,15,  1,12,  0, dS, "AMD Sempron (Palermo DH8-D0)" ),
    new FMSQ( 0,15,  1,12,  0, MX, "AMD Athlon XP-M (Winchester DH8-D0)" ),
    new FMSQ( 0,15,  1,12,  0, MA, "AMD mobile Athlon 64 (Oakville DH8-D0)" ),
    new FMSQ( 0,15,  1,12,  0, dA, "AMD Athlon 64 (Winchester DH8-D0)" ),
    new FMS ( 0,15,  1,12,  0,     "AMD Athlon 64 (unknown type) (Winchester/Oakville/Georgetown/Sonora/Palermo DH8-D0)" ),
    new FMQ ( 0,15,  1,12,     MS, "AMD mobile Sempron (Georgetown/Sonora DH8)" ),
    new FMQ ( 0,15,  1,12,     dS, "AMD Sempron (Palermo DH8)" ),
    new FMQ ( 0,15,  1,12,     MX, "AMD Athlon XP-M (Winchester DH8)" ),
    new FMQ ( 0,15,  1,12,     MA, "AMD mobile Athlon 64 (Oakville DH8)" ),
    new FMQ ( 0,15,  1,12,     dA, "AMD Athlon 64 (Winchester DH8)" ),
    new FM  ( 0,15,  1,12,         "AMD Athlon 64 (Winchester/Oakville/Georgetown/Sonora/Palermo DH8)" ),
    new FMSQ( 0,15,  1,15,  0, dS, "AMD Sempron (Palermo DH8-D0)" ),
    new FMSQ( 0,15,  1,15,  0, dA, "AMD Athlon 64 (Winchester DH8-D0)" ),
    new FMS ( 0,15,  1,15,  0,     "AMD Athlon 64 (Winchester DH8-D0) / Sempron (Palermo DH8-D0)" ),
    new FMQ ( 0,15,  1,15,     dS, "AMD Sempron (Palermo DH8)" ),
    new FMQ ( 0,15,  1,15,     dA, "AMD Athlon 64 (Winchester DH8)" ),
    new FM  ( 0,15,  1,15,         "AMD Athlon 64 (unknown type) (Winchester/Palermo DH8)" ),
    new FMSQ( 0,15,  2, 1,  0, s8, "AMD Dual Core Opteron (Egypt JH-E1)" ),
    new FMSQ( 0,15,  2, 1,  0, sO, "AMD Dual Core Opteron (Italy JH-E1)" ),
    new FMS ( 0,15,  2, 1,  0,     "AMD Dual Core Opteron (Italy/Egypt JH-E1)" ),
    new FMSQ( 0,15,  2, 1,  2, s8, "AMD Dual Core Opteron (Egypt JH-E6)" ),
    new FMSQ( 0,15,  2, 1,  2, sO, "AMD Dual Core Opteron (Italy JH-E6)" ),
    new FMS ( 0,15,  2, 1,  2,     "AMD Dual Core Opteron (Italy/Egypt JH-E6)" ),
    new FMQ ( 0,15,  2, 1,     s8, "AMD Dual Core Opteron (Egypt JH)" ),
    new FMQ ( 0,15,  2, 1,     sO, "AMD Dual Core Opteron (Italy JH)" ),
    new FM  ( 0,15,  2, 1,         "AMD Dual Core Opteron (Italy/Egypt JH)" ),
    new FMSQ( 0,15,  2, 3,  2, DO, "AMD Dual Core Opteron (Denmark JH-E6)" ),
    new FMSQ( 0,15,  2, 3,  2, dF, "AMD Athlon 64 FX (Toledo JH-E6)" ),
    new FMSQ( 0,15,  2, 3,  2, dm, "AMD Athlon 64 X2 (Manchester JH-E6)" ),
    new FMSQ( 0,15,  2, 3,  2, dA, "AMD Athlon 64 X2 (Toledo JH-E6)" ),
    new FMS ( 0,15,  2, 3,  2,     "AMD Athlon 64 (unknown type) (Toledo/Manchester/Denmark JH-E6)" ),
    new FMQ ( 0,15,  2, 3,     sO, "AMD Dual Core Opteron (Denmark JH)" ),
    new FMQ ( 0,15,  2, 3,     dF, "AMD Athlon 64 FX (Toledo JH)" ),
    new FMQ ( 0,15,  2, 3,     dm, "AMD Athlon 64 X2 (Manchester JH)" ),
    new FMQ ( 0,15,  2, 3,     dA, "AMD Athlon 64 X2 (Toledo JH)" ),
    new FM  ( 0,15,  2, 3,         "AMD Athlon 64 (unknown type) (Toledo/Manchester/Denmark JH)" ),
    new FMSQ( 0,15,  2, 4,  2, MA, "AMD mobile Athlon 64 (Newark SH-E5)" ),
    new FMSQ( 0,15,  2, 4,  2, MT, "AMD mobile Turion (Lancaster/Richmond SH-E5)" ),
    new FMS ( 0,15,  2, 4,  2,     "AMD mobile Athlon 64 (unknown type) (Newark/Lancaster/Richmond SH-E5)" ),
    new FMQ ( 0,15,  2, 4,     MA, "AMD mobile Athlon 64 (Newark SH)" ),
    new FMQ ( 0,15,  2, 4,     MT, "AMD mobile Turion (Lancaster/Richmond SH)" ),
    new FM  ( 0,15,  2, 4,         "AMD mobile Athlon 64 (unknown type) (Newark/Lancaster/Richmond SH)" ),
    new FMQ ( 0,15,  2, 5,     s8, "AMD Opteron (Athens SH-E4)" ),
    new FMQ ( 0,15,  2, 5,     sO, "AMD Opteron (Troy SH-E4)" ),
    new FM  ( 0,15,  2, 5,         "AMD Opteron (Troy/Athens SH-E4)" ),
    new FMSQ( 0,15,  2, 7,  1, sO, "AMD Opteron (Venus SH-E4)" ),
    new FMSQ( 0,15,  2, 7,  1, dF, "AMD Athlon 64 FX (San Diego SH-E4)" ),
    new FMSQ( 0,15,  2, 7,  1, dA, "AMD Athlon 64 (San Diego SH-E4)" ),
    new FMS ( 0,15,  2, 7,  1,     "AMD Athlon 64 (unknown type) (Venus/San Diego SH-E4)" ),
    new FMQ ( 0,15,  2, 7,     sO, "AMD Opteron (San Diego SH)" ),
    new FMQ ( 0,15,  2, 7,     dF, "AMD Athlon 64 FX (San Diego SH)" ),
    new FMQ ( 0,15,  2, 7,     dA, "AMD Athlon 64 (San Diego SH)" ),
    new FM  ( 0,15,  2, 7,         "AMD Athlon 64 (unknown type) (San Diego SH)" ),
    new FM  ( 0,15,  2,11,         "AMD Athlon 64 X2 (Manchester BH-E4)" ),
    new FMS ( 0,15,  2,12,  0,     "AMD Sempron (Palermo DH-E3)" ),
    new FMSQ( 0,15,  2,12,  2, MS, "AMD mobile Sempron (Albany/Roma DH-E6)" ),
    new FMSQ( 0,15,  2,12,  2, dS, "AMD Sempron (Palermo DH-E6)" ),
    new FMSQ( 0,15,  2,12,  2, dA, "AMD Athlon 64 (Venice DH-E6)" ),
    new FMS ( 0,15,  2,12,  2,     "AMD Athlon 64 (Venice/Palermo/Albany/Roma DH-E6)" ),
    new FMQ ( 0,15,  2,12,     MS, "AMD mobile Sempron (Albany/Roma DH)" ),
    new FMQ ( 0,15,  2,12,     dS, "AMD Sempron (Palermo DH)" ),
    new FMQ ( 0,15,  2,12,     dA, "AMD Athlon 64 (Venice DH)" ),
    new FM  ( 0,15,  2,12,         "AMD Athlon 64 (Venice/Palermo/Albany/Roma DH)" ),
    new FMSQ( 0,15,  2,15,  0, dS, "AMD Sempron (Palermo DH-E3)" ),
    new FMSQ( 0,15,  2,15,  0, dA, "AMD Athlon 64 (Venice DH-E3)" ),
    new FMS ( 0,15,  2,15,  0,     "AMD Athlon 64 (Venice/Palermo DH-E3)" ),
    new FMSQ( 0,15,  2,15,  2, dS, "AMD Sempron (Palermo DH-E6)" ),
    new FMSQ( 0,15,  2,15,  2, dA, "AMD Athlon 64 (Venice DH-E6)" ),
    new FMS ( 0,15,  2,15,  2,     "AMD Athlon 64 (Venice/Palermo DH-E6)" ),
    new FMQ ( 0,15,  2,15,     dS, "AMD Sempron (Palermo DH)" ),
    new FMQ ( 0,15,  2,15,     dA, "AMD Athlon 64 (Venice DH)" ),
    new FM  ( 0,15,  2,15,         "AMD Athlon 64 (Venice/Palermo DH)" ),
    new FMS ( 0,15,  4, 1,  2,     "AMD Dual-Core Opteron (Santa Rosa JH-F2)" ),
    new FMS ( 0,15,  4, 1,  3,     "AMD Dual-Core Opteron (Santa Rosa JH-F3)" ),
    new FM  ( 0,15,  4, 1,         "AMD Dual-Core Opteron (Santa Rosa)" ),
    new FMSQ( 0,15,  4, 3,  2, DO, "AMD Dual-Core Opteron (Santa Rosa JH-F2)" ),
    new FMSQ( 0,15,  4, 3,  2, sO, "AMD Opteron (Santa Rosa JH-F2)" ),
    new FMSQ( 0,15,  4, 3,  2, dF, "AMD Athlon 64 FX Dual-Core (Windsor JH-F2)" ),
    new FMSQ( 0,15,  4, 3,  2, dA, "AMD Athlon 64 X2 Dual-Core (Windsor JH-F2)" ),
    new FMS ( 0,15,  4, 3,  2,     "AMD Athlon 64 (unknown type) (Windsor JH-F2)" ),
    new FMSQ( 0,15,  4, 3,  3, DO, "AMD Dual-Core Opteron (Santa Rosa JH-F3)" ),
    new FMSQ( 0,15,  4, 3,  3, sO, "AMD Opteron (Santa Rosa JH-F3)" ),
    new FMSQ( 0,15,  4, 3,  3, dF, "AMD Athlon 64 FX Dual-Core (Windsor JH-F3)" ),
    new FMSQ( 0,15,  4, 3,  3, dA, "AMD Athlon 64 X2 Dual-Core (Windsor JH-F3)" ),
    new FMS ( 0,15,  4, 3,  3,     "AMD Athlon 64 (unknown type) (Windsor/Santa Rosa JH-F3)" ),
    new FMQ ( 0,15,  4, 3,     DO, "AMD Dual-Core Opteron (Santa Rosa)" ),
    new FMQ ( 0,15,  4, 3,     sO, "AMD Opteron (Santa Rosa)" ),
    new FMQ ( 0,15,  4, 3,     dF, "AMD Athlon 64 FX Dual-Core (Windsor)" ),
    new FMQ ( 0,15,  4, 3,     dA, "AMD Athlon 64 X2 Dual-Core (Windsor)" ),
    new FM  ( 0,15,  4, 3,         "AMD Athlon 64 (unknown type) (Windsor/Santa Rosa)" ),
    new FMSQ( 0,15,  4, 8,  2, dA, "AMD Athlon 64 X2 Dual-Core (Windsor BH-F2)" ),
    new FMSQ( 0,15,  4, 8,  2, Mt, "AMD Turion 64 X2 (Trinidad BH-F2)" ),
    new FMSQ( 0,15,  4, 8,  2, MT, "AMD Turion 64 X2 (Taylor BH-F2)" ),
    new FMS ( 0,15,  4, 8,  2,     "AMD Athlon 64 (unknown type) (Windsor/Taylor/Trinidad BH-F2)" ),
    new FMQ ( 0,15,  4, 8,     dA, "AMD Athlon 64 X2 Dual-Core (Windsor)" ),
    new FMQ ( 0,15,  4, 8,     Mt, "AMD Turion 64 X2 (Trinidad)" ),
    new FMQ ( 0,15,  4, 8,     MT, "AMD Turion 64 X2 (Taylor)" ),
    new FM  ( 0,15,  4, 8,         "AMD Athlon 64 (unknown type) (Windsor/Taylor/Trinidad)" ),
    new FMS ( 0,15,  4,11,  2,     "AMD Athlon 64 X2 Dual-Core (Windsor BH-F2)" ),
    new FM  ( 0,15,  4,11,         "AMD Athlon 64 X2 Dual-Core (Windsor)" ),
    new FMSQ( 0,15,  4,12,  2, MS, "AMD mobile Sempron (Keene BH-F2)" ),
    new FMSQ( 0,15,  4,12,  2, dS, "AMD Sempron (Manila BH-F2)" ),
    new FMSQ( 0,15,  4,12,  2, Mt, "AMD Turion (Trinidad BH-F2)" ),
    new FMSQ( 0,15,  4,12,  2, MT, "AMD Turion (Taylor BH-F2)" ),
    new FMSQ( 0,15,  4,12,  2, dA, "AMD Athlon 64 (Orleans BH-F2)" ), 
    new FMS ( 0,15,  4,12,  2,     "AMD Athlon 64 (unknown type) (Orleans/Manila/Keene/Taylor/Trinidad BH-F2)" ),
    new FMQ ( 0,15,  4,12,     MS, "AMD mobile Sempron (Keene)" ),
    new FMQ ( 0,15,  4,12,     dS, "AMD Sempron (Manila)" ),
    new FMQ ( 0,15,  4,12,     Mt, "AMD Turion (Trinidad)" ),
    new FMQ ( 0,15,  4,12,     MT, "AMD Turion (Taylor)" ),
    new FMQ ( 0,15,  4,12,     dA, "AMD Athlon 64 (Orleans)" ), 
    new FM  ( 0,15,  4,12,         "AMD Athlon 64 (unknown type) (Orleans/Manila/Keene/Taylor/Trinidad)" ),
    new FMSQ( 0,15,  4,15,  2, MS, "AMD mobile Sempron (Keene DH-F2)" ),
    new FMSQ( 0,15,  4,15,  2, dS, "AMD Sempron (Manila DH-F2)" ),
    new FMSQ( 0,15,  4,15,  2, dA, "AMD Athlon 64 (Orleans DH-F2)" ),
    new FMS ( 0,15,  4,15,  2,     "AMD Athlon 64 (unknown type) (Orleans/Manila/Keene DH-F2)" ),
    new FMQ ( 0,15,  4,15,     MS, "AMD mobile Sempron (Keene)" ),
    new FMQ ( 0,15,  4,15,     dS, "AMD Sempron (Manila)" ),
    new FMQ ( 0,15,  4,15,     dA, "AMD Athlon 64 (Orleans)" ),
    new FM  ( 0,15,  4,15,         "AMD Athlon 64 (unknown type) (Orleans/Manila/Keene)" ),
    new FMS ( 0,15,  5,13,  3,     "AMD Opteron (Santa Rosa JH-F3)" ),
    new FM  ( 0,15,  5,13,         "AMD Opteron (Santa Rosa)" ),
    new FMSQ( 0,15,  5,15,  2, MS, "AMD mobile Sempron (Keene DH-F2)" ),
    new FMSQ( 0,15,  5,15,  2, dS, "AMD Sempron (Manila DH-F2)" ),
    new FMSQ( 0,15,  5,15,  2, dA, "AMD Athlon 64 (Orleans DH-F2)" ),
    new FMS ( 0,15,  5,15,  2,     "AMD Athlon 64 (unknown type) (Orleans/Manila/Keene DH-F2)" ),
    new FMS ( 0,15,  5,15,  3,     "AMD Athlon 64 (Orleans DH-F3)" ),
    new FMQ ( 0,15,  5,15,     MS, "AMD mobile Sempron (Keene)" ),
    new FMQ ( 0,15,  5,15,     dS, "AMD Sempron (Manila)" ),
    new FMQ ( 0,15,  5,15,     dA, "AMD Athlon 64 (Orleans)" ),
    new FM  ( 0,15,  5,15,         "AMD Athlon 64 (unknown type) (Orleans/Manila/Keene)" ),
    new FM  ( 0,15,  5,15,         "AMD Athlon 64 (Orleans)" ),
    new FMS ( 0,15,  6, 8,  1,     "AMD Turion 64 X2 (Tyler BH-G1)" ),
    new FMSQ( 0,15,  6, 8,  2, MT, "AMD Turion 64 X2 (Tyler BH-G2)" ),
    new FMSQ( 0,15,  6, 8,  2, dS, "AMD Sempron Dual-Core (Tyler BH-G2)" ),
    new FMS ( 0,15,  6, 8,  2,     "AMD Turion 64 (unknown type) (Tyler BH-G2)" ),
    new FMQ ( 0,15,  6, 8,     MT, "AMD Turion 64 X2 (Tyler)" ),
    new FMQ ( 0,15,  6, 8,     dS, "AMD Sempron Dual-Core (Tyler)" ),
    new FM  ( 0,15,  6, 8,         "AMD Turion 64 (unknown type) (Tyler)" ),
    new FMSQ( 0,15,  6,11,  1, dS, "AMD Sempron Dual-Core (Sparta BH-G1)" ),
    new FMSQ( 0,15,  6,11,  1, dA, "AMD Athlon 64 X2 Dual-Core (Brisbane BH-G1)" ),
    new FMS ( 0,15,  6,11,  1,     "AMD Athlon 64 (unknown type) (Brisbane/Sparta BH-G1)" ),
    new FMSQ( 0,15,  6,11,  2, dA, "AMD Athlon 64 X2 Dual-Core (Brisbane BH-G2)" ),
    new FMSQ( 0,15,  6,11,  2, Mn, "AMD Turion Neo X2 Dual-Core (Huron BH-G2)" ),
    new FMSQ( 0,15,  6,11,  2, MN, "AMD Athlon Neo X2 (Huron BH-G2)" ),
    new FMS ( 0,15,  6,11,  2,     "AMD Athlon 64 (unknown type) (Brisbane/Huron BH-G2)" ),
    new FMQ ( 0,15,  6,11,     dS, "AMD Sempron Dual-Core (Sparta)" ),
    new FMQ ( 0,15,  6,11,     Mn, "AMD Turion Neo X2 Dual-Core (Huron)" ),
    new FMQ ( 0,15,  6,11,     MN, "AMD Athlon Neo X2 (Huron)" ),
    new FMQ ( 0,15,  6,11,     dA, "AMD Athlon 64 X2 Dual-Core (Brisbane)" ),
    new FM  ( 0,15,  6,11,         "AMD Athlon 64 (unknown type) (Brisbane/Sparta/Huron)" ),
    new FMSQ( 0,15,  6,12,  2, MS, "AMD mobile Sempron (Sherman DH-G2)" ),
    new FMSQ( 0,15,  6,12,  2, dS, "AMD Sempron (Sparta DH-G2)" ),
    new FMSQ( 0,15,  6,12,  2, dA, "AMD Athlon 64 (Lima DH-G2)" ),
    new FMS ( 0,15,  6,12,  2,     "AMD Athlon 64 (unknown type) (Lima/Sparta/Sherman DH-G2)" ),
    new FMQ ( 0,15,  6,12,     MS, "AMD mobile Sempron (Sherman)" ),
    new FMQ ( 0,15,  6,12,     dS, "AMD Sempron (Sparta)" ),
    new FMQ ( 0,15,  6,12,     dA, "AMD Athlon 64 (Lima)" ),
    new FM  ( 0,15,  6,12,         "AMD Athlon 64 (unknown type) (Lima/Sparta/Sherman)" ),
    new FMSQ( 0,15,  6,15,  2, MS, "AMD mobile Sempron (Sherman DH-G2)" ),
    new FMSQ( 0,15,  6,15,  2, dS, "AMD Sempron (Sparta DH-G2)" ),
    new FMSQ( 0,15,  6,15,  2, MN, "AMD Athlon Neo (Huron DH-G2)" ),
    new FMS ( 0,15,  6,15,  2,     "AMD Athlon Neo (unknown type) (Huron/Sparta/Sherman DH-G2)" ),
    new FMQ ( 0,15,  6,15,     MS, "AMD mobile Sempron (Sherman)" ),
    new FMQ ( 0,15,  6,15,     dS, "AMD Sempron (Sparta)" ),
    new FMQ ( 0,15,  6,15,     MN, "AMD Athlon Neo (Huron)" ),
    new FM  ( 0,15,  6,15,         "AMD Athlon Neo (unknown type) (Huron/Sparta/Sherman)" ),
    new FMSQ( 0,15,  7,12,  2, MS, "AMD mobile Sempron (Sherman DH-G2)" ),
    new FMSQ( 0,15,  7,12,  2, dS, "AMD Sempron (Sparta DH-G2)" ),
    new FMSQ( 0,15,  7,12,  2, dA, "AMD Athlon (Lima DH-G2)" ),
    new FMS ( 0,15,  7,12,  2,     "AMD Athlon (unknown type) (Lima/Sparta/Sherman DH-G2)" ),
    new FMQ ( 0,15,  7,12,     MS, "AMD mobile Sempron (Sherman)" ),
    new FMQ ( 0,15,  7,12,     dS, "AMD Sempron (Sparta)" ),
    new FMQ ( 0,15,  7,12,     dA, "AMD Athlon (Lima)" ),
    new FM  ( 0,15,  7,12,         "AMD Athlon (unknown type) (Lima/Sparta/Sherman)" ),
    new FMSQ( 0,15,  7,15,  1, MS, "AMD mobile Sempron (Sherman DH-G1)" ),
    new FMSQ( 0,15,  7,15,  1, dS, "AMD Sempron (Sparta DH-G1)" ),
    new FMSQ( 0,15,  7,15,  1, dA, "AMD Athlon 64 (Lima DH-G1)" ),
    new FMS ( 0,15,  7,15,  1,     "AMD Athlon 64 (unknown type) (Lima/Sparta/Sherman DH-G1)" ),
    new FMSQ( 0,15,  7,15,  2, MS, "AMD mobile Sempron (Sherman DH-G2)" ),
    new FMSQ( 0,15,  7,15,  2, dS, "AMD Sempron (Sparta DH-G2)" ),
    new FMSQ( 0,15,  7,15,  2, MN, "AMD Athlon Neo (Huron DH-G2)" ),
    new FMSQ( 0,15,  7,15,  2, dA, "AMD Athlon 64 (Lima DH-G2)" ),
    new FMS ( 0,15,  7,15,  2,     "AMD Athlon 64 (unknown type) (Lima/Sparta/Sherman/Huron DH-G2)" ),
    new FMQ ( 0,15,  7,15,     MS, "AMD mobile Sempron (Sherman)" ),
    new FMQ ( 0,15,  7,15,     dS, "AMD Sempron (Sparta)" ),
    new FMQ ( 0,15,  7,15,     MN, "AMD Athlon Neo (Huron)" ),
    new FMQ ( 0,15,  7,15,     dA, "AMD Athlon 64 (Lima)" ),
    new FM  ( 0,15,  7,15,         "AMD Athlon 64 (unknown type) (Lima/Sparta/Sherman/Huron)" ),
    new FMS ( 0,15, 12, 1,  3,     "AMD Athlon 64 FX Dual-Core (Windsor JH-F3)" ),
    new FM  ( 0,15, 12, 1,         "AMD Athlon 64 FX Dual-Core (Windsor)" ),
    new F   ( 0,15,                "AMD (unknown model)" ),
    new FMS ( 1,15,  0, 0,  0,     "AMD (unknown type) (Barcelona DR-A0)" ), // sandpile.org
    new FMS ( 1,15,  0, 0,  1,     "AMD (unknown type) (Barcelona DR-A1)" ), // sandpile.org
    new FMS ( 1,15,  0, 0,  2,     "AMD (unknown type) (Barcelona DR-A2)" ), // sandpile.org
    new FMS ( 1,15,  0, 2,  0,     "AMD (unknown type) (Barcelona DR-B0)" ), // sandpile.org
    new FMSQ( 1,15,  0, 2,  1, sO, "AMD Quad-Core Opteron (Barcelona DR-B1)" ),
    new FMS ( 1,15,  0, 2,  1,     "AMD (unknown type) (Barcelona DR-B1)" ),
    new FMSQ( 1,15,  0, 2,  2, EO, "AMD Embedded Opteron (Barcelona DR-B2)" ),
    new FMSQ( 1,15,  0, 2,  2, sO, "AMD Quad-Core Opteron (Barcelona DR-B2)" ),
    new FMSQ( 1,15,  0, 2,  2, Tp, "AMD Phenom Triple-Core (Toliman DR-B2)" ),
    new FMSQ( 1,15,  0, 2,  2, Qp, "AMD Phenom Quad-Core (Agena DR-B2)" ),
    new FMS ( 1,15,  0, 2,  2,     "AMD (unknown type) (Barcelona/Toliman/Agena DR-B2)" ),
    new FMSQ( 1,15,  0, 2,  3, EO, "AMD Embedded Opteron (Barcelona DR-B3)" ),
    new FMSQ( 1,15,  0, 2,  3, sO, "AMD Quad-Core Opteron (Barcelona DR-B3)" ),
    new FMSQ( 1,15,  0, 2,  3, Tp, "AMD Phenom Triple-Core (Toliman DR-B3)" ),
    new FMSQ( 1,15,  0, 2,  3, Qp, "AMD Phenom Quad-Core (Agena DR-B3)" ),
    new FMSQ( 1,15,  0, 2,  3, dA, "AMD Athlon Dual-Core (Kuma DR-B3)" ),
    new FMS ( 1,15,  0, 2,  3,     "AMD (unknown type) (Barcelona/Toliman/Agena/Kuma DR-B3)" ),
    new FMS ( 1,15,  0, 2, 10,     "AMD Quad-Core Opteron (Barcelona DR-BA)" ),
    new FMQ ( 1,15,  0, 2,     EO, "AMD Embedded Opteron (Barcelona)" ),
    new FMQ ( 1,15,  0, 2,     sO, "AMD Quad-Core Opteron (Barcelona)" ),
    new FMQ ( 1,15,  0, 2,     Tp, "AMD Phenom Triple-Core (Toliman)" ),
    new FMQ ( 1,15,  0, 2,     Qp, "AMD Phenom Quad-Core (Agena)" ),
    new FMQ ( 1,15,  0, 2,     dA, "AMD Athlon Dual-Core (Kuma)" ),
    new FM  ( 1,15,  0, 2,         "AMD (unknown type) (Barcelona/Toliman/Agena/Kuma)" ),
    new FMS ( 1,15,  0, 4,  0,     "AMD Athlon (unknown type) (Regor/Propus/Shanghai/Callisto/Heka/Deneb RB-C0)" ), // sandpile.org
    new FMS ( 1,15,  0, 4,  1,     "AMD Athlon (unknown type) (Regor/Propus/Shanghai/Callisto/Heka/Deneb RB-C1)" ), // sandpile.org
    new FMSQ( 1,15,  0, 4,  2, EO, "AMD Embedded Opteron (Shanghai RB-C2)" ),
    new FMSQ( 1,15,  0, 4,  2, sO, "AMD Quad-Core Opteron (Shanghai RB-C2)" ),
    new FMSQ( 1,15,  0, 4,  2, dr, "AMD Athlon Dual-Core (Propus RB-C2)" ),
    new FMSQ( 1,15,  0, 4,  2, dA, "AMD Athlon Dual-Core (Regor RB-C2)" ),
    new FMSQ( 1,15,  0, 4,  2, Dp, "AMD Phenom II X2 (Callisto RB-C2)" ),
    new FMSQ( 1,15,  0, 4,  2, Tp, "AMD Phenom II X3 (Heka RB-C2)" ),
    new FMSQ( 1,15,  0, 4,  2, Qp, "AMD Phenom II X4 (Deneb RB-C2)" ),
    new FMS ( 1,15,  0, 4,  2,     "AMD Athlon (unknown type) (Regor/Propus/Shanghai/Callisto/Heka/Deneb RB-C2)" ),
    new FMSQ( 1,15,  0, 4,  3, Dp, "AMD Phenom II X2 (Callisto RB-C3)" ),
    new FMSQ( 1,15,  0, 4,  3, Tp, "AMD Phenom II X3 (Heka RB-C3)" ),
    new FMSQ( 1,15,  0, 4,  3, Qp, "AMD Phenom II X4 (Deneb RB-C3)" ),
    new FMS ( 1,15,  0, 4,  3,     "AMD Phenom II (unknown type) (Callisto/Heka/Deneb RB-C3)" ),
    new FMQ ( 1,15,  0, 4,     EO, "AMD Embedded Opteron (Shanghai)" ),
    new FMQ ( 1,15,  0, 4,     sO, "AMD Quad-Core Opteron (Shanghai)" ),
    new FMQ ( 1,15,  0, 4,     dr, "AMD Athlon Dual-Core (Propus)" ),
    new FMQ ( 1,15,  0, 4,     dA, "AMD Athlon Dual-Core (Regor)" ),
    new FMQ ( 1,15,  0, 4,     Dp, "AMD Phenom II X2 (Callisto)" ),
    new FMQ ( 1,15,  0, 4,     Tp, "AMD Phenom II X3 (Heka)" ),
    new FMQ ( 1,15,  0, 4,     Qp, "AMD Phenom II X4 (Deneb)" ),
    new FM  ( 1,15,  0, 4,         "AMD Athlon (unknown type) (Regor/Propus/Shanghai/Callisto/Heka/Deneb)" ),
    new FMS ( 1,15,  0, 5,  0,     "AMD Athlon (unknown type) (Regor/Rana/Propus BL-C0)" ), // sandpile.org
    new FMS ( 1,15,  0, 5,  1,     "AMD Athlon (unknown type) (Regor/Rana/Propus BL-C1)" ), // sandpile.org
    new FMSQ( 1,15,  0, 5,  2, DA, "AMD Athlon II X2 (Regor BL-C2)" ),
    new FMSQ( 1,15,  0, 5,  2, TA, "AMD Athlon II X3 (Rana BL-C2)" ),
    new FMSQ( 1,15,  0, 5,  2, QA, "AMD Athlon II X4 (Propus BL-C2)" ),
    new FMS ( 1,15,  0, 5,  2,     "AMD Athlon (unknown type) (Regor/Rana/Propus BL-C2)" ),
    new FMSQ( 1,15,  0, 5,  3, TA, "AMD Athlon II X3 (Rana BL-C3)" ),
    new FMSQ( 1,15,  0, 5,  3, QA, "AMD Athlon II X4 (Propus BL-C3)" ),
    new FMSQ( 1,15,  0, 5,  3, Tp, "AMD Phenom II Triple-Core (Heka BL-C3)" ),
    new FMSQ( 1,15,  0, 5,  3, Qp, "AMD Phenom II Quad-Core (Deneb BL-C3)" ),
    new FMS ( 1,15,  0, 5,  3,     "AMD Athlon (unknown type) (Regor/Rana/Propus/Callisto/Heka/Deneb BL-C3)" ),
    new FMQ ( 1,15,  0, 5,     DA, "AMD Athlon II X2 (Regor)" ),
    new FMQ ( 1,15,  0, 5,     TA, "AMD Athlon II X3 (Rana)" ),
    new FMQ ( 1,15,  0, 5,     QA, "AMD Athlon II X4 (Propus)" ),
    new FMQ ( 1,15,  0, 5,     Tp, "AMD Phenom II Triple-Core (Heka)" ),
    new FMQ ( 1,15,  0, 5,     Qp, "AMD Phenom II Quad-Core (Deneb)" ),
    new FM  ( 1,15,  0, 5,         "AMD Athlon (unknown type) (Regor/Rana/Propus/Callisto/Heka/Deneb)" ),
    new FMS ( 1,15,  0, 6,  0,     "AMD Athlon (unknown type) (Regor/Sargas/Caspain DA-C0)" ),
    new FMS ( 1,15,  0, 6,  1,     "AMD Athlon (unknown type) (Regor/Sargas/Caspain DA-C1)" ),
    new FMSQ( 1,15,  0, 6,  2, MS, "AMD Sempron Mobile (Sargas DA-C2)" ),
    new FMSQ( 1,15,  0, 6,  2, dS, "AMD Sempron II (Sargas DA-C2)" ),
    new FMSQ( 1,15,  0, 6,  2, MT, "AMD Turion II Dual-Core Mobile (Caspian DA-C2)" ),
    new FMSQ( 1,15,  0, 6,  2, MA, "AMD Athlon II Dual-Core Mobile (Regor DA-C2)" ),
    new FMSQ( 1,15,  0, 6,  2, DA, "AMD Athlon II X2 (Regor DA-C2)" ),
    new FMSQ( 1,15,  0, 6,  2, dA, "AMD Athlon II (Sargas DA-C2)" ),
    new FMS ( 1,15,  0, 6,  2,     "AMD Athlon (unknown type) (Regor/Sargas/Caspain DA-C2)" ),
    new FMSQ( 1,15,  0, 6,  3, Ms, "AMD V-Series Mobile (Champlain DA-C3)" ),
    new FMSQ( 1,15,  0, 6,  3, DS, "AMD Sempron II X2 (Regor DA-C3)" ),
    new FMSQ( 1,15,  0, 6,  3, dS, "AMD Sempron II (Sargas DA-C3)" ),
    new FMSQ( 1,15,  0, 6,  3, MT, "AMD Turion II Dual-Core Mobile (Champlain DA-C3)" ),
    new FMSQ( 1,15,  0, 6,  3, Mp, "AMD Phenom II Dual-Core Mobile (Champlain DA-C3)" ),
    new FMSQ( 1,15,  0, 6,  3, MA, "AMD Athlon II Dual-Core Mobile (Champlain DA-C3)" ),
    new FMSQ( 1,15,  0, 6,  3, DA, "AMD Athlon II X2 (Regor DA-C3)" ),
    new FMSQ( 1,15,  0, 6,  3, dA, "AMD Athlon II (Sargas DA-C3)" ),
    new FMS ( 1,15,  0, 6,  3,     "AMD Athlon (unknown type) (Regor/Sargas/Champlain DA-C3)" ),
    new FMQ ( 1,15,  0, 6,     Ms, "AMD V-Series Mobile (Champlain)" ),
    new FMQ ( 1,15,  0, 6,     MS, "AMD Sempron Mobile (Sargas)" ),
    new FMQ ( 1,15,  0, 6,     DS, "AMD Sempron II X2 (Regor)" ),
    new FMQ ( 1,15,  0, 6,     dS, "AMD Sempron II (Sargas)" ),
    new FMQ ( 1,15,  0, 6,     MT, "AMD Turion II Dual-Core Mobile (Caspian / Champlain)" ),
    new FMQ ( 1,15,  0, 6,     Mp, "AMD Phenom II Dual-Core Mobile (Champlain)" ),
    new FMQ ( 1,15,  0, 6,     MA, "AMD Athlon II Dual-Core Mobile (Regor / Champlain)" ),
    new FMQ ( 1,15,  0, 6,     DA, "AMD Athlon II X2 (Regor)" ),
    new FMQ ( 1,15,  0, 6,     dA, "AMD Athlon II (Sargas)" ),
    new FM  ( 1,15,  0, 6,         "AMD Athlon (unknown type) (Regor/Sargas/Caspian/Champlain)" ),
    new FMSQ( 1,15,  0, 8,  0, SO, "AMD Six-Core Opteron (Istanbul HY-D0)" ),
    new FMSQ( 1,15,  0, 8,  0, sO, "AMD Opteron 4100 (Lisbon HY-D0)" ),
    new FMS ( 1,15,  0, 8,  0,     "AMD Opteron (unknown type) (Lisbon/Istanbul HY-D0)" ),
    new FMS ( 1,15,  0, 8,  1,     "AMD Opteron 4100 (Lisbon HY-D1)" ),
    new FMQ ( 1,15,  0, 8,     SO, "AMD Six-Core Opteron (Istanbul)" ),
    new FMQ ( 1,15,  0, 8,     sO, "AMD Opteron 4100 (Lisbon)" ),
    new FM  ( 1,15,  0, 8,         "AMD Opteron (unknown type) (Lisbon/Istanbul)" ),
    new FMS ( 1,15,  0, 9,  0,     "AMD Opteron 6100 (Magny-Cours HY-D0)" ), // sandpile.org
    new FMS ( 1,15,  0, 9,  1,     "AMD Opteron 6100 (Magny-Cours HY-D1)" ),
    new FM  ( 1,15,  0, 9,         "AMD Opteron 6100 (Magny-Cours)" ),
    new FMSQ( 1,15,  0,10,  0, Qp, "AMD Phenom II X4 (Zosma PH-E0)" ),
    new FMSQ( 1,15,  0,10,  0, Sp, "AMD Phenom II X6 (Thuban PH-E0)" ),
    new FMS ( 1,15,  0,10,  0,     "AMD Phenom II (unknown type) (Zosma/Thuban PH-E0)" ),
    new FMQ ( 1,15,  0,10,     Qp, "AMD Phenom II X4 (Zosma)" ),
    new FMQ ( 1,15,  0,10,     Sp, "AMD Phenom II X6 (Thuban)" ),
    new FM  ( 1,15,  0,10,         "AMD Phenom II (unknown type) (Zosma/Thuban)" ),
    new F   ( 1,15,                "AMD (unknown model)" ),
    new FMSQ( 2,15,  0, 3,  1, MU, "AMD Turion X2 Ultra Dual-Core Mobile (Griffin LG-B1)" ),
    new FMSQ( 2,15,  0, 3,  1, MT, "AMD Turion X2 Dual-Core Mobile (Lion LG-B1)" ),
    new FMSQ( 2,15,  0, 3,  1, DS, "AMD Sempron X2 Dual-Core (Sable LG-B1)" ),
    new FMSQ( 2,15,  0, 3,  1, dS, "AMD Sempron (Sable LG-B1)" ),
    new FMSQ( 2,15,  0, 3,  1, DA, "AMD Athlon X2 Dual-Core (Lion LG-B1)" ),
    new FMSQ( 2,15,  0, 3,  1, dA, "AMD Athlon (Lion LG-B1)" ),
    new FMS ( 2,15,  0, 3,  1,     "AMD Athlon (unknown type) (Lion/Sable LG-B1)" ),
    new FMQ ( 2,15,  0, 3,     MU, "AMD Turion X2 Ultra (Griffin)" ),
    new FMQ ( 2,15,  0, 3,     MT, "AMD Turion X2 (Lion)" ),
    new FMQ ( 2,15,  0, 3,     DS, "AMD Sempron X2 Dual-Core (Sable)" ),
    new FMQ ( 2,15,  0, 3,     dS, "AMD Sempron (Sable)" ),
    new FMQ ( 2,15,  0, 3,     DA, "AMD Athlon X2 Dual-Core (Lion)" ),
    new FMQ ( 2,15,  0, 3,     dA, "AMD Athlon (Lion)" ),
    new FM  ( 2,15,  0, 3,         "AMD Athlon (unknown type) (Lion/Sable)" ),
    new F   ( 2,15,                "AMD (unknown model)" ),
    new FMS ( 3,15,  0, 0,  0,     "AMD Athlon (unknown type) (Llano LN-A0)" ), // sandpile.org
    new FMS ( 3,15,  0, 0,  1,     "AMD Athlon (unknown type) (Llano LN-A1)" ), // sandpile.org
    new FMSQ( 3,15,  0, 1,  0, dS, "AMD Sempron Dual-Core (Llano LN-B0)" ),
    new FMSQ( 3,15,  0, 1,  0, dA, "AMD Athlon II Dual-Core (Llano LN-B0)" ),
    new FMSQ( 3,15,  0, 1,  0, Sa, "AMD A-Series (Llano LN-B0)" ),
    new FMSQ( 3,15,  0, 1,  0, Se, "AMD E2-Series (Llano LN-B0)" ),
    new FMS ( 3,15,  0, 1,  0,     "AMD Athlon (unknown type) (Llano LN-B0)" ),
    new FMQ ( 3,15,  0, 1,     dS, "AMD Sempron Dual-Core (Llano)" ),
    new FMQ ( 3,15,  0, 1,     dA, "AMD Athlon II Dual-Core (Llano)" ),
    new FMQ ( 3,15,  0, 1,     Sa, "AMD A-Series (Llano)" ),
    new FMQ ( 3,15,  0, 1,     Se, "AMD E2-Series (Llano)" ),
    new FM  ( 3,15,  0, 1,         "AMD Athlon (unknown type) (Llano)" ),
    new FMS ( 3,15,  0, 2,  0,     "AMD Athlon (unknown type) (Llano LN-B0)" ), // sandpile.org
    new F   ( 3,15,                "AMD (unknown model) (Llano)" ),
    new FMSQ( 5,15,  0, 1,  0, Sc, "AMD C-Series (Ontario ON-B0)" ),
    new FMSQ( 5,15,  0, 1,  0, Se, "AMD E-Series (Zacate ON-B0)" ),
    new FMSQ( 5,15,  0, 1,  0, Sg, "AMD G-Series (Ontario/Zacate ON-B0)" ),
    new FMSQ( 5,15,  0, 1,  0, Sz, "AMD Z-Series (Desna ON-B0)" ),
    new FMS ( 5,15,  0, 1,  0,     "AMD (unknown type) (Ontario/Zacate/Desna ON-B0)" ),
    new FM  ( 5,15,  0, 1,         "AMD (unknown type) (Ontario/Zacate/Desna)" ),
    new FMSQ( 5,15,  0, 2,  0, Sc, "AMD C-Series (Ontario ON-C0)" ),
    new FMSQ( 5,15,  0, 2,  0, Se, "AMD E-Series (Zacate ON-C0)" ),
    new FMSQ( 5,15,  0, 2,  0, Sg, "AMD G-Series (Ontario/Zacate ON-C0)" ),
    new FMSQ( 5,15,  0, 2,  0, Sz, "AMD Z-Series (Desna ON-C0)" ),
    new FMS ( 5,15,  0, 2,  0,     "AMD (unknown type) (Ontario/Zacate/Desna ON-C0)" ),
    new FM  ( 5,15,  0, 2,         "AMD (unknown type) (Ontario/Zacate/Desna)" ),
    new F   ( 5,15,                "AMD (unknown model)" ),
    new FMS ( 6,15,  0, 0,  0,     "AMD (unknown type) (Interlagos/Valencia/Zurich/Zambezi OR-A0)" ), // sandpile.org
    new FMS ( 6,15,  0, 0,  1,     "AMD (unknown type) (Interlagos/Valencia/Zurich/Zambezi OR-A1)" ), // sandpile.org
    new FMS ( 6,15,  0, 1,  0,     "AMD (unknown type) (Interlagos/Valencia/Zurich/Zambezi OR-B0)" ), // sandpile.org
    new FMS ( 6,15,  0, 1,  1,     "AMD (unknown type) (Interlagos/Valencia/Zurich/Zambezi OR-B1)" ), // sandpile.org
    new FMSQ( 6,15,  0, 1,  2, sO, "AMD Opteron 6200 (Interlagos OR-B2) / Opteron 4200 (Valencia OR-B2) / Opteron 3200 (Zurich OR-B2)" ),
    new FMSQ( 6,15,  0, 1,  2, df, "AMD FX-Series (Zambezi OR-B2)" ),
    new FMS ( 6,15,  0, 1,  2,     "AMD (unknown type) (Interlagos/Valencia/Zurich/Zambezi OR-B2)" ),
    new FMQ ( 6,15,  0, 1,     sO, "AMD Opteron 6200 (Interlagos) / Opteron 4200 (Valencia) / Opteron 3200 (Zurich)" ),
    new FMQ ( 6,15,  0, 1,     df, "AMD FX-Series (Zambezi)" ),
    new FM  ( 6,15,  0, 1,         "AMD (unknown type) (Interlagos/Valencia/Zurich/Zambezi)" ),
    new FMSQ( 6,15,  0, 2,  0, sO, "AMD Opteron 6300 (Abu Dhabi OR-C0) / Opteron 4300 (Seoul OR-C0) / Opteron 3300 (Delhi OR-C0)" ),
    new FMSQ( 6,15,  0, 2,  0, df, "AMD FX-Series (Vishera OR-C0)" ),
    new FMS ( 6,15,  0, 2,  0,     "AMD (unknown type) (Abu Dhabi/Seoul/Delhi/Vishera OR-C0)" ),
    new FMQ ( 6,15,  0, 2,     sO, "AMD Opteron 6300 (Abu Dhabi) / Opteron 4300 (Seoul) / Opteron 3300 (Delhi)" ),
    new FMQ ( 6,15,  0, 2,     df, "AMD FX-Series (Vishera)" ),
    new FM  ( 6,15,  0, 2,         "AMD (unknown type) (Abu Dhabi/Seoul/Delhi/Vishera)" ),
    new FMSQ( 6,15,  1, 0,  1, Sa, "AMD A-Series (Trinity TN-A1)" ),
    new FMSQ( 6,15,  1, 0,  1, Sr, "AMD R-Series (Trinity TN-A1)" ),
    new FMSQ( 6,15,  1, 0,  1, dA, "AMD Athlon Dual-Core / Athlon Quad-Core (Trinity TN-A1)" ),
    new FMSQ( 6,15,  1, 0,  1, dS, "AMD Sempron Dual-Core (Trinity TN-A1)" ),
    new FMSQ( 6,15,  1, 0,  1, dI, "AMD FirePro (Trinity TN-A1)" ),
    new FMS ( 6,15,  1, 0,  1,     "AMD (unknown type) (Trinity TN-A1)" ),
    new FMQ ( 6,15,  1, 0,     Sa, "AMD A-Series (Trinity)" ),
    new FMQ ( 6,15,  1, 0,     Sr, "AMD R-Series (Trinity)" ),
    new FMQ ( 6,15,  1, 0,     dA, "AMD Athlon Dual-Core / Athlon Quad-Core (Trinity)" ),
    new FMQ ( 6,15,  1, 0,     dS, "AMD Sempron Dual-Core (Trinity)" ),
    new FMQ ( 6,15,  1, 0,     dI, "AMD FirePro (Trinity)" ),
    new FM  ( 6,15,  1, 0,         "AMD (unknown type) (Trinity TN-A1)" ),
    new FMSQ( 6,15,  1, 3,  1, Sa, "AMD A-Series (Richland RL-A1)" ),
    new FMSQ( 6,15,  1, 3,  1, Sr, "AMD R-Series (Richland RL-A1)" ),
    new FMSQ( 6,15,  1, 3,  1, dA, "AMD Athlon Dual-Core / Athlon Quad-Core (Richland RL-A1)" ),
    new FMSQ( 6,15,  1, 3,  1, dS, "AMD Sempron Dual-Core (Richland RL-A1)" ),
    new FMSQ( 6,15,  1, 3,  1, dI, "AMD FirePro (Richland RL-A1)" ),
    new FMS ( 6,15,  1, 3,  1,     "AMD (unknown type) (Richland RL-A1)" ),
    new FMQ ( 6,15,  1, 3,     Sa, "AMD A-Series (Richland)" ),
    new FMQ ( 6,15,  1, 3,     Sr, "AMD R-Series (Richland)" ),
    new FMQ ( 6,15,  1, 3,     dA, "AMD Athlon Dual-Core / Athlon Quad-Core (Richland)" ),
    new FMQ ( 6,15,  1, 3,     dS, "AMD Sempron Dual-Core (Richland)" ),
    new FMQ ( 6,15,  1, 3,     dI, "AMD FirePro (Richland)" ),
    new FM  ( 6,15,  1, 3,         "AMD (unknown type) (Richland)" ),
    new FMS ( 6,15,  3, 0,  0,     "AMD (unknown type) (Kaveri KV-A0)" ),
    new FMSQ( 6,15,  3, 0,  1, Sa, "AMD Elite Performance A-Series (Kaveri KV-A1)" ),
    new FMSQ( 6,15,  3, 0,  1, Mr, "AMD Mobile R-Series (Kaveri KV-A1)" ),
    new FMSQ( 6,15,  3, 0,  1, sO, "AMD Opteron X1200 / X2200 (Kaveri KV-A1)" ),
    new FMSQ( 6,15,  3, 0,  1, Sr, "AMD R-Series (Bald Eagle KV-A1)"),  // undocumented, but instlatx64 example
    new FMS ( 6,15,  3, 0,  1,     "AMD (unknown type) (Kaveri KV-A1)" ),
    new FMQ ( 6,15,  3, 0,     Sa, "AMD Elite Performance A-Series (Kaveri)" ),
    new FMQ ( 6,15,  3, 0,     Mr, "AMD Mobile R-Series (Kaveri)" ),
    new FMQ ( 6,15,  3, 0,     sO, "AMD Opteron X1200 / X2200 (Kaveri)" ),
    new FM  ( 6,15,  3, 0,         "AMD (unknown type) (Kaveri)" ),
    new FMSQ( 6,15,  3, 8,  1, Sa, "AMD A-Series (Godavari A1)" ), // sandpile.org
    new FMS ( 6,15,  3, 8,  1,     "AMD (unknown type) (Godavari A1)" ), // sandpile.org
    new FMQ ( 6,15,  3, 8,     Sa, "AMD A-Series (Godavari)" ),
    new FM  ( 6,15,  3, 8,         "AMD (unknown type) (Godavari)" ),
    new FMS ( 6,15,  6, 0,  0,     "AMD (unknown type) (Carrizo/Toronto CZ-A0)" ), // sandpile.org
    new FMSQ( 6,15,  6, 0,  1, sO, "AMD Opteron (Toronto CZ-A1)" ), // undocumented, but instlatx64 sample
    new FMSQ( 6,15,  6, 0,  1, df, "AMD FX-Series (Carrizo CZ-A1)" ), // undocumented, but instlatx64 sample
    new FMS ( 6,15,  6, 0,  1,     "AMD (unknown type) (Carrizo/Toronto CZ-A1)" ), // undocumented, but instlatx64 sample
    new FMQ ( 6,15,  6, 0,     sO, "AMD Opteron (Toronto)" ), // undocumented, but instlatx64 sample
    new FMQ ( 6,15,  6, 0,     df, "AMD FX-Series (Carrizo)" ), // undocumented, but instlatx64 sample
    new FM  ( 6,15,  6, 0,         "AMD (unknown type) (Carrizo/Toronto)" ), // undocumented, but instlatx64 sample
    new FMSQ( 6,15,  6, 5,  1, Sa, "AMD A-Series (Carrizo/Bristol Ridge/Stoney Ridge CZ-A1/BR-A1)" ), // undocumented, but samples from Alexandros Couloumbis & instlatx64; sandpile.org stepping
    new FMSQ( 6,15,  6, 5,  1, Se, "AMD E-Series (Stoney Ridge CZ-A1/BR-A1)" ), // undocumented; sandpile.org stepping
    new FMSQ( 6,15,  6, 5,  1, Sg, "AMD G-Series (Brown Falcon/Prairie Falcon CZ-A1/BR-A1)" ), // undocumented; sandpile.org stepping
    new FMSQ( 6,15,  6, 5,  1, Sr, "AMD R-Series (Merlin Falcon CZ-A1/BR-A1)" ), // undocumented; sandpile.org stepping
    new FMS ( 6,15,  6, 5,  1,     "AMD (unknown type) (Carrizo/Bristol Ridge/Stoney Ridge/Toronto/Brown Falcon/Merlin Falcon/Prairie Falcon CZ-A1/BR-A1)" ), // sandpile.org
    new FMQ ( 6,15,  6, 5,     Sa, "AMD A-Series (Carrizo/Bristol Ridge/Stoney Ridge)" ), // undocumented, but samples from Alexandros Couloumbis & instlatx64
    new FMQ ( 6,15,  6, 5,     Se, "AMD E-Series (Stoney Ridge)" ), // undocumented
    new FMQ ( 6,15,  6, 5,     Sg, "AMD G-Series (Brown Falcon/Prairie Falcon)" ), // undocumented
    new FMQ ( 6,15,  6, 5,     Sr, "AMD R-Series (Merlin Falcon)" ), // undocumented
    new FM  ( 6,15,  6, 5,         "AMD (unknown type) (Carrizo/Bristol Ridge/Stoney Ridge/Toronto/Brown Falcon/Merlin Falcon/Prairie Falcon)" ), // undocumented, but sample from Alexandros Couloumbis
    new FMSQ( 6,15,  7, 0,  0, Sa, "AMD A-Series (Carrizo/Bristol Ridge/Stoney Ridge ST-A0)" ),
    new FMSQ( 6,15,  7, 0,  0, Se, "AMD E-Series (Stoney Ridge ST-A0)" ),
    new FMSQ( 6,15,  7, 0,  0, Sg, "AMD G-Series (Brown Falcon/Prairie Falcon ST-A0)" ),
    new FMSQ( 6,15,  7, 0,  0, Sr, "AMD R-Series (Merlin Falcon ST-A0)" ),
    new FMS ( 6,15,  7, 0,  0,     "AMD (unknown type) (Carrizo/Bristol Ridge/Stoney Ridge/Toronto/Brown Falcon/Merlin Falcon/Prairie Falcon ST-A0)" ),
    new FMQ ( 6,15,  7, 0,     Sa, "AMD A-Series (Carrizo/Bristol Ridge/Stoney Ridge)" ),
    new FMQ ( 6,15,  7, 0,     Se, "AMD E-Series (Stoney Ridge)" ),
    new FMQ ( 6,15,  7, 0,     Sg, "AMD G-Series (Brown Falcon/Prairie Falcon)" ),
    new FMQ ( 6,15,  7, 0,     Sr, "AMD R-Series (Merlin Falcon)" ),
    new FM  ( 6,15,  7, 0,         "AMD (unknown type) (Carrizo/Bristol Ridge/Stoney Ridge/Toronto/Brown Falcon/Merlin Falcon/Prairie Falcon)" ),
    new F   ( 6,15,                "AMD (unknown model)" ),
    new FMS ( 7,15,  0, 0,  0,     "AMD (unknown type) (Kabini/Temash/Kyoto KB-A0)" ), // sandpile.org
    new FMSQ( 7,15,  0, 0,  1, dA, "AMD Athlon (Kabini KB-A1)" ),
    new FMSQ( 7,15,  0, 0,  1, Sa, "AMD A-Series (Kabini/Temash KB-A1)" ),
    new FMSQ( 7,15,  0, 0,  1, Se, "AMD E-Series (Kabini KB-A1)" ),
    new FMSQ( 7,15,  0, 0,  1, Sg, "AMD G-Series (Kabini KB-A1)" ),
    new FMSQ( 7,15,  0, 0,  1, sO, "AMD Opteron X1100/X2100 Series (Kyoto KB-A1)" ),
    new FMS ( 7,15,  0, 0,  1,     "AMD (unknown type) (Kabini/Temash/Kyoto KB-A1)" ),
    new FMQ ( 7,15,  0, 0,     dA, "AMD Athlon (Kabini)" ),
    new FMQ ( 7,15,  0, 0,     Sa, "AMD A-Series (Kabini/Temash)" ),
    new FMQ ( 7,15,  0, 0,     Se, "AMD E-Series (Kabini)" ),
    new FMQ ( 7,15,  0, 0,     Sg, "AMD G-Series (Kabini)" ),
    new FMQ ( 7,15,  0, 0,     sO, "AMD Opteron X1100/X2100 Series (Kyoto)" ),
    new FM  ( 7,15,  0, 0,         "AMD (unknown type) (Kabini/Temash/Kyoto)" ),
    new FM  ( 7,15,  2, 6,         "AMD (unknown type) (Cato)" ), // undocumented; instlatx64 sample; engr sample?
    // sandpile.org mentions (7,15),(0,4) Jaguar-esque "BV" cores
    // (with stepping 1 = A1), but I have no idea of any such code name.
    // The AMD docs (53072) omit the CPUID entirely.  But if this sticks to the
    // recent AMD pattern, these must be (7,15),(3,0).
    new FMSQ( 7,15,  3, 0,  1, Sa, "AMD A-Series (Beema ML-A1)" ),
    new FMSQ( 7,15,  3, 0,  1, Se, "AMD E-Series (Beema ML-A1)" ),
    new FMSQ( 7,15,  3, 0,  1, Sg, "AMD G-Series (Steppe Eagle/Crowned Eagle ML-A1)" ), // undocumented; instlatx64 sample
    new FMSQ( 7,15,  3, 0,  1, Ta, "AMD A-Series Micro (Mullins ML-A1)" ),
    new FMSQ( 7,15,  3, 0,  1, Te, "AMD E-Series Micro (Mullins ML-A1)" ),
    new FMS ( 7,15,  3, 0,  1,     "AMD (unknown type) (Beema/Mullins ML-A1)" ),
    new FMQ ( 7,15,  3, 0,     Sa, "AMD A-Series (Beema)" ),
    new FMQ ( 7,15,  3, 0,     Se, "AMD E-Series (Beema)" ),
    new FMQ ( 7,15,  3, 0,     Sg, "AMD E-Series (Steppe Eagle/Crowned Eagle)" ),
    new FMQ ( 7,15,  3, 0,     Ta, "AMD A-Series Micro (Mullins)" ),
    new FMQ ( 7,15,  3, 0,     Te, "AMD E-Series Micro (Mullins)" ),
    new FM  ( 7,15,  3, 0,         "AMD (unknown type) (Beema/Mullins/Steppe Eagle/Crowned Eagle)" ),
    // sandpile.org mentions (7,15),(6,0) Puma-esque "NL" cores
    // (with stepping 1 = A1), but I have no idea of any such code name.
    // changed
    /*
    new F   ( 7,15,                "AMD (unknown model)" ),
    new FMS ( 8,15,  0, 0,  1,     "AMD (unknown type) (Summit Ridge/Naples ZP-A1)" ), // sandpile.org
    new FMSQ( 8,15,  0, 1,  0, EE, "AMD EPYC (Snowy Owl ZP-B0)" ),
//  new FMSQ( 8,15,  0, 1,  0, sE, "AMD EPYC (Naples ZP-B0)" ),
    new FMSQ( 8,15,  0, 1,  0, sE, "AMD EPYC (1st Gen) (Naples ZP-B0)"),             // changed
    new FMSQ( 8,15,  0, 1,  0, dH, "AMD Ryzen Threadripper 1000 (Whitehaven B0)"),   // added
//  new FMSQ( 8,15,  0, 1,  0, dR, "AMD Ryzen (Summit Ridge ZP-B0)" ),
    new FMSQ( 8,15,  0, 1,  0, dR, "AMD Ryzen 1000 (Summit Ridge ZP-B0)"),           // added
    new FMS ( 8,15,  0, 1,  0,     "AMD (unknown type) (Summit Ridge/Naples ZP-B0)" ),
    new FMSQ( 8,15,  0, 1,  1, EE, "AMD EPYC (Snowy Owl ZP-B1)" ),
    new FMSQ( 8,15,  0, 1,  1, sE, "AMD EPYC (Naples ZP-B1)" ),
    new FMSQ( 8,15,  0, 1,  1, dH, "AMD Ryzen Threadripper 1000 (Whitehaven B1)"),   // added
//  new FMSQ( 8,15,  0, 1,  1, dR, "AMD Ryzen (Summit Ridge ZP-B1)" ),
    new FMSQ( 8,15,  0, 1,  1, dR, "AMD Ryzen 1000 (Summit Ridge ZP-B1)"),           // changed
    new FMS ( 8,15,  0, 1,  1,     "AMD (unknown type) (Summit Ridge/Naples ZP-B1)" ),
    new FMSQ( 8,15,  0, 1,  2, EE, "AMD EPYC (Snowy Owl ZP-B2)" ),
    new FMSQ( 8,15,  0, 1,  2, sE, "AMD EPYC (Naples ZP-B2)" ),
    new FMSQ( 8,15,  0, 1,  2, dR, "AMD Ryzen (Summit Ridge ZP-B2)" ),
    new FMS ( 8,15,  0, 1,  2,     "AMD (unknown type) (Summit Ridge/Naples ZP-B2)" ),
    new FMQ ( 8,15,  0, 1,     EE, "AMD EPYC (Snowy Owl)" ),
    new FMQ ( 8,15,  0, 1,     sE, "AMD EPYC (Naples)" ),
    new FMQ ( 8,15,  0, 1,     dR, "AMD Ryzen (Summit Ridge)" ),
    new FM  ( 8,15,  0, 1,         "AMD (unknown type) (Summit Ridge/Naples)" ),
    new FMS ( 8,15,  0, 8,  2,     "AMD Ryzen (Pinnacle Ridge PiR-B2)" ),
    new FM  ( 8,15,  0, 8,         "AMD Ryzen (Pinnacle Ridge)" ),
    new FMS ( 8,15,  1, 0,  1,     "AMD Ryzen (unknown type) (Raven Ridge/Snowy Owl/Great Horned Owl/Banded Kestrel RV-A1)" ), // found only on en.wikichip.org & instlatx64 examples; sandpile.org
    new FMSQ( 8,15,  1, 1,  0, ER, "AMD Ryzen Embedded (Great Horned Owl/Banded Kestrel RV-B0)" ), // only instlatx64 example; stepping from usual pattern
    new FMSQ( 8,15,  1, 1,  0, AR, "AMD Ryzen (Raven Ridge RV-B0)" ), // found only on en.wikichip.org & instlatx64 examples; stepping from usual pattern
    new FMS ( 8,15,  1, 1,  0,     "AMD Ryzen (unknown type) (Raven Ridge/Snowy Owl/Great Horned Owl/Banded Kestrel RV-B0)" ), // found only on en.wikichip.org & instlatx64 examples; stepping from usual pattern
    new FMQ ( 8,15,  1, 1,     ER, "AMD Ryzen Embedded (Great Horned Owl/Banded Kestrel)" ), // only instlatx64 example
    new FMQ ( 8,15,  1, 1,     AR, "AMD Ryzen (Raven Ridge)" ), // found only on en.wikichip.org & instlatx64 examples
    new FM  ( 8,15,  1, 1,         "AMD Ryzen (unknown type) (Raven Ridge/Snowy Owl/Great Horned Owl/Banded Kestrel)" ), // found only on en.wikichip.org & instlatx64 examples
    new FMS ( 8,15,  1, 8,  1,     "AMD Ryzen (Picasso A1)" ),
    new FM  ( 8,15,  1, 8,         "AMD Ryzen (Picasso)" ),
    new FMS ( 8,15,  2, 0,  1,     "AMD Ryzen (Dali A1)" ),
    new FM  ( 8,15,  2, 0,         "AMD Ryzen (Dali)" ),
    new FMSQ( 8,15,  3, 1,  0, dR, "AMD Ryzen (Castle Peak B0)" ),
    new FMQ ( 8,15,  3, 1,     dR, "AMD Ryzen (Castle Peak)" ),
    new FMSQ( 8,15,  3, 1,  0, sE, "AMD EPYC (Rome B0)" ),
    new FMQ ( 8,15,  3, 1,     sE, "AMD EPYC (Rome)" ),
    new FMS ( 8,15,  3, 1,  0,     "AMD Ryzen (Castle Peak B0) / EPYC (Rome B0)" ),
    new FM  ( 8,15,  3, 1,         "AMD Ryzen (Castle Peak) / EPYC (Rome)" ),
    new FM  ( 8,15,  5, 0,         "AMD DG02SRTBP4MFA (Fenghuang 15FF)" ), // internal model, only instlatx64 example
    new FMS ( 8,15,  6, 0,  1,     "AMD Ryzen (Renoir A1)" ),
    new FM  ( 8,15,  6, 0,         "AMD Ryzen (Renoir)" ),
    new FMS ( 8,15,  7, 1,  0,     "AMD Ryzen (Matisse B0)" ), // undocumented, but samples from Steven Noonan
    new FM  ( 8,15,  7, 1,         "AMD Ryzen (Matisse)" ), // undocumented, but samples from Steven Noonan
    new F   ( 8,15,                "AMD (unknown model)" ),
    new FMS (10,15,  2, 1,  0,     "AMD Ryzen (Vermeer)"),
    new FMS (10,15,  5, 0,  0,     "AMD Ryzen (Cezanne)"),
    new FMS (10,15,  0, 1,  1,     "AMD EPYC (Milan)"),
    */
    new F   ( 7,15,                "AMD (unknown model)"),
    // Todd Allen comment:
   // In Zen-based CPUs, the model uses only the extended model and the
   // high-order bit of the model.  Meanwhile, the stepping name (revision) is
   // determinable mechanically from the low order 3 bits of the model,
   // converted to alphabetic characters, 0=A, 1=B, 2=C, etc.; and the stepping
   // number.  This is mentioned in each of individual the Processor Programming
   // Reference manuals under CPUID_FN00000001_EAX.
   // PPR 54945
/*  
    new FMS ( 8,15,  0, 0,  1,     "AMD (unknown type) (Summit Ridge/Naples ZP-A1)"), // sandpile.org
    new FMSQ( 8,15,  0, 1,  0, EE, "AMD EPYC (1st Gen) (Snowy Owl ZP-B0)"),
    new FMSQ( 8,15,  0, 1,  0, sE, "AMD EPYC (1st Gen) (Naples ZP-B0)"),
    new FMSQ( 8,15,  0, 1,  0, dH, "AMD Ryzen Threadripper 1000 (Whitehaven B0)"),
    new FMSQ( 8,15,  0, 1,  0, dR, "AMD Ryzen 1000 (Summit Ridge ZP-B0)"),
    new FMS ( 8,15,  0, 1,  0,     "AMD (unknown type) (Summit Ridge/Naples ZP-B0)"),
    new FMSQ( 8,15,  0, 1,  1, EE, "AMD EPYC (1st Gen) (Snowy Owl ZP-B1)"),
    new FMSQ( 8,15,  0, 1,  1, sE, "AMD EPYC (1st Gen) (Naples ZP-B1)"),
    new FMSQ( 8,15,  0, 1,  1, dH, "AMD Ryzen Threadripper 1000 (Whitehaven B1)"),
    new FMSQ( 8,15,  0, 1,  1, dR, "AMD Ryzen 1000 (Summit Ridge ZP-B1)"),
    new FMS ( 8,15,  0, 1,  1,     "AMD (unknown type) (Summit Ridge/Naples ZP-B1)"),
    new FMSQ( 8,15,  0, 1,  2, EE, "AMD EPYC (1st Gen) (Snowy Owl ZP-B2)"),
    new FMSQ( 8,15,  0, 1,  2, sE, "AMD EPYC (1st Gen) (Naples ZP-B2)"),
    new FMSQ( 8,15,  0, 1,  2, dH, "AMD Ryzen Threadripper 1000 (Whitehaven B2)"),
    new FMSQ( 8,15,  0, 1,  2, dR, "AMD Ryzen 1000 (Summit Ridge ZP-B2)"),
    new FMS ( 8,15,  0, 1,  2,     "AMD (unknown type) (Summit Ridge/Naples ZP-B2)"),
    new FMQ ( 8,15,  0, 1,     EE, "AMD EPYC (1st Gen) (Snowy Owl)"),
    new FMQ ( 8,15,  0, 1,     sE, "AMD EPYC (1st Gen) (Naples)"),
    new FMQ ( 8,15,  0, 1,     dH, "AMD Ryzen Threadripper 1000 (Whitehaven)"),
    new FMQ ( 8,15,  0, 1,     dR, "AMD Ryzen 1000 (Summit Ridge)"),
    new FM  ( 8,15,  0, 1,         "AMD (unknown type) (Summit Ridge/Naples)"),
    new FMSQ( 8,15,  0, 8,  2, dH, "AMD Ryzen Threadripper 2000 (Colfax B2)"),
    new FMSQ( 8,15,  0, 8,  2, dR, "AMD Ryzen 2000 (Pinnacle Ridge PiR-B2)"),
    new FMS ( 8,15,  0, 8,  2,     "AMD Ryzen (unknown type) (Pinnacle Ridge PiR-B2)"),
    new FMQ ( 8,15,  0, 8,     dH, "AMD Ryzen Threadripper 2000 (Colfax)"),
    new FMQ ( 8,15,  0, 8,     dR, "AMD Ryzen 2000 (Pinnacle Ridge)"),
    new FM  ( 8,15,  0, 8,         "AMD Ryzen (unknown type) (Pinnacle Ridge)"),
*/
    new FMmQ( 8,15,  0, 0,     EE, "AMD EPYC (1st Gen) (Snowy Owl ZP)"),               // TODO:  "AMD EPYC (1st Gen) (Snowy Owl ZP-%c%u)"
    new FMmQ( 8,15,  0, 0,     sE, "AMD EPYC (1st Gen) (Naples ZP)"),                  // TODO:  "AMD EPYC (1st Gen) (Naples ZP-%c%u)"
    new FMmQ( 8,15,  0, 0,     dH, "AMD Ryzen Threadripper 1000 (Whitehaven)"),        // TODO:  "AMD Ryzen Threadripper 1000 (Whitehaven %c%u)"
    new FMmQ( 8,15,  0, 0,     dR, "AMD Ryzen 1000 (Summit Ridge ZP)"),                // TODO:  "AMD Ryzen 1000 (Summit Ridge ZP-%c%u)"
    new FMm ( 8,15,  0, 0,         "AMD (unknown type) (Summit Ridge/Naples ZP)"),     // TODO:  "AMD (unknown type) (Summit Ridge/Naples ZP-%c%u)"
    new FMmQ( 8,15,  0, 8,     dH, "AMD Ryzen Threadripper 2000 (Colfax)"),            // TODO:  "AMD Ryzen Threadripper 2000 (Colfax %c%u)"
    new FMmQ( 8,15,  0, 8,     dR, "AMD Ryzen 2000 (Pinnacle Ridge PiR)"),             // TODO:  "AMD Ryzen 2000 (Pinnacle Ridge PiR-%c%u)"
    new FMm ( 8,15,  0, 8,         "AMD Ryzen (unknown type) (Pinnacle Ridge PiR)"),   // TODO:  "AMD Ryzen (unknown type) (Pinnacle Ridge PiR-%c%u)"
//
/*
    new FMS ( 8,15,  1, 0,  1,     "AMD Ryzen (unknown type) (Raven Ridge/Great Horned Owl/Banded Kestrel RV-A1)"), // found only on en.wikichip.org & instlatx64 examples; sandpile.org
    new FMSQ( 8,15,  1, 1,  0, dA, "AMD Athlon Pro 200 (Raven Ridge RV-A1)"),
    new FMSQ( 8,15,  1, 1,  0, VR, "AMD Ryzen Embedded V1000 (Great Horned Owl RV-B0)"), // only instlatx64 example; stepping from usual pattern
    new FMSQ( 8,15,  1, 1,  0, RR, "AMD Ryzen Embedded R1000 (Banded Kestrel RV-B0)"), // guess based on Great Horned Owl pattern
    new FMSQ( 8,15,  1, 1,  0, AR, "AMD Ryzen 2000 (Raven Ridge RV-B0)"), // found only on en.wikichip.org & instlatx64 examples; stepping from usual pattern
    new FMS ( 8,15,  1, 1,  0,     "AMD Ryzen (unknown type) (Raven Ridge/Great Horned Owl/Banded Kestrel RV-B0)"), // found only on en.wikichip.org & instlatx64 examples; stepping from usual pattern
    new FMQ ( 8,15,  1, 1,     dA, "AMD Athlon Pro 200 (Raven Ridge)"),
    new FMQ ( 8,15,  1, 1,     VR, "AMD Ryzen Embedded V1000 (Great Horned Owl)"), // only instlatx64 example
    new FMQ ( 8,15,  1, 1,     RR, "AMD Ryzen Embedded R1000 (Banded Kestrel)"), // guess based on Great Horned Owl pattern
    new FMQ ( 8,15,  1, 1,     AR, "AMD Ryzen 2000 (Raven Ridge)"), // found only on en.wikichip.org & instlatx64 examples
    new FM  ( 8,15,  1, 1,         "AMD Ryzen (unknown type) (Raven Ridge/Great Horned Owl/Banded Kestrel)"), // found only on en.wikichip.org & instlatx64 examples
    new FMSQ( 8,15,  1, 8,  1, dA, "AMD Athlon Pro 300 (Picasso A1)"), // only instlatx64 example
    new FMSQ( 8,15,  1, 8,  1, AR, "AMD Ryzen 3000 (Picasso A1)"),
    new FMS ( 8,15,  1, 8,  1,     "AMD (unknown type) (Picasso A1)"),
    new FMQ ( 8,15,  1, 8,     dA, "AMD Athlon Pro 300 (Picasso)"),
    new FMQ ( 8,15,  1, 8,     AR, "AMD Ryzen 3000 (Picasso)"),
    new FM  ( 8,15,  1, 8,         "AMD (unknown type) (Picasso)"),
    new FMSQ( 8,15,  2, 0,  1, dR, "AMD Ryzen 1000 (Dali A1)"),
    new FMS ( 8,15,  2, 0,  1,     "AMD (unknown type) (Dali A1)"),
    new FMQ ( 8,15,  2, 0,     dR, "AMD Ryzen 1000 (Dali)"),
    new FM  ( 8,15,  2, 0,         "AMD (unknown type) (Dali)"),
    new FMSQ( 8,15,  3, 1,  0, dR, "AMD Ryzen Threadripper 3000 (Castle Peak B0)"),
    new FMQ ( 8,15,  3, 1,     dR, "AMD Ryzen Threadripper 3000 (Castle Peak)"),
    new FMSQ( 8,15,  3, 1,  0, sE, "AMD EPYC (2nd Gen) (Rome B0)"),
    new FMQ ( 8,15,  3, 1,     sE, "AMD EPYC (2nd Gen) (Rome)"),
    new FMS ( 8,15,  3, 1,  0,     "AMD (unknown type) (Castle Peak/Rome B0)"),
    new FM  ( 8,15,  3, 1,         "AMD (unknown type) (Castle Peak/Rome)"),
    new FM  ( 8,15,  4, 7,         "AMD 4700S Desktop Kit (Oberon)"), // undocumented; instlatx64 sample; engr sample?
    new FM  ( 8,15,  5, 0,         "AMD DG02SRTBP4MFA (Fenghuang 15FF)"), // internal model, only instlatx64 example
    new FMSQ( 8,15,  6, 0,  1, ER, "AMD Ryzen Embedded V2000 (Grey Hawk A1)"), // found only on en.wikichip.org
    new FMSQ( 8,15,  6, 0,  1, dR, "AMD Ryzen 4000 (Renoir A1)"),
    new FMS ( 8,15,  6, 0,  1,     "AMD (unknown type) (Renoir/Grey Hawk A1)"),
    new FMQ ( 8,15,  6, 0,     ER, "AMD Ryzen Embedded V2000 (Grey Hawk)"), // found only on en.wikichip.org
    new FMQ ( 8,15,  6, 0,     dR, "AMD Ryzen 4000 (Renoir)"),
    new FM  ( 8,15,  6, 0,         "AMD (unknown type) (Renoir/Grey Hawk)"),
    new FMS ( 8,15,  6, 8,  1,     "AMD Ryzen 5000 (Lucienne A1)"), // undocumented, but instlatx64 samples
    new FM  ( 8,15,  6, 8,         "AMD Ryzen 5000 (Lucienne)"), // undocumented, but instlatx64 samples
    new FMS ( 8,15,  7, 1,  0,     "AMD Ryzen 3000 (Matisse B0)"), // undocumented, but samples from Steven Noonan
    new FM  ( 8,15,  7, 1,         "AMD Ryzen 3000 (Matisse)"), // undocumented, but samples from Steven Noonan
    new FMS ( 8,15,  9, 0,  0,     "AMD Ryzen (Van Gogh A0)"), // undocumented, but (engr?) sample via instlatx64 from @patrickschur_
    new FM  ( 8,15,  9, 0,         "AMD Ryzen (Van Gogh)"), // undocumented, but (engr?) sample via instlatx64 from @patrickschur_
    new FM  ( 8,15,  9, 8,         "AMD Ryzen (Mero)"), // undocumented, but (engr?) sample via instlatx64 from @zimogorets
    new FM  ( 8,15, 10, 0,         "AMD Ryzen (Mendocino)"), // undocumented, but (engr?) sample via instlatx64 from @ExecuFix
    new F   ( 8,15,                "AMD (unknown model)"),
*/
    // PPR 55449
    new FMmQ( 8,15,  1, 0,     dA, "AMD Athlon Pro 200 (Raven Ridge RV)"),  // TODO:  "AMD Athlon Pro 200 (Raven Ridge RV-%c%u)"
    new FMmQ( 8,15,  1, 0,     VR, "AMD Ryzen Embedded V1000 (Great Horned Owl RV)"), // only instlatx64 example   // TODO:  "AMD Ryzen Embedded V1000 (Great Horned Owl RV-%c%u)"
    new FMmQ( 8,15,  1, 0,     RR, "AMD Ryzen Embedded R1000 (Banded Kestrel RV)"), // guess based on Great Horned Owl pattern   // TODO:  "AMD Ryzen Embedded R1000 (Banded Kestrel RV-%c%u)"
    new FMmQ( 8,15,  1, 0,     AR, "AMD Ryzen 2000 (Raven Ridge RV)"), // found only on en.wikichip.org & instlatx64 examples   // TODO:  "AMD Ryzen 2000 (Raven Ridge RV-%c%u)"
    new FMm ( 8,15,  1, 0,         "AMD Ryzen (unknown type) (Raven Ridge/Great Horned Owl/Banded Kestrel RV)"), // found only on en.wikichip.org & instlatx64 examples   // TODO:  "AMD Ryzen (unknown type) (Raven Ridge/Great Horned Owl/Banded Kestrel RV-%c%u)"
    // PPR 55570, PPR 55449
    new FMmQ( 8,15,  1, 8,     dA, "AMD Athlon Pro 300 (Picasso)"),   // TODO:  "AMD Athlon Pro 300 (Picasso %c%u)"
    new FMmQ( 8,15,  1, 8,     AR, "AMD Ryzen 3000 (Picasso)"),       // TODO:  "AMD Ryzen 3000 (Picasso %c%u)"
    new FMm ( 8,15,  1, 8,         "AMD (unknown type) (Picasso)"),   // TODO:  "AMD (unknown type) (Picasso %c%u)"
    // PPR 55772
    new FMmQ( 8,15,  2, 0,     dR, "AMD Ryzen 1000 (Dali)"),        // TODO:  "AMD Ryzen 1000 (Dali %c%u)"
    new FMm ( 8,15,  2, 0,         "AMD (unknown type) (Dali)"),    // TODO:  "AMD (unknown type) (Dali %c%u)"
    // PPR 56323, PPR 55803
    new FMmQ( 8,15,  3, 0,     dR, "AMD Ryzen Threadripper 3000 (Castle Peak)"),   // TODO:  "AMD Ryzen Threadripper 3000 (Castle Peak %c%u)"
    new FMmQ( 8,15,  3, 0,     sE, "AMD EPYC (2nd Gen) (Rome)"),   // TODO:  "AMD EPYC (2nd Gen) (Rome %c%u)"
    new FMm ( 8,15,  3, 0,         "AMD (unknown type) (Castle Peak/Rome)"),   // TODO:  "AMD (unknown type) (Castle Peak/Rome %c%u)"
    new FM  ( 8,15,  4, 7,         "AMD 4700S Desktop Kit (Cardinal)"), // undocumented; instlatx64 sample; engr sample?
    new FM  ( 8,15,  5, 0,         "AMD DG02SRTBP4MFA (Fenghuang 15FF)"), // internal model, only instlatx64 example
    // PPR 55922
    new FMmQ( 8,15,  6, 0,     ER, "AMD Ryzen Embedded V2000 (Grey Hawk)"), // found only on en.wikichip.org   // TODO:  "AMD Ryzen Embedded V2000 (Grey Hawk %c%u)"
    new FMmQ( 8,15,  6, 0,     dR, "AMD Ryzen 4000 (Renoir)"),   // TODO:  "AMD Ryzen 4000 (Renoir %c%u)"
    new FMm ( 8,15,  6, 0,         "AMD (unknown type) (Renoir/Grey Hawk)"),   // TODO:  "AMD (unknown type) (Renoir/Grey Hawk %c%u)"
    new FMm ( 8,15,  6, 8,         "AMD Ryzen 5000 (Lucienne)"), // undocumented, but instlatx64 samples   // TODO:  "AMD Ryzen 5000 (Lucienne %c%u)"
    new FMm ( 8,15,  7, 0,         "AMD Ryzen 3000 (Matisse)"), // PPR 56176, but samples from Steven Noonan   // TODO:  "AMD Ryzen 3000 (Matisse %c%u)"
    new FM  ( 8,15,  8, 4,         "AMD 4800S Desktop Kit (ProjectX)"), // undocumented, but sample via instlatx64
    new FMm ( 8,15,  9, 0,         "AMD Ryzen (Van Gogh)"), // undocumented, but samples from instlatx64   // TODO:  "AMD Ryzen (Van Gogh %c%u)"
    new FMm ( 8,15,  9, 8,         "AMD Ryzen (Mero)"), // undocumented, but (engr?) sample via instlatx64 from @zimogorets   // TODO:  "AMD Ryzen (Mero %c%u)"
    new FMm ( 8,15, 10, 0,         "AMD Ryzen 7000 (Mendocino)"), // PPR 57243   // TODO:  "AMD Ryzen 7000 (Mendocino %c%u)"
    new F   ( 8,15,                "AMD (unknown model)"),
/*
    new FMS (10,15,  0, 1,  1,     "AMD EPYC (3rd Gen) (Milan B1)"),
    new FM  (10,15,  0, 1,         "AMD EPYC (3rd Gen) (Milan)"),
    new FMS (10,15,  0, 8,  0,     "AMD Ryzen Threadripper 5000 (Chagall A0)"), // undocumented, but (engr?) sample via instlatx64 from @ExecuFix
    new FM  (10,15,  0, 8,         "AMD Ryzen Threadripper 5000 (Chagall)"), // undocumented, but (engr?) sample via instlatx64 from @ExecuFix
    new FMS (10,15,  1, 0,  0,     "AMD EPYC (Genoa A0)"), // undocumented, but (engr?) sample via instlatx64 from @ExecuFix
    new FM  (10,15,  1, 0,         "AMD EPYC (Genoa)"), // undocumented, but (engr?) sample via instlatx64 from @ExecuFix
    new FMS (10,15,  1, 1,  1,     "AMD EPYC (Genoa)"),
    new FMS (10,15,  7, 4,  1,     "AMD Ryzen 7000 (Phoenix)"),
    new FMS (10,15,  1, 8,  0,     "AMD Ryzen (Storm Peak A0)"), // undocumented, but (engr?) sample from @patrickschur_
    new FM  (10,15,  1, 8,         "AMD Ryzen (Storm Peak)"), // undocumented, but (engr?) sample from @patrickschur_
    new FMS (10,15,  2, 1,  0,     "AMD Ryzen 5000 (Vermeer B0)"), // undocumented, but instlatx64 samples
    new FMS (10,15,  2, 1,  1,     "AMD Ryzen 5000 (Vermeer B1)"), // undocumented, but sample from @patrickschur_
    new FMS (10,15,  2, 1,  2,     "AMD Ryzen 5000 (Vermeer B2)"), // undocumented, but sample from @patrickschur_
    new FM  (10,15,  2, 1,         "AMD Ryzen 5000 (Vermeer)"),
    new FMS (10,15,  3, 0,  0,     "AMD Ryzen (Badami A0)"), // undocumented, but (engr?) sample via instlatx64 from @patrickschur_
    new FM  (10,15,  3, 0,         "AMD Ryzen (Badami)"), // undocumented, but (engr?) sample via instlatx64 from @patrickschur_
    new FMS (10,15,  4, 0,  0,     "AMD Ryzen (Rembrandt A0)"), // undocumented, but (engr?) sample via instlatx64 from @patrickschur_
    new FM  (10,15,  4, 0,         "AMD Ryzen (Rembrandt)"), // undocumented, but (engr?) sample via instlatx64 from @patrickschur_
    new FMS (10,15,  5, 0,  0,     "AMD Ryzen 5000 (Cezanne A0)"), // undocumented, but instlatx64 samples
    new FM  (10,15,  5, 0,         "AMD Ryzen 5000 (Cezanne)"), // undocumented, but instlatx64 samples
    new FMS (10,15,  5, 1,  1,     "AMD Ryzen 5000 (Cezanne B1)"),
    new FM  (10,15,  5, 1,         "AMD Ryzen 5000 (Cezanne)"),
    new FMS (10,15,  6, 0,  0,     "AMD Ryzen (Raphael A0)"), // undocumented, but (engr?) sample via instlatx64 from @patrickschur_
    new FM  (10,15,  6, 0,         "AMD Ryzen (Raphael)"), // undocumented, but (engr?) sample via instlatx64 from @patrickschur_
    new FM  (10,15,  6, 1,         "AMD Ryzen (Raphael)"),
    new FMS (10,15,  7, 0,  0,     "AMD Ryzen (Phoenix A0)"), // undocumented, but (engr?) sample via instlatx64 from @patrickschur_
    new FM  (10,15,  7, 0,         "AMD Ryzen (Phoenix)"), // undocumented, but (engr?) sample via instlatx64 from @patrickschur_
    new FMS (10,15, 10, 0,  0,     "AMD Ryzen (Bergamo A0)"), // undocumented, but (engr?) sample via instlatx64 from @ExecuFix
    new FM  (10,15, 10, 0,         "AMD Ryzen (Bergamo)"), // undocumented, but (engr?) sample via instlatx64 from @ExecuFix
    new F   (10,15,                "AMD (unknown model)") }; // undocumented, but samples from Steven Noonan
*/
    new FMm (10,15,  0, 0,         "AMD EPYC (3rd Gen) (Milan)"), // 56683   // TODO:  "AMD EPYC (3rd Gen) (Milan %c%u)"
    new FMm (10,15,  0, 8,         "AMD Ryzen Threadripper 5000 (Chagall)"), // undocumented, but sample from CCRT   // TODO:  "AMD Ryzen Threadripper 5000 (Chagall %c%u)"
    new FMm (10,15,  1, 0,         "AMD EPYC (4th Gen) (Genoa)"), // PPR 57095, PPR 55901   // TODO:  "AMD EPYC (4th Gen) (Genoa %c%u)"
    new FMm (10,15,  1, 8,         "AMD Ryzen Threadripper 7000 (Storm Peak)"), // undocumented, but samples from instlatx64   // TODO:  "AMD Ryzen Threadripper 7000 (Storm Peak %c%u)"
    new FMm (10,15,  2, 0,         "AMD Ryzen 5000 (Vermeer)"), // PPR 56214   // TODO:  "AMD Ryzen 5000 (Vermeer %c%u)"
    new FMm (10,15,  3, 0,         "AMD Ryzen (Badami)"), // undocumented, but (engr?) sample via instlatx64 from @patrickschur_   // TODO:  "AMD Ryzen (Badami %c%u)"
    new FMm (10,15,  4, 0,         "AMD Ryzen 6000/7000 (Rembrandt)"), // undocumented, but instlatx64 samples   // TODO:  "AMD Ryzen 6000/7000 (Rembrandt %c%u)"
    new FMm (10,15,  5, 0,         "AMD Ryzen 5000 (Cezanne/Barcelo)"), // PPR 56569   // TODO:  "AMD Ryzen 5000 (Cezanne/Barcelo %c%u)"
    new FMm (10,15,  6, 0,         "AMD Ryzen 7000 (Raphael)"), // PPR 56713   // TODO:  "AMD Ryzen 7000 (Raphael %c%u)")
    new FMm (10,15,  7, 0,         "AMD Ryzen 7000/8000 (Phoenix)"), // PPR 57019   // TODO:  "AMD Ryzen 7000/8000 (Phoenix %c%u)"
    new FM  (10,15,  7,12,         "AMD Ryzen (Hawk Point)"), // sample via instlatx64 from geekbench.com (special case only for model 12?)   // TODO:  "AMD Ryzen (Hawk Point %c%u)"
    new FMm (10,15,  7, 8,         "AMD Ryzen (Phoenix 2)"), // Coreboot*   // TODO:  "AMD Ryzen (Phoenix 2 %c%u)"
    new FMm (10,15,  8, 0,         "AMD Instinct MI300C"), // undocumented, but LKML: https://lkml.org/lkml/2023/7/21/835 from AMD's Yazen Ghannam
    new FMm (10,15,  9, 0,         "AMD Instinct MI300A"), // undocumented, but LKML: https://lkml.org/lkml/2023/7/21/835 from AMD's Yazen Ghannam
    new FMm (10,15, 10, 0,         "AMD EPYC (4th Gen) (Bergamo/Siena)"), // PPR 57228   // TODO:  "AMD EPYC (4th Gen) (Bergamo/Siena %c%u)"
    new F   (10,15,                "AMD (unknown model)"),
    new FMm (11,15,  0, 0,         "AMD EPYC (5th Gen) (Turin)"), // PPR 57238   // TODO:  "AMD EPYC (5th Gen) (Turin %c%u)"
    new FMm (11,15,  0, 8,         "AMD EPYC (5th Gen) (Turin)"), // PPR 57238   // TODO:  "AMD EPYC (5th Gen) (Turin %c%u)"
    new FMm (11,15,  1, 0,         "AMD EPYC (unknown type) (Sorano)"), // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian   // TODO:  "AMD EPYC (unknown type) (Sorano %c%u)"
    new FMm (11,15,  1, 8,         "AMD EPYC (unknown type) (Sorano)"), // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian   // TODO:  "AMD EPYC (unknown type) (Sorano %c%u)"
    // Are all these Strix Point's Ryzen AI 300 CPU's?
    // I suspect the latter ones are not.
    new FMm (11,15,  2, 0,         "AMD Ryzen AI 300 (Strix Point)"), // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian   // TODO:  "AMD Ryzen AI 300 (Strix Point %c%u)"
    new FMm (11,15,  2, 8,         "AMD Ryzen AI 300 (Strix Point)"), // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian   // TODO:  "AMD Ryzen AI 300 (Strix Point %c%u)"
    new FMm (11,15,  3, 0,         "AMD Ryzen (Strix Point)"), // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian   // TODO:  "AMD Ryzen (Strix Point %c%u)"
    new FMm (11,15,  3, 8,         "AMD Ryzen (Strix Point)"), // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian   // TODO:  "AMD Ryzen (Strix Point %c%u)"
    new FMm (11,15,  4, 0,         "AMD Ryzen 9000 (Granite Ridge)"), // undocumented, but LX*, sample from Chan Edison & engr sample via instlatx64 from einsteinathome.org (13142934)   // TODO:  "AMD Ryzen 9000 (Granite Ridge %c%u)"
    new FMm (11,15,  4, 8,         "AMD Ryzen 9000 (Granite Ridge)"), // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian   // TODO:  "AMD Ryzen 9000 (Granite Ridge %c%u)"
    new FMm (11,15,  5, 0,         "AMD EPYC (unknown type) (Venice)"), // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian   // TODO:  "AMD EPYC (unknown type) (Venice %c%u)"
    new FMm (11,15,  5, 8,         "AMD EPYC (unknown type) (Venice)"), // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian   // TODO:  "AMD EPYC (unknown type) (Venice %c%u)"
    new FMm (11,15,  6, 0,         "AMD Ryzen (Krackan Point)"), // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian   // TODO:  "AMD Ryzen (Krackan Point %c%u)"
    new FMm (11,15,  6, 8,         "AMD Ryzen (Krackan Point)"), // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian   // TODO:  "AMD Ryzen (Krackan Point %c%u)"
    new FMm (11,15,  7, 0,         "AMD Ryzen (Strix Halo)"), // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian   // TODO:  "AMD Ryzen (Strix Halo %c%u)"
    new FMm (11,15,  7, 8,         "AMD Ryzen (Strix Halo)"), // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian   // TODO:  "AMD Ryzen (Strix Halo %c%u)"
    new F   (11,15,                "AMD (unknown model)") };
//
    final CriteriaDescriptor[] AMD_X_DATA = {
    new FM  ( 0, 5,  0, 0,     "AMD SSA5 (PR75, PR90, PR100)" ),
    new FM  ( 0, 5,  0, 1,     "AMD 5k86 (PR120, PR133)" ),
    new FM  ( 0, 5,  0, 2,     "AMD 5k86 (PR166)" ),
    new FM  ( 0, 5,  0, 3,     "AMD 5k86 (PR200)" ),
    new FMQ ( 0, 5, 0, 10, MG, "AMD Geode" ),
    new F   ( 0, 5,            "AMD 5k86 (unknown model)" ),
    new FM  ( 0, 6,  0, 6,     "AMD K6, .30um" ),
    new FM  ( 0, 6,  0, 7,     "AMD K6 (Little Foot), .25um" ),
    new FMS ( 0, 6,  0, 8,  0, "AMD K6-2 (Chomper A)" ),
    new FMS ( 0, 6,  0, 8, 12, "AMD K6-2 (Chomper A)" ),
    new FM  ( 0, 6,  0, 8,     "AMD K6-2 (Chomper)" ),
    new FMS ( 0, 6,  0, 9,  1, "AMD K6-III (Sharptooth B)" ),
    new FM  ( 0, 6,  0, 9,     "AMD K6-III (Sharptooth)" ),
    new FM  ( 0, 6,  0,13,     "AMD K6-2+, K6-III+" ),
    new F   ( 0, 6,            "AMD K6 (unknown model)" ),
    new FM  ( 0, 7,  0, 1,     "AMD Athlon, .25um" ),
    new FM  ( 0, 7,  0, 2,     "AMD Athlon (K75 / Pluto / Orion), .18um" ),
    new FMS ( 0, 7,  0, 3,  0, "AMD Duron / mobile Duron (Spitfire A0)" ),
    new FMS ( 0, 7,  0, 3,  1, "AMD Duron / mobile Duron (Spitfire A2)" ),
    new FM  ( 0, 7,  0, 3,     "AMD Duron / mobile Duron (Spitfire)" ),
    new FMS ( 0, 7,  0, 4,  2, "AMD Athlon (Thunderbird A4-A7)" ),
    new FMS ( 0, 7,  0, 4,  4, "AMD Athlon (Thunderbird A9)" ),
    new FM  ( 0, 7,  0, 4,     "AMD Athlon (Thunderbird)" ),
    new FMS ( 0, 7,  0, 6,  0, "AMD Athlon / Athlon MP mobile Athlon 4 / mobile Duron (Palomino A0)" ),
    new FMS ( 0, 7,  0, 6,  1, "AMD Athlon / Athlon MP / Duron / mobile Athlon / mobile Duron (Palomino A2)" ),
    new FMS ( 0, 7,  0, 6,  2, "AMD Athlon MP / Athlon XP / Duron / Duron MP / mobile Athlon / mobile Duron (Palomino A5)" ),
    new FM  ( 0, 7,  0, 6,     "AMD Athlon / Athlon MP / Athlon XP / Duron / Duron MP / mobile Athlon / mobile Duron (Palomino)" ),
    new FMS ( 0, 7,  0, 7,  0, "AMD Duron / Duron MP / mobile Duron (Morgan A0)" ),
    new FMS ( 0, 7,  0, 7,  1, "AMD Duron / Duron MP / mobile Duron (Morgan A1)" ),
    new FM  ( 0, 7,  0, 7,     "AMD Duron / Duron MP / mobile Duron (Morgan)" ),
    new FMS ( 0, 7,  0, 8,  0, "AMD Athlon XP / Athlon MP / Sempron / Duron / Duron MP (Thoroughbred A0)" ),
    new FMS ( 0, 7,  0, 8,  1, "AMD Athlon XP / Athlon MP / Sempron / Duron / Duron MP (Thoroughbred B0)" ),
    new FM  ( 0, 7,  0, 8,     "AMD Athlon XP / Athlon MP / Sempron / Duron / Duron MP (Thoroughbred)" ),
    new FMS ( 0, 7,  0,10,  0, "AMD Athlon XP / Athlon MP / Sempron / mobile Athlon XP-M / mobile Athlon XP-M (LV) (Barton A2)" ),
    new FM  ( 0, 7,  0,10,     "AMD Athlon XP / Athlon MP / Sempron / mobile Athlon XP-M / mobile Athlon XP-M (LV) (Barton)" ),
    new F   ( 0, 7,            "AMD Athlon XP / Athlon MP / Sempron / Duron / Duron MP / mobile Athlon / mobile Athlon XP-M / mobile Athlon XP-M (LV) / mobile Duron (unknown model)") }; 

    String s1 = detectorHelper( stdTfms, bi, AMD_DATA );
    String s2 = detectorHelper( extTfms, bi, AMD_X_DATA );
    return new String[] { s1, s2 };
    }
}
