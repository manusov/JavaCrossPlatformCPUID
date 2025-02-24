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
for Zhaoxin processors. 
stdTfms = Standard Type, Family, Model, Stepping, 
          CPUID standard function 00000001h, register EAX
extTfms = Extended Type, Family, Model, Stepping, 
          CPUID extended function 80000001h, register EAX
bi      = Brand Index, CPUID function 00000001h, register EBX,
          only bits[7-0] must be selected by AND mask inside 
          CriteriaDescriptor.detector() method called by detectorHelper.

*/

package cpuidv3.servicecpudata;

class ZhaoxinSynth extends Synth
{

ZhaoxinSynth( DatabaseStash stash )
    {
    super( stash );
    }

@Override String[] detect( int stdTfms, int extTfms, int bi )
    {
    final CriteriaDescriptor[] ZHAOXIN_DATA = {
    new FM  ( 0, 7,  1,11,  "Zhaoxin KaiXian KX-5000 / Kaisheng KH-20000 (WuDaoKou)" ),
    new FM  ( 0, 7,  1,15,  "Zhaoxin KaiXian ZX-D (WuDaoKou)" ), // geekbench.com example (steppings 12 & 14)
    new FM  ( 0, 7,  3,11,  "Zhaoxin KaiXian KX-6000 / Kaisheng KH-30000 (LuJiaZui)") }; // /proc/cpuinfo screenshot: KX-6840@3000MHz (stepping 0)
    String s1 = detectorHelper( stdTfms, bi, ZHAOXIN_DATA );
    return new String[] { s1, null };
    }
}
