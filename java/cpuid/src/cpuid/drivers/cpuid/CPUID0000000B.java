/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
CPUID driver component:
CPUID standard function 0000000Bh declared as CPR.COMMAND.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID0000000B extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME = 
    "Extended multiprocessing topology information";
    
// Parameters table up string
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "SMT" , "Core" };

// Return if function not found
private final static String[][] NO_RESULT =
    {
        { "n/a" , "" , "" , "" , "" }
    };

// Control tables for results decoding
private final static String[] SMT_PARMS = 
    {
    "Number of logical processor at this level type" ,
    "Bits shift right on x2APIC ID to get next level ID" ,
    "Level number" ,
    "Level type" ,
    "Current x2APIC ID"
    };

// Calculate control data total size for output formatting
private final static int NX = COMMAND_UP_1.length;
private final static int NY = SMT_PARMS.length;

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
    int pointer = CPUID.findFunction( array, 0x0000000B );
    // Return "n/a" if this function entry not found
    if ( pointer < 0 ) { return NO_RESULT; }
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
    int count = (int) ( ( array[0] ) & (long) ( (long)(-1) >>> 32) );
    // Left vertical columns fixed names
    for ( int i=0; i<NY; i++ ) { result[i][0] = SMT_PARMS[i]; }
    // Temporary variables
    int function, rEAX, rEBX, rECX, rEDX, j;
    // Cycle for sub-leafs of CPUID function 0000000Bh
    while ( count > 0 )
        {
        // Get CPUID function number field
        function = (int) ( array[ pointer ] >>> 32 );
        // Break if end of sub-leafs of function 0000000Bh
        if ( function != 0x0000000B ) { break; }
        rEAX = (int) ( ( array[ pointer+2 ] ) & (long) ( (long)(-1) >>> 32) );
        rEBX = (int) ( array[ pointer+2 ] >>> 32 );
        rECX = (int) ( ( array[ pointer+3 ] ) & (long) ( (long)(-1) >>> 32) );
        rEDX = (int) ( array[ pointer+3 ] >>> 32 );
        j = ( rECX >> 8 ) & 0xFF;
        // Write fields with numeric parameters
        if ((j==1)|(j==2))  // Detect known codes
            {
            result[0][j] = String.format( "%d"   , rEBX & 0xFFFF );
            result[1][j] = String.format( "%d"   , rEAX & 0x1F );
            result[2][j] = String.format( "%d"   , rECX & 0xFF );
            result[3][j] = String.format( "%d"   , (rECX >> 8) & 0xFF );
            result[4][j] = String.format( "%08Xh" , rEDX );
            }
        pointer += 4;  // Units = 8 bytes (long), 4*8=32 bytes per entry    
        count--;       // Units = entries, 32-bytes per entry
        }
    // Result is ready, all strings filled
    return result;
    }
}