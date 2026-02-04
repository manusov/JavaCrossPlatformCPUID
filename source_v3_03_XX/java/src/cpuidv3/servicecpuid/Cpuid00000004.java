/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 00000004h =
Deterministic cache parameters.

*/

package cpuidv3.servicecpuid;

class Cpuid00000004 extends ReservedFunctionCpuid
{
Cpuid00000004()
    { setFunction( 0x00000004 ); }

@Override String getLongName()
    { return "Deterministic cache parameters"; }

@Override String[] getParametersListUp()
    { return new String[] 
        { "Parameter" , "L1 code" , "L1 data" , "L2 unified" , "L3 unified" }; }

// Control tables for results decoding
private final static String[] CACHE_PARMS =
    { "Cache size (KB)",
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
      "Direct mapped (0) or complex (1)" };

private final static int L1_CODE = 0x22;
private final static int L1_DATA = 0x21;
private final static int L2_UNIFIED = 0x43;
private final static int L3_UNIFIED = 0x63;

@Override String[][] getParametersList()
    {
    String s[][] = { { "?", "?", "?", "?", "?" } };
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        int rowsCount = CACHE_PARMS.length;
        int columnsCount = s[0].length;
        int cacheCount = entries.length;
        s = new String[rowsCount][columnsCount];
        for( int i=0; i<rowsCount; i++ )
            {
            s[i][0] = CACHE_PARMS[i];
            for( int j=1; j<columnsCount; j++ )
                {
                s[i][j] = "";
                }
            }
        for( int i=0; i<cacheCount; i++ )
            {
            // Detect cache level and type by tag value
            int j;
            switch( entries[i].eax & 0xFF )
                {
                case L1_CODE:
                    j = 1;
                    break;
                case L1_DATA:
                    j = 2;
                    break;
                case L2_UNIFIED:
                    j = 3;
                    break;
                case L3_UNIFIED:
                    j = 4;
                    break;
                default:
                    j = 0;
                    break;
                }
            // Not visual data if known entries not found
            if ( j == 0 ) continue;
            // Temporary variables with cache parameters    
            int x1 = entries[i].ecx + 1;
            int x2 = ( entries[i].ebx & 0xFFF ) + 1;
            int x3 = ( ( entries[i].ebx >> 12 ) & 0x3FF) + 1;
            int x4 = ( ( entries[i].ebx >> 22 ) & 0x3FF) + 1;
            // Fill j-selected column with extracted numeric values
            s[0][j]  = String.format( "%d" , x1 * x2 * x3 * x4 / 1024 );
            s[1][j]  = String.format( "%d" , x2 );
            s[2][j]  = String.format( "%d" , x3 );
            s[3][j]  = String.format( "%d" , x4 );
            s[4][j]  = String.format( "%d" , x1 );
            s[5][j]  = String.format
                ( "%d" , ( ( entries[i].eax >> 14 ) & 0xFFF ) + 1 );
            s[6][j]  = String.format
                ( "%d" , ( ( entries[i].eax >> 26 ) & 0x3F) + 1 );
            s[7][j]  = String.format( "%d" , ( entries[i].eax & 0x0100) >> 8 );
            s[8][j]  = String.format( "%d" , ( entries[i].eax & 0x0200) >> 9 );
            s[9][j]  = String.format( "%d" , entries[i].edx & 0x0001 );
            s[10][j] = String.format( "%d" , ( entries[i].edx & 0x0002 ) >> 1 );
            s[11][j] = String.format( "%d" , ( entries[i].edx & 0x0004) >> 2 );
            }
        }
    return s;
    }
}
