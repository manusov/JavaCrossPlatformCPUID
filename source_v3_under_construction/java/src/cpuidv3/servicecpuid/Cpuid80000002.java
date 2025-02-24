/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Extended Function 80000002h =
Processor name string [1 of 3].

*/

package cpuidv3.servicecpuid;

import cpuidv3.sal.EntryCpuidSubfunction;


class Cpuid80000002 extends ReservedFunctionCpuid implements ICpuName
{
    Cpuid80000002() { setFunction( 0x80000002 ); }

    @Override String getLongName()
        { return "Processor name string [1 of 3]"; }

    @Override String[][] getParametersList()
    {
        return new String[][] { { "CPU name string", getCpuName() } };
    }

    @Override public String getCpuName()
    {
        String result;
        EntryCpuidSubfunction[] e1 = container.buildEntries( 0x80000002 );
        EntryCpuidSubfunction[] e2 = container.buildEntries( 0x80000003 );
        EntryCpuidSubfunction[] e3 = container.buildEntries( 0x80000004 );
        if ( ( e1 != null )&&( e2 != null )&&( e3 != null )&&
             ( e1.length == 1 )&&( e2.length == 1)&&( e3.length == 1 ) )
        {
            EntryCpuidSubfunction[] functions = 
                new EntryCpuidSubfunction[] { e1[0], e2[0], e3[0] };
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
            result = sb.toString().trim().replaceAll( "\\s+", " " );
        }
        else
        {
            result = "?";
        }
        return result;
    }
}
