/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

This file contains Processors and Hypervisors data exported from
Todd Allen CPUID project. Some variables and functions names not compliant
with java naming conventions, this fields using original C/C++ naming.

Base class for vendor-specific processor name detection 
by signature and additional flags.
stdTfms = Standard Type, Family, Model, Stepping, 
          CPUID standard function 00000001h, register EAX
extTfms = Extended Type, Family, Model, Stepping, 
          CPUID extended function 80000001h, register EAX
bi      = Brand Index, CPUID function 00000001h, register EBX,
          only bits[7-0] must be selected by AND mask inside 
          CriteriaDescriptor.detector() method called by detectorHelper.

*/

package dumploader.cpudata;

class Synth 
{
final DatabaseStash stash;

Synth( DatabaseStash stash )
    {
    this.stash = stash;
    }
    
String[] detect( int stdTfms, int extTfms, int bi )
{
    return null;
}

String detectorHelper( int tfms, int bi, CriteriaDescriptor[] cds )
    {
    String name = null;
    for ( CriteriaDescriptor cd : cds )
        {
        if ( cd.detector( tfms, bi ) )
            {
            name = cd.name;
            break;
            }
        }
    return name;
    }
}
