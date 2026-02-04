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

Decode Intel brand index and set flags, used for processor detection.
val_1_eax, val_1_ebx = registers after CPUID function 1.
stash = processor information collection, updated by detect() method.

*/

package cpuidv3.servicecpudata;

import static cpuidv3.servicecpudata.DefineArithmetic.*;

class IntelStashBrand 
{

void detect( VendorStash stash )
    {
    int val_1_eax = stash.val_1_eax;
    int val_1_ebx = stash.val_1_ebx;
            
    switch ( __B( val_1_ebx ) )
    {
        case 0:  break;
        case 1:  stash.bri.desktop_celeron = true; break;
        case 2:  stash.bri.desktop_pentium = true; break;
        case 3:  if ( __FMS(val_1_eax) == _FMS(0,6, 0,11, 1) ) 
                {
                stash.bri.desktop_celeron = true;
                }
                else
                {
                stash.bri.xeon = true;
                }
                break;
        case 4:  stash.bri.desktop_pentium = true; break;
        case 6:  stash.bri.desktop_pentium = true; break;
        case 7:  stash.bri.desktop_celeron = true; break;
        case 8:  stash.bri.desktop_pentium = true; break;
        case 9:  stash.bri.desktop_pentium = true; break;
        case 10: stash.bri.desktop_celeron = true; break;
        case 11: 
            if ( __FMS( val_1_eax ) <= _FMS( 0,15, 0,1, 2) )
                {
                stash.bri.xeon_mp = true;
                }
            else
                {
                stash.bri.xeon = true;
                }
                break;
        case 12: stash.bri.xeon_mp         = true; break;
        case 14: 
            if ( __FMS( val_1_eax ) <= _FMS( 0,15, 0,1, 3 ) )
                {
                stash.bri.xeon = true;
                }
            else
                {
                stash.bri.mobile_pentium_m = true;
                }
                break;
        case 15: 
            if ( __FM(val_1_eax) == _FM ( 0,15, 0,2 ) )
                {
                stash.bri.mobile_pentium_m = true;
                } 
                else
                {
                stash.bri.mobile_celeron = true;
                }
                break;
        case 16: stash.bri.desktop_celeron = true; break;
        case 17: stash.bri.mobile_pentium  = true; break;
        case 18: stash.bri.desktop_celeron = true; break;
        case 19: stash.bri.mobile_celeron  = true; break;
        case 20: stash.bri.desktop_celeron = true; break;
        case 21: stash.bri.mobile_pentium  = true; break;
        case 22: stash.bri.desktop_pentium = true; break;
        case 23: stash.bri.mobile_celeron  = true; break;
        default: break;
        }
    }
}
