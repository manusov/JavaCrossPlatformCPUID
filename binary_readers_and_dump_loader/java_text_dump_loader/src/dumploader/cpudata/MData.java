/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

This file contains Processors and Hypervisors data exported from
Todd Allen CPUID project. Some variables and functions names not compliant
with java naming conventions, this fields using original C/C++ naming.

Data set for processor microarchitecture description.
u = uarch         = process-dependent name
f = family        = process-neutral name:
                    sometimes independent (e.g. Core)
                    sometimes based on lead uarch (e.g. Nehalem)
p = phys          = physical properties: die process, #pins, etc.
c = core_is_uarch = for some uarches, the core names are based on the
                    uarch names, so the uarch name becomes redundant

*/

package dumploader.cpudata;

public class MData 
{
public String  u;  // microarchitecture
public String  f;  // family
public String  p;  // physical characteristics
public boolean c;  // core flag

MData( String u, String f, String p, boolean c )
    {
    this.u = u;
    this.f = f;
    this.p = p;
    this.c = c;
    }
}
