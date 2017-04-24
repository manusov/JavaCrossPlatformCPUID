//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID standard function 00000003h declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;


public class CPUID00000003 extends CommandAdapter
{
private static final String 
        F_NAME = "Processor Serial Number";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }

private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Value, hex" };
private final static int NX = COMMAND_UP_1.length;
private final static int NY = 1;

@Override public String[] getCommandUp1( long[] array )
    { return COMMAND_UP_1; }

@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]="n/a"; } }

    int x1 = CPUID.findFunction( array, 0x00000001 );
    int x2 = CPUID.findFunction( array, 0x00000003 );
    if ((x1<0)|(x2<0)) { return result; }

    long y1 = array[x1+2];                            // Fn1.EAX = PSN, High 
    long y2 = (array[x1+3] >> 32) & ( 1 << 18 );      // Check PSN feature
    long y3 = array[x2+3];                            // Fn2.EDX = Middle 
    if (y2==0) { return result; }                     // Fn2.ECX = Low

    int[] z = new int[6];
    z[0] = (int)( ( y1 >> 16 ) & 0xFFFF );
    z[1] = (int)( y1 & 0xFFFF );
    z[2] = (int)( ( y3 >> 48 ) & 0xFFFF );
    z[3] = (int)( ( y3 >> 32 ) & 0xFFFF );
    z[4] = (int)( ( y3 >> 16 ) & 0xFFFF );
    z[5] = (int)( y3 & 0xFFFF );
    
    result[0][0] = "Processor Serial Number (PSN)";
    result[0][1] = "";
    for (int i=0; i<z.length; i++)
        {
        result[0][1] = result[0][1] + String.format( "%04X", z[i] );
        if ( i<z.length-1 ) { result[0][1] = result[0][1] + "-"; }
        }

    return result;
    }

}
