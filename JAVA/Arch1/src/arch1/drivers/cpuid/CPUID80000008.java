//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID extended function 80000008h declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID80000008 extends CommandAdapter
{
private static final String 
        F_NAME = "Address size and physical core information";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }
    
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };
private final static Object[][] DECODER_EAX =
    {
        { "Physical address size"       ,  7 , 0 } ,
        { "Linear address size"         , 15 , 8 } ,
        { "Guest physical address size" , 23 , 16 }
    };
private final static Object[][] DECODER_ECX =
    {
        { "Number of physical cores - 1" ,  7 ,  0 } ,
        { "APIC ID size"                 , 15 , 12 } ,
    };

private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX.length + 1;
private final static int NY2 = DECODER_ECX.length + 1;
private final static int NY  = NY1+NY2;

@Override public String[] getCommandUp1( long[] array )
    { return COMMAND_UP_1; }

@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }
    int x = CPUID.findFunction( array, 0x80000008 );
    if (x<0) { return result; }
    
    
    int y=0;
    int[] z;
    int p=0;
    y = (int) ( array[x+2] & (((long)((long)(-1)>>>32))) );              // EAX
    z = CPUID.decodeBitfields ( "EAX" , DECODER_EAX , y , result , p );
    writeSize( z, 0, result, p );
    writeSize( z, 1, result, p+1 );
    writeSize( z, 2, result, p+2 );
    
    p=NY1;
    y = (int) ( array[x+3] & (((long)((long)(-1)>>>32))) );              // ECX
    z = CPUID.decodeBitfields ( "ECX" , DECODER_ECX , y , result , p );
    if ( z[0]>0 ) { result[p][4] = "" + (z[0]+1); }
    else          { result[p][4] = "n/a"; }
    if ( z[1]>0 ) { result[p+1][4] = z[1] + "-bit"; }
    else          { result[p+1][4] = "n/a"; }
    return result;
    }

// Helper methods

private static void writeSize( int[] z , int i, String[][] result, int p )
    {
    String s="";
    int z1 = z[i];
    int z2 = 1 << (z1-30);
    int z3 = 1 << (z1-40);
    if (z1==0) s = "n/a";
    if ((z1>0)&&(z1<30)) { s = "Invalid"; }
    if ((z1>=30)&&(z1<40)) { s = z1 + "-bit, " + z2 + " GB space"; }
    if (z1>=40) { s = z1 + "-bit, " + z3 + " TB space"; }
    result[p][4] = s;
    }

}
