/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

This file contains Processors and Hypervisors data exported from
Todd Allen CPUID project. Some variables and functions names not compliant
with java naming conventions, this fields using original C/C++ naming.

Processor name detection by signature and additional flags,
for Intel processors. 
stdTfms = Standard Type, Family, Model, Stepping, 
          CPUID standard function 00000001h, register EAX
extTfms = Extended Type, Family, Model, Stepping, 
          CPUID extended function 80000001h, register EAX
bi      = Brand Index, CPUID function 00000001h, register EBX,
          only bits[7-0] must be selected by AND mask inside 
          CriteriaDescriptor.detector() method called by detectorHelper.

*/

package cpuidv3.servicecpudata;

import static cpuidv3.servicecpudata.DefineArithmetic.BIT_EXTRACT_LE;
import static cpuidv3.servicecpudata.VendorDetectPhysical.VENDOR_T.VENDOR_INTEL;

class IntelSynth extends Synth
{
/*
** Query macros are used in the synth tables to disambiguate multiple chips
** with the same family, model, and/or stepping.
*/
private boolean is_intel  = false;
private boolean is_mobile = false;

private boolean xD = false;     
private boolean mD = false;
private boolean cD = false;
private boolean dP = false;
private boolean dC = false;
private boolean pK = false;
private boolean xK = false;
private boolean sX = false;
private boolean MC = false;
private boolean MP = false;
private boolean dc = false;
private boolean Mc = false;
private boolean QW = false;
private boolean XE = false;
private boolean da = false;
private boolean Xc = false;
private boolean sQ = false;
private boolean Qc = false;
private boolean Me = false;
private boolean de = false;
private boolean Qe = false;
private boolean se = false;
private boolean MM = false;
private boolean sM = false;
private boolean sP = false;
private boolean sI = false;
private boolean dd = false;
private boolean s7 = false;
private boolean dG = false;
private boolean LU = false;
private boolean LG = false;
private boolean LY = false;
private boolean UX = false;
private boolean UP = false;
private boolean d1 = false;
private boolean UC = false;
private boolean YP = false;
private boolean Y8 = false;
private boolean sS = false;
private boolean YC = false;
private boolean dL = false;
private boolean DG = false;
private boolean iM = false;
private boolean dU = false;

private boolean Ha = false;
private boolean Hc = false;
private boolean Ia = false;
private boolean Ic = false;

private boolean is_vmx (int val_1_ecx )
    {
    return ( BIT_EXTRACT_LE( (val_1_ecx), 5, 6 ) ) != 0;
    }

IntelSynth( DatabaseStash stash )
    {
    super( stash );
    }

private boolean strsub( String st, String subst )
    {
    return st.contains( subst );
    }

@Override String[] detect( int stdTfms, int extTfms, int bi )
    {
    is_intel  = stash.vendor == VENDOR_INTEL;
    is_mobile = stash.br.mobile;
    
    /*
    ** Intel major queries:
    **
    ** d? = think "desktop"
    ** s? = think "server" (multiprocessor)
    ** M? = think "mobile"
    ** X? = think "Extreme Edition"
    ** L? = think "Line"
    **
    ** ?G = think generic CPU
    ** ?P = think Pentium
    ** ?C = think Celeron
    ** ?a = think Atom
    ** ?X = think Xeon
    ** ?M = think Xeon MP / Pentium M
    ** ?c = think Core
    ** ?d = think Pentium D
    ** ?S = think Scalable (Bronze/Silver/Gold/Platinum)
    */
    dG = is_intel && ! is_mobile && stash.br.generic;
    dP = ( is_intel && stash.br.pentium ) || stash.bri.desktop_pentium;
    dC = ( is_intel && !is_mobile && stash.br.celeron) ||
         stash.bri.desktop_celeron;
    da = is_intel && stash.br.atom;
    dd = is_intel && stash.br.pentium_d;
    // #define TODD_WAS_HERE
    // #ifdef TODD_WAS_HERE
    dc = is_intel && !is_mobile && stash.br.core;
    // #else
    // #define dc (is_intel && !is_mobile && (stash->br.core || stash->br.generic))
    // #endif
    sX = ( is_intel && stash.br.xeon ) || stash.bri.xeon;
    sM = ( is_intel && stash.br.xeon_mp ) || stash.bri.xeon_mp;
    sS = is_intel && stash.br.xeon && stash.br.scalable;
    MP = ( is_intel && is_mobile && stash.br.pentium ) ||
         stash.bri.mobile_pentium;
    MC = ( is_intel && is_mobile && stash.br.celeron) || 
         stash.bri.mobile_celeron;
    MM = ( is_intel && stash.br.pentium_m ) || 
         stash.bri.mobile_pentium_m;
    Mc = is_intel && is_mobile && stash.br.core;
    Xc = is_intel && stash.br.extreme;
    LU = is_intel && stash.br.u_line;
    LY = is_intel && stash.br.y_line;
    LG = is_intel && stash.br.g_line;
    UC = dC && stash.br.u_line;
    UP = dP && stash.br.u_line;
    YC = dC && stash.br.y_line;
    YP = dP && stash.br.y_line;
    d1 = is_intel && stash.br.cc150;

    /* 
    ** Intel special cases 
    */
    /* Pentium II Xeon (Deschutes), distinguished from Pentium II (Deschutes) */
    xD = stash.L2_4w_1Mor2M;
    /* Mobile Pentium II (Deschutes), distinguished from Pentium II (Deschutes) */
    mD = stash.L2_4w_256K;
    /* Intel Celeron (Deschutes), distinguished from  Pentium II (Deschutes) */
    // cD = !( stash.L2_4w_1Mor2M && !stash.L2_4w_512K && !stash.L2_4w_256K );
    cD = stash.L2_4w_1Mor2M && !stash.L2_4w_512K && !stash.L2_4w_256K;  // CORRECTED
    /* Pentium III Xeon (Katmai), distinguished from Pentium III (Katmai) */
    xK = stash.L2_4w_1Mor2M || stash.L2_8w_1Mor2M;
    /* Pentium II (Katmai), verified, so distinguished from fallback case */
    pK = ( stash.L2_4w_512K || stash.L2_8w_256K || stash.L2_8w_512K) &&
         !stash.L2_4w_1Mor2M && !stash.L2_8w_1Mor2M;
    /* Irwindale, distinguished from Nocona */
    sI = sX && stash.L2_2M;
    /* Potomac, distinguished from Cranford */
    sP = sM && stash.L3;
    /* Allendale, distinguished from Conroe */
    dL = dc && stash.L2_2M;
    /* Dual-Core Xeon Processor 5100 (Woodcrest B1) pre-production,
    distinguished from Core 2 Duo (Conroe B1) */
    QW = dG && stash.br.generic && ( stash.mp.cores == 4 || 
            ( stash.mp.cores == 2 && stash.mp.hyperthreads == 2) );
    /* Core Duo (Yonah), distinguished from Core Solo (Yonah) */
    DG = dG && stash.mp.cores == 2;
    /* Core 2 Quad, distinguished from Core 2 Duo */
    Qc = dc && stash.mp.cores == 4;
    /* Core 2 Extreme (Conroe B1), distinguished from Core 2 Duo (Conroe B1) */
    XE = dc && strsub( stash.brand, " E6800" );
    /* Quad-Core Xeon, distinguished from Xeon; and
    Xeon Processor 3300, distinguished from Xeon Processor 3100 */
    sQ = sX && stash.mp.cores == 4;
    /* Xeon Processor 7000, distinguished from Xeon */
    // #define IS_VMX(val_1_ecx)  (BIT_EXTRACT_LE((val_1_ecx), 5, 6))
    s7 = sX && is_vmx( stash.val_1_ecx );
    /* Wolfdale C0/E0, distinguished from Wolfdale M0/R0 */
    de = dc && stash.L2_6M;
    /* Penryn C0/E0, distinguished from Penryn M0/R0 */
    Me = Mc && stash.L2_6M;
    /* Yorkfield C1/E0, distinguished from Yorkfield M1/E0 */
    Qe = Qc && stash.L2_6M;
    /* Yorkfield E0, distinguished from Yorkfield R0 */
    se = sQ && stash.L2_6M;
    /* Amber Lake-Y, distinguished from Kaby Lake-Y */
    Y8 = LY && stash.br.i_8000;
    /* Comet Lake V1, distinguished from Whiskey Lake V0 */
    UX = LU && stash.br.i_10000;
    /* Intel Core Ultra */
    dU = is_intel && ! is_mobile && stash.br.core_ultra;
    /* Montage Jintide, undocumented, only instlatx64 example */
    iM = is_intel && stash.br.montage;
    
    Ha = stash.smallCore;
    Hc = stash.bigCore;
    Ia = stash.smallCore && stash.br.core;
    Ic = stash.bigCore && stash.br.core;
    
    final CriteriaDescriptor[] INTEL_DATA = {
/*
    new FM  (    0, 4,  0, 0,         "Intel i80486DX-25/33, .18um" ), // process from sandpile.org
    new FM  (    0, 4,  0, 1,         "Intel i80486DX-50, .18um" ),    // process from sandpile.org
    new FM  (    0, 4,  0, 2,         "Intel i80486SX, .18um" ),       // process from sandpile.org
    new FM  (    0, 4,  0, 3,         "Intel i80486DX/2" ),
*/    
    new FM  (    0, 4,  0, 0,         "Intel i80486DX-25/33" ),
    new FM  (    0, 4,  0, 1,         "Intel i80486DX-50" ),
    new FM  (    0, 4,  0, 2,         "Intel i80486SX" ),
    new FM  (    0, 4,  0, 3,         "Intel i80486DX/2" ),
    
    new FM  (    0, 4,  0, 4,         "Intel i80486SL, .8um" ),
    new FM  (    0, 4,  0, 5,         "Intel i80486SX/2, .8um" ),
    new FM  (    0, 4,  0, 7,         "Intel i80486DX/2-WB, .8um" ),
    new FM  (    0, 4,  0, 8,         "Intel i80486DX/4, .6um" ),
    new FM  (    0, 4,  0, 9,         "Intel i80486DX/4-WB, .6um" ),
    new F   (    0, 4,                "Intel i80486 (unknown model)" ),
    new FM  (    0, 5,  0, 0,         "Intel Pentium 60/66 A-step" ), // no docs
    // Intel docs (243326).
    new TFM (1,  0, 5,  0, 1,         "Intel Pentium 60/66 OverDrive for P5" ),
    new FMS (    0, 5,  0, 1,  3,     "Intel Pentium 60/66 (B1)" ),
    new FMS (    0, 5,  0, 1,  5,     "Intel Pentium 60/66 (C1)" ),
    new FMS (    0, 5,  0, 1,  7,     "Intel Pentium 60/66 (D1)" ),
    new FM  (    0, 5,  0, 1,         "Intel Pentium 60/66" ),
    // Intel docs (242480).
    new TFM (1,  0, 5,  0, 2,         "Intel Pentium 75 - 200 OverDrive for P54C" ),
    new FMS (    0, 5,  0, 2,  1,     "Intel Pentium P54C 75 - 200 (B1)" ),
    new FMS (    0, 5,  0, 2,  2,     "Intel Pentium P54C 75 - 200 (B3)" ),
    new FMS (    0, 5,  0, 2,  4,     "Intel Pentium P54C 75 - 200 (B5)" ),
    new FMS (    0, 5,  0, 2,  5,     "Intel Pentium P54C 75 - 200 (C2/mA1)" ),
    new FMS (    0, 5,  0, 2,  6,     "Intel Pentium P54C 75 - 200 (E0)" ),
    new FMS (    0, 5,  0, 2, 11,     "Intel Pentium P54C 75 - 200 (cB1)" ),
    new FMS (    0, 5,  0, 2, 12,     "Intel Pentium P54C 75 - 200 (cC0)" ),
    new FM  (    0, 5,  0, 2,         "Intel Pentium P54C 75 - 200" ),
    new TFM (1,  0, 5,  0, 3,         "Intel Pentium OverDrive for i486 (P24T)" ), // no docs
    // Intel docs (242480).
    new TFM (1,  0, 5,  0, 4,         "Intel Pentium OverDrive for P54C" ),
    new FMS (    0, 5,  0, 4,  3,     "Intel Pentium MMX P55C (B1)" ),
    new FMS (    0, 5,  0, 4,  4,     "Intel Pentium MMX P55C (A3)" ),
    new FM  (    0, 5,  0, 4,         "Intel Pentium MMX P55C" ),
    // Intel docs (242480).
    new FMS (    0, 5,  0, 7,  0,     "Intel Pentium MMX P54C 75 - 200 (A4)" ),
    new FM  (    0, 5,  0, 7,         "Intel Pentium MMX P54C 75 - 200" ),
    // Intel docs (242480).
    new FMS (    0, 5,  0, 8,  1,     "Intel Pentium MMX P55C (Tillamook A0)" ),
    new FMS (    0, 5,  0, 8,  2,     "Intel Pentium MMX P55C (Tillamook B2)" ),
    new FM  (    0, 5,  0, 8,         "Intel Pentium MMX P55C (Tillamook)" ),
    // Intel docs (329676) provides stepping names, but no numbers.
    // However, A0 is the only name.
    new FM  (    0, 5,  0, 9,         "Intel Quark X1000 / D1000 / D2000 / C1000 (Lakemont)" ),
    new F   (    0, 5,                "Intel Pentium (unknown model)" ),
    new FM  (    0, 6,  0, 0,         "Intel Pentium Pro A-step" ),
    // Intel docs (242689).
    new FMS (    0, 6,  0, 1,  1,     "Intel Pentium Pro (B0)" ),
    new FMS (    0, 6,  0, 1,  2,     "Intel Pentium Pro (C0)" ),
    new FMS (    0, 6,  0, 1,  6,     "Intel Pentium Pro (sA0)" ),
    new FMS (    0, 6,  0, 1,  7,     "Intel Pentium Pro (sA1), .35um" ),
    new FMS (    0, 6,  0, 1,  9,     "Intel Pentium Pro (sB1), .35um" ),
    new FM  (    0, 6,  0, 1,         "Intel Pentium Pro" ),
    // Intel docs (243337)
    new TFM (1,  0, 6,  0, 3,         "Intel Pentium II OverDrive" ),
    new FMS (    0, 6,  0, 3,  3,     "Intel Pentium II (Klamath C0)" ),
    new FMS (    0, 6,  0, 3,  4,     "Intel Pentium II (Klamath C1)" ),
    new FM  (    0, 6,  0, 3,         "Intel Pentium II (Klamath)" ),
    new FM  (    0, 6,  0, 4,         "Intel Pentium P55CT OverDrive (Deschutes)" ),
    // Intel docs (243337, 243748, 243776, 243887).
    new FMSQ(    0, 6,  0, 5,  0, xD, "Intel Pentium II Xeon (Deschutes A0)" ),
    new FMSQ(    0, 6,  0, 5,  0, mD, "Intel Mobile Pentium II (Deschutes A0)" ),
    new FMSQ(    0, 6,  0, 5,  0, cD, "Intel Celeron (Deschutes A0)" ),
    new FMS (    0, 6,  0, 5,  0,     "Intel Pentium II (unknown type) (Deschutes A0)" ),
    new FMSQ(    0, 6,  0, 5,  1, xD, "Intel Pentium II Xeon (Deschutes A1)" ),
    new FMSQ(    0, 6,  0, 5,  1, cD, "Intel Celeron (Deschutes A1)" ),
    new FMS (    0, 6,  0, 5,  1,     "Intel Pentium II (unknown type) (Deschutes A1)" ),
    new FMSQ(    0, 6,  0, 5,  2, xD, "Intel Pentium II Xeon (Deschutes B0)" ),
    new FMSQ(    0, 6,  0, 5,  2, mD, "Intel Mobile Pentium II (Deschutes B0)" ),
    new FMSQ(    0, 6,  0, 5,  2, cD, "Intel Celeron (Deschutes B0)" ),
    // new FMS (    0, 6,  0, 5,  2,     "Intel Pentium II (unknown type) (Deschutes B0)" ),
    new FMS (    0, 6,  0, 5,  2,     "Intel Pentium II (Deschutes B0)" ),  // CHANGED
    new FMSQ(    0, 6,  0, 5,  3, xD, "Intel Pentium II Xeon (Deschutes B1)" ),
    new FMSQ(    0, 6,  0, 5,  3, cD, "Intel Celeron (Deschutes B1)" ),
    new FMS (    0, 6,  0, 5,  3,     "Intel Pentium II (unknown type) (Deschutes B1)" ),
    new FMQ (    0, 6,  0, 5,     xD, "Intel Pentium II Xeon (Deschutes)" ),
    new FMQ (    0, 6,  0, 5,     mD, "Intel Mobile Pentium II (Deschutes)" ),
    new FMQ (    0, 6,  0, 5,     cD, "Intel Celeron (Deschutes)" ),
    new FM  (    0, 6,  0, 5,         "Intel Pentium II (unknown type) (Deschutes)" ),
    // Intel docs (243748, 243887, 244444).
    new FMSQ(    0, 6,  0, 6,  0, dP, "Intel Pentium II (Mendocino A0)" ),
    new FMSQ(    0, 6,  0, 6,  0, dC, "Intel Celeron (Mendocino A0)" ),
    new FMS (    0, 6,  0, 6,  0,     "Intel Pentium II (unknown type) (Mendocino A0)" ),
    new FMSQ(    0, 6,  0, 6,  5, dC, "Intel Celeron (Mendocino B0)" ),
    new FMSQ(    0, 6,  0, 6,  5, dP, "Intel Pentium II (Mendocino B0)" ),
    new FMS (    0, 6,  0, 6,  5,     "Intel Pentium II (unknown type) (Mendocino B0)" ),
    new FMS (    0, 6,  0, 6, 10,     "Intel Mobile Pentium II (Mendocino A0)" ),
    new FM  (    0, 6,  0, 6,         "Intel Pentium II (Mendocino)" ),
    // Intel docs (244453, 244460).
    new FMSQ(    0, 6,  0, 7,  2, pK, "Intel Pentium III (Katmai B0)" ),
    new FMSQ(    0, 6,  0, 7,  2, xK, "Intel Pentium III Xeon (Katmai B0)" ),
    new FMS (    0, 6,  0, 7,  2,     "Intel Pentium III (unknown type) (Katmai B0)" ),
    new FMSQ(    0, 6,  0, 7,  3, pK, "Intel Pentium III (Katmai C0)" ),
    new FMSQ(    0, 6,  0, 7,  3, xK, "Intel Pentium III Xeon (Katmai C0)" ),
    new FMS (    0, 6,  0, 7,  3,     "Intel Pentium III (unknown type) (Katmai C0)" ),
    new FMQ (    0, 6,  0, 7,     pK, "Intel Pentium III (Katmai)" ),
    new FMQ (    0, 6,  0, 7,     xK, "Intel Pentium III Xeon (Katmai)" ),
    new FM  (    0, 6,  0, 7,         "Intel Pentium III (unknown type) (Katmai)" ),
    // Intel docs (243748, 244453, 244460, 245306, 245421).
    new FMSQ(    0, 6,  0, 8,  1, sX, "Intel Pentium III Xeon (Coppermine A2)" ),
    new FMSQ(    0, 6,  0, 8,  1, MC, "Intel Mobile Celeron (Coppermine A2)" ),
    new FMSQ(    0, 6,  0, 8,  1, dC, "Intel Celeron (Coppermine A2)" ),
    new FMSQ(    0, 6,  0, 8,  1, MP, "Intel Mobile Pentium III (Coppermine A2)" ),
    new FMSQ(    0, 6,  0, 8,  1, dP, "Intel Pentium III (Coppermine A2)" ),
    new FMS (    0, 6,  0, 8,  1,     "Intel Pentium III (unknown type) (Coppermine A2)" ),
    new FMSQ(    0, 6,  0, 8,  3, sX, "Intel Pentium III Xeon (Coppermine B0)" ),
    new FMSQ(    0, 6,  0, 8,  3, MC, "Intel Mobile Celeron (Coppermine B0)" ),
    new FMSQ(    0, 6,  0, 8,  3, dC, "Intel Celeron (Coppermine B0)" ),
    new FMSQ(    0, 6,  0, 8,  3, MP, "Intel Mobile Pentium III (Coppermine B0)" ),
    new FMSQ(    0, 6,  0, 8,  3, dP, "Intel Pentium III (Coppermine B0)" ),
    new FMS (    0, 6,  0, 8,  3,     "Intel Pentium III (unknown type) (Coppermine B0)" ),
    new FMSQ(    0, 6,  0, 8,  6, sX, "Intel Pentium III Xeon (Coppermine C0)" ),
    new FMSQ(    0, 6,  0, 8,  6, MC, "Intel Mobile Celeron (Coppermine C0)" ),
    new FMSQ(    0, 6,  0, 8,  6, dC, "Intel Celeron (Coppermine C0)" ),
    new FMSQ(    0, 6,  0, 8,  6, MP, "Intel Mobile Pentium III (Coppermine C0)" ),
    new FMSQ(    0, 6,  0, 8,  6, dP, "Intel Pentium III (Coppermine C0)" ),
    new FMS (    0, 6,  0, 8,  6,     "Intel Pentium III (unknown type) (Coppermine C0)" ),
    new FMSQ(    0, 6,  0, 8, 10, sX, "Intel Pentium III Xeon (Coppermine D0)" ),
    new FMSQ(    0, 6,  0, 8, 10, MC, "Intel Mobile Celeron (Coppermine D0)" ),
    new FMSQ(    0, 6,  0, 8, 10, dC, "Intel Celeron (Coppermine D0)" ),
    new FMSQ(    0, 6,  0, 8, 10, MP, "Intel Mobile Pentium III (Coppermine D0)" ),
    new FMSQ(    0, 6,  0, 8, 10, dP, "Intel Pentium III (Coppermine D0)" ),
    new FMS (    0, 6,  0, 8, 10,     "Intel Pentium III (unknown type) (Coppermine D0)" ),
    new FMQ (    0, 6,  0, 8,     sX, "Intel Pentium III Xeon (Coppermine)" ),
    new FMQ (    0, 6,  0, 8,     MC, "Intel Mobile Celeron (Coppermine)" ),
    new FMQ (    0, 6,  0, 8,     dC, "Intel Celeron (Coppermine)" ),
    new FMQ (    0, 6,  0, 8,     MP, "Intel Mobile Pentium III (Coppermine)" ),
    new FMQ (    0, 6,  0, 8,     dP, "Intel Pentium III (Coppermine)" ),
    new FM  (    0, 6,  0, 8,         "Intel Pentium III (unknown type) (Coppermine)" ),
    // Intel docs (252665, 300303).
    new FMSQ(    0, 6,  0, 9,  5, dC, "Intel Celeron M (Banias B1)" ),
    new FMSQ(    0, 6,  0, 9,  5, dP, "Intel Pentium M (Banias B1)" ),
    new FMS (    0, 6,  0, 9,  5,     "Intel Pentium M (unknown type) (Banias B1)" ),
    new FMQ (    0, 6,  0, 9,     dC, "Intel Celeron M (Banias)" ),
    new FMQ (    0, 6,  0, 9,     dP, "Intel Pentium M (Banias)" ),
    new FM  (    0, 6,  0, 9,         "Intel Pentium M (unknown type) (Banias)" ),
    // Intel docs (244460).
    new FMS (    0, 6,  0,10,  0,     "Intel Pentium III Xeon (Cascades A0)" ),
    new FMS (    0, 6,  0,10,  1,     "Intel Pentium III Xeon (Cascades A1)" ),
    new FMS (    0, 6,  0,10,  4,     "Intel Pentium III Xeon (Cascades B0)" ),
    new FM  (    0, 6,  0,10,         "Intel Pentium III Xeon (Cascades)" ),
    // Intel docs (243748, 244453, 245306, 245421).
    new FMSQ(    0, 6,  0,11,  1, dC, "Intel Celeron (Tualatin A1)" ),
    new FMSQ(    0, 6,  0,11,  1, MC, "Intel Mobile Celeron (Tualatin A1)" ),
    new FMSQ(    0, 6,  0,11,  1, dP, "Intel Pentium III (Tualatin A1)" ),
    new FMS (    0, 6,  0,11,  1,     "Intel Pentium III (unknown type) (Tualatin A1)" ),
    new FMSQ(    0, 6,  0,11,  4, dC, "Intel Celeron (Tualatin B1)" ),
    new FMSQ(    0, 6,  0,11,  4, MC, "Intel Mobile Celeron (Tualatin B1)" ),
    new FMSQ(    0, 6,  0,11,  4, dP, "Intel Pentium III (Tualatin B1)" ),
    new FMS (    0, 6,  0,11,  4,     "Intel Pentium III (unknown type) (Tualatin B1)" ),
    new FMQ (    0, 6,  0,11,     dC, "Intel Celeron (Tualatin)" ),
    new FMQ (    0, 6,  0,11,     MC, "Intel Mobile Celeron (Tualatin)" ),
    new FMQ (    0, 6,  0,11,     dP, "Intel Pentium III (Tualatin)" ),
    new FM  (    0, 6,  0,11,         "Intel Pentium III (unknown type) (Tualatin)" ),
    // Intel docs (300303, 302209).
    new FMSQ(    0, 6,  0,13,  6, dC, "Intel Celeron M (Dothan B1), 90nm" ),
    new FMSQ(    0, 6,  0,13,  6, dP, "Intel Pentium M (Dothan B1), 90nm" ),
    new FMS (    0, 6,  0,13,  6,     "Intel Pentium M (unknown type) (Dothan B1), 90nm" ),
    new FMSQ(    0, 6,  0,13,  8, dC, "Intel Celeron M (Dothan C0), 90nm/65nm" ),
    new FMSQ(    0, 6,  0,13,  8, MP, "Intel Processor A100/A110 (Stealey C0) / Pentium M (Crofton C0), 90nm" ),
    new FMSQ(    0, 6,  0,13,  8, dP, "Intel Pentium M (Dothan C0), 90nm" ),
    new FMS (    0, 6,  0,13,  8,     "Intel Pentium M (unknown type) (Dothan/Stealey/Crofton C0), 90nm/65nm" ),
    new FMQ (    0, 6,  0,13,     dC, "Intel Celeron M (Dothan)" ),
    new FMQ (    0, 6,  0,13,     MP, "Intel Processor A100/A110 (Stealey)" ),
    new FMQ (    0, 6,  0,13,     dP, "Intel Pentium M (Dothan)" ),
    new FM  (    0, 6,  0,13,         "Intel Pentium M (unknown type) (Dothan/Crofton)" ),
    // Intel docs (300303, 309222, 311392, 316515).
    new FMSQ(    0, 6,  0,14,  8, sX, "Intel Xeon Processor LV (Sossaman C0)" ),
    new FMSQ(    0, 6,  0,14,  8, dC, "Intel Celeron (Yonah C0)" ),
    new FMSQ(    0, 6,  0,14,  8, DG, "Intel Core Duo (Yonah C0)" ),
    new FMSQ(    0, 6,  0,14,  8, dG, "Intel Core Solo (Yonah C0)" ),
    new FMS (    0, 6,  0,14,  8,     "Intel Core (unknown type) (Yonah/Sossaman C0)" ),
    new FMSQ(    0, 6,  0,14, 12, sX, "Intel Xeon Processor LV (Sossaman D0)" ),
    new FMSQ(    0, 6,  0,14, 12, dC, "Intel Celeron M (Yonah D0)" ),
    new FMSQ(    0, 6,  0,14, 12, MP, "Intel Pentium Dual-Core Mobile T2000 (Yonah D0)" ),
    new FMSQ(    0, 6,  0,14, 12, DG, "Intel Core Duo (Yonah D0)" ),
    new FMSQ(    0, 6,  0,14, 12, dG, "Intel Core Solo (Yonah D0)" ),
    new FMS (    0, 6,  0,14, 12,     "Intel Core (unknown type) (Yonah/Sossaman D0)" ),
    new FMS (    0, 6,  0,14, 13,     "Intel Pentium Dual-Core Mobile T2000 (Yonah M0)" ),
    new FMQ (    0, 6,  0,14,     sX, "Intel Xeon Processor LV (Sossaman)" ),
    new FMQ (    0, 6,  0,14,     dC, "Intel Celeron (Yonah)" ),
    new FMQ (    0, 6,  0,14,     MP, "Intel Pentium Dual-Core Mobile (Yonah)" ),
    new FMQ (    0, 6,  0,14,     DG, "Intel Core Duo (Yonah)" ),
    new FMQ (    0, 6,  0,14,     dG, "Intel Core Solo (Yonah)" ),
    new FM  (    0, 6,  0,14,         "Intel Core (unknown type) (Yonah/Sossaman)" ),
    // Intel docs (313279, 313356, 314079, 314916, 315338, 315593, 316134,
    // 316515, 316982, 317667, 318081, 318925, 319735).
    new FMSQ(    0, 6,  0,15,  2, sX, "Intel Dual-Core Xeon Processor 3000 (Conroe L2)" ),
    new FMSQ(    0, 6,  0,15,  2, Mc, "Intel Core Duo Mobile (Merom L2)" ),
    new FMSQ(    0, 6,  0,15,  2, dc, "Intel Core Duo (Conroe L2)" ),
    new FMSQ(    0, 6,  0,15,  2, dP, "Intel Pentium Dual-Core Desktop Processor E2000 (Allendale L2)" ),
    new FMS (    0, 6,  0,15,  2,     "Intel Core (unknown type) (Conroe/Merom/Allendale L2)" ),
    new FMS (    0, 6,  0,15,  4,     "Intel Core 2 Duo (Conroe B0) / Xeon Processor 5100 (Woodcrest B0) (pre-production)" ),
    new FMSQ(    0, 6,  0,15,  5, QW, "Intel Dual-Core Xeon Processor 5100 (Woodcrest B1) (pre-production)" ),
    new FMSQ(    0, 6,  0,15,  5, XE, "Intel Core 2 Extreme Processor (Conroe B1)" ),
    new FMSQ(    0, 6,  0,15,  5, dL, "Intel Core 2 Duo (Allendale B1)" ),
    new FMSQ(    0, 6,  0,15,  5, dc, "Intel Core 2 Duo (Conroe B1)" ),
    new FMS (    0, 6,  0,15,  5,     "Intel Core 2 (unknown type) (Conroe/Allendale B1)" ),
    new FMSQ(    0, 6,  0,15,  6, Xc, "Intel Core 2 Extreme Processor (Conroe B2)" ),
    new FMSQ(    0, 6,  0,15,  6, Mc, "Intel Core 2 Duo Mobile (Merom B2)" ),
    new FMSQ(    0, 6,  0,15,  6, dL, "Intel Core 2 Duo (Allendale B2)" ),
    new FMSQ(    0, 6,  0,15,  6, dc, "Intel Core 2 Duo (Conroe B2)" ),
    new FMSQ(    0, 6,  0,15,  6, dC, "Intel Celeron M (Conroe B2)" ),
    new FMSQ(    0, 6,  0,15,  6, sX, "Intel Dual-Core Xeon Processor 3000 (Conroe B2) / Dual-Core Xeon Processor 5100 (Woodcrest B2)" ),
    new FMS (    0, 6,  0,15,  6,     "Intel Core 2 (unknown type) (Conroe/Allendale/Woodcrest B2)" ),
    new FMSQ(    0, 6,  0,15,  7, sX, "Intel Quad-Core Xeon Processor 3200 (Kentsfield B3) / Quad-Core Xeon Processor 5300 (Clovertown B3)" ),
    new FMSQ(    0, 6,  0,15,  7, Xc, "Intel Core 2 Extreme Quad-Core Processor QX6xx0 (Kentsfield B3)" ),
    new FMS (    0, 6,  0,15,  7,     "Intel Core 2 (unknown type) (Kentsfield/Clovertown B3)" ),
    new FMSQ(    0, 6,  0,15, 10, Mc, "Intel Core 2 Duo Mobile (Merom E1)" ),
    new FMSQ(    0, 6,  0,15, 10, dC, "Intel Celeron Processor 500 (Merom E1)" ),
    new FMS (    0, 6,  0,15, 10,     "Intel Core 2 (unknown type) (Merom E1)" ),
    new FMSQ(    0, 6,  0,15, 11, sQ, "Intel Quad-Core Xeon Processor 5300 (Clovertown G0)" ),
    new FMSQ(    0, 6,  0,15, 11, sX, "Intel Xeon Processor 3000 (Conroe G0) / Xeon Processor 3200 (Kentsfield G0) / Xeon Processor 7200/7300 (Tigerton G0)" ),
    new FMSQ(    0, 6,  0,15, 11, Xc, "Intel Core 2 Extreme Quad-Core Processor QX6xx0 (Kentsfield G0)" ),
    new FMSQ(    0, 6,  0,15, 11, Mc, "Intel Core 2 Duo Mobile (Merom G2)" ),
    new FMSQ(    0, 6,  0,15, 11, Qc, "Intel Core 2 Quad (Conroe G0)" ),
    new FMSQ(    0, 6,  0,15, 11, dc, "Intel Core 2 Duo (Conroe G0)" ),
    new FMS (    0, 6,  0,15, 11,     "Intel Core 2 (unknown type) (Merom/Conroe/Kentsfield/Clovertown/Tigerton G0)" ),
    new FMSQ(    0, 6,  0,15, 13, Mc, "Intel Core 2 Duo Mobile (Merom M1) / Celeron Processor 500 (Merom E1)" ),
    new FMSQ(    0, 6,  0,15, 13, Qc, "Intel Core 2 Quad (Conroe M0)" ),
    new FMSQ(    0, 6,  0,15, 13, dc, "Intel Core 2 Duo (Conroe M0)" ),
    new FMSQ(    0, 6,  0,15, 13, dP, "Intel Pentium Dual-Core Desktop Processor E2000 (Allendale M0)" ),
    new FMSQ(    0, 6,  0,15, 13, dC, "Intel Celeron Dual-Core E1000 (Allendale M0) / Celeron Dual-Core T1000 (Merom M0)" ),
    new FMS (    0, 6,  0,15, 13,     "Intel Core 2 (unknown type) (Merom/Conroe/Allendale M0 / Merom E1)" ),
    new FMQ (    0, 6,  0,15,     sQ, "Intel Quad-Core Xeon (Woodcrest)" ),
    new FMQ (    0, 6,  0,15,     sX, "Intel Dual-Core Xeon (Conroe / Woodcrest) / Quad-Core Xeon (Kentsfield / Clovertown) / Xeon (Tigerton G0)" ),
    new FMQ (    0, 6,  0,15,     Xc, "Intel Core 2 Extreme Processor (Conroe) / Core 2 Extreme Quad-Core (Kentsfield)" ),
    new FMQ (    0, 6,  0,15,     Mc, "Intel Core Duo Mobile / Core 2 Duo Mobile (Merom) / Celeron (Merom)" ),
    new FMQ (    0, 6,  0,15,     Qc, "Intel Core 2 Quad (Conroe)" ),
    new FMQ (    0, 6,  0,15,     dc, "Intel Core Duo / Core 2 Duo (Conroe)" ),
    new FMQ (    0, 6,  0,15,     dP, "Intel Pentium Dual-Core (Allendale)" ),
    new FMQ (    0, 6,  0,15,     dC, "Intel Celeron M (Conroe) / Celeron (Merom) / Celeron Dual-Core (Allendale)" ),
    new FM  (    0, 6,  0,15,         "Intel Core 2 (unknown type) (Merom/Conroe/Allendale/Kentsfield/Allendale/Clovertown/Woodcrest/Tigerton)" ),
    // Intel docs (320257).
    new FMS (    0, 6,  1, 5,  0,     "Intel EP80579 (Tolapai B0)" ),
    // Intel docs (314079, 316964, 317667, 318547).
    new FMSQ(    0, 6,  1, 6,  1, MC, "Intel Celeron Processor 200/400/500 (Conroe-L/Merom-L A1)" ),
    new FMSQ(    0, 6,  1, 6,  1, dC, "Intel Celeron M (Merom-L A1)" ),
    new FMSQ(    0, 6,  1, 6,  1, Mc, "Intel Core 2 Duo Mobile (Merom A1)" ),
    new FMS (    0, 6,  1, 6,  1,     "Intel Core 2 (unknown type) (Merom/Conroe A1)" ),
    new FMQ (    0, 6,  1, 6,     MC, "Intel Celeron Processor 200/400/500 (Conroe-L/Merom-L)" ),
    new FMQ (    0, 6,  1, 6,     dC, "Intel Celeron M (Merom-L)" ),
    new FMQ (    0, 6,  1, 6,     Mc, "Intel Core 2 Duo Mobile (Merom)" ),
    new FM  (    0, 6,  1, 6,         "Intel Core 2 (unknown type) (Merom/Conroe)" ),
    // Intel docs (318585, 318586, 318727, 318733, 318915, 319006, 319007,
    // 319129, 320121, 320468, 320469, 322568).
    new FMSQ(    0, 6,  1, 7,  6, sQ, "Intel Xeon Processor 3300 (Yorkfield C0) / Xeon Processor 5200 (Wolfdale C0) / Xeon Processor 5400 (Harpertown C0)" ),
    new FMSQ(    0, 6,  1, 7,  6, sX, "Intel Xeon Processor 3100 (Wolfdale C0) / Xeon Processor 5200 (Wolfdale C0) / Xeon Processor 5400 (Harpertown C0)" ),
    new FMSQ(    0, 6,  1, 7,  6, Xc, "Intel Core 2 Extreme QX9000 (Yorkfield C0)" ),
    new FMSQ(    0, 6,  1, 7,  6, Me, "Intel Mobile Core 2 Duo (Penryn C0)" ),
    new FMSQ(    0, 6,  1, 7,  6, Mc, "Intel Mobile Core 2 Duo (Penryn M0)" ),
    new FMSQ(    0, 6,  1, 7,  6, de, "Intel Core 2 Duo (Wolfdale C0)" ),
    new FMSQ(    0, 6,  1, 7,  6, dc, "Intel Core 2 Duo (Wolfdale M0)" ),
    new FMSQ(    0, 6,  1, 7,  6, dP, "Intel Pentium Dual-Core Processor E5000 (Wolfdale M0)" ),
    new FMS (    0, 6,  1, 7,  6,     "Intel Core 2 (unknown type) (Penryn/Wolfdale/Yorkfield/Harpertown C0/M0)" ),
    new FMSQ(    0, 6,  1, 7,  7, sQ, "Intel Xeon Processor 3300 (Yorkfield C1)" ),
    new FMSQ(    0, 6,  1, 7,  7, Xc, "Intel Core 2 Extreme QX9000 (Yorkfield C1)" ),
    new FMSQ(    0, 6,  1, 7,  7, Qe, "Intel Core 2 Quad-Core Q9000 (Yorkfield C1)" ),
    new FMSQ(    0, 6,  1, 7,  7, Qc, "Intel Core 2 Quad-Core Q9000 (Yorkfield M1)" ),
    new FMS (    0, 6,  1, 7,  7,     "Intel Core 2 (unknown type) (Penryn/Wolfdale/Yorkfield/Harpertown C1/M1)" ),
    new FMSQ(    0, 6,  1, 7, 10, Me, "Intel Mobile Core 2 (Penryn E0)" ),
    new FMSQ(    0, 6,  1, 7, 10, Mc, "Intel Mobile Core 2 (Penryn R0)" ),
    new FMSQ(    0, 6,  1, 7, 10, Qe, "Intel Core 2 Quad-Core Q9000 (Yorkfield E0)" ),
    new FMSQ(    0, 6,  1, 7, 10, Qc, "Intel Core 2 Quad-Core Q9000 (Yorkfield R0)" ),
    new FMSQ(    0, 6,  1, 7, 10, de, "Intel Core 2 Duo (Wolfdale E0)" ),
    new FMSQ(    0, 6,  1, 7, 10, dc, "Intel Core 2 Duo (Wolfdale R0)" ),
    new FMSQ(    0, 6,  1, 7, 10, dP, "Intel Pentium Dual-Core Processor E5000/E6000 / Pentium T4000 (Wolfdale R0)" ),
    new FMSQ(    0, 6,  1, 7, 10, dC, "Intel Celeron E3000 / T3000 / 900 / SU2300 (Wolfdale R0)" ), // T3000 & 900 names from MRG* 2018-03-06
    new FMSQ(    0, 6,  1, 7, 10, MC, "Intel Celeron M ULV 700 (Penryn R0)" ),
    new FMSQ(    0, 6,  1, 7, 10, se, "Intel Xeon Processor 3300 (Yorkfield E0)" ),
    new FMSQ(    0, 6,  1, 7, 10, sQ, "Intel Xeon Processor 3300 (Yorkfield R0)" ),
    new FMSQ(    0, 6,  1, 7, 10, sX, "Intel Xeon Processor 3100 (Wolfdale E0) / Xeon Processor 3300 (Yorkfield R0) / Xeon Processor 5200 (Wolfdale E0) / Xeon Processor 5400 (Harpertown E0)" ),
    new FMS (    0, 6,  1, 7, 10,     "Intel Core 2 (unknown type) (Penryn/Wolfdale/Yorkfield/Harpertown E0/R0)" ),
    new FMQ (    0, 6,  1, 7,     se, "Intel Xeon (Wolfdale / Yorkfield / Harpertown)" ),
    new FMQ (    0, 6,  1, 7,     sQ, "Intel Xeon (Wolfdale / Yorkfield / Harpertown)" ),
    new FMQ (    0, 6,  1, 7,     sX, "Intel Xeon (Wolfdale / Yorkfield / Harpertown)" ),
    new FMQ (    0, 6,  1, 7,     Mc, "Intel Mobile Core 2 (Penryn)" ),
    new FMQ (    0, 6,  1, 7,     Xc, "Intel Core 2 Extreme (Yorkfield)" ),
    new FMQ (    0, 6,  1, 7,     Qc, "Intel Core 2 Quad-Core (Yorkfield)" ),
    new FMQ (    0, 6,  1, 7,     dc, "Intel Core 2 Duo (Wolfdale)" ),
    new FMQ (    0, 6,  1, 7,     dC, "Intel Celeron (Wolfdale)" ),
    new FMQ (    0, 6,  1, 7,     MC, "Intel Celeron M ULV (Penryn)" ),
    new FMQ (    0, 6,  1, 7,     dP, "Intel Pentium (Wolfdale)" ),
    new FM  (    0, 6,  1, 7,         "Intel Core 2 (unknown type) (Penryn/Wolfdale/Yorkfield/Harpertown)" ),
    // Intel docs (320836, 321324, 321333).
    new FMS (    0, 6,  1,10,  4,     "Intel Core i7-900 (Bloomfield C0)" ),
    new FMSQ(    0, 6,  1,10,  5, dc, "Intel Core i7-900 (Bloomfield D0)" ),
    new FMSQ(    0, 6,  1,10,  5, sX, "Intel Xeon Processor 3500 (Bloomfield D0) / Xeon Processor 5500 (Gainestown D0)" ),
    new FMS (    0, 6,  1,10,  5,     "Intel Core (unknown type) (Bloomfield/Gainestown D0)" ),
    new FMQ (    0, 6,  1,10,     dc, "Intel Core (Bloomfield)" ),
    new FMQ (    0, 6,  1,10,     sX, "Intel Xeon (Bloomfield / Gainestown)" ),
    new FM  (    0, 6,  1,10,         "Intel Core (unknown type) (Bloomfield / Gainestown)" ),
    // Intel docs (319536, 319974, 320047, 320529, 322861, 322862, 322849,
    // 324341).
    new FMS (    0, 6,  1,12,  1,     "Intel Atom N270 (Diamondville B0)" ),
    new FMS (    0, 6,  1,12,  2,     "Intel Atom 200/N200/300 (Diamondville C0) / Atom Z500 (Silverthorne C0)" ),
    new FMS (    0, 6,  1,12, 10,     "Intel Atom D400/N400 (Pineview A0) / Atom D500/N500 (Pineview B0)" ),
    new FM  (    0, 6,  1,12,         "Intel Atom (Diamondville / Silverthorne / Pineview)" ),
    // Intel docs (320336).
    new FMS (    0, 6,  1,13,  1,     "Intel Xeon Processor 7400 (Dunnington A1)" ),
    new FM  (    0, 6,  1,13,         "Intel Xeon (unknown type) (Dunnington)" ),
    // Intel docs (320767, 322166, 322373, 323105).
    new FMSQ(    0, 6,  1,14,  4, sX, "Intel Xeon Processor EC3500/EC5500 (Jasper Forest B0)" ), // EC names from MRG* 2018-03-06
    new FMSQ(    0, 6,  1,14,  4, dC, "Intel Celeron P1053 (Jasper Forest B0)" ),
    new FMS (    0, 6,  1,14,  4,     "Intel Xeon (unknown type) (Jasper Forest B0)" ),
    new FMSQ(    0, 6,  1,14,  5, sX, "Intel Xeon Processor 3400 (Lynnfield B1)" ),
    new FMSQ(    0, 6,  1,14,  5, Mc, "Intel Core i7-700/800/900 Mobile (Clarksfield B1)" ),
    new FMSQ(    0, 6,  1,14,  5, dc, "Intel Core i*-700/800/900 (Lynnfield B1)" ), // 900 from MRG* 2018-03-06
    new FMS (    0, 6,  1,14,  5,     "Intel Core (unknown type) (Lynnfield/Clarksfield B1)" ),
    new FMQ (    0, 6,  1,14,     sX, "Intel Xeon (Lynnfield) / Xeon (Jasper Forest)" ),
    new FMQ (    0, 6,  1,14,     dC, "Intel Celeron (Jasper Forest)" ),
    new FMQ (    0, 6,  1,14,     Mc, "Intel Core Mobile (Clarksfield)" ),
    new FMQ (    0, 6,  1,14,     dc, "Intel Core (Lynnfield)" ),
    new FM  (    0, 6,  1,14,         "Intel Core (unknown type) (Lynnfield/Clarksfield)" ),
    new FM  (    0, 6,  1,15,         "Intel (unknown model) (Havendale/Auburndale)" ),
    // Intel docs (322814, 322911, 323179, 323847, 323056, 324456).
    new FMSQ(    0, 6,  2, 5,  2, sX, "Intel Xeon Processor L3406 (Clarkdale C2)" ),
    new FMSQ(    0, 6,  2, 5,  2, MC, "Intel Celeron Mobile P4500 (Arrandale C2)" ),
    new FMSQ(    0, 6,  2, 5,  2, MP, "Intel Pentium P6000 Mobile (Arrandale C2)" ),
    new FMSQ(    0, 6,  2, 5,  2, dP, "Intel Pentium G6900 / P4500 (Clarkdale C2)" ),
    new FMSQ(    0, 6,  2, 5,  2, Mc, "Intel Core i*-300/400/500/600 Mobile (Arrandale C2)" ),
    new FMSQ(    0, 6,  2, 5,  2, dc, "Intel Core i*-300/500/600 (Clarkdale C2)" ),
    new FMS (    0, 6,  2, 5,  2,     "Intel Core (unknown type) (Clarkdale/Arrandale C2)" ),
    new FMSQ(    0, 6,  2, 5,  5, MC, "Intel Celeron Mobile U3400 (Arrandale K0) / Celeron Mobile P4600 (Arrandale K0)" ),
    new FMSQ(    0, 6,  2, 5,  5, MP, "Intel Pentium U5000 Mobile (Arrandale K0)" ),
    new FMSQ(    0, 6,  2, 5,  5, dP, "Intel Pentium P4500 / U3400 / G6900 (Clarkdale K0)" ), // G6900 only from MRG* 2018-03-06
    new FMSQ(    0, 6,  2, 5,  5, dc, "Intel Core i*-300/400/500/600 (Clarkdale K0)" ),
    new FMS (    0, 6,  2, 5,  5,     "Intel Core (unknown type) (Clarkdale/Arrandale K0)" ),
    new FMQ (    0, 6,  2, 5,     sX, "Intel Xeon Processor L3406 (Clarkdale)" ),
    new FMQ (    0, 6,  2, 5,     MC, "Intel Celeron Mobile (Arrandale)" ),
    new FMQ (    0, 6,  2, 5,     MP, "Intel Pentium Mobile (Arrandale)" ),
    new FMQ (    0, 6,  2, 5,     dP, "Intel Pentium (Clarkdale)" ),
    new FMQ (    0, 6,  2, 5,     Mc, "Intel Core Mobile (Arrandale)" ),
    new FMQ (    0, 6,  2, 5,     dc, "Intel Core (Clarkdale)" ),
    new FM  (    0, 6,  2, 5,         "Intel Core (unknown type) (Clarkdale/Arrandale)" ),
    // Intel docs (324209, 325307, 325309, 325630).
    new FMS (    0, 6,  2, 6,  1,     "Intel Atom Z600 (Lincroft C0) / Atom E600 (Tunnel Creek B0/B1)" ),
    new FM  (    0, 6,  2, 6,         "Intel Atom Z600 (Lincroft) / Atom E600 (Tunnel Creek B0/B1)" ),
    new FM  (    0, 6,  2, 7,         "Intel Atom Z2000 (Medfield)" ), // no spec update, only instlatx64 example (stepping 1)
    // Intel docs (327335) omit stepping numbers, but (324643, 324827, 324972)
    // provide some.  An instlatx64 stepping 6 example has been spoted, but it
    // isn't known which stepping name that is.
    new FMSQ(    0, 6,  2,10,  7, Xc, "Intel Mobile Core i7 Extreme (Sandy Bridge D2/J1/Q0)" ),
    new FMSQ(    0, 6,  2,10,  7, Mc, "Intel Mobile Core i*-2000 (Sandy Bridge D2/J1/Q0)" ),
    new FMSQ(    0, 6,  2,10,  7, dc, "Intel Core i*-2000 (Sandy Bridge D2/J1/Q0)" ),
    new FMSQ(    0, 6,  2,10,  7, MC, "Intel Celeron G400/G500/700/800/B800 (Sandy Bridge J1/Q0)" ),
    new FMSQ(    0, 6,  2,10,  7, sX, "Intel Xeon E3-1100 / E3-1200 v1 (Sandy Bridge D2/J1/Q0)" ),
    new FMSQ(    0, 6,  2,10,  7, dP, "Intel Pentium G500/G600/G800 / Pentium 900 (Sandy Bridge Q0)" ),
    new FMS (    0, 6,  2,10,  7,     "Intel Core (unknown type) (Sandy Bridge D2/J1/Q0)" ),
    new FMQ (    0, 6,  2,10,     Xc, "Intel Mobile Core i7 Extreme (Sandy Bridge)" ),
    new FMQ (    0, 6,  2,10,     Mc, "Intel Mobile Core i*-2000 (Sandy Bridge)" ),
    new FMQ (    0, 6,  2,10,     dc, "Intel Core i*-2000 (Sandy Bridge)" ),
    new FMQ (    0, 6,  2,10,     MC, "Intel Celeron G400/G500/700/800/B800 (Sandy Bridge)" ),
    new FMQ (    0, 6,  2,10,     sX, "Intel Xeon E3-1100 / E3-1200 v1 (Sandy Bridge)" ),
    new FMQ (    0, 6,  2,10,     dP, "Intel Pentium G500/G600/G800 / Pentium 900 (Sandy Bridge)" ),
    new FM  (    0, 6,  2,10,         "Intel Core (unknown type) (Sandy Bridge)" ),
    // Intel docs (323254: i7-900, 323338: Xeon 3600, 323372: Xeon 5600).
    // https://en.wikipedia.org/wiki/Westmere_(microarchitecture) provided
    // A0 & B0 stepping values.
    new FMSQ(    0, 6,  2,12,  0, dc, "Intel Core i7-900 / Core i7-980X (Gulftown A0)" ),
    new FMSQ(    0, 6,  2,12,  0, sX, "Intel Xeon Processor 3600 / 5600 (Westmere-EP A0)" ),
    new FMS (    0, 6,  2,12,  0,     "Intel Core (unknown type) (Gulftown/Westmere-EP A0)" ),
    new FMSQ(    0, 6,  2,12,  1, dc, "Intel Core i7-900 / Core i7-980X (Gulftown B0)" ),
    new FMSQ(    0, 6,  2,12,  1, sX, "Intel Xeon Processor 3600 / 5600 (Westmere-EP B0)" ),
    new FMS (    0, 6,  2,12,  1,     "Intel Core (unknown type) (Gulftown/Westmere-EP B0)" ),
    new FMSQ(    0, 6,  2,12,  2, dc, "Intel Core i7-900 / Core i7-980X (Gulftown B1)" ),
    new FMSQ(    0, 6,  2,12,  2, sX, "Intel Xeon Processor 3600 / 5600 (Westmere-EP B1)" ),
    new FMS (    0, 6,  2,12,  2,     "Intel Core (unknown type) (Gulftown/Westmere-EP B1)" ),
    new FMQ (    0, 6,  2,12,     dc, "Intel Core (unknown type) (Gulftown)" ),
    new FMQ (    0, 6,  2,12,     sX, "Intel Xeon (unknown type) (Westmere-EP)" ),
    new FM  (    0, 6,  2,12,         "Intel (unknown type) (Gulftown/Westmere-EP)" ),
    // Intel docs (326198, 326510).
    new FMSQ(    0, 6,  2,13,  6, sX, "Intel Xeon E5-1600/2600 (Sandy Bridge-E C1/M0)" ),
    new FMSQ(    0, 6,  2,13,  6, dc, "Intel Core i7-3800/3900 (Sandy Bridge-E C1)" ),
    new FMS (    0, 6,  2,13,  6,     "Intel Core (unknown type) (Sandy Bridge-E C1)" ),
    new FMSQ(    0, 6,  2,13,  7, sX, "Intel Xeon E5-1600/2600/4600 (Sandy Bridge-E C2/M1)" ),
    new FMSQ(    0, 6,  2,13,  7, dc, "Intel Core i7-3800/3900 (Sandy Bridge-E C2)" ),
    new FMSQ(    0, 6,  2,13,  7, dP, "Intel Pentium 1405 (Sandy Bridge-E C1)" ), // MRG* 2018-03-06
    new FMS (    0, 6,  2,13,  7,     "Intel Core (unknown type) (Sandy Bridge-E C2/M1)" ),
    new FMQ (    0, 6,  2,13,     sX, "Intel Xeon E5-1600/2600 (Sandy Bridge-E)" ),
    new FMQ (    0, 6,  2,13,     dc, "Intel Core i7-3800/3900 (Sandy Bridge-E)" ),
    new FMQ (    0, 6,  2,13,     dP, "Intel Pentium 1405 (Sandy Bridge-E)" ), // MRG* 2018-03-06
    new FM  (    0, 6,  2,13,         "Intel Core (unknown type) (Sandy Bridge-E)" ),
    // Intel docs (323344) are inconsistent.  Using Table 2 information.
    // instlatx64 samples have steppings 4 & 5, but no idea which stepping names
    // those are.
    new FMS (    0, 6,  2,14,  6,     "Intel Xeon Processor 6500 / 7500 (Beckton D0)" ),
    new FM  (    0, 6,  2,14,         "Intel Xeon Processor 6500 / 7500 (Beckton)" ),
    // Intel docs (325122).
    new FMS (    0, 6,  2,15,  2,     "Intel Xeon E7-8800 / Xeon E7-4800 / Xeon E7-2800 (Westmere-EX A2)" ),
    new FM  (    0, 6,  2,15,         "Intel Xeon (unknown type) (Westmere-EX)" ),
    // Intel docs (332067) omit stepping numbers for D1, but (328105) provide
    // some.
    new FMS (    0, 6,  3, 5,  1,     "Intel Atom Z2760 (Clover Trail C0) / Z8000 (Cherry Trail C0)" ),
    new FM  (    0, 6,  3, 5,         "Intel Atom Z2760 (Clover Trail) / Z8000 (Cherry Trail)" ),
    // Intel docs (326140) for Cedarview
    // Intel docs (328198) do not provide any FMS for Centerton, but an example
    // from jhladky@redhat.com does.
    // instlatx64 has example with stepping 9, but no idea what stepping name
    // that is.
    new FMS (    0, 6,  3, 6,  1,     "Intel Atom D2000/N2000 (Cedarview B1/B2/B3) / S1200 (Centerton B1)" ),
    new FM  (    0, 6,  3, 6,         "Intel Atom D2000/N2000 (Cedarview) / S1200 (Centerton)" ),
    // Intel docs (329475, 329671, 329901, 600827).
    new FMS (    0, 6,  3, 7,  1,     "Intel Atom Z3000 (Bay Trail-T A0)" ),
    new FMSQ(    0, 6,  3, 7,  2, dC, "Intel Celeron N2800 / N2900 (Bay Trail-M B0/B1)" ),
    new FMSQ(    0, 6,  3, 7,  2, dP, "Intel Pentium N3500 / J2800 / J2900 (Bay Trail-M B0/B1)" ),
    new FMS (    0, 6,  3, 7,  2,     "Intel Atom (unknown type) (Bay Trail-M B0/B1)" ),
    new FMSQ(    0, 6,  3, 7,  3, dC, "Intel Celeron N1700 / N1800 / N2800 / N2900 / J1700 / J1800 / J1900 (Bay Trail-M B2/B3)" ),
    new FMSQ(    0, 6,  3, 7,  3, dP, "Intel Pentium N3500 / J2800 / J2900 (Bay Trail-M B2/B3) / Atom E3800 (Bay Trail-I B3)" ),
    new FMSQ(    0, 6,  3, 7,  3, da, "Intel Atom E3800 / Z3700 (Bay Trail-I B3)" ), // Z3700 only from MRG* 2019-08-31
    new FMS (    0, 6,  3, 7,  3,     "Intel Atom (unknown type) (Bay Trail B2/B3)" ),
    new FMSQ(    0, 6,  3, 7,  4, dC, "Intel Celeron N2800 / N2900 (Bay Trail-M C0)" ),
    new FMSQ(    0, 6,  3, 7,  4, dP, "Intel Pentium N3500 / J2800 / J2900 (Bay Trail-M C0)" ),
    new FMS (    0, 6,  3, 7,  4,     "Intel Atom (unknown type) (Bay Trail-M C0 / Bay Trail-T B2/B3)" ),
    new FMSQ(    0, 6,  3, 7,  8, da, "Intel Atom Z3700 (Bay Trail)" ), // only MRG* 2019-08-31 (unknown stepping name)
    new FMSQ(    0, 6,  3, 7,  8, dC, "Intel Celeron N2800 / N2900 (Bay Trail)" ), // only MRG* 2019-08-31 (unknown stepping name)
    new FMSQ(    0, 6,  3, 7,  8, dP, "Intel Pentium N3500 (Bay Trail)" ), // only MRG* 2019-08-31 (unknown stepping name)
    new FMS (    0, 6,  3, 7,  8,     "Intel Atom (unknown type) (Bay Trail)" ), // only MRG* 2019-08-31 (unknown stepping name)
    new FMSQ(    0, 6,  3, 7,  9, da, "Intel Atom E3800 (Bay Trail-I D0)" ),
    new FMSQ(    0, 6,  3, 7,  9, dC, "Intel Celeron N2800 / N2900 (Bay Trail-M/D D0/D1)" ), // only MRG* 2018-03-06
    new FMSQ(    0, 6,  3, 7,  9, dP, "Intel Pentium J1800 / J1900 (Bay Trail-M/D D0/D1)" ), // only MRG* 2018-03-06
    new FMS (    0, 6,  3, 7,  9,     "Intel Atom (unknown type) (Bay Trail D0)" ),
    new FM  (    0, 6,  3, 7,         "Intel Atom (unknown type) (Bay Trail-M / Bay Trail-T / Bay Trail-I)" ),
    // Intel docs (326766, 326770, 326774, 329376).
    // How to differentiate Gladden from Ivy Bridge here?
    new FMSQ(    0, 6,  3,10,  9, Mc, "Intel Mobile Core i*-3000 (Ivy Bridge E1/L1) / Pentium 900/1000/2000/2100 (P0)" ),
    new FMSQ(    0, 6,  3,10,  9, dc, "Intel Core i*-3000 (Ivy Bridge E1/N0/L1)" ),
    new FMSQ(    0, 6,  3,10,  9, sX, "Intel Xeon E3-1100 v2 / E3-1200 v2 (Ivy Bridge E1/N0/L1)" ),
    new FMSQ(    0, 6,  3,10,  9, dC, "Intel Celeron 1000 / G1600 (Ivy Bridge P0)" ), // only MRG 2019-08-31
    new FMSQ(    0, 6,  3,10,  9, dP, "Intel Pentium G1600/G2000/G2100 / Pentium B900C (Ivy Bridge P0)" ),
    new FMS (    0, 6,  3,10,  9,     "Intel Core (unknown type) (Ivy Bridge E1/N0/L1/P0)" ),
    new FMQ (    0, 6,  3,10,     Mc, "Intel Mobile Core i*-3000 (Ivy Bridge) / Pentium 900/1000/2000/2100" ),
    new FMQ (    0, 6,  3,10,     dc, "Intel Core i*-3000 (Ivy Bridge)" ),
    new FMQ (    0, 6,  3,10,     sX, "Intel Xeon E3-1100 v2 / E3-1200 v2 (Ivy Bridge)" ),
    new FMQ (    0, 6,  3,10,     dC, "Intel Celeron 1000 / G1600 (Ivy Bridge)" ), // only MRG 2019-08-31
    new FMQ (    0, 6,  3,10,     dP, "Intel Pentium G1600/G2000/G2100 / Pentium B900C (Ivy Bridge)" ),
    new FM  (    0, 6,  3,10,         "Intel Core (unknown type) (Ivy Bridge)" ),
    // Intel docs (328899, 328903, 328908) omit the stepping numbers for (0,6),(3,12) C0 & D0.
    // MRG* 2018-03-06 mentions (0,6),(3,12),3, but doesn't specify which stepping name it is.
    // Coreboot* identifies the steppings.
    new FMSQ(    0, 6,  3,12,  1, sX, "Intel Xeon E3-1200 v3 (Haswell A0)" ),
    new FMSQ(    0, 6,  3,12,  1, Mc, "Intel Mobile Core i*-4000U (Mobile M) (Haswell A0)" ),
    new FMSQ(    0, 6,  3,12,  1, dc, "Intel Core i*-4000 / Mobile Core i*-4000 (Haswell A0)" ),
    new FMSQ(    0, 6,  3,12,  1, MC, "Intel Mobile Celeron 2900U (Mobile M) (Haswell A0)" ),
    new FMSQ(    0, 6,  3,12,  1, dC, "Intel Celeron G1800 / G2000 (Haswell A0)" ), // G2000 only from MRG* 2019-08-31
    new FMSQ(    0, 6,  3,12,  1, MP, "Intel Mobile Pentium 3500U / 3600U / 3500Y (Mobile M) (Haswell A0)" ),
    new FMSQ(    0, 6,  3,12,  1, dP, "Intel Pentium G3000 (Haswell A0)" ),
    new FMS (    0, 6,  3,12,  1,     "Intel Core (unknown type) (Haswell A0)" ),
    new FMSQ(    0, 6,  3,12,  2, sX, "Intel Xeon E3-1200 v3 (Haswell B0)" ),
    new FMSQ(    0, 6,  3,12,  2, Mc, "Intel Mobile Core i*-4000U (Mobile M) (Haswell B0)" ),
    new FMSQ(    0, 6,  3,12,  2, dc, "Intel Core i*-4000 / Mobile Core i*-4000 (Haswell B0)" ),
    new FMSQ(    0, 6,  3,12,  2, MC, "Intel Mobile Celeron 2900U (Mobile M) (Haswell B0)" ),
    new FMSQ(    0, 6,  3,12,  2, dC, "Intel Celeron G1800 / G2000 (Haswell B0)" ), // G2000 only from MRG* 2019-08-31
    new FMSQ(    0, 6,  3,12,  2, MP, "Intel Mobile Pentium 3500U / 3600U / 3500Y (Mobile M) (Haswell B0)" ),
    new FMSQ(    0, 6,  3,12,  2, dP, "Intel Pentium G3000 (Haswell B0)" ),
    new FMS (    0, 6,  3,12,  2,     "Intel Core (unknown type) (Haswell B0)" ),
    new FMSQ(    0, 6,  3,12,  3, sX, "Intel Xeon E3-1200 v3 (Haswell C0)" ),
    new FMSQ(    0, 6,  3,12,  3, Mc, "Intel Mobile Core i*-4000U (Mobile M) (Haswell C0)" ),
    new FMSQ(    0, 6,  3,12,  3, dc, "Intel Core i*-4000 / Mobile Core i*-4000 (Haswell C0)" ),
    new FMSQ(    0, 6,  3,12,  3, MC, "Intel Mobile Celeron 2900U (Mobile M) (Haswell C0)" ),
    new FMSQ(    0, 6,  3,12,  3, dC, "Intel Celeron G1800 / G2000 (Haswell C0)" ), // G2000 only from MRG* 2019-08-31
    new FMSQ(    0, 6,  3,12,  3, MP, "Intel Mobile Pentium 3500U / 3600U / 3500Y (Mobile M) (Haswell C0)" ),
    new FMSQ(    0, 6,  3,12,  3, dP, "Intel Pentium G3000 (Haswell C0)" ),
    new FMS (    0, 6,  3,12,  3,     "Intel Core (unknown type) (Haswell C0)" ),
    new FMQ (    0, 6,  3,12,     sX, "Intel Xeon E3-1200 v3 (Haswell)" ),
    new FMQ (    0, 6,  3,12,     Mc, "Intel Mobile Core i*-4000U (Mobile M) (Haswell)" ),
    new FMQ (    0, 6,  3,12,     dc, "Intel Core i*-4000 / Mobile Core i*-4000 (Haswell)" ),
    new FMQ (    0, 6,  3,12,     MC, "Intel Mobile Celeron 2900U (Mobile M) (Haswell)" ),
    new FMQ (    0, 6,  3,12,     dC, "Intel Celeron G1800 / G2000 (Haswell)" ), // G2000 only from MRG* 2019-08-31
    new FMQ (    0, 6,  3,12,     MP, "Intel Mobile Pentium 3500U / 3600U / 3500Y (Mobile M) (Haswell)" ),
    new FMQ (    0, 6,  3,12,     dP, "Intel Pentium G3000 (Haswell)" ),
    new FM  (    0, 6,  3,12,         "Intel Core (unknown type) (Haswell)" ),
    // Intel docs (330836) omit the stepping numbers for (0,6),(3,13) E0 & F0.
    // MRG* 2019-08-31 mentions stepping 4, but doesn't specify which stepping name it is.
    // Coreboot* identifies the steppings.
    new FMSQ(    0, 6,  3,13,  2, dc, "Intel Core i*-5000 (Broadwell-U C0) / Core M (Broadwell-Y C0)" ),
    new FMSQ(    0, 6,  3,13,  2, MC, "Intel Mobile Celeron 3000 (Broadwell-U C0)" ),
    new FMSQ(    0, 6,  3,13,  2, dC, "Intel Celeron 3000 (Broadwell-U C0)" ),
    new FMSQ(    0, 6,  3,13,  2, dP, "Intel Pentium 3700U / 3800U / 3200U (Broadwell-U C0)" ), // only MRG* 2018-03-06, 2019-08-31
    new FMS (    0, 6,  3,13,  2,     "Intel Core (unknown type) (Broadwell-U/Y C0)" ),
    new FMSQ(    0, 6,  3,13,  3, dc, "Intel Core i*-5000 (Broadwell-U D0) / Core M (Broadwell-Y D0)" ),
    new FMSQ(    0, 6,  3,13,  3, MC, "Intel Mobile Celeron 3000 (Broadwell-U D0)" ),
    new FMSQ(    0, 6,  3,13,  3, dC, "Intel Celeron 3000 (Broadwell-U D0)" ),
    new FMSQ(    0, 6,  3,13,  3, dP, "Intel Pentium 3700U / 3800U / 3200U (Broadwell-U D0)" ), // only MRG* 2018-03-06, 2019-08-31
    new FMS (    0, 6,  3,13,  3,     "Intel Core (unknown type) (Broadwell-U/Y D0)" ),
    new FMSQ(    0, 6,  3,13,  4, dc, "Intel Core i*-5000 (Broadwell-U E0) / Core M (Broadwell-Y E0)" ),
    new FMSQ(    0, 6,  3,13,  4, MC, "Intel Mobile Celeron 3000 (Broadwell-U E0)" ),
    new FMSQ(    0, 6,  3,13,  4, dC, "Intel Celeron 3000 (Broadwell-U E0)" ),
    new FMSQ(    0, 6,  3,13,  4, dP, "Intel Pentium 3700U / 3800U / 3200U (Broadwell-U E0)" ), // only MRG* 2018-03-06, 2019-08-31
    new FMS (    0, 6,  3,13,  4,     "Intel Core (unknown type) (Broadwell-U/Y E0)" ),
    new FMQ (    0, 6,  3,13,     dc, "Intel Core i*-5000 (Broadwell-U) / Core M (Broadwell-Y)" ),
    new FMQ (    0, 6,  3,13,     MC, "Intel Mobile Celeron 3000 (Broadwell-U)" ),
    new FMQ (    0, 6,  3,13,     dC, "Intel Celeron 3000 (Broadwell-U)" ),
    new FMQ (    0, 6,  3,13,     dP, "Intel Pentium 3700U / 3800U / 3200U (Broadwell-U)" ), // only MRG* 2018-03-06, 2019-08-31
    new FM  (    0, 6,  3,13,         "Intel Core (unknown type) (Broadwell-U/Y)" ),
    // Intel docs (329189, 329368, 329597).
    new FMSQ(    0, 6,  3,14,  4, sX, "Intel Xeon E5-1600/E5-2600 v2 (Ivy Bridge-EP C1/M1/S1)" ),
    new FMSQ(    0, 6,  3,14,  4, dc, "Intel Core i*-4000 (Ivy Bridge-E S1)" ),
    new FMS (    0, 6,  3,14,  4,     "Intel Core (unknown type) (Ivy Bridge-EP C1/M1/S1)" ),
    new FMSQ(    0, 6,  3,14,  7, sX, "Intel Xeon E5-4600 / E7-2800 / E7-4800 / E7-8800 v2 (Ivy Bridge-EX D1)" ), // E5-4600 names from MRG* 2018-03-06, 2019-08-31
    new FMS (    0, 6,  3,14,  7,     "Intel Xeon (unknown type) (Ivy Bridge-EX D1)" ),
    new FMQ (    0, 6,  3,14,     sX, "Intel Xeon E5-1600 / E5-2600 v2 (Ivy Bridge-EP) / Xeon E5-4600 / E7-2800 / E7-4800 / E7-8800 (Ivy Bridge-EX)" ),
    new FMQ (    0, 6,  3,14,     dc, "Intel Core i9-4000 (Ivy Bridge-E)" ),
    new FM  (    0, 6,  3,14,         "Intel Core (unknown type) (Ivy Bridge-E / Ivy Bridge-EP / Ivy Bridge-EX)" ),
    // Intel docs (330785, 330841, 332317).
    new FMSQ(    0, 6,  3,15,  2, dc, "Intel Core i7-5000 Extreme Edition (Haswell-E R2)" ),
    new FMSQ(    0, 6,  3,15,  2, sX, "Intel Xeon E5-x600 v3 (Haswell-EP C1/M1/R2)" ),
    new FMS (    0, 6,  3,15,  2,     "Intel (unknown type) (Haswell C1/M1/R2)" ),
    new FMS (    0, 6,  3,15,  4,     "Intel Xeon E7-4800 / E7-8800 v3 (Haswell-EP E0)" ),
    new FM  (    0, 6,  3,15,         "Intel Core (unknown type) (Haswell R2 / Haswell-EP)" ),
    // Intel docs (328903) omit the stepping numbers for (0,6),(4,5) C0 & D0.
    // MRG* 2019-08-31 mentions stepping 1, but doesn't specify which stepping name it is.
    // Coreboot* identifies the 0 stepping as B0, but not what the 1 stepping is.
    new FMSQ(    0, 6,  4, 5,  0, dc, "Intel Core i*-4000U (Haswell-ULT B0)" ), // no docs, but example from Brice Goglin
    new FMSQ(    0, 6,  4, 5,  0, Mc, "Intel Mobile Core i*-4000Y (Mobile U/Y) (Haswell-ULT B0)" ),
    new FMSQ(    0, 6,  4, 5,  0, MP, "Intel Mobile Pentium 3500U / 3600U / 3500Y (Mobile U/Y) (Haswell-ULT B0)" ),
    new FMSQ(    0, 6,  4, 5,  0, MC, "Intel Mobile Celeron 2900U (Mobile U/Y) (Haswell-ULT B0)" ),
    new FMS (    0, 6,  4, 5,  0,     "Intel Core (unknown type) (Haswell-ULT B0)" ),
    new FMQ (    0, 6,  4, 5,     dc, "Intel Core i*-4000U (Haswell-ULT)" ), // no docs, but example from Brice Goglin
    new FMQ (    0, 6,  4, 5,     Mc, "Intel Mobile Core i*-4000Y (Mobile U/Y) (Haswell-ULT)" ),
    new FMQ (    0, 6,  4, 5,     MP, "Intel Mobile Pentium 3500U / 3600U / 3500Y (Mobile U/Y) (Haswell-ULT)" ),
    new FMQ (    0, 6,  4, 5,     MC, "Intel Mobile Celeron 2900U (Mobile U/Y) (Haswell-ULT)" ),
    new FM  (    0, 6,  4, 5,         "Intel Core (unknown type) (Haswell-ULT)" ),
    // Intel docs (328899,328903) omit the stepping numbers for (0,6),(4,6) C0 & D0.
    // MRG* mentions (0,6),(4,6),1, but doesn't specify which stepping name it is.
    new FMQ (    0, 6,  4, 6,     Mc, "Intel Mobile Core i*-4000Y (Mobile H) (Crystal Well)" ),
    new FMQ (    0, 6,  4, 6,     dc, "Intel Core i*-4000 / Mobile Core i*-4000 (Desktop R) (Crystal Well)" ),
    new FMQ (    0, 6,  4, 6,     MP, "Intel Mobile Pentium 3500U / 3600U / 3500Y (Mobile H) (Crystal Well)" ),
    new FMQ (    0, 6,  4, 6,     dC, "Intel Celeron G1800 (Desktop R) (Crystal Well)" ),
    new FMQ (    0, 6,  4, 6,     MC, "Intel Mobile Celeron 2900U (Mobile H) (Crystal Well)" ),
    new FMQ (    0, 6,  4, 6,     dP, "Intel Pentium G3000 (Desktop R) (Crystal Well)" ),
    new FM  (    0, 6,  4, 6,         "Intel Core (unknown type) (Crystal Well)" ),
    // So far, all these (0,6),(4,7) processors are stepping G0, but the
    // Intel docs (332381, 332382) omit the stepping number for G0.
    // MRG* 2018-03-06 describes Broadwell H 43e.
    new FMSQ(    0, 6,  4, 7,  1, dc, "Intel Core i*-5000 (Broadwell G0)" ),
    new FMSQ(    0, 6,  4, 7,  1, Mc, "Intel Mobile Core i7-5000 (Broadwell G0)" ),
    new FMSQ(    0, 6,  4, 7,  1, sX, "Intel Xeon E3-1200 v4 (Broadwell G0)" ),
    new FMS (    0, 6,  4, 7,  1,     "Intel (unknown type) (Broadwell-H G0)" ),
    new FMQ (    0, 6,  4, 7,     dc, "Intel Core i7-5000 (Broadwell)" ),
    new FMQ (    0, 6,  4, 7,     Mc, "Intel Mobile Core i7-5000 (Broadwell)" ),
    new FMQ (    0, 6,  4, 7,     sX, "Intel Xeon E3-1200 v4 (Broadwell)" ),
    new FM  (    0, 6,  4, 7,         "Intel Core (unknown type) (Broadwell)" ),
    // no spec update; only MSR_CPUID_table* so far
    // MRG* 2018-03-06 mentions steppings 8 and 9, but without names for either.
    new FM  (    0, 6,  4,10,         "Intel Atom Z3400 (Merrifield)" ),
    // Intel docs (332095).
    new FMSQ(    0, 6,  4,12,  3, dC, "Intel Celeron N3000 / J3000 (Braswell C0)" ),
    new FMSQ(    0, 6,  4,12,  3, dP, "Intel Pentium N3000 / J3000 (Braswell C0)" ),
    new FMSQ(    0, 6,  4,12,  3, da, "Intel Atom x5-E8000 / x*-Z8000 (Cherry Trail C0)" ), // no spec update; only MRG* 2018-03-06, 2019-08-31
    new FMS (    0, 6,  4,12,  3,     "Intel Atom (unknown type) (Braswell/Cherry Trail C0)" ),
    new FMSQ(    0, 6,  4,12,  4, dC, "Intel Celeron N3000 / J3000 (Braswell D1)" ),
    new FMSQ(    0, 6,  4,12,  4, dP, "Intel Pentium N3000 / J3000 (Braswell D1)" ),
    new FMSQ(    0, 6,  4,12,  4, da, "Intel Atom x5-E8000 / x*-Z8000 (Cherry Trail D1)" ), // no spec update; only MRG* 2018-03-06, 2019-08-31
    new FMS (    0, 6,  4,12,  4,     "Intel Atom (unknown type) (Braswell/Cherry Trail D1)" ),
    new FMQ (    0, 6,  4,12,     dC, "Intel Celeron N3000 / J3000 (Braswell)" ),
    new FMQ (    0, 6,  4,12,     dP, "Intel Pentium N3000 / J3000 (Braswell)" ),
    new FMQ (    0, 6,  4,12,     da, "Intel Atom x5-E8000 / x*-Z8000 (Cherry Trail)" ), // no spec update; only MRG* 2018-03-06, 2019-08-31
    new FM  (    0, 6,  4,12,         "Intel Atom (unknown type) (Braswell/Cherry Trail)" ),
    // Intel docs (329460, 330061).
    new FMS (    0, 6,  4,13,  0,     "Intel Atom C2000 (Avoton A0/A1)" ),
    new FMS (    0, 6,  4,13,  8,     "Intel Atom C2000 (Avoton/Rangeley B0/C0)" ),
    new FM  (    0, 6,  4,13,         "Intel Atom C2000 (Avoton)" ),
    // Intel docs (332689) omit the stepping numbers for (0,6),(4,14) D1 & K1.
    // MRG* 2018-03-06 mentions (0,6),(4,14),3, but doesn't specify which
    // stepping name it is.
    // Coreboot* identifies the 2 (C0) & 3 (D0) steppings, neither of which is
    // mentioned in (332689).
    // Coreboot* identifies stepping 8 as (Kaby Lake G0). Perhaps they were just
    // early engineering samples of Kaby Lake.
    new FMSQ(    0, 6,  4,14,  2, dc, "Intel Core i*-6000U / m*-6Y00 (Skylake C0)" ),
    new FMSQ(    0, 6,  4,14,  2, dP, "Intel Pentium 4405U / Pentium 4405Y (Skylake C0)" ),
    new FMSQ(    0, 6,  4,14,  2, dC, "Intel Celeron 3800U / 39000U (Skylake C0)" ),
    new FMSQ(    0, 6,  4,14,  2, sX, "Intel Xeon E3-1500m (Skylake C0)" ), // no spec update; only MSR_CPUID_table* so far
    new FMS (    0, 6,  4,14,  2,     "Intel Core (unknown type) (Skylake C0)" ),
    new FMSQ(    0, 6,  4,14,  3, dc, "Intel Core i*-6000U / m*-6Y00 (Skylake D0)" ),
    new FMSQ(    0, 6,  4,14,  3, dP, "Intel Pentium 4405U / Pentium 4405Y (Skylake D0)" ),
    new FMSQ(    0, 6,  4,14,  3, dC, "Intel Celeron 3800U / 39000U (Skylake D0)" ),
    new FMSQ(    0, 6,  4,14,  3, sX, "Intel Xeon E3-1500m (Skylake D0)" ), // no spec update; only MSR_CPUID_table* so far
    new FMS (    0, 6,  4,14,  3,     "Intel Core (unknown type) (Skylake D0)" ),
    new FMS (    0, 6,  4,14,  8,     "Intel Core (unknown type) (Kaby Lake G0)" ), // Coreboot*
    new FMQ (    0, 6,  4,14,     dc, "Intel Core i*-6000U / m*-6Y00 (Skylake)" ),
    new FMQ (    0, 6,  4,14,     dP, "Intel Pentium 4405U / Pentium 4405Y (Skylake)" ),
    new FMQ (    0, 6,  4,14,     dC, "Intel Celeron 3800U / 39000U (Skylake)" ),
    new FMQ (    0, 6,  4,14,     sX, "Intel Xeon E3-1500m (Skylake)" ), // no spec update; only MSR_CPUID_table* so far
    new FM  (    0, 6,  4,14,         "Intel Core (unknown type) (Skylake)" ),
    // Intel docs (333811, 334165) omit the stepping numbers for (0,6),(4,15)
    // B0, M0 & R0, but (334208) provide some.
    new FMSQ(    0, 6,  4,15,  1, dc, "Intel Core i7-6800K / i7-6900K / i7-6900X (Broadwell-E R0)" ),
    new FMQ (    0, 6,  4,15,     dc, "Intel Core i7-6800K / i7-6900K / i7-6900X (Broadwell-E)" ),
    new FMSQ(    0, 6,  4,15,  1, sX, "Intel Xeon E5-1600 / E5-2600 / E5-4600 v4 (Broadwell-E) / E7-4800 / E7-8800 v4 (Broadwell-EX B0)" ),
    new FMQ (    0, 6,  4,15,     sX, "Intel Xeon E5-1600 / E5-2600 / E5-4600 v4 (Broadwell-E) / E7-4800 / E7-8800 v4 (Broadwell-EX)" ),
    new FM  (    0, 6,  4,15,         "Intel Core (unknown type) (Broadwell-E / Broadwell-EX)" ),
    // Intel docs (335901) omit almost all details for the Core versions of
    // (0,6),(5,5).  But Intel docs (336065: Xeon Scalable steppings 2 & 4,
    // 338848: Xeon Scalable (2nd gen) stepping 7, and 338854: Xeon D-2000
    // stepping 2) provides some.
    // MRG* 2019-11-13 mentions stepping 3, but doesn't mention stepping name.
    // geekbench.com has an "Intel Xeon Gold 6230" example of a stepping 5, but
    // no stepping name.
    new FMSQ(    0, 6,  5, 5,  2, sS, "Intel Scalable Bronze/Silver/Gold/Platinum (Skylake B0/L0)" ),
    new FMSQ(    0, 6,  5, 5,  2, sX, "Intel Xeon W 2000 / D-2100 (Skylake B0/L0)" ),
    new FMSQ(    0, 6,  5, 5,  4, sS, "Intel Scalable Bronze/Silver/Gold/Platinum (Skylake H0/M0/U0)" ),
    new FMSQ(    0, 6,  5, 5,  4, sX, "Intel Xeon W 2000 / D-2100 (Skylake H0/M0/U0)" ), // D-2100 from MRG* 2018-03-06
    new FMSQ(    0, 6,  5, 5,  4, dc, "Intel Core i9-7000X (Skylake-X H0/M0/U0)" ), // only from MRG* 2018-03-06
    new FMSQ(    0, 6,  5, 5,  4, iM, "Montage Jintide Gen1" ), // undocumented; only instlatx64 example
    new FMSQ(    0, 6,  5, 5,  6, sS, "Intel Scalable (2nd Gen) Bronze/Silver/Gold/Platinum (Cascade Lake)" ), // no docs, but example from Greg Stewart
    new FMSQ(    0, 6,  5, 5,  6, sX, "Intel Xeon W 2000 (Cascade Lake)" ), // no docs, but example from Greg Stewart
    new FMSQ(    0, 6,  5, 5,  7, dc, "Intel Core i*-10000X (Cascade Lake-X B1/L1/R1)" ), // no docs, but instlatx64 example
    new FMSQ(    0, 6,  5, 5,  7, sS, "Intel Scalable (2nd Gen) Bronze/Silver/Gold/Platinum (Cascade Lake B1/L1/R1)" ),
    new FMSQ(    0, 6,  5, 5,  7, sX, "Intel Xeon W 2000 (Cascade Lake-W B1/L1/R1)" ),
    new FMS (    0, 6,  5, 5, 10,     "Intel (unknown type) (Cooper Lake)" ),
    new FMQ (    0, 6,  5, 5,     sS, "Intel Scalable Bronze/Silver/Gold/Platinum (Skylake / Cascade Lake)" ),
    new FMQ (    0, 6,  5, 5,     sX, "Intel Xeon W 2000 / D-2100 (Skylake / Cascade Lake)" ),
    new FMQ (    0, 6,  5, 5,     dc, "Intel Core i*-6000X / i*-7000X (Skylake-X) / i*-10000X (Cascade Lake-X)" ),
    new FM  (    0, 6,  5, 5,         "Intel Core (unknown type) (Skylake / Skylake-X / Cascade Lake / Cascade Lake-X)" ),
    // Intel docs (332054).
    new FMS (    0, 6,  5, 6,  1,     "Intel Xeon D-1500 (Broadwell-DE U0)" ),
    new FMS (    0, 6,  5, 6,  2,     "Intel Xeon D-1500 (Broadwell-DE V1)" ),
    new FMS (    0, 6,  5, 6,  3,     "Intel Xeon D-1500 (Broadwell-DE V2/V3)" ), // V3 from MRG* 2018-03-06
    new FMS (    0, 6,  5, 6,  4,     "Intel Xeon D-1500 (Broadwell-DE Y0)" ),
    new FMS (    0, 6,  5, 6,  5,     "Intel Xeon D-1500N (Broadwell-DE A1)" ),
    new FM  (    0, 6,  5, 6,         "Intel Xeon (unknown type) (Broadwell-DE)" ),
    // Intel docs (334646) omit the stepping number for B0.  But as of Jan 2020,
    // it is the only stepping, and all examples seen have stepping number 1.
    new FMS (    0, 6,  5, 7,  1,     "Intel Xeon Phi x200 (Knights Landing B0)" ),
    new FM  (    0, 6,  5, 7,         "Intel Xeon Phi x200 (Knights Landing)" ),
    new FM  (    0, 6,  5,10,         "Intel Atom Z3500 (Moorefield)" ), // no spec update; only MSR_CPUID_table* & instlatx64 example so far
    // Intel docs (334820) & MRG* 2018-03-06
    // Coreboot* provides stepping 8 (A0).
    new FMS (    0, 6,  5,12,  2,     "Intel Atom T5000 (Apollo Lake)" ), // no spec update; only MRG* 2018-03-06
    new FMS (    0, 6,  5,12,  8,     "Intel Atom (unknown type) (Apollo Lake A0)" ),
    new FMSQ(    0, 6,  5,12,  9, dP, "Intel Pentium N4000 / J4000 (Apollo Lake B0/B1/D0)" ),
    new FMSQ(    0, 6,  5,12,  9, dC, "Intel Celeron N3000 / J3000 (Apollo Lake B0/B1/D0)" ),
    new FMSQ(    0, 6,  5,12,  9, da, "Intel Atom x*-E3900 / x*-A3900 (Apollo Lake B0/B1/D0)" ), // A3900 only from MRG* 2019-08-31
    new FMS (    0, 6,  5,12,  9,     "Intel Atom (unknown type) (Apollo Lake B0/B1/D0)" ),
    new FMSQ(    0, 6,  5,12, 10, da, "Intel Atom x*-E3900 (Apollo Lake E0)" ),
    new FMS (    0, 6,  5,12, 10,     "Intel Atom (unknown type) (Apollo Lake E0)" ),
    new FM  (    0, 6,  5,12,         "Intel Atom (unknown type) (Apollo Lake)" ), // no spec update for Atom; only MSR_CPUID_table* so far
    // No spec update; only MSR_CPUID_table* so far (and instlatx64 example)
    // MRG* 2018-03-06 mentions (0,6),(5,13),1 stepping, but doesn't mention stepping name.
    new FM  (    0, 6,  5,13,         "Intel Atom x3-C3000 (SoFIA)" ),
    // Intel docs (332689,333133) omit the stepping numbers for (0,6),(5,14)
    // R0 & S0.
    // MRG* 2018-03-06 mentions (0,6),(5,14),3, but doesn't specify which
    // stepping name it is.
    // Coreboot* identifies the 1 (Q0) & 3 (R0) steppings, but not the S0
    // stepping.
    // Coreboot* identifies stepping 8 as (Kaby Lake-H A0). Perhaps they were just
    // early engineering samples of Kaby Lake.
    new FMSQ(    0, 6,  5,14,  1, dc, "Intel Core i*-6000 (Skylake-H Q0)" ),
    new FMSQ(    0, 6,  5,14,  1, dP, "Intel Pentium G4000 (Skylake-H Q0)" ),
    new FMSQ(    0, 6,  5,14,  1, dC, "Intel Celeron G3900 (Skylake-H Q0)" ),
    new FMSQ(    0, 6,  5,14,  1, sX, "Intel Xeon E3-1200 / E3-1500 v5 (Skylake-H Q0)" ), // E3-1500 only from MRG 2019-08-31
    new FMS (    0, 6,  5,14,  1,     "Intel Core (unknown type) (Skylake-H Q0)" ),
    new FMSQ(    0, 6,  5,14,  3, dc, "Intel Core i*-6000 (Skylake-H R0)" ),
    new FMSQ(    0, 6,  5,14,  3, dP, "Intel Pentium G4000 (Skylake-H R0)" ),
    new FMSQ(    0, 6,  5,14,  3, dC, "Intel Celeron G3900 (Skylake-H R0)" ),
    new FMSQ(    0, 6,  5,14,  3, sX, "Intel Xeon E3-1200 / E3-1500 v5 (Skylake-H R0)" ), // E3-1500 only from MRG 2019-08-31
    new FMS (    0, 6,  5,14,  3,     "Intel Core (unknown type) (Skylake-H R0)" ),
    new FMS (    0, 6,  4,14,  8,     "Intel Core (unknown type) (Kaby Lake-H A0)" ), // Coreboot*
    new FMQ (    0, 6,  5,14,     dc, "Intel Core i*-6000 (Skylake-H)" ),
    new FMQ (    0, 6,  5,14,     dP, "Intel Pentium G4000 (Skylake-H)" ),
    new FMQ (    0, 6,  5,14,     dC, "Intel Celeron G3900 (Skylake-H)" ),
    new FMQ (    0, 6,  5,14,     sX, "Intel Xeon E3-1200 / E3-1500 v5 (Skylake-H)" ), // E3-1500 only from MRG 2019-08-31
    new FM  (    0, 6,  5,14,         "Intel Core (unknown type) (Skylake-H)" ),
    // Intel docs (336345).
    new FMS (    0, 6,  5,15,  0,     "Intel Atom C3000 (Denverton A0/A1)" ),
    new FMS (    0, 6,  5,15,  1,     "Intel Atom C3000 (Denverton B0/B1)" ),
    new FM  (    0, 6,  5,15,         "Intel Atom C3000 (Denverton)" ),
    new FM  (    0, 6,  6, 5,         "Intel XMM 7272 (SoFIA)" ), // no spec update; only MRG* 2018-03-06, 2019-08-31
    // no spec update; only MSR_CPUID_table* & instlatx64 example so far
    // Coreboot* provides the steppings.
    new FMS (    0, 6,  6, 6,  0,     "Intel Core (Cannon Lake A0)" ),
    new FMS (    0, 6,  6, 6,  1,     "Intel Core (Cannon Lake B0)" ),
    new FMS (    0, 6,  6, 6,  2,     "Intel Core (Cannon Lake C0)" ),
    new FMS (    0, 6,  6, 6,  3,     "Intel Core (Cannon Lake D0)" ),
    new FM  (    0, 6,  6, 6,         "Intel Core (Cannon Lake)" ),
    new FM  (    0, 6,  6, 7,         "Intel Core (Cannon Lake)"), // DPTF*
    new FMSQ(    0, 6,  6,10,  6, sS, "Intel Scalable (3rd Gen) Bronze/Silver/Gold/Platinum (Ice Lake D2/M1)"),
    new FMQ (    0, 6,  6,10,     sS, "Intel Scalable (3rd Gen) Bronze/Silver/Gold/Platinum (Ice Lake)"),
    new FM  (    0, 6,  6,10,         "Intel (unknown type) (Ice Lake)" ),
    new FM  (    0, 6,  6,12,         "Intel Core (Ice Lake)" ),  // no spec update; only MSR_CPUID_table* so far; DPTF* claims this is Meteor Lake S.
    // No spec update; only MRG* 2018-03-06, 2019-08-31.  It is some sort of Atom,
    // but no idea which uarch or core.
    new FM  (    0, 6,  6,14,         "Intel Puma 7" ),
    // No spec update; only instlatx64 example.
    new FM  (    0, 6,  7, 5,         "Intel Spreadtrum SC9853I-IA" ),
    // Intel docs (336562).
    // MRG* 2019-11-13 mentions stepping 8, but doesn't mention stepping name.
    // Coreboot* provides steppings 0 (A0) & 8 (R0).
    new FMSQ(    0, 6,  7,10,  0, dP, "Intel Pentium Silver N5000 / J5000 (Gemini Lake A0)" ),
    new FMSQ(    0, 6,  7,10,  0, dC, "Intel Celeron N4000 / J4000 (Gemini Lake A0)" ),
    new FMS (    0, 6,  7,10,  0,     "Intel (unknown type) (Gemini Lake A0)" ),
    new FMSQ(    0, 6,  7,10,  1, dP, "Intel Pentium Silver N5000 / J5000 (Gemini Lake B0)" ),
    new FMSQ(    0, 6,  7,10,  1, dC, "Intel Celeron N4000 / J4000 (Gemini Lake B0)" ),
    new FMS (    0, 6,  7,10,  1,     "Intel (unknown type) (Gemini Lake B0)" ),
    new FMSQ(    0, 6,  7,10,  8, dP, "Intel Pentium Silver N5000 / J5000 (Gemini Lake R0)" ),
    new FMSQ(    0, 6,  7,10,  8, dC, "Intel Celeron N4000 / J4000 (Gemini Lake R0)" ),
    new FMS (    0, 6,  7,10,  8,     "Intel (unknown type) (Gemini Lake R0)" ),
    new FMQ (    0, 6,  7,10,     dP, "Intel Pentium Silver N5000 / J5000 (Gemini Lake)" ),
    new FMQ (    0, 6,  7,10,     dC, "Intel Celeron N4000 / J4000 (Gemini Lake)" ),
    new FM  (    0, 6,  7,10,         "Intel (unknown type) (Gemini Lake)" ),
    new FM  (    0, 6,  7,13,         "Intel Core i*-10000 (Ice Lake)" ), // no spec update; only MSR_CPUID_table* so far
    // Intel docs (341079) provide inconsistent information about stepping
    // numbers for (0,6),(7,14), and it contradicts actual samples, so I'm
    // ignoring it.
    // Currently there are no Ice Lake CPUs for Xeon/Pentium/Celeron.
    // Coreboot* provides steppings 0 (A0) & 1 (B0), but not for stepping 5,
    // seen in an instlatx64 sample.
    new FMS (    0, 6,  7,14,  0,     "Intel Core i*-10000 (Ice Lake-U/Y A0)" ),
    new FMS (    0, 6,  7,14,  1,     "Intel Core i*-10000 (Ice Lake-U/Y B0)" ),
    new FM  (    0, 6,  7,14,         "Intel Core i*-10000 (Ice Lake-U/Y)" ),
    // no spec update; only MSR_CPUID_table* so far
    // MRG* 2018-03-06 mentions stepping 0, but doesn't specify which stepping name it is.
    new FM  (    0, 6,  8, 5,         "Intel Xeon Phi (Knights Mill)" ),
    new FM  (    0, 6,  8, 6,         "Intel Atom (Elkhart Lake)" ),
    new FM  (    0, 6,  8,10,         "Intel Atom (Lakefield)" ), // no spec update; LX*
    // changed
    // Coreboot* provides steppings.
    // new FMS (    0, 6,  8,12,  0,     "Intel Core (Tiger Lake-U A0)" ),
    // new FMS (    0, 6,  8,12,  1,     "Intel Core (Tiger Lake-U B0)" ),
    // new FM  (    0, 6,  8,12,         "Intel Core (Tiger Lake-U)" ),
    // new FM  (    0, 6,  8,13,         "Intel Core (Tiger Lake)" ), // LX*
    // Coreboot* provides steppings.
    new FMS (    0, 6,  8,12,  0,     "Intel Core (Tiger Lake-U A0)"),
    new FMSQ(    0, 6,  8,12,  1, dC, "Intel Celeron 6000 (Tiger Lake-U B0)"),
    new FMSQ(    0, 6,  8,12,  1, dP, "Intel Pentium Gold 7505 (Tiger Lake-U B0)"),
    new FMSQ(    0, 6,  8,12,  1, dc, "Intel Core i*-11000 / i*-1100G* (Tiger Lake-U B0)"),
    new FMS (    0, 6,  8,12,  1,     "Intel (unknown type) (Tiger Lake-U B0)"),
    new FMQ (    0, 6,  8,12,     dC, "Intel Celeron (Tiger Lake-U)"),
    new FMQ (    0, 6,  8,12,     dP, "Intel Pentium (Tiger Lake-U)"),
    new FMQ (    0, 6,  8,12,     dc, "Intel Core (Tiger Lake-U)"),
    new FM  (    0, 6,  8,12,         "Intel (unknown type) (Tiger Lake-U)"),
    // no spec update; only MSR_CPUID_table* so far
    // Coreboot* provides stepping.
    new FMS (    0, 6,  8,13,  1,     "Intel Core (Tiger Lake R0)"),
    new FM  (    0, 6,  8,13,         "Intel Core (Tiger Lake)"),
    // end of changed
    // Intel docs (334663) omit the stepping numbers for (0,6),(8,14)
    // H0, J1 & Y0, but (338025, 615213) provide some.
    // Coreboot* provides the 9 (H0) & 10 (Y0) stepping, but not J1.
    new FMSQ(    0, 6,  8,14,  9, UC, "Intel Celeron 3x65U (Kaby Lake H0)" ), // MRG* 2019-08-31 pinned down stepping
    new FMSQ(    0, 6,  8,14,  9, UP, "Intel Celeron 4415U (Kaby Lake H0)" ), // MRG* 2019-08-31 pinned down stepping
    new FMSQ(    0, 6,  8,14,  9, YC, "Intel Celeron 3x65Y (Kaby Lake H0)" ), // MRG* 2019-08-31 pinned down stepping
    new FMSQ(    0, 6,  8,14,  9, YP, "Intel Celeron 4410Y (Kaby Lake H0)" ), // MRG* 2019-08-31 pinned down stepping
    new FMSQ(    0, 6,  8,14,  9, Y8, "Intel i*-8000Y / m*-8000Y (Amber Lake-Y H0)" ), // no spec update; only MRG* 2019-08-31 & instlatx64 examples
    new FMSQ(    0, 6,  8,14,  9, LY, "Intel Core i*-7000Y (Kaby Lake H0)" ), // no spec update; only MRG* 2019-08-31 & instlatx64 examples
    new FMSQ(    0, 6,  8,14,  9, dc, "Intel Core i*-7000U (Kaby Lake H0)" ), // no docs on stepping; MRG* 2018-03-06, 2019-08-31
    new FMSQ(    0, 6,  8,14, 10, dc, "Intel Core i*-8000U (Kaby Lake Y0)" ), // no docs on stepping; MRG* 2018-03-06
    new FMSQ(    0, 6,  8,14, 11, LU, "Intel Core i*-8000U (Whiskey Lake-U W0)" ),
    new FMSQ(    0, 6,  8,14, 11, LY, "Intel Core i*-8000Y (Amber Lake-Y W0)" ),
    new FMSQ(    0, 6,  8,14, 11, UC, "Intel Celeron 4205U (Whiskey Lake-U W0)" ),
    new FMSQ(    0, 6,  8,14, 11, UP, "Intel Pentium 5405U (Whiskey Lake-U W0)" ),
    new FMS (    0, 6,  8,14, 11,     "Intel Core (unknown type) (Whiskey Lake-U W0 / Amber Lake-Y W0)" ),
    new FMSQ(    0, 6,  8,14, 12, UX, "Intel Core i*-10000U (Comet Lake-U V1)" ),
    new FMSQ(    0, 6,  8,14, 12, LU, "Intel Core i*-8000U (Whiskey Lake-U V0)" ),
    new FMSQ(    0, 6,  8,14, 12, LY, "Intel Core i*-8000Y / m*-8000Y / i*-10000Y (Amber Lake-Y V0)" ), // m*-8000Y & i*-10000Y from MRG* 2019-11-13
    new FMSQ(    0, 6,  8,14, 12, dP, "Intel Pentium 6000U (Comet Lake-U V1)" ), // MRG* 2019-08-31 pinned down stepping
    new FMSQ(    0, 6,  8,14, 12, dC, "Intel Celeron 5000U (Comet Lake-U V1)" ), // MRG* 2019-08-31 pinned down stepping
    new FMS (    0, 6,  8,14, 12,     "Intel (unknown type) (Whiskey Lake-U V0 / Comet Lake-U V1)" ),
    new FM  (    0, 6,  8,14,         "Intel Core (unknown type) (Kaby Lake / Amber Lake-Y / Whiskey Lake-U / Comet Lake-U)" ),
    new FMS (    0, 6,  8,15, 8,      "Intel Xeon (Sapphire Rapids)" ),
    new FM  (    0, 6,  8,15,         "Intel Xeon (unknown type) (Sapphire Rapids)" ), // LX*
    // changed
    // LX*.  Coreboot* provides stepping.
    // new FMS (    0, 6,  9, 6,  0,     "Intel Atom (Elkhart Lake A0)" ),
    // new FMS (    0, 6,  9, 6,  1,     "Intel Atom (Elkhart Lake B0)" ),
    // new FM  (    0, 6,  9, 6,         "Intel Atom (Elkhart Lake)" ),
    // LX*.  Coreboot* provides stepping.
    // new FMS (    0, 6,  9, 7,  0,     "Intel Atom (Alder Lake-S A0)" ),
    // new FM  (    0, 6,  9, 7,         "Intel Atom (Alder Lake-S)" ),
    // Coreboot*.  Coreboot* provides stepping.
    // new FMS (    0, 6,  9,10,  0,     "Intel Atom (Alder Lake-P A0)" ),
    // new FM  (    0, 6,  9,10,         "Intel Atom (Alder Lake-P)" ),
    // LX*.  Coreboot* provides stepping.
    // new FMS (    0, 6,  9,12,  0,     "Intel Atom (Jasper Lake A0)" ),
    // new FM  (    0, 6,  9,12,         "Intel Atom (Jasper Lake)" ),
    // new FM  (    0, 6,  9,13,         "Intel NNP I-1000 (Spring Hill)" ), // LX*
    // LX*.  Coreboot* provides stepping.
    new FMSQ(    0, 6,  9, 6,  0, dC, "Intel Celeron J6400 / N6400 (Elkhart Lake A0)"),
    new FMSQ(    0, 6,  9, 6,  0, dP, "Intel Pentium J6400 / N6400 (Elkhart Lake A0)"),
    new FMSQ(    0, 6,  9, 6,  0, da, "Intel Atom (Elkhart Lake A0)"),
    new FMS (    0, 6,  9, 6,  0,     "Intel (unknown type) (Elkhart Lake A0)"),
    new FMSQ(    0, 6,  9, 6,  1, dC, "Intel Celeron J6400 / N6400 (Elkhart Lake B0/B1)"),
    new FMSQ(    0, 6,  9, 6,  1, dP, "Intel Pentium J6400 / N6400 (Elkhart Lake B0/B1)"),
    new FMSQ(    0, 6,  9, 6,  1, da, "Intel Atom (Elkhart Lake B0/B1)"),
    new FMS (    0, 6,  9, 6,  1,     "Intel (unknown type) (Elkhart Lake B0/B1)"),
    new FMQ (    0, 6,  9, 6,     dC, "Intel Celeron (Elkhart Lake)"),
    new FMQ (    0, 6,  9, 6,     dP, "Intel Pentium (Elkhart Lake)"),
    new FMQ (    0, 6,  9, 6,     da, "Intel Atom (Elkhart Lake)"),
    new FM  (    0, 6,  9, 6,         "Intel (unknown type) (Elkhart Lake)"),
    // Coreboot* provides A0 stepping.
    // Intel docs (682436) mention Core stepping value 2, but omit stepping name.
    new FMSQ(    0, 6,  9, 7,  0, dc, "Intel Core (Alder Lake-S A0)"),
    new FMS (    0, 6,  9, 7,  0,     "Intel (unknown type) (Alder Lake-S A0)"),
    new FMQ (    0, 6,  9, 7,     dc, "Intel Core i*-12000K / i*-12000KF (Alder Lake-S)"),
    new FM  (    0, 6,  9, 7,         "Intel (unknown type) (Alder Lake-S)"),
    // Coreboot*.  Coreboot* provides stepping.
    new FMS (    0, 6,  9,10,  0,     "Intel Atom (Alder Lake A0)"),
    new FMS (    0, 6,  9,10,  1,     "Intel Atom (Alder Lake A1)"),
    new FMS (    0, 6,  9,10,  2,     "Intel Atom (Alder Lake A2)"),
    new FMS (    0, 6,  9,10,  4,     "Intel Atom (Alder Lake A3)"),
    new FM  (    0, 6,  9,10,         "Intel Atom (Alder Lake)"),
    // LX*.  Coreboot* provides stepping.
    new FMSQ(    0, 6,  9,12,  0, dC, "Intel Celeron N4500 / N5100 (Jasper Lake A0)"),
    new FMSQ(    0, 6,  9,12,  0, dP, "Intel Pentium N6000 (Jasper Lake A0)"),
    new FMS (    0, 6,  9,12,  0,     "Intel (unknown type) (Jasper Lake A0)"),
    new FMQ (    0, 6,  9,12,     dC, "Intel Celeron N4500 / N5100 (Jasper Lake)"),
    new FMQ (    0, 6,  9,12,     dP, "Intel Pentium N6000 (Jasper Lake)"),
    new FM  (    0, 6,  9,12,         "Intel (unknown type) (Jasper Lake)"),
    new FM  (    0, 6,  9,13,         "Intel NNP I-1000 (Spring Hill)"), // LX*
    // end of changed
    // Intel docs (334663, 335718, 336466, 338014) omit the stepping numbers for
    // (0,6),(9,14) B0, but (337346) provides some.
    // Coreboot* provides the 9 (B0) stepping.
    // WARNING: If adding new steppings here, also update decode_uarch_intel.
    new FMSQ(    0, 6,  9,14,  9, LG, "Intel Core i*-8700 (Kaby Lake-H B0)" ), // no docs on stepping; only MRG* 2018-03-06, 2019-08-31
    new FMSQ(    0, 6,  9,14,  9, dc, "Intel Core i*-7700 (Kaby Lake-H B0)" ), // no docs on stepping; only MRG* 2018-03-06 & instlatx64 examples
    new FMSQ(    0, 6,  9,14,  9, sX, "Intel Xeon E3-1200 v6 (Kaby Lake-H B0)" ), // no docs on stepping; only MRG* 2018-03-06
    new FMSQ(    0, 6,  9,14,  9, dC, "Intel Celeron G3930 (Kaby Lake-H B0)" ), // MRG* 2020-01-27 pinned down stepping
    new FMSQ(    0, 6,  9,14, 10, LU, "Intel Core i*-8000 U Line (Coffee Lake D0)" ),
    new FMSQ(    0, 6,  9,14, 10, dc, "Intel Core i*-8000 S/H Line (Coffee Lake U0)" ),
    new FMSQ(    0, 6,  9,14, 10, sX, "Intel Xeon E-2100 (Coffee Lake U0)" ), // MRG* 2019-08-31
    new FMSQ(    0, 6,  9,14, 11, dc, "Intel Core i*-8000 S Line (Coffee Lake B0)" ),
    new FMSQ(    0, 6,  9,14, 11, dC, "Intel Celeron G4900 (Coffee Lake B0)" ), // no spec update; MRG* 2020-01-27
    new FMSQ(    0, 6,  9,14, 11, dP, "Intel Pentium Gold G5000 (Coffee Lake B0)" ), // MRG* 2020-01-27 pinned down stepping
    new FMSQ(    0, 6,  9,14, 12, dc, "Intel Core i*-9000 S Line (Coffee Lake P0)" ),
    new FMSQ(    0, 6,  9,14, 13, d1, "Intel CC150 (Coffee Lake R0)" ), // no docs; only instlatx64 example
    new FMSQ(    0, 6,  9,14, 13, dc, "Intel Core i*-9000 H Line (Coffee Lake R0)" ),
    new FMSQ(    0, 6,  9,14, 13, sX, "Intel Xeon E-2200 (Coffee Lake R0)" ), // no docs on stepping; only MRG 2019-11-13
    new FM  (    0, 6,  9,14,         "Intel (unknown type) (Kaby Lake / Coffee Lake)" ),
    new FM  (    0, 6,  9,15,         "Intel (unknown type) (Ice Lake)"), // undocumented, but (engr?) sample via instlatx64 from Komachi_ENSAKA
    // changed
    // LX*.  Coreboot* provides more detail & steppings
    // (615213) mentions the (0,6),(10,5),2 and (0,6),(10,5),5 steppings, but
    // does not provide their names.
    // en.wikichip.org provides more details on stepping names.
    // new FMSQ(    0, 6, 10, 5,  0, dc, "Intel Core i*-10000 (Comet Lake-H/S G0)" ),
    // new FMS (    0, 6, 10, 5,  0,     "Intel (unknown type) (Comet Lake-H/S G0)" ),
    // new FMSQ(    0, 6, 10, 5,  1, dc, "Intel Core i*-10000 (Comet Lake-H/S P0)" ),
    // new FMS (    0, 6, 10, 5,  1,     "Intel (unknown type) (Comet Lake-H/S P0)" ),
    // new FMSQ(    0, 6, 10, 5,  2, dc, "Intel Core i*-10000 (Comet Lake-H/S R1)" ),
    // new FMS (    0, 6, 10, 5,  2,     "Intel (unknown type) (Comet Lake-H/S R1)" ),
    // new FMSQ(    0, 6, 10, 5,  3, dc, "Intel Core i*-10000 (Comet Lake-H/S G1)" ),
    // new FMS (    0, 6, 10, 5,  3,     "Intel (unknown type) (Comet Lake-H/S G1)" ),
    // new FMSQ(    0, 6, 10, 5,  4, dc, "Intel Core i*-10000 (Comet Lake-H/S P1)" ),
    // new FMS (    0, 6, 10, 5,  4,     "Intel (unknown type) (Comet Lake-H/S P1)" ),
    // new FMSQ(    0, 6, 10, 5,  5, dc, "Intel Core i*-10000 (Comet Lake-H/S Q0)" ),
    // new FMS (    0, 6, 10, 5,  5,     "Intel (unknown type) (Comet Lake-H/S Q0)" ),
    // new FMQ (    0, 6, 10, 5,     dc, "Intel Core i*-10000 (Comet Lake-H/S)" ),
    // new FM  (    0, 6, 10, 5,         "Intel (unknown type) (Comet Lake-H/S)" ),
    // LX*.  Coreboot* provides more detail & steppings
    // (615213) mentions the (0,6),(10,5),2 and (0,6),(10,5),5 steppings, but
    // does not provide their names.
    // en.wikichip.org provides more details on stepping names.
    new FMSQ(    0, 6, 10, 5,  0, dC, "Intel Celeron 5000 (Comet Lake-H/S G0)"),
    new FMSQ(    0, 6, 10, 5,  0, dP, "Intel Pentium Gold G6400 / G6500 (Comet Lake-H/S G0)"),
    new FMSQ(    0, 6, 10, 5,  0, dc, "Intel Core i*-10000 (Comet Lake-H/S G0)"),
    new FMSQ(    0, 6, 10, 5,  0, sX, "Intel Xeon W-1200 (Comet Lake-H/S G0)"),
    new FMS (    0, 6, 10, 5,  0,     "Intel (unknown type) (Comet Lake-H/S G0)"),
    new FMSQ(    0, 6, 10, 5,  1, dC, "Intel Celeron 5000 (Comet Lake-H/S P0)"),
    new FMSQ(    0, 6, 10, 5,  1, dP, "Intel Pentium Gold G6400 / G6500 (Comet Lake-H/S P0)"),
    new FMSQ(    0, 6, 10, 5,  1, dc, "Intel Core i*-10000 (Comet Lake-H/S P0)"),
    new FMSQ(    0, 6, 10, 5,  1, sX, "Intel Xeon W-1200 (Comet Lake-H/S P0)"),
    new FMS (    0, 6, 10, 5,  1,     "Intel (unknown type) (Comet Lake-H/S P0)"),
    new FMSQ(    0, 6, 10, 5,  2, dC, "Intel Celeron 5000 (Comet Lake-H/S R1)"),
    new FMSQ(    0, 6, 10, 5,  2, dP, "Intel Pentium Gold G6400 / G6500 (Comet Lake-H/S R1)"),
    new FMSQ(    0, 6, 10, 5,  2, dc, "Intel Core i*-10000 (Comet Lake-H/S R1)"),
    new FMSQ(    0, 6, 10, 5,  2, sX, "Intel Xeon W-1200 (Comet Lake-H/S R1)"),
    new FMS (    0, 6, 10, 5,  2,     "Intel (unknown type) (Comet Lake-H/S R1)"),
    new FMSQ(    0, 6, 10, 5,  3, dC, "Intel Celeron 5000 (Comet Lake-H/S G1)"),
    new FMSQ(    0, 6, 10, 5,  3, dP, "Intel Pentium Gold G6400 / G6500 (Comet Lake-H/S G1)"),
    new FMSQ(    0, 6, 10, 5,  3, dc, "Intel Core i*-10000 (Comet Lake-H/S G1)"),
    new FMSQ(    0, 6, 10, 5,  3, sX, "Intel Xeon W-1200 (Comet Lake-H/S G1)"),
    new FMS (    0, 6, 10, 5,  3,     "Intel (unknown type) (Comet Lake-H/S G1)"),
    new FMSQ(    0, 6, 10, 5,  4, dC, "Intel Celeron 5000 (Comet Lake-H/S P1)"),
    new FMSQ(    0, 6, 10, 5,  4, dP, "Intel Pentium Gold G6400 / G6500 (Comet Lake-H/S P1)"),
    new FMSQ(    0, 6, 10, 5,  4, dc, "Intel Core i*-10000 (Comet Lake-H/S P1)"),
    new FMSQ(    0, 6, 10, 5,  4, sX, "Intel Xeon W-1200 (Comet Lake-H/S P1)"),
    new FMS (    0, 6, 10, 5,  4,     "Intel (unknown type) (Comet Lake-H/S P1)"),
    new FMSQ(    0, 6, 10, 5,  5, dC, "Intel Celeron 5000 (Comet Lake-H/S Q0)"),
    new FMSQ(    0, 6, 10, 5,  5, dP, "Intel Pentium Gold G6400 / G6500 (Comet Lake-H/S Q0)"),
    new FMSQ(    0, 6, 10, 5,  5, dc, "Intel Core i*-10000 (Comet Lake-H/S Q0)"),
    new FMSQ(    0, 6, 10, 5,  5, sX, "Intel Xeon W-1200 (Comet Lake-H/S Q0)"),
    new FMS (    0, 6, 10, 5,  5,     "Intel (unknown type) (Comet Lake-H/S Q0)"),
    new FMQ (    0, 6, 10, 5,     dC, "Intel Celeron 5000 (Comet Lake-H/S)"),
    new FMQ (    0, 6, 10, 5,     dP, "Intel Pentium Gold G6400 / G6500 (Comet Lake-H/S)"),
    new FMQ (    0, 6, 10, 5,     dc, "Intel Core i*-10000 (Comet Lake-H/S)"),
    new FMQ (    0, 6, 10, 5,     sX, "Intel Xeon W-1200 (Comet Lake-H/S)"),
    new FM  (    0, 6, 10, 5,         "Intel (unknown type) (Comet Lake-H/S)"),
    // end of changed
    // changed
    // (615213) provides steppings.
    // MRG* 2019-11-13 & instlatx64 example
    // Coreboot* provides steppings.
    // new FMS (    0, 6, 10, 6,  0,     "Intel Core i*-10000 (Comet Lake-U A0)" ),
    // new FMS (    0, 6, 10, 6,  1,     "Intel Core i*-10000 (Comet Lake-U K0/K1/S0)" ),
    // new FMS (    0, 6, 10, 6,  2,     "Intel Core i*-10000 (Comet Lake-H R1)" ),
    // new FMS (    0, 6, 10, 6,  3,     "Intel Core i*-10000 (Comet Lake-S G1)" ),
    // new FMS (    0, 6, 10, 6,  5,     "Intel Core i*-10000 (Comet Lake-S Q0)" ),
    // new FM  (    0, 6, 10, 6,         "Intel Core i*-10000 (Comet Lake)" ),
    // new FM  (    0, 6, 10, 7,         "Intel (unknown type) (Rocket Lake)" ),
    // new FQ  (    0, 6,            sX, "Intel Xeon (unknown model)" ),
    // new FQ  (    0, 6,            se, "Intel Xeon (unknown model)" ),
    // new FQ  (    0, 6,            MC, "Intel Mobile Celeron (unknown model)" ),
    // new FQ  (    0, 6,            dC, "Intel Celeron (unknown model)" ),
    // new FQ  (    0, 6,            Xc, "Intel Core Extreme (unknown model)" ),
    // new FQ  (    0, 6,            Mc, "Intel Mobile Core (unknown model)" ),
    // new FQ  (    0, 6,            dc, "Intel Core (unknown model)" ),
    // new FQ  (    0, 6,            MP, "Intel Mobile Pentium (unknown model)" ),
    // new FQ  (    0, 6,            dP, "Intel Pentium (unknown model)" ),
    // new F   (    0, 6,                "Intel (unknown model)" ),
    // (615213) provides steppings.
    // MRG* 2019-11-13 & instlatx64 example
    // Coreboot* provides steppings.
    new FMS (    0, 6, 10, 6,  0,     "Intel Core i*-10000 (Comet Lake-U A0)"),
    new FMS (    0, 6, 10, 6,  1,     "Intel Core i*-10000 (Comet Lake-U K0/K1/S0)"),
    new FMS (    0, 6, 10, 6,  2,     "Intel Core i*-10000 (Comet Lake-H R1)"),
    new FMS (    0, 6, 10, 6,  3,     "Intel Core i*-10000 (Comet Lake-S G1)"),
    new FMS (    0, 6, 10, 6,  5,     "Intel Core i*-10000 (Comet Lake-S Q0)"),
    new FM  (    0, 6, 10, 6,         "Intel Core i*-10000 (Comet Lake)"),
    new FMQ (    0, 6, 10, 7,     dc, "Intel Core i*-11000 (Rocket Lake)"),
    new FMQ (    0, 6, 10, 7,     sX, "Intel Xeon E-1300 / E-2300G (Rocket Lake)"),
    new FM  (    0, 6, 10, 7,         "Intel (unknown type) (Rocket Lake)"),
    new FM  (    0, 6, 10, 8,         "Intel (unknown type) (Rocket Lake)"),    // undocumented, but (engr?) sample via instlatx64 from Komachi_ENSAKA
    new FMSQ  (  0, 6, 10,10, 4, dU,  "Intel Core Ultra 1xxH (Meteor Lake-M C0)"),
    new FMS   (  0, 6, 10,10, 4,      "Intel (unknown type) (Meteor Lake-M C0)"),
    new FMQ   (  0, 6, 10,10,    dU,  "Intel Core Ultra 1xxH (Meteor Lake-M)"),
    new FM    (  0, 6, 10,10,         "Intel (unknown type) (Meteor Lake-M)"),  // DPTF*; undocumented, but (engr?) sample via instlatx64 from Komachi_ENSAKA
    new FM  (    0, 6, 10,11,         "Intel (unknown type) (Meteor Lake-N)"),  // DPTF*
    new FM  (    0, 6, 10,12,         "Intel (unknown type) (Meteor Lake-S)"),  // undocumented, but (engr?) sample via instlatx64 from Komachi_ENSAKA
    new FM  (    0, 6, 10,13,         "Intel (unknown type) (Granite Rapids)"), // undocumented, but (engr?) sample via instlatx64 from Komachi_ENSAKA
    new FM  (    0, 6, 10,15,         "Intel (unknown type) (Sierra Forest)"),  // undocumented, but (engr?) sample via instlatx64 from Komachi_ENSAKA
    new FMS (    0, 6, 11, 7, 1,      "Intel Core i*-13000 (Raptor Lake)"),
    new FM  (    0, 6, 11, 7,         "Intel (unknown type) (Raptor Lake)"),    // LX*; DPTF* (which also says Raptor Lake-S)
    new FMS (    0, 6, 11,10,2,       "Intel Core i*-13xx (Raptor Lake)"),
    new FMS (    0, 6, 11,10,3,       "Intel Core i*-13xx (Raptor Lake)"),
    new FM  (    0, 6, 11,10,         "Intel (unknown type) (Raptor Lake-P)"),  // LX*; DPTF*
//    
    new FMSQ(    0, 6, 11,14,  0, da, "Intel Atom x7000E (Alder Lake-N A0/N0)"), // ILPMDF* 20230512
    new FMSQ(    0, 6, 11,14,  0, Ia, "Intel Core i*-N300 N-Series E-core (Alder Lake-N A0/N0)"),
    new FMSQ(    0, 6, 11,14,  0, Ic, "Intel Core i*-N300 N-Series P-core (Alder Lake-N A0/N0)"), // possibly no P-cores ever for this model
    new FMSQ(    0, 6, 11,14,  0, Ha, "Intel N-Series E-core (Alder Lake-N A0/N0 / Twin Lake N0)"),
    new FMSQ(    0, 6, 11,14,  0, Hc, "Intel N-Series P-core (Alder Lake-N A0/N0 / Twin Lake N0)"), // possibly no P-cores ever for these model
    new FMS (    0, 6, 11,14,  0,     "Intel N-Series / Atom x7000E (Alder Lake-N A0/N0 / Twin Lake N0)"),
    new FMQ (    0, 6, 11,14,     da, "Intel Atom x7000E (Alder Lake-N)"), // ILPMDF* 20230512
    new FMQ (    0, 6, 11,14,     Ia, "Intel Core i*-N300 N-Series E-core (Alder Lake-N)"),
    new FMQ (    0, 6, 11,14,     Ic, "Intel Core i*-N300 N-Series P-core (Alder Lake-N)"), // possibly no P-cores ever for this model
    new FMQ (    0, 6, 11,14,     Ha, "Intel N-Series E-core (Alder Lake-N / Twin Lake)"),
    new FMQ (    0, 6, 11,14,     Hc, "Intel N-Series P-core (Alder Lake-N / Twin Lake)"), // possibly no P-cores ever for this model
    new FM  (    0, 6, 11,14,         "Intel N-Series / Atom x7000E (Alder Lake-N / Twin Lake)"),
    new FM  (    0, 6, 11,14,         "Intel (unknown type) (Alder Lake)"),     // Coreboot* (or maybe Gracemont "little" cores tied to Alder Lake?) (Alder Lake-N stepping 0=A0, when I'm sure)
//
    new FMS (    0, 6, 11,15,2,       "Intel Core i*-13xx (Raptor Lake)"),
    new FQ  (    0, 6,            sX, "Intel Xeon (unknown model)"),
    new FQ  (    0, 6,            se, "Intel Xeon (unknown model)"),
    new FQ  (    0, 6,            MC, "Intel Mobile Celeron (unknown model)"),
    new FQ  (    0, 6,            dC, "Intel Celeron (unknown model)"),
    new FQ  (    0, 6,            Xc, "Intel Core Extreme (unknown model)"),
    new FQ  (    0, 6,            Mc, "Intel Mobile Core (unknown model)"),
    new FQ  (    0, 6,            dc, "Intel Core (unknown model)"),
    new FQ  (    0, 6,            MP, "Intel Mobile Pentium (unknown model)"),
    new FQ  (    0, 6,            dP, "Intel Pentium (unknown model)"),
    new F   (    0, 6,                "Intel (unknown model)"),
    // end of changed
    // Intel docs (249720).
    new FMS (    0, 7,  0, 0,  6,     "Intel Itanium (Merced C0)" ),
    new FMS (    0, 7,  0, 0,  7,     "Intel Itanium (Merced C1)" ),
    new FMS (    0, 7,  0, 0,  8,     "Intel Itanium (Merced C2)" ),
    new F   (    0, 7,                "Intel Itanium (unknown model)" ),
    new FM  (    0,11,  0, 0,         "Intel Xeon Phi x100 Coprocessor (Knights Ferry)" ), // found only on en.wikichip.org
    // Intel docs (328205).
    new FMS (    0,11,  0, 1,  1,     "Intel Xeon Phi x100 Coprocessor (Knights Corner B0)" ),
    new FMS (    0,11,  0, 1,  3,     "Intel Xeon Phi x100 Coprocessor (Knights Corner B1)" ),
    new FMS (    0,11,  0, 1,  4,     "Intel Xeon Phi x100 Coprocessor (Knights Corner C0)" ),
    new FM  (    0,11,  0, 1,         "Intel Xeon Phi x100 Coprocessor (Knights Corner)" ),
    // Intel docs (249199, 249678).
    new FMS (    0,15,  0, 0,  7,     "Intel Pentium 4 (Willamette B2)" ),
    new FMSQ(    0,15,  0, 0, 10, dP, "Intel Pentium 4 (Willamette C1)" ),
    new FMSQ(    0,15,  0, 0, 10, sX, "Intel Xeon (Foster C1)" ),
    new FMS (    0,15,  0, 0, 10,     "Intel Pentium 4 (unknown type) 4 (Willamette/Foster C1)" ),
    new FMQ (    0,15,  0, 0,     dP, "Intel Pentium 4 (Willamette)" ),
    new FMQ (    0,15,  0, 0,     sX, "Intel Xeon (Foster)" ),
    new FM  (    0,15,  0, 0,         "Intel Pentium 4 (unknown type) (Willamette/Foster)" ),
    // Intel docs (249199, 249678, 290741, 290749).
    new FMS (    0,15,  0, 1,  1,     "Intel Xeon MP (Foster C0)" ),
    new FMSQ(    0,15,  0, 1,  2, dP, "Intel Pentium 4 (Willamette D0)" ),
    new FMSQ(    0,15,  0, 1,  2, sX, "Intel Xeon (Foster D0)" ),
    new FMS (    0,15,  0, 1,  2,     "Intel Pentium 4 (unknown type) (Willamette/Foster D0)" ),
    new FMSQ(    0,15,  0, 1,  3, dP, "Intel Pentium 4(Willamette E0)" ),
    new FMSQ(    0,15,  0, 1,  3, dC, "Intel Celeron 478-pin (Willamette E0)" ),
    new FMS (    0,15,  0, 1,  3,     "Intel Pentium 4 (unknown type) (Willamette/Foster E0)" ),
    new FMQ (    0,15,  0, 1,     dP, "Intel Pentium 4 (Willamette)" ),
    new FMQ (    0,15,  0, 1,     sX, "Intel Xeon (Foster)" ),
    new FM  (    0,15,  0, 1,         "Intel Pentium 4 (unknown type) (Willamette/Foster)" ),
    // Intel docs (249199, 249678, 250721, 251309, 253176, 290741, 290749).
    new FMS (    0,15,  0, 2,  2,     "Intel Xeon MP (Gallatin A0)" ),
    new FMSQ(    0,15,  0, 2,  4, sX, "Intel Xeon (Prestonia B0)" ),
    new FMSQ(    0,15,  0, 2,  4, MM, "Intel Mobile Pentium 4 Processor-M (Northwood B0)" ),
    new FMSQ(    0,15,  0, 2,  4, MC, "Intel Mobile Celeron (Northwood B0)" ),
    new FMSQ(    0,15,  0, 2,  4, dP, "Intel Pentium 4 (Northwood B0)" ),
    new FMS (    0,15,  0, 2,  4,     "Intel Pentium 4 (unknown type) (Northwood/Prestonia B0)" ),
    new FMSQ(    0,15,  0, 2,  5, dP, "Intel Pentium 4 (Northwood B1/M0)" ),
    new FMSQ(    0,15,  0, 2,  5, sM, "Intel Xeon MP (Gallatin B1)" ),
    new FMSQ(    0,15,  0, 2,  5, sX, "Intel Xeon (Prestonia B1)" ),
    new FMS (    0,15,  0, 2,  5,     "Intel Pentium 4 (unknown type) (Northwood/Prestonia/Gallatin B1/M0)" ),
    new FMS (    0,15,  0, 2,  6,     "Intel Xeon MP (Gallatin C0)" ),
    new FMSQ(    0,15,  0, 2,  7, sX, "Intel Xeon (Prestonia C1)" ),
    new FMSQ(    0,15,  0, 2,  7, dC, "Intel Celeron 478-pin (Northwood C1)" ),
    new FMSQ(    0,15,  0, 2,  7, MC, "Intel Mobile Celeron (Northwood C1)" ),
    new FMSQ(    0,15,  0, 2,  7, MM, "Intel Mobile Pentium 4 Processor-M (Northwood C1)" ),
    new FMSQ(    0,15,  0, 2,  7, dP, "Intel Pentium 4 (Northwood C1)" ),
    new FMS (    0,15,  0, 2,  7,     "Intel Pentium 4 (unknown type) (Northwood/Prestonia C1)" ),
    new FMSQ(    0,15,  0, 2,  9, sX, "Intel Xeon (Prestonia D1)" ),
    new FMSQ(    0,15,  0, 2,  9, dC, "Intel Celeron 478-pin (Northwood D1)" ),
    new FMSQ(    0,15,  0, 2,  9, MC, "Intel Mobile Celeron (Northwood D1)" ),
    new FMSQ(    0,15,  0, 2,  9, MM, "Intel Mobile Pentium 4 Processor-M (Northwood D1)" ),
    new FMSQ(    0,15,  0, 2,  9, MP, "Intel Mobile Pentium 4 (Northwood D1)" ),
    new FMSQ(    0,15,  0, 2,  9, dP, "Intel Pentium 4 (Northwood D1)" ),
    new FMS (    0,15,  0, 2,  9,     "Intel Pentium 4 (unknown type) (Northwood/Prestonia D1)" ),
    new FMQ (    0,15,  0, 2,     dP, "Intel Pentium 4 (Northwood)" ),
    new FMQ (    0,15,  0, 2,     sM, "Intel Xeon MP (Gallatin)" ),
    new FMQ (    0,15,  0, 2,     sX, "Intel Xeon (Prestonia)" ),
    new FM  (    0,15,  0, 2,         "Intel Pentium 4 (unknown type) (Northwood/Prestonia/Gallatin)" ),
    // Intel docs (302352, 302402, 302403, 302441).
    new FMSQ(    0,15,  0, 3,  3, dP, "Intel Pentium 4 (Prescott C0)" ),
    new FMSQ(    0,15,  0, 3,  3, dC, "Intel Celeron D (Prescott C0)" ),
    new FMS (    0,15,  0, 3,  3,     "Intel Pentium 4 (unknown type) (Prescott C0)" ),
    new FMSQ(    0,15,  0, 3,  4, sX, "Intel Xeon (Nocona D0)" ),
    new FMSQ(    0,15,  0, 3,  4, dC, "Intel Celeron D (Prescott D0)" ),
    new FMSQ(    0,15,  0, 3,  4, MP, "Intel Mobile Pentium 4 (Prescott D0)" ),
    new FMSQ(    0,15,  0, 3,  4, dP, "Intel Pentium 4 (Prescott D0)" ),
    new FMS (    0,15,  0, 3,  4,     "Intel Pentium 4 (unknown type) (Prescott/Nocona D0)" ),
    new FMQ (    0,15,  0, 3,     sX, "Intel Xeon (Nocona)" ),
    new FMQ(     0,15,  0, 3,     dC, "Intel Celeron D (Prescott)" ),
    new FMQ (    0,15,  0, 3,     MP, "Intel Mobile Pentium 4 (Prescott)" ),
    new FMQ (    0,15,  0, 3,     dP, "Intel Pentium 4 (Prescott)" ),
    new FM  (    0,15,  0, 3,         "Intel Pentium 4 (unknown type) (Prescott/Nocona)" ),
    // Intel docs (302354, 306752, 306757, 306832, 309159, 309627).
    new FMSQ(    0,15,  0, 4,  1, sP, "Intel Xeon MP (Potomac C0)" ),
    new FMSQ(    0,15,  0, 4,  1, sM, "Intel Xeon MP (Cranford A0)" ),
    new FMSQ(    0,15,  0, 4,  1, sX, "Intel Xeon (Nocona E0)" ),
    new FMSQ(    0,15,  0, 4,  1, dC, "Intel Celeron D (Prescott E0)" ),
    new FMSQ(    0,15,  0, 4,  1, MP, "Intel Mobile Pentium 4 (Prescott E0)" ),
    new FMSQ(    0,15,  0, 4,  1, dP, "Intel Pentium 4 (Prescott E0)" ),
    new FMS (    0,15,  0, 4,  1,     "Intel Pentium 4 (unknown type) (Prescott/Nocona/Cranford/Potomac E0)" ),
    new FMSQ(    0,15,  0, 4,  3, sI, "Intel Xeon (Irwindale N0)" ),
    new FMSQ(    0,15,  0, 4,  3, sX, "Intel Xeon (Nocona N0)" ),
    new FMSQ(    0,15,  0, 4,  3, dP, "Intel Pentium 4 (Prescott N0)" ),
    new FMS (    0,15,  0, 4,  3,     "Intel Pentium 4 (unknown type) (Prescott/Nocona/Irwindale N0)" ),
    new FMSQ(    0,15,  0, 4,  4, dd, "Intel Pentium D Processor 8x0 (Smithfield A0)" ),
    new FMSQ(    0,15,  0, 4,  4, dG, "Intel Pentium Extreme Edition Processor 840 (Smithfield A0)" ),
    new FMS (    0,15,  0, 4,  4,     "Intel Pentium D (unknown type) (Smithfield A0)" ),
    new FMSQ(    0,15,  0, 4,  7, dd, "Intel Pentium D Processor 8x0 (Smithfield B0)" ),
    new FMSQ(    0,15,  0, 4,  7, dG, "Pentium Extreme Edition Processor 840 (Smithfield B0)" ),
    new FMS (    0,15,  0, 4,  7,     "Intel Pentium D (unknown type) (Smithfield B0)" ),
    new FMSQ(    0,15,  0, 4,  8, s7, "Intel Dual-Core Xeon Processor 7000 (Paxville A0)" ),
    new FMSQ(    0,15,  0, 4,  8, sX, "Intel Dual-Core Xeon (Paxville A0)" ),
    new FMS (    0,15,  0, 4,  8,     "Intel Dual-Core Xeon (unknown type) (Paxville A0)" ),
    new FMSQ(    0,15,  0, 4,  9, sM, "Intel Xeon MP (Cranford B0)" ),
    new FMSQ(    0,15,  0, 4,  9, dC, "Intel Celeron D (Prescott G1)" ),
    new FMSQ(    0,15,  0, 4,  9, dP, "Intel Pentium 4 (Prescott G1)" ),
    new FMS (    0,15,  0, 4,  9,     "Intel Pentium 4 (unknown type) (Prescott/Cranford G1)" ),
    new FMSQ(    0,15,  0, 4, 10, sI, "Intel Xeon (Irwindale R0)" ),
    new FMSQ(    0,15,  0, 4, 10, sX, "Intel Xeon (Nocona R0)" ),
    new FMSQ(    0,15,  0, 4, 10, dP, "Intel Pentium 4 (Prescott R0)" ),
    new FMS (    0,15,  0, 4, 10,     "Intel Pentium 4 (unknown type) (Prescott/Nocona/Irwindale R0)" ),
    new FMQ (    0,15,  0, 4,     sM, "Intel Xeon MP (Nocona/Potomac)" ),
    new FMQ (    0,15,  0, 4,     sX, "Intel Xeon (Nocona/Irwindale)" ),
    new FMQ (    0,15,  0, 4,     dC, "Intel Celeron D (Prescott)" ),
    new FMQ (    0,15,  0, 4,     MP, "Intel Mobile Pentium 4 (Prescott)" ),
    new FMQ (    0,15,  0, 4,     dd, "Intel Pentium D (Smithfield A0)" ),
    new FMQ (    0,15,  0, 4,     dP, "Intel Pentium 4 (Prescott) / Pentium Extreme Edition (Smithfield A0)" ),
    new FM  (    0,15,  0, 4,         "Intel Pentium 4 (unknown type) (Prescott/Nocona/Irwindale/Smithfield/Cranford/Potomac)" ),
    // Intel docs (310307, 310309, 311827, 313065, 314554).
    new FMSQ(    0,15,  0, 6,  2, dd, "Intel Pentium D Processor 9xx (Presler B1)" ),
    new FMSQ(    0,15,  0, 6,  2, dP, "Intel Pentium 4 Processor 6x1 (Cedar Mill B1) / Pentium Extreme Edition Processor 955 (Presler B1)" ),
    new FMS (    0,15,  0, 6,  2,     "Intel Pentium 4 (unknown type) (Cedar Mill/Presler B1)" ),
    new FMSQ(    0,15,  0, 6,  4, dd, "Intel Pentium D Processor 9xx (Presler C1)" ),
    new FMSQ(    0,15,  0, 6,  4, dP, "Intel Pentium 4 Processor 6x1 (Cedar Mill C1) / Pentium Extreme Edition Processor 955 (Presler C1)" ),
    new FMSQ(    0,15,  0, 6,  4, dC, "Intel Celeron D Processor 34x/35x (Cedar Mill C1)" ),
    new FMSQ(    0,15,  0, 6,  4, sX, "Intel Xeon Processor 5000 (Dempsey C1)" ),
    new FMS (    0,15,  0, 6,  4,     "Intel Pentium 4 (unknown type) (Cedar Mill/Presler/Dempsey C1)" ),
    new FMSQ(    0,15,  0, 6,  5, dC, "Intel Celeron D Processor 36x (Cedar Mill D0)" ),
    new FMSQ(    0,15,  0, 6,  5, dd, "Intel Pentium D Processor 9xx (Presler D0)" ),
    new FMSQ(    0,15,  0, 6,  5, dP, "Intel Pentium 4 Processor 6x1 (Cedar Mill D0) / Pentium Extreme Edition Processor 955 (Presler D0)" ),
    new FMS (    0,15,  0, 6,  5,     "Intel Pentium 4 (unknown type) (Cedar Mill/Presler D0)" ),
    new FMS (    0,15,  0, 6,  8,     "Intel Xeon Processor 71x0 (Tulsa B0)" ),
    new FMQ (    0,15,  0, 6,     dd, "Intel Pentium D (Presler)" ),
    new FMQ (    0,15,  0, 6,     dP, "Intel Pentium 4 (Cedar Mill) / Pentium Extreme Edition (Presler)" ),
    new FMQ (    0,15,  0, 6,     dC, "Intel Celeron D (Cedar Mill)" ),
    new FMQ (    0,15,  0, 6,     sX, "Intel Xeon (Dempsey / Tulsa)" ),
    new FM  (    0,15,  0, 6,         "Intel Pentium 4 (unknown type) (Cedar Mill/Presler/Dempsey/Tulsa)" ),
    new FQ  (    0,15,            sM, "Intel Xeon MP (unknown model)" ),
    new FQ  (    0,15,            sX, "Intel Xeon (unknown model)" ),
    new FQ  (    0,15,            MC, "Intel Mobile Celeron (unknown model)" ),
    new FQ  (    0,15,            MC, "Intel Mobile Pentium 4 (unknown model)" ),
    new FQ  (    0,15,            MM, "Intel Mobile Pentium 4 Processor-M (unknown model)" ),
    new FQ  (    0,15,            dC, "Intel Celeron (unknown model)" ),
    new FQ  (    0,15,            dd, "Intel Pentium D (unknown model)" ),
    new FQ  (    0,15,            dP, "Intel Pentium 4 (unknown model)" ),
    new FQ  (    0,15,            dc, "Intel Pentium (unknown model)" ),
    new F   (    0,15,                "Intel Pentium 4 / Pentium D / Xeon / Xeon MP / Celeron / Celeron D (unknown model)" ),
    // NOTE: Intel spec updates describe CPUID in Itanium Register 3 format:
    //    AAFFMMSSNN
    //    AA = archrev (not reported via CPUID instruction)
    //    FF = family
    //    MM = model
    //    SS = revision/stepping
    //    NN = number (not reported via CPUID instruction)
    // Intel docs (251141).
    new FMS (    1,15,  0, 0,  7,     "Intel Itanium2 (McKinley B3)" ),
    new FM  (    1,15,  0, 0,         "Intel Itanium2 (McKinley)" ),
    // Intel docs (251141).
    new FMS (    1,15,  0, 1,  5,     "Intel Itanium2 (Madison/Deerfield/Hondo B1)" ),
    new FM  (    1,15,  0, 1,         "Intel Itanium2 (Madison/Deerfield/Hondo)" ),
    // Intel docs (251141).
    new FMS (    1,15,  0, 2,  1,     "Intel Itanium2 (Madison 9M/Fanwood A1)" ),
    new FMS (    1,15,  0, 2,  2,     "Intel Itanium2 (Madison 9M/Fanwood A2)" ),
    new FM  (    1,15,  0, 2,         "Intel Itanium2 (Madison)" ),
    new F   (    1,15,                "Intel Itanium2 (unknown model)" ),
    // Intel docs (251141).
    new FMS (    2, 0,  0, 0,  5,     "Intel Itanium2 Dual-Core Processor 9000 (Montecito/Millington C1), 90nm" ),
    new FMS (    2, 0,  0, 0,  7,     "Intel Itanium2 Dual-Core Processor 9000 (Montecito/Millington C2), 90nm" ),
    new FM  (    2, 0,  0, 0,         "Intel Itanium2 Dual-Core Processor 9000 (Montecito/Millington), 90nm" ),
    // Intel docs (251141).
    new FMS (    2, 0,  0, 1,  1,     "Intel Itanium2 Dual-Core Processor 9100 (Montvale A1), 90nm" ),
    new FM  (    2, 0,  0, 1,         "Intel Itanium2 Dual-Core Processor 9100 (Montvale), 90nm" ),
    // Intel docs (323169).
    new FMS (    2, 0,  0, 2,  4,     "Intel Itanium2 Processor 9300 (Tukwila E0), 65nm" ),
    new FM  (    2, 0,  0, 2,         "Intel Itanium2 Processor 9300 (Tukwila), 65nm" ),
    new F   (    2, 0,                "Intel Itanium2 (unknown model)" ),
    // Intel docs (323169).
    new FMS (    2, 1,  0, 0,  4,     "Intel Itanium2 Processor 9500 (Poulson D0), 32nm" ),
    new FMS (    2, 1,  0, 0,  5,     "Intel Itanium2 Processor 9700 (Kittson E0), 22nm" ),
    new FM  (    2, 1,  0, 0,         "Intel Itanium2 (unknown model) (Poulson/Kittson)" ),
    new F   (    2, 1,                "Intel Itanium2 (unknown model)" ) };
    
    String s1 = detectorHelper( stdTfms, bi, INTEL_DATA );
    return new String[] { s1, null };
    }
}
