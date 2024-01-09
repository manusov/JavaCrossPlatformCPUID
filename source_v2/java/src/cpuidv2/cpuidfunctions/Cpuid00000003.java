/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2024.
-------------------------------------------------------------------------------
Class for support CPUID Standard Function 00000003h = Processor serial number.
*/

package cpuidv2.cpuidfunctions;

class Cpuid00000003 extends ReservedFunctionCpuid
{
Cpuid00000003()
    { setFunction( 0x00000003 ); }

@Override String getLongName()
    { return "Processor serial number"; }

@Override String[][] getParametersList()
    {
    String[][] s = null;
    EntryCpuid[] entries1 = container.buildEntries( 0x00000001 );
    if ( ( entries != null )&&( entries1 != null )&&
         ( entries.length > 0 )&&( entries1.length > 0 ) )
        {
        if ( ( entries1[0].edx & ( 1 << 18 ) ) != 0 )  // check PSN supported
            {
            s = new String[][] { { "Processor Serial Number", "n/a" } };
            int[] psn = new int[] 
                { entries[0].ecx, entries[0].edx, entries1[0].eax };
            StringBuilder sb = new StringBuilder( "" );
            for( int i=2; i>=0; i-- )
                {
                int a = psn[i];
                for( int j=1; j>=0; j-- )
                    {
                    int b = ( a >>> ( 16 * j ) ) & 0x0000FFFF;
                    sb.append( String.format( "%04X", b ) );
                    if ( ! ( ( i == 0 )&&( j == 0) ) ) sb.append( "-" );
                    }
                }
            s[0][1] = sb.toString();
            }
        }
    return s == null ? super.getParametersList() : s;
    }
}
