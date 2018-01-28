//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID standard function 00000016h declared as CPR.COMMAND.

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID00000016 extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME =
    "Processor frequency information";
    
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
        { "Processor base frequency" , 15 , 0 }
    };
private final static Object[][] DECODER_EBX =
    {
        { "Maximum frequency" , 15 , 0 }
    };
private final static Object[][] DECODER_ECX =
    {
        { "Bus (reference) frequency" , 15 , 0 }
    };

// Calculate control data total size for output formatting
private final static int NX   = COMMAND_UP_1.length;
private final static int NY1  = DECODER_EAX.length + 0;
private final static int NY2  = DECODER_EBX.length + 0;
private final static int NY3  = DECODER_EBX.length + 0;
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
    int x1 = CPUID.findFunction( array, 0x00000016 );
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
    int[] z = CPUID.decodeBitfields ( "EAX" , DECODER_EAX , y , result , p );
    result[p][4] = z[0] + " MHz";
    // Parameters from CPUID dump, EBX register
    p = NY1;
    y = (int) ( array[x1+2] >>> 32 );                                // y = EBX
    z = CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    result[p][4] = z[0] + " MHz";
    // Parameters from CPUID dump, ECX register
    p = NY1+NY2;
    y = (int) ( array[x1+3] & (((long)((long)(-1)>>>32))) );         // y = ECX
    z = CPUID.decodeBitfields ( "ECX" , DECODER_ECX , y , result , p );
    result[p][4] = z[0] + " MHz";
    // Result is ready, all strings filled
    return result;
    }
    
}