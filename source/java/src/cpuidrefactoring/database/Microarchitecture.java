/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
This file contains Processors and Hypervisors
data exported from Todd Allen CPUID project.
Some variables and functions names not compliant with java
naming conventions, this fields using original C/C++ naming.
-----------------------------------------------
Base class for vendor-specific microarchitecture detectors.
tfms = Standard Type, Family, Model, Stepping, 
       CPUID standard function 00000001h, register EAX
bi   = Brand Index, CPUID function 00000001h, register EBX,
       only bits[7-0] must be selected by AND mask inside 
       CriteriaDescriptor.detector() method called by detectorHelper.
*/

package cpuidrefactoring.database;

class Microarchitecture
{
String  u;  // microarchitecture
String  f;  // family
String  p;  // physical parameters
boolean c;  // core microarchitecture

final DatabaseStash stash;

Microarchitecture ( DatabaseStash stash )
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
