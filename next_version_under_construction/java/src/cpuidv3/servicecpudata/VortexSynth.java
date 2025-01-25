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
for Vortex processors. 
stdTfms = Standard Type, Family, Model, Stepping, 
          CPUID standard function 00000001h, register EAX
extTfms = Extended Type, Family, Model, Stepping, 
          CPUID extended function 80000001h, register EAX
bi      = Brand Index, CPUID function 00000001h, register EBX,
          only bits[7-0] must be selected by AND mask inside 
          CriteriaDescriptor.detector() method called by detectorHelper.

*/

package cpuidv3.servicecpudata;

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
    new FM ( 0, 5,  0, 8,     "Vortex86MX" ),
    new FM ( 0, 6,  0, 0,     "Vortex86EX2"),    // undocumented; /proc/cpuinfo seen in wild
    new FM ( 0, 6,  0, 1,     "Vortex86DX3") };  // undocumented; only instlatx64 example
    
    String s1 = detectorHelper( stdTfms, bi, VORTEX_DATA );
    return new String[] { s1, null };
    }
}
