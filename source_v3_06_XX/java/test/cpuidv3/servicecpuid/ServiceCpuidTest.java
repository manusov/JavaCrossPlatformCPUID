/*
Unit test for class: ServiceCpuid.java.
Loads text hex data for emulation CPUID, generating binary data
and reads text strings generated as text = F ( binary data ).
*/

package cpuidv3.servicecpuid;

import cpuidv3.servicecpudata.EntryCpuidSubfunction;
import cpuidv3.servicecpudata.ServiceCpudata.VENDOR_T;
import cpuidv3.servicecpudata.ServiceCpudata.HYPERVISOR_T;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class ServiceCpuidTest 
{

/*
Test template methods.    
*/    
    
    public ServiceCpuidTest()
    {
        System.out.println( "ServiceCpuidTest() runs." );
    }
    
    @BeforeClass
    public static void setUpClass()
    {
        System.out.println( "setUpClass() runs." );
    }
    
    @AfterClass
    public static void tearDownClass()
    {
        System.out.println( "tearDownClass() runs." );
    }
    
    @Before
    public void setUp()
    {
        System.out.println( "setUp() runs." );
    }
    
    @After
    public void tearDown()
    {
        System.out.println( "tearDown() runs." );
    }
    
    @Test
    public void testTarget()
    {
        System.out.println( "Test class ServiceCpuid.java.\r\n" );
        HelperHexLoader loader = new HelperHexLoader();
        HelperCpuidExtractor extractor = new HelperCpuidExtractor();
        HelperCpuidDumper dumper = new HelperCpuidDumper();
        
        int[] rawHexData = loader.loadHexDump( FILE_PATH );
        if( rawHexData != null )
        {
            EntryCpuidSubfunction[][] entries = 
                extractor.extractCpuid( rawHexData );
            
            if(( entries != null )&&( entries.length > 0 )&&
               ( entries[0] != null ))
            {
                System.out.println
                    ( entries.length + " CPUs, " + 
                      entries[0].length + 
                      " subfunctions entries per CPU.\r\n" + 
                      "Running CPUID dumper...\r\n" );
                dumper.printDump( entries );
                
// ========== Start test servicecpuid package. =================================

                System.out.println( "---------- Classes ----------" );
                // Test class ServiceCpudata.java.                        ( 1 )
                ServiceCpuid service = ServiceCpuid.getInstance();
                System.out.println( service );
                service.earlyVendors( EMULATED_V, EMULATED_H );
                
//              service.setCpuidDump( entries, CPU_INDEX );
                service.setCpuidDump( entries[CPU_INDEX] );
                
                System.out.println( "\r\n---------- Enumerator ----------" );
                String tableName = service.getEnumeratorName();
                String[] tableUp = service.getEnumeratorFirstTableUp();
                String[][] tableData = service.getEnumeratorFirstTable();
                dumper.printTable( tableName, tableUp, tableData );
                tableUp = service.getEnumeratorFirstTableUp();
                tableData = service.getEnumeratorFirstTable();
                dumper.printTable
                    ( "Enumerator second table", tableUp, tableData );
                
                System.out.println( "\r\n---------- Processor ----------" );
                tableUp = service.getProcessorFirstTableUp( CPU_INDEX );
                tableData = service.getProcessorFirstTable( CPU_INDEX );
                dumper.printTable
                    ( "Processor first table", tableUp, tableData );
                
                System.out.println( "\r\n---------- Hybrid ----------" );
                HybridReturn hr = service.getProcessorHybrid( CPU_INDEX );
                System.out.println( "Hybrid name : " + hr.hybridName );
                System.out.println( "Hybrid CPU  : " + hr.hybridCpu );
                System.out.println( "Hybrid SMT  : " + hr.hybridSmt );
                
                System.out.println
                    ( "\r\n---------- CPUID functions ----------\r\n" );
                int fncCount = service.getDetectedFunctionsCount();
                for( int fncIndex=0; fncIndex<fncCount; fncIndex++ )
                {
                    if( service.getFunctionShow( fncIndex ) )
                    {
                        String sName = service.getFunctionShortName( fncIndex );
                        String lName = service.getFunctionLongName( fncIndex );
                        String fncName = sName + " = " + lName;
                        String[] fncUp = 
                            service.getFunctionFirstTableUp( fncIndex );
                        String[][] fncData = 
                            service.getFunctionFirstTable( fncIndex );
                        dumper.printTable( fncName, fncUp, fncData );
                        fncUp = service.getFunctionSecondTableUp( fncIndex );
                        fncData = service.getFunctionSecondTable( fncIndex );
                        dumper.printTable( fncUp, fncData );
                        System.out.println();
                    }
                }
                
                // System.out.println();
                
// ========== End test servicecpuid package. ===================================

            }
            else
            {
                fail( "Text file interpreting FAILED." );
            }
        }
        else
        {
            fail( "Text file load FAILED." );
        }
    }
    
/*
Target functionality parameters.
*/    

    private final static String NAME_1 = 
            "AuthenticAMD0B20F40_K20_StrixPoint_06_CPUID.txt";
    private final static String NAME_2 = 
            "GenuineIntel00A06A4_MeteorLake_07_CPUID.txt";
    private final static String NAME_3 = 
            "AuthenticAMD0700F01_K16_Kabini3_CPUID_VM.txt";
    private final static String FILE_PATH = 
            "E:\\Exchange\\1\\dumps\\" + NAME_3;
            // "E:\\1\\dumps\\" + NAME_3;
    
    // (!) This must be consistent with loaded dump.
    private final static int CPU_INDEX = 0;  // (!) Note about index < ncpu.
    
    // (!) This must be consistent with loaded dump.
    private final static VENDOR_T EMULATED_V = 
            VENDOR_T.VENDOR_AMD;
    private final static HYPERVISOR_T EMULATED_H = 
            HYPERVISOR_T.HYPERVISOR_MICROSOFT;
    
    // Load and parse operation status representation.
    private enum LOAD_STATUS { RESULT_OK, READ_FAILED, NO_DATA, BAD_SIZE };
    
/*
Target functionality helpers methods with own classes:
Class for load text file with hex dump (InstLatX64 style file required).
Engineering sample from "Rethinking CPUID" project. Helper class.
Loading hex dump at InstLatX64 format (hardware platform emulation),
parsing it, result is int[] array.
*/    

    final class HelperHexLoader 
    {
        // Patterns for strings detection.
        private final static String ALL_S   =
            "CPUID 00000000: 00000000-00000000-00000000-00000000";
        private final static String START_S  = "CPUID";
        // Separate by ":" or TAB.
        private final static String SPLIT_S1 = ":|\t";
        // Separate by "-" or " ".
        private final static String SPLIT_S2 = "-| ";
        // Helper method for load text file, parse it and visual status.
        // INPUT:   parentWin = parent GUI frame
        //          filePath = loaded file path string
        // OUTPUT:  int[] array contains data if loaded OK, otherwise null.
        int[] loadHexDump( String filePath )
        {
            // (1) Setup context.
            LOAD_STATUS loadStatus = LOAD_STATUS.RESULT_OK;
            int count = 0;
            File file = new File( filePath );
            ArrayList<Integer> dump = new ArrayList<>();
            // (2) Loading and parsing text dump file by lines.
            try ( FileReader fr = new FileReader( file );
                  BufferedReader reader = new BufferedReader( fr ) )
            {
                String line = reader.readLine();
                while( line != null )
                {
                    line = line.trim().toUpperCase();
                    if ( ( line.length() >= ALL_S.length() ) && 
                         ( line.startsWith( START_S )      ) )
                    {
                        line = line.substring( START_S.length() ).trim();
                        String[] words = line.split( SPLIT_S1 );
                        if ( ( words != null ) && ( words.length >= 2 ) )
                        {
                            for( int i=0; i<words.length; i++ )
                            {   // Reject spaces and tabs at start and at end.
                                words[i] = words[i].replace("\t", " ").trim();
                            }
                            String function = words[0];
                            String[] values = 
                                //words[words.length - 1].split( SPLIT_S2 );
                                words[1].split( SPLIT_S2 );
                            if ( ( function.length() == 8 ) &&
                                 ( values.length >= 4 ) )
                            {
                                try
                                {
                                    int f = Integer.parseUnsignedInt
                                        ( function, 16 );
                                    dump.add( f );
                                    count++;
                                    for( int i=0; i<4; i++ )
                                    {
                                        int r = Integer.parseUnsignedInt
                                                    ( values[i], 16 );
                                        dump.add( r );
                                        count++;
                                    }
                                }
                                catch( NumberFormatException e ) { }
                            }
                        }
                    }
                    line = reader.readLine();
                }
                if( count <= 0 )
                {
                    loadStatus = LOAD_STATUS.NO_DATA;
                }
                else if ( ( count % 5 ) != 0)
                {
                    loadStatus = LOAD_STATUS.BAD_SIZE;
                }
            }
            catch( IOException ex )
            {
                loadStatus = LOAD_STATUS.READ_FAILED;
            }
            // (3) Check and interpreting result and status.
            switch ( loadStatus )
            {
                case RESULT_OK:    // File loaded and interpreted OK.
                    System.out.println
                        ( "Text file loaded OK:\r\n" + filePath  + "." );
                    break;
                case READ_FAILED:  // File loading failed: I/O error.
                    System.out.println
                        ( "Read failed " + 
                          "(InstLatx64 compatible report required)." );
                    break;
                case NO_DATA:      // File loading failed: no valid data found.
                    System.out.println
                        ( "No data (InstLatx64 compatible report required)." );
                    break;
                case BAD_SIZE:     // File parsing failed: incorrect dump size.
                    System.out.println
                        ( "Bad size " + 
                          "(InstLatx64 compatible report required)." );
                    break;
                default:           // File loading failed = unknown error.
                    System.out.println( "Unknown error." );
                    break;
            }
            // (4) Return result.
            if( dump.isEmpty() )
            {
                return null;
            }
            else
            {
                return dump.stream().mapToInt( Integer::intValue ).toArray();
            }
        }
    }
   
/*
Target functionality helpers methods with own classes:
Class for parsing int[] array with CPU data after InstLatX64 file load.
Engineering sample from "Rethinking CPUID" project. Helper class.
Parsing int[] array, create 2D array of EntryCpuidSubfunction data class:
[cpu#][subfunction#].
*/
    
    final class HelperCpuidExtractor 
    {
        EntryCpuidSubfunction[][] extractCpuid( int[] loadedData )
        {
            EntryCpuidSubfunction[][] cpuidDump = null;
            if ( ( loadedData != null )&&( loadedData.length > 0 )&&
                 ( ( loadedData.length % 5 ) == 0) )
            {
                // (1) Setup context.
                ArrayList<EntryCpuidSubfunction[]> a = new ArrayList<>();
                ArrayList<EntryCpuidSubfunction> b = new ArrayList<>();
                int subfunction = 0;
                int previous = -1;
                // (2) Cycle for generating entries.
                for( int i=0; i<loadedData.length; i+=5 )
                {
                    int function = loadedData[i + 0];
                    if(( function != previous )||( function == 0 ))
                    {
                        previous = function;
                        subfunction = 0;
                    }
                    int pass = 0;
                    int eax = loadedData[i + 1];
                    int ebx = loadedData[i + 2];
                    int ecx = loadedData[i + 3];
                    int edx = loadedData[i + 4];
                    EntryCpuidSubfunction entry = new EntryCpuidSubfunction
                        ( function, subfunction, pass, eax, ebx, ecx, edx );
                    b.add( entry );
                    subfunction++;
                    // (2.1) Detect end of all data or new logical CPU block.
                    if (( i == loadedData.length - 5 )||
                        ( loadedData[i + 5] == 0 ))
                    {
                        EntryCpuidSubfunction[] entries = 
                            b.toArray( new EntryCpuidSubfunction[ b.size() ] );
                        a.add( entries );
                        b.clear();
                    }
                }
                // (3) Verification results of interpreting, converting data.
                if( !a.isEmpty() )
                {
                    EntryCpuidSubfunction[][] temp = 
                        new EntryCpuidSubfunction[ a.size() ][];
                    boolean status = true;
                    // (3.1) Convert array list to array.
                    for( int i=0; i<a.size(); i++ )
                    {
                        if (( a.get( i ) == null )||( a.get( i ).length == 0 ))
                        {
                            status = false;
                            break;
                        }
                        temp[i] = 
                            new EntryCpuidSubfunction[ a.get( i ).length ];
                        System.arraycopy
                            ( a.get( i ), 0, temp[i], 0, a.get( i ).length );
                    }
                    // (3.2) Return result if valid.
                    if( status )
                    {
                        cpuidDump = temp;
                    }
                }
            }
            return cpuidDump;
        }
    }

/*
Target functionality helpers methods with own classes:
Class for dump CPUID information for all found logical processors.
Engineering sample from "Rethinking CPUID" project. Helper class.
Console print 2D array of EntryCpuidSubfunction data class, 
contains array [cpu#][subfunction#].
*/

    final class HelperCpuidDumper 
    {
        void printDump( EntryCpuidSubfunction[][] entries )
        {
            int cpuCount =entries.length;
            for( int i=0; i<cpuCount; i++ )
            {
                System.out.println( String.format( " CPU #%d", i ) );
                helperPrintTable( entries[i] );
            }
        }
    
        private void helperPrintTable( EntryCpuidSubfunction[] entry )
        {
            final String tableString = 
                " CPUID    subfunction  pass      " +
                "EAX      EBX      ECX      EDX";
            final int TAB_LINE_LENGTH = tableString.length() + 6;
            StringBuilder sb = new StringBuilder();
            for( int i=0; i<TAB_LINE_LENGTH; i++ ) { sb.append( "-" );  }
            String tabLine = sb.toString();
            System.out.println( tabLine );
            System.out.println( tableString );
            System.out.println( tabLine );
            for ( EntryCpuidSubfunction e : entry ) 
            {
                int fnc = e.function;
                int subfnc = e.subfunction;
                int pass = e.pass;
                int eax = e.eax;
                int ebx = e.ebx;
                int ecx = e.ecx;
                int edx = e.edx;
                String line = String.format
                    ( " %08X %08X     %08X  %08X %08X %08X %08X",
                      fnc, subfnc, pass, eax, ebx, ecx, edx );
                System.out.println( line );
            }
            System.out.println( tabLine + "\r\n");
        }
        
        // Build formatted report as single string with sub-strings.
        // Report format is text table.
        // Table can be used for text console output or saved to text file.
        void printTable
            ( String tableName, String[] tableUp, String[][] tableData )
            {
                printTable( tableName, tableUp, tableData, false );
            }
            
        void printTable( String[] tableUp, String[][] tableData )
        {
            printTable( null, tableUp, tableData, true );
        }
        
        void printTable
            ( String tableName, String[] tableUp, String[][] tableData, 
              boolean f )
        {
            // Verify report data consistency.
            if (( ( !f )&&( tableName == null ) )||( tableUp == null )||
                ( tableData == null ))
            {
                System.out.println
                    ( "Report FAILED: null object detected." );
                return;
            }
            if (( ( !f )&&( tableName.length() == 0 ) )||
                ( tableUp.length == 0 )||( tableData.length == 0 )||
                ( tableData[0] == null )||( tableData.length == 0))
            {
                System.out.println
                    ( "Report FAILED: null or zero-length object detected." );
                return;
            }
            // Additional verify report data consistency, count format values.
            int maxLengths[] = new int[tableUp.length];
            boolean consistency = true;
            for( int i=0; i<tableUp.length; i++ )
            {
                if( tableUp[i] == null )
                {
                    consistency = false;
                    break;
                }
                else
                {
                    maxLengths[i] = tableUp[i].length();
                }
            }
            for( int i=0; ( i<tableData.length ) && consistency; i++ )
            {
                if(( tableData[i] == null )||
                   ( tableData[i].length != maxLengths.length ))
                {
                    consistency = false;
                    break;
                }
                else
                {
                    for( int j=0; j<tableData[i].length; j++ )
                    {
                        if( tableData[i][j] == null )
                        {
                            consistency = false;
                            break;
                        }
                        else
                        {
                            int n = tableData[i][j].trim().length();
                            if( maxLengths[j] < n )
                            {
                                maxLengths[j] = n;
                            }
                        }
                    }
                }
            }
            if( !consistency )
            {
                System.out.println
                    ( "Report FAILED: inconsistent table data detected." );
                return;
            }
            // Consistency verification PASSED, now start build report.
            StringBuilder report = new StringBuilder( "" );
            int tableWidth = 1;
            for( int i=0; i<maxLengths.length; i++ )
            {
                tableWidth += maxLengths[i];
                tableWidth += 3;
            }
            StringBuilder sb = new StringBuilder( "" );
            for( int i=0; i<tableWidth; i++ )
            {
                sb.append( "-" );
            }
            // Table name.
            String tableLine = sb.toString();
            if ( !f )
            {
                String sName = String.format
                    ( "%s.\r\n", helperStringCell( tableName, 0 ) );
                report.append( sName );
                report.append( tableLine );
                report.append( "\r\n" );
            }
            // Table up.
            for( int i=0; i<maxLengths.length; i++ )
            {
                String s = ( i > 0 ) ? "  " : "";
                report.append( s );
                int cellWidth = maxLengths[i];
                report.append( helperStringCell( tableUp[i], cellWidth ) );
            }
            report.append( "\r\n" );
            report.append( tableLine );
            report.append( "\r\n" );
            // Table content.
            for ( String[] td : tableData ) 
            {
                for ( int j = 0; j<maxLengths.length; j++ )
                {
                    String s = ( j > 0 ) ? "  " : "";
                    report.append( s );
                    int cellWidth = maxLengths[j];
                    report.append( helperStringCell( td[j], cellWidth ) );
                }
                report.append( "\r\n" );
            }
        report.append( tableLine );
        System.out.println( report.toString() ); 
        }
    
        private static final int MAX_CELL = 120;
        private String helperStringCell( String s, int cellWidth )
        {
            s = s.trim();
            StringBuilder sb1 = new StringBuilder( " " );
            sb1.append( s );
            // Add spaces if required for formatting.
            if( cellWidth > 1 )
            {
                int count = cellWidth + 1 - s.length();
                for( int i=1; i<count; i++ )
                {
                    sb1.append( " " );
                }
            }
            // Limit string length by MAX_CELL constant.
            StringBuilder sb2 = sb1;
            if ( sb1.length() > MAX_CELL ) 
            {
                sb2.append( sb1.substring( 0, MAX_CELL - 2 ) );
                sb2.append( "..." );
            }
            return sb2.toString();
        }
    }
}
