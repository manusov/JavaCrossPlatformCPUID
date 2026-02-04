/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

SAL = Service Abstraction Layer. DL = Dump loader mode.
This variant for "Dump loader only" application build.
See also child class SALHW.java for "Physical platform with dump loader"
application build.

*/

package cpuidv3.sal;

import cpuidv3.pal.PAL.PAL_STATUS;
// import javax.swing.tree.DefaultMutableTreeNode;
// import javax.swing.tree.DefaultTreeModel;

public class SALDL extends SAL
{
    @Override public boolean getDumpLoaderBuild() { return true; }
    
    @Override public PAL_STATUS getPalStatus()
    {
        return PAL_STATUS.NOT_REQUIRED;
    }
    
    @Override public String getRuntimeName()       
    { 
        return "Dump loader only";   
    }
    
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
    // For singleton constructor must be private.
    private SALDL() { }
    
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
        if( index != SERVICE_ID.CPUID_FILE.ordinal() )
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
/*            
            String[] up = new String[]{ "" };
            String[][] data = new String[][]
                {{ "This CPUID application build as hex dump loader only, please load hex dump file." }};
            return new ChangeableTableModel( up, data );
*/
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
/*            
            String[] up = new String[] { "Parameter", "Value" };
            String[][] data = new String[][]
                { { "dump not loaded", "dump not loaded" } };
            ChangeableTableModel model = new ChangeableTableModel( up, data );

            TreeEntry entryRoot = new TreeEntry
                ( " [ Dump not loaded. ] ", "", false, false );
            DefaultMutableTreeNode dmtnRoot = 
                new DefaultMutableTreeNode( entryRoot, true );
            
            String[] tNames = new String[]{ "?" };

            ChangeableTableModel[] tables1 = 
                new ChangeableTableModel[]{ model };
            ChangeableTableModel[] tables2 = 
                new ChangeableTableModel[]{ model };

            DefaultTreeModel treeModel = new DefaultTreeModel( dmtnRoot, true );
            return new TreeScreenModel( treeModel, tNames, tables1, tables2 );
*/
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
/*            
            String[] up = new String[]{ "" };
            String[][] data = new String[][]
                {{ "This CPUID application build as hex dump loader only, please load hex dump file." }};
            return new ChangeableTableModel( up, data );
*/
            return null;
        }
    }
}
