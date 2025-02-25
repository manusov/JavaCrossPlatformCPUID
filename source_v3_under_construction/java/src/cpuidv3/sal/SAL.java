/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

SAL = Service Abstraction Layer.
Class interconnects service classes and PAL (Platform Abstraction Layer)
native libraries interface. Note SAL is up level, PAL is low level.

*/

package cpuidv3.sal;

import cpuidv3.pal.PAL;
import cpuidv3.pal.PAL.OS_TYPE;
import cpuidv3.pal.PAL.PAL_STATUS;
import cpuidv3.servicecpuid.DecoderCpuid;
import cpuidv3.servicecpuid.HybridReturn;
import cpuidv3.servicecpuid.IHybrid;
import cpuidv3.servicemp.DecoderOsMp;
import static cpuidv3.servicemp.DecoderOsMp.SN_CACHE;
import static cpuidv3.servicemp.DecoderOsMp.SN_NUMA_NODE;
import static cpuidv3.servicemp.DecoderOsMp.SN_PROCESSOR_CORE;
import static cpuidv3.servicemp.DecoderOsMp.SN_PROCESSOR_GROUP;
import static cpuidv3.servicemp.DecoderOsMp.SN_PROCESSOR_PACKAGE;
import static cpuidv3.servicemp.DecoderOsMp.SN_PROCESSOR_THREAD;
import cpuidv3.servicemplinux.DecoderOsMpLinux;
import cpuidv3.servicempwindows.DecoderOsMpWindows;
import static java.lang.Integer.min;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class SAL 
{
    private final PAL pal;
    PAL getPal()                     { return pal;                  }
    public PAL_STATUS getPalStatus() { return pal.getPalStatus();   }
    public String getRuntimeName()   { return pal.getRuntimeName(); }
    
    // Thread-safe singleton pattern for PAL class.
    private static volatile SAL instance;
    public static SAL getInstance( String libPath )
    {
        SAL result = instance;
        if ( result != null )
        {
            return result;
        }
        // Some redundant operations required for thread-safe, see links above.
        synchronized( SAL.class ) 
        {
            if ( instance == null ) 
            {
                instance = new SAL( libPath );
            }
            return instance;
        }
    }

    // For singleton constructor must be private.
    private SAL( String libPath )
    {
        pal = PAL.getInstance( libPath );
    }

    public enum SERVICE_ID
    {
        CPUID_PHYSICAL,
        CPUID_FILE,
        TOPOLOGY,
        CLOCKS,
        CONTEXT,
        OS_INFO,
        JVM_INFO,
        UNKNOWN
    }
    
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

    Service getService( SERVICE_ID  id )
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
    
    private boolean overrideByFile = false;
    public void restartOverride( boolean overrideMode )
    {
        overrideByFile = overrideMode;
    }
    
    public boolean internalLoadAllBinaryData()
    {
        boolean loadStatus = true;
        for( Service service : SERVICES )
        {
            loadStatus &= service.internalLoadBinaryData();
        }
        return loadStatus;
    }
    
    public boolean internalLoadNonAffinizedCpuid()
    {
        Service service = getService( SAL.SERVICE_ID.CPUID_PHYSICAL );
        return service.internalLoadNonAffinized();
    }

    public void clearAllBinaryData()
    {
        for( Service service : SERVICES )
        {
            service.clearBinaryData();
        }
    }
    
    public long[][] getCpuidBinaryData()
    {
        SERVICE_ID id;
        if ( overrideByFile )
        {
            id = SAL.SERVICE_ID.CPUID_FILE;
        }
        else
        {
            id = SAL.SERVICE_ID.CPUID_PHYSICAL;
        }
        Service service = getService( id );
        return service.getBinaryData();
    }
    
    public boolean setCpuidBinaryData( long[][] data )
    {
        Service service = getService( SAL.SERVICE_ID.CPUID_PHYSICAL );
        service.setBinaryData( data );
        return true;
    }
    
    public boolean setCpuidBinaryData( long[] loadedData )
    {
        boolean status = false;
        if ( ( loadedData != null )&&( loadedData.length > 0 )&&
                ( ( loadedData.length % 4 ) == 0) )
        {
            long[][] data = new long[][]{ loadedData };
            Service service = getService( SAL.SERVICE_ID.CPUID_FILE );
            service.setBinaryData( data );
            status = true;
        }        
        return status;
    }
    
    public boolean setCpuidBinaryData( int[] loadedData )
    {
        boolean status = false;
        if ( ( loadedData != null )&&( loadedData.length > 0 )&&
                ( ( loadedData.length % 5 ) == 0) )
        {
            ArrayList<Long[]> a = new ArrayList<>();
            ArrayList<Long> b = new ArrayList<>();
            int subfunction = 0;
            int previous = -1;
            for( int i=0; i<loadedData.length; i+=5 )
            {
                int function = loadedData[i + 0];
                if(( function != previous )||( function == 0 ))
                {
                    previous = function;
                    subfunction = 0;
                }
                long f = function;
                long s = subfunction++;
                long eax = loadedData[i + 1];
                long ebx = loadedData[i + 2];
                long ecx = loadedData[i + 3];
                long edx = loadedData[i + 4];
                b.add( f << 32 );
                b.add( s & 0xFFFFFFFFL );
                b.add( ( ebx << 32 ) + ( eax & 0xFFFFFFFFL ) );
                b.add( ( edx << 32 ) + ( ecx & 0xFFFFFFFFL ) );
                if (( i == loadedData.length - 5 )||( loadedData[i + 5] == 0 ))
                {
                    Long[] longs = b.toArray( new Long[ b.size() ] );
                    a.add( longs );
                    b.clear();
                }
            }
            
            if ( a.size() > 0 )
            {
                status = true;
                long[][] data = new long[ a.size() ][];
                for( int i=0; i<a.size(); i++ )
                {
                    Long[] longs = a.get( i );
                    data[i] = new long[ longs.length ];
                    if ( longs.length == 0 )
                    {
                        status = false;
                        break;
                    }
                    for( int j=0; j<longs.length; j++ )
                    {
                        data[i][j] = longs[j];
                    }
                }
                if ( status )
                {
                    Service service = getService( SAL.SERVICE_ID.CPUID_FILE );
                    service.setBinaryData( data );
                }
            }
        }
        return status;
    }
    
    // Public methods called from GUI depend on user actions.    
    // Methods for support information panels.
    
    private EntryLogicalCpu[] processorsList = null;
    private DecoderCpuid decoderCpuid = null;
    private DecoderOsMp decoderOsMp = null;
    private boolean decoderCpuidStatus = false;
    private boolean decoderOsMpStatus = false;
    private String dataOsMpName = null;
    
    private int setAndParsePerCpuEntriesDump( EntryCpuidSubfunction[] entries )
    {
        decoderCpuid.setEntriesDump( entries );
        int detectedFunctionsCount = decoderCpuid.parseEntriesDump();
        decoderCpuidStatus = ( detectedFunctionsCount > 0 );
        return detectedFunctionsCount;
    }

    public ChangeableTableModel getSummaryTable()
    {
        decoderCpuid = new DecoderCpuid();
        OS_TYPE osType = getPal().getOsType();
        if (( osType == OS_TYPE.WIN32 )||( osType == OS_TYPE.WIN64 )) 
        {
            decoderOsMp = new DecoderOsMpWindows();
            Service service = getService( SAL.SERVICE_ID.TOPOLOGY );
            service.internalLoadBinaryData();

            long[][] topoBin = service.getBinaryData();
            long[] data = null;
            int dataType = 0;
            if ( topoBin != null )
            {
                if ( ( topoBin.length > 0 )&&( topoBin[0] != null ) )
                {
                    data = topoBin[0];
                    dataType = 1;
                    dataOsMpName = "WinAPI GetLogicalProcessorInformationEx()";
                }
                else if ( ( topoBin.length > 1 )&&( topoBin[1] != null ) )
                {
                    data = topoBin[1];
                    dataType = 0;
                    dataOsMpName = "WinAPI GetLogicalProcessorInformation()";
                }
                
                if( data != null )
                {
                    decoderOsMp.setBinary( data );
                    decoderOsMpStatus = decoderOsMp.initBinary( osType, dataType );
                    if ( decoderOsMpStatus )    
                    {
                        decoderOsMpStatus = decoderOsMp.parseBinary();
                    }
                }
            }
        }
        else if  (( osType == OS_TYPE.LINUX32 )||( osType == OS_TYPE.LINUX64 ))
        {
            decoderOsMp = new DecoderOsMpLinux();
            decoderOsMpStatus = decoderOsMp.initBinary( osType, 0 );
            if ( decoderOsMpStatus )    
            {
                decoderOsMpStatus = decoderOsMp.parseBinary();
            }
            dataOsMpName = "Linux /sys/devices/system/cpu/";
        }
        
        long[][] rawBinary = getCpuidBinaryData();
        
        if ( ( rawBinary == null )&&( !overrideByFile ) )
        {
            Service service = getService( SAL.SERVICE_ID.CPUID_PHYSICAL );
            if ( service.internalLoadBinaryData() )
            {
                rawBinary = getCpuidBinaryData();
            }
        }
        
        if ( rawBinary != null )
        {
            processorsList = new EntryLogicalCpu[ rawBinary.length ];
            for( int i=0; i<rawBinary.length; i++ )
            {
                if(( rawBinary[i] != null )&&(( rawBinary[i].length % 4 ) == 0 ))
                {
                    int eCount = rawBinary[i].length / 4;
                    EntryCpuidSubfunction[] entriesPerCpu =
                        new EntryCpuidSubfunction[ eCount ];
                    for( int j=0; j<eCount; j++ )
                    {
                        int f = (int)( rawBinary[i][j*4] >>> 32 );
                        int sf = (int)( rawBinary[i][j*4 + 1] & 0xFFFFFFFFL );
                        int eax = (int)( rawBinary[i][j*4 + 2] & 0xFFFFFFFFL );
                        int ebx = (int)( rawBinary[i][j*4 + 2] >>> 32 );
                        int ecx = (int)( rawBinary[i][j*4 + 3] & 0xFFFFFFFFL );
                        int edx = (int)( rawBinary[i][j*4 + 3] >>> 32 );
                        entriesPerCpu[j] = new EntryCpuidSubfunction
                            ( f, sf, 0, eax, ebx, ecx, edx );
                    }
                    processorsList[i] = new EntryLogicalCpu( entriesPerCpu );
                }
            }
        }
        
        boolean hybridDetected;
        int hybridPcores = 1;
        int hybridEcores = 1;
        int hybridLpeCores = 1;
        int hybridPthreads = 0;
        int hybridEthreads = 0;
        int hybridLpeThreads = 0;

        if (( processorsList != null )&&( processorsList.length > 0 )&&
            ( processorsList[0] != null ))
        {
            setAndParsePerCpuEntriesDump( processorsList[0].sunfunctionsList );
    
// This also required for initualization summary screen for hybrid topology.
            preGetDetailsTree();
            
            hybridDetected = true;
            for ( EntryLogicalCpu e : processorsList ) 
            {
                if ( e != null )
                {
                    if ( ( e.hybridDesc != null )&&
                         ( e.hybridDesc.hybridCpu != null ) )
                    {
                        switch ( e.hybridDesc.hybridCpu ) 
                        {
                        case P_CORE:
                            hybridPthreads++;
                            hybridPcores = e.hybridDesc.hybridSmt;
                            break;
                        case E_CORE:
                            hybridEthreads++;
                            hybridEcores = e.hybridDesc.hybridSmt;
                            break;
                        case LP_E_CORE:
                            hybridLpeThreads++;
                            hybridLpeCores = e.hybridDesc.hybridSmt;
                            break;
                        default:
                            hybridDetected = false;
                            break;
                        }
                    }
                    else
                    {
                        hybridDetected = false;
                    }
                }
                else
                {
                    hybridDetected = false;
                }
                
                if ( !hybridDetected )
                {
                    break;
                }
            }
        }
        else
        {
            return null;  // TODO. Partial info possible instean return null.
        }
        
        String[] tableUp = decoderCpuid.getSummaryTableUp();
        String[][] tableData1 = decoderCpuid.getSummaryTable();
        String[][] tableData2 = null;
        String[][] tableData3 = null;

        if ( hybridDetected )
        {
            hybridPcores = hybridPthreads / hybridPcores;
            hybridEcores = hybridEthreads / hybridEcores;
            hybridLpeCores = hybridLpeThreads / hybridLpeCores;
         
            tableData2 = new String[][]
            {
                { "Performance cores : threads",
                    String.format( "%d : %d", hybridPcores, hybridPthreads ) },
                
                { "Efficiency cores : threads",
                    String.format( "%d : %d", hybridEcores, hybridEthreads ) },
                
                { "Low power efficiency cores : threads",
                    String.format( "%d : %d", hybridLpeCores, hybridLpeThreads ) }
            };
        }

        if ( !overrideByFile )
        {
            tableData3 = decoderOsMp.getSummaryAddStrings();
        }
        
        int dataCount = 0;
        if ( tableData1 != null ) { dataCount += tableData1.length;  }
        if ( tableData2 != null ) { dataCount += tableData2.length;  }
        if ( tableData3 != null ) { dataCount += tableData3.length + 2;  }
        String[][] data = new String[dataCount][2];
        int dataIndex = 0;
        if ( tableData1 != null ) 
            { for ( String[] d : tableData1 ) { data[dataIndex++] = d; } }
        if ( tableData2 != null ) 
            { for ( String[] d : tableData2 ) { data[dataIndex++] = d; } }
        if ( tableData3 != null ) 
        { 
            data[dataIndex++] = new String[]{ "", "" };
            final String OS_JVM = "Operating system and java runtime";
            String osJvm = getRuntimeName();
            if ( osJvm == null ) { osJvm = "?"; }
            data[dataIndex++] = new String[]{ OS_JVM, osJvm };
            for ( String[] d : tableData3 ) { data[dataIndex++] = d; } 
        }

        if( ( tableUp != null )&&( tableUp.length > 0 ) &&( dataCount > 0 ) )
        {
            return new ChangeableTableModel( tableUp, data );
        }
        else
        {
            return null;
        }
    }
    
    private ArrayList<String> tablesNames = null;    
    private ArrayList<ChangeableTableModel> threadsDataTables = null;
    private ArrayList<ChangeableTableModel> threadsDumpTables = null;
    private ArrayList<ChangeableTableModel> dataTables = null;
    private ArrayList<ChangeableTableModel> dumpTables = null;
    private ArrayList<DefaultMutableTreeNode> dmtnThreads = null;
    private DefaultMutableTreeNode dmtnRoot = null;
    private int globalIndex = 0;

    // This also required for initualization summary screen for hybrid topology.    
    private void preGetDetailsTree()
    {
        tablesNames = new ArrayList();
        threadsDataTables = new ArrayList<>();
        threadsDumpTables = new ArrayList<>();
        dmtnThreads = new ArrayList<>();
        dataTables = new ArrayList<>();
        dumpTables = new ArrayList<>();
        globalIndex = 0;
        
        if (( decoderCpuid != null )&&( decoderCpuidStatus ))
        {
            int rootCount = processorsList.length;

            TreeEntry entryRoot = new TreeEntry( globalIndex++, 
                decoderCpuid.getEnumeratorName(),"", true, false );
            dmtnRoot = new DefaultMutableTreeNode( entryRoot, true );
            
            dataTables.add( new ChangeableTableModel
                ( decoderCpuid.getEnumeratorFirstTableUp(), 
                  decoderCpuid.getEnumeratorFirstTable() ) );
            dumpTables.add( new ChangeableTableModel
                ( decoderCpuid.getEnumeratorSecondTableUp(), 
                  decoderCpuid.getEnumeratorSecondTable() ) );
            
            tablesNames.add( decoderCpuid.getEnumeratorName() );

            for( int i=0; i<rootCount; i++ )
            {
                EntryLogicalCpu ecp = processorsList[i];
                EntryCpuidSubfunction[] ecs = ecp.sunfunctionsList;
    // This important for initialization.
                int functionCount = setAndParsePerCpuEntriesDump( ecs );

    // This also required for initualization summary screen for hybrid topology.
                processorsList[i].hybridDesc =
                decoderCpuid.getProcessorHybrid( i );
                ChangeableTableModel threadDataTable = new ChangeableTableModel
                    ( decoderCpuid.getProcessorFirstTableUp( i ),
                      decoderCpuid.getProcessorFirstTable( i ) );
                dataTables.add( threadDataTable );
                threadsDataTables.add( threadDataTable );
                
                ChangeableTableModel threadDumpTable = new ChangeableTableModel
                    ( decoderCpuid.getProcessorSecondTableUp( i ),
                      decoderCpuid.getProcessorSecondTable( i ) );
                dumpTables.add( threadDumpTable );
                threadsDumpTables.add( threadDumpTable );
                
                String cpuNumber = String.format( "thread[%d]", i );
                tablesNames.add( cpuNumber );
                        
                TreeEntry entryCpu = new TreeEntry( globalIndex++, 
                        cpuNumber, "", false, false );

                DefaultMutableTreeNode dmtnCpu = 
                    new DefaultMutableTreeNode( entryCpu, true );
                
                TreeEntry entryStandard =  // Child node 1 = Standard CPUID
                    new TreeEntry( "Standard functions", "", false, false );
                DefaultMutableTreeNode dmtnStandard = 
                    new DefaultMutableTreeNode( entryStandard, true );
                dmtnCpu.add( dmtnStandard );
        
                TreeEntry entryExtended =  // Child node 2 = Extended CPUID
                    new TreeEntry( "Extended functions", "", false, false );
                DefaultMutableTreeNode dmtnExtended =
                    new DefaultMutableTreeNode( entryExtended, true );
                dmtnCpu.add( dmtnExtended );
        
                TreeEntry entryVendor =  // Child node 3 = Vendor CPUID
                    new TreeEntry( "Vendor functions", "", false, false );
                DefaultMutableTreeNode dmtnVendor = 
                    new DefaultMutableTreeNode( entryVendor, true );
                dmtnCpu.add( dmtnVendor );

                TreeEntry entryVirtual =  // Child node 4 = Virtual CPUID
                    new TreeEntry( "Virtual functions", "", false, false );
                DefaultMutableTreeNode dmtnVirtual = 
                    new DefaultMutableTreeNode( entryVirtual, true );
                dmtnCpu.add( dmtnVirtual );
                
                for( int j=0; j< functionCount; j++ )
                {
                    dataTables.add( new ChangeableTableModel
                        ( decoderCpuid.getFunctionFirstTableUp( j ),
                          decoderCpuid.getFunctionFirstTable( j ) ) );
                    dumpTables.add( new ChangeableTableModel
                        ( decoderCpuid.getFunctionSecondTableUp( j ),
                          decoderCpuid.getFunctionSecondTable( j ) ) );
                    
                    tablesNames.add( decoderCpuid.getFunctionShortName( j ) +
                             " = " + decoderCpuid.getFunctionLongName( j ));

                    TreeEntry entry = new TreeEntry( globalIndex++, 
                        decoderCpuid.getFunctionShortName( j ),
                        decoderCpuid.getFunctionLongName( j ),
                        false, true );
                    
                    DefaultMutableTreeNode node = 
                            new DefaultMutableTreeNode( entry , false );
                    char c1 = decoderCpuid.getFunctionShortName( j ).charAt(0);
                    String s1 = "";
                    if ( decoderCpuid.getFunctionShortName( j ).length() > 4 )
                    {
                        s1 = decoderCpuid.getFunctionShortName( j )
                                .substring( 0, 4 );
                    }

// Select function group (tree branch) for current analused function
//
// 00000000h - 7FFFFFFFh  = Range for Standard CPUID functions
// 80000000h - FFFFFFFFh  = Range for Extended CPUID functions
// 8FFFFFFFh              = Special signature for AMD
// C0000000h - CFFFFFFFh  = Range for VIA/IDT Vendor CPUID functions
// 80860000h - 8086FFFFh  = Range for Transmeta Vendor CPUID functions
// 20000000h - 2FFFFFFFh  = Range for Intel Xeon Phi Vendor CPUID functions
// 40000000h - 4FFFFFFFh  = Range for Virtual CPUID functions
//
// Note about checks sequence is important, because 8086xxxx can match 8xxxxxxx.
// Note about ranges reduced by this check algorithm:
//
// 00000000h - 0FFFFFFFh  = Range for Standard CPUID functions
// 80000000h - 8FFFFFFFh  = Range for Extended CPUID functions

                    if ( ( c1 == '2' ) || ( c1 == 'C' ) ||
                            ( s1.equals( "8086" )) ) 
                                            { dmtnVendor.add( node );   }
                    else if ( c1 == '0' )   { dmtnStandard.add( node ); }
                    else if ( c1 == '8' )   { dmtnExtended.add( node ); }
                    else if ( c1 == '4' )   { dmtnVirtual.add( node );  }
                }
                
                dmtnThreads.add( dmtnCpu );
            }
        }
    }

    public TreeScreenModel getDetailsTree()
    {
        TreeScreenModel result = null;
        
        if ( ( processorsList != null )&& // ( cpuidTree != null )&&
           ( ( threadsDataTables != null )&&
             ( threadsDataTables.size() == processorsList.length ) )&&
             ( ( threadsDumpTables != null )&&
             ( threadsDumpTables.size() == processorsList.length ) ) )
        {
            int[] smt = new int[processorsList.length];
            for( int i=0; i<processorsList.length; i++ )
            {
                HybridReturn hr = processorsList[i].hybridDesc;
                if ( ( hr != null )&&( hr.hybridSmt > 0 )  &&
                   ( ( hr.hybridCpu == IHybrid.HYBRID_CPU.P_CORE ) ||
                     ( hr.hybridCpu == IHybrid.HYBRID_CPU.E_CORE ) ||
                     ( hr.hybridCpu == IHybrid.HYBRID_CPU.LP_E_CORE ) ) )
                {
                    smt[i] = hr.hybridSmt;
                }
                else
                {
                    smt[i] = decoderCpuid.getSummarySmt();
                }
            }
        
            int coreCount = 0;
            for( int i=0; i<processorsList.length; )
            {
                String coreNumber = String.format( "core[%d]", coreCount++ );
                
                TreeEntry entryCore = new TreeEntry( globalIndex++,
                    coreNumber, processorsList[i].hybridDesc.hybridName,
                    true, false, processorsList[i].hybridDesc.hybridCpu );

                DefaultMutableTreeNode dmtnCore = 
                    new DefaultMutableTreeNode( entryCore, true );
                int threadsPerCore = smt[i];
                int tempI = i;
                
                int threadsCount = 
                    min ( threadsPerCore, processorsList.length );
                
                for( int j=0; j<threadsCount; j++ )
                {
                    dmtnCore.add( dmtnThreads.get( i++ ) );
                }
                
                dmtnRoot.add( dmtnCore );
                dataTables.add ( threadsDataTables.get( tempI ) );
                dumpTables.add( threadsDumpTables.get( tempI ) );
                tablesNames.add( coreNumber );
            }
        
            ChangeableTableModel[] tables1 = dataTables.toArray
                ( new ChangeableTableModel[dataTables.size()] );
            ChangeableTableModel[] tables2 = dumpTables.toArray
               ( new ChangeableTableModel[dumpTables.size()] );
            
            String[] tNames = tablesNames.toArray
                ( new String[tablesNames.size()]);
            
            DefaultTreeModel treeModel = 
                new DefaultTreeModel( dmtnRoot , true );
            result = new TreeScreenModel( treeModel, tNames, tables1, tables2 );
        }
      
        return result;
    }
    
    public TreeScreenModel getSmpTree()
    {
        TreeScreenModel result = null;
        
        if (( decoderOsMp != null )&&( decoderOsMpStatus ))
        {
            String[] sNames = decoderOsMp.getShortNames();
            String[] lNames = decoderOsMp.getLongNames();
            String[][] lstUps = decoderOsMp.getListsUps();
            String[][][] lsts = decoderOsMp.getLists();
            String[][] dmpUps = decoderOsMp.getDumpsUps();
            String[][][] dmps = decoderOsMp.getDumps();
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
                new TreeEntry( dataOsMpName, "", true, false );
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

    
    public ChangeableTableModel getDumpTable()
    {
        SERVICE_ID id;
        if ( overrideByFile )
        {
            id = SAL.SERVICE_ID.CPUID_FILE;
        }
        else
        {
            id = SAL.SERVICE_ID.CPUID_PHYSICAL;
        }
        Service service = getService( id );
        return new ChangeableTableModel
            ( service.getTableUp(), service.getTableData() );
    }


    public ChangeableTableModel getClockTable()
    {
        Service service = getService( SAL.SERVICE_ID.CLOCKS );
        return new ChangeableTableModel
            ( service.getTableUp(), service.getTableData() );
    }

    
    public ChangeableTableModel getContextTable()
    {
        Service service = getService( SAL.SERVICE_ID.CONTEXT );
        return new ChangeableTableModel
            ( service.getTableUp(), service.getTableData() );
    }

    
    public ChangeableTableModel getOsTable()
    {
        Service service = getService( SAL.SERVICE_ID.OS_INFO );
        return new ChangeableTableModel
            ( service.getTableUp(), service.getTableData() );
    }

    
    public ChangeableTableModel getJvmTable()
    {
        Service service = getService( SAL.SERVICE_ID.JVM_INFO );
        return new ChangeableTableModel
            ( service.getTableUp(), service.getTableData() );
    }

    
    public void consoleSummary()
    {
        for( Service service : SERVICES )
        {
            service.printSummaryReport();
        }
    }
}
