/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Helper class for SAL (Service Abstraction Layer), contains complex
methods for get system information ( CPUID and OS API SMP information).

*/

package cpuidv3.sal;

import cpuidv3.pal.PAL;
import cpuidv3.servicecpudata.EntryCpuidSubfunction;
import cpuidv3.servicecpudata.ServiceCpudata;
import cpuidv3.servicecpuid.HybridReturn;
import cpuidv3.servicecpuid.ServiceCpuid;
import static cpuidv3.servicecpuid.ServiceCpuid.HYBRID_CPU.*;
import cpuidv3.servicemplinux.ServiceMpLinux;
import cpuidv3.servicempwindows.ServiceMpWindows;
import static java.lang.Integer.min;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

class HelperSystemInfo 
{
    private final SAL sal;
    private final ServiceCpuid serviceCpuid;
    private final ServiceCpudata serviceCpudata;
    
    private EntryLogicalCpu[] processorsList = null;
    private boolean serviceCpuidStatus = false;
    
    private ArrayList<String> tablesNames = null;
    private ArrayList<ChangeableTableModel> threadsDataTables = null;
    private ArrayList<ChangeableTableModel> threadsDumpTables = null;
    private ArrayList<ChangeableTableModel> dataTables = null;
    private ArrayList<ChangeableTableModel> dumpTables = null;
    private ArrayList<DefaultMutableTreeNode> dmtnThreads = null;
    private DefaultMutableTreeNode dmtnRoot = null;
    private int globalIndex = 0;
    
    HelperSystemInfo( SAL sal )
    {
        this.sal = sal;
        serviceCpuid = ServiceCpuid.getInstance();
        serviceCpudata = ServiceCpudata.getInstance();
    }
    
// This method called as step 1 of 3.
    ChangeableTableModel getSummaryTable()
    {
        PAL.OS_TYPE osType = PAL.OS_TYPE.NOT_USED;
        PAL pal = sal.getPal();
// PAL check, because can be runned at "dump loader only" build.
        if ( pal != null )
        {
            osType = sal.getPal().getOsType();
        }

        if (( osType == PAL.OS_TYPE.WIN32 )||( osType == PAL.OS_TYPE.WIN64 )) 
        {
            sal.serviceMp = new ServiceMpWindows();
            Service service = sal.getService( SAL.SERVICE_ID.TOPOLOGY );
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
                    sal.serviceMpName = 
                            "WinAPI GetLogicalProcessorInformationEx()";
                }
                else if ( ( topoBin.length > 1 )&&( topoBin[1] != null ) )
                {
                    data = topoBin[1];
                    dataType = 0;
                    sal.serviceMpName = 
                            "WinAPI GetLogicalProcessorInformation()";
                }
                
                if( data != null )
                {
                    sal.serviceMp.setBinary( data );
                    sal.serviceMpStatus = 
                            sal.serviceMp.initBinary( osType, dataType );
                    if ( sal.serviceMpStatus )    
                    {
                        sal.serviceMpStatus = sal.serviceMp.parseBinary();
                    }
                }
            }
        }
        else if  (( osType == PAL.OS_TYPE.LINUX32 )||
                  ( osType == PAL.OS_TYPE.LINUX64 ))
        {
            sal.serviceMp = new ServiceMpLinux();
            sal.serviceMpStatus = sal.serviceMp.initBinary( osType, 0 );
            if ( sal.serviceMpStatus )    
            {
                sal.serviceMpStatus = sal.serviceMp.parseBinary();
            }
            sal.serviceMpName = "Linux /sys/devices/system/cpu/";
        }
        
        long[][] rawBinary = sal.getCpuidBinaryData();
        
        if ( ( rawBinary == null )&&( !sal.overrideByFile ) )
        {
            Service service = sal.getService( SAL.SERVICE_ID.CPUID_PHYSICAL );
            if ( sal.overrideByFile )
            {
                service = sal.getService( SAL.SERVICE_ID.CPUID_FILE );
            }

            if ( service.internalLoadBinaryData() )
            {
                rawBinary = sal.getCpuidBinaryData();
            }
        }
        
        if ( rawBinary != null )
        {
            processorsList = new EntryLogicalCpu[ rawBinary.length ];
            for( int i=0; i<rawBinary.length; i++ )
            {
                if(( rawBinary[i] != null )&&
                   (( rawBinary[i].length % 4 ) == 0 ))
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
        
        boolean hybridDetected = false;
        int hybridPcores = 1;
        int hybridEcores = 1;
        int hybridLpeCores = 1;
        int hybridPthreads = 0;
        int hybridEthreads = 0;
        int hybridLpeThreads = 0;
        
        int examplePcore = -1;
        int exampleEcore = -1;
        int exampleLpeCore = -1;

        if (( processorsList != null )&&( processorsList.length > 0 )&&
            ( processorsList[0] != null ))
        {
// TODO. Required before vendor-specific CPU initialization.
            serviceCpudata.earlyCpuidDump
                ( processorsList[0].sunfunctionsList );
// TODO. Required after vendor-specific CPU initialization.
            serviceCpudata.setCpuidDump
                ( processorsList[0].sunfunctionsList );
// Get info.
            serviceCpudata.buildStash
                ( processorsList[0].sunfunctionsList );
// Communicate vendor enum from serviceCpudata to serviceCpuid.
            ServiceCpudata.VENDOR_T vt = serviceCpudata.getPhysicalVendorEnum();
            ServiceCpudata.HYPERVISOR_T ht = serviceCpudata.getVirtualVendorEnum();
            serviceCpuid.earlyVendors( vt, ht );
// TODO. More flexible, if ServiceCpuid class has information about
// all logical processor. Here set for one, later by one at cycle.
            int count = serviceCpuid.setCpuidDump
                ( processorsList[0].sunfunctionsList );
            serviceCpuidStatus = ( count > 0);
// This also required for initualization summary screen for hybrid topology.
            preGetDetailsTree();
            
            hybridDetected = true;
            for ( int index=0; index<processorsList.length; index++ ) 
            {
                EntryLogicalCpu e = processorsList[index];
                if ( e != null )
                {
                    if ( ( e.hybridDesc != null )&&
                         ( e.hybridDesc.hybridCpu != null ) )
                    {
                        switch ( e.hybridDesc.hybridCpu ) 
                        {
                        case P_CORE:
                            examplePcore = index;
                            hybridPthreads++;
                            hybridPcores = e.hybridDesc.hybridSmt;
                            break;
                        case E_CORE:
                            exampleEcore = index;
                            hybridEthreads++;
                            hybridEcores = e.hybridDesc.hybridSmt;
                            break;
                        case LP_E_CORE:
                            exampleLpeCore = index;
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
        
        String[] tableUp = new String[] { "Parameter", "Value" };
        String[][] tableData1 = null;
        String[][] tableData2 = null;
        String[][] tableData3 = null;

// CPUID summary info.
        if ( serviceCpuidStatus )
        {
            ArrayList<String[]> a = new ArrayList<>();
            
// CPUID unified (non vendor provided) info.
            serviceCpuid.appendSummaryCpuidInfo( a );
            
// CPUID vendor provided info. Hybrid CPUs support added.
            if( hybridDetected && (( examplePcore > 0 )||
                    ( exampleEcore > 0 )||( exampleLpeCore > 0 )) )
            {   // This branch for hybrid cores, info about all detected types.
                helperHybrid( a, examplePcore, "Performance cores" );
                helperHybrid( a, exampleEcore, "Efficient cores" );
                helperHybrid( a, exampleLpeCore, "Low-power efficient cores" );
            }
            else
            {   // This branch for non-hybrid cores.
                serviceCpudata.appendSummaryVendorInfo( a );
            }
            // Multiprocessing info for both branches.
            serviceCpudata.appendSummaryVendorMpInfo( a );

            // Store scratchpads to result.
            tableData1 = a.toArray( new String[ a.size() ][] );
        }
        
// Hybrid CPU info.
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

// OS multiprocessing info.
        if ( !sal.overrideByFile )
        {
            tableData3 = sal.serviceMp.getSummaryAddStrings();
        }
        
// Concatenate tables:
// CPUID summary info, Hybrid CPU, OS multiprocessing.
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
            String osJvm = sal.getRuntimeName();
            if ( osJvm == null ) { osJvm = "?"; }
            data[dataIndex++] = new String[]{ OS_JVM, osJvm };
            for ( String[] d : tableData3 ) { data[dataIndex++] = d; } 
        }

        if( ( tableUp != null )&&( tableUp.length > 0 )&&( dataCount > 0 ) )
        {
            return new ChangeableTableModel( tableUp, data );
        }
        else
        {
            return null;
        }
    }

    private void helperHybrid
        ( ArrayList<String[]> a, int coreIndex, String coreName )
    {
        if (( coreIndex > 0 )&&( processorsList != null )&&
            ( coreIndex < processorsList.length ))
        {
            EntryCpuidSubfunction[] e =
                processorsList[coreIndex].sunfunctionsList;
            serviceCpudata.setCpuidDump( e );
            serviceCpudata.buildStash( e );
            serviceCpudata.appendSummaryVendorInfo( a, coreName );
        }
    }
   
// This method call also required for
// initialization summary screen shows hybrid topology.
// This method called as step 2 of 3.    
    private void preGetDetailsTree()
    {
        tablesNames = new ArrayList();
        threadsDataTables = new ArrayList<>();
        threadsDumpTables = new ArrayList<>();
        dmtnThreads = new ArrayList<>();
        dataTables = new ArrayList<>();
        dumpTables = new ArrayList<>();
        globalIndex = 0;
        
        if (( serviceCpuid != null )&&( serviceCpuidStatus ))
        {
            int rootCount = processorsList.length;

            TreeEntry entryRoot = new TreeEntry( globalIndex++, 
                serviceCpuid.getEnumeratorName(),"", true, false );
            dmtnRoot = new DefaultMutableTreeNode( entryRoot, true );
            
            dataTables.add( new ChangeableTableModel
                ( serviceCpuid.getEnumeratorFirstTableUp(), 
                  serviceCpuid.getEnumeratorFirstTable() ) );
            dumpTables.add( new ChangeableTableModel
                ( serviceCpuid.getEnumeratorSecondTableUp(), 
                  serviceCpuid.getEnumeratorSecondTable() ) );
            
            tablesNames.add( serviceCpuid.getEnumeratorName() );

            for( int i=0; i<rootCount; i++ )
            {
                EntryLogicalCpu ecp = processorsList[i];
                EntryCpuidSubfunction[] ecs = ecp.sunfunctionsList;
                int functionCount = serviceCpuid.setCpuidDump( ecs );
// This also required for initualization summary screen show hybrid topology.
                processorsList[i].hybridDesc =
                    serviceCpuid.getProcessorHybrid( i );
                ChangeableTableModel threadDataTable = new ChangeableTableModel
                    ( serviceCpuid.getProcessorFirstTableUp( i ),
                      serviceCpuid.getProcessorFirstTable( i ) );
                dataTables.add( threadDataTable );
                threadsDataTables.add( threadDataTable );
                
                Service service = 
                        sal.getService( SAL.SERVICE_ID.CPUID_PHYSICAL );
                if ( sal.overrideByFile )
                {
                    service = sal.getService( SAL.SERVICE_ID.CPUID_FILE );
                }

                String[] dumpUp = service.getTableUp();
                String[][] dumpData = service.getTableData( i );
                ChangeableTableModel threadDumpTable = 
                    new ChangeableTableModel( dumpUp, dumpData );
                
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
                        ( serviceCpuid.getFunctionFirstTableUp( j ),
                          serviceCpuid.getFunctionFirstTable( j ) ) );
                    dumpTables.add( new ChangeableTableModel
                        ( serviceCpuid.getFunctionSecondTableUp( j ),
                          serviceCpuid.getFunctionSecondTable( j ) ) );
                    
                    tablesNames.add( serviceCpuid.getFunctionShortName( j ) +
                             " = " + serviceCpuid.getFunctionLongName( j ));

                    TreeEntry entry = new TreeEntry( globalIndex++, 
                        serviceCpuid.getFunctionShortName( j ),
                        serviceCpuid.getFunctionLongName( j ),
                        false, true );
                    
                    DefaultMutableTreeNode node = 
                            new DefaultMutableTreeNode( entry , false );
                    char c1 = serviceCpuid.getFunctionShortName( j ).charAt(0);
                    String s1 = "";
                    if ( serviceCpuid.getFunctionShortName( j ).length() > 4 )
                    {
                        s1 = serviceCpuid.getFunctionShortName( j )
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

// This method called as step 3 of 3.
    TreeScreenModel getDetailsTree()
    {
        TreeScreenModel result = null;
        
        if ( ( processorsList != null )&&
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
                   ( ( hr.hybridCpu == ServiceCpuid.HYBRID_CPU.P_CORE ) ||
                     ( hr.hybridCpu == ServiceCpuid.HYBRID_CPU.E_CORE ) ||
                     ( hr.hybridCpu == ServiceCpuid.HYBRID_CPU.LP_E_CORE ) ) )
                {
                    smt[i] = hr.hybridSmt;
                }
                else
                {
                    smt[i] = serviceCpudata.getStashSmt();
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
}
