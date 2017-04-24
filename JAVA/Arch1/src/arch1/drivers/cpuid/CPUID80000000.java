//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID extended function 80000000h declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;
import arch1.kernel.IOPB;

public class CPUID80000000 extends CommandAdapter
{
private static final String 
        F_NAME = "Maximum extended CPUID function number";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }
    
@Override public String[][] getCommandText1( long[] array )
    {
    String[][] s = new String[2][2];
    s[0][0] = "Maximum extended CPUID level";
    s[1][0] = "CPU extended vendor string";
    s[0][1] = "n/a";
    s[1][1] = "n/a";
    int x = CPUID.findFunction( array, 0x80000000 );
    if (x>0)
        {
        int y = (int)( array[x+2] & ((long)((long)(-1)>>>32)) );
        s[0][1] = String.format("%08Xh", y);
        byte[] z1 = IOPB.receiveBytes(array, x+2, 2);
        // Built vendor string, StringBuffer use less memory
        StringBuffer z2 = new StringBuffer("");
        for (int i=4; i<8; i++)   
            { if(z1[i]==0) { break; } z2 = z2.append((char)z1[i]); }
        for (int i=12; i<16; i++) 
            { if(z1[i]==0) { break; } z2 = z2.append((char)z1[i]); }
        for (int i=8; i<12; i++)  
            { if(z1[i]==0) { break; } z2 = z2.append((char)z1[i]); }
         String z3 = z2.toString();
        if (z3.length()>0) { s[1][1] = z3; }
        }
    return s;
    }
}
