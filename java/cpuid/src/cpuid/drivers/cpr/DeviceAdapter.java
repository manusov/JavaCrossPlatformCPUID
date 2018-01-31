/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
Interface adapter for CPR model, entire Device submodel.
See detail comments in the Device.java.
*/

package cpuid.drivers.cpr;

public abstract class DeviceAdapter implements Device 
{
private final static String[] SIMPLEST_UP = { "Parameter" , "Value" };
    
@Override public void setBinary(long[] x)            { }
@Override public long[] getBinary()                  { return null; }
@Override public boolean initBinary()                { return false; }
@Override public boolean parseBinary()               { return false; }

@Override public String getSummaryName()             { return "Summary"; }
@Override public String[] getSummaryUp()             { return SIMPLEST_UP; }
@Override public String[][] getSummaryText()         { return simplestText(); }

@Override public String getDumpName()                { return "Dump";    }
@Override public String[] getDumpUp()                { return SIMPLEST_UP; }
@Override public String[][] getDumpText()            { return simplestText(); }

@Override public int getCommandsCount()              { return 0; }
@Override public String getCommandShortName(int x)   { return null; }
@Override public String getCommandLongName(int x)    { return null; }
@Override public boolean getCommandSupported(int x)  { return false; }
@Override public String[] getCommandUp1(int x)       { return SIMPLEST_UP; }
@Override public String[][] getCommandText1(int x)   { return simplestText(); }
@Override public String[] getCommandUp2(int x)       { return SIMPLEST_UP; }
@Override public String[][] getCommandText2(int x)   { return simplestText(); }

@Override public int getPinsCount()                  { return 0; }
@Override public String getPinName(int x)            { return null; }
@Override public boolean getPinSupported(int x)      { return false; }
@Override public String[] getPinUp1(int x)           { return SIMPLEST_UP; }
@Override public String[][] getPinText1(int x)       { return simplestText(); }
@Override public String[] getPinUp2(int x)           { return SIMPLEST_UP; }
@Override public String[][] getPinText2(int x)       { return simplestText(); }

@Override public int getRegistersCount()             { return 0; }
@Override public String getRegisterName(int x)       { return null; }
@Override public boolean getRegisterSupported(int x) { return false; }
@Override public String[] getRegisterUp1(int x)      { return SIMPLEST_UP; }
@Override public String[][] getRegisterText1(int x)  { return simplestText(); }
@Override public String[] getRegisterUp2(int x)      { return SIMPLEST_UP; }
@Override public String[][] getRegisterText2(int x)  { return simplestText(); }

// Helper method, returns blanked text table

private static String[][] simplestText()
    {
    int n = 200;    // 100;  // cause bug with function 00000007h
    int m = 2;
    String[][] s = new String[n][m];
    for ( int i=0; i<n; i++ )
        { for ( int j=0; j<m; j++ ) { s[i][j] = ""; } }
    return s;
    }

}
