//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID extended function 80000019h declared as CPR.COMMAND.

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID80000019 extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME =
    "1GB paging TLB parameters";
    
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
        { "L1 data TLB for 1GB pages associativity"            , 31 , 28 } ,
        { "L1 data TLB for 1GB pages number of entries"        , 27 , 16 } ,
        { "L1 instruction TLB for 1GB pages associativity"     , 15 , 12 } , 
        { "L1 instruction TLB for 1GB pages number of entries" , 11 ,  0 }
    };
private final static Object[][] DECODER_EBX =
    {
        { "L2 data TLB for 1GB pages associativity"              , 31 , 28 } ,
        { "L2 data TLB for 1GB pages number of entries"          , 27 , 16 } ,
        { "L2 instruction TLB for 1GB pages associativity"       , 15 , 12 } ,
        { "L2 instruction TLB for 1GB pages number of entries"   , 11 ,  0 }
    };

// Calculate control data total size for output formatting
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX.length + 1;
private final static int NY2 = DECODER_EBX.length + 0;
private final static int NY  = NY1+NY2;

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
    int x = CPUID.findFunction( array, 0x80000019 );
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
    writeAssociativity( z[0] , p , result );
    result[p+1][4] = "" + z[1];
    writeAssociativity( z[2] , p+2 , result );
    result[p+3][4] = "" + z[3];
    // Parameters from CPUID dump, EBX register
    p=NY1;
    y = (int) ( array[x+2] >>> 32 );                                 // y = EBX
    z = CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    writeAssociativity( z[0] , p , result );
    result[p+1][4] = "" + z[1];
    writeAssociativity( z[2] , p+2 , result );
    result[p+3][4] = "" + z[3];
    // Result is ready, all strings filled
    return result;
    }

// Helper method, write cache associativity, string=F(number)
// INPUT:   a = associativity encoded value
//          p = index of string in the strings array
//          s = destination strings array
// OUTPUT:  none (void)
private void writeAssociativity ( int a , int p , String[][] s )
    {
    switch(a)
        {
        case 0:  { s[p][4] = "Disabled"; break; }
        case 1:  { s[p][4] = "1 way, direct mapped"; break; }
        case 2:  { s[p][4] = "2 ways"; break; }
        case 3:  { s[p][4] = "Reserved"; break; }
        case 4:  { s[p][4] = "4 ways"; break; }
        case 5:  { s[p][4] = "Reserved"; break; }
        case 6:  { s[p][4] = "8 ways"; break; }
        case 7:  { s[p][4] = "Reserved"; break; }
        case 8:  { s[p][4] = "16 ways"; break; }
        case 9:  { s[p][4] = "Reserved"; break; }
        case 10: { s[p][4] = "32 ways"; break; }
        case 11: { s[p][4] = "48 ways"; break; }
        case 12: { s[p][4] = "64 ways"; break; }
        case 13: { s[p][4] = "96 ways"; break; }
        case 14: { s[p][4] = "128 ways"; break; }
        case 15: { s[p][4] = "Fully associative"; break; }
        default: { s[p][4] = "Invalid"; }
        }
    }
    
}
