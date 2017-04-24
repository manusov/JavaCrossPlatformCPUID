//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID standard function 0000000Ah declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID0000000A extends CommandAdapter
{
private static final String 
        F_NAME = "Architectural Performance Monitoring features";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }
    
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };
private final static Object[][] DECODER_EAX =
    {
        { "Version ID of architectural performance monitoring" , 7, 0 } ,
        { "Number of GP PMC per logical CPU" , 15 , 8 } ,
        { "Bit width of GP PMC" , 23 , 16 } ,
        { "Length of bit vector to enumerate architectural PM events" , 31 , 24 }
    };
private final static Object[][] DECODER_EBX =
    {
        { "Core cycle event not available flag", 0, 0 } ,
        { "Instruction retired event not available flag", 1, 1 } ,
        { "Reference cycles event not available flag", 2, 2 } ,
        { "Last level cache reference event not available flag", 3, 3 } ,
        { "Last level cache misses event not available flag", 4, 4 } ,
        { "Branch instruction retired event not available flag", 5, 5 } ,
        { "Branch mispredict retired event not available flag", 6, 6 }
    };
private final static Object[][] DECODER_EDX =
    {
        { "Number of fixed-function performance counters" , 4, 0 } ,
        { "Bit width of fixed-function performance counters" , 12, 5 }
    };

private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX.length + 1;
private final static int NY2 = DECODER_EBX.length + 1;
private final static int NY3 = DECODER_EDX.length + 1;
private final static int NY  = NY1+NY2+NY3;

@Override public String[] getCommandUp1( long[] array )
    { return COMMAND_UP_1; }

@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }
    int x = CPUID.findFunction( array, 0x0000000A );
    if (x<0) { return result; }
    
    int y=0;
    int p=0;
    int[] z;
    String s;
    y = (int) ( array[x+2] & (((long)((long)(-1)>>>32))) );              // EAX
    z = CPUID.decodeBitfields ( "EAX" , DECODER_EAX , y , result , p );
    for( int i=0; i<z.length; i++ )
        { result[p+i][4] = String.format( "%d", z[i] ); }
    
    p = NY1;
    y = (int) ( array[x+2] >>> 32 );                                     // EBX
    z = CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    //for( int i=0; i<z.length; i++ )
    //    {
    //    if ( z[i] == 0) { s = "available"; }
    //    else { s = "not available"; }
    //    result[p+i][4] = s;
    //    }
    
    p = NY1+NY2;
    y = (int) ( array[x+3] >>> 32 );                                     // EDX
    z = CPUID.decodeBitfields ( "EDX" , DECODER_EDX , y , result , p );
    for( int i=0; i<z.length; i++ )
        { result[p+i][4] = String.format( "%d", z[i] ); }
    
    return result;
    }


}