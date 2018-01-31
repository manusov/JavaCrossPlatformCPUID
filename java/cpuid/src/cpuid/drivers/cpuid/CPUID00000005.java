/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
CPUID driver component:
CPUID standard function 00000005h declared as CPR.COMMAND.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID00000005 extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME = 
    "MONITOR/MWAIT features";
    
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
        { "Smallest monitor line size" , 15 , 0 }
    };
private final static Object[][] DECODER_EBX =
    {
        { "Largest monitor line size" , 15 , 0 }
    };
private final static Object[][] DECODER_ECX =
    {
        { "Enumeration of MONITOR-MWAIT extensions flag"       , 0 , 0 } ,
        { "Interrupt break event for MWAIT (even if disabled)" , 1 , 1 }
    };
private final static Object[][] DECODER_EDX =
    {
        { "Number of C0 sub C-states supported using MWAIT" ,  3 ,  0 } ,
        { "Number of C1 sub C-states supported using MWAIT" ,  7 ,  4 } ,
        { "Number of C2 sub C-states supported using MWAIT" , 11 ,  8 } ,
        { "Number of C3 sub C-states supported using MWAIT" , 15 , 12 } ,
        { "Number of C4 sub C-states supported using MWAIT" , 19 , 16 } ,
        { "Number of C5 sub C-states supported using MWAIT" , 23 , 20 } ,
        { "Number of C6 sub C-states supported using MWAIT" , 27 , 24 } ,
        { "Number of C7 sub C-states supported using MWAIT" , 31 , 28 } 
    };

// Calculate control data total size for output formatting
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX.length;
private final static int NY2 = DECODER_EBX.length;
private final static int NY3 = DECODER_ECX.length;
private final static int NY4 = DECODER_EDX.length;
private final static int NY  = NY1+NY2+NY3+NY4;

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
    int x = CPUID.findFunction( array, 0x00000005 );
    // Return "n/a" if this function entry not found
    if (x<0) { return NO_RESULT; }
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
    int y = (int) ( array[x+2] & (((long)((long)(-1)>>>32))) );      // y = EAX
    int[] z = CPUID.decodeBitfields ( "EAX" , DECODER_EAX , y , result , p );
    result[p][4] = String.format( "%d Bytes", z[0] );
    // Parameters from CPUID dump, EBX register
    p = NY1;
    y = (int) ( array[x+2] >>> 32 );                                 // y = EBX
    z = CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    result[p][4] = String.format( "%d Bytes", z[0] );
    // Parameters from CPUID dump, ECX register
    p = NY1+NY2;
    y = (int) ( array[x+3] & (((long)((long)(-1)>>>32))) );          // y = ECX
    z = CPUID.decodeBitfields ( "ECX" , DECODER_ECX , y , result , p );
    for ( int i=0; i<z.length; i++ ) 
        { result[p+i][4] = String.format("%d", z[i]); }
    // Parameters from CPUID dump, EDX register
    p = NY1+NY2+NY3;
    y = (int) ( array[x+3] >>> 32 );                                 // y = EDX
    z = CPUID.decodeBitfields ( "EDX" , DECODER_EDX , y , result , p );
    for ( int i=0; i<z.length; i++ ) 
        { result[p+i][4] = String.format("%d", z[i]); }
    // Result is ready, all strings filled
    return result;
    }

}
