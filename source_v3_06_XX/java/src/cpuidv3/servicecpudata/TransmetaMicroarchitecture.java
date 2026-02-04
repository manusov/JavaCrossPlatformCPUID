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

Processor microarchitecture and physical parameters detection 
by signature and additional flags, for Transmeta processors. 
tfms = Standard Type, Family, Model, Stepping, 
       CPUID standard function 00000001h, register EAX
bi   = Brand Index, CPUID function 00000001h, register EBX,
       only bits[7-0] must be selected by AND mask inside 
       CriteriaDescriptor.detector() method called by detectorHelper.

*/

package cpuidv3.servicecpudata;

class TransmetaMicroarchitecture extends Microarchitecture
{

TransmetaMicroarchitecture( VendorStash stash )
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
