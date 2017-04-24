//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Interface adapter for CPR model "Command" submodel.

package arch1.drivers.cpr;

public abstract class CommandAdapter implements Command 
{

//---------- Interface implementation ------------------------------------------    

@Override public String getCommandShortName( long[] array )   { return null ; }
@Override public String getCommandLongName( long[] array )    { return null ; }
@Override public String[] getCommandUp1( long[] array )       { return null ; }
@Override public String[][] getCommandText1( long[] array )   { return null ; }
@Override public String[] getCommandUp2( long[] array )       { return null ; }
@Override public String[][] getCommandText2( long[] array )   { return null ; }
@Override public boolean getCommandSupported( long[] array )  { return false; }

//---------- Helper methods for child classes ----------------------------------


/*

// Moved to class CPR

public int findFunction(long[] x, int y)
    {
    int base = 4;
    int length = (int)(x[0] & ((long)((long)(-1)>>>32)));
    for ( int i=0; i<length; i++ )
        {
        if ( (x[base] >> 32 ) == y ) { return base; } 
        base += 4;
        }
    return -1;
    }

public int[] decodeBitfields
    ( String regname, Object[][] regmap, int regvalue, 
      String[][] destination, int offset )
    {
    int n = regmap.length;
    String s1, s2;
    int x1,x2,x3,y1,y2;
    int[] extract = new int[n];
    
    for (int i=0; i<n; i++)
        {
        x1 = (int)regmap[i][1];
        x2 = (int)regmap[i][2];
        x3 = x1 - x2 + 1;
        if (x1==x2) { s1 = String.format( "%d", x1 ); }
        else        { s1 = String.format( "%d-%d", x1, x2 ); }
        y1 = regvalue >> x2;
        y2 = ~( -1 << x3 );
        if (y2==0) { y2=-1; }
        y1 = y1 & y2;
        extract[i] = y1;
        s2 = String.format("%X", y1);
        destination[i+offset][0] = (String)regmap[i][0];
        destination[i+offset][1] = regname;
        destination[i+offset][2] = s1;
        destination[i+offset][3] = s2;
        }
    
    return extract;
    }

public void decodeBitmap
    ( String regname, String[][] regmap, int regvalue, 
      String[][] destination, int offset )
    {
    int n = regmap.length;
    String s1, s2, s3;
    int x1 = 1;

    for (int i=0; i<n; i++)
        {
        s1 = String.format("%d = ",i) + regmap[i][0];
        if ((regvalue & x1) == 0) { s2 = "0"; s3 = "not supported"; }
        else { s2 = "1"; s3 = "supported"; }
        x1 <<= 1;
        destination[i+offset][0] = regmap[i][1];
        destination[i+offset][1] = regname;
        destination[i+offset][2] = s1;
        destination[i+offset][3] = s2;
        destination[i+offset][4] = s3;
        }
    }
*/

}
