/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
CPUID driver component:
CPUID extended function 80000002h declared as CPR.COMMAND.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;
import cpuid.kernel.IOPB;

public class CPUID80000002 extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME =
    "Processor name string [1 of 3]";
    
// Control data total size for output formatting    
private final static int NX = 2;
private final static int NY = 1;

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
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }
    result[0][0] = "CPU name string";
    result[0][1] = "n/a";
    // Scan binary dump, find entries for 3 functions
    int x1 = CPUID.findFunction( array, 0x80000002 );  // CPU name, part 1 of 3
    int x2 = CPUID.findFunction( array, 0x80000003 );  // CPU name, part 2 of 3
    int x3 = CPUID.findFunction( array, 0x80000004 );  // CPU name, part 3 of 3
    // If one of sub-strings not available, return
    if ( (x1<0)||(x2<0)||(x3<0) ) { return result; }
    // Get fragments from CPUID binary dump and build CPU name string
    String s1 = IOPB.receiveString( array, x1+2, 2 );
    String s2 = IOPB.receiveString( array, x2+2, 2 );
    String s3 = IOPB.receiveString( array, x3+2, 2 );
    result[0][1] = (s1+s2+s3).trim();
    return result;
    }
    
}
