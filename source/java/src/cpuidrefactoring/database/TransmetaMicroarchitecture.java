/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
This file contains Processors and Hypervisors
data exported from Todd Allen CPUID project.
Some variables and functions names not compliant with java
naming conventions, this fields using original C/C++ naming.
-----------------------------------------------
Processor microarchitecture and physical parameters detection 
by signature and additional flags, for Transmeta processors. 
tfms = Standard Type, Family, Model, Stepping, 
       CPUID standard function 00000001h, register EAX
bi   = Brand Index, CPUID function 00000001h, register EBX,
       only bits[7-0] must be selected by AND mask inside 
       CriteriaDescriptor.detector() method called by detectorHelper.
*/

package cpuidrefactoring.database;

class TransmetaMicroarchitecture extends Microarchitecture
{

TransmetaMicroarchitecture( DatabaseStash stash )
    {
    super( stash );
    }

@Override MData detect( int tfms, int bi )
    {
    final CriteriaDescriptor[] TRANSMETA_MICROARCHITECTURE = {
    new F( 0, 5,  ()-> { u = "Crusoe"; c = true; } ),
    new F( 0, 15, ()-> { u = "Efficeon"; c = true; } ) };
    return detectorHelper( tfms, bi, TRANSMETA_MICROARCHITECTURE );
    }
}
