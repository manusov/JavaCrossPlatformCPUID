/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

This file contains Processors and Hypervisors data exported from
Todd Allen CPUID project. Some variables and functions names not compliant
with java naming conventions, this fields using original C/C++ naming.

Processor model name detection by signature and additional flags,
for Intel processors. 
tfms = Standard Type, Family, Model, Stepping, 
       CPUID standard function 00000001h, register EAX
bi   = Brand Index, CPUID function 00000001h, register EBX,
       only bits[7-0] must be selected by AND mask inside 
       CriteriaDescriptor.detector() method called by detectorHelper.

*/

package cpuidv3.servicecpudata;

class IntelBrandId extends Brand
{
@Override String detect( int tfms, int bi )
    {
    final CriteriaDescriptor[] INTEL_BRAND = {
    new B   (                   1, "Intel Celeron, .18um" ),
    new FMSB( 0, 6,  0,11,  1,  2, "Intel Pentium III, .13um" ),       // ADDED
    new B   (                   2, "Intel Pentium III, .18um" ),
    new FMSB( 0, 6,  0,11,  1,  3, "Intel Celeron, .13um" ),
    new B   (                   3, "Intel Pentium III Xeon, .18um" ),
    new B   (                   4, "Intel Pentium III, .13um" ),
    new B   (                   6, "Mobile Intel Pentium III, .13um" ),
    new B   (                   7, "Mobile Intel Celeron, .13um" ),
    new FMB ( 0,15,  0, 0,      8, "Intel Pentium 4, .18um" ),
    new FMSB( 0,15,  0, 1,  0,  8, "Intel Pentium 4, .18um" ),
    new FMSB( 0,15,  0, 1,  1,  8, "Intel Pentium 4, .18um" ),
    new FMSB( 0,15,  0, 1,  2,  8, "Intel Pentium 4, .18um" ),
    new B   (                   8, "Mobile Intel Celeron 4, .13um" ),
    new B   (                   9, "Intel Pentium 4, .13um" ),
    new B   (                  10, "Intel Celeron 4, .18um" ),
    new FMB ( 0,15,  0, 0,     11, "Intel Xeon MP, .18um" ),
    new FMSB( 0,15,  0, 1,  0, 11, "Intel Xeon MP, .18um" ),
    new FMSB( 0,15,  0, 1,  1, 11, "Intel Xeon MP, .18um" ),
    new FMSB( 0,15,  0, 1,  2, 11, "Intel Xeon MP, .18um" ),
    new B   (                  11, "Intel Xeon, .13um" ),
    new B   (                  12, "Intel Xeon MP, .13um" ),
    new FMB ( 0,15,  0, 0,     14, "Intel Xeon, .18um" ),
    new FMSB( 0,15,  0, 1,  0, 14, "Intel Xeon, .18um" ),
    new FMSB( 0,15,  0, 1,  1, 14, "Intel Xeon, .18um" ),
    new FMSB( 0,15,  0, 1,  2, 14, "Intel Xeon, .18um" ),
    new FMB ( 0,15,  0, 2,     14, "Mobile Intel Pentium 4 Processor-M" ),
    new B   (                  14, "Mobile Intel Xeon, .13um" ),
    new FMB ( 0,15,  0, 2,     15, "Mobile Intel Pentium 4 Processor-M" ),
    new B   (                  15, "Mobile Intel Celeron 4" ),
    new B   (                  17, "Mobile Genuine Intel" ),
    new B   (                  18, "Intel Celeron M" ),
    new B   (                  19, "Mobile Intel Celeron" ),
    new B   (                  20, "Intel Celeron" ),
    new B   (                  21, "Mobile Genuine Intel" ),
    new B   (                  22, "Intel Pentium M, .13um" ),
    new B   (                  23, "Mobile Intel Celeron" ) };
    final int N = INTEL_BRAND.length;
    String name = null;
    for( int i=0; i<N; i++ )
        {
            if ( INTEL_BRAND[i].detector( tfms, bi ) )
                {
                name = INTEL_BRAND[i].name;
                break;
                }
        }
    return name;
    }
}
