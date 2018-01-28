//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID standard function 00000003h declared as CPR.COMMAND.

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID00000003 extends CommandAdapter
{
// CPUID function full name 
private static final String F_NAME =
    "Processor Serial Number";

// Parameters table up string
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Value, hex" };

// Return if function not found
private final static String[][] NO_RESULT =
    {
        { "n/a" , "" }
    };

// Calculate control data total size for output formatting
private final static int NX = COMMAND_UP_1.length;
private final static int NY = 1;

// Return CPUID this function full name
// INPUT:   Reserved array
// OUTPUT:  String, CPUID function full name
@Override public String getCommandLongName(long[] dummy ) 
    { return F_NAME; }

// Return CPUID this function parameters table up string
// INPUT:   Reserved array
// OUTPUT:  String, CPUID function details table up string
@Override public String[] getCommandUp1( long[] array )
    { return COMMAND_UP_1; }

// Build and return CPUID this function detail information table
// INPUT:   Binary array = CPUID dump data
// OUTPUT:  Array of strings = CPUID this function detail information table
@Override public String[][] getCommandText1( long[] array )
    {
    // Scan binary dump, find entry for this function
    int x1 = CPUID.findFunction( array, 0x00000001 );
    int x2 = CPUID.findFunction( array, 0x00000003 );
    // Return "n/a" if this function entry not found
    if ((x1<0)|(x2<0)) { return NO_RESULT; }
    // Build and pre-blank result text array
    String[][] result = new String[NY][NX];  // Text formatted by control data
    for (int i=0; i<NY; i++)  // Cycle for rows
        { 
        for(int j=0; j<NX; j++)  // Cycle for columns
            { 
            result[i][j]="";
            }
        }
    // Get PSN fields and check CPU PSN feature bit
    long y1 = array[x1+2];                            // Fn1.EAX = PSN, High 
    long y2 = (array[x1+3] >> 32) & ( 1 << 18 );      // Check PSN feature
    long y3 = array[x2+3];                            // Fn2.EDX = Middle 
    if (y2==0) { return NO_RESULT; }                  // Fn2.ECX = Low
    // Extract CPU PSN bit fields from CPUID results
    int[] z = new int[6];
    z[0] = (int)( ( y1 >> 16 ) & 0xFFFF );
    z[1] = (int)( y1 & 0xFFFF );
    z[2] = (int)( ( y3 >> 48 ) & 0xFFFF );
    z[3] = (int)( ( y3 >> 32 ) & 0xFFFF );
    z[4] = (int)( ( y3 >> 16 ) & 0xFFFF );
    z[5] = (int)( y3 & 0xFFFF );
    // Prepare text table
    result[0][0] = "Processor Serial Number (PSN)";
    result[0][1] = "";
    // Build PSN numeric fields
    for (int i=0; i<z.length; i++)
        {
        result[0][1] = result[0][1] + String.format( "%04X", z[i] );
        if ( i<z.length-1 ) { result[0][1] = result[0][1] + "-"; }
        }
    // Result is ready, all strings filled
    return result;
    }

}
