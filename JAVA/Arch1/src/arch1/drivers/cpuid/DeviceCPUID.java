//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID data source declared as CPR.DEVICE, main module of complex driver.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.Command;
import arch1.drivers.cpr.CommandAdapter;
import arch1.drivers.cpr.DeviceAdapter;


public class DeviceCPUID extends DeviceAdapter
{

private long[] cpuidArray;
private final static int ARRAY_BASE = 4;
private int arrayCursor;
private long dataArray;
private int entriesCount=0;

private final static String[] DUMP_UP = 
    { "Function" , "Subfunction", "Pass", "EAX", "EBX", "ECX", "EDX" };
private final static int DUMP_COLS = DUMP_UP.length;
private String[][] dumpText;

// private final static int SUMMARY_COUNT = 10;

// private final static String[] SUMMARY_UP =                     // v0.45 chgs.
//     { "Parameter" , "Value" };

// private final static int SUMMARY_COLS = SUMMARY_UP.length;     // v0.45 chgs.
// private String[][] summaryText;

private String[] functionsShortNames;
private int[] functionsCodes;

private final static int NS=0x17, NV=0x1, NE=0x1F;
private final static int NSUM = NS+NV+NE;
private final static Object[] CPUID_FUNCTIONS =
    {
    new CPUID00000000() ,
    new CPUID00000001() ,
    new CPUID00000002() ,
    new CPUID00000003() ,
    new CPUID00000004() ,
    new CPUID00000005() ,
    new CPUID00000006() ,
    new CPUID00000007() ,
    new CPUID00000008() ,
    new CPUID00000009() ,
    new CPUID0000000A() ,
    new CPUID0000000B() ,
    new CPUID0000000C() ,
    new CPUID0000000D() ,
    new CPUID0000000E() ,
    new CPUID0000000F() ,
    new CPUID00000010() ,
    new CPUID00000011() ,
    new CPUID00000012() ,
    new CPUID00000013() ,
    new CPUID00000014() ,
    new CPUID00000015() ,
    new CPUID00000016() ,
    new CPUID40000000() ,
    new CPUID80000000() ,
    new CPUID80000001() ,
    new CPUID80000002() ,
    new CPUID80000003() ,
    new CPUID80000004() ,
    new CPUID80000005() ,
    new CPUID80000006() ,
    new CPUID80000007() ,
    new CPUID80000008() ,
    new CPUID80000009() ,
    new CPUID8000000A() ,
    new CPUID8000000B() ,
    new CPUID8000000C() ,
    new CPUID8000000D() ,
    new CPUID8000000E() ,
    new CPUID8000000F() ,
    new CPUID80000010() ,
    new CPUID80000011() ,
    new CPUID80000012() ,
    new CPUID80000013() ,
    new CPUID80000014() ,
    new CPUID80000015() ,
    new CPUID80000016() ,
    new CPUID80000017() ,
    new CPUID80000018() ,
    new CPUID80000019() ,
    new CPUID8000001A() ,
    new CPUID8000001B() ,
    new CPUID8000001C() ,
    new CPUID8000001D() ,
    new CPUID8000001E() ,
    };


//---------- Get names ---------------------------------------------------------

@Override public String getSummaryName() { return "CPUID Summary"; }
@Override public String getDumpName()    { return "CPUID Dump";    }

//---------- Communications with PAL under Headblock control -------------------

@Override public void setBinary(long[] x) { cpuidArray = x;    }
@Override public long[] getBinary()       { return cpuidArray; }

//---------- Binary level operations -------------------------------------------

@Override public boolean initBinary()
    {
    entriesCount = ( int )( cpuidArray[0] & 0x3FF );
    if ((entriesCount<=0)|(entriesCount>511)) { return false; }
    return true;
    }

@Override public boolean parseBinary()
    {
    // int i=0, j=0, k=0;
    // int n = SUMMARY_COUNT;
    // int m = SUMMARY_COLS;
    // summaryText = new String[n][m];

    // ...
        
    int i=0, j=0, k=0;
    int n = entriesCount;
    int m = DUMP_COLS;
    dumpText = new String[n][m];
    arrayCursor = ARRAY_BASE;
    dataArray=0;
    for (i=0; i<n; i++)
        {
        for (j=0; j<m; j+=2)
            {
            dataArray = cpuidArray[arrayCursor] >>> 32;
            dumpText[i][j] = String.format("%08X", dataArray );
            k++;
            if (j<m-1)
                {
                dataArray = cpuidArray[arrayCursor+1] & ((long)(-1) >>> 32);
                dumpText[i][j+1] = String.format("%08X", dataArray );
                k++;
                }
            arrayCursor++;
            }
        }
    
    i=0; j=0; k=0;
    functionsShortNames = new String[NSUM];
    functionsCodes = new int[NSUM];
    for (i=0; i<NS; i++)
        {
        functionsCodes[i] = i;
        functionsShortNames[i] = String.format( "%08X", functionsCodes[i] );
        }
    for (j=0; j<NV; j++)
        {
        functionsCodes[i] = j+0x40000000;
        functionsShortNames[i] = String.format( "%08X",  functionsCodes[i] );
        i++;
        }
    for (k=0; k<NE; k++)
        {
        functionsCodes[i] = k+0x80000000;
        functionsShortNames[i] = String.format( "%08X", functionsCodes[i] );
        i++;
        }
    return true;
    }

//---------- Summary information operations ------------------------------------

// @Override public String[]   getSummaryUp()    { return summaryUp; }

@Override public String[][] getSummaryText()
    {
    // return summaryText;
    CPUID80000002 x1 = new CPUID80000002();
    CPUID00000000 x2 = new CPUID00000000();
    CPUID80000000 x3 = new CPUID80000000();
    String[][] s1 = x1.getCommandText1(cpuidArray);
    String[][] s2 = x2.getCommandText1(cpuidArray);
    String[][] s3 = x3.getCommandText1(cpuidArray);
    
    int nx=2, ny = s1.length + s2.length + 1; // s3.length; // ny=100;
    String s[][] = new String[ny][nx];
    for( int i=0; i<ny; i++ ) { for ( int j=0; j<nx; j++ ) { s[i][j]=""; } }
    int k=0;
    for( int i=0; i<s1.length; i++ )
        { for ( int j=0; j<s1[0].length; j++ ) { s[k][j] = s1[i][j]; } k++; }
    for( int i=s2.length-1; i>-1; i-- )
        { for ( int j=0; j<s2[0].length; j++ ) { s[k][j] = s2[i][j]; } k++; }
    for( int i=0; i<1; i++ )
        { for ( int j=0; j<2; j++ ) { s[k][j] = s3[i][j]; } k++; }

    return s;
    }

//---------- Dump information operations ---------------------------------------

@Override public String[]   getDumpUp()    { return DUMP_UP; }
@Override public String[][] getDumpText()  { return dumpText; }

//---------- Commands (CPUID functions) operations -----------------------------

@Override public int getCommandsCount()
    {
    return NSUM;
    }

@Override public String getCommandShortName(int x)
    {
    return functionsShortNames[x];
    }

@Override public String getCommandLongName(int x)
    {
    return ((CommandAdapter)(CPUID_FUNCTIONS[x])).getCommandLongName(null);
    }

@Override public boolean getCommandSupported(int x)
    {
    x = functionsCodes[x];
    arrayCursor = ARRAY_BASE;
    for (int i=0; i<entriesCount; i++)
        {
        dataArray = cpuidArray[arrayCursor] >> 32;
        if (dataArray==x) return true;
        arrayCursor += 4;
        }
    return false;
    }

@Override public String[] getCommandUp1(int x)
    {
    String[] s = ((Command)(CPUID_FUNCTIONS[x])).getCommandUp1(cpuidArray);
    if (s==null) { s = super.getCommandUp1(x); }
    return s;        
    }

@Override public String[][] getCommandText1(int x)
    {
    int k=1;
    int r=2;
    String[][] s1 = super.getCommandText1(x);
    String[][] s2 = ((Command)(CPUID_FUNCTIONS[x])).getCommandText1(cpuidArray);
    String[]   s3 = ((Command)(CPUID_FUNCTIONS[x])).getCommandUp1(cpuidArray);
    // Note exception if Up1 exist but Text1 not exist,
    // child class can implement Text1 only or both Text1+Up1.
    // Note limit 100 strings.
    // Prepare s1(template)
    if ((s2!=null)&&(s3!=null))
        {
        int p = s3.length;
        int q = s1.length;
        r = p;
        s1 = new String[q][p];
        for ( int i=0; i<q; i++ )
            { for ( int j=0; j<p; j++ ) { s1[i][j] = ""; } }  // Blanking
        }
    // Copy used strings from s2(actual data) to s1(template)
     if (s2!=null)
        {
        int n = s1[0].length;
        int m = s2.length;
        for ( int i=0; i<m; i++ )
            { for( int j=0; j<n; j++ ) { s1[i][j] = s2[i][j]; } k++; }  // Copy
        }

    //--- Trim ---
    String[][] s4 = new String[k][r];
    for (int i=0; i<k; i++) { for (int j=0; j<r; j++) { s4[i][j] = s1[i][j]; } }

     
    return s4;   // old s1
    }

@Override public String[] getCommandUp2(int unused)
    {
    return DUMP_UP;
    }

@Override public String[][] getCommandText2(int x)
    {
    //--- Blank ---
    int n = DUMP_UP.length;
    int m = 20;
    String[][] s = new String[m][n];
    for (int i=0; i<m; i++)
        { for(int j=0; j<n; j++) { s[i][j]=""; } }
    s[0][0] = "n/a";
    //--- Fill ---
    x = functionsCodes[x];
    arrayCursor = ARRAY_BASE;
    int k=0;
    for (int i=0; i<entriesCount; i++)
        {
        dataArray = cpuidArray[arrayCursor] >> 32;
        if ( (x==dataArray) && (k<=m) )
            {
            for (int j=0; j<n; j++)
                { s[k][j] = dumpText[ arrayCursor/4-1 ][j]; } 
            k++;
            }
        arrayCursor += 4;
        } 
    //--- Trim ---
    String[][] s1 = new String[k][n];
    for (int i=0; i<k; i++) { for (int j=0; j<n; j++) { s1[i][j] = s[i][j]; } }

    //--- Return ---
    return s1;  // old s
    }


//---------- Pins operations ---------------------------------------------------
// This functionality not used for CPUID.
//---------- Registers operations ----------------------------------------------
// This functionality not used for CPUID.

}
