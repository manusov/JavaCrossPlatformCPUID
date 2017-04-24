//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID standard function 00000006h declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID00000006 extends CommandAdapter
{
private static final String 
        F_NAME = "Thermal and Power Management features";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }

private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };
private final static String[][] DECODER_EAX =
    { 
        { "DTS"   , "Digital temperature sensor" } ,
        { "TB"    , "Intel Turbo Boost technology" } ,
        { "ARAT"  , "APIC timer always running" } ,
        { "x"     , "Reserved" } ,
        { "PLN"   , "Power limit notification controls" } ,
        { "ECMD"  , "Clock modulation duty cycle extension" } ,
        { "PTM"   , "Package thermal management" } ,
        { "HWP-B" , "Hardware ctrl. perf. states base registers" } ,
        { "HWP-N" , "HWP notification" } ,
        { "HWP-W" , "HWP activity window" } ,
        { "HWP-E" , "HWP energy performance preference" } ,
        { "HWP-P" , "HWP package level request" } ,
        { "x"     , "Reserved" } ,
        { "HDC"   , "Hardware duty cycling base register" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } 
    };
private final static Object[][] DECODER_EBX =
    {
        { "Number of interrupt thresholds in digital thermal sensor" , 3 , 0 }
    }; 
private final static Object[][] DECODER_ECX =
    {
        { "Hardware coordination feedback capability"             , 0 , 0 } ,
        { "Processor supports performance-energy bias preference" , 3 , 3 }
    };
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX.length + 1;
private final static int NY2 = DECODER_EBX.length + 1;
private final static int NY3 = DECODER_ECX.length + 1;
private final static int NY  = NY1+NY2+NY3;

@Override public String[] getCommandUp1( long[] array )
    { return COMMAND_UP_1; }

@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }
    int x = CPUID.findFunction( array, 0x00000006 );
    if (x<0) { return result; }
    
    int y=0;
    int p=0;
    int[] z;
    y = (int) ( array[x+2] & (((long)((long)(-1)>>>32))) );              // EAX
    CPUID.decodeBitmap ( "EAX" , DECODER_EAX , y , result , p );
    
    p = NY1;
    y = (int) ( array[x+2] >>> 32 );                                     // EBX
    z = CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    result[p][4] = String.format( "%d", z[0] );

    p = NY1+NY2;
    y = (int) ( array[x+3] & (((long)((long)(-1)>>>32))) );              // ECX
    z = CPUID.decodeBitfields ( "ECX" , DECODER_ECX , y , result , p );
    for ( int i=0; i<z.length; i++ ) 
        { result[p+i][4] = String.format("%d", z[i]); }

    return result;
    }
}