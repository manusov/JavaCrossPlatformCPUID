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

Decode Intel cache descriptors and set flags, used for processor detection.
value = cache descriptor byte, one of bytes result of CPUID function 2.
val_2_eax, val_2_ebx, val_2_ecx, val_2_edx = registers after CPUID function 2.
stash = processor information collection, updated by detect() method.

*/

package cpuidv3.servicecpudata;

class IntelStashCache 
{

void detect( DatabaseStash stash )
    {
    detectDword( stash, stash.val_2_eax, 1 );
    detectDword( stash, stash.val_2_ebx, 0 );
    detectDword( stash, stash.val_2_ecx, 0 );
    detectDword( stash, stash.val_2_edx, 0 );
    }

private void detectDword( DatabaseStash stash, int value, int shift )
    {
    if ( ( value & 0x80000000 ) == 0 )
        {
        value >>= ( shift * 8 );
        int count = 4 - shift;
        for( int i=0; i<count; i++ )
            {
            detectByte( stash, value & 0xFF );
            value >>= 8;
            }
        }
    }

private void detectByte( DatabaseStash stash, int value )
    {
    switch ( value ) 
        {
        case 0x42: stash.L2_4w_256K   = true; break;
        case 0x43: stash.L2_4w_512K   = true; break;
        case 0x44: stash.L2_4w_1Mor2M = true; break;
        case 0x45: stash.L2_4w_1Mor2M = true; break;
        case 0x80: stash.L2_8w_512K   = true; break;
        case 0x82: stash.L2_8w_256K   = true; break;
        case 0x83: stash.L2_8w_512K   = true; break;
        case 0x84: stash.L2_8w_1Mor2M = true; break;
        case 0x85: stash.L2_8w_1Mor2M = true; break;
        }

    switch ( value ) 
        {
        case 0x45:
        case 0x7d:
        case 0x85:
        stash.L2_2M = true; 
        break;
        }

    switch ( value ) 
        {
        case 0x4e:
        stash.L2_6M = true; 
        break;
        }

   switch ( value )
        {
        case 0x22:
        case 0x23:
        case 0x25:
        case 0x29:
        case 0x46:
        case 0x47:
        case 0x49:
        case 0x4a:
        case 0x4b:
        case 0x4c:
        case 0x4d:
        case 0x88:
        case 0x89:
        case 0x8a:
        case 0x8d:
        case 0xd0:
        case 0xd1:
        case 0xd2:
        case 0xd6:
        case 0xd7:
        case 0xd8:
        case 0xdc:
        case 0xdd:
        case 0xde:
        case 0xe2:
        case 0xe3:
        case 0xe4:
        case 0xea:
        case 0xeb:
        case 0xec:
        stash.L3 = true;
        break;
        }

    switch ( value )
        {
        case 0x21:
        case 0x3c:
        case 0x42:
        case 0x7a:
        case 0x7e:
        case 0x82:
        stash.L2_256K = true;
        break;
        }

   switch ( value )
        {
        case 0x3e:
        case 0x43:
        case 0x7b:
        case 0x7f:
        case 0x83:
        case 0x86:
        stash.L2_512K = true;
        break;
        }
    }
}
