/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Vendor Specific Function
80860003h = Transmeta vendor-specific: 
            Transmeta information string, part 1 of 4.
*/

package cpuidrefactoring.devicecpuid;

public class Cpuid80860003 extends ReservedFunctionCpuid
{
Cpuid80860003()
    { setFunction( 0x80860003 ); }

@Override String getLongName()
    { return "Transmeta information string [1 of 4]"; }

@Override String[][] getParametersList()
    {
    String[][] table =
            new String[][] { { "Transmeta information string", "n/a" } };
    EntryCpuid[] e1 = container.buildEntries( 0x80860003 );
    EntryCpuid[] e2 = container.buildEntries( 0x80860004 );
    EntryCpuid[] e3 = container.buildEntries( 0x80860005 );
    EntryCpuid[] e4 = container.buildEntries( 0x80860006 );
    if ( ( e1 != null )&&( e2 != null )&&( e3 != null )&&( e4 != null ) &&
         ( e1.length == 1 )&&( e2.length == 1) &&
         ( e3.length == 1 )&&( e4.length == 1 ) )
        {
        EntryCpuid[] functions = 
                new EntryCpuid[] { e1[0], e2[0], e3[0], e4[0] };
        int[] data = new int[16];
        for( int i=0; i<4; i++ )
            {
            data[i*4]   = functions[i].eax;
            data[i*4+1] = functions[i].ebx;
            data[i*4+2] = functions[i].ecx;
            data[i*4+3] = functions[i].edx;
            }
        StringBuilder sb = new StringBuilder( "" );
        for( int i=0; i<16; i++ )
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
        if ( sb.length() != 0 )
            table[0][1] = sb.toString().trim();
        else
            table[0][1] = "n/a";
        }
    else
        {
        table[0][1] = "ERROR";
        }
    return table;
    }
}
