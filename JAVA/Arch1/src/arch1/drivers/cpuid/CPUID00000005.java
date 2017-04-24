//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID standard function 00000005h declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID00000005 extends CommandAdapter
{
private static final String 
        F_NAME = "MONITOR/MWAIT features";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }
    
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };
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
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX.length;
private final static int NY2 = DECODER_EBX.length;
private final static int NY3 = DECODER_ECX.length;
private final static int NY4 = DECODER_EDX.length;
private final static int NY  = NY1+NY2+NY3+NY4;

@Override public String[] getCommandUp1( long[] array )
    { return COMMAND_UP_1; }

@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }
    int x = CPUID.findFunction( array, 0x00000005 );
    if (x<0) { return result; }
    
    int y=0;
    int p=0;
    int[] z;
    y = (int) ( array[x+2] & (((long)((long)(-1)>>>32))) );              // EAX
    z = CPUID.decodeBitfields ( "EAX" , DECODER_EAX , y , result , p );
    result[p][4] = String.format( "%d Bytes", z[0] );

    p = NY1;
    y = (int) ( array[x+2] >>> 32 );                                     // EBX
    z = CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    result[p][4] = String.format( "%d Bytes", z[0] );

    p = NY1+NY2;
    y = (int) ( array[x+3] & (((long)((long)(-1)>>>32))) );              // ECX
    z = CPUID.decodeBitfields ( "ECX" , DECODER_ECX , y , result , p );
    for ( int i=0; i<z.length; i++ ) 
        { result[p+i][4] = String.format("%d", z[i]); }
    
    p = NY1+NY2+NY3;
    y = (int) ( array[x+3] >>> 32 );                                     // EDX
    z = CPUID.decodeBitfields ( "EDX" , DECODER_EDX , y , result , p );
    for ( int i=0; i<z.length; i++ ) 
        { result[p+i][4] = String.format("%d", z[i]); }
    
    return result;
    }

}
