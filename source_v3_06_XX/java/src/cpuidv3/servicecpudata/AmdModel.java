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

Processor model string vendor-specific detectors, for AMD processors.
Use stash - class with processor parameters.

*/

package cpuidv3.servicecpudata;

import static cpuidv3.servicecpudata.DefineArithmetic.*;

class AmdModel extends Model
{
    
@Override String[] detect( VendorStash stash )
    {
    String brand_pre  = null;
    String brand_post = null;
    String proc       = "";
    if ( stash == null ) return null;

    if ( ( __F( stash.val_1_eax ) == _XF( 0 ) + _F( 15 ) ) &&
         ( __M( stash.val_1_eax ) <  _XM( 4 ) + _M( 0 )  ) )
        {
        /*
        ** Algorithm from:
        **    Revision Guide for AMD Athlon 64 and AMD Opteron Processors 
        **    (25759 Rev 3.79), Constructing the Processor Name String.
        ** But using only the Processor numbers.
        */
        int bti;
        int NN;
        if ( __B( stash.val_1_ebx) != 0 ) 
            {
            bti = BIT_EXTRACT_LE( __B( stash.val_1_ebx ), 5, 8 ) << 2;
            NN  = BIT_EXTRACT_LE( __B( stash.val_1_ebx ), 0, 5 );
            } 
        else if ( BIT_EXTRACT_LE( stash.val_80000001_ebx, 0, 12 ) != 0 ) 
            {
            bti = BIT_EXTRACT_LE( stash.val_80000001_ebx, 6, 12 );
            NN  = BIT_EXTRACT_LE( stash.val_80000001_ebx, 0,  6 );
            } 
        else
            {
            return null;
            }
        int XX = 22 + NN;
        int YY = 38 + 2 * NN;
        int ZZ = 24 + NN;
        int TT = 24 + NN;
        int RR = 45 + 5 * NN;
        int EE = 9 + NN;
        switch ( bti )
            {
            case 0x04:
                brand_pre = "AMD Athlon(tm) 64";
                proc = String.format( "Processor %02d00+", XX );
                break;
            case 0x05:
                brand_pre = "AMD Athlon(tm) 64 X2 Dual Core";
                proc = String.format( "Processor %02d00+", XX );
                break;
            case 0x06:
                brand_pre  = "AMD Athlon(tm) 64";
                proc = String.format( "FX-%02d", ZZ );
                brand_post = "Dual Core";
                break;
            case 0x08:
                brand_pre = "AMD Athlon(tm) 64";
                proc = String.format( "Processor %02d00+", XX );
                break;
            case 0x09:
                brand_pre = "AMD Athlon(tm) 64";
                proc = String.format( "Processor %02d00+", XX );
                break;
            case 0x0a:
                brand_pre = "AMD Turion(tm) Mobile Technology";
                proc = String.format( "ML-%02d", XX );
                break;
            case 0x0b:
                brand_pre = "AMD Turion(tm) Mobile Technology";
                proc = String.format( "MT-%02d", XX );
                break;
            case 0x0c:
            case 0x0d:
                brand_pre = "AMD Opteron(tm)";
                proc = String.format( "Processor 1%02d", YY );
                break;
            case 0x0e:
                brand_pre = "AMD Opteron(tm)";
                proc = String.format( "Processor 1%02d HE", YY );
                break;
            case 0x0f:
                brand_pre = "AMD Opteron(tm)";
                proc = String.format( "Processor 1%02d EE", YY );
                break;
            case 0x10:
            case 0x11:
                brand_pre = "AMD Opteron(tm)";
                proc = String.format( "Processor 2%02d", YY );
                break;
            case 0x12:
                brand_pre = "AMD Opteron(tm)";
                proc = String.format( "Processor 2%02d HE", YY );
                break;
            case 0x13:
                brand_pre = "AMD Opteron(tm)";
                proc = String.format( "Processor 2%02d EE", YY );
                break;
            case 0x14:
            case 0x15:
                brand_pre = "AMD Opteron(tm)";
                proc = String.format( "Processor 8%02d", YY );
                break;
            case 0x16:
                brand_pre = "AMD Opteron(tm)";
                proc = String.format( "Processor 8%02d HE", YY );
                break;
            case 0x17:
                brand_pre = "AMD Opteron(tm)";
                proc = String.format( "Processor 8%02d EE", YY );
                break;
            case 0x18:
                brand_pre = "AMD Athlon(tm) 64";
                proc = String.format( "Processor %02d00+", EE );
                break;
            case 0x1d:
                brand_pre = "Mobile AMD Athlon(tm) XP-M";
                proc = String.format( "Processor %02d00+", XX );
                break;
            case 0x1e:
                brand_pre = "Mobile AMD Athlon(tm) XP-M";
                proc = String.format( "Processor %02d00+", XX );
                break;
            case 0x20:
                brand_pre = "AMD Athlon(tm) XP";
                proc = String.format( "Processor %02d00+", XX );
                break;
            case 0x21:
            case 0x23:
                brand_pre = "Mobile AMD Sempron(tm)";
                proc = String.format( "Processor %02d00+", TT );
                break;
            case 0x22:
            case 0x26:
                brand_pre = "AMD Sempron(tm)";
                proc = String.format( "Processor %02d00+", TT );
                break;
            case 0x24:
                brand_pre = "AMD Athlon(tm) 64";
                proc = String.format( "FX-%02d", ZZ );
                break;
            case 0x29:
            case 0x2c:
            case 0x2d:
            case 0x38:
            case 0x3b:
                brand_pre = "Dual Core AMD Opteron(tm)";
                proc = String.format( "Processor 1%02d", RR );
                break;
            case 0x2a:
            case 0x30:
            case 0x31:
            case 0x39:
            case 0x3c:
                brand_pre = "Dual Core AMD Opteron(tm)";
                proc = String.format( "Processor 2%02d", RR );
                break;
            case 0x2b:
            case 0x34:
            case 0x35:
            case 0x3a:
            case 0x3d:
                brand_pre = "Dual Core AMD Opteron(tm)";
                proc = String.format( "Processor 8%02d", RR );
                break;
            case 0x2e:
                brand_pre = "Dual Core AMD Opteron(tm)";
                proc = String.format( "Processor 1%02d HE", RR );
                break;
            case 0x2f:
                brand_pre = "Dual Core AMD Opteron(tm)";
                proc = String.format( "Processor 1%02d EE", RR );
                break;
            case 0x32:
                brand_pre = "Dual Core AMD Opteron(tm)";
                proc = String.format( "Processor 2%02d HE", RR );
                break;
            case 0x33:
                brand_pre = "Dual Core AMD Opteron(tm)";
                proc = String.format( "Processor 2%02d EE", RR );
                break;
            case 0x36:
                brand_pre = "Dual Core AMD Opteron(tm)";
                proc = String.format( "Processor 8%02d HE", RR );
                break;
            case 0x37:
                brand_pre = "Dual Core AMD Opteron(tm)";
                proc = String.format( "Processor 8%02d EE", RR );
                break;
            }
        }
    else if ( ( __F( stash.val_1_eax ) == _XF( 0 ) + _F( 15 ) ) &&
              ( __M( stash.val_1_eax ) >= _XM( 4 ) + _M( 0 )  ) )
        {
        /*
        ** Algorithm from:
        **    Revision Guide for AMD NPT Family 0Fh Processors (33610 Rev 3.46),
        **    Constructing the Processor Name String.
        ** But using only the Processor numbers.
        */
        int bti;
        int pwrlmt;
        int NN;
        int pkgtype;
        int cmpcap;
        pwrlmt  = ( ( BIT_EXTRACT_LE( stash.val_80000001_ebx, 6, 9 ) << 1 )
                  + BIT_EXTRACT_LE ( stash.val_80000001_ebx, 14, 15 ) );
        bti     = BIT_EXTRACT_LE ( stash.val_80000001_ebx, 9, 14 );
        NN      = ( ( BIT_EXTRACT_LE( stash.val_80000001_ebx, 15, 16 ) << 5 )
                  + BIT_EXTRACT_LE( stash.val_80000001_ebx, 0, 5 ) );
        pkgtype = BIT_EXTRACT_LE( stash.val_80000001_eax, 4, 6 );
        cmpcap  = ( ( BIT_EXTRACT_LE( stash.val_80000008_ecx, 0, 8 ) > 0 )
                  ? 0x1 : 0x0 );
        int RR = NN - 1;
        int PP = 26 + NN;
        int TT = 15 + cmpcap*10 + NN;
        int ZZ = 57 + NN;
        int YY = 29 + NN;
        int x = PKGTYPE( pkgtype ) + CMPCAP( cmpcap ) + 
                BTI( bti ) + PWRLMT( pwrlmt );
        /* Table 7: Name String Table for F (1207) and Fr3 (1207) Processors */
        if ( PKGTYPE(1) + CMPCAP(0) + BTI(1) + PWRLMT(2) == x )           {
            brand_pre = "AMD Opteron(tm)";
            proc = String.format( "Processor 22%02d EE", RR );            }
        else if ( PKGTYPE(1) + CMPCAP(1) + BTI(1) + PWRLMT(2) == x )      {
            brand_pre = "Dual-Core AMD Opteron(tm)";
            proc = String.format( "Processor 22%02d EE", RR );            }
        else if ( PKGTYPE(1) + CMPCAP(1) + BTI(0) + PWRLMT(2) == x )      {
            brand_pre = "Dual-Core AMD Opteron(tm)";
            proc = String.format( "Processor 12%02d EE", RR );            }
        else if ( PKGTYPE(1) + CMPCAP(1) + BTI(0) + PWRLMT(6) == x )      {
            brand_pre = "Dual-Core AMD Opteron(tm)";
            proc = String.format( "Processor 12%02d HE", RR );            }
        else if ( PKGTYPE(1) + CMPCAP(1) + BTI(1) + PWRLMT(6) == x )      {
            brand_pre = "Dual-Core AMD Opteron(tm)";
            proc = String.format( "Processor 22%02d HE", RR );            }
        else if ( PKGTYPE(1) + CMPCAP(1) + BTI(1) + PWRLMT(10) == x )     {
            brand_pre = "Dual-Core AMD Opteron(tm)";
            proc = String.format( "Processor 22%02d", RR );               }
        else if ( PKGTYPE(1) + CMPCAP(1) + BTI(1) + PWRLMT(12) == x )     {
            brand_pre = "Dual-Core AMD Opteron(tm)";
            proc = String.format( "Processor 22%02d SE", RR );            }
        else if ( PKGTYPE(1) + CMPCAP(1) + BTI(4) + PWRLMT(2) == x )      {
            brand_pre = "Dual-Core AMD Opteron(tm)";
            proc = String.format( "Processor 82%02d EE", RR );            }
        else if ( PKGTYPE(1) + CMPCAP(1) + BTI(4) + PWRLMT(6) == x )      {
            brand_pre = "Dual-Core AMD Opteron(tm)";
            proc = String.format( "Processor 82%02d HE", RR );            }
        else if ( PKGTYPE(1) + CMPCAP(1) + BTI(4) + PWRLMT(10) == x )     {
            brand_pre = "Dual-Core AMD Opteron(tm)";
            proc = String.format( "Processor 82%02d", RR );               }
        else if ( PKGTYPE(1) + CMPCAP(1) + BTI(4) + PWRLMT(12) == x )     {
            brand_pre = "Dual-Core AMD Opteron(tm)";
            proc = String.format( "Processor 82%02d SE", RR );            }
        else if ( PKGTYPE(1) + CMPCAP(1) + BTI(6) + PWRLMT(14) == x )     {
            brand_pre = "AMD Athlon(tm) 64";
            proc = String.format( "FX-%02d", ZZ );                        }
        /* Table 8: Name String Table for AM2 and ASB1 Processors */
        else if ( PKGTYPE(3) + CMPCAP(0) + BTI(1) + PWRLMT(5) == x )      {
            brand_pre = "AMD Sempron(tm)";
            proc = String.format( "Processor LE-1%02d0", RR );            }
        else if ( PKGTYPE(3) + CMPCAP(0) + BTI(2) + PWRLMT(6) == x )      {
            brand_pre = "AMD Athlon(tm)";
            proc = String.format( "Processor LE-1%02d0", ZZ );            }
        else if ( PKGTYPE(3) + CMPCAP(0) + BTI(3) + PWRLMT(6) == x )      {
            brand_pre = "AMD Athlon(tm)";
            proc = String.format( "Processor 1%02d0B", ZZ );              }
        else if ( ( PKGTYPE(3) + CMPCAP(0) + BTI(4) + PWRLMT(1) == x )
               || ( PKGTYPE(3) + CMPCAP(0) + BTI(4) + PWRLMT(2) == x )
               || ( PKGTYPE(3) + CMPCAP(0) + BTI(4) + PWRLMT(3) == x )
               || ( PKGTYPE(3) + CMPCAP(0) + BTI(4) + PWRLMT(4) == x )
               || ( PKGTYPE(3) + CMPCAP(0) + BTI(4) + PWRLMT(5) == x )
               || ( PKGTYPE(3) + CMPCAP(0) + BTI(4) + PWRLMT(8) == x ) )  {
            brand_pre = "AMD Athlon(tm) 64";
            proc = String.format( "Processor %02d00+", TT );              }
        else if ( PKGTYPE(3) + CMPCAP(0) + BTI(5) + PWRLMT(2) == x )      {
            brand_pre = "AMD Sempron(tm)";
            proc = String.format( "Processor %02d50p", RR );              }
        else if ( ( PKGTYPE(3) + CMPCAP(0) + BTI(6) + PWRLMT(4) == x )
               || ( PKGTYPE(3) + CMPCAP(0) + BTI(6) + PWRLMT(8) == x ) )  {
            brand_pre = "AMD Sempron(tm)";
            proc = String.format( "Processor %02d00+", TT );              }
        else if ( ( PKGTYPE(3) + CMPCAP(0) + BTI(7) + PWRLMT(1) == x )
               || ( PKGTYPE(3) + CMPCAP(0) + BTI(7) + PWRLMT(2) == x ) )  {
            brand_pre = "AMD Sempron(tm)";
            proc = String.format( "Processor %02d0U", TT );               }
        else if ( ( PKGTYPE(3) + CMPCAP(0) + BTI(8) + PWRLMT(2) == x )
               || ( PKGTYPE(3) + CMPCAP(0) + BTI(8) + PWRLMT(3) == x ) )  {
            brand_pre = "AMD Athlon(tm)";
            proc = String.format( "Processor %02d50e", TT );              }
        else if ( PKGTYPE(3) + CMPCAP(0) + BTI(9) + PWRLMT(2) == x )      {
            brand_pre = "AMD Athlon(tm) Neo";
            proc = String.format( "Processor MV-%02d", TT );              }
        else if ( PKGTYPE(3) + CMPCAP(0) + BTI(12) + PWRLMT(2) == x )     {
            brand_pre = "AMD Sempron(tm)";
            proc = String.format( "Processor 2%02dU", RR );               }
        else if ( PKGTYPE(3) + CMPCAP(1) + BTI(1) + PWRLMT(6) == x )      {
            brand_pre = "Dual-Core AMD Opteron(tm)";
            proc = String.format( "Processor 12%02d HE", RR );            }
        else if ( PKGTYPE(3) + CMPCAP(1) + BTI(1) + PWRLMT(10) == x )     {
            brand_pre = "Dual-Core AMD Opteron(tm)";
            proc = String.format( "Processor 12%02d", RR );               }
        else if ( PKGTYPE(3) + CMPCAP(1) + BTI(1) + PWRLMT(12) == x )     {
            brand_pre = "Dual-Core AMD Opteron(tm)";
            proc = String.format( "Processor 12%02d SE", RR );            }
        else if ( PKGTYPE(3) + CMPCAP(1) + BTI(3) + PWRLMT(3) == x )      {
            brand_pre = "AMD Athlon(tm) X2 Dual Core";
            proc = String.format( "Processor BE-2%02d0", TT );            }
        else if ( ( PKGTYPE(3) + CMPCAP(1) + BTI(4) + PWRLMT(1) == x )
               || ( PKGTYPE(3) + CMPCAP(1) + BTI(4) + PWRLMT(2) == x )
               || ( PKGTYPE(3) + CMPCAP(1) + BTI(4) + PWRLMT(6) == x )
               || ( PKGTYPE(3) + CMPCAP(1) + BTI(4) + PWRLMT(8) == x )
               || ( PKGTYPE(3) + CMPCAP(1) + BTI(4) + PWRLMT(12) == x ) ) {
            brand_pre = "AMD Athlon(tm) 64 X2 Dual Core";
            proc = String.format( "Processor %02d00+", TT );              }
        else if ( PKGTYPE(3) + CMPCAP(1) + BTI(5) + PWRLMT(12) == x )     {
            brand_pre  = "AMD Athlon(tm) 64";
            proc = String.format( "FX-%02d", ZZ );
            brand_post = "Dual Core";                                     }
        else if ( PKGTYPE(3) + CMPCAP(1) + BTI(6) + PWRLMT(6) == x )      {
            brand_pre = "AMD Sempron(tm) Dual Core";
            proc = String.format( "Processor %02d00", RR );               }
        else if ( PKGTYPE(3) + CMPCAP(1) + BTI(7) + PWRLMT(3) == x )      {
            brand_pre = "AMD Athlon(tm) Dual Core";
            proc = String.format( "Processor %02d50e", TT );              }
        else if ( ( PKGTYPE(3) + CMPCAP(1) + BTI(7) + PWRLMT(6) == x )
               || ( PKGTYPE(3) + CMPCAP(1) + BTI(7) + PWRLMT(7) == x ) )  {
            brand_pre = "AMD Athlon(tm) Dual Core";
            proc = String.format( "Processor %02d00B", TT );              }
        else if ( PKGTYPE(3) + CMPCAP(1) + BTI(8) + PWRLMT(3) == x )      {
            brand_pre = "AMD Athlon(tm) Dual Core";
            proc = String.format( "Processor %02d50B", TT );              }
        else if ( PKGTYPE(3) + CMPCAP(1) + BTI(9) + PWRLMT(1) == x ) {
            brand_pre = "AMD Athlon(tm) X2 Dual Core";
            proc = String.format( "Processor %02d50e", TT );              }
        else if ( ( PKGTYPE(3) + CMPCAP(1) + BTI(10) + PWRLMT(1) == x )
               || ( PKGTYPE(3) + CMPCAP(1) + BTI(10) + PWRLMT(2) == x ) ) {
            brand_pre = "AMD Athlon(tm) Neo X2 Dual Core";
            proc = String.format( "Processor %02d50e", TT );              }
        else if ( PKGTYPE(3) + CMPCAP(1) + BTI(11) + PWRLMT(0) == x )     {
            brand_pre = "AMD Turion(tm) Neo X2 Dual Core";
            proc = String.format( "Processor L6%02d", RR );               }
        else if ( PKGTYPE(3) + CMPCAP(1) + BTI(12) + PWRLMT(0) == x )     {
            brand_pre = "AMD Turion(tm) Neo X2 Dual Core";
            proc = String.format( "Processor L3%02d", RR );               }
        /* Table 9: Name String Table for S1g1 Processors */
        else if ( PKGTYPE(0) + CMPCAP(0) + BTI(1) + PWRLMT(2) == x )      {
            brand_pre = "AMD Athlon(tm) 64";
            proc = String.format( "Processor %02d00+", TT );              }
        else if ( PKGTYPE(0) + CMPCAP(0) + BTI(2) + PWRLMT(12) == x )     {
            brand_pre = "AMD Turion(tm) 64 Mobile Technology";
            proc = String.format( "MK-%02d", YY );                        }
        else if ( PKGTYPE(0) + CMPCAP(0) + BTI(3) + PWRLMT(1) == x )      {
            brand_pre = "Mobile AMD Sempron(tm)";
            proc = String.format( "Processor %02d00+", TT );              }
        else if ( ( PKGTYPE(0) + CMPCAP(0) + BTI(3) + PWRLMT(6) == x )
               || ( PKGTYPE(0) + CMPCAP(0) + BTI(3) + PWRLMT(12) == x ) ) {
            brand_pre = "Mobile AMD Sempron(tm)";
            proc = String.format( "Processor %02d00+", PP );              }
        else if ( PKGTYPE(0) + CMPCAP(0) + BTI(4) + PWRLMT(2) == x )      {
            brand_pre = "AMD Sempron(tm)";
            proc = String.format( "Processor %02d00+", TT );              }
        else if ( ( PKGTYPE(0) + CMPCAP(0) + BTI(6) + PWRLMT(4) == x )
               || ( PKGTYPE(0) + CMPCAP(0) + BTI(6) + PWRLMT(6) == x )
               || ( PKGTYPE(0) + CMPCAP(0) + BTI(6) + PWRLMT(12) == x ) ) {
            brand_pre = "AMD Athlon(tm)";
            proc = String.format( "Processor TF-%02d", TT );              }
        else if ( PKGTYPE(0) + CMPCAP(0) + BTI(7) + PWRLMT(3) == x )      {
            brand_pre = "AMD Athlon(tm)";
            proc = String.format( "Processor L1%02d", RR );               }
        else if ( PKGTYPE(0) + CMPCAP(1) + BTI(1) + PWRLMT(12) == x )     {
            brand_pre = "AMD Sempron(tm)";
            proc = String.format( "Processor TJ-%02d", YY );              }
        else if ( PKGTYPE(0) + CMPCAP(1) + BTI(2) + PWRLMT(12) == x )     {
            brand_pre = "AMD Turion(tm) 64 X2 Mobile Technology";
            proc = String.format( "Processor TL-%02d", YY );              }
        else if ( ( PKGTYPE(0) + CMPCAP(1) + BTI(3) + PWRLMT(4) == x )
               || ( PKGTYPE(0) + CMPCAP(1) + BTI(3) + PWRLMT(12) == x ) ) {
            brand_pre = "AMD Turion(tm) 64 X2 Dual-Core";
            proc = String.format( "Processor TK-%02d", YY );              }
        else if ( PKGTYPE(0) + CMPCAP(1) + BTI(5) + PWRLMT(4) == x )      {
            brand_pre = "AMD Turion(tm) 64 X2 Dual Core";
            proc = String.format( "Processor %02d00+", TT );              }
        else if ( PKGTYPE(0) + CMPCAP(1) + BTI(6) + PWRLMT(2) == x ) {
            brand_pre = "AMD Turion(tm) X2 Dual Core";
            proc = String.format( "Processor L3%02d", RR );               }
        else if ( PKGTYPE(0) + CMPCAP(1) + BTI(7) + PWRLMT(4) == x )      {
            brand_pre = "AMD Turion(tm) X2 Dual Core";
            proc = String.format( "Processor L5%02d", RR );               }
        }
    else if ( __F( stash.val_1_eax ) == _XF( 1 ) + _F( 15 )
           || __F( stash.val_1_eax ) == _XF( 2 ) + _F( 15 )
           || __F( stash.val_1_eax ) == _XF( 3 ) + _F( 15 )
           || __F( stash.val_1_eax ) == _XF( 5 ) + _F( 15 ) ) 
        {
        /*
        ** Algorithm from:
        **    AMD Revision Guide for AMD Family 10h Processors (41322 Rev 3.74)
        **    AMD Revision Guide for AMD Family 11h Processors (41788 Rev 3.08)
        **    AMD Revision Guide for AMD Family 12h Processors (44739 Rev 3.10)
        **    AMD Revision Guide for AMD Family 14h Models 00h-0Fh Processors
        **    (47534 Rev 3.00)
        ** But using only the Processor numbers.
        */
        int str1;
        int str2;
        int pg;
        int partialmodel;
        int pkgtype;
        int nc;
        String s1;
        String s2;
        str2         = BIT_EXTRACT_LE( stash.val_80000001_ebx,  0,  4 );
        partialmodel = BIT_EXTRACT_LE( stash.val_80000001_ebx,  4, 11 );
        str1         = BIT_EXTRACT_LE( stash.val_80000001_ebx, 11, 15 );
        pg           = BIT_EXTRACT_LE( stash.val_80000001_ebx, 15, 16 );
        pkgtype      = BIT_EXTRACT_LE( stash.val_80000001_ebx, 28, 32 );
        nc           = BIT_EXTRACT_LE( stash.val_80000008_ecx,  0,  8 );
        
        /* 
        ** In every String2 Values table, there were special cases for
        ** pg == 0 && str2 == 15 which defined them as the empty string.
        ** But that produces the same result as an undefined string, so
        ** don't bother trying to handle them.
        */
        if ( __F( stash.val_1_eax ) == _XF( 1 ) + _F( 15 ) ) 
            {
            if ( pkgtype >= 2 ) 
                {
                partialmodel--;
                }
            /* Family 10h tables */
            switch ( pkgtype ) 
                {
                case 0:
                    /* 41322 3.74: table 14: String1 Values for Fr2, Fr5, and Fr6 (1207) Processors */
                    int x = PG( pg ) + NC( nc ) + STR1( str1 );
                    if      ( x == PG(0) + NC(3) + STR1(0) ) { brand_pre = "Quad-Core AMD Opteron(tm)"; s1 = "Processor 83"; }
                    else if ( x == PG(0) + NC(3) + STR1(1) ) { brand_pre = "Quad-Core AMD Opteron(tm)"; s1 = "Processor 23"; }
                    else if ( x == PG(0) + NC(5) + STR1(0) ) { brand_pre = "Six-Core AMD Opteron(tm)";  s1 = "Processor 84"; }
                    else if ( x == PG(0) + NC(5) + STR1(1) ) { brand_pre = "Six-Core AMD Opteron(tm)";  s1 = "Processor 24"; }
                    else if ( x == PG(1) + NC(3) + STR1(1) ) { brand_pre = "Embedded AMD Opteron(tm)";  s1 = "Processor ";   }
                    else if ( x == PG(1) + NC(5) + STR1(1) ) { brand_pre = "Embedded AMD Opteron(tm)";  s1 = "Processor ";   }
                    else                                     {                                          s1 = null;           }
                    /* 41322 3.74: table 15: String2 Values for Fr2, Fr5, and Fr6 (1207) Processors */
                    x = PG( pg ) + NC( nc ) + STR2( str2 );
                    if      ( x == PG(0) + NC(3) + STR2(10) )  { s2 = " SE";   }
                    else if ( x == PG(0) + NC(3) + STR2(11) )  { s2 = " HE";   }
                    else if ( x == PG(0) + NC(3) + STR2(12) )  { s2 = " EE";   }
                    else if ( x == PG(0) + NC(5) + STR2(0)  )  { s2 = " SE";   }
                    else if ( x == PG(0) + NC(5) + STR2(1)  )  { s2 = " HE";   }
                    else if ( x == PG(0) + NC(5) + STR2(2)  )  { s2 = " EE";   }
                    else if ( x == PG(1) + NC(3) + STR2(1)  )  { s2 = "GF HE"; }
                    else if ( x == PG(1) + NC(3) + STR2(2)  )  { s2 = "HF HE"; }
                    else if ( x == PG(1) + NC(3) + STR2(3)  )  { s2 = "VS";    }
                    else if ( x == PG(1) + NC(3) + STR2(4)  )  { s2 = "QS HE"; }
                    else if ( x == PG(1) + NC(3) + STR2(5)  )  { s2 = "NP HE"; }
                    else if ( x == PG(1) + NC(3) + STR2(6)  )  { s2 = "KH HE"; }
                    else if ( x == PG(1) + NC(3) + STR2(7)  )  { s2 = "KS HE"; }
                    else if ( x == PG(1) + NC(5) + STR2(1)  )  { s2 = "QS";    }
                    else if ( x == PG(1) + NC(5) + STR2(2)  )  { s2 = "KS HE"; }
                    else                                       { s2 = null;    }
                    break;
                case 1:
                    /* 41322 3.74: table 16: String1 Values for AM2r2 and AM3 Processors */
                    x = PG( pg ) + NC( nc ) + STR1( str1 );
                    if      ( x == PG(0) + NC(0) + STR1(2) )  { brand_pre = "AMD Sempron(tm)";           s1 = "1"; }
                    /* This case obviously collides with one later */
                    /* case PG(0) + NC(0) + STR1(3): *brand_pre = "AMD Athlon(tm) II";         s1 = "AMD Athlon(tm) II 1"; */
                    else if ( x == PG(0) + NC(0) + STR1(1)  ) { brand_pre = "AMD Athlon(tm)";            s1 = "";     }
                    else if ( x == PG(0) + NC(0) + STR1(3)  ) { brand_pre = "AMD Athlon(tm) II X2";      s1 = "2";    }
                    else if ( x == PG(0) + NC(0) + STR1(4)  ) { brand_pre = "AMD Athlon(tm) II X2";      s1 = "B";    }
                    else if ( x == PG(0) + NC(0) + STR1(5)  ) { brand_pre = "AMD Athlon(tm) II X2";      s1 = "";     }
                    else if ( x == PG(0) + NC(0) + STR1(7)  ) { brand_pre = "AMD Phenom(tm) II X2";      s1 = "5";    }
                    else if ( x == PG(0) + NC(0) + STR1(10) ) { brand_pre = "AMD Phenom(tm) II X2";      s1 = "";     }
                    else if ( x == PG(0) + NC(0) + STR1(11) ) { brand_pre = "AMD Phenom(tm) II X2";      s1 = "B";    }
                    else if ( x == PG(0) + NC(0) + STR1(12) ) { brand_pre = "AMD Sempron(tm) X2";        s1 = "1";    }
                    else if ( x == PG(0) + NC(2) + STR1(0)  ) { brand_pre = "AMD Phenom(tm)";            s1 = "";     }
                    else if ( x == PG(0) + NC(2) + STR1(3)  ) { brand_pre = "AMD Phenom(tm) II X3";      s1 = "B";    }
                    else if ( x == PG(0) + NC(2) + STR1(4)  ) { brand_pre = "AMD Phenom(tm) II X3";      s1 = "";     }
                    else if ( x == PG(0) + NC(2) + STR1(7)  ) { brand_pre = "AMD Phenom(tm) II X3";      s1 = "4";    }
                    else if ( x == PG(0) + NC(2) + STR1(8)  ) { brand_pre = "AMD Phenom(tm) II X3";      s1 = "7";    }
                    else if ( x == PG(0) + NC(2) + STR1(10) ) { brand_pre = "AMD Phenom(tm) II X3";      s1 = "";     }
                    else if ( x == PG(0) + NC(3) + STR1(0)  ) { brand_pre = "Quad-Core AMD Opteron(tm)"; s1 = "Processor 13"; }
                    else if ( x == PG(0) + NC(3) + STR1(2)  ) { brand_pre = "AMD Phenom(tm)";            s1 = "";     }
                    else if ( x == PG(0) + NC(3) + STR1(3)  ) { brand_pre = "AMD Phenom(tm) II X4";      s1 = "9";    }
                    else if ( x == PG(0) + NC(3) + STR1(4)  ) { brand_pre = "AMD Phenom(tm) II X4";      s1 = "8";    }
                    else if ( x == PG(0) + NC(3) + STR1(7)  ) { brand_pre = "AMD Phenom(tm) II X4";      s1 = "B";    }
                    else if ( x == PG(0) + NC(3) + STR1(8)  ) { brand_pre = "AMD Phenom(tm) II X4";      s1 = "";     }
                    else if ( x == PG(0) + NC(3) + STR1(10) ) { brand_pre = "AMD Athlon(tm) II X4";      s1 = "6";    }
                    else if ( x == PG(0) + NC(3) + STR1(15) ) { brand_pre = "AMD Athlon(tm) II X4";      s1 = "";     }
                    else if ( x == PG(0) + NC(5) + STR1(0)  ) { brand_pre = "AMD Phenom(tm) II X6";      s1 = "1";    }
                    else if ( x == PG(1) + NC(1) + STR1(1)  ) { brand_pre = "AMD Athlon(tm) II XLT V";   s1 = "";     }
                    else if ( x == PG(1) + NC(1) + STR1(2)  ) { brand_pre = "AMD Athlon(tm) II XL V";    s1 = "";     }
                    else if ( x == PG(1) + NC(3) + STR1(1)  ) { brand_pre = "AMD Phenom(tm) II XLT Q";   s1 = "";     }
                    else if ( x == PG(1) + NC(3) + STR1(2)  ) { brand_pre = "AMD Phenom(tm) II X4";      s1 = "9";    }
                    else if ( x == PG(1) + NC(3) + STR1(3)  ) { brand_pre = "AMD Phenom(tm) II X4";      s1 = "8";    }
                    else if ( x == PG(1) + NC(3) + STR1(4)  ) { brand_pre = "AMD Phenom(tm) II X4";      s1 = "6";    }
                    else                                      {                                          s1 = null;   }
                    /* 41322 3.74: table 17: String2 Values for AM2r2 and AM3 Processors */
                    x = PG( pg ) + NC( nc ) + STR2( str2 );
                    if      ( x == PG(0) + NC(0) + STR2(10) ) { s2 = " Processor";                }
                    else if ( x == PG(0) + NC(0) + STR2(11) ) { s2 = "u Processor";               }
                    else if ( x == PG(0) + NC(1) + STR2(3)  ) { s2 = "50 Dual-Core Processor";    }
                    else if ( x == PG(0) + NC(1) + STR2(6)  ) { s2 = " Processor";                }
                    else if ( x == PG(0) + NC(1) + STR2(7)  ) { s2 = "e Processor";               }
                    else if ( x == PG(0) + NC(1) + STR2(9)  ) { s2 = "0 Processor";               }
                    else if ( x == PG(0) + NC(1) + STR2(10) ) { s2 = "0e Processor";              }
                    else if ( x == PG(0) + NC(1) + STR2(11) ) { s2 = "u Processor";               }
                    else if ( x == PG(0) + NC(2) + STR2(0)  ) { s2 = "00 Triple-Core Processor";  }
                    else if ( x == PG(0) + NC(2) + STR2(1)  ) { s2 = "00e Triple-Core Processor"; }
                    else if ( x == PG(0) + NC(2) + STR2(2)  ) { s2 = "00B Triple-Core Processor"; }
                    else if ( x == PG(0) + NC(2) + STR2(3)  ) { s2 = "50 Triple-Core Processor";  }
                    else if ( x == PG(0) + NC(2) + STR2(4)  ) { s2 = "50e Triple-Core Processor"; }
                    else if ( x == PG(0) + NC(2) + STR2(5)  ) { s2 = "50B Triple-Core Processor"; }
                    else if ( x == PG(0) + NC(2) + STR2(6)  ) { s2 = " Processor";                }
                    else if ( x == PG(0) + NC(2) + STR2(7)  ) { s2 = "e Processor";               }
                    else if ( x == PG(0) + NC(2) + STR2(9)  ) { s2 = "0e Processor";              }
                    else if ( x == PG(0) + NC(2) + STR2(10) ) { s2 = "0 Processor";               }
                    else if ( x == PG(0) + NC(3) + STR2(0)  ) { s2 = "00 Quad-Core Processor";    }
                    else if ( x == PG(0) + NC(3) + STR2(1)  ) { s2 = "00e Quad-Core Processor";   }
                    else if ( x == PG(0) + NC(3) + STR2(2)  ) { s2 = "00B Quad-Core Processor";   }
                    else if ( x == PG(0) + NC(3) + STR2(3)  ) { s2 = "50 Quad-Core Processor";    }
                    else if ( x == PG(0) + NC(3) + STR2(4)  ) { s2 = "50e Quad-Core Processor";   }
                    else if ( x == PG(0) + NC(3) + STR2(5)  ) { s2 = "50B Quad-Core Processor";   }
                    else if ( x == PG(0) + NC(3) + STR2(6)  ) { s2 = " Processor";                }
                    else if ( x == PG(0) + NC(3) + STR2(7)  ) { s2 = "e Processor";               }
                    else if ( x == PG(0) + NC(3) + STR2(9)  ) { s2 = "0e Processor";              }
                    else if ( x == PG(0) + NC(3) + STR2(14) ) { s2 = "0 Processor";               }
                    else if ( x == PG(0) + NC(5) + STR2(0)  ) { s2 = "5T Processor";              }
                    else if ( x == PG(0) + NC(5) + STR2(1)  ) { s2 = "0T Processor";              }
                    else if ( x == PG(1) + NC(1) + STR2(1)  ) { s2 = "L Processor";               }
                    else if ( x == PG(1) + NC(1) + STR2(2)  ) { s2 = "C Processor";               }
                    else if ( x == PG(1) + NC(3) + STR2(1)  ) { s2 = "L Processor";               }
                    else if ( x == PG(1) + NC(3) + STR2(4)  ) { s2 = "T Processor";               }
                    else                                      { s2 = null;                        }
                    break;
                case 2:
                    /* 41322 3.74: table 18: String1 Values for S1g3 and S1g4 Processors */
                    x = PG( pg ) + NC( nc ) + STR1( str1 );
                    if      ( x == PG(0) + NC(0) + STR1(0) ) { brand_pre = "AMD Sempron(tm)";                          s1 = "M1"; }
                    else if ( x == PG(0) + NC(0) + STR1(1) ) { brand_pre = "AMD";                                      s1 = "V";  }
                    else if ( x == PG(0) + NC(1) + STR1(0) ) { brand_pre = "AMD Turion(tm) II Ultra Dual-Core Mobile"; s1 = "M6"; }
                    else if ( x == PG(0) + NC(1) + STR1(1) ) { brand_pre = "AMD Turion(tm) II Dual-Core Mobile";       s1 = "M5"; }
                    else if ( x == PG(0) + NC(1) + STR1(2) ) { brand_pre = "AMD Athlon(tm) II Dual-Core";              s1 = "M3"; }
                    else if ( x == PG(0) + NC(1) + STR1(3) ) { brand_pre = "AMD Turion(tm) II";                        s1 = "P";  }
                    else if ( x == PG(0) + NC(1) + STR1(4) ) { brand_pre = "AMD Athlon(tm) II";                        s1 = "P";  }
                    else if ( x == PG(0) + NC(1) + STR1(5) ) { brand_pre = "AMD Phenom(tm) II";                        s1 = "X";  }
                    else if ( x == PG(0) + NC(1) + STR1(6) ) { brand_pre = "AMD Phenom(tm) II";                        s1 = "N";  }
                    else if ( x == PG(0) + NC(1) + STR1(7) ) { brand_pre = "AMD Turion(tm) II";                        s1 = "N";  }
                    else if ( x == PG(0) + NC(1) + STR1(8) ) { brand_pre = "AMD Athlon(tm) II";                        s1 = "N";  }
                    else if ( x == PG(0) + NC(2) + STR1(2) ) { brand_pre = "AMD Phenom(tm) II";                        s1 = "P";  }
                    else if ( x == PG(0) + NC(2) + STR1(3) ) { brand_pre = "AMD Phenom(tm) II";                        s1 = "N";  }
                    else if ( x == PG(0) + NC(3) + STR1(1) ) { brand_pre = "AMD Phenom(tm) II";                        s1 = "P";  }
                    else if ( x == PG(0) + NC(3) + STR1(2) ) { brand_pre = "AMD Phenom(tm) II";                        s1 = "X";  }
                    else if ( x == PG(0) + NC(3) + STR1(3) ) { brand_pre = "AMD Phenom(tm) II";                        s1 = "N";  }
                    else                                     {                                                         s1 = null; }
                    /* 41322 3.74: table 19: String1 Values for S1g3 and S1g4 Processors */
                    x = PG(pg) + NC(nc) + STR2(str2);
                    if      ( x == PG(0) + NC(0) + STR2(1) ) { s2 = "0 Processor";             }
                    else if ( x == PG(0) + NC(1) + STR2(2) ) { s2 = "0 Dual-Core Processor";   }
                    else if ( x == PG(0) + NC(2) + STR2(2) ) { s2 = "0 Triple-Core Processor"; }
                    else if ( x == PG(0) + NC(3) + STR2(1) ) { s2 = "0 Quad-Core Processor";   }
                    else                                     {                      s2 = null; }
                    break;
                case 3:
                    /* 41322 3.74: table 20: String1 Values for G34r1 Processors */
                    x = PG( pg ) + NC( nc ) + STR1( str1 );
                    if      ( x == PG(0) + NC(7)  + STR1(0) ) { brand_pre = "AMD Opteron(tm)";          s1 = "Processor 61"; }
                    else if ( x == PG(0) + NC(11) + STR1(0) ) { brand_pre = "AMD Opteron(tm)";          s1 = "Processor 61"; }
                    else if ( x == PG(1) + NC(7)  + STR1(1) ) { brand_pre = "Embedded AMD Opteron(tm)"; s1 = "Processor ";   }
                    /* It sure is odd that there are no 0/7/1 or 0/11/1 cases here. */
                    else                                      {                                         s1 = null;           }
                    /* 41322 3.74: table 21: String2 Values for G34r1 Processors */
                    x = PG( pg ) + NC( nc ) + STR2( str2 );
                    if      ( x == PG(0) + NC(7)  + STR1(0) ) { s2 = " HE"; }
                    else if ( x == PG(0) + NC(7)  + STR1(1) ) { s2 = " SE"; }
                    else if ( x == PG(0) + NC(11) + STR1(0) ) { s2 = " HE"; }
                    else if ( x == PG(0) + NC(11) + STR1(1) ) { s2 = " SE"; }
                    else if ( x == PG(1) + NC(7)  + STR1(1) ) { s2 = "QS";  }
                    else if ( x == PG(1) + NC(7)  + STR1(2) ) { s2 = "KS";  }
                    else                                      { s2 = null;  }
                    break;
                case 4:
                    /* 41322 3.74: table 22: String1 Values for ASB2 Processors */
                    x = PG( pg ) + NC( nc ) + STR1( str1 );
                    if      ( x == PG(0) + NC(0) + STR1(1) ) { brand_pre = "AMD Athlon(tm) II Neo"; s1 = "K";  }
                    else if ( x == PG(0) + NC(0) + STR1(2) ) { brand_pre = "AMD";                   s1 = "V";  }
                    else if ( x == PG(0) + NC(0) + STR1(3) ) { brand_pre = "AMD Athlon(tm) II Neo"; s1 = "R";  }
                    else if ( x == PG(0) + NC(1) + STR1(1) ) { brand_pre = "AMD Turion(tm) II Neo"; s1 = "K";  }
                    else if ( x == PG(0) + NC(1) + STR1(2) ) { brand_pre = "AMD Athlon(tm) II Neo"; s1 = "K";  }
                    else if ( x == PG(0) + NC(1) + STR1(3) ) { brand_pre = "AMD";                   s1 = "V";  }
                    else if ( x == PG(0) + NC(1) + STR1(4) ) { brand_pre = "AMD Turion(tm) II Neo"; s1 = "N";  }
                    else if ( x == PG(0) + NC(1) + STR1(5) ) { brand_pre = "AMD Athlon(tm) II Neo"; s1 = "N";  }
                    else                                     {                                      s1 = null; }
                    /* 41322 3.74: table 23: String2 Values for ASB2 Processors */
                    x = (PG( pg ) + NC( nc ) + STR2( str2 ) );
                    if      ( x == PG(0) + NC(0)  + STR1(1) ) { s2 = "5 Processor";           }
                    else if ( x == PG(0) + NC(0)  + STR1(2) ) { s2 = "L Processor";           }
                    else if ( x == PG(0) + NC(1)  + STR1(1) ) { s2 = "5 Dual-Core Processor"; }
                    else if ( x == PG(0) + NC(1)  + STR1(2) ) { s2 = "L Dual-Core Processor"; }
                    else                                      { s2 = null;                    }
                    break;
                case 5:
                    /* 41322 3.74: table 24: String1 Values for C32r1 Processors */
                    x = PG( pg ) + NC( nc ) + STR1( str1 );
                    if      ( x == PG(0) + NC(3) + STR1(0) ) { brand_pre = "AMD Opteron(tm)";          s1 = "41"; }
                    else if ( x == PG(0) + NC(5) + STR1(0) ) { brand_pre = "AMD Opteron(tm)";          s1 = "41"; }
                    else if ( x == PG(1) + NC(3) + STR1(1) ) { brand_pre = "Embedded AMD Opteron(tm)"; s1 = " ";  }
                    else if ( x == PG(1) + NC(5) + STR1(1) ) { brand_pre = "Embedded AMD Opteron(tm)"; s1 = " ";  }
                    /* It sure is odd that there are no 0/3/1 or 0/5/1 cases here. */
                    else                                     {                                         s1 = null; }
                    /* 41322 3.74: table 25: String2 Values for C32r1 Processors */
                    /* 41322 3.74: table 25 */
                    x = PG( pg ) + NC( nc ) + STR2( str2 ); 
                    if      ( x == PG(0) + NC(3) + STR1(0) ) { s2 = " HE";   }
                    else if ( x == PG(0) + NC(3) + STR1(1) ) { s2 = " EE";   }
                    else if ( x == PG(0) + NC(5) + STR1(0) ) { s2 = " HE";   }
                    else if ( x == PG(0) + NC(5) + STR1(1) ) { s2 = " EE";   }
                    else if ( x == PG(1) + NC(3) + STR1(1) ) { s2 = "QS HE"; }
                    else if ( x == PG(1) + NC(3) + STR1(2) ) { s2 = "LE HE"; }
                    else if ( x == PG(1) + NC(3) + STR1(3) ) { s2 = "CL EE"; }
                    else if ( x == PG(1) + NC(5) + STR1(1) ) { s2 = "KX HE"; }
                    else if ( x == PG(1) + NC(5) + STR1(2) ) { s2 = "GL EE"; }
                    else                                     { s2 = null;    }
                    break;
                default:
                    s1 = null;
                    s2 = null;
                    break;
                }
            }
        else if ( __F( stash.val_1_eax ) == _XF( 2 ) + _F( 15 ) )
            {
                         /* Family 11h tables */
            switch (pkgtype) 
                {
                case 2:
                    /* 41788 3.08: table 3: String1 Values for S1g2 Processors */
                    int x = PG( pg ) + NC( nc ) + STR1( str1 );
                    if      ( x == PG(0) + NC(0) + STR1(0) ) { brand_pre = "AMD Sempron(tm)";                          s1 = "SI-"; }
                    else if ( x == PG(0) + NC(0) + STR1(1) ) { brand_pre = "AMD Athlon(tm)";                           s1 = "QI-"; }
                    else if ( x == PG(0) + NC(1) + STR1(0) ) { brand_pre = "AMD Turion(tm) X2 Ultra Dual-Core Mobile"; s1 = "ZM-"; }
                    else if ( x == PG(0) + NC(1) + STR1(1) ) { brand_pre = "AMD Turion(tm) X2 Dual-Core Mobile";       s1 = "RM-"; }
                    else if ( x == PG(0) + NC(1) + STR1(2) ) { brand_pre = "AMD Athlon(tm) X2 Dual-Core";              s1 = "QL-"; }
                    else if ( x == PG(0) + NC(1) + STR1(3) ) { brand_pre = "AMD Sempron(tm) X2 Dual-Core";             s1 = "NI-"; }
                    else                                     {                                                         s1 = null;  }
                    /* 41788 3.08: table 4: String2 Values for S1g2 Processors */
                    x = PG( pg ) + NC( nc ) + STR2( str2 ); 
                    if      ( x == PG(0) + NC(0) + STR2(0) ) { s2 = "";   }
                    else if ( x == PG(0) + NC(1) + STR2(0) ) { s2 = "";   }
                    else                                     { s2 = null; }
                    break;
                default:
                    s1 = null;
                    s2 = null;
                    break;
                }
            }
        else if ( __F( stash.val_1_eax ) == _XF( 3 ) + _F( 15 ) ) 
            {
            partialmodel--;
                     /* Family 12h tables */
            switch ( pkgtype ) 
                {
                case 1:
                    /* 44739 3.10: table 6: String1 Values for FS1 Processors */ 
                    int x = PG( pg ) + NC( nc ) + STR1( str1 );
                    if      ( x == PG(0) + NC(1) + STR1(3) ) { brand_pre = "AMD"; s1 = "A4-33"; }
                    else if ( x == PG(0) + NC(1) + STR1(5) ) { brand_pre = "AMD"; s1 = "E2-30"; }
                    else if ( x == PG(0) + NC(3) + STR1(1) ) { brand_pre = "AMD"; s1 = "A8-35"; }
                    else if ( x == PG(0) + NC(3) + STR1(3) ) { brand_pre = "AMD"; s1 = "A6-34"; }
                    else                                     {                    s1 = null;    }
                    /* 44739 3.10: table 7: String2 Values for FS1 Processors */ 
                    x = PG( pg ) + NC( nc ) + STR2( str2 );
                    if      ( x == PG(0) + NC(1) + STR2(1) ) {  s2 = "M";  }
                    else if ( x == PG(0) + NC(1) + STR2(2) ) {  s2 = "MX"; }
                    else if ( x == PG(0) + NC(3) + STR2(1) ) {  s2 = "M";  }
                    else if ( x == PG(0) + NC(3) + STR2(2) ) {  s2 = "MX"; }
                    else                       { s2 = null; }
                    break;
                case 2:
                    /* 44739 3.10: table 8: String1 Values for FM1 Processors */ 
                    x = PG( pg ) + NC( nc ) + STR1( str1 );
                    if      ( x == PG(0) + NC(1) + STR1(1)  ) { brand_pre = "AMD";                   s1 = "A4-33"; }
                    else if ( x == PG(0) + NC(1) + STR1(2)  ) { brand_pre = "AMD";                   s1 = "E2-32"; }
                    else if ( x == PG(0) + NC(1) + STR1(4)  ) { brand_pre = "AMD Athlon(tm) II X2";  s1 = "2";     }
                    else if ( x == PG(0) + NC(1) + STR1(5)  ) { brand_pre = "AMD";                   s1 = "A4-34"; }
                    else if ( x == PG(0) + NC(1) + STR1(12) ) { brand_pre = "AMD Sempron(tm) X2";    s1 = "1";     }
                    else if ( x == PG(0) + NC(2) + STR1(5)  ) { brand_pre = "AMD";                   s1 = "A6-35"; }
                    else if ( x == PG(0) + NC(3) + STR1(5)  ) { brand_pre = "AMD";                   s1 = "A8-38"; }
                    else if ( x == PG(0) + NC(3) + STR1(6)  ) { brand_pre = "AMD";                   s1 = "A6-36"; }
                    else if ( x == PG(0) + NC(3) + STR1(13) ) { brand_pre = "AMD Athlon(tm) II X4";  s1 = "6";     }
                    else                                      {                                      s1 = null;    }
                    /* 44739 3.10: table 9: String2 Values for FM1 Processors */ 
                    x = PG( pg ) + NC( nc ) + STR2( str2 );
                    if      ( x == PG(0) + NC(1) + STR2(1) ) { s2 = " APU with Radeon(tm) HD Graphics"; }
                    else if ( x == PG(0) + NC(1) + STR2(2) ) { s2 = " Dual-Core Processor";             }
                    else if ( x == PG(0) + NC(2) + STR2(1) ) { s2 = " APU with Radeon(tm) HD Graphics"; }
                    else if ( x == PG(0) + NC(3) + STR2(1) ) { s2 = " APU with Radeon(tm) HD Graphics"; }
                    else if ( x == PG(0) + NC(3) + STR2(3) ) { s2 = " Quad-Core Processor";             }
                    else                                     { s2 = null;                               }
                    break;
                default:
                    s1 = null;
                    s2 = null;
                    break;
                }
            }
        else if (__F( stash.val_1_eax ) == _XF( 5 ) + _F( 15 ) )
            {
            partialmodel--;
                     /* Family 14h Models 00h-0Fh tables */
            switch ( pkgtype ) 
                {
                case 0:
                    /* 47534 3.00: table 4: String1 Values for FT1 Processors */
                    int x = PG( pg ) + NC( nc ) + STR1( str1 );
                    if      ( x == PG(0) + NC(0) + STR1(1) ) { brand_pre = "AMD"; s1 = "C-";   }
                    else if ( x == PG(0) + NC(0) + STR1(2) ) { brand_pre = "AMD"; s1 = "E-";   }
                    else if ( x == PG(0) + NC(0) + STR1(4) ) { brand_pre = "AMD"; s1 = "G-T";  }
                    else if ( x == PG(0) + NC(1) + STR1(1) ) { brand_pre = "AMD"; s1 = "C-";   }
                    else if ( x == PG(0) + NC(1) + STR1(2) ) { brand_pre = "AMD"; s1 = "E-";   }
                    else if ( x == PG(0) + NC(1) + STR1(3) ) { brand_pre = "AMD"; s1 = "Z-";   }
                    else if ( x == PG(0) + NC(1) + STR1(4) ) { brand_pre = "AMD"; s1 = "G-T";  }
                    else if ( x == PG(0) + NC(1) + STR1(5) ) { brand_pre = "AMD"; s1 = "E1-1"; }
                    else if ( x == PG(0) + NC(1) + STR1(6) ) { brand_pre = "AMD"; s1 = "E2-1"; }
                    else if ( x == PG(0) + NC(1) + STR1(7) ) { brand_pre = "AMD"; s1 = "E2-2"; }
                    else                                     {                    s1 = null;   }
                    /* 47534 3.00: table 5: String2 Values for FT1 Processors */
                    x = PG( pg ) + NC( nc ) + STR2( str2 );
                    if      ( x == PG(0) + NC(0) + STR2(1)  ) { s2 = "";   }
                    else if ( x == PG(0) + NC(0) + STR2(2)  ) { s2 = "0";  }
                    else if ( x == PG(0) + NC(0) + STR2(3)  ) { s2 = "5";  }
                    else if ( x == PG(0) + NC(0) + STR2(4)  ) { s2 = "0x"; }
                    else if ( x == PG(0) + NC(0) + STR2(5)  ) { s2 = "5x"; }
                    else if ( x == PG(0) + NC(0) + STR2(6)  ) { s2 = "x";  }
                    else if ( x == PG(0) + NC(0) + STR2(7)  ) { s2 = "L";  }
                    else if ( x == PG(0) + NC(0) + STR2(8)  ) { s2 = "N";  }
                    else if ( x == PG(0) + NC(0) + STR2(9)  ) { s2 = "R";  }
                    else if ( x == PG(0) + NC(0) + STR2(10) ) { s2 = "0";  }
                    else if ( x == PG(0) + NC(0) + STR2(11) ) { s2 = "5";  }
                    else if ( x == PG(0) + NC(0) + STR2(12) ) { s2 = "";   }
                    else if ( x == PG(0) + NC(0) + STR2(13) ) { s2 = "0D"; }
                    else if ( x == PG(0) + NC(1) + STR2(1)  ) { s2 = "";   }
                    else if ( x == PG(0) + NC(1) + STR2(2)  ) { s2 = "0";  }
                    else if ( x == PG(0) + NC(1) + STR2(3)  ) { s2 = "5";  }
                    else if ( x == PG(0) + NC(1) + STR2(4)  ) { s2 = "0x"; }
                    else if ( x == PG(0) + NC(1) + STR2(5)  ) { s2 = "5x"; }
                    else if ( x == PG(0) + NC(1) + STR2(6)  ) { s2 = "x";  }
                    else if ( x == PG(0) + NC(1) + STR2(7)  ) { s2 = "L";  }
                    else if ( x == PG(0) + NC(1) + STR2(8)  ) { s2 = "N";  }
                    else if ( x == PG(0) + NC(1) + STR2(9)  ) { s2 = "0";  }
                    else if ( x == PG(0) + NC(1) + STR2(10) ) { s2 = "5";  }
                    else if ( x == PG(0) + NC(1) + STR2(11) ) { s2 = "";   }
                    else if ( x == PG(0) + NC(1) + STR2(12) ) { s2 = "E";  }
                    else if ( x == PG(0) + NC(1) + STR2(13) ) { s2 = "0D"; }
                    else                                      { s2 = null; }
                    break;
                default:
                s1 = null;
                s2 = null;
                break;
                }
            }
        else 
            {
            s1 = null;
            s2 = null;
            }

        if ( s1 != null ) 
            {
            String p = String.format("%s%02d", s1, partialmodel );
            proc = proc + " " + p;
            if ( s2 != null)
                {
                p = String.format( "%s", s2 );
                proc = proc + " " + p;
                }
            }
       }
    
    // build model string
    StringBuilder sb = new StringBuilder();
    if ( brand_pre != null )
        {
        sb.append( brand_pre );
        }
    if ( brand_post != null )
        {
        sb.append( " " );
        sb.append( brand_post );
        }
    if ( proc != null )
        {
        sb.append( " " );
        sb.append( proc );
        }
    String model = null;
    if ( sb.length() > 1 )
        {
        model = sb.toString();
        }
    return new String[] { brand_pre, brand_post, proc, model };
    }
}
