/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Special thanks to:
https://refactoring.guru/design-patterns/singleton/java/example#example-2
https://refactoring.guru/java-dcl-issue
about Singleton pattern.

SAL = Service Abstraction Layer. DL = Dump loader mode.
This variant for "Dump loader only" application build.
See also child class SALHW.java for "Physical platform with dump loader"
application build.

*/

package cpuidv3.sal;

import cpuidv3.pal.PAL;

public class SALDL extends SAL
{
    // Thread-safe singleton pattern for PAL class.
    private static volatile SALDL instance;
    public static SALDL getInstance()
    {
        SALDL result = instance;
        if ( result != null )
        {
            return result;
        }
        // Some redundant operations required for thread-safe, see links above.
        synchronized( SALDL.class ) 
        {
            if ( instance == null ) 
            {
                instance = new SALDL();
            }
            return instance;
        }
    }
    // For singleton class, constructor must be private.
    private SALDL() { }
    
    @Override public boolean getDumpLoaderBuild() { return true; }
    
    @Override public PAL.PAL_STATUS getPalStatus()
    {
        return PAL.PAL_STATUS.NOT_REQUIRED;
    }
    
    @Override public String getRuntimeName()       
    { 
        return "Dump loader only";   
    }
    
    private final Service[] SERVICES =
    {
        null,
        new ServiceCpuidFile( this ),
        null,
        null,
        null,
        null,
        null,
        new Service( this )
    };

    @Override Service getService( SERVICE_ID id )
    {
        int index = id.ordinal();
        if( index == SERVICE_ID.CPUID_PHYSICAL.ordinal() )
        {   // This override added for service request from HelperSystemInfo.
            index = SERVICE_ID.CPUID_FILE.ordinal();
        }
        else if( index != SERVICE_ID.CPUID_FILE.ordinal() )
        {
            index = SERVICE_ID.UNKNOWN.ordinal();
        }
        return SERVICES[index];
    }
    
    // Public methods called from GUI depend on user actions.
    // Methods for support tools - dialogue boxes.

    @Override public void clearAllBinaryData()
    {
        Service service = getService( SERVICE_ID.CPUID_FILE );
        if ( service != null )
        {
            service.clearBinaryData();
        }
    }
    
    @Override public long[][] getCpuidBinaryData()
    {
        long[][] result = null;
        if ( overrideByFile )
        {
            Service service = getService( SERVICE_ID.CPUID_FILE );
            if ( service != null )
            {
                result = service.getBinaryData();
            }
        }
        return result;
    }
    
    @Override public ChangeableTableModel getSummaryTable()
    {
        if ( overrideByFile )
        {
            return super.getSummaryTable();
        }
        else
        {
            return null;
        }
    }
    
    @Override public TreeScreenModel getDetailsTree()
    {
        if ( overrideByFile )
        {
            return super.getDetailsTree();
        }
        else
        {
            return null;
        }
    }
    
    @Override public ChangeableTableModel getDumpTable()
    {
        if ( overrideByFile )
        {
            return super.getDumpTable();
        }
        else
        {
            return null;
        }
    }
}
