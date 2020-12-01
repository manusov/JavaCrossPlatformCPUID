/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Summary Information,
Processor name string, Vendor String, 
maximum standard and extended functions.
*/

package cpuidrefactoring.devicecpuid;

import cpuidrefactoring.database.DatabaseManager;
import cpuidrefactoring.database.DatabaseStash;
import cpuidrefactoring.database.MData;
import java.util.ArrayList;

class CpuidSummary extends SummaryCpuid
{

@Override String getShortName() 
    { return "CPUID Summary"; }

@Override String getLongName() 
    { return "Show CPUID information as main parameters summary"; }

private final static int BASE_STANDARD_CPUID  = 0x00000000;
private final static int BASE_EXTENDED_CPUID  = 0x80000000;
private final static int BASE_VIRTUAL_CPUID   = 0x40000000;
private final static int NAME_STRING_CPUID    = 0x80000002;

private final static int TFMS_AND_BRAND_CPUID = 0x00000001;
private final static int EXTENDED_TFMS        = 0x80000001;
private final static int CORE_PHYSICAL        = 0x80000008;

private final static int CACHE_DESCRIPTORS    = 0x00000002;
private final static int CACHE_DETERMINISTIC  = 0x00000004;
private final static int EXTENDED_TOPOLOGY    = 0x0000000B;
private final static int V2_EXTENDED_TOPOLOGY = 0x0000001F;
private final static int AMD_MP_TOPOLOGY      = 0x8000001E;

@Override String[][] getParametersList()
    {
    ArrayList<String[]> a = new ArrayList<>();  // array of strings for screen
    String[][] s;                               // scratch pad
    
    // Some of this parameters also used later for data base calls
    String[] physicalVendor = null;
    String[] physicalModel  = null;
    String[] virtualVendor  = null;
    String[] virtualMax =  null;
    
    // Get and Write CPU model name string,    
    ReservedFunctionCpuid f = container.findFunction( NAME_STRING_CPUID );
    if ( f != null )
        {
        s = f.getParametersList();
        if ( ( s != null )&&( s.length >= 1 ) )
            {
            physicalModel = s[0];
            a.add( physicalModel );  // Write CPU model name string
            }
        }
    
    // Get Virtual CPUID parameters,
    // used later by strings order and for data base calls
    f = container.findFunction( BASE_VIRTUAL_CPUID );
    if ( f != null )
        {
        s = f.getParametersList();
        if ( ( s != null )&&( s.length >= 1 ) )
            {
            virtualVendor = s[1];
            }
        if ( ( s != null )&&( s.length >= 2 ) )
            {
            virtualMax = s[0];
            }
        }
    
    // Get and Write Standard CPUID parameters
    f = container.findFunction( BASE_STANDARD_CPUID );
    if ( f != null )
        {
        s = f.getParametersList();
        if ( ( s != null )&&( s.length >= 2 ) )
            {
            physicalVendor = s[1];
            a.add( physicalVendor );     // Write Physical CPU vendor string
            if ( virtualVendor != null )
                {
                a.add( virtualVendor );  // Write Virtual CPU vendor string
                }
            a.add( s[0] );               // Write Maximum standard CPUID level
            }
        }
    else
        {  // this for exotic variant: virtual vendor without physical vendor
        if ( virtualVendor != null )
            {
            a.add( virtualVendor );
            }
        }
    
    // Get and Write Maximum extended CPUID level
    f = container.findFunction( BASE_EXTENDED_CPUID );
    if ( f != null )
        {
        s = f.getParametersList();
        if ( ( s != null )&&( s.length >= 1 ) )
            {
            a.add( s[0] );  // Write Maximum extended CPUID level
            }
        }
    
    // Write Maximum virtual CPUID level
    if ( virtualMax != null )
        {
        a.add( virtualMax );
        }
    
    // Extract arguments parameters for call database
    String signP = null;
    String signV = null;
    String model = null;
    if ( ( physicalVendor != null )&&( physicalVendor.length >= 2 ) )
        {
        signP = physicalVendor[1];
        }
    if ( ( virtualVendor != null )&&( virtualVendor.length >= 2 ) )
        {
        signV = virtualVendor[1];
        }
    if ( ( physicalModel != null )&&( physicalModel.length >= 2 ) )
        {
        model = physicalModel[1];
        }

/*
Database usage 3 of 3 = Late vendor decoding.
See also: ApplicationCpuid.java , DeviceCpuid.java.
Initializing data base for CPU/Hypervisor vendor-specific late detection.
*/                

    if ( ( signP != null )||( signV != null ) )
        {
        DatabaseManager manager = new DatabaseManager( signP, signV );
        DatabaseStash stash = manager.getStash();
        
        // load from dump to stash: base standard CPUID
        EntryCpuid[] e = container.buildEntries( BASE_STANDARD_CPUID );
        if ( ( e != null )&&( e.length >= 1 ) )
            {
                stash.val_0_eax = e[0].eax;
            }        
        // load from dump to stash: type, family, model, stepping, brand index
        e = container.buildEntries( TFMS_AND_BRAND_CPUID );
        if ( ( e != null )&&( e.length >= 1 ) )
            {
            stash.val_1_eax = e[0].eax;
            stash.val_1_ebx = e[0].ebx;
            stash.val_1_ecx = e[0].ecx;
            stash.val_1_edx = e[0].edx;
            }
        
        // load from dump to stash: extended TFMS
        e = container.buildEntries( EXTENDED_TFMS );
        if ( ( e != null )&&( e.length >= 1 ) )
            {
            stash.val_80000001_eax = e[0].eax;
            stash.val_80000001_ebx = e[0].ebx;
            stash.val_80000001_ecx = e[0].ecx;
            stash.val_80000001_edx = e[0].edx;
            }
        
        // load from dump to stash: core physical parameters
        e = container.buildEntries( CORE_PHYSICAL );
        if ( ( e != null )&&( e.length >= 1 ) )
            {
            stash.val_80000008_ecx = e[0].ecx;
            }
        
        // load from dump to stash: cache descriptors data by function 02h
        e = container.buildEntries( CACHE_DESCRIPTORS );
        if ( ( e != null )&&( e.length >= 1 ) )
            {
                stash.val_2_eax = e[0].eax;
                stash.val_2_ebx = e[0].ebx;
                stash.val_2_ecx = e[0].ecx;
                stash.val_2_edx = e[0].edx;
            }
        
        // load from dump to stash: cache and MP topology by function 04h
        e = container.buildEntries( CACHE_DETERMINISTIC );
        if ( ( e != null )&&( e.length >= 1 ) )
            {
            if ( e[0].eax != 0 )
                {
                stash.val_4_eax = e[0].eax;
                stash.saw_4 = true;
                }
            }
        
        // load from dump to stash: cache and MP topology by function 0Bh
        e = container.buildEntries( EXTENDED_TOPOLOGY );
        if ( ( e != null )&&( e.length >= 1 ) )
            {
            for( int i=0; i<e.length; i++ )
                {
                if ( i < stash.val_b_eax.length )
                    {
                    stash.val_b_eax[i] = e[i].eax;
                    stash.saw_b = true;
                    }
                if ( i < stash.val_b_ebx.length )
                    {
                    stash.val_b_ebx[i] = e[i].ebx;
                    stash.saw_b = true;
                    }
                }
            }
        
        // load from dump to stash: cache and MP topology by function 1Fh
        e = container.buildEntries( V2_EXTENDED_TOPOLOGY );
        if ( ( e != null )&&( e.length >= 1 ) )
            {
            for( int i=0; i<e.length; i++ )
                {
                if ( i < stash.val_1f_eax.length )
                    {
                    stash.val_1f_eax[i] = e[i].eax;
                    stash.saw_1f = true;
                    }
                if ( i < stash.val_1f_ebx.length )
                    {
                    stash.val_1f_ebx[i] = e[i].ebx;
                    stash.saw_1f = true;
                    }
                if ( i < stash.val_1f_ecx.length )
                    {
                    stash.val_1f_ecx[i] = e[i].ecx;
                    stash.saw_1f = true;
                    }
                }
            }

        // load from dump to stash: AMD MP topology
        e = container.buildEntries( AMD_MP_TOPOLOGY );
        if ( ( e != null )&&( e.length >= 1 ) )
            {
                stash.val_8000001e_ebx = e[0].ebx;
            }
        
        // set model string at stash, used for parsing and keywords detection
        stash.brand = model;
        
        // initializing private stash fields = f( public stash fields )
        manager.buildStash();
        
        // get strings for visualization as additional summary
        String nameP = manager.getPhysicalVendor();
        String nameV = manager.getVirtualVendor();
        String nameB = manager.getBrand();
        
        String nameMP = stash.getMpMethod();
        int mpc = stash.getMpCores();
        int mph = stash.getMpHyperthreads();
        
        String[] synth = manager.getSynth();
        String synth1 = null;
        String synth2 = null;
        if ( ( synth != null )&&( synth.length >= 1 )&&( synth[0] != null ) )
            {
            synth1 = synth[0];
            }
        if ( ( synth != null )&&( synth.length >= 2 )&&( synth[1] != null ) )
            {
            synth2 = synth[1];
            }
        
        MData mdata = manager.getMicroarchitecture();
        String uarch = null;
        String physc = null;
        if ( mdata != null )
            {
            uarch = mdata.u;
            physc = mdata.p;
            }
        
        // additional summary strings visualization
        String[] interval = new String[] { "", "" };
        if ( ( nameP != null )||( nameV != null ) )
            {
            a.add( interval );
            if ( nameP != null )
                a.add( new String[] { "Processor vendor" , nameP } );
            if ( nameV != null )
                a.add( new String[] { "Hypervisor vendor" , nameV } );
            if ( nameB != null )
                a.add( new String[] { "Brand Index" , nameB } );
            if ( synth1 != null )
                a.add( new String[] { "Model" , synth1 } );
            if ( synth2 != null )
                a.add( new String[] { "Model (extended)" , synth2 } );
            
            if ( ( uarch != null )&&( physc != null ) )
                { String temp = String.format
                    ( "%s ( %s )", uarch, physc );
                  a.add( new String[] 
                    { "Microarchitecture and physical" , temp } );  }
            else if ( uarch != null )
                a.add( new String[] { "Microarchitecture" , uarch } );
            
            if ( ( nameMP != null )&&( mpc > 0 )&&( mph > 0 ) )
                { String temp = String.format
                    ( "%s ( %d cores, %d threads )", nameMP, mpc, mpc * mph );
                  a.add( new String[] { "MP enumeration method" , temp } );  }
            }
        }

/*
End of database usage 3 of 3 = Late vendor detection.
*/                
   
/*

TODO.
Yet Brand Index only supported by database. Add:
  + synth string,
  - microarchitecture,
  - family, 
  - physical characteristics, 
  - model
  + MP topology method
  + extracted core/ht count (?) rename this name or combine with previous
  - and other database functionality.

*/

    
    // return default string ( if empty ) or
    // generated strings array ( if not empty )
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
