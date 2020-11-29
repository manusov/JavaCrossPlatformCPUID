/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
This file contains Processors and Hypervisors
data exported from Todd Allen CPUID project.
Some variables and functions names not compliant with java
naming conventions, this fields using original C/C++ naming.
-----------------------------------------------
Vendor detector for virtual machine monitors (hypervisors).
Plus, data base management methods.
*/

package cpuidrefactoring.database;

public class VendorDetectVirtual 
{
/*
This static entry point used for early hypervisor vendor detection, 
required for:
1) Re-load dump with vendor-specific functions try, note unconditional
   use of vendor-specific functions can cause hardware failures.
2) Select or control vendor-specific leafs of CPUID instruction, note
   vendor type argument required for some parsings and must be valid
   before paesings.
pattern = hypervisor vendor signature.
*/
private static HYPERVISOR_T savedResult = null;
public static  HYPERVISOR_T getSavedResult() { return savedResult; }
public static  HYPERVISOR_T earlyDetect( String signature )
    {
    savedResult = null;
    HYPERVISOR_T[] pv = HYPERVISOR_T.values();
    for ( HYPERVISOR_T value : pv ) 
        {
        String pattern = V_SIGN[ value.ordinal()][0];
        if ( pattern == null )
            {
            if ( signature == null )
                {
                savedResult = null;
                break;
                }
            }
        else if ( pattern.equals( signature ) ) 
            {
            savedResult = value;
            break;
            }
        }
    return savedResult;
    }
    
public enum HYPERVISOR_T
    {
    HYPERVISOR_UNKNOWN,
    HYPERVISOR_VMWARE,
    HYPERVISOR_XEN,
    HYPERVISOR_KVM,
    HYPERVISOR_ORACLE,
    HYPERVISOR_MICROSOFT,
    }

final static String[][] V_SIGN =
    { { null                 , "unknown"   } ,
      { "VMwareVMware"       , "VMware"    } ,
      { "XenVMMXenVMM"       , "Xen"       } ,
      { "KVMKVMKVM"          , "KVM"       } ,
      { "VBoxVBoxVBox"       , "Oracle"    } ,
      { "Microsoft Hv"       , "Microsoft" } };

private HYPERVISOR_T vVendor = null;
private String vSign = null;
private String vName;

HYPERVISOR_T getVvendor() { return vVendor; }

String detectVirtual( String pattern )
    {
    HYPERVISOR_T[] vv = HYPERVISOR_T.values();
    for ( int i=0; ( i < vv.length )&&( vSign == null ); i++ )
        {
        switch ( vv[i] )
            {
            case HYPERVISOR_UNKNOWN:
                vVendor  = vv[i];
                vSign    = V_SIGN[i][0];
                vName    = V_SIGN[i][1];
                break;
            case HYPERVISOR_VMWARE:
            case HYPERVISOR_XEN:
            case HYPERVISOR_KVM:
            case HYPERVISOR_MICROSOFT:
            case HYPERVISOR_ORACLE:
                if ( pattern == null )
                    {
                    vVendor   = null;
                    vSign     = null;
                    vName     = null;
                    }
                else if ( V_SIGN[i][0].equals( pattern ) )
                    {
                    vVendor   = vv[i];
                    vSign     = V_SIGN[i][0];
                    vName     = V_SIGN[i][1];
                    }
            }
        }
    return vName;
    }
    
}
