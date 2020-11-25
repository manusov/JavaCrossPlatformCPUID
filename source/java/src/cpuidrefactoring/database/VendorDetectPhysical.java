/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
This file contains Processors and Hypervisors
data exported from Todd Allen CPUID project.
Some variables and functions names not compliant with java
naming conventions, this fields using original C/C++ naming.
-----------------------------------------------
Vendor detector for physical procesors.
Plus, data base management methods.
*/

package cpuidrefactoring.database;

class VendorDetectPhysical 
{
private DatabaseStash stash;

VendorDetectPhysical( DatabaseStash stash )
    {
    this.stash = stash;
    }
    
public enum VENDOR_T
    {
    VENDOR_UNKNOWN,
    VENDOR_INTEL,
    VENDOR_AMD,
    VENDOR_CYRIX,
    VENDOR_VIA,
    VENDOR_TRANSMETA,
    VENDOR_UMC,
    VENDOR_NEXGEN,
    VENDOR_RISE,
    VENDOR_SIS,
    VENDOR_NSC,
    VENDOR_VORTEX,
    VENDOR_RDC,
    VENDOR_HYGON,
    VENDOR_ZHAOXIN
    };

final static String[][] P_SIGN =
    { { null           , "unknown"   } ,
      { "GenuineIntel" , "Intel"     } ,
      { "AuthenticAMD" , "AMD"       } ,
      { "CyrixInstead" , "Cyrix"     } ,
      { "CentaurHauls" , "VIA"       } ,
      { "GenuineTMx86" , "Transmeta" } ,
      { "UMC UMC UMC " , "UMC"       } ,
      { "NexGenDriven" , "Nexgen"    } ,
      { "RiseRiseRise" , "Rise"      } ,
      { "SiS SiS SiS " , "SiS"       } ,
      { "Geode by NSC" , "NSC"       } ,
      { "Vortex86 SoC" , "Vortex"    } ,
      { "Genuine  RDC" , "RDC"       } ,
      { "HygonGenuine" , "Hygon"     } ,
      { "  Shanghai  " , "Zhaoxin"   } };

private final Phandler[] P_HAND =
    { 
    new Phandler( null, null, null, null ),
    new Phandler( () -> { return new IntelBrandId(); }, 
                  () -> { return new IntelMicroarchitecture( stash ); }, 
                  () -> { return new IntelSynth( stash ); },
                  null ),
    new Phandler( null, 
                  () -> { return new AmdMicroarchitecture( stash ); }, 
                  () -> { return new AmdSynth( stash ); },
                  () -> { return new AmdModel(); } ),
    new Phandler( null, 
                  () -> { return new CyrixMicroarchitecture( stash ); }, 
                  () -> { return new CyrixSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  () -> { return new ViaMicroarchitecture( stash ); }, 
                  () -> { return new ViaSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  () -> { return new TransmetaMicroarchitecture( stash ); }, 
                  () -> { return new TransmetaSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  () -> { return new UmcMicroarchitecture( stash ); }, 
                  () -> { return new UmcSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  () -> { return new NexgenMicroarchitecture( stash ); }, 
                  () -> { return new NexgenSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  () -> { return new RiseMicroarchitecture( stash ); }, 
                  () -> { return new RiseSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  () -> { return new SisMicroarchitecture( stash ); }, 
                  () -> { return new SisSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  null, 
                  () -> { return new NscSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  null, 
                  () -> { return new VortexSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  null, 
                  () -> { return new RdcSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  () -> { return new HygonMicroarchitecture( stash ); }, 
                  () -> { return new HygonSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  () -> { return new ZhaoxinMicroarchitecture( stash ); }, 
                  () -> { return new ZhaoxinSynth( stash ); }, 
                  null )
    };

private VENDOR_T pVendor = null;
private String   pSign = null;
private String   pName;
private Phandler pHandler;

VENDOR_T getPvendor()  { return pVendor;  }
Phandler getPhandler() { return pHandler; }

String detectPhysical( String pattern )
    {
    VENDOR_T[] pv = VENDOR_T.values();
    for ( int i=0; ( i < pv.length )&&( pSign == null ); i++ )
        {
        switch ( pv[i] )
            {
            case VENDOR_UNKNOWN:
                pVendor  = pv[i];
                pSign    = P_SIGN[i][0];
                pName    = P_SIGN[i][1];
                pHandler = P_HAND[i];
                break;
            case VENDOR_INTEL:
            case VENDOR_AMD:
            case VENDOR_CYRIX:
            case VENDOR_VIA:
            case VENDOR_TRANSMETA:
            case VENDOR_UMC:
            case VENDOR_NEXGEN:
            case VENDOR_RISE:
            case VENDOR_SIS:
            case VENDOR_NSC:
            case VENDOR_VORTEX:
            case VENDOR_RDC:
            case VENDOR_HYGON:
            case VENDOR_ZHAOXIN:
                if ( P_SIGN[i][0].equals( pattern ) )
                    {
                    pVendor   = pv[i];
                    pSign     = P_SIGN[i][0];
                    pName     = P_SIGN[i][1];
                    pHandler  = P_HAND[i];
                    }
            }
        }
    return pName;
    }
}


