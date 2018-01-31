/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
CPUID driver component:
CPUID standard function 00000004h declared as CPR.COMMAND.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID00000004 extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME =
    "Deterministic cache parameters";

// Parameters table up string
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "L1 code" , "L1 data" , "L2 unified" , "L3 unified" };

// Return if function not found
private final static String[][] NO_RESULT =
    {
        { "n/a" , "" , "" , "" , "" }
    };

// Control tables for results decoding
private final static String[] CACHE_PARMS =
    {
    "Cache size (KB)",
    "System coherency line size (bytes)",
    "Physical line partitions",
    "Ways of associativity",
    "Number of sets",
    "Max. logical CPUs per this cache",
    "Max. cores per physical package",
    "Self initializing cache level",
    "Fully associative cache",
    "WBINVD/INVD lower caches levels",
    "Inclusive of lower cache levels",
    "Direct mapped (0) or complex(1)"
    };

// Calculate control data total size for output formatting
private final static int NX = COMMAND_UP_1.length;
private final static int NY = CACHE_PARMS.length;

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
    int pointer = CPUID.findFunction( array, 0x00000004 );
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
    // Get total number of CPUID dump entries
    int count = (int) ( ( array[0] ) & (long) ( (long)(-1) >>> 32) );
    // Built left column with fixed parameters names
    for ( int i=0; i<NY; i++ ) { result[i][0] = CACHE_PARMS[i]; }
    // Variables 
    int function, rEAX, rEBX, rECX, rEDX, j, y1, y2, y3, y4;
    // Cycle for sub-leafs of CPUID function 4
    while ( count > 0 )
        {
        // Get function number from current entry
        function = (int) ( array[ pointer ] >>> 32 );
        // Break if end of function 4 subfunctions sequence
        if ( function != 0x00000004 ) { break; }
        // Get CPUID result registers values
        rEAX = (int) ( ( array[ pointer+2 ] ) & (long) ( (long)(-1) >>> 32) );
        rEBX = (int) ( array[ pointer+2 ] >>> 32 );
        rECX = (int) ( ( array[ pointer+3 ] ) & (long) ( (long)(-1) >>> 32) );
        rEDX = (int) ( array[ pointer+3 ] >>> 32 );
        // Detect cache level and type by tag value
        j=0;
        if ( ( rEAX & 0xFF ) == 0x22 ) j=1;    // L1 code
        if ( ( rEAX & 0xFF ) == 0x21 ) j=2;    // L1 data
        if ( ( rEAX & 0xFF ) == 0x43 ) j=3;    // L2 unified
        if ( ( rEAX & 0xFF ) == 0x63 ) j=4;    // L3 unified
        if ( j != 0 )
            {
            // Temporary variables with cache parameters    
            y1 = rECX + 1;
            y2 = ( rEBX & 0xFFF ) + 1;
            y3 = ( (rEBX >> 12) & 0x3FF) + 1;
            y4 = ( (rEBX >> 22) & 0x3FF) + 1;
            // Fill j-selected column with extracted numeric values
            result[0][j]  = String.format( "%d" , y1*y2*y3*y4 / 1024 );
            result[1][j]  = String.format( "%d" , y2 );
            result[2][j]  = String.format( "%d" , y3 );
            result[3][j]  = String.format( "%d" , y4 );
            result[4][j]  = String.format( "%d" , y1 );
            result[5][j]  = String.format( "%d" , ((rEAX >> 14) & 0xFFF) + 1);
            result[6][j]  = String.format( "%d" , ((rEAX >> 26) & 0x3F) + 1 );
            result[7][j]  = String.format( "%d" , (rEAX & 0x0100) >> 8);
            result[8][j]  = String.format( "%d" , (rEAX & 0x0200) >> 9);
            result[9][j]  = String.format( "%d" , rEDX & 0x0001);
            result[10][j] = String.format( "%d" , (rEDX & 0x0002) >> 1);
            result[11][j] = String.format( "%d" , (rEDX & 0x0004) >> 2);
            }
        pointer += 4;  // Units = 8 bytes (long), 4*8=32 bytes per entry    
        count--;       // Units = entries, 32-bytes per entry
        }
    // Result is ready, all strings filled
    return result;
    }
}
