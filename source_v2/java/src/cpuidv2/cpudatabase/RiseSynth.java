/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2024.
-------------------------------------------------------------------------------
This file contains Processors and Hypervisors data exported from
Todd Allen CPUID project. Some variables and functions names not compliant
with java naming conventions, this fields using original C/C++ naming.
-------------------------------------------------------------------------------
Processor name detection by signature and additional flags,
for Rise processors. 
stdTfms = Standard Type, Family, Model, Stepping, 
          CPUID standard function 00000001h, register EAX
extTfms = Extended Type, Family, Model, Stepping, 
          CPUID extended function 80000001h, register EAX
bi      = Brand Index, CPUID function 00000001h, register EBX,
          only bits[7-0] must be selected by AND mask inside 
          CriteriaDescriptor.detector() method called by detectorHelper.
*/

package cpuidv2.cpudatabase;

class RiseSynth extends Synth
{

RiseSynth( DatabaseStash stash )
    {
    super( stash );
    }

@Override String[] detect( int stdTfms, int extTfms, int bi )
    {
    final CriteriaDescriptor[] RISE_DATA = {
    new FM ( 0,5,  0,0,     "Rise mP6 iDragon, .25u" ),
    new FM ( 0,5,  0,2,     "Rise mP6 iDragon, .18u" ),
    new FM ( 0,5,  0,8,     "Rise mP6 iDragon II, .25u" ),
    new FM ( 0,5,  0,9,     "Rise mP6 iDragon II, .18u" ) };
    String s1 = detectorHelper( stdTfms, bi, RISE_DATA );
    return new String[] { s1, null };
    }
}
