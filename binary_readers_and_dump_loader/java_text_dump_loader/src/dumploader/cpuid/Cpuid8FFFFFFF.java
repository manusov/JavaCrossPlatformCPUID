/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Extended Function 8FFFFFFFh =
Processor non official signature.

*/

package dumploader.cpuid;

class Cpuid8FFFFFFF extends ReservedFunctionCpuid
{
Cpuid8FFFFFFF()
    { setFunction( 0x8FFFFFFF ); }
 
@Override String getLongName()
    { return "Processor non official signature"; }

@Override String[][] getParametersList()
    {
    String[][] table = new String[][] { { "Text string", "n/a" } };
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        int[] data = new int[] 
            { entries[0].eax, entries[0].ebx, entries[0].ecx, entries[0].edx };
                StringBuilder sb = new StringBuilder( "" );
        for( int i=0; i < data.length; i++ )
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
