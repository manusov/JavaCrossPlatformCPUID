/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class provides CPUID dump binary data from runtime platform, include SMP
support and affinized execution of CPUID instruction at required logical
processor. Legacy single-thread non-affinized mode also supported.
JNI library, used bt ServiceCpuidPhysical.java, executes CPUID instruction
at runtime.

*/

package cpuidv3.sal;

import cpuidv3.pal.PAL;
import static cpuidv3.sal.HelperTableToReport.tableReport;
import java.util.ArrayList;

class ServiceCpuidPhysical extends Service
{
    private final static int MP_LIMIT = 64;   // TODO. Remove this limit.

    ServiceCpuidPhysical( SAL s ) { super( s ); }
    
    private long[][] binaryData = null;
    
    @Override boolean internalLoadBinaryData()
    {
        boolean status = false;
        if ( binaryData != null )
        {   // Use cached binary data, if already loaded.
            status = true;
        }
        else
        {   // Create new binary data if yet not loaded or cleared.
            PAL pal = sal.getPal();
            long[] platformArray = pal.getPlatformInfo();
            if (( platformArray != null )&&( platformArray.length > 0 ))
            {
                int cpuCount = (int)( platformArray[0] & 0xFFFFFFFFL );
                if( cpuCount > 0 )
                {
                    if( cpuCount > MP_LIMIT )
                    {
                        cpuCount = MP_LIMIT;
                    }
                    long[][] tempData = new long[cpuCount][];
                    for( int cpuIndex=0; cpuIndex<cpuCount; cpuIndex++ )
                    {
                        tempData[cpuIndex] = pal.getCpuidAffinized( cpuIndex );
                    }
                    binaryData = tempData;
                    status = true;
                }
            }
        }
        return status;
    }
    
    @Override boolean internalLoadNonAffinized()
    {
        boolean status = false;
        binaryData = null;
        PAL pal = sal.getPal();
        long[] tempData = pal.getCpuid();
        if (( tempData != null )&&( tempData.length > 0 ))
        {
            binaryData = new long[][]{ tempData };
            status = true;
        }
        return status;
    }
    
    @Override void clearBinaryData()
    {
        binaryData = null;
    }
    
    @Override long[][] getBinaryData()
    {
        return binaryData;
    }
    
    @Override void setBinaryData( long[][] data )
    {
        binaryData = data;
    }
    
    @Override String getTableName() 
        { return "CPUID instruction results as hex dump."; }

    @Override String[] getTableUp()
        { return new String[]{ "Logical CPU", "Function", "Subfunction", 
            "Pass", "EAX", "EBX", "ECX", "EDX" }; }
    
    private final static String[] TABLE_EMPTY_LINE = 
        { "", "", "", "", "", "", "", "" };
    private final static String[] TABLE_ERROR_LINE = 
        { "CPUID failed", "?", "?", "?", "?", "?", "?", "?" };
    
    @Override String[][] getTableData()
    {
        ArrayList<String[]> tableList = new ArrayList<>();
        if ( internalLoadBinaryData() )
        {
            long[][] dumpAll = getBinaryData();    // TODO. Think about unification with binary and text loaders. Use CpuEntry, CpuidSubfunctionEntry ? Or memory usage ?
            for( int i=0; i<dumpAll.length; i++ )
            {
                long[] dumpOne = dumpAll[i];
                for( int j=0; ( dumpOne != null )&&( j<dumpOne.length ); j+=4 )
                {
                    int function = (int)( dumpOne[j] >>> 32 );
                    int subfunction = (int)( dumpOne[j+1] & 0xFFFFFFFFL );
                    int pass = (int)( dumpOne[j+1] >>> 32 );
                    int eax = (int)( dumpOne[j+2] & 0xFFFFFFFFL );
                    int ebx = (int)( dumpOne[j+2] >>> 32 );
                    int ecx = (int)( dumpOne[j+3] & 0xFFFFFFFFL );
                    int edx = (int)( dumpOne[j+3] >>> 32 );
                    String[] line = new String[ getTableUp().length ];
                    line[0] = "" + i;
                    line[1] = String.format("%08X", function );
                    line[2] = String.format("%08X", subfunction );
                    line[3] = String.format("%08X", pass );
                    line[4] = String.format("%08X", eax );
                    line[5] = String.format("%08X", ebx );
                    line[6] = String.format("%08X", ecx );
                    line[7] = String.format("%08X", edx );
                    tableList.add( line );
                }
                if ( i < ( dumpAll.length - 1 ) )
                {
                    tableList.add( TABLE_EMPTY_LINE );
                    tableList.add( getTableUp() );
                }
            }
        }
        return tableList.isEmpty() ? 
            new String[][] { TABLE_ERROR_LINE } :
            tableList.toArray( new String[tableList.size()][] );
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
