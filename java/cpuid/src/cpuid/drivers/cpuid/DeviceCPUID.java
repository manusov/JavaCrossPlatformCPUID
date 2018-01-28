//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID data source declared as CPR.DEVICE, main module of complex driver.

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.Command;
import cpuid.drivers.cpr.CommandAdapter;
import cpuid.drivers.cpr.DeviceAdapter;

public class DeviceCPUID extends DeviceAdapter
{

private long[] cpuidArray;                // CPUID binary dump array
private final static int ARRAY_BASE = 4;  // First element number after header
private int arrayCursor;                  // Position for array operations
private long dataArray;                   // Temporary data for array ops.
private int entriesCount=0;               // Number of CPUID functions entries 

private final static String[] DUMP_UP =   // Table up for cpuid dump
    { "Function" , "Subfunction" , "Pass" , "EAX" , "EBX" , "ECX" , "EDX" };
private final static int DUMP_COLS = DUMP_UP.length;
private String[][] dumpText;              // Text block for cpuid dump
private String[] functionsShortNames;     // Short names for tabs
private int[] functionsCodes;             // CPUID function codes (EAX)

// Number of Standard, Virtual, Extended recognized functions
private final static int NS=0x17, NV=0x1, NE=0x20;  // For different types
private final static int NSUM = NS+NV+NE;           // Total recognized

private final static Object[] CPUID_FUNCTIONS =     // List of recognized
    {
    new CPUID00000000() ,  // first standard function = 00000000h
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
    new CPUID40000000() ,  // first virtual function = 40000000h
    new CPUID80000000() ,  // first extended function = 80000000h
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
    new CPUID8000001F() ,
    };

// Binary communications, can be routed to current platform or saved file
@Override public void setBinary(long[] x) { cpuidArray = x;    }
@Override public long[] getBinary()       { return cpuidArray; }

// Initializing binary level operations and return validity flag
@Override public boolean initBinary()
    {
    entriesCount = ( int )( cpuidArray[0] & 0x3FF );  // Get count from header
    return ((entriesCount>0)&(entriesCount<512));
    }

// Build text blocks by binary data and return validity flag
@Override public boolean parseBinary()
    {
    // Build cpuid dump
    int i, j;
    dumpText = new String[entriesCount][DUMP_COLS];
    arrayCursor = ARRAY_BASE;
    dataArray=0;
    for (i=0; i<entriesCount; i++)  // Cycle for dump rows 
        {
        for (j=0; j<DUMP_COLS; j+=2)  // Cycle for dump columns
            {
            dataArray = cpuidArray[arrayCursor] >>> 32;  // Even column
            dumpText[i][j] = String.format("%08X", dataArray );
            if (j<DUMP_COLS-1)  // Odd column, except last
                {
                dataArray = cpuidArray[arrayCursor+1] & ((long)(-1) >>> 32);
                dumpText[i][j+1] = String.format("%08X", dataArray );
                }
            arrayCursor++;
            }
        }
    // Build Standard, Extended, Virtual functions short names
    functionsShortNames = new String[NSUM];
    functionsCodes = new int[NSUM];
    for (i=0; i<NS; i++)  // Cycle for standard functions short names
        {
        functionsCodes[i] = i;
        functionsShortNames[i] = String.format( "%08X", functionsCodes[i] );
        }
    for (j=0; j<NV; j++)  // Cycle for virtual functions short names
        {
        functionsCodes[i] = j+0x40000000;
        functionsShortNames[i] = String.format( "%08X",  functionsCodes[i] );
        i++;
        }
    for (j=0; j<NE; j++)  // Cycle for extended functions short names
        {
        functionsCodes[i] = j+0x80000000;
        functionsShortNames[i] = String.format( "%08X", functionsCodes[i] );
        i++;
        }
    return true;
    }

// Get names for Summary and Dump tabs
@Override public String getSummaryName() { return "CPUID Summary"; }
@Override public String getDumpName()    { return "CPUID Dump";    }

// Build text block for Summary information window
@Override public String[][] getSummaryText()
    {
    // Get CPU name and vendor strings, standard and extended functions count
    CPUID80000002 x1 = new CPUID80000002();
    CPUID00000000 x2 = new CPUID00000000();
    CPUID80000000 x3 = new CPUID80000000();
    String[][] s1 = x1.getCommandText1(cpuidArray);  // CPU name string
    String[][] s2 = x2.getCommandText1(cpuidArray);  // Standard functions
    String[][] s3 = x3.getCommandText1(cpuidArray);  // Extended functions
    // Create and pre-blank text array, sized by functions results
    int nx=2, ny = s1.length + s2.length + 1;
    String s[][] = new String[ny][nx];
    for( int i=0; i<ny; i++ )  // Blank cycle for rows
        {
        for ( int j=0; j<nx; j++ )  // Blank cycle for columns
            {
            s[i][j]=""; 
            }
        }
    // Start copy content string to summary table
    int k=0;  // pointer for sequental copy strings, rows offset
    // Foreach cycle comments (s11=temporary created):
    // At each iteration, s11[] assigns value of s1[][i]
    for (String[] s11 : s1)  // CPU name string, rows cycle
        {
        // System.arraycopy operator comments:
        // parm#1 = source array
        // parm#2 = starting position at source array
        // parm#3 = destination array
        // parm#4 = starting position at destination array
        // parm#5 = length
        System.arraycopy(s11, 0, s[k], 0, s1[0].length);  // columns cycle
        k++;
        }
    for( int i=s2.length-1; i>-1; i-- )  // Standard functions data, rows cycle
        { 
        System.arraycopy(s2[i], 0, s[k], 0, s2[0].length);  // columns cycle
        k++; 
        }
    for( int i=0; i<1; i++ )  // Extended functions data, rows cycle
        { 
        System.arraycopy(s3[i], 0, s[k], 0, 2);  // columns cycle
        k++; 
        }
    return s;
    }

// Get CPUID dump table up string with columns names
@Override public String[]   getDumpUp()    { return DUMP_UP; }

// Get CPUID dump table content strings
@Override public String[][] getDumpText()  { return dumpText; }

// Get number of CPUID functions
@Override public int getCommandsCount()    { return NSUM; }

// Get short name string by given CPUID function code
@Override public String getCommandShortName(int x)
    { return functionsShortNames[x]; }

// Get long name string by given CPUID function code
@Override public String getCommandLongName(int x)
    { return ((CommandAdapter)(CPUID_FUNCTIONS[x])).getCommandLongName(null); }

// Return flag supported/not supported by given CPUID function code
@Override public boolean getCommandSupported(int x)
    {
    x = functionsCodes[x];  // Get CPUID function by command ID
    arrayCursor = ARRAY_BASE;
    for (int i=0; i<entriesCount; i++)  // Find required number in the array
        {
        dataArray = cpuidArray[arrayCursor] >> 32;
        if (dataArray==x) return true;  // check CPUID function code match
        arrayCursor += 4;             // advance pointer to next entry = 4 long
        }
    return false;  // control here if no matches found
    }

// CPUID one function description text table up strings with columns names
@Override public String[] getCommandUp1(int x)
    {
    String[] s = ((Command)(CPUID_FUNCTIONS[x])).getCommandUp1(cpuidArray);
    if (s==null) { s = super.getCommandUp1(x); }
    return s;        
    }

// CPUID one function description text table content strings
@Override public String[][] getCommandText1(int x)
    {
    int k=1;
    int r=2;
    String[][] s1 = super.getCommandText1(x);
    String[][] s2 = ((Command)(CPUID_FUNCTIONS[x])).getCommandText1(cpuidArray);
    String[]   s3 = ((Command)(CPUID_FUNCTIONS[x])).getCommandUp1(cpuidArray);
    // Note exception generated if Up1 exist but Text1 not exist,
    // child class can implement Text1 only or both Text1+Up1.
    // Note limit 100 strings.
    // Prepare s1(template)
    if ((s2!=null)&&(s3!=null))
        {
        int p = s3.length;  // p = number of strings(words) in the table up
        int q = s1.length;  // q = number of columns in the table content
        r = p;
        s1 = new String[q][p];
        // Blanking
        for ( int i=0; i<q; i++ )  // Cycle for rows
            { 
            for ( int j=0; j<p; j++ )  // Cycle for columns
                { 
                s1[i][j] = ""; 
                } 
            }  
        }
    // Copy used strings from s2(actual data) to s1(template)
     if (s2!=null)
        {
        int n = s1[0].length;
        int m = s2.length;
        for ( int i=0; i<m; i++ )  // Cycle for rows
            { 
            System.arraycopy(s2[i], 0, s1[i], 0, n);  // Cycle for columns
            k++; 
            }
        }
    //--- Trim ---
    String[][] s4 = new String[k][r];
    for (int i=0; i<k; i++)  // Cycle for rows 
        {
        System.arraycopy(s1[i], 0, s4[i], 0, r);  // Cycle for columns
        }
    return s4;  // Return 2D text table
    }

// Return CPUID one function dump up string with columns names
@Override public String[] getCommandUp2(int unused) { return DUMP_UP; }

// Return CPUID one function dump content strings
@Override public String[][] getCommandText2(int x)
    {
    // Blank
    int n = DUMP_UP.length;
    int m = 20;
    String[][] s = new String[m][n];
    for (int i=0; i<m; i++)  // Cycle for rows
        { 
        for(int j=0; j<n; j++)  // Cycle for columns
            {
            s[i][j]="";
            } 
        }
    s[0][0] = "n/a";
    // Fill
    x = functionsCodes[x];
    arrayCursor = ARRAY_BASE;
    int k=0;
    for (int i=0; i<entriesCount; i++)  // Cycle for rows
        {
        dataArray = cpuidArray[arrayCursor] >> 32;
        if ( (x==dataArray) && (k<=m) )
            {  // Cycle by arraycopy for columns
            System.arraycopy(dumpText[ arrayCursor/4-1 ], 0, s[k], 0, n); 
            k++;
            }
        arrayCursor += 4;
        } 
    // Trim
    String[][] s1 = new String[k][n];
    for (int i=0; i<k; i++)  // Cycle for rows 
        {
        System.arraycopy(s[i], 0, s1[i], 0, n);  // Cycle for columns
        }
    return s1;
    }

}
