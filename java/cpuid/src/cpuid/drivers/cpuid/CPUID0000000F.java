/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
CPUID driver component:
CPUID standard function 0000000Fh declared as CPR.COMMAND.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID0000000F extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME = 
    "Platform Quality of Service enumeration";
    
// Parameters table up string
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };

// Return if function not found
private final static String[][] NO_RESULT =
    {
        { "n/a" , "" , "" , "" , "" }
    };

// Control tables for results decoding
private final static Object[][] DECODER_EBX0 =
    {
        { "Maximum range of RMID within this physical CPU" , 31 , 0 }
    };
private final static String[][] DECODER_EDX0 =
    {
        { "x"      , "Reserved" } ,
        { "L3 QoS" , "L3 cache quality of service monitoring" }
    };
private final static Object[][] DECODER_EBX1 =
    {
        { "Conversion factor from IA32_QM_CTR to metric" , 31 , 0 }
    };
private final static Object[][] DECODER_ECX1 =
    {
        { "Maximum range of RMID of this resource type" , 31 , 0 }
    };
private final static String[][] DECODER_EDX1 =
    {
        { "L3 OM" , "L3 occupancy monitoring"       } ,
        { "L3 TB" , "L3 total bandwidth monitoring" } ,
        { "L3 LB" , "L3 local bandwidth monitoring" }
    };

// Calculate control data total size for output formatting
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EBX0.length + 0;
private final static int NY2 = DECODER_EDX0.length + 1;
private final static int NY3 = DECODER_EBX1.length + 0;
private final static int NY4 = DECODER_ECX1.length + 0;
private final static int NY5 = DECODER_EDX1.length + 0;
private final static int NSF = 2; // 10;  // max. number of subfunctions
private final static int NY  = NY1+NY2 + (NY3+NY4+NY5) * NSF;

// Return CPUID this function full name
// INPUT:   Reserved array
// OUTPUT:  String, CPUID function full name
@Override public String getCommandLongName(long[] dummy ) 
    { return F_NAME; }

// Return CPUID this function parameters table up string
// INPUT:   Reserved array
// OUTPUT:  String, CPUID function details table up string
@Override public String[] getCommandUp1( long[] dummy )
    { return COMMAND_UP_1; }

// Build and return CPUID this function detail information table
// INPUT:   Binary array = CPUID dump data
// OUTPUT:  Array of strings = CPUID this function detail information table
@Override public String[][] getCommandText1( long[] array )
    {
    // Scan binary dump, find entry for this function
    int x1 = CPUID.findFunction( array, 0x0000000F );
    // Return "n/a" if this function entry not found
    if (x1<0) { return NO_RESULT; }
    // Build and pre-blank result text array
    String[][] result = new String[NY][NX];  // Text formatted by control data
    for (int i=0; i<NY; i++)  // Cycle for rows
        { 
        for(int j=0; j<NX; j++)  // Cycle for columns
            { 
            result[i][j]=""; 
            } 
        }
    // Get number of entries per CPUID dump, field from dump header
    int x2 = (int) ( array[0] & (((long)((long)(-1)>>>32))) );
    x2 = (x2+1)*4;  // Calculate x2 = limit
    // Parameters from CPUID dump, EBX register
    int p=0;  // pointer for sequentally store strings in the table
    int y = (int) ( array[x1+2] >>> 32 );                            // y = EBX
    CPUID.decodeBitfields ( "EBX" , DECODER_EBX0 , y , result , p );
    // Parameters from CPUID dump, EDX register
    p = NY1;
    y = (int) ( array[x1+3] >>> 32 );                                // y = EDX
    CPUID.decodeBitmap ( "EDX" , DECODER_EDX0 , y , result , p );
    // Start cycle for sub-leafs (sub-functions) of CPUID function 0000000Fh
    p = NY1 + NY2;
    for ( int i=0; i<NSF; i++ )  // Cycle for sub-functions
        {
        x1 = x1 + 4;
        if (x1>=x2) { break; }                     // Return if dump size limit
        y = (int) ( array[x1] >>> 32 );
        if (y != 0x0000000F ) { break; }           // End of function entries
        // Parameters from CPUID dump, EBX register
        y = (int) ( array[x1+2] >>> 32 );                            // y = EBX
        CPUID.decodeBitfields ( "EBX" , DECODER_EBX1 , y , result , p );
        p = p + NY3;
        // Parameters from CPUID dump, ECX register
        y = (int) ( array[x1+3] & (((long)((long)(-1)>>>32))) );     // y = ECX
        CPUID.decodeBitfields ( "ECX" , DECODER_ECX1 , y , result , p );
        p = p + NY4;
        // Parameters from CPUID dump, EDX register
        y = (int) ( array[x1+3] >>> 32 );                            // y = EDX
        CPUID.decodeBitmap ( "EDX" , DECODER_EDX1 , y , result , p );
        p = p + NY5;
        }
    // Trim extra strings
    String[][] result1 = new String[p][NX];
    // Cycle for pre-blank text table
    for (int i=0; i<p; i++)  // Cycle for rows, copy valid part of table 
        {   // Cycle for columns
        System.arraycopy( result[i] , 0 , result1[i] , 0 , NX );
        }
    // Result is ready, all strings filled
    return result1;
    }

}