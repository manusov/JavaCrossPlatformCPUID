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
by signature and additional flags, for AMD processors. 
tfms = Standard Type, Family, Model, Stepping, 
       CPUID standard function 00000001h, register EAX
bi   = Brand Index, CPUID function 00000001h, register EBX,
       only bits[7-0] must be selected by AND mask inside 
       CriteriaDescriptor.detector() method called by detectorHelper.

*/

package cpuidv3.servicecpudata;

class AmdMicroarchitecture extends Microarchitecture
{

AmdMicroarchitecture( DatabaseStash stash )
    {
    super( stash );
    }
    
@Override MData detect( int tfms, int bi )
    {
    final CriteriaDescriptor[] AMD_MICROARCHITECTURE = {
    new FM  ( 0, 4, 0, 3,      ()-> { u = "Am486"; } ),
    new FM  ( 0, 4, 0, 7,      ()-> { u = "Am486"; } ),
    new FM  ( 0, 4, 0, 8,      ()-> { u = "Am486"; } ),
    new FM  ( 0, 4, 0, 9,      ()-> { u = "Am486"; } ),
    new F   ( 0, 4,            ()-> { u = "Am5x86"; } ),
    new FM  ( 0, 5, 0, 6,      ()-> { u = "K6"; p = ".30um"; } ),
    new FM  ( 0, 5, 0, 7,      ()-> { u = "K6"; p = ".25um"; } ), // p from sandpile.org
    new FM  ( 0, 5, 0, 13,     ()-> { u = "K6"; p = ".18um"; } ), // p from sandpile.org
//  new F   ( 0, 5,            ()-> { u = "K6"; } ),
    new F   ( 0, 5,            ()-> { u = "K5"; } ),                             // CHANGED
    new FM  ( 0, 6, 0, 1,      ()-> { u = "K7"; p = ".25um"; } ),
    new FM  ( 0, 6, 0, 2,      ()-> { u = "K7"; p = ".18um"; } ),
    new F   ( 0, 6,            ()-> { u = "K7"; } ),
    new FMS ( 0, 15, 0, 4, 8,  ()-> { u = "K8"; p = "754-pin, .13um"; } ),
    new FMS ( 0, 15, 0, 4, 10, ()-> { u = "K8"; p = "754-pin, .13um"; } ),       // ADDED
    new FM  ( 0, 15, 0, 4,     ()-> { u = "K8"; p = "940-pin, .13um"; } ),
    new FM  ( 0, 15, 0, 5,     ()-> { u = "K8"; p = "940-pin, .13um"; } ),
    new FM  ( 0, 15, 0, 7,     ()-> { u = "K8"; p = "939-pin, .13um"; } ),
    new FM  ( 0, 15, 0, 8,     ()-> { u = "K8"; p = "754-pin, .13um"; } ),
    new FM  ( 0, 15, 0, 11,    ()-> { u = "K8"; p = "939-pin, .13um"; } ),
    new FM  ( 0, 15, 0, 12,    ()-> { u = "K8"; p = "754-pin, .13um"; } ),
    new FM  ( 0, 15, 0, 14,    ()-> { u = "K8"; p = "754-pin, .13um"; } ),
    new FM  ( 0, 15, 0, 15,    ()-> { u = "K8"; p = "939-pin, .13um"; } ),
    new FM  ( 0, 15, 1, 4,     ()-> { u = "K8"; p = "754-pin, 90nm"; } ),
    new FM  ( 0, 15, 1, 5,     ()-> { u = "K8"; p = "940-pin, 90nm"; } ),
    new FM  ( 0, 15, 1, 7,     ()-> { u = "K8"; p = "939-pin, 90nm"; } ),
    new FM  ( 0, 15, 1, 8,     ()-> { u = "K8"; p = "754-pin, 90nm"; } ),
    new FM  ( 0, 15, 1, 11,    ()-> { u = "K8"; p = "939-pin, 90nm"; } ),
    new FM  ( 0, 15, 1, 12,    ()-> { u = "K8"; p = "754-pin, 90nm"; } ),
    new FM  ( 0, 15, 1, 15,    ()-> { u = "K8"; p = "939-pin, 90nm"; } ),
    new FM  ( 0, 15, 2, 1,     ()-> { u = "K8"; p = "940-pin, 90nm"; } ),
    new FM  ( 0, 15, 2, 3,     ()-> { u = "K8"; p = "939-pin, 90nm"; } ),
    new FM  ( 0, 15, 2, 4,     ()-> { u = "K8"; p = "754-pin, 90nm"; } ),
    new FM  ( 0, 15, 2, 5,     ()-> { u = "K8"; p = "940-pin, 90nm"; } ),
    new FM  ( 0, 15, 2, 7,     ()-> { u = "K8"; p = "939-pin, 90nm"; } ),
    new FM  ( 0, 15, 2, 11,    ()-> { u = "K8"; p = "939-pin, 90nm"; } ),
    new FM  ( 0, 15, 2, 12,    ()-> { u = "K8"; p = "754-pin, 90nm"; } ),
    new FM  ( 0, 15, 2, 15,    ()-> { u = "K8"; p = "939-pin, 90nm"; } ),
    new FM  ( 0, 15, 4, 1,     ()-> { u = "K8"; p = "90nm"; } ),
    new FM  ( 0, 15, 4, 3,     ()-> { u = "K8"; p = "90nm"; } ),
    new FM  ( 0, 15, 4, 8,     ()-> { u = "K8"; p = "90nm"; } ),
    new FM  ( 0, 15, 4, 11,    ()-> { u = "K8"; p = "90nm"; } ),
    new FM  ( 0, 15, 4, 12,    ()-> { u = "K8"; p = "90nm"; } ),
    new FM  ( 0, 15, 4, 15,    ()-> { u = "K8"; p = "90nm"; } ),
    new FM  ( 0, 15, 5, 13,    ()-> { u = "K8"; p = "90nm"; } ),
    new FM  ( 0, 15, 5, 15,    ()-> { u = "K8"; p = "90nm"; } ),
    new FM  ( 0, 15, 6, 8,     ()-> { u = "K8"; p = "65nm"; } ),
    new FM  ( 0, 15, 6, 11,    ()-> { u = "K8"; p = "65nm"; } ),
    new FM  ( 0, 15, 6, 12,    ()-> { u = "K8"; p = "65nm"; } ),
    new FM  ( 0, 15, 6, 15,    ()-> { u = "K8"; p = "65nm"; } ),
    new FM  ( 0, 15, 7, 12,    ()-> { u = "K8"; p = "65nm"; } ),
    new FM  ( 0, 15, 7, 15,    ()-> { u = "K8"; p = "65nm"; } ),
    new FM  ( 0, 15, 12, 1,    ()-> { u = "K8"; p = "90nm"; } ),
    new FM  ( 1, 15, 0, 0,     ()-> { u = "K10"; p = "65nm"; } ), // sandpile.org
    new FM  ( 1, 15, 0, 2,     ()-> { u = "K10"; p = "65nm"; } ),
    new FM  ( 1, 15, 0, 4,     ()-> { u = "K10"; p = "45nm"; } ),
    new FM  ( 1, 15, 0, 5,     ()-> { u = "K10"; p = "45nm"; } ),
    new FM  ( 1, 15, 0, 6,     ()-> { u = "K10"; p = "45nm"; } ),
    new FM  ( 1, 15, 0, 8,     ()-> { u = "K10"; p = "45nm"; } ),
    new FM  ( 1, 15, 0, 9,     ()-> { u = "K10"; p = "45nm"; } ),
    new FM  ( 1, 15, 0, 10,    ()-> { u = "K10"; p = "45nm"; } ),
    new F   ( 2, 15,           ()-> { u = "Puma 2008"; p = "65nm"; } ),
    new F   ( 3, 15,           ()-> { u = "K10"; p = "32nm"; } ),
    new F   ( 5, 15,           ()-> { u = "Bobcat"; p = "40nm"; } ),
    new FM  ( 6, 15, 0, 0,     ()-> { u = "Bulldozer"; p = "32nm"; } ), // instlatx64 engr sample
    new FM  ( 6, 15, 0, 1,     ()-> { u = "Bulldozer"; p = "32nm"; } ),
    new FM  ( 6, 15, 0, 2,     ()-> { u = "Piledriver"; p = "32nm"; } ),
    new FM  ( 6, 15, 1, 0,     ()-> { u = "Piledriver"; p = "32nm"; } ),
    new FM  ( 6, 15, 1, 3,     ()-> { u = "Piledriver"; p = "32nm"; } ),
    new FM  ( 6, 15, 3, 0,     ()-> { u = "Steamroller"; p = "28nm"; } ),
    new FM  ( 6, 15, 3, 8,     ()-> { u = "Steamroller"; p = "28nm"; } ),
    new FM  ( 6, 15, 4, 0,     ()-> { u = "Steamroller"; p = "28nm"; } ), // Software Optimization Guide (15h) says it has the same inst latencies as (6,15),(3,x).
    new FM  ( 6, 15, 6, 0,     ()-> { u = "Excavator"; p = "28nm"; } ), // undocumented, but instlatx64 samples
    new FM  ( 6, 15, 6, 5,     ()-> { u = "Excavator"; p = "28nm"; } ), // undocumented, but sample from Alexandros Couloumbis
    new FM  ( 6, 15, 7, 0,     ()-> { u = "Excavator"; p = "28nm"; } ),
    new FM  ( 7, 15, 0, 0,     ()-> { u = "Jaguar"; p = "28nm"; } ),
    new FM  ( 7, 15, 2, 6,     ()-> { u = "Cato"; p = "28nm"; } ), // only instlatx64 example; engr sample?
    new FM  ( 7, 15, 3, 0,     ()-> { u = "Puma 2014"; p = "28nm"; } ),
    // Todd Allen note:
    // In Zen-based CPUs, the model uses only the extended model and the
    // high-order bit of the model.  The low-order 3 bits of the model are part
    // of the stepping.
/*
    new FM  ( 8, 15, 0, 0,     ()-> { u = "Zen"; p = "14nm"; } ), // instlatx64 engr sample
    new FM  ( 8, 15, 0, 1,     ()-> { u = "Zen"; p = "14nm"; } ),
    new FM  ( 8, 15, 0, 8,     ()-> { u = "Zen+"; p = "12nm"; } ),
    new FM  ( 8, 15, 1, 1,     ()-> { u = "Zen"; p = "14nm"; } ), // found only on en.wikichip.org & instlatx64 examples
    new FM  ( 8, 15, 1, 8,     ()-> { u = "Zen+"; p = "12nm"; } ),
    new FM  ( 8, 15, 2, 0,     ()-> { u = "Zen"; p = "14nm"; } ),
    new FM  ( 8, 15, 3, 1,     ()-> { u = "Zen 2"; p = "7nm"; } ),  // found only on en.wikichip.org
    new FM  ( 8, 15, 6, 0,     ()-> { u = "Zen 2"; p = "7nm"; } ),
    new FM  ( 8, 15, 7, 1,     ()-> { u = "Zen 2"; p = "7nm"; } ),   // undocumented, but samples from Steven Noonan
    new FM  ( 10, 15, 1, 1,    ()-> { u = "Zen 4"; p = "5nm"; } ),
    new FM  ( 10, 15, 6, 1,    ()-> { u = "Zen 4"; p = "5nm"; } ),
    new FM  ( 10, 15, 7, 4,    ()-> { u = "Zen 4"; p = "5nm"; } ),
*/
    // Note FMm ignores model bits 3-0.
    new FMm ( 8,15, 0, 0,      ()-> { u = "Zen"; p = "14nm"; } ),
    new FMm ( 8,15, 0, 8,      ()-> { u = "Zen+"; p = "12nm"; } ),
    new FMm ( 8,15, 1, 0,      ()-> { u = "Zen"; p = "14nm"; } ), // found only on en.wikichip.org & instlatx64 examples
    new FMm ( 8,15, 1, 8,      ()-> { u = "Zen+"; p = "12nm"; } ),
    new FMm ( 8,15, 2, 0,      ()-> { u = "Zen"; p = "14nm"; } ),
    new FMm ( 8,15, 3, 0,      ()-> { u = "Zen 2"; p = "7nm"; } ),  // found only on en.wikichip.org
    new FMm ( 8,15, 4, 0,      ()-> { u = "Zen 2"; p = "7nm"; } ),  // only instlatx64 example; engr sample?
    new FMm ( 8,15, 6, 0,      ()-> { u = "Zen 2"; p = "7nm"; } ),
    new FMm ( 8,15, 6, 8,      ()-> { u = "Zen 2"; p = "7nm"; } ),  // undocumented, but instlatx64 samples
    new FMm ( 8,15, 7, 0,      ()-> { u = "Zen 2"; p = "7nm"; } ),  // undocumented, but samples from Steven Noonan
    new FMm ( 8,15, 8, 0,      ()-> { u = "Zen 2"; p = "7nm"; } ),  // undocumented, but sample via instlatx64
    new FMm ( 8,15, 9, 0,      ()-> { u = "Zen 2"; p = "7nm"; } ),  // undocumented, but sample via instlatx64 from @patrickschur_
    new FMm ( 8,15, 9, 8,      ()-> { u = "Zen 2"; p = "7nm"; } ),  // undocumented, but sample via instlatx64 from @zimogorets
    new FMm ( 8,15, 10, 0,     ()-> { u = "Zen 2"; p = "7nm"; } ),  // sample via instlatx64 from @ExecuFix
    new FMm (10,15, 0, 0,      ()-> { u = "Zen 3"; p = "TSMC 7nm"; } ),
    new FMm (10,15, 0, 8,      ()-> { u = "Zen 3"; p = "TSMC 7nm"; } ),  // undocumented, but sample via instlatx64 from @ExecuFix
    new FMm (10,15, 1, 0,      ()-> { u = "Zen 4"; p = "TSMC N5"; } ),
    new FMm (10,15, 1, 8,      ()-> { u = "Zen 4"; p = "TSMC 5FF"; } ),  // undocumented, but sample via instlatx64 from @patrickschur_
    new FMm (10,15, 2, 0,      ()-> { u = "Zen 3"; p = "TSMC 7FF"; } ),
    new FMm (10,15, 3, 0,      ()-> { u = "Zen 3"; p = "TSMC N5P"; } ),  // undocumented, but sample via instlatx64 from @patrickschur_
    new FMm (10,15, 4, 0,      ()-> { u = "Zen 3"; p = "TSMC N6"; } ),  // undocumented, but instlatx64 sample
    new FMm (10,15, 5, 0,      ()-> { u = "Zen 3"; p = "TSMC 7nm"; } ),
    new FMm (10,15, 6, 0,      ()-> { u = "Zen 4"; p = "TSMC N5"; } ),  // undocumented, but instlatx64 sample
    new FMm (10,15, 7, 0,      ()-> { u = "Zen 4"; p = "TSMC 4nm"; } ),  // undocumented, but engr sample via instlatx64 from bakerlab.org (6220795)
    new FMm (10,15, 7, 8,      ()-> { u = "Zen 4"; p = "TSMC 4nm"; } ),  // Coreboot*
    new FMm (10,15, 8, 0,      ()-> { u = "Zen 4"; p = "TSMC N5"; } ),  // undocumented, but LKML: https://lkml.org/lkml/2023/7/21/835 from AMD's Yazen Ghannam
    new FMm (10,15, 9, 0,      ()-> { u = "Zen 4"; p = "TSMC N5"; } ),  // undocumented, but LKML: https://lkml.org/lkml/2023/7/21/835 from AMD's Yazen Ghannam
    new FMm (10,15, 10, 0,     ()-> { u = "Zen 4c"; p = "TSMC N5"; } ),
    new F   ( 10, 15,          ()-> { u = "Zen 3"; p = "7nm"; } ),  // undocumented, LX*
    
    new FMm (11,15, 0, 0,      ()-> { u = "Zen 5";  p = "TSMC N4P"; } ),  // LX* & tangentially documented: 58088 AMD 1Ah Models 00h-0Fh and Models 10h-1Fh ACPI v6.5 Porting Guide
    new FMm (11,15, 0, 8,      ()-> { u = "Zen 5"; p = "TSMC N4P"; } ),  // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian
    new FMm (11,15, 1, 0,      ()-> { u = "Zen 5"; p = "TSMC N3"; } ),  // tangentially documented: 58088 AMD 1Ah Models 00h-0Fh and Models 10h-1Fh ACPI v6.5 Porting Guide
    new FMm (11,15, 1, 8,      ()-> { u = "Zen 5"; p = "TSMC N3"; } ),  // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian
    new FMm (11,15, 2, 0,      ()-> { u = "Zen 5"; p = "TSMC N4P"; } ),  // undocumented, but engr sample via instlatx64 from milkyway.cs.rpi.edu (996435)
    new FMm (11,15, 2, 8,      ()-> { u = "Zen 5"; p = "TSMC N4P"; } ),  // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian
    new FMm (11,15, 3, 0,      ()-> { u = "Zen 5"; p = "TSMC N4P"; } ),  // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian
    new FMm (11,15, 3, 8,      ()-> { u = "Zen 5"; p = "TSMC N4P"; } ),  // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian
    new FMm (11,15, 4, 0,      ()-> { u = "Zen 5"; p = "TSMC N4P"; } ),  // undocumented, but LX* & engr sample via instlatx64 from einsteinathome.org (13142934)
    new FMm (11,15, 4, 8,      ()-> { u = "Zen 5"; p = "TSMC N4P"; } ),  // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian
    new FMm (11,15, 5, 0,      ()-> { u = "Zen 5"; p = "TSMC N4P"; } ),  // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian
    new FMm (11,15, 5, 8,      ()-> { u = "Zen 5"; p = "TSMC N4P"; } ),  // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian
    new FMm (11,15, 6, 0,      ()-> { u = "Zen 5"; p = "TSMC N4P"; } ),  // undocumented, but engr sample via instlatx64 from @kepler_l2
    new FMm (11,15, 6, 8,      ()-> { u = "Zen 5"; p = "TSMC N4P"; } ),  // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian
    new FMm (11,15, 7, 0,      ()-> { u = "Zen 5"; p = "TSMC N4P"; } ),  // undocumented, but LX* & engr sample via instlatx64 from @kepler_l2
    new FMm (11,15, 7, 8,      ()-> { u = "Zen 5"; p = "TSMC N4P"; } ) };  // undocumented, but LLVM patch from AMD's Ganesh Gopalasubramanian
    
    return detectorHelper( tfms, 0, AMD_MICROARCHITECTURE );
    }
}
