//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID extended function 80000006h declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID80000006 extends CommandAdapter
{
private static final String 
        F_NAME = "L2/L3 cache and L2 TLB information";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }
    
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };
private final static Object[][] DECODER_EAX =
    {
        { "L2 data TLB for 2/4MB pages associativity"            , 31 , 28 } ,
        { "L2 data TLB for 2/4MB pages number of entries"        , 27 , 16 } ,
        { "L2 instruction TLB for 2/4MB pages associativity"     , 15 , 12 } , 
        { "L2 instruction TLB for 2/4MB pages number of entries" , 11 ,  0 }
    };
private final static Object[][] DECODER_EBX =
    {
        { "L2 data TLB for 4KB pages associativity"              , 31 , 28 } ,
        { "L2 data TLB for 4KB pages number of entries"          , 27 , 16 } ,
        { "L2 instruction TLB for 4KB pages associativity"       , 15 , 12 } ,
        { "L2 instruction TLB for 4KB pages number of entries"   , 11 ,  0 }
    };
private final static Object[][] DECODER_ECX =
    {
        { "L2 unified cache size"                                , 31 , 16 } ,
        { "L1 unified cache associativity"                       , 15 , 12 } ,
        { "L2 unified cache lines per tag"                       , 11 ,  8 } ,
        { "L2 unified cache line size"                           ,  7 ,  0 }
    };
private final static Object[][] DECODER_EDX =
    {
        { "L3 unified cache size"                                , 31 , 18 } ,
        { "L3 unified cache associativity"                       , 15 , 12 } ,
        { "L3 unified cache lines per tag"                       , 11 ,  8 } ,
        { "L3 unified cache line size"                           ,  7 ,  0 }
    };

private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX.length + 1;
private final static int NY2 = DECODER_EBX.length + 1;
private final static int NY3 = DECODER_ECX.length + 1;
private final static int NY4 = DECODER_EDX.length + 1;
private final static int NY  = NY1+NY2+NY3+NY4;

@Override public String[] getCommandUp1( long[] array )
    { return COMMAND_UP_1; }

@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }
    int x = CPUID.findFunction( array, 0x80000006 );
    if (x<0) { return result; }
    
    int y=0;
    int[] z;
    int p=0;
    y = (int) ( array[x+2] & (((long)((long)(-1)>>>32))) );              // EAX
    z = CPUID.decodeBitfields ( "EAX" , DECODER_EAX , y , result , p );
    writeAssociativity( z[0] , p , result );
    result[p+1][4] = "" + z[1];
    writeAssociativity( z[2] , p+2 , result );
    result[p+3][4] = "" + z[3];
    
    p=NY1;
    y = (int) ( array[x+2] >>> 32 );                                     // EBX
    z = CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    writeAssociativity( z[0] , p , result );
    result[p+1][4] = "" + z[1];
    writeAssociativity( z[2] , p+2 , result );
    result[p+3][4] = "" + z[3];

    p=NY1+NY2;
    y = (int) ( array[x+3] & (((long)((long)(-1)>>>32))) );              // ECX
    z = CPUID.decodeBitfields ( "ECX" , DECODER_ECX , y , result , p );
    result[p][4] = z[0] + " KB";
    writeAssociativity( z[1] , p+1 , result );
    result[p+2][4] = "" + z[2];
    result[p+3][4] = z[3] + " Bytes";

    p=NY1+NY2+NY3;
    y = (int) ( array[x+3] >>> 32 );                                     // EDX
    z = CPUID.decodeBitfields ( "EDX" , DECODER_EDX , y , result , p );
    result[p][4] = z[0] + " KB";
    writeAssociativity( z[1] , p+1 , result );
    result[p+2][4] = "" + z[2];
    result[p+3][4] = z[3] + " Bytes";
    
    return result;
    }

// Helper methods

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
