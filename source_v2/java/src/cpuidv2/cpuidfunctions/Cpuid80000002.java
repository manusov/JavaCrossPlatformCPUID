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
Class for support CPUID Extended Function 80000002h =
Processor name string [1 of 3].
*/

package cpuidv2.cpuidfunctions;

class Cpuid80000002 extends ReservedFunctionCpuid
{
Cpuid80000002()
    { setFunction( 0x80000002 ); }

@Override String getLongName()
    { return "Processor name string [1 of 3]"; }

@Override String[][] getParametersList()
    {
    String[][] table = new String[][] { { "CPU name string", "n/a" } };
    EntryCpuid[] e1 = container.buildEntries( 0x80000002 );
    EntryCpuid[] e2 = container.buildEntries( 0x80000003 );
    EntryCpuid[] e3 = container.buildEntries( 0x80000004 );
    if ( ( e1 != null )&&( e2 != null )&&( e3 != null )&&
         ( e1.length == 1 )&&( e2.length == 1)&&( e3.length == 1 ) )
        {
        EntryCpuid[] functions = new EntryCpuid[] { e1[0], e2[0], e3[0] };
        int[] data = new int[12];
        for( int i=0; i<3; i++ )
            {
            data[i*4]   = functions[i].eax;
            data[i*4+1] = functions[i].ebx;
            data[i*4+2] = functions[i].ecx;
            data[i*4+3] = functions[i].edx;
            }
        StringBuilder sb = new StringBuilder( "" );
        for( int i=0; i<12; i++ )
            {
            int d = data[i];
            for( int j=0; j<4; j++ )
                {
                char c = (char)( d & 0xFF );
                if ( c != 0 )
                    {
                    if ( ( c < ' ' )||( c > '}' ) ) c = '.';
                    sb.append( c );
                    }
                d = d >>> 8;
                }
            }
        table[0][1] = sb.toString().trim().replaceAll( "\\s+", " " );
        }
    else
        {
        table[0][1] = "ERROR";
        }
    return table;
    }
}
