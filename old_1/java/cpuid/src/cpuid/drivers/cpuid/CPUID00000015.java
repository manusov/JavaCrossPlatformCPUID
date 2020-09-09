/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
CPUID driver component:
CPUID standard function 00000015h declared as CPR.COMMAND.
TODO: add support ECX = nominal frequency of core crystal.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID00000015 extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME = 
    "TSC/Core crystal clock ratio";
    
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
        { "Denominator of the TSC/core crystal clock ratio" , 31 , 0 }
    };
private final static Object[][] DECODER_EBX =
    {
        { "Numerator of the TSC/core crystal clock ratio" , 31 , 0 }
    };
private final static Object[][] DECODER_ECX =
    {
        { "Nominal frequency of the core crystal" , 31 , 0 }
    };

// Calculate control data total size for output formatting
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX.length + 0;
private final static int NY2 = DECODER_EBX.length + 0;
private final static int NY3 = DECODER_ECX.length + 0;
private final static int NY  = NY1 + NY2 + NY3;

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
    int x1 = CPUID.findFunction( array, 0x00000015 );
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
    
    // Parameters from CPUID dump, EAX register
    int p=0;  // pointer for sequentally store strings in the table
    int y = (int) (array[x1+2] & (((long)((long)(-1)>>>32))) );      // y = EAX
    CPUID.decodeBitfields ( "EAX" , DECODER_EAX , y , result , p );
    result[p][NX-1] = String.format("%d",y);
    
    // Parameters from CPUID dump, EBX register
    p = NY1;
    y = (int) ( array[x1+2] >>> 32 );                                // y = EBX
    CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    result[p][NX-1] = String.format("%d",y);
    
    // Parameters from CPUID dump, EBX register
    p = NY1+NY2;
    y = (int) ( array[x1+3] & (((long)((long)(-1)>>>32))) );         // y = ECX
    CPUID.decodeBitfields ( "ECX" , DECODER_ECX , y , result , p );
    if ( y != 0 )
        {
        long hz = ( (long) y ) & ( ((long)((long)(-1)>>>32)) );
        double mHz = hz / 1000000.0; 
        result[p][NX-1] = String.format( "%.3f MHz", mHz );
        }
    else
        {
        result[p][NX-1] = "n/a";
        }
    
    // Result is ready, all strings filled
    return result;
    }
}