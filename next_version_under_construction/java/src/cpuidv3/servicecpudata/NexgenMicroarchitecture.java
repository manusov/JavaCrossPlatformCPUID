/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

This file contains Processors and Hypervisors data exported from
Todd Allen CPUID project. Some variables and functions names not compliant
with java naming conventions, this fields using original C/C++ naming.

Processor microarchitecture and physical parameters detection 
by signature and additional flags, for Nexgen processors. 
tfms = Standard Type, Family, Model, Stepping, 
       CPUID standard function 00000001h, register EAX
bi   = Brand Index, CPUID function 00000001h, register EBX,
       only bits[7-0] must be selected by AND mask inside 
       CriteriaDescriptor.detector() method called by detectorHelper.
*/

package cpuidv3.servicecpudata;

class NexgenMicroarchitecture extends Microarchitecture
{

NexgenMicroarchitecture( DatabaseStash stash ) 
    {
    super( stash );
    }
    
@Override MData detect( int tfms, int bi )
    {
    final CriteriaDescriptor[] NEXGEN_MICROARCHITECTURE = {
    new F( 0, 5, ()-> { u = "Nx586"; } ) };
    return detectorHelper( tfms, bi, NEXGEN_MICROARCHITECTURE );
    }
}
