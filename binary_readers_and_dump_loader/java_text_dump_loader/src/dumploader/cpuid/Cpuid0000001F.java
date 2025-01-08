/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 0000001Fh =
Advanced multiprocessing topology information.

*/

package dumploader.cpuid;

class Cpuid0000001F extends ReservedFunctionCpuid
{
Cpuid0000001F()
    { setFunction( 0x0000001F ); }

@Override String getLongName()
    { return "Advanced multiprocessing topology information"; }

@Override String[] getParametersListUp()
    { return new String[] 
        { "Parameter" , "SMT" , "Core" , "Module" , "Tile" , "Die" }; }

// Control tables for results decoding
private final static String[] SMT_PARMS = 
    { "Number of logical processor at this level type" ,
      "Bits shift right on x2APIC ID to get next level ID" ,
      "Level number" ,
      "Level type" ,
      "Current x2APIC ID"
    };

@Override String[][] getParametersList()
    {
    String s[][] = { { "?", "?", "?", "?", "?", "?" } };
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
            // Write fields with numeric parameters
            if ( ( j >= 1 )&( j <= 5 ) )  // Detect known codes
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
}
