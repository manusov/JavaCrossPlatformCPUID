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

package cpuidv2.cpudatabase;

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
