/*
Test for class DecoderCpuid.java.
This is not pure class-level unit test, because depends on PAL class,
native libraries, helpers, other classes and gets path to resource package.
Note PAL = Platform Abstraction Layer.
Note SAL = Services Abstraction Layer.
*/

package cpuidv3.servicecpuid;

import cpuidv3.sal.ChangeableTableModel;
import cpuidv3.sal.EntryCpuidSubfunction;
import static cpuidv3.sal.HelperTableToReport.tableReport;
import cpuidv3.servicecpuid.IHybrid.HYBRID_CPU;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DecoderCpuidTest 
{
    public DecoderCpuidTest() { }
    
    DecoderCpuid decoder;

    // CPUID 00000000: 00000001-756E6547-6C65746E-49656E69
    // CPUID 00000001: 00000480-00000000-00000000-00000003
    
    private final EntryCpuidSubfunction[] entries = 
    {
        new EntryCpuidSubfunction 
            ( 0, 0, 0, 0x00000001, 0x756E6547, 0x6C65746E, 0x49656E69 ),
        new EntryCpuidSubfunction 
            ( 1, 0, 0, 0x00000480, 0x00000000, 0x00000000, 0x00000003 )
    };
    
    @Before public void setUp() 
    {
        decoder = new DecoderCpuid();
    }
    
    @After public void tearDown() 
    {
        decoder = null;
    }
    
    @Test public void testGetSummaryTable()
    {
        decoder.setEntriesDump( entries );
        int count = decoder.parseEntriesDump();
        if ( count != 2)
        {
            fail( "Unexpected entries count." );
        }
        String[] up = decoder.getSummaryTableUp();
        String[][] data = decoder.getSummaryTable();
        if (( up == null )||( data == null ))
        {
            fail( "Get summary table failed." );
        }
        ChangeableTableModel tableModel = new ChangeableTableModel( up, data );
        String tableData = tableReport( tableModel );
        System.out.println( tableData );
        
        int summarySmt = decoder.getSummarySmt();
        if ( summarySmt != 1 )
        {
            fail( "Unexpected SMT count." );
        }
        
        HybridReturn summaryHybrid = decoder.getProcessorHybrid( 0 );
        if (( summaryHybrid == null )||( summaryHybrid.hybridCpu != HYBRID_CPU.DEFAULT ))
        {
            fail( "Unexpected hybrid CPU type." );
        }
    }
    
    @Test public void testGetEnumeratorName() 
    {
        decoder.setEntriesDump( entries );
        decoder.parseEntriesDump();
        String s = decoder.getEnumeratorName();
        if ( s == null )
        {
            fail( "Get enumerator name failed." );
        }
        System.out.println( s );
    }

    @Test public void testGetEnumeratorTable()
    {
        decoder.setEntriesDump( entries );
        decoder.parseEntriesDump();
        
        String[] up1 = decoder.getEnumeratorFirstTableUp();
        String[][] data1 = decoder.getEnumeratorFirstTable();
        if (( up1 == null )||( data1 == null ))
        {
            fail( "Get enumerator first table failed." );
        }
        ChangeableTableModel model1 = 
            new ChangeableTableModel( up1, data1 );
        String table1 = tableReport( model1 );
        System.out.println( table1 );

        String[] up2 = decoder.getEnumeratorSecondTableUp();
        String[][] data2 = decoder.getEnumeratorSecondTable();
        if (( up2 == null )||( data2 == null ))
        {
            fail( "Get enumerator second table failed." );
        }
        ChangeableTableModel model2 = 
                new ChangeableTableModel( up2, data2 );
        String table2 = tableReport( model2 );
        System.out.println( table2 );
    }
        
    @Test public void testGetProcessorTable()
    {
        decoder.setEntriesDump( entries );
        decoder.parseEntriesDump();
        
        String[] up1 = decoder.getProcessorFirstTableUp( 0 );
        String[][] data1 = decoder.getProcessorFirstTable( 0 );
        if (( up1 == null )||( data1 == null ))
        {
            fail( "Get processor first table failed." );
        }
        ChangeableTableModel model1 = 
            new ChangeableTableModel( up1, data1 );
        String table1 = tableReport( model1 );
        System.out.println( table1 );

        String[] up2 = decoder.getProcessorSecondTableUp( 0 );
        String[][] data2 = decoder.getProcessorSecondTable( 0 );
        if (( up2 == null )||( data2 == null ))
        {
            fail( "Get processor second table failed." );
        }
        ChangeableTableModel model2 = 
            new ChangeableTableModel( up2, data2 );
        String table2 = tableReport( model2 );
        System.out.println( table2 );
    }

    @Test public void testGetFunctionsTables()
    {
        decoder.setEntriesDump( entries );
        int count = decoder.parseEntriesDump();
        if ( count != 2)
        {
            fail( "Unexpected entries count." );
        }

        for( int i=0; i<count; i++ )
        {
            String shortName = decoder.getFunctionShortName( i );
            String longName = decoder.getFunctionLongName( i );
            if(( shortName == null )||( longName == null ))
            {
                fail( "Get CPUID function name failed." );
            }
            System.out.println( shortName + " = " + longName + "." );
            
            String[] up1 = decoder.getFunctionFirstTableUp( i );
            String[][] data1 = decoder.getFunctionFirstTable( i );
            if (( up1 == null )||( data1 == null ))
            {
                fail( "Get CPUID function first table failed." );
            }
            ChangeableTableModel model1 = 
                new ChangeableTableModel( up1, data1 );
            String table1 = tableReport( model1 );
            System.out.println( table1 );

            String[] up2 = decoder.getFunctionSecondTableUp( i );
            String[][] data2 = decoder.getFunctionSecondTable( i );
            if (( up2 == null )||( data2 == null ))
            {
                fail( "Get processor second table failed." );
            }
            ChangeableTableModel model2 = 
                new ChangeableTableModel( up2, data2 );
            String table2 = tableReport( model2 );
            System.out.println( table2 );
        }
        System.out.println();
    }
}
