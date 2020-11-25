/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
This file contains Processors and Hypervisors
data exported from Todd Allen CPUID project.
Some variables and functions names not compliant with java
naming conventions, this fields using original C/C++ naming.
-----------------------------------------------
Processor microarchitecture and physical parameters detection 
by signature and additional flags, for Rise processors. 
tfms = Standard Type, Family, Model, Stepping, 
       CPUID standard function 00000001h, register EAX
bi   = Brand Index, CPUID function 00000001h, register EBX,
       only bits[7-0] must be selected by AND mask inside 
       CriteriaDescriptor.detector() method called by detectorHelper.
*/

package cpuidrefactoring.database;

class RiseMicroarchitecture extends Microarchitecture
{

RiseMicroarchitecture( DatabaseStash stash )
    {
    super( stash );
    }

@Override MData detect( int tfms, int bi )
    {
    final CriteriaDescriptor[] RISE_MICROARCHITECTURE = {
    new F( 0, 5, ()-> { u = "mP6"; c = true; } ) };
    return detectorHelper( tfms, bi, RISE_MICROARCHITECTURE );
    }
}
