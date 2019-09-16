/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
CPUID driver component:
CPUID standard function 00000010h declared as CPR.COMMAND.
TODO: required add sub-leafs 2 and 3 for L2 and DRAM.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID00000010 extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME =
    "Platform Quality of Service enforcement";
    
// Parameters table up string
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };

// Return if function not found
private final static String[][] NO_RESULT =
    {
        { "n/a" , "" , "" , "" , "" }
    };

// Control tables for results decoding, subfunction 0
private final static String[][] DECODER_EBX0 =
    {
        { "x"      , "Reserved" } , 
        { "L3 QoS" , "L3 cache allocation technology, QoS enforcement" } ,
        { "L2 QoS" , "L2 cache allocation technology, QoS enforcement" } ,
        { "M QoS"  , "Memory bandwidth allocation, QoS enforcement" } ,
    };

// subfunction 1
private final static Object[][] DECODER_EAX1 =
    {
        { "L3 Cache QoS.  Length of the capacity bit mask for ResID" , 4 , 0 } , 
    };
private final static Object[][] DECODER_EBX1 =
    {
        { "Bit-granular map of isolation/contention" , 31 , 0 } , 
    };
private final static String[][] DECODER_ECX1 =
    {
        { "x"      , "Reserved" } , 
        { "UCOS"   , "Update of COS should be infrequent" } ,
        { "CDP"    , "Code and Data prioritization technology" }
    };
private final static Object[][] DECODER_EDX1 =
    {
        { "Highest COS number supported for this ResID" , 15 , 0 } 
    };

// subfunction 2, note ECX reserved for this subfunction
private final static Object[][] DECODER_EAX2 =
    {
        { "L2 Cache QoS.  Length of the capacity bit mask for ResID" , 4 , 0 } , 
    };
private final static Object[][] DECODER_EBX2 = DECODER_EBX1;
private final static Object[][] DECODER_EDX2 = DECODER_EDX1;

// subfunction 3, note EBX reserved for this subfunction
private final static Object[][] DECODER_EAX3 =
    {
        { "DRAM QoS.  Maximum MBA throttling value for this ResID" , 11 , 0 } 
    };
private final static String[][] DECODER_ECX3 =
    {
        { "x"      , "Reserved" } , 
        { "x"      , "Reserved" } , 
        { "UCOS"   , "Update of COS should be infrequent" }
    };
private final static Object[][] DECODER_EDX3 = DECODER_EDX1;

// Calculate control data total size for output formatting
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EBX0.length + 1;

private final static int NY2 = DECODER_EAX1.length + 0;
private final static int NY3 = DECODER_EBX1.length + 0;
private final static int NY4 = DECODER_ECX1.length + 0;
private final static int NY5 = DECODER_EDX1.length + 1;

private final static int NY6 = DECODER_EAX2.length + 0;
private final static int NY7 = DECODER_EBX2.length + 0;
private final static int NY8 = DECODER_EDX2.length + 1;

private final static int NY9  = DECODER_EAX3.length + 0;
private final static int NY10 = DECODER_ECX3.length + 0;
private final static int NY11 = DECODER_EDX3.length + 0;

private final static int NY  = NY1 + NY2 + NY3 + NY4 + NY5 +
                               NY6 + NY7 + NY8 + NY9 + NY10 + NY11;

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
    boolean nextValid;
    int rEAX, rEBX, rECX, rEDX;
    
    // Scan binary dump, find entry for this function
    int x1 = CPUID.findFunction( array, 0x00000010 );
    // Return "n/a" if this function entry not found
    if (x1<0) { return NO_RESULT; }                            // x1 = pointer
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
    rEBX = (int) ( array[x1+2] >>> 32 );                            // y = EBX
    CPUID.decodeBitmap ( "EBX" , DECODER_EBX0 , rEBX , result , p );
    
    // prepare for subfunction 1
    x1 = x1 + 4;
    if ( ( x1 < x2 ) & ((int) ( array[x1] >>> 32 ) == 0x00000010 ) )
    {
    rEAX = (int) (array[x1+2] & (((long)((long)(-1)>>>32))) );
    rEBX = (int) ( array[x1+2] >>> 32 );
    rECX = (int) ( array[x1+3] & (((long)((long)(-1)>>>32))) );
    rEDX = (int) ( array[x1+3] >>> 32 );
    }
    else
    {
    rEAX = rEBX = rECX = rEDX = 0;        
    }
    
    // Start second group of sub-leafs: parameters
    
    // subfunction 1
    // Parameters from CPUID dump, EAX register
    p = p + NY1;
    CPUID.decodeBitfields ( "EAX" , DECODER_EAX1 , rEAX , result , p );
    // Parameters from CPUID dump, EBX register
    p = p + NY2;
    CPUID.decodeBitfields ( "EBX" , DECODER_EBX1 , rEBX , result , p );
    // Parameters from CPUID dump, ECX register
    p = p + NY3;
    CPUID.decodeBitmap ( "ECX" , DECODER_ECX1 , rECX , result , p );
    // Parameters from CPUID dump, EDX register
    p = p + NY4;
    CPUID.decodeBitfields ( "EDX" , DECODER_EDX1 , rEDX , result , p );
    
    // prepare for subfunction 2
    x1 = x1 + 4;
    if ( ( x1 < x2 ) & ((int) ( array[x1] >>> 32 ) == 0x00000010 ) )
    {
    rEAX = (int) (array[x1+2] & (((long)((long)(-1)>>>32))) );
    rEBX = (int) ( array[x1+2] >>> 32 );
    rEDX = (int) ( array[x1+3] >>> 32 );
    }
    else
    {
    rEAX = rEBX = rEDX = 0;        
    }

    // subfunction 2
    // Parameters from CPUID dump, EAX register
    p = p + NY5;
    CPUID.decodeBitfields ( "EAX" , DECODER_EAX2 , rEAX , result , p );
    // Parameters from CPUID dump, EBX register
    p = p + NY6;
    CPUID.decodeBitfields ( "EBX" , DECODER_EBX2 , rEBX , result , p );
    // Parameters from CPUID dump, EDX register
    p = p + NY7;
    CPUID.decodeBitfields ( "EDX" , DECODER_EDX2 , rEDX , result , p );

    // prepare for subfunction 3
    x1 = x1 + 4;
    if ( ( x1 < x2 ) & ((int) ( array[x1] >>> 32 ) == 0x00000010 ) )
    {
    rEAX = (int) (array[x1+2] & (((long)((long)(-1)>>>32))) );
    rECX = (int) ( array[x1+3] & (((long)((long)(-1)>>>32))) );
    rEDX = (int) ( array[x1+3] >>> 32 );
    }
    else
    {
    rEAX = rECX = rEDX = 0;        
    }
    
    // subfunction 3
    // Parameters from CPUID dump, EAX register
    p = p + NY8;
    CPUID.decodeBitfields ( "EAX" , DECODER_EAX3 , rEAX , result , p );
    // Parameters from CPUID dump, ECX register
    p = p + NY9;
    CPUID.decodeBitmap ( "ECX" , DECODER_ECX3 , rECX , result , p );
    // Parameters from CPUID dump, EDX register
    p = p + NY10;
    CPUID.decodeBitfields ( "EDX" , DECODER_EDX3 , rEDX , result , p );
    
    // Result is ready, all strings filled
    return result;
    }

}

