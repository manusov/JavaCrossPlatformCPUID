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

Processor microarchitecture and physical parameters detection 
by signature and additional flags, for Intel processors. 
tfms = Standard Type, Family, Model, Stepping, 
       CPUID standard function 00000001h, register EAX
bi   = Brand Index, CPUID function 00000001h, register EBX,
       only bits[7-0] must be selected by AND mask inside 
       CriteriaDescriptor.detector() method called by detectorHelper.

Intel calls "Whiskey Lake", "Amber Lake", and "Comet Lake" distinct
uarch's optimized from "Kaby Lake".  That just leads to confusion and long
uarch names with slashes.  Their families & models overlap, and just
differ based on brand (based on target market):
  (0,6),(8,14),9  = Kaby Lake      -or- Amber Lake-Y
  (0,6),(8,14),11 = Whiskey Lake-U -or- Amber Lake-Y
  (0,6),(8,14),12 = Whiskey Lake-U -or- Amber Lake-Y -or- Comet Lake-U
If the only way to distinguish two uarch's is by brand, I am skeptical
that they really are distinct uarch's!  This is analogous to the multitude
of core names in pre-Sandy Bridge days.  So I am treating those 3 as
distinct cores within the "Kaby Lake" uarch.  This reduces the number of
uarches in the Skylake-based era to:

  [Skylake]      = lead uarch in {Skylake} family
  [Cascade Lake] = Skylake + DL Boost + spectre/meltdown fixes
  [Kaby Lake]    = Skylake, 14nm+ (includes Whiskey, Amber, Comet)
  [Coffee Lake]  = Kaby Lake, 14nm++, 1.5x CPUs/die
  [Palm Cove]    = Coffee Lake, 10nm, AVX-512

That is a more manageable set.
NOTE: Ice Lake & Tiger Lake cores are in the separate Sunny Cove uarch.

*/

package cpuidv3.servicecpudata;

class IntelMicroarchitecture extends Microarchitecture
{

IntelMicroarchitecture( DatabaseStash stash )
    {
    super( stash );
    }
    
@Override MData detect( int tfms, int bi )
    {
    boolean mt = stash.br.montage;
    boolean Hc = stash.bigCore;
    boolean Ha = stash.smallCore;
    boolean Hz = stash.zeroCore;
    
    final CriteriaDescriptor[] INTEL_MICROARCHITECTURE = {
    new F   ( 0, 4,            ()-> { f = "i486"; } ), // p depends on core
    new FM  ( 0, 5, 0, 0,      ()-> { f = "P5"; p = ".8um"; } ),
    new FM  ( 0, 5, 0, 1,      ()-> { f = "P5"; p = ".8um"; } ),
    new FM  ( 0, 5, 0, 2,      ()-> { f = "P5"; } ),
    new FM  ( 0, 5, 0, 3,      ()-> { f = "P5"; p = ".6um"; } ),
    new FM  ( 0, 5, 0, 4,      ()-> { f = "P5 MMX"; } ),
    new FM  ( 0, 5, 0, 7,      ()-> { f = "P5 MMX"; } ),
    new FM  ( 0, 5, 0, 8,      ()-> { f = "P5 MMX"; p = ".25um"; } ),
    new FM  ( 0, 5, 0, 9,      ()-> { f = "P5 MMX"; } ),
    new FM  ( 0, 6, 0, 0,      ()-> { f = "P6 Pentium II"; } ),
    new FM  ( 0, 6, 0, 1,      ()-> { f = "P6 Pentium II"; } ), // p depends on core
    new FM  ( 0, 6, 0, 2,      ()-> { f = "P6 Pentium II"; } ),
    new FM  ( 0, 6, 0, 3,      ()-> { f = "P6 Pentium II"; p = ".35um"; } ),
    new FM  ( 0, 6, 0, 4,      ()-> { f = "P6 Pentium II"; } ),
    new FM  ( 0, 6, 0, 5,      ()-> { f = "P6 Pentium II"; p = ".25um"; } ),
    new FM  ( 0, 6, 0, 6,      ()-> { f = "P6 Pentium II"; p = "L2 cache"; } ),
    new FM  ( 0, 6, 0, 7,      ()-> { f = "P6 Pentium III"; p = ".25um"; } ),
    new FM  ( 0, 6, 0, 8,      ()-> { f = "P6 Pentium III"; p = ".18um"; } ),
    new FM  ( 0, 6, 0, 9,      ()-> { f = "P6 Pentium M"; p = ".13um"; } ),
    new FM  ( 0, 6, 0, 10,     ()-> { f = "P6 Pentium III"; p = ".18um"; } ),
    new FM  ( 0, 6, 0, 11,     ()-> { f = "P6 Pentium III"; p = ".13um"; } ),
    new FM  ( 0, 6, 0, 13,     ()-> { u = "Dothan"; f = "P6 Pentium M"; } ),  // p depends on core
    new FM  ( 0, 6, 0, 14,     ()-> { u = "Yonah"; f = "P6 Pentium M"; p = "65nm"; } ),
    new FM  ( 0, 6, 0, 15,     ()-> { u = "Merom"; f = "Core"; p = "65nm"; } ),
    new FM  ( 0, 6, 1, 5,      ()-> { u = "Dothan"; f = "P6 Pentium M"; p = "90nm"; } ),
    new FM  ( 0, 6, 1, 6,      ()-> { u = "Merom"; f = "Core"; p = "65nm"; } ),
    new FM  ( 0, 6, 1, 7,      ()-> { u = "Penryn"; f = "Core"; p = "45nm"; } ),
    new FM  ( 0, 6, 1, 10,     ()-> { u = "Nehalem"; f = "Nehalem"; p = "45nm"; } ),
    new FM  ( 0, 6, 1, 12,     ()-> { u = "Bonnell"; p = "45nm"; } ),
    new FM  ( 0, 6, 1, 13,     ()-> { u = "Penryn"; f = "Core"; p = "45nm"; } ),
    new FM  ( 0, 6, 1, 14,     ()-> { u = "Nehalem"; f = "Nehalem"; p = "45nm"; } ),
    new FM  ( 0, 6, 1, 15,     ()-> { u = "Nehalem"; f = "Nehalem"; p = "45nm"; } ),
    new FM  ( 0, 6, 2, 5,      ()-> { u = "Westmere"; f = "shrink of Nehalem"; p = "32nm"; } ),
    new FM  ( 0, 6, 2, 6,      ()-> { u = "Bonnell"; p = "45nm"; } ),
    new FM  ( 0, 6, 2, 7,      ()-> { u = "Saltwell"; p = "32nm"; } ),
    new FM  ( 0, 6, 2, 10,     ()-> { u = "Sandy Bridge"; c = true; f = "Sandy Bridge"; p = "32nm"; } ),
    new FM  ( 0, 6, 2, 12,     ()-> { u = "Westmere"; f = "shrink of Nehalem"; p = "32nm"; } ),
    new FM  ( 0, 6, 2, 13,     ()-> { u = "Sandy Bridge"; c = true; f = "Sandy Bridge"; p = "32nm"; } ),
    new FM  ( 0, 6, 2, 14,     ()-> { u = "Nehalem"; f = "Nehalem"; p = "45nm"; } ),
    new FM  ( 0, 6, 2, 15,     ()-> { u = "Westmere"; f = "Nehalem"; p = "32nm"; } ),
    new FM  ( 0, 6, 3, 5,      ()-> { u = "Saltwell"; p = "14nm"; } ),
    new FM  ( 0, 6, 3, 6,      ()-> { u = "Saltwell"; p = "32nm"; } ),
    new FM  ( 0, 6, 3, 7,      ()-> { u = "Silvermont"; p = "22nm"; } ),
    new FM  ( 0, 6, 3, 10,     ()-> { u = "Ivy Bridge"; c = true; f = "shrink of Sandy Bridge"; p = "22nm"; } ),
    new FM  ( 0, 6, 3, 12,     ()-> { u = "Haswell"; c = true; f = "Haswell"; p = "22nm"; } ),
    new FM  ( 0, 6, 3, 13,     ()-> { u = "Broadwell"; c = true; f = "shrink of Haswell"; p = "14nm"; } ),
    new FM  ( 0, 6, 3, 14,     ()-> { u = "Ivy Bridge"; c = true; f = "shrink of Sandy Bridge"; p = "22nm"; } ),
    new FM  ( 0, 6, 3, 15,     ()-> { u = "Haswell"; c = true; f = "Haswell"; p = "22nm"; } ),
    new FM  ( 0, 6, 4, 5,      ()-> { u = "Haswell"; c = true; f = "Haswell"; p = "22nm"; } ),
    new FM  ( 0, 6, 4, 6,      ()-> { u = "Haswell"; c = true; f = "Haswell"; p = "22nm"; } ),
    new FM  ( 0, 6, 4, 7,      ()-> { u = "Broadwell"; c = true; f = "shrink of Haswell"; p = "14nm"; } ),
    new FM  ( 0, 6, 4, 10,     ()-> { u = "Silvermont"; p = "22nm"; } ), // no docs, but /proc/cpuinfo seen in wild
    new FM  ( 0, 6, 4, 12,     ()-> { u = "Airmont"; p = "14nm"; } ),
    new FM  ( 0, 6, 4, 13,     ()-> { u = "Silvermont"; p = "22nm"; } ),
    new FMS ( 0, 6, 4, 14, 8,  ()-> { u = "Kaby Lake"; f = "optim of Skylake"; p = "14nm+"; } ),
    new FM  ( 0, 6, 4, 14,     ()-> { u = "Skylake"; c = true; f = "Skylake"; p = "14nm"; } ),
    new FM  ( 0, 6, 4, 15,     ()-> { u = "Broadwell"; c = true; f = "shrink of Haswell"; p = "14nm"; } ),
    new FMQ ( 0, 6, 5, 5, mt,  ()-> { u = "Jintide Gen1"; c = true; f = "Skylake"; } ),      // undocumented; only instlatx64 example
    new FMS ( 0, 6, 5, 5, 6,   ()-> { u = "Cascade Lake"; c = true; f = "optim of Skylake"; p = "14nm++"; } ), // no docs, but example from Greg Stewart
    new FMS ( 0, 6, 5, 5, 7,   ()-> { u = "Cascade Lake"; c = true; f = "optim of Skylake"; p = "14nm++"; } ),
    new FMS ( 0, 6, 5, 5, 10,  ()-> { u = "Cooper Lake"; c = true; f = "optim of Skylake"; p = "14nm++"; } ),
    new FMS ( 0, 6, 5, 5, 11,  ()-> { u = "Cooper Lake"; c = true; f = "optim of Skylake"; p = "14nm++"; } ),
    new FM  ( 0, 6, 5, 5,      ()-> { u = "Skylake"; c = true; f = "Skylake"; p = "14nm"; } ),
    new FM  ( 0, 6, 5, 6,      ()-> { u = "Broadwell"; c = true; f = "shrink of Haswell"; p = "14nm"; } ),
    new FM  ( 0, 6, 5, 7,      ()-> { u = "Knights Landing"; c = true; p = "14nm"; } ),
    new FM  ( 0, 6, 5, 10,     ()-> { u = "Silvermont"; p = "22nm"; } ), // no spec update; only MSR_CPUID_table* so far
    new FM  ( 0, 6, 5, 12,     ()-> { u = "Goldmont"; p = "14nm"; } ), // no spec update for Atom; only MSR_CPUID_table* so far
    new FM  ( 0, 6, 5, 13,     ()-> { u = "Silvermont"; p = "22nm"; } ), // no spec update; only MSR_CPUID_table* so far
    new FMS ( 0, 6, 5, 14, 8,  ()-> { u = "Kaby Lake"; f = "optim of Skylake"; p = "14nm"; } ),
    new FM  ( 0, 6, 5, 14,     ()-> { u = "Skylake"; c = true; f = "Skylake"; p = "14nm"; } ),
    new FM  ( 0, 6, 5, 15,     ()-> { u = "Goldmont"; p = "14nm"; } ),
    new FM  ( 0, 6, 6, 6,      ()-> { u = "Palm Cove"; f = "Palm Cove"; p = "10nm"; } ), // no spec update; only MSR_CPUID_table* so far
    new FM  ( 0, 6,  6, 7,     ()-> { u = "Palm Cove"; f = "Palm Cove"; p = "10nm"; } ), // DPTF*
    new FM  ( 0, 6, 6, 10,     ()-> { u = "Sunny Cove"; f = "Sunny Cove"; p = "10nm+"; } ), // no spec update; only MSR_CPUID_table* so far
    new FM  ( 0, 6, 6, 12,     ()-> { u = "Sunny Cove"; f = "Sunny Cove"; p = "10nm+"; } ), // no spec update; only MSR_CPUID_table* so far
    new FM  ( 0, 6, 6, 14,     ()-> { u = "Airmont"; p = "14nm"; } ), // no spec update; only Intel's "Retpoline: A Branch Target Injection Mitigation"
    new FM  ( 0, 6, 7, 5,      ()-> { u = "Airmont"; p = "14nm"; } ), // no spec update; whispers & rumors
    new FM  ( 0, 6, 7, 10,     ()-> { u = "Goldmont Plus"; p = "14nm"; } ),
    new FM  ( 0, 6, 7, 13,     ()-> { u = "Sunny Cove"; f = "Sunny Cove"; p = "10nm+"; } ), // no spec update; only MSR_CPUID_table* so far
    new FM  ( 0, 6, 7, 14,     ()-> { u = "Sunny Cove"; f = "Sunny Cove"; p = "10nm+"; } ),
    new FM  ( 0, 6, 8, 5,      ()-> { u = "Knights Mill"; c = true; p = "14nm"; } ), // no spec update; only MSR_CPUID_table* so far
    new FM  ( 0, 6, 8, 6,      ()-> { u = "Tremont"; p = "10nm"; } ), // LX*
    new FMQ ( 0, 6, 8, 10, Ha, ()-> { u = "Tremont"; p = "10nm"; } ), // no spec update; LX*
    new FMQ ( 0, 6, 8, 10, Hc, ()-> { u = "Sunny cove"; p = "10nm"; } ), // no spec update; LX*
    new FM  ( 0, 6, 8, 12,     ()-> { u = "Willow Cove"; f = "optim of Sunny Cove"; p = "10nm++"; } ), // found only on en.wikichip.org
    new FM  ( 0, 6, 8, 13,     ()-> { u = "Willow Cove"; f = "optim of Sunny Cove"; p = "10nm++"; } ), // LX*
    new FM  ( 0, 6, 8, 14,     ()-> { u = "Kaby Lake"; f = "optim of Skylake"; p = "14nm+/14nm++"; } ),
    new FM  ( 0, 6, 8, 15,     ()-> { u = "Sapphire Rapids"; c = true; f = "Golden Cove"; p = "Intel 7"; } ), // LX*
    new FM  ( 0, 6, 9, 5,      ()-> { u = "Sapphire Rapids"; c = true; f = "Golden Cove"; p = "Intel 7"; } ), // Intel SDE 9.24.0 misc/cpuid/spr/cpuid.def
    new FM  ( 0, 6, 9, 6,      ()-> { u = "Tremont"; p = "10nm"; } ), // LX*
    new FMQ ( 0, 6, 9, 7,  Ha, ()-> { u = "Gracemont"; p = "Intel 7"; } ),
    new FMQ ( 0, 6, 9, 7,  Hc, ()-> { u = "Golden Cove"; p = "Intel 7"; } ),
    new FMQ ( 0, 6, 9, 7,  Hz, ()-> { u = "Golden Cove"; p = "Intel 7"; } ),
    new FMQ ( 0, 6, 9,10,  Ha, ()-> { u = "Gracemont"; p = "Intel 7"; } ), // Coreboot*
    new FMQ ( 0, 6, 9,10,  Hc, ()-> { u = "Golden Cove"; p = "Intel 7"; } ), // Coreboot*
    new FM  ( 0, 6, 9, 12,     ()-> { u = "Tremont"; p = "10nm"; } ), // LX*
    new FM  ( 0, 6, 9, 13,     ()-> { u = "Sunny Cove"; f = "Sunny Cove"; p = "10nm+"; } ), // LX*
    new FMS ( 0, 6, 9, 14, 9,  ()-> { u = "Kaby Lake"; f = "optim of Skylake"; p = "14nm++"; } ),
    new FMS ( 0, 6, 9, 14, 10, ()-> { u = "Coffee Lake"; c = true; f = "optim of Skylake"; p = "14nm++"; } ),
    new FMS ( 0, 6, 9, 14, 11, ()-> { u = "Coffee Lake"; c = true; f = "optim of Skylake"; p = "14nm++"; } ),
    new FMS ( 0, 6, 9, 14, 12, ()-> { u = "Coffee Lake"; c = true; f = "optim of Skylake"; p = "14nm++"; } ),
    new FMS ( 0, 6, 9, 14, 13, ()-> { u = "Coffee Lake"; c = true; f = "optim of Skylake"; p = "14nm++"; } ),
    new FM  ( 0, 6, 9, 14,     ()-> { u = "Kaby Lake / Coffee Lake"; f = "optim of Skylake"; p = "14nm+/14nm++"; } ),
    new FM  ( 0, 6, 10, 5,     ()-> { u = "Kaby Lake"; f = "Skylake"; p = "14nm"; } ), // LX*
    new FM  ( 0, 6, 9, 15,     ()-> { u = "Sunny Cove"; f = "Sunny Cove"; p = "10nm+"; } ), // undocumented, but (engr?) sample via instlatx64 from Komachi_ENSAKA
    new FM  ( 0, 6, 10, 5,     ()-> { u = "Kaby Lake"; f = "optim of Skylake"; p = "14nm+++"; } ), // LX*
    new FM  ( 0, 6, 10, 6,     ()-> { u = "Kaby Lake"; f = "optim Skylake"; p = "14nm+++"; } ), // no spec update; only instlatx64 example
    new FM  ( 0, 6, 10, 7,     ()-> { u = "Cypress Cove"; f = "backport of Sunny Cove"; p = "14nm+++"; } ), // LX*
    new FM  ( 0, 6, 10, 8,     ()-> { u = "Cypress Cove"; f = "backport of Sunny Cove"; p = "14nm+++"; } ), // undocumented, but (engr?) sample via instlatx64 from Komachi_ENSAKA
    new FMQ ( 0, 6, 10,10, Ha, ()-> { u = "Crestmont"; p = "Intel 4"; } ), // MSR_CPUID_table*; DPTF*, LX* (but -L); (engr?) sample via instlatx64 from Komachi_ENSAKA
    new FMQ ( 0, 6, 10,10, Hc, ()-> { u = "Redwood Cove"; f = "shrink of Raptor Cove, optim of Golden Cove"; p = "Intel 4"; } ), // MSR_CPUID_table*; DPTF*, LX* (but -L); (engr?) sample via instlatx64 from Komachi_ENSAKA
    new FMQ ( 0, 6, 10,11, Ha, ()-> { u = "Crestmont"; p = "Intel 4"; } ), // DPTF*
    new FMQ ( 0, 6, 10,11, Hc, ()-> { u = "Redwood Cove"; f = "shrink of Raptor Cove"; p = "Intel 4"; } ), // DPTF*
    new FMQ ( 0, 6, 10,12, Ha, ()-> { u = "Crestmont"; p = "Intel 4"; } ), // MSR_CPUID_table*; (engr?) sample via instlatx64 from Komachi_ENSAKA
    new FMQ ( 0, 6, 10,12, Hc, ()-> { u = "Redwood Cove"; f = "shrink of Raptor Cove"; p = "Intel 4"; } ), // MSR_CPUID_table*; (engr?) sample via instlatx64 from Komachi_ENSAKA
    new FM  ( 0, 6, 10,13,     ()-> { u = "Granite Rapids"; c = true; f = "Redwood Cove"; p = "Intel 4"; } ), // MSR_CPUID_table*; LX*; (engr?) sample via instlatx64 from Komachi_ENSAKA
    new FM  ( 0, 6, 10,14,     ()-> { u = "Granite Rapids"; c = true; f = "Redwood Cove"; p = "Intel 4"; } ), // MSR_CPUID_table*; LX*
    new FM  ( 0, 6, 10,15,     ()-> { u = "Sierra Forest"; p = "Intel 3"; } ), // MSR_CPUID_table*; LX*; (engr?) sample via instlatx64 from Komachi_ENSAKA
    new FMQ ( 0, 6, 11, 5, Ha, ()-> { u = "Skymont"; p = "TSMC N3B"; } ), // MSR_CPUID_table*
    new FMQ ( 0, 6, 11, 5, Hc, ()-> { u = "Lion Cove"; p = "TSMC N3B"; } ), // MSR_CPUID_table*
    new FM  ( 0, 6, 11, 6,     ()-> { u = "Crestmont"; p = "Intel 7"; } ),  // MSR_CPUID_table*; LX*; (although assumption that Grand Ridge is Crestmont)
    new FMQ ( 0, 6, 11, 7, Ha, ()-> { u = "Gracemont"; p = "Intel 7"; } ),  // MSR_CPUID_table*; LX*; DPTF*
    new FMQ ( 0, 6, 11, 7, Hc, ()-> { u = "Raptor Cove"; f = "optim of Golden Cove"; p = "Intel 7"; } ), // MSR_CPUID_table*; LX*; DPTF*
    new FMQ ( 0, 6, 11,10, Ha, ()-> { u = "Gracemont"; p = "Intel 7"; } ), // DPTF*; Coreboot*
    new FMQ ( 0, 6, 11,10, Hc, ()-> { u = "Raptor Cove"; f = "optim of Golden Cove"; p = "Intel 7"; } ), // DPTF*; Coreboot*
    new FMQ ( 0, 6, 11,12, Ha, ()-> { u = "Skymont"; p = "Intel 18A"; } ),
    new FMQ ( 0, 6, 11,12, Hc, ()-> { u = "Lion Cove"; p = "Intel 18A"; } ),
    new FMQ ( 0, 6, 11,13, Ha, ()-> { u = "Skymont"; p = "TSMC N3B"; } ),
    new FMQ ( 0, 6, 11,13, Hc, ()-> { u = "Lion Cove"; p = "TSMC N3B"; } ),
    new FMQ ( 0, 6, 11,14, Ha, ()-> { u = "Gracemont"; p = "Intel 7"; } ),
    new FMQ ( 0, 6, 11,14, Hc, ()-> { u = "Golden Cove"; p = "Intel 7"; } ), // possibly no P-cores ever for this model
    new FMQ ( 0, 6, 11,15, Ha, ()-> { u = "Gracemont"; p = "Intel 7"; } ), // MSR_CPUID_table*
    new FMQ ( 0, 6, 11,15, Hc, ()-> { u = "Raptor Cove"; f = "optim of Golden Cove"; p = "Intel 7"; } ), // MSR_CPUID_table*
    new FMQ ( 0, 6, 12, 5, Ha, ()-> { u = "Skymont"; p = "TSMC N3"; } ),
    new FMQ ( 0, 6, 12, 5, Hc, ()-> { u = "Lion Cove"; p = "TSMC N3"; } ),
    new FMQ ( 0, 6, 12, 6, Ha, ()-> { u = "Skymont"; p = "TSMC N3"; } ),
    new FMQ ( 0, 6, 12, 6, Hc, ()-> { u = "Lion Cove"; p = "TSMC N3"; } ),
    // Whatever uarch namess are correct for (0,6),(12,12) Panther Lake (expect Intel 18A)
    new FM  ( 0, 6, 12,15,     ()-> { u = "Emerald Rapids"; c = true; f = "Raptor Cove"; p = "Intel 7"; } ), // MSR_CPUID_table*; LX*
    new FM  ( 0, 6, 13,13,     ()-> { u = "Darkmont"; p = "Intel 18A"; } ), // MSR_CPUID_table*; LX*
    new F   ( 0, 7,            ()-> { u = "Itanium"; } ),
    new FM  ( 0, 11, 0, 0,     ()-> { u = "Knights Ferry"; c = true; f = "K1OM"; p = "45nm"; } ), // found only on en.wikichip.org
    new FM  ( 0, 11, 0, 1,     ()-> { u = "Knights Corner"; c = true; f = "K1OM"; p = "22nm"; } ),
    new FM  ( 0, 15, 0, 0,     ()-> { u = "Willamette"; f = "Netburst"; p = ".18um"; } ),
    new FM  ( 0, 15, 0, 1,     ()-> { u = "Willamette"; f = "Netburst"; p = ".18um"; } ),
    new FM  ( 0, 15, 0, 2,     ()-> { u = "Northwood"; f = "Netburst"; p = ".13um"; } ),
    new FM  ( 0, 15, 0, 3,     ()-> { u = "Prescott"; f = "Netburst"; p = "90nm"; } ),
    new FM  ( 0, 15, 0, 4,     ()-> { u = "Prescott"; f = "Netburst"; p = "90nm"; } ),
    new FM  ( 0, 15, 0, 6,     ()-> { u = "Cedar Mill"; f = "Netburst"; p = "65nm"; } ),
    new F   ( 0, 15,           ()-> { f = "Netburst"; } ),
    new FM  ( 1, 15, 0, 0,     ()-> { u = "Itanium2"; p = ".18um"; } ),
    new FM  ( 1, 15, 0, 1,     ()-> { u = "Itanium2"; p = ".13um"; } ),
    new FM  ( 1, 15, 0, 2,     ()-> { u = "Itanium2"; p = ".13um"; } ),
    new F   ( 1, 15,           ()-> { u = "Itanium2"; } ),
    new F   ( 2, 0,            ()-> { u = "Itanium2"; p = "90nm"; } ),
    new F   ( 2, 1,            ()-> { u = "Itanium2"; } ),
    new FM  ( 4,15, 0, 0,      ()-> { u = "Panther Cove"; p = "Intel 18A"; } ),    // MSR_CPUID_table* (-054, quickly retracted in -055), LX*
    new FM  ( 4,15, 0, 1,      ()-> { u = "Panther Cove"; p = "Intel 18A"; } ) };  // MSR_CPUID_table*, LX*
    return detectorHelper( tfms, bi, INTEL_MICROARCHITECTURE );
    }
}
