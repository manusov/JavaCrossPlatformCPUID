/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
This file contains Processors and Hypervisors
data exported from Todd Allen CPUID project.
Some variables and functions names not compliant with java
naming conventions, this fields using original C/C++ naming.
-----------------------------------------------
Processor name detection by signature and additional flags,
for Vortex processors. 
stdTfms = Standard Type, Family, Model, Stepping, 
          CPUID standard function 00000001h, register EAX
extTfms = Extended Type, Family, Model, Stepping, 
          CPUID extended function 80000001h, register EAX
bi      = Brand Index, CPUID function 00000001h, register EBX,
          only bits[7-0] must be selected by AND mask inside 
          CriteriaDescriptor.detector() method called by detectorHelper.
*/

package cpuidv2.cpudatabase;

class VortexSynth extends Synth
{

VortexSynth( DatabaseStash stash )
    {
    super( stash );
    }

@Override String[] detect( int stdTfms, int extTfms, int bi )
    {
    final CriteriaDescriptor[] VORTEX_DATA = {
    new FM ( 0, 5,  0, 2,     "Vortex86DX" ),
    new FM ( 0, 5,  0, 8,     "Vortex86MX" ) };
    String s1 = detectorHelper( stdTfms, bi, VORTEX_DATA );
    return new String[] { s1, null };
    }
}
