/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 0000000Bh =
Extended multiprocessing topology information.

*/

package cpuidv3.servicecpuid;

import cpuidv3.servicecpudata.EntryCpuidSubfunction;

class Cpuid0000000B extends ReservedFunctionCpuid  implements IX2ApicId
{
Cpuid0000000B() { setFunction( 0x0000000B ); }

@Override String getLongName()
    { return "Extended multiprocessing topology information"; }

@Override String[] getParametersListUp()
    { return new String[] { "Parameter" , "SMT" , "Core" }; }

// Control tables for results decoding.
private final static String[] SMT_PARMS = 
    { "Number of logical processor at this level type" ,
      "Bits shift right on x2APIC ID to get next level ID" ,
      "Level number" ,
      "Level type" ,
      "Current x2APIC ID" };

@Override String[][] getParametersList()
    {
    String s[][] = { { "?", "?", "?" } };
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        int rowsCount = SMT_PARMS.length;
        int columnsCount = s[0].length;
        int smtCount = entries.length;
        s = new String[rowsCount][columnsCount];
        for( int i=0; i<rowsCount; i++ )
            {
            s[i][0] = SMT_PARMS[i];
            }
        for( int i=0; i<smtCount; i++ )
            {
            int j = ( entries[i].ecx >> 8 ) & 0xFF;
            // Write fields with numeric parameters.
            if ( ( j == 1 )|( j == 2 ) )  // Detect known codes.
                {
                s[0][j] = String.format( "%d" , entries[i].ebx & 0xFFFF );
                s[1][j] = String.format( "%d" , entries[i].eax & 0x1F );
                s[2][j] = String.format( "%d" , entries[i].ecx & 0xFF );
                s[3][j] = String.format
                    ( "%d" , ( entries[i].ecx >> 8 ) & 0xFF );
                s[4][j] = String.format( "%08Xh" , entries[i].edx );
                }
            }
        }
    return s;
    }

    @Override public String getX2ApicId()
    {
        final int CPU_FEATURES_FUNCTION = 0x00000001;  // CPUID function 1.
        final int X2APIC_SUPPORT_MASK   = 0x00200000;  // Bit ECX.21 = X2APIC.
        String result = "n/a";
        if ( ( entries != null )&&( entries.length > 0 ) )
        {
            result = "x2APIC not supported or disabled";
            EntryCpuidSubfunction[] e = buildEntries( CPU_FEATURES_FUNCTION );
            if ( ( e != null )&&( e.length > 0 )&&
               ( ( e[0].ecx & X2APIC_SUPPORT_MASK ) != 0 ) )
            {
                int x2ApicId = entries[0].edx;
                result = String.format( "%08Xh", x2ApicId );
            }
        }
        return result;
    }
}
