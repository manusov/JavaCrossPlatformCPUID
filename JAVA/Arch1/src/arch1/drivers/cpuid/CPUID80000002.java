//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID extended function 80000002h declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;
import arch1.kernel.IOPB;

public class CPUID80000002 extends CommandAdapter
{
private static final String 
        F_NAME = "Processor name string [1 of 3]";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }
    
private final static int NX = 2;
private final static int NY = 1;
    
    @Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }
    result[0][0] = "CPU name string";
    result[0][1] = "n/a";
    
    int x1 = CPUID.findFunction( array, 0x80000002 );
    int x2 = CPUID.findFunction( array, 0x80000003 );
    int x3 = CPUID.findFunction( array, 0x80000004 );
    if ( (x1<0)||(x2<0)||(x3<0) ) { return result; }
    
    String s1 = IOPB.receiveString( array, x1+2, 2 );
    String s2 = IOPB.receiveString( array, x2+2, 2 );
    String s3 = IOPB.receiveString( array, x3+2, 2 );
    result[0][1] = (s1+s2+s3).trim();
    
    return result;
    }
    
}
