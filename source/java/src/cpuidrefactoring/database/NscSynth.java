/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
This file contains Processors and Hypervisors
data exported from Todd Allen CPUID project.
Some variables and functions names not compliant with java
naming conventions, this fields using original C/C++ naming.
-----------------------------------------------
Processor name detection by signature and additional flags,
for Nsc processors. 
stdTfms = Standard Type, Family, Model, Stepping, 
          CPUID standard function 00000001h, register EAX
extTfms = Extended Type, Family, Model, Stepping, 
          CPUID extended function 80000001h, register EAX
bi      = Brand Index, CPUID function 00000001h, register EBX,
          only bits[7-0] must be selected by AND mask inside 
          CriteriaDescriptor.detector() method called by detectorHelper.
*/

package cpuidrefactoring.database;

class NscSynth extends Synth
{

NscSynth( DatabaseStash stash )
    {
    super(stash);
    }

@Override String[] detect( int stdTfms, int extTfms, int bi )
    {
    final CriteriaDescriptor[] NSC_DATA = {
    new FM ( 0, 5,  0, 4,     "NSC Geode GX1/GXLV/GXm" ),
    new FM ( 0, 5,  0, 5,     "NSC Geode GX2" ),
    new FM ( 0, 5,  0,10,     "NSC Geode LX" ), // sandpile.org
    new F  ( 0, 5,            "NSC Geode (unknown model)" ) };
    String s1 = detectorHelper( stdTfms, bi, NSC_DATA );
    return new String[] { s1, null };
    }
}
