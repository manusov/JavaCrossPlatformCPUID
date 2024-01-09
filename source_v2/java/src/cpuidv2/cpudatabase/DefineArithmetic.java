/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2024.
-------------------------------------------------------------------------------
This file contains Processors and Hypervisors data exported from
Todd Allen CPUID project. Some variables and functions names not compliant
with java naming conventions, this fields using original C/C++ naming.
-------------------------------------------------------------------------------
Bits mask and bits positioning constants methods for extract
Type, Family, Model, Stepping, Brand Index from CPUID information.
*/

package cpuidv2.cpudatabase;

class DefineArithmetic 
{

// logical masks for processor signature bitfields
final static int __F    ( int v ) { return v & 0x0ff00f00; }  // family
final static int __M    ( int v ) { return v & 0x000f00f0; }  // model
final static int __FM   ( int v ) { return v & 0x0fff0ff0; }  // family, model
final static int __FMS  ( int v ) { return v & 0x0fff0fff; }  // family, model, stepping
final static int __TF   ( int v ) { return v & 0x0ff03f00; }  // type, family
final static int __TFM  ( int v ) { return v & 0x0fff3ff0; }  // type, family, model
final static int __TFMS ( int v ) { return v & 0x0fff3fff; }  // type, family, model, stepping

// shifts counts for processor signature bitfields
final static int _T ( int v ) { return v << 12; }  // type
final static int _F ( int v ) { return v << 8;  }  // family
final static int _M ( int v ) { return v << 4;  }  // model
final static int _S ( int v ) { return v;       }  // stepping
final static int _XF( int v ) { return v << 20; }  // extended family
final static int _XM( int v ) { return v << 16; }  // extended model

// combinations of processor signature bitfields
final static int _FM( int xf, int f, int xm, int m ) 
    { return _XF(xf) + _F(f) + _XM(xm) + _M(m); }
final static int _FMS( int xf, int f, int xm, int m, int s ) 
    { return _XF( xf ) + _F( f ) + _XM( xm ) + _M( m ) + _S( s ); }

// logical mask for brand index and raw value
final static int __B( int v ) { return v & 0x000000ff; }  // brand index
final static int _B ( int v ) { return v; }               // raw value

// common constants and operations
final static int BPI = 32;    // bits per integer
final static int POWER2   ( int power ) 
    { return 1 << power; }
final static int RIGHTMASK( int width ) 
    { return ( ( ( width ) >= BPI) ? ~0 : POWER2( width ) - 1 ); }
final static int BIT_EXTRACT_LE( int value, int start, int after )
    { return ( ( ( value ) & RIGHTMASK( after ) ) >> start ); }

// AMD model detection definitions
final static int PKGTYPE ( int pkgtype ) { return pkgtype << 11; }
final static int CMPCAP  ( int cmpcap  ) { return cmpcap  << 9;  }
final static int BTI     ( int bti     ) { return bti     << 4;  }
final static int PWRLMT  ( int pwrlmt  ) { return pwrlmt;        }

final static int NC   ( int nc   ) { return nc   << 9; }
final static int PG   ( int pg   ) { return pg   << 8; }
final static int STR1 ( int str1 ) { return str1 << 4; }
final static int STR2 ( int str2 ) { return str2;      }

}
