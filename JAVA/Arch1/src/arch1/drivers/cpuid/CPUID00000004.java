//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID standard function 00000004h declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID00000004 extends CommandAdapter
{
private static final String 
        F_NAME = "Deterministic cache parameters";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }
    
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "L1 code" , "L1 data" , "L2 unified" , "L3 unified" };
private final static String[] CACHE_PARMS =
    {
    "Cache size (KB)",
    "System coherency line size (bytes)",
    "Physical line partitions",
    "Ways of associativity",
    "Number of sets",
    "Max. logical CPUs per this cache",
    "Max. cores per physical package",
    "Self initializing cache level",
    "Fully associative cache",
    "WBINVD/INVD lower caches levels",
    "Inclusive of lower cache levels",
    "Direct mapped (0) or complex(1)"
    };
private final static int NX = COMMAND_UP_1.length;
private final static int NY = CACHE_PARMS.length;

@Override public String[] getCommandUp1( long[] array )
    { return COMMAND_UP_1; }

@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }
    
    int count = (int) ( ( array[0] ) & (long) ( (long)(-1) >>> 32) );
    int pointer = CPUID.findFunction( array, 0x00000004 );
    if ( pointer < 0 ) { return result; }
    
    for ( int i=0; i<NY; i++ ) { result[i][0] = CACHE_PARMS[i]; }
    
    int function, rEAX, rEBX, rECX, rEDX, j, y1, y2, y3, y4;
    while ( count > 0 )
    {
    function = (int) ( array[ pointer ] >>> 32 );
    if ( function != 0x00000004 ) { break; }
    rEAX = (int) ( ( array[ pointer+2 ] ) & (long) ( (long)(-1) >>> 32) );
    rEBX = (int) ( array[ pointer+2 ] >>> 32 );
    rECX = (int) ( ( array[ pointer+3 ] ) & (long) ( (long)(-1) >>> 32) );
    rEDX = (int) ( array[ pointer+3 ] >>> 32 );
    j=0;
    if ( ( rEAX & 0xFF ) == 0x22 ) j=1;    // L1 code
    if ( ( rEAX & 0xFF ) == 0x21 ) j=2;    // L1 data
    if ( ( rEAX & 0xFF ) == 0x43 ) j=3;    // L2 unified
    if ( ( rEAX & 0xFF ) == 0x63 ) j=4;    // L3 unified
    if ( j != 0 )
        {
        y1 = rECX + 1;
        y2 = ( rEBX & 0xFFF ) + 1;
        y3 = ( (rEBX >> 12) & 0x3FF) + 1;
        y4 = ( (rEBX >> 22) & 0x3FF) + 1;
        result[0][j]  = String.format( "%d" , y1*y2*y3*y4 / 1024 );
        result[1][j]  = String.format( "%d" , y2 );
        result[2][j]  = String.format( "%d" , y3 );
        result[3][j]  = String.format( "%d" , y4 );
        result[4][j]  = String.format( "%d" , y1 );
        result[5][j]  = String.format( "%d" , ((rEAX >> 14) & 0xFFF) + 1);
        result[6][j]  = String.format( "%d" , ((rEAX >> 26) & 0x3F) + 1 );
        result[7][j]  = String.format( "%d" , (rEAX & 0x0100) >> 8);
        result[8][j]  = String.format( "%d" , (rEAX & 0x0200) >> 9);
        result[9][j]  = String.format( "%d" , rEDX & 0x0001);
        result[10][j] = String.format( "%d" , (rEDX & 0x0002) >> 1);
        result[11][j] = String.format( "%d" , (rEDX & 0x0004) >> 2);
        }
    pointer += 4;  // Units = 8 bytes (long), 4*8=32 bytes per entry    
    count--;       // Units = entries, 32-bytes per entry
    }
    return result;
    }
}
