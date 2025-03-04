/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

SAL = Service Abstraction Layer. Parent abstract class.

This class inherited by SALHW.java (HW=Hardware) for
"Physical platform with dump loader" application build.

This class inherited by SALDL.java (DL=Dump loader) for 
"Dump loader only" application build.

*/

package cpuidv3.sal;

import cpuidv3.pal.PAL;
import cpuidv3.pal.PAL.OS_TYPE;
import cpuidv3.pal.PAL.PAL_STATUS;
import cpuidv3.servicecpuid.DecoderCpuid;
import cpuidv3.servicecpuid.HybridReturn;
import cpuidv3.servicecpuid.IHybrid;
import cpuidv3.servicemp.DecoderOsMp;
import cpuidv3.servicemplinux.DecoderOsMpLinux;
import cpuidv3.servicempwindows.DecoderOsMpWindows;
import static java.lang.Integer.min;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class SAL         
{
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

    public boolean getDumpLoaderBuild() { return true; }
    
    PAL getPal()                     { return null; }
    public PAL_STATUS getPalStatus() { return null; }
    public String getRuntimeName()   { return "";   }
    
    Service getService( SERVICE_ID id ) { return null;  }

    // Public methods called from GUI depend on user actions.
    // Methods for support tools - dialogue boxes.

    boolean overrideByFile = false;
    public void restartOverride( boolean overrideMode )
    {
        overrideByFile = overrideMode;
    }
    
    public boolean internalLoadAllBinaryData()             { return false; }
    public boolean internalLoadNonAffinizedCpuid()         { return false; }
    public void clearAllBinaryData()                       {               }
    public long[][] getCpuidBinaryData()                   { return null;  }
    public boolean setCpuidBinaryData( long[][] data )     { return false; }
    public boolean setCpuidBinaryData( long[] loadedData ) { return false; }
    
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
                    Service service = getService( SERVICE_ID.CPUID_FILE );
                    if ( service != null )
                    {
                        service.setBinaryData( data );
                    }
                    else
                    {
                        status = false;
                    }
                }
            }
        }
        return status;
    }

    private EntryLogicalCpu[] processorsList = null;
    private DecoderCpuid decoderCpuid = null;
    private boolean decoderCpuidStatus = false;
    
    DecoderOsMp decoderOsMp = null;
    String dataOsMpName = null;
    boolean decoderOsMpStatus = false;
    
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
        
        OS_TYPE osType = OS_TYPE.NOT_USED;
        PAL pal = getPal();
        // PAL check, because can be runned at "dump loader only" build.
        if ( pal != null )
        {
            osType = getPal().getOsType();
        }

        if (( osType == OS_TYPE.WIN32 )||( osType == OS_TYPE.WIN64 )) 
        {
            decoderOsMp = new DecoderOsMpWindows();
            Service service = getService( SERVICE_ID.TOPOLOGY );
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
            Service service = getService( SERVICE_ID.CPUID_PHYSICAL );
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

    public TreeScreenModel getSmpTree() { return null;  }
    
    public ChangeableTableModel getDumpTable()
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
        return new ChangeableTableModel
            ( service.getTableUp(), service.getTableData() );
    }

    public ChangeableTableModel getClockTable()            { return null;  }
    public ChangeableTableModel getContextTable()          { return null;  }
    public ChangeableTableModel getOsTable()               { return null;  }
    public ChangeableTableModel getJvmTable()              { return null;  }
    public void consoleSummary()                           {               }
}


