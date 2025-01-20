/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

This file contains Processors and Hypervisors data exported from
Todd Allen CPUID project. Some variables and functions names not compliant
with java naming conventions, this fields using original C/C++ naming.

Processor name detection by signature and additional flags,
for Hygon processors. 
stdTfms = Standard Type, Family, Model, Stepping, 
          CPUID standard function 00000001h, register EAX
extTfms = Extended Type, Family, Model, Stepping, 
          CPUID extended function 80000001h, register EAX
bi      = Brand Index, CPUID function 00000001h, register EBX,
          only bits[7-0] must be selected by AND mask inside 
          CriteriaDescriptor.detector() method called by detectorHelper.

*/

package cpuidv3.servicecpudata;

class HygonSynth extends Synth
{

HygonSynth( DatabaseStash stash )
    {
    super( stash );
    }

@Override String[] detect( int stdTfms, int extTfms, int bi )
    {
    final CriteriaDescriptor[] HYGON_DATA = {
    new FMS ( 9, 15,  0, 0,  1, "Hygon Dhyana (A1)" ),
    new FMS ( 9, 15,  0, 0,  2, "Hygon Dhyana (A2)" ) };
    String s1 = detectorHelper( stdTfms, bi, HYGON_DATA );
    String s2 = detectorHelper( extTfms, bi, HYGON_DATA );
    return new String[] { s1, s2 };
    }
}
