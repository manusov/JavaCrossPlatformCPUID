/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 00000000h =
Maximum standard function and vendor string.

*/

package cpuidv3.servicecpuid;

class Cpuid00000000 extends ReservedFunctionCpuid implements IVendorName
{
    Cpuid00000000() { setFunction( 0x00000000 ); }

    @Override String getLongName()
        { return "Maximum standard function and vendor string"; }

    @Override String[][] getParametersList()
    {
        String[][] table = new String[][]
            { { "Maximum standard CPUID level" , "n/a"             } ,
            {   "CPU vendor string"            , getVendorName() } };
        if ( ( entries != null )&&( entries.length == 1 ) )
        {
            table[0][1] = String.format( "%08Xh" , entries[0].eax );
        }
        return table;
    }
    
    @Override public String getVendorName()
    {
        String result = "n/a";
        if ( ( entries != null )&&( entries.length == 1 ) )
        {
            String s = extractVendorString( entries[0], false );
            if ( s != null )
            {
                result = s;
            }
        }
        return result;
    }
}
