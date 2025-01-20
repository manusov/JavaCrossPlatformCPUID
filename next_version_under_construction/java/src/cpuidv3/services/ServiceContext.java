/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class supports get CPU and OS context management bitmaps (capability for save
and restore CPU registers context). Also converts context management bitmaps
to array of viewable text strings and AbstractTableModel. 
Argument is binary bitmaps. Result is array of text strings and 
AbstractTableModel consumed by GUI and text reports.

*/

package cpuidv3.services;

import static cpuidv3.services.HelperTableToReport.tableReport;
import static cpuidv3.services.PAL.REQUEST_GET_OS_CONTEXT;

class ServiceContext extends Service
{
    ServiceContext( SAL s ) { super( s ); }
    
    @Override String getTableName() { return 
        "CPU and OS context management features by XGETBV instruction."; }
    
    @Override String[] getTableUp() { return 
        new String[]{ "Feature", "Bit", "CPU validated", "OS validated" }; }

    private final static String[] CONTEXT_NAMES =
    {
        "x87 FPU/MMX, ST/MM registers" ,                             // bit 0
        "SSE 128-bit, XMM[0-15] registers " ,                        // bit 1
        "AVX 256-bit, YMM[0-15] registers" ,                         // bit 2
        "MPX BNDREG, bound registers" ,                              // bit 3
        "MPX BNDCSR, control and status registers" ,                 // bit 4
        "AVX512 64-bit predicates, K[0-7] registers" ,               // bit 5
        "AVX 512-bit, ZMM[0-15] registers" ,                         // bit 6
        "AVX 512-bit, ZMM[16-31] registers" ,                        // bit 7
        "Intel processor trace state (PT), reserved for IA32_XSS" ,  // bit 8
        "PKRU, protection key state" ,                               // bit 9
        "Reserved" ,                                                 // bit 10
        "CET user state (CET_U), reserved for IA32_XSS" ,            // bit 11
        "CET supervisor state (CET_S), reserved for IA32_XSS" ,      // bit 12
        "Hardware duty cycling state (HDC), reserved for IA32_XSS" , // bit 13
        "User interrupt (UINTR), reserved for IA32_XSS" ,            // bit 14
        "Last Branch Record (LBR), reserved for IA32_XSS" ,          // bit 15
        
        "Hardware P-states (HWP), reserved for IA32_XSS" ,           // bit 16
        "Intel AMX tile configuration (XTILECFG)" ,                  // bit 17
        "Intel AMX tile data (XTILEDATA)" ,                          // bit 18
        "Intel APX EGPR state (R16-R31)" ,                           // bit 19
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,                                                // bit 31

        "Reserved" ,                                                // bit 32
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,                                                // bit 47

        "Reserved" ,                                                // bit 48
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "Reserved" ,
        "LWP, Lightweight Profiling state" ,                        // bit 62
        "XCR0 vector expansion"                                     // bit 63
    };
   
    @Override String[][] getTableData()
    {
        long cpuMap = 0;
        long osMap = 0;
        // Get binary data use JNI call.
        PAL pal = sal.getPal();
        final int OPB_SIZE = 4;                         // 4 QWORDs = 32 bytes.
        long[] opb = new long[ OPB_SIZE ];
        if ( ( pal.platformRequest
                ( null, opb, REQUEST_GET_OS_CONTEXT, OPB_SIZE ) ) != 0 )
        {
            long status = opb[0];
            long count = status & 0xFFFFFFFFL;
            long error = status >>> 32;
            // Load results if valid.
            if(( error == 0 )&&( count >= 1 ))
            {
                cpuMap = opb[2];
            }
            if(( error == 0 )&&( count >= 2 ))
            {
                osMap = opb[3];
            }
        }
        
        int count = CONTEXT_NAMES.length;
        long mask = 1;
        String[][] tableData = new String[count][4];
        for ( int i=0; i<count; i++ )
        {
            tableData[i][0] = CONTEXT_NAMES[i];
            tableData[i][1] = "" + i;
            tableData[i][2] = ( ( cpuMap & mask ) == 0 ) ? "0" : "1";
            tableData[i][3] = ( ( osMap & mask ) == 0 ) ? "0" : "1";
            mask <<= 1;
        }
        
        return tableData;
    }
    
    @Override void printSummaryReport()
    {
        String s = getTableName();
        System.out.println( "[ " + s + "]\r\n" );
        s = tableReport
            ( new ChangeableTableModel( getTableUp(), getTableData() ) );
        System.out.println( s );
    }
}
