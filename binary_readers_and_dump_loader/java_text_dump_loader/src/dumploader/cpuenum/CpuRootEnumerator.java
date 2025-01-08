/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Loader for text dump files with 
service entry points for dump parsing results get.

*/

package dumploader.cpuenum;

import dumploader.cpuid.DecoderCpuid;
import dumploader.cpuid.HybridReturn;
import java.util.ArrayList;

public class CpuRootEnumerator 
{
    private final DecoderCpuid decoderCpuid;
    public int setAndParsePerCpuEntriesDump( EntryCpuidSubfunction[] entries )
    {
        decoderCpuid.setEntriesDump( entries );
        return decoderCpuid.parseEntriesDump();
    }
    
    private final boolean status;
    public boolean getStatus()
    {
        return status;
    }
    
    private final EntryLogicalCpu[] processorsList;
    public EntryLogicalCpu[] getProcessorsList()
    {
        return processorsList;
    }

    public ChangeableTableModel getSummaryTable()
    {
        boolean hybridDetected = false;
        int hybridPcores = 1;
        int hybridEcores = 1;
        int hybridLpeCores = 1;
        int hybridPthreads = 0;
        int hybridEthreads = 0;
        int hybridLpeThreads = 0;

        if (( processorsList != null )&&( processorsList.length > 0 ))
        {
            setAndParsePerCpuEntriesDump( processorsList[0].sunfunctionsList );
            
            hybridDetected = true;
            for ( EntryLogicalCpu e : processorsList ) 
            {
                if ( e != null )
                {
                    if ( ( e.hybridDesc != null )&&( e.hybridDesc.hybridCpu != null ) )
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
        
        if ( hybridDetected )
        {
            hybridPcores = hybridPthreads / hybridPcores;
            hybridEcores = hybridEthreads / hybridEcores;
            hybridLpeCores = hybridLpeThreads / hybridLpeCores;
         
            String[] tableUp = decoderCpuid.getSummaryTableUp();
            String[][] tableData1 = decoderCpuid.getSummaryTable();
            String[][] tableData2 = 
            {
                { "Performance cores : threads",
                    String.format( "%d : %d", hybridPcores, hybridPthreads ) },
                
                { "Efficiency cores : threads",
                    String.format( "%d : %d", hybridEcores, hybridEthreads ) },
                
                { "Low power efficiency cores : threads",
                    String.format( "%d : %d", hybridLpeCores, hybridLpeThreads ) }
            };
            
            int summaryRows = tableData1.length + tableData2.length;
            String[][] allTableData = new String[summaryRows][2];
            int index = 0;
            for( int i=0; i<tableData1.length; i++ )
            {
                allTableData[ index ] = tableData1[ i ];
                index++;
            }
            for( int i=0; i<tableData2.length; i++ )
            {
                allTableData[ index ] = tableData2[ i ];
                index++;
            }
            return new ChangeableTableModel( tableUp, allTableData );
        }
        else
        {
            return new ChangeableTableModel
                ( decoderCpuid.getSummaryTableUp(), 
                  decoderCpuid.getSummaryTable() );
        }
    }

    public String getEnumeratorName()
    {
        return decoderCpuid.getEnumeratorName();
    }

    public ChangeableTableModel getEnumeratorFirstTable()
    {
        return new ChangeableTableModel
            ( decoderCpuid.getEnumeratorFirstTableUp(), 
              decoderCpuid.getEnumeratorFirstTable() );
    }

    public ChangeableTableModel getEnumeratorSecondTable()
    {
        return new ChangeableTableModel
            ( decoderCpuid.getEnumeratorSecondTableUp(), 
              decoderCpuid.getEnumeratorSecondTable() );
    }
    
    public String getProcessorName( int cpu )
    {
        return String.format( "CPU#%d", cpu );
    }

    public ChangeableTableModel getProcessorFirstTable( int cpuIndex )
    {
        return new ChangeableTableModel
            ( decoderCpuid.getProcessorFirstTableUp( cpuIndex ), 
              decoderCpuid.getProcessorFirstTable( cpuIndex ) );
    }

    public ChangeableTableModel getProcessorSecondTable(  int cpuIndex )
    {
        return new ChangeableTableModel
            ( decoderCpuid.getProcessorSecondTableUp( cpuIndex ), 
              decoderCpuid.getProcessorSecondTable( cpuIndex ) );
    }

    public HybridReturn getProcessorHybrid( int cpuIndex )
    {
        HybridReturn hr = decoderCpuid.getProcessorHybrid( cpuIndex );
        processorsList[cpuIndex].hybridDesc = hr;
        return hr;
    }

    public String getFunctionShortName( int function )
    {
        return decoderCpuid.getFunctionShortName( function );
    }

    public String getFunctionLongName( int function )
    {
        return decoderCpuid.getFunctionLongName( function );
    }
    
    public ChangeableTableModel getFunctionFirstTable( int function )
    {
        return new ChangeableTableModel
            ( decoderCpuid.getFunctionFirstTableUp( function ), 
              decoderCpuid.getFunctionFirstTable( function ) );
    }
    
    public ChangeableTableModel getFunctionSecondTable( int function )
    {
        return new ChangeableTableModel
            ( decoderCpuid.getFunctionSecondTableUp( function ), 
              decoderCpuid.getFunctionSecondTable( function ) );
    }

    public CpuRootEnumerator( int[] dump )
    {
        if ( ( dump != null )&&( dump.length > 0 )&&
                ( ( dump.length % 5 ) == 0) )
        {
            ArrayList<EntryLogicalCpu> a = new ArrayList<>();
            ArrayList<EntryCpuidSubfunction> b = new ArrayList<>();
            int subfunction = 0;
            int previous = -1;
            
            for( int i=0; i<dump.length; i+=5 )
            {
                if( dump[i + 0] != previous )
                {
                    previous = dump[i + 0];
                    subfunction = 0;
                }

                EntryCpuidSubfunction entrySubfunction = 
                    new EntryCpuidSubfunction
                        ( dump[i + 0], subfunction++, 0,
                          dump[i + 1], dump[i + 2], dump[i + 3], dump[i + 4] );
                b.add( entrySubfunction );
                
                if (( i == dump.length - 5 )||( dump[i + 5] == 0 ))
                {
                    EntryCpuidSubfunction[] entries = 
                        b.toArray( new EntryCpuidSubfunction[b.size()] );
                    EntryLogicalCpu entryCpu = new EntryLogicalCpu( entries );
                    a.add( entryCpu );
                    b.clear();
                }
            }
            
            decoderCpuid = new DecoderCpuid();
            processorsList = a.toArray( new EntryLogicalCpu[a.size()] );
            status = true;
        }
        else
        {
            decoderCpuid = null;
            processorsList = null;
            status = false;
        }
    }
}
