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
    String[] interval = new String[] { "", "" };
    if ( ( signP != null )||( signV != null ) )
        {
        DatabaseManager manager = new DatabaseManager( signP, signV );
        DatabaseStash stash = manager.getStash();
        EntryCpuid[] e = container.buildEntries( TFMS_AND_BRAND_CPUID );
        if ( ( e != null )&&( e.length >= 1 ) )
            {
            stash.val_1_eax = e[0].eax;
            stash.val_1_ebx = e[0].ebx;
            }
        stash.brand = model;
        manager.buildStash();
        
        String nameP = manager.getPhysicalVendor();
        String nameV = manager.getVirtualVendor();
        String nameB = manager.getBrand();
        if ( ( nameP != null )||( nameV != null ) )
            {
            a.add( interval );
            if ( nameP != null )
                {
                a.add( new String[] { "Processor vendor" , nameP } );
                }
            if ( nameV != null )
                {
                a.add( new String[] { "Hypervisor vendor" , nameV } );
                }
            if ( nameB != null )
                {
                a.add( new String[] { "Brand Index" , nameB } );
                }
            }
        }
/*
End of database usage 3 of 3 = Late vendor detection.
*/                
   
    /*
    TODO.
    Yet Brand Index only supported by database. Add:
        - microarchitecture,
        - family, 
        - physical characteristics, 
        - synth string,
        - model 
        - and other database functionality.
    */

    
    // return default string ( if empty ) or
    // generated strings array ( if not empty )
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
