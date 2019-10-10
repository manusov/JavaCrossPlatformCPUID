/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
CPUID driver component:
CPUID extended function 80000020h declared as CPR.COMMAND.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID80000020 extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME =
    "AMD bandwidth enforcement";
    
// Parameters table up string
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };

// Return if function not found
private final static String[][] NO_RESULT =
    {
        { "n/a" , "" , "" , "" , "" }
    };

// Control tables for results decoding
private final static String[][] DECODER_EBX0 =
    {
        { "Reserved" , "x" } ,
        { "L3BE"     , "L3 external read bandwidth enforcement" }
    };
private final static Object[][] DECODER_EAX1 =
    {
        { "L3 external read bandwidth enforcement bit range length" , 
          31 , 0 } , 
    };
private final static Object[][] DECODER_EDX1 =
    {
        { "Maximum COS number for L3 external read bandwidth enforcement" , 
          31 , 0 } 
    };


// Calculate control data total size for output formatting
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EBX0.length + 0 + 1;
private final static int NY2 = DECODER_EAX1.length + 0;
private final static int NY3 = DECODER_EDX1.length + 0;
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
    int x1 = CPUID.findFunction( array, 0x80000020 );
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
    
    // Subfunction 0, parameters from CPUID dump, EAX register
    int p=0;  // pointer for sequentally store strings in the table
    int y = (int) ( array[x1+2] >>> 32 );                           // y = EBX
    CPUID.decodeBitmap ( "EBX" , DECODER_EBX0 , y , result , p );
    
    // check sub-functions entries list continuation
    x1 = x1 + 4;
    if (x1>=x2) { return result; }             // Return if dump size limit
    y = (int) ( array[x1] >>> 32 );
    if (y != 0x80000020 ) { return result; }   // End of function entries
    
    // Subfunction 1, EAX
    p = p + NY1;
    y = (int) (array[x1+2] & (((long)((long)(-1)>>>32))) );          // y = EAX
    CPUID.decodeBitfields ( "EAX" , DECODER_EAX1 , y , result , p );

    // Subfunction 1, EDX
    p = p + NY2;
    y = (int) ( array[x1+3] >>> 32 );                                // y = EDX
    CPUID.decodeBitfields ( "EDX" , DECODER_EDX1 , y , result , p );
    
    // Result is ready, all strings filled
    return result;
    }

}
