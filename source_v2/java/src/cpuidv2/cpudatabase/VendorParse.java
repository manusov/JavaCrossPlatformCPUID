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
Processor brand name string parser.
Used results of CPUID functions 80000002h - 80000004h.
*/

package cpuidv2.cpudatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class VendorParse 
{

private boolean strsub( String st, String subst )
    {
    return st.contains( subst );
    }

private boolean strreg( String st, String subst )
    {
    Pattern pattern = Pattern.compile( subst );
    Matcher matcher = pattern.matcher( st );
    return matcher.find();
    }
    
void decodeBrandString( DatabaseStash stash )
    {
    if ( ( stash == null )||( stash.brand == null ) ) return;
    
    String brand = stash.brand;
    
    stash.br.mobile    = strsub( brand, "Mobile" ) || 
                         strsub( brand, "mobile" );
    stash.br.celeron   = strsub( brand, "Celeron" );
    stash.br.core      = strsub( brand, "Core(TM)" );
    stash.br.pentium   = strsub( brand, "Pentium" );
    stash.br.atom      = strsub( brand, "Atom" );
    stash.br.xeon_mp   = strsub( brand, "Xeon MP" )   ||
                         strsub( brand, "Xeon(TM) MP" ) ||
                         strsub( brand, "Xeon(R)" );
    stash.br.xeon      = strsub( brand, "Xeon" );
    stash.br.pentium_m = strsub( brand, "Pentium(R) M" );
    stash.br.pentium_d = strsub( brand, "Pentium(R) D" );
    stash.br.extreme   = strreg( brand, " ?X[0-9][0-9][0-9][0-9]" );
    stash.br.generic   = strsub( brand, "Genuine Intel(R) CPU" );
    stash.br.scalable  = strsub( brand, "Bronze" ) ||
                         strsub( brand, "Silver")  ||
                         strsub( brand, "Gold")    ||
                         strsub( brand, "Platinum" );
    stash.br.u_line    = strreg( brand, "Core.* [im][3579]-[0-9]*U" ) ||
                         strreg( brand, "Pentium.* [0-9]*U" )         ||
                         strreg( brand, "Celeron.* [0-9]*U" );
    stash.br.y_line    = strreg( brand, "Core.* [im][3579]-[0-9]*Y" ) ||
                         strreg( brand, "Pentium.* [0-9]*Y")          ||
                         strreg( brand, "Celeron.* [0-9]*Y" );
    stash.br.g_line    = strreg( brand, "Core.* [im][3579]-[0-9]*G" );
    stash.br.i_8000    = strreg( brand, "Core.* [im][3579]-8[0-9][0-9][0-9]" );
    stash.br.i_10000   = strreg( brand, "Core.* i[3579]-10[0-9][0-9][0-9]" );
    stash.br.cc150     = strreg( brand, "CC150" );
    
    // Montage Jintide, undocumented, only instlatx64 example
    stash.br.montage   = strsub( brand, "Montage(R)" );

    stash.br.athlon_lv = strsub( brand, "Athlon(tm) XP-M (LV)" );
    stash.br.athlon_xp = strsub( brand, "Athlon(tm) XP" ) ||
                         strsub( brand, "Athlon(TM) XP");
    stash.br.duron     = strsub( brand, "Duron" );
    stash.br.athlon    = strsub( brand, "Athlon" );
    stash.br.sempron   = strsub( brand, "Sempron" );
    stash.br.phenom    = strsub( brand, "Phenom" );
    stash.br.series    = strsub( brand, "Series" );
    stash.br.a_series  = strsub( brand, "AMD A" )  ||
                         strsub( brand, "AMD PRO A" );
    stash.br.c_series  = strsub( brand, "AMD C" );
    stash.br.e_series  = strsub( brand, "AMD E" );
    stash.br.g_series  = strsub( brand, "AMD G" );
    stash.br.r_series  = strsub( brand, "AMD R" );
    stash.br.z_series  = strsub( brand, "AMD Z" );
    stash.br.geode     = strsub( brand, "Geode" );
    stash.br.turion    = strsub( brand, "Turion" );
    stash.br.neo       = strsub( brand, "Neo" );
    stash.br.athlon_fx = strsub( brand, "Athlon(tm) 64 FX" );
    stash.br.athlon_mp = strsub( brand, "Athlon(tm) MP" );
    stash.br.duron_mp  = strsub( brand, "Duron(tm) MP" );
    stash.br.opteron   = strsub( brand, "Opteron" );
    stash.br.fx        = strsub( brand, "AMD FX" );
    stash.br.firepro   = strsub( brand, "Firepro" ); // total guess
    stash.br.ultra     = strsub( brand, "Ultra" );
    stash.br.t_suffix  = strreg( brand, "[0-9][0-9][0-9][0-9]T" );
    stash.br.ryzen     = strsub( brand, "Ryzen" );
    stash.br.epyc      = strsub( brand, "EPYC" );
    stash.br.epyc_3000 = strreg( brand, "EPYC 3[0-9][0-9][0-9]" );
    stash.br.threadripper = strsub( brand, "Threadripper" );

    stash.br.embedded   = strsub( brand, "Embedded" );
    stash.br.embedded_V = strsub( brand, "Embedded V" );
    stash.br.embedded_R = strsub( brand, "Embedded R" );
   
    if ( strsub( brand, "Dual Core" ) || strsub( brand, " X2 ") ) 
        {
        stash.br.cores = 2;
        }
    else if ( strsub( brand, "Triple-Core" ) || strsub( brand, " X3 ") )
        {
        stash.br.cores = 3;
        } 
    else if ( strsub( brand, "Quad-Core" ) || strsub( brand, " X4 " ) )
        {
        stash.br.cores = 4;
        } 
    else if ( strsub( brand, "Six-Core" ) || strsub( brand, " X6 " ) )
        {
        stash.br.cores = 6;
        } 
    else 
        {
        stash.br.cores = 0; // means unspecified by the brand string
        }

    stash.br.mediagx = strsub( brand, "MediaGXtm" );

    stash.br.c7      = strsub( brand, "C7" );
    stash.br.c7m     = strsub( brand, "C7-M" );
    stash.br.c7d     = strsub( brand, "C7-D" );
    stash.br.eden    = strsub( brand, "Eden" );
    stash.br.zhaoxin = strsub( brand, "ZHAOXIN" );
    }
}
