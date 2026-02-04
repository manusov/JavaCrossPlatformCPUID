/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Special thanks to:
https://refactoring.guru/design-patterns/singleton/java/example#example-2
https://refactoring.guru/java-dcl-issue
about Singleton pattern.

SAL = Service Abstraction Layer. HW = Hardware mode.
This variant for "Physical platform with dump loader" application build.
See also child class SALDL.java for "Dump loader only" application build.
Class interconnects service classes and PAL (Platform Abstraction Layer)
native libraries interface. Note SAL is high level, PAL is low level.

*/

package cpuidv3.sal;

import cpuidv3.pal.PAL;
import cpuidv3.pal.PAL.PAL_STATUS;
import static cpuidv3.sal.HelperTableToReport.tableReport;
import static cpuidv3.servicemp.ServiceMp.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class SALHW extends SAL
{
   // Thread-safe singleton pattern for SALHW class.
    private static volatile SALHW instance;
    public static SALHW getInstance( String libPath )
    {
        SALHW result = instance;
        if ( result != null )
        {
            return result;
        }
        // Some redundant operations required for thread-safe, see links above.
        synchronized( SALHW.class ) 
        {
            if ( instance == null ) 
            {
                instance = new SALHW( libPath );
            }
            return instance;
        }
    }
    // For singleton class, constructor must be private.
    private SALHW( String libPath )
    {
        pal = PAL.getInstance( libPath );
    }

    private final PAL pal;
    
    @Override PAL getPal()                     { return pal;                  }
    @Override public PAL_STATUS getPalStatus() { return pal.getPalStatus();   }
    @Override public String getRuntimeName()   { return pal.getRuntimeName(); }

    @Override public boolean getDumpLoaderBuild() { return false; }
    
    private final Service[] SERVICES =
    {
        new ServiceCpuidPhysical( this ),
        new ServiceCpuidFile( this ),
        new ServiceTopology( this ),
        new ServiceClocks( this ),
        new ServiceContext( this ),
        new ServiceOsInfo( this ),
        new ServiceJvmInfo( this ),
        new Service( this )
    };
    
    @Override Service getService( SERVICE_ID id )
    {
        int index = id.ordinal();
        int limit = SERVICE_ID.UNKNOWN.ordinal();
        if( index > limit )
        {
            index = limit;
        }
        return SERVICES[index];
    }
    
    // Public methods called from GUI depend on user actions.
    // Methods for support tools - dialogue boxes.
    
    @Override public boolean internalLoadAllBinaryData()
    {
        boolean loadStatus = true;
        for( Service service : SERVICES )
        {
            loadStatus &= service.internalLoadBinaryData();
        }
        return loadStatus;
    }
    
    @Override public boolean internalLoadNonAffinizedCpuid()
    {
        Service service = getService( SERVICE_ID.CPUID_PHYSICAL );
        return service.internalLoadNonAffinized();
    }

    @Override public void clearAllBinaryData()
    {
        for( Service service : SERVICES )
        {
            service.clearBinaryData();
        }
    }
    
    @Override public long[][] getCpuidBinaryData()
    {
        SERVICE_ID id;
        if ( overrideByFile )
        {
            id = SERVICE_ID.CPUID_FILE;
        }
        else
        {
            id = SERVICE_ID.CPUID_PHYSICAL;
        }
        Service service = getService( id );
        return service.getBinaryData();
    }
    
    @Override public boolean setCpuidBinaryData( long[][] data )
    {
        Service service = getService( SERVICE_ID.CPUID_PHYSICAL );
        service.setBinaryData( data );
        return true;
    }
    
    @Override public boolean setCpuidBinaryData( long[] loadedData )
    {
        boolean status = false;
        if ( ( loadedData != null )&&( loadedData.length > 0 )&&
                ( ( loadedData.length % 4 ) == 0) )
        {
            long[][] data = new long[][]{ loadedData };
            Service service = getService( SERVICE_ID.CPUID_FILE );
            service.setBinaryData( data );
            status = true;
        }        
        return status;
    }

    // Public methods called from GUI depend on user actions.    
    // Methods for support information panels.
    
    @Override public TreeScreenModel getSmpTree()
    {
        TreeScreenModel result = null;
        
        if (( serviceMp != null )&&( serviceMpStatus ))
        {
            String[] sNames = serviceMp.getShortNames();
            String[] lNames = serviceMp.getLongNames();
            String[][] lstUps = serviceMp.getListsUps();
            String[][][] lsts = serviceMp.getLists();
            String[][] dmpUps = serviceMp.getDumpsUps();
            String[][][] dmps = serviceMp.getDumps();
            int count = sNames.length;
            
            String[] tNames = new String[count];
            for( int i=0; i<count; i++ )
            {
                tNames[i] = sNames[i] + " = " + lNames[i];
            }
            
            ChangeableTableModel[] tables1 = new ChangeableTableModel[count];
            ChangeableTableModel[] tables2 = new ChangeableTableModel[count];
            for( int i=0; i<count; i++ )
            {
                tables1[i] = new ChangeableTableModel( lstUps[i], lsts[i] );
                tables2[i] = new ChangeableTableModel( dmpUps[i], dmps[i] );
            }
            
            TreeEntry entryRootPlatform = 
                new TreeEntry( serviceMpName, "", true, false );
            DefaultMutableTreeNode dmtnRootPlatform = 
                new DefaultMutableTreeNode( entryRootPlatform, true );
            
            TreeEntry entryCores =  // Child node 1 = Processor cores.
                new TreeEntry( "Processor cores", "", false, false );
            DefaultMutableTreeNode dmtnCores = 
                new DefaultMutableTreeNode( entryCores, true );
            dmtnRootPlatform.add( dmtnCores );
        
            TreeEntry entryCaches =  // Child node 2 = Caches.
                new TreeEntry( "Caches", "", false, false );
            DefaultMutableTreeNode dmtnCaches =
                new DefaultMutableTreeNode( entryCaches, true );
            dmtnRootPlatform.add( dmtnCaches );
        
            TreeEntry entryNuma =  // Child node 3 = NUMA domains.
                new TreeEntry( "NUMA domains", "", false, false );
            DefaultMutableTreeNode dmtnNuma = 
                new DefaultMutableTreeNode( entryNuma, true );
            dmtnRootPlatform.add( dmtnNuma );

            TreeEntry entryGroups =  // Child node 4 = Processor groups.
                new TreeEntry( "Processor groups", "", false, false );
            DefaultMutableTreeNode dmtnGroups = 
                new DefaultMutableTreeNode( entryGroups, true );
            dmtnRootPlatform.add( dmtnGroups );

            TreeEntry entryPackages =  // Child node 5 = Processor sockets.
                new TreeEntry( "Processor packages", "", false, false );
            DefaultMutableTreeNode dmtnPackages = 
                new DefaultMutableTreeNode( entryPackages, true );
            dmtnRootPlatform.add( dmtnPackages );

            int coreIndex = 0;
            for ( int i=0; i<count; i++ )
            {
                TreeEntry entry = new TreeEntry
                    ( i, sNames[i], lNames[i], false, true );

                // Detect node type, add to selected sub-tree.
                if ( sNames[i].startsWith( SN_PROCESSOR_CORE ) )
                {
                    DefaultMutableTreeNode node =
                        new DefaultMutableTreeNode( entry , true );
                    
                    for( int j=0; j<count; j++ )
                    {
                        TreeEntry entryThread = new TreeEntry
                              ( j, sNames[j], "", false, false );
                        
                        if ( sNames[j].startsWith
                                ( SN_PROCESSOR_THREAD + coreIndex ) )
                        {
                            DefaultMutableTreeNode nodeThread = new 
                                DefaultMutableTreeNode( entryThread , false );
                            node.add( nodeThread );
                        }
                    }
                    
                    dmtnCores.add( node );
                    coreIndex++;
                }
                else if ( sNames[i].startsWith( SN_CACHE ) )
                {
                    DefaultMutableTreeNode node =
                        new DefaultMutableTreeNode( entry , false );
                    dmtnCaches.add( node );
                }
                else if ( sNames[i].startsWith( SN_NUMA_NODE ) )
                {
                    DefaultMutableTreeNode node =
                        new DefaultMutableTreeNode( entry , false );
                    dmtnNuma.add( node );
                }
                else if ( sNames[i].startsWith( SN_PROCESSOR_GROUP ) )
                {
                    DefaultMutableTreeNode node =
                        new DefaultMutableTreeNode( entry , false );
                    dmtnGroups.add( node );
                }
                else if ( sNames[i].startsWith( SN_PROCESSOR_PACKAGE ) )
                {
                    DefaultMutableTreeNode node =
                        new DefaultMutableTreeNode( entry , false );
                    dmtnPackages.add( node );
                }
            }

            DefaultTreeModel treeModel =
                new DefaultTreeModel( dmtnRootPlatform , true );
            result = new TreeScreenModel( treeModel, tNames, tables1, tables2 );
        }

        return result;
    }

    @Override public ChangeableTableModel getClockTable()
    {
        Service service = getService( SERVICE_ID.CLOCKS );
        return new ChangeableTableModel
            ( service.getTableUp(), service.getTableData() );
    }

    @Override public ChangeableTableModel getContextTable()
    {
        Service service = getService( SERVICE_ID.CONTEXT );
        return new ChangeableTableModel
            ( service.getTableUp(), service.getTableData() );
    }

    @Override public ChangeableTableModel getOsTable()
    {
        Service service = getService( SERVICE_ID.OS_INFO );
        return new ChangeableTableModel
            ( service.getTableUp(), service.getTableData() );
    }

    @Override public ChangeableTableModel getJvmTable()
    {
        Service service = getService( SERVICE_ID.JVM_INFO );
        return new ChangeableTableModel
            ( service.getTableUp(), service.getTableData() );
    }

    private final static String CONSOLE_SUMMARY_NAME = 
        "Summary info.";
    private final static String CONSOLE_DETAILS_NAME = 
        "CPUID details list for first logical CPU.";
    private final static String CONSOLE_SMP_NAME = 
        "Platform SMP topology list.";
    private final static String KEY_CPUID = "00000000";
    private final static String KEY_THREAD = "thread";
        
    @Override public void consoleSummary()
    {
        // Console output for system information summary.
        String sTable = tableReport( getSummaryTable() );
        helperReportTable( CONSOLE_SUMMARY_NAME, sTable );
        
        // Console output for CPUID details tree information.
        TreeScreenModel treeScreenModel = getDetailsTree();
        String[] names = treeScreenModel.tablesPairsNames;
        ChangeableTableModel[] t1 = treeScreenModel.upTables;
        ChangeableTableModel[] t2 = treeScreenModel.downTables;
        int startIndex = -1;
        int endIndex = -1;
        if(( names != null )&&( t1 != null )&&( t2 != null )&&
           ( t1.length == names.length )&&( t2.length == names.length ))
        {
            for( int i=0; i<names.length; i++ )
            {
                if(( names[i].startsWith( KEY_CPUID ))&&( startIndex < 0 ))
                {
                    startIndex = i;  // First logical CPU, CPUID function 0.
                }
                else if ( names[i].startsWith( KEY_THREAD )&&
                        ( startIndex >= 0 ))
                {
                    endIndex = i;  // Next thread after first logical CPU.
                    break;
                }
            }
            helperReportTree
                ( CONSOLE_DETAILS_NAME, names, t1, t2, startIndex, endIndex );
        }
        
        // Console output for SMP topology tree information.
        treeScreenModel = getSmpTree();
        names = treeScreenModel.tablesPairsNames;
        t1 = treeScreenModel.upTables;
        t2 = treeScreenModel.downTables;
        if(( names != null )&&( t1 != null )&&( t2 != null )&&
           ( t1.length == names.length )&&( t2.length == names.length ))
        {
            startIndex = 0;
            endIndex = names.length;
            ChangeableTableModel[] t2a = 
                new ChangeableTableModel[names.length];
            t2a[0] = t2[0];  // Write dump table for first entry, other = null.
            helperReportTree
                ( CONSOLE_SMP_NAME, names, t1, t2a, startIndex, endIndex );
        }

        // Console output for other services.
        for( Service service : SERVICES )
        {
            service.printSummaryReport();
        }
    }
    
    private void helperReportTree( String name, String[] names, 
        ChangeableTableModel[] t1, ChangeableTableModel[] t2,
        int startIndex, int endIndex )
    {
        helperReportTable( name, null );
        for( int i=startIndex; i<endIndex; i++ )
        {
            if(( names[i] != null )&&( t1[i] != null ))
            {
                String sTable = tableReport( t1[i] );
                helperReportTable( names[i], sTable );
            }
            if(( names[i] != null )&&( t2[i] != null ))
            {
                String sTable = tableReport( t2[i] );
                helperReportTable( null, sTable );
            }
        }
    }

    private void helperReportTable( String sName, String sTable )
    {
        if( sName != null )
        {
            System.out.println( "[ " + sName + " ]\r\n" );
        }
        if( sTable != null )
        {
            System.out.println( sTable );
        }
    }
}
