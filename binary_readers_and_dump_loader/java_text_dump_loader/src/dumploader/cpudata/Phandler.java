/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

This file contains Processors and Hypervisors data exported from
Todd Allen CPUID project. Some variables and functions names not compliant
with java naming conventions, this fields using original C/C++ naming.

Set of interface methods for get processor data:
Brand description, Microarchitecture parameters, Synthetic model string.

*/

package dumploader.cpudata;

interface GetBR { Brand gBR(); }
interface GetMA { Microarchitecture gMA(); }
interface GetSY { Synth gSY(); }
interface GetMD { Model gMD(); }

class Phandler
{
final GetBR gbr;
final GetMA gma;
final GetSY gsy;
final GetMD gmd;
Phandler( GetBR gbr, GetMA gma, GetSY gsy, GetMD gmd )
    {
    this.gbr = gbr;
    this.gma = gma;
    this.gsy = gsy;
    this.gmd = gmd;
    }
}
