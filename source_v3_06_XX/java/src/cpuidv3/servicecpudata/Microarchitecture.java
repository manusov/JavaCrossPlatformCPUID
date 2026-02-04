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

Base class for vendor-specific microarchitecture detectors.
tfms = Standard Type, Family, Model, Stepping, 
       CPUID standard function 00000001h, register EAX
bi   = Brand Index, CPUID function 00000001h, register EBX,
       only bits[7-0] must be selected by AND mask inside 
       CriteriaDescriptor.detector() method called by detectorHelper.

*/

package cpuidv3.servicecpudata;

class Microarchitecture
{
String  u;  // microarchitecture
String  f;  // family
String  p;  // physical parameters
boolean c;  // core microarchitecture

final VendorStash stash;

Microarchitecture ( VendorStash stash )
    {
    this.stash = stash;
    }

MData detect( int tfms, int bi )
    {
    return null;
    }

MData detectorHelper( int tfms, int bi, CriteriaDescriptor[] cds )
    {
    u = null;
    f = null;
    p = null;
    c = false;
    for ( CriteriaDescriptor cd : cds )
        {
        if ( cd.detector( tfms, bi ) )
            {
            if ( cd.writer != null )
                {
                cd.writer.writeParms();
                }
            break;
            }
        }
    return new MData( u, f, p, c );
    }
}
