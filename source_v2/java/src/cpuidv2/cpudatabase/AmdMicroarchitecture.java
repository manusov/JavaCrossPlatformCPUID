/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
This file contains Processors and Hypervisors
data exported from Todd Allen CPUID project.
Some variables and functions names not compliant with java
naming conventions, this fields using original C/C++ naming.
-----------------------------------------------
Processor microarchitecture and physical parameters detection 
by signature and additional flags, for AMD processors. 
tfms = Standard Type, Family, Model, Stepping, 
       CPUID standard function 00000001h, register EAX
bi   = Brand Index, CPUID function 00000001h, register EBX,
       only bits[7-0] must be selected by AND mask inside 
       CriteriaDescriptor.detector() method called by detectorHelper.
*/

package cpuidv2.cpudatabase;

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
    new F   ( 10, 15,          ()-> { u = "Zen 3"; p = "7nm"; } ) };  // undocumented, LX*
    return detectorHelper( tfms, 0, AMD_MICROARCHITECTURE );
    }
}
