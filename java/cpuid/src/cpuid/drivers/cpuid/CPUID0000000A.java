/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
CPUID driver component:
CPUID standard function 0000000Ah declared as CPR.COMMAND.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID0000000A extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME = 
    "Architectural performance monitoring features";

// Parameters table up string
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };

// Return if function not found
private final static String[][] NO_RESULT =
    {
        { "n/a" , "" , "" , "" , "" }
    };

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    {
        { "Version ID of architectural performance monitoring" , 7, 0 } ,
        { "Number of GP PMC per logical CPU" , 15 , 8 } ,
        { "Bit width of GP PMC" , 23 , 16 } ,
        { "Length of bit vector to enumerate architectural PM events" , 31 , 24 }
    };
private final static Object[][] DECODER_EBX =
    {
        { "Core cycle event not available flag", 0, 0 } ,
        { "Instruction retired event not available flag", 1, 1 } ,
        { "Reference cycles event not available flag", 2, 2 } ,
        { "Last level cache reference event not available flag", 3, 3 } ,
        { "Last level cache misses event not available flag", 4, 4 } ,
        { "Branch instruction retired event not available flag", 5, 5 } ,
        { "Branch mispredict retired event not available flag", 6, 6 }
    };
private final static Object[][] DECODER_EDX =
    {
        { "Number of fixed-function performance counters" , 4, 0 } ,
        { "Bit width of fixed-function performance counters" , 12, 5 } ,
        { "Any thread deprecation" , 15 , 15 }
    };

// Calculate control data total size for output formatting
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX.length + 1;
private final static int NY2 = DECODER_EBX.length + 1;
private final static int NY3 = DECODER_EDX.length + 0;
private final static int NY  = NY1+NY2+NY3;

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
    int x = CPUID.findFunction( array, 0x0000000A );
    // Return "n/a" if this function entry not found
    if (x<0) { return NO_RESULT; }
    // Build and pre-blank result text array
    String[][] result = new String[NY][NX];  // Text formatted by control data
    for (int i=0; i<NY; i++)  // This cycle for rows
        { 
        for(int j=0; j<NX; j++)  // This cycle for columns
            { 
            result[i][j]=""; 
            } 
        }
    // Parameters from CPUID dump, EAX register
    int p=0;  // pointer for sequentally store strings in the table
    int y = (int) ( array[x+2] & (((long)((long)(-1)>>>32))) );      // y = EAX
    int[] z = CPUID.decodeBitfields ( "EAX" , DECODER_EAX , y , result , p );
    for( int i=0; i<z.length; i++ )
        { result[p+i][4] = String.format( "%d", z[i] ); }
    // Parameters from CPUID dump, EBX register
    p = NY1;
    y = (int) ( array[x+2] >>> 32 );                                 // y = EBX
    CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    // Parameters from CPUID dump, EDX register
    p = NY1+NY2;
    y = (int) ( array[x+3] >>> 32 );                                 // y = EDX
    z = CPUID.decodeBitfields ( "EDX" , DECODER_EDX , y , result , p );
    for( int i=0; i<z.length; i++ )
        { result[p+i][4] = String.format( "%d", z[i] ); }
    // Result is ready, all strings filled
    return result;
    }

}