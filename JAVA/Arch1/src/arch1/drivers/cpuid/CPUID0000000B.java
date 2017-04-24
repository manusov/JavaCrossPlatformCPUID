//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID standard function 0000000Bh declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID0000000B extends CommandAdapter
{
private static final String 
        F_NAME = "Extended multiprocessing topology information";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }

private final static String[] COMMAND_UP_1 =
    { "Parameter" , "SMT" , "Core" };
private final static String[] SMT_PARMS = 
    {
    "Number of logical processor at this level type" ,
    "Bits shift right on x2APIC ID to get next level ID" ,
    "Level number" ,
    "Level type" ,
    "Current x2APIC ID"
    };
private final static int NX = COMMAND_UP_1.length;
private final static int NY = SMT_PARMS.length;

@Override public String[] getCommandUp1( long[] array )
    { return COMMAND_UP_1; }

@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }
    
    int count = (int) ( ( array[0] ) & (long) ( (long)(-1) >>> 32) );
    int pointer = CPUID.findFunction( array, 0x0000000B );
    if ( pointer < 0 ) { return result; }
    
    for ( int i=0; i<NY; i++ ) { result[i][0] = SMT_PARMS[i]; }
   
    int function, rEAX, rEBX, rECX, rEDX, j, y1, y2, y3, y4;
    while ( count > 0 )
        {
        function = (int) ( array[ pointer ] >>> 32 );
        if ( function != 0x0000000B ) { break; }
        rEAX = (int) ( ( array[ pointer+2 ] ) & (long) ( (long)(-1) >>> 32) );
        rEBX = (int) ( array[ pointer+2 ] >>> 32 );
        rECX = (int) ( ( array[ pointer+3 ] ) & (long) ( (long)(-1) >>> 32) );
        rEDX = (int) ( array[ pointer+3 ] >>> 32 );
        j = ( rECX >> 8 ) & 0xFF;
        if ((j==1)|(j==2))
        {
        result[0][j] = String.format( "%d"   , rEBX & 0xFFFF );
        result[1][j] = String.format( "%d"   , rEAX & 0x1F );
        result[2][j] = String.format( "%d"   , rECX & 0xFF );
        result[3][j] = String.format( "%d"   , (rECX >> 8) & 0xFF );
        result[4][j] = String.format( "%08Xh" , rEDX );
        }
        pointer += 4;  // Units = 8 bytes (long), 4*8=32 bytes per entry    
        count--;       // Units = entries, 32-bytes per entry
        }
    return result;
    }
}