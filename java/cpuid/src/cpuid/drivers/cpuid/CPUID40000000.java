//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID virtual function 40000000h declared as CPR.COMMAND.

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;
import cpuid.kernel.IOPB;

public class CPUID40000000 extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME =
    "Virtual CPUID vendor string";

// Return CPUID this function full name
// INPUT:   Reserved array
// OUTPUT:  String, CPUID function full name
@Override public String getCommandLongName(long[] dummy ) 
    { return F_NAME; }

// Build and return CPUID this function detail information table
// INPUT:   Binary array = CPUID dump data
// OUTPUT:  Array of strings = CPUID this function detail information table
@Override public String[][] getCommandText1( long[] array )
    {
    String[][] s = new String[1][2];
    s[0][0] = "Virtual CPU vendor string";
    s[0][1] = "n/a";
    // Scan binary dump, find entry for this function
    int x = CPUID.findFunction( array, 0x40000000 );
    if (x>0)
        {
        // Get bytes, offset = entry + 2*8=16 bytes, length = 2*8=16 bytes
        byte[] z1 = IOPB.receiveBytes(array, x+2, 2);
        // Built vendor string, StringBuffer use less memory
        StringBuffer z2 = new StringBuffer("");
        for (int i=4; i<8; i++)      // copy chars from EBX position   
            { if(z1[i]==0) { break; } z2 = z2.append((char)z1[i]); }
        for (int i=12; i<16; i++)    // copy chars from EDX position 
            { if(z1[i]==0) { break; } z2 = z2.append((char)z1[i]); }
        for (int i=8; i<12; i++)     // copy chars from ECX position
            { if(z1[i]==0) { break; } z2 = z2.append((char)z1[i]); }
        String z3 = z2.toString();
        if (z3.length()>0) { s[0][1] = z3; }
        }
    return s;
    }
}
    
