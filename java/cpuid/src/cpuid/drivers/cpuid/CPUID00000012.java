/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
CPUID driver component:
CPUID standard function 00000012h declared as CPR.COMMAND.
TODO: Required add sub-leafs.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID00000012 extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME =
    "Intel Security Guard Extensions information";
    
// Parameters table up string
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };

// Return if function not found
private final static String[][] NO_RESULT =
    {
        { "n/a" , "" , "" , "" , "" }
    };

// Control tables for results decoding
private final static String[][] DECODER_EAX0 =
    {
        { "SGX1" , "SGX1 instruction set" } , 
        { "SGX2" , "SGX2 instruction set" } ,
        { "x"      , "Reserved" } , 
        { "x"      , "Reserved" } , 
        { "x"      , "Reserved" } , 
        { "ENCLV"  , " " } , 
        { "ETRACK" , " " }
    };
private final static Object[][] DECODER_EBX0 =
    {
        { "MISCSELECT extended features bit vector" , 31 , 0 } , 
    };
private final static Object[][] DECODER_EDX0 =
    {
        { "Enclave non-64 bit mode size length bits" ,  7 , 0 } , 
        { "Enclave 64-bit mode size length bits"     , 15 , 8 } 
    };

private final static Object[][] DECODER_EAX1 =
    {
        { "Validity bitmap for SECS.ATTRIBUTES[31-0]" ,  31 , 0 } 
    };
private final static Object[][] DECODER_EBX1 =
    {
        { "Validity bitmap for SECS.ATTRIBUTES[63-32]" ,  31 , 0 } 
    };
private final static Object[][] DECODER_ECX1 =
    {
        { "Validity bitmap for SECS.ATTRIBUTES[95-64]" ,  31 , 0 } 
    };
private final static Object[][] DECODER_EDX1 =
    {
        { "Validity bitmap for SECS.ATTRIBUTES[127-96]" ,  31 , 0 } 
    };

private final static Object[][] DECODER_EAX2 =
    {
        { "Sub-leaf tag"          ,  3 ,  0 } ,
        { "Physical address bits" , 31 , 12 }
    };
private final static Object[][] DECODER_EBX2 =
    {
        { "Physical address bits" ,  19 , 0 } 
    };
private final static Object[][] DECODER_ECX2 =
    {
        { "EPC section tag"  ,  3 ,  0 } ,
        { "Size bits"        , 31 , 12 }
    };
private final static Object[][] DECODER_EDX2 =
    {
        { "Size bits" ,  19 , 0 } 
    };

// Calculate control data total size for output formatting
private final static int NX   = COMMAND_UP_1.length;
private final static int NY1  = DECODER_EAX0.length + 0;
private final static int NY2  = DECODER_EBX0.length + 0;
private final static int NY3  = DECODER_EDX0.length + 1;
private final static int NY4  = DECODER_EAX1.length + 0;
private final static int NY5  = DECODER_EBX1.length + 0;
private final static int NY6  = DECODER_ECX1.length + 0;
private final static int NY7  = DECODER_EDX1.length + 1;
private final static int NY8  = DECODER_EAX2.length + 0;
private final static int NY9  = DECODER_EBX2.length + 0;
private final static int NY10 = DECODER_ECX2.length + 0;
private final static int NY11 = DECODER_EDX2.length + 0;
private final static int NY  = NY1+NY2+NY3+NY4+NY5+NY6+NY7+NY8+NY9+NY10+NY11;

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
    int x1 = CPUID.findFunction( array, 0x00000012 );
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

    // Results of subfunction = 0
    // Get number of entries per CPUID dump, field from dump header
    int x2 = (int) ( array[0] & (((long)((long)(-1)>>>32))) );
    x2 = (x2+1)*4;  // Calculate x2 = limit
    // Parameters from CPUID dump, EAX register
    int p=0;  // pointer for sequentally store strings in the table
    int y = (int) (array[x1+2] & (((long)((long)(-1)>>>32))) );      // y = EAX
    CPUID.decodeBitmap ( "EAX" , DECODER_EAX0 , y , result , p );
    // Parameters from CPUID dump, EBX register
    p = NY1;
    y = (int) ( array[x1+2] >>> 32 );                                // y = EBX
    CPUID.decodeBitfields ( "EBX" , DECODER_EBX0 , y , result , p );
    // Parameters from CPUID dump, EDX register
    p = NY1+NY2;
    y = (int) ( array[x1+3] >>> 32 );                                // y = EDX
    CPUID.decodeBitfields ( "EDX" , DECODER_EDX0 , y , result , p );
    
    // Results of subfunction = 1
    // Parameters from CPUID dump, EAX register    
    p = NY1+NY2+NY3;
    x1 = x1 + 4;
    // Return if dump size limit
    if (x1>=x2) { return trimLines( result, p-1 ); }
    y = (int) ( array[x1] >>> 32 );
    // End of function entries
    if (y != 0x00000012 ) { return trimLines( result, p-1 ); }
    y = (int) (array[x1+2] & (((long)((long)(-1)>>>32))) );          // y = EAX
    CPUID.decodeBitfields ( "EAX" , DECODER_EAX1 , y , result , p );
    // Parameters from CPUID dump, EBX register    
    p = NY1+NY2+NY3+NY4;
    y = (int) ( array[x1+2] >>> 32 );                                // y = EBX
    CPUID.decodeBitfields ( "EBX" , DECODER_EBX1 , y , result , p );
    // Parameters from CPUID dump, ECX register    
    p = NY1+NY2+NY3+NY4+NY5;
    y = (int) ( array[x1+3] & (((long)((long)(-1)>>>32))) );         // y = ECX
    CPUID.decodeBitfields ( "ECX" , DECODER_ECX1 , y , result , p );
    // Parameters from CPUID dump, EDX register
    p = NY1+NY2+NY3+NY4+NY5+NY6;
    y = (int) ( array[x1+3] >>> 32 );                                // y = EDX
    CPUID.decodeBitfields ( "EDX" , DECODER_EDX1 , y , result , p );

    // Results of subfunctions > 1
    // Parameters from CPUID dump, EAX register
    p = NY1+NY2+NY3+NY4+NY5+NY6+NY7;
    x1 = x1 + 4;
    // Return if dump size limit
    if (x1>=x2) { return trimLines( result, p-1 ); }
    y = (int) ( array[x1] >>> 32 );
    // End of function entries
    if (y != 0x00000012 ) { return trimLines( result, p-1 ); }
    y = (int) (array[x1+2] & (((long)((long)(-1)>>>32))) );          // y = EAX
    CPUID.decodeBitfields ( "EAX" , DECODER_EAX2 , y , result , p );
    // Parameters from CPUID dump, EBX register
    p = NY1+NY2+NY3+NY4+NY5+NY6+NY7+NY8;
    y = (int) ( array[x1+2] >>> 32 );                                // y = EBX
    CPUID.decodeBitfields ( "EBX" , DECODER_EBX2 , y , result , p );
    // Parameters from CPUID dump, ECX register
    p = NY1+NY2+NY3+NY4+NY5+NY6+NY7+NY8+NY9;
    y = (int) ( array[x1+3] & (((long)((long)(-1)>>>32))) );         // y = ECX
    CPUID.decodeBitfields ( "ECX" , DECODER_ECX2 , y , result , p );
    // Parameters from CPUID dump, EDX register
    p = NY1+NY2+NY3+NY4+NY5+NY6+NY7+NY8+NY9+NY10;
    y = (int) ( array[x1+3] >>> 32 );                                // y = EDX
    CPUID.decodeBitfields ( "EDX" , DECODER_EDX2 , y , result , p );
    
    // Result is ready, all strings filled
    p = NY;
    return trimLines( result, p );
    }

// Helper method, trim extra strings (move this to static helper class?)
// INPUT:   s1 = array of strings
//          p = current write string index 
// OUTPUT:  trimmed array
private String[][] trimLines(String[][] s1, int p)
    {
    String[][] s2 = new String[p][NX];
    // Cycle for pre-blank text table
    for (int i=0; i<p; i++)  // Cycle for rows, copy valid part of table 
        {   // Cycle for columns
        System.arraycopy( s1[i] , 0 , s2[i] , 0 , NX );
        }
    return s2;
    }

}