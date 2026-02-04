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

Special thanks to:
https://refactoring.guru/design-patterns/singleton/java/example#example-2
https://refactoring.guru/java-dcl-issue
about Singleton pattern.

Processors and Hypervisors data base public interface class:
access methods set.

*/

package cpuidv3.servicecpudata;

import static cpuidv3.servicecpudata.ServiceCpudata.VENDOR_T.*;
import java.util.ArrayList;

public final class ServiceCpudata 
{
    // Thread-safe singleton pattern for ServiceCpudata class.
    private static volatile ServiceCpudata instance;
    public static ServiceCpudata getInstance()
    {
        ServiceCpudata result = instance;
        if ( result != null )
        {
            return result;
        }
        // Some redundant operations required for thread-safe, see links above.
        synchronized( ServiceCpudata.class ) 
        {
            if ( instance == null ) 
            {
                instance = new ServiceCpudata();
            }
            return instance;
        }
    }
    // For singleton, constructor must be private.
    // Dump not set in the constructor, for easy reload.
    private ServiceCpudata()
    {
        vdp = new VendorDetectPhysical();
        vdv = new VendorDetectVirtual();
        vip = new VendorInitPhysical();
        viv = new VendorInitVirtual();
        ved = new VendorDump();
    }

    // CPU and Hypervisors vendors data.
    
    public enum VENDOR_T
    { VENDOR_UNKNOWN,
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
      VENDOR_ZHAOXIN,
      VENDOR_MONTAGE }
    
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
      { "  Shanghai  " , "Zhaoxin"   } ,
      { "GenuineIntel" , "Montage"   } };

    public enum HYPERVISOR_T
    { HYPERVISOR_UNKNOWN,
      HYPERVISOR_VMWARE,
      HYPERVISOR_XEN,
      HYPERVISOR_KVM,
      HYPERVISOR_ORACLE,
      HYPERVISOR_MICROSOFT,
      HYPERVISOR_ACRN }
    
    final static String[][] V_SIGN =
    { { null                 , "unknown"   } ,
      { "VMwareVMware"       , "VMware"    } ,
      { "XenVMMXenVMM"       , "Xen"       } ,
      { "KVMKVMKVM"          , "KVM"       } ,
      { "VBoxVBoxVBox"       , "Oracle"    } ,
      { "Microsoft Hv"       , "Microsoft" } ,
      { "ACRNACRNACRN"       , "ACRN"      } };

    // Fields, initialized by constructor: classes for
    // detection and support processors and hypervisor.
    // TODO. Make better sequence explicity.
    // TODO. Make better naming.
    
    private final VendorDetectPhysical vdp;
    private final VendorDetectVirtual vdv;
    private final VendorInitPhysical vip;
    private final VendorInitVirtual viv;
    private final VendorDump ved;

    // Fields, initialized by setters and other methods (not constructor),
    // for easy reload.
    
    private VendorStash stash;
    
    private String   physicalVendor;
    private String   virtualVendor;
    private Phandler handler;

    private Brand brand;
    private Microarchitecture microarchitecture;
    private Synth synth;
    private Model model;
    
    // Two methods earlyCpuidDump() and setCpuidDump() required if
    // vendor-specific CPU initializations used for re-read more detail
    // CPUID information.
    public void earlyCpuidDump( EntryCpuidSubfunction[] e )
    {
        vdp.setCpuidDump( e );
        vdv.setCpuidDump( e );
        vdp.earlyCPU();
        vdv.earlyVMM();
    }

    public void setCpuidDump( EntryCpuidSubfunction[] e )
    {
        vdp.setCpuidDump( e );
        vdv.setCpuidDump( e );

        // Stash cleared by setCpuidDump(), need re-create.
        stash = new VendorStash();
        vdp.setStash( stash );
        vdv.setStash( stash );
        // Get and store parameters = f( dump ).
        physicalVendor   = vdp.detectCPU();
        virtualVendor    = vdv.detectVMM();
        
        stash.vendor     = vdp.getPvendor();
        stash.hypervisor = vdv.getHvendor();
        handler          = vdp.getPhandler();
        // Get and store classes for CPU model and parameters decoding.
        brand             = ( handler != null )&&( handler.gbr != null ) ?
                            handler.gbr.gBR() : null;
        microarchitecture = ( handler != null )&&( handler.gma != null ) ?
                            handler.gma.gMA() : null;
        synth             = ( handler != null )&&( handler.gsy != null ) ?
                            handler.gsy.gSY() : null;
        model             = ( handler != null )&&( handler.gmd != null ) ?
                            handler.gmd.gMD() : null;
    }
    
    public void buildStash( EntryCpuidSubfunction[] entries )
    {
        ved.extractFromDump( stash, entries );
        vip.buildStash( stash );
    }
    
    public VENDOR_T getPhysicalVendorEnum()    { return stash.vendor; }
    public HYPERVISOR_T getVirtualVendorEnum() { return stash.hypervisor; }
    
    public String getPhysicalVendorName()
    {
        String s = physicalVendor;
        if ( ( stash.vendor == VENDOR_VIA )&&( stash.br.zhaoxin ) )
        {   // Support special case VIA vs Zhaoxin.
            s = P_SIGN[ VENDOR_ZHAOXIN.ordinal() ][1];
        }
        else if ( ( stash.vendor == VENDOR_INTEL )&&( stash.br.montage ) )
        {   // Support special case INTEL vs Montage.
            s = P_SIGN[ VENDOR_MONTAGE.ordinal() ][1];
        }
        return s; 
    }

    public String getVirtualVendorName()
    { 
        return virtualVendor; 
    }

    public String getBrand()
    { 
        int tfms = stash.val_1_eax;
        int bi   = stash.val_1_ebx;
        return ( brand != null ) ?
            brand.detect( tfms, bi ) : null; 
    }

    public String[] getSynth()
    {
        int stdTfms = stash.val_1_eax;
        int extTfms = stash.val_80000001_eax;
        int bi      = stash.val_1_ebx;
        return ( synth != null ) ?  
            synth.detect( stdTfms, extTfms, bi ) : null; 
    }

    public MData getMicroarchitecture()
    { 
        int tfms = stash.val_1_eax;
        int bi   = stash.val_1_ebx;
        return ( microarchitecture != null ) ?  
            microarchitecture.detect( tfms, bi ) : null;
    }

    public String[] getModel()
    { 
        return ( model != null ) ? 
            model.detect( stash ) : null;
    }
    
// Added methods for CPUID summary info.
// This additions for ServiceCpudata.java and ServiceCpuid.java classes.
    
    public int getStashSmt()
    {
        int result = stash.getMpHyperthreads();
        if ( result <= 0)
        {
            result = 1;
        }
        return result;
    }
    
    public void appendSummaryVendorInfo( ArrayList<String[]> a )
    {
        appendSummaryVendorInfo( a, null );
    }
    
    public void appendSummaryVendorInfo
        ( ArrayList<String[]> a, String hybridName )
    {
        // Get strings for visualization as additional summary.
        String nameP = getPhysicalVendorName();
        String nameV = getVirtualVendorName();
        String nameB = getBrand();

        String[] synSt = getSynth();
        String synth1 = null;
        String synth2 = null;
        if ( ( synSt != null )&&( synSt.length >= 1 )&&( synSt[0] != null ) )
        {
            synth1 = synSt[0];
        }
        if ( ( synSt != null )&&( synSt.length >= 2 )&&( synSt[1] != null ) )
        {
            synth2 = synSt[1];
        }
        
        MData mdata = getMicroarchitecture();
        String uarch = null;
        String physc = null;
        String family = null;
        if ( mdata != null )
        {
            uarch = mdata.u;
            physc = mdata.p;
            if ( ( mdata.f != null ) && ( mdata.u != null ) &&
                 ( ! mdata.c ) && ( !( mdata.f.equals( mdata.u ) ) ) )
            {
                family = mdata.f;
            }
            else if ( ( mdata.u == null ) && ( ! mdata.c ) )
            {
                family = mdata.f;
            }
        }

        String[] modelSynth = getModel();
        String msynth = null;
        if ( ( modelSynth != null )&&( modelSynth.length >= 4 ) )
        {
            msynth = modelSynth[3];
        }

        // Additional summary strings visualization.
        if ( ( nameP != null )||( nameV != null ) )
        {
            String[] interval = new String[] { "", "" };
            a.add( interval );
            
            String processorVendor = "Processor vendor";
            if( hybridName != null )
            {
                processorVendor = "[ " + hybridName + " ]  " + processorVendor;
            }
            
            if ( nameP != null )
                a.add( new String[] { processorVendor , nameP } );
            if ( nameV != null )
                a.add( new String[] { "Hypervisor vendor" , nameV } );
            if ( nameB != null )
                a.add( new String[] { "Brand Index" , nameB } );
            
            boolean physOnce = false;
            if ( ( family != null )&&( physc != null ) )
                { physOnce = true;
                  String temp = String.format
                    ( "%s ( %s )", family, physc );
                  a.add( new String[] 
                    { "Family and physical" , temp } );  }
            else if ( family != null )
                a.add( new String[] { "Family" , family } );
            
            if ( ( ! physOnce )&&( uarch != null )&&( physc != null ) )
                { String temp = String.format
                    ( "%s ( %s )", uarch, physc );
                  a.add( new String[] 
                    { "Microarchitecture and physical" , temp } );  }
            else if ( uarch != null )
                a.add( new String[] { "Microarchitecture" , uarch } );
            
            if ( synth1 != null )
                a.add( new String[] { "Model" , synth1 } );
            if ( synth2 != null )
                a.add( new String[] { "Model (extended)" , synth2 } );
            if ( msynth != null )
                a.add( new String[] { "Model (reconstructed)" , msynth } );
        }
    }
        
    public void appendSummaryVendorMpInfo( ArrayList<String[]> a )
    {
        String nameP = getPhysicalVendorName();
        String nameV = getVirtualVendorName();
        if ( ( nameP != null )||( nameV != null ) )
        {
            final String nameMP = stash.getMpMethod();
            final int mpc = stash.getMpCores();
            final int mph = stash.getMpHyperthreads();
            final int mpu = stash.getMpUnits();

            if ( ( nameMP != null )&&( mpc > 0 )&&( mph > 0 ))
            {
                String[] interval = new String[] { "", "" };
                a.add( interval );

                if ( ( !stash.hybridCheck )||
                     ( !( stash.bigCore || stash.smallCore ) ) )
                {
                    String sm;
                    if ( mpu > 1 )
                    {  // MP topology with units.
                        sm = String.format
                            ( "%s ( %d cores, %d threads, %d units )", 
                            nameMP, mpc * mpu, mpc * mpu * mph, mpu );
                    }
                    else
                    { // MP topology without units.
                        sm = String.format( "%s ( %d cores, %d threads )", 
                            nameMP, mpc, mpc * mph );
                    }
                    a.add( new String[] { "MP enumeration method" , sm } );  
                }
                else
                {
                    a.add( new String[]{ "Hybrid topology", "detected" } );
                }
            }
        }
    }
}
