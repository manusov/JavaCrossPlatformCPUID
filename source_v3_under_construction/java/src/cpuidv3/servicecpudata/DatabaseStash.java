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

Processors and Hypervisors data base public interface class:
processor description variables and flags.

*/

package cpuidv3.servicecpudata;

import cpuidv3.servicecpudata.VendorDetectPhysical.VENDOR_T;
import cpuidv3.servicecpudata.VendorDetectVirtual.HYPERVISOR_T;

public class DatabaseStash 
{

VENDOR_T      vendor     = null;
HYPERVISOR_T  hypervisor = null;

// this public part of class contains fields, loaded from dump
// and fields, set as dump analyzing results.
// this public part must be actual before buildStash() method call.

public boolean  saw_4  = false;    // Means CPUID function 00000004h supported.
public boolean  saw_b  = false;    // Means CPUID function 0000000Bh supported.
public boolean  saw_1f = false;    // Means CPUID function 0000001Fh supported.

public boolean hybridCheck = false;   // Intel Hybrid technology support.
public boolean bigCore     = false;
public boolean smallCore   = false;
public boolean zeroCore    = false;

public int    val_0_eax = 0;
public int    val_1_eax = 0;
public int    val_1_ebx = 0;
public int    val_1_ecx = 0;
public int    val_1_edx = 0;
public int    val_2_eax = 0;
public int    val_2_ebx = 0;
public int    val_2_ecx = 0;
public int    val_2_edx = 0;
public int    val_4_eax = 0;
public int[]  val_b_eax  = new int[2];
public int[]  val_b_ebx  = new int[2]; 
public int[]  val_1f_eax = new int[6];
public int[]  val_1f_ebx = new int[6];
public int[]  val_1f_ecx = new int[6];

public int    val_80000001_eax = 0;
public int    val_80000001_ebx = 0;
public int    val_80000001_ecx = 0;
public int    val_80000001_edx = 0;
public int    val_80000008_ecx = 0;
public int    val_8000001e_ebx = 0;
public int    val_80000006_ecx = 0;

public String brand          = null;
public String transmeta_info = null;
public String override_brand = null;
public String soc_brand      = null;

// internal (non-public) part, but with getters for some parameters,
// requires externally read, but not requires externally write
public String getMpMethod()    { return mp.method;       }
public int getMpCores()        { return mp.cores;        }
public int getMpHyperthreads() { return mp.hyperthreads; }
public int getMpUnits()        { return mp.units;        }

public int transmeta_proc_rev = 0;

MP mp = new MP();
class MP
    {
    String method       = null;
    int    cores        = 0;
    int    hyperthreads = 0;
    int    units        = 0;
    }

BR br = new BR();
class BR
    {
    boolean mobile;
    
//  INTEL intel;
//  class INTEL
//      {
        boolean celeron    = false;
        boolean core       = false;
        boolean pentium    = false;
        boolean atom       = false;
        boolean xeon_mp    = false;
        boolean xeon       = false;
        boolean pentium_m  = false;
        boolean pentium_d  = false;
        boolean extreme    = false;
        boolean generic    = false;
        boolean scalable   = false;
        boolean u_line     = false;
        boolean y_line     = false;
        boolean g_line     = false;
        boolean i_8000     = false;
        boolean i_10000    = false;
        boolean cc150      = false;
        boolean core_ultra = false;
        // Montage Jintide, undocumented, only instlatx64 example
        boolean montage   = false;
//      }
    
//  AMD amd;
//  class AMD
//      {
        boolean athlon_lv = false;
        boolean athlon_xp = false;
        boolean duron     = false;
        boolean athlon    = false;
        boolean sempron   = false;
        boolean phenom    = false;
        boolean series    = false;
        boolean a_series  = false;
        boolean c_series  = false;
        boolean e_series  = false;
        boolean g_series  = false;
        boolean r_series  = false;
        boolean z_series  = false;
        boolean geode     = false;
        boolean turion    = false;
        boolean neo       = false;
        boolean athlon_fx = false;
        boolean athlon_mp = false;
        boolean duron_mp  = false;
        boolean opteron   = false;
        boolean fx        = false;
        boolean firepro   = false;
        boolean ultra     = false;
        boolean t_suffix  = false;
        boolean ryzen     = false;
        boolean epyc      = false;
        boolean epyc_3000 = false;
        boolean threadripper = false;
        boolean embedded_V   = false;
        boolean embedded_R   = false;

        boolean embedded  = false;
        int     cores     = 0;
//      }
    
//  CYRIX cyrix;
//  class CYRIX
//      {
        boolean mediagx = false;
//      }
    
//  VIA via;
//  class VIA
//      {
        boolean c7      = false;
        boolean c7m     = false;
        boolean c7d     = false;
        boolean eden    = false;
        boolean zhaoxin = false;
//      }
    }
    
BRI bri = new BRI();
class BRI
    {
    boolean desktop_pentium  = false;
    boolean desktop_celeron  = false;
    boolean mobile_pentium   = false;
    boolean mobile_pentium_m = false;
    boolean mobile_celeron   = false;
    boolean xeon_mp          = false;
    boolean xeon             = false;
    }
    
                               /* ==============implications============== */
                               /* PII (F6, M5)            PIII (F6, M7)    */
                               /* ----------------------  ---------------  */
boolean L2_4w_1Mor2M = false;  /* Xeon                    Xeon             */
boolean L2_4w_512K   = false;  /* normal, Mobile or Xeon  normal or Xeon   */
boolean L2_4w_256K   = false;  /* Mobile                   -               */
boolean L2_8w_1Mor2M = false;  /*  -                      Xeon             */
boolean L2_8w_512K   = false;  /*  -                      normal           */
boolean L2_8w_256K   = false;  /*  -                      normal or Xeon   */
        /* none */             /* Celeron                  -               */

boolean L2_2M        = false;  /* Nocona lacks, Irwindale has */
                               /* Conroe has more, Allendale has this */
boolean L2_6M        = false;  /* Yorkfield C1/E0 has this, M1/R0 has less */
boolean L3           = false;  /* Cranford lacks, Potomac has */

boolean L2_256K      = false;  /* Barton has more, Thorton has this */
boolean L2_512K      = false;  /* Toledo has more, Manchester E6 has this */

}
