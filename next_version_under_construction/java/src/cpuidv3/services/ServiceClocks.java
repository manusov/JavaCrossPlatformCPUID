/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class provides measurement of CPU TSC (Time Stamp Counter) clock frequency.
Also converts frequency value to array of viewable text strings
and AbstractTableModel.
Argument is frequency value. Result is array of text strings and
AbstractTableModel consumed by GUI and text reports.

*/

package cpuidv3.services;

import static cpuidv3.services.HelperTableToReport.tableReport;
import static cpuidv3.services.PAL.REQUEST_MEASURE_TSC_FREQUENCY;


class ServiceClocks extends Service
{
    ServiceClocks( SAL s ) { super( s ); }
    
    @Override String getTableName() 
        { return "CPU clock measurement by RDTSC instruction."; }

    @Override String[] getTableUp()
        { return new String[]{ "Parameter", "Value" }; }

    @Override String[][] getTableData()
    {
        double frequency = 0.0;
        PAL pal = sal.getPal();
        int OPB_SIZE = 2;                             // 2 QWORDs = 16 bytes.
        long[] opb = new long[ OPB_SIZE ];
        if ( ( pal.platformRequest
                ( null, opb, REQUEST_MEASURE_TSC_FREQUENCY, OPB_SIZE ) ) != 0 )
        {
            long status = opb[0];
            long count = status & 0xFFFFFFFFL;
            long error = status >>> 32;
            // Load results if valid.
            if((error == 0)&&(count >= 1))
            {
                frequency = opb[1];
            }
        }
        
        frequency /= 1000000.0;
        String s = String.format( "%.2f MHz", frequency );
        return new String[][] { { "Time Stamp Counter clock", s } };
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
