/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Driver class for interpreting CPUID information.
Text arrays = F ( CPUID binary dump ).
*/

package cpuidrefactoring.devicecpuid;

import cpuidrefactoring.system.Device;
import java.util.ArrayList;

public class DeviceCpuid extends Device
{

private final static ReservedFunctionCpuid[] STANDARD_FUNCTIONS =
    {
    new Cpuid00000000(),
    new Cpuid00000001(),
    new Cpuid00000002(),
    new Cpuid00000003(),
    new Cpuid00000004(),
    new Cpuid00000005(),
    new Cpuid00000006(),
    new Cpuid00000007(),
    new Cpuid00000008(),
    new Cpuid00000009(),
    new Cpuid0000000A(),
    new Cpuid0000000B(),
    new Cpuid0000000C(),
    new Cpuid0000000D(),
    new Cpuid0000000E(),
    new Cpuid0000000F(),
    new Cpuid00000010(),
    new Cpuid00000011(),
    new Cpuid00000012(),
    new Cpuid00000013(),
    new Cpuid00000014(),
    new Cpuid00000015(),
    new Cpuid00000016(),
    new Cpuid00000017(),
    new Cpuid00000018(),
    new Cpuid00000019(),
    new Cpuid0000001A(),
    new Cpuid0000001B(),
    new Cpuid0000001C(),
    new Cpuid0000001D(),
    new Cpuid0000001E(),
    new Cpuid0000001F()
    };
    
private final static ReservedFunctionCpuid[] EXTENDED_FUNCTIONS =
    {
    new Cpuid80000000(),
    new Cpuid80000001(),
    new Cpuid80000002(),
    new Cpuid80000003(),
    new Cpuid80000004(),
    new Cpuid80000005(),
    new Cpuid80000006(),
    new Cpuid80000007(),
    new Cpuid80000008(),
    new Cpuid80000009(),
    new Cpuid8000000A(),
    new Cpuid8000000B(),
    new Cpuid8000000C(),
    new Cpuid8000000D(),
    new Cpuid8000000E(),
    new Cpuid8000000F(),
    new Cpuid80000010(),
    new Cpuid80000011(),
    new Cpuid80000012(),
    new Cpuid80000013(),
    new Cpuid80000014(),
    new Cpuid80000015(),
    new Cpuid80000016(),
    new Cpuid80000017(),
    new Cpuid80000018(),
    new Cpuid80000019(),
    new Cpuid8000001A(),
    new Cpuid8000001B(),
    new Cpuid8000001C(),
    new Cpuid8000001D(),
    new Cpuid8000001E(),
    new Cpuid8000001F(),
    new Cpuid80000020()
    };

private final static ReservedFunctionCpuid[] VIRTUAL_FUNCTIONS =
    {
    new Cpuid40000000()
    };

private final static SummaryCpuid[] SUMMARY_SCREENS =
    {
    new CpuidSummary(),
    new CpuidDump(),
    new CpuidTree()
    };

private final ContainerCpuid container = new ContainerCpuid
    ( STANDARD_FUNCTIONS, EXTENDED_FUNCTIONS, 
      VIRTUAL_FUNCTIONS, SUMMARY_SCREENS );

private String[]     screensShortNames;
private String[]     screensLongNames;
private String[][]   screensListsUp;
private String[][][] screensLists;
private String[][]   screensDumpsUp;
private String[][][] screensDumps;

@Override public String[] getScreensShortNames()
    { return screensShortNames; }

@Override public String[] getScreensLongNames()
    { return screensLongNames; }

@Override public String[][] getScreensListsUp()
    { return screensListsUp; }

@Override public String[][][] getScreensLists()
    { return screensLists; }

@Override public String[][] getScreensDumpsUp()
    { return screensDumpsUp; }

@Override public String[][][] getScreensDumps()
    { return screensDumps; }

public DeviceCpuid()
    {
    ReservedFunctionCpuid.setContainer( container );
    SummaryCpuid.setContainer( container );
    }

@Override public long[] getBinary()
    { return container.getBinaryDump(); }

@Override public void setBinary( long[] bd )
    { container.setBinaryDump( bd ); }

@Override public boolean initBinary()
    {
    long[] bd = container.getBinaryDump();
    if ( bd == null ) return false;
    int n = bd.length;                  // n = array full size, units = long
    if ( ( n == 0 ) || ( ( n % 8 ) != 0 ) ) return false;
    int m = ( int )( bd[0] & 0x3FF );   // m = entries count, units = 32 bytes
    return ( ( m > 0 ) & ( m < 512 ) & ( m <= n / 4 ) );
    }

@Override public boolean parseBinary()
    {
    ArrayList<String> shortNames = new ArrayList<>();
    ArrayList<String> longNames = new ArrayList<>();
    ArrayList<String[]> listsUp = new ArrayList<>();
    ArrayList<String[][]> lists = new ArrayList<>();
    ArrayList<String[]> dumpsUp = new ArrayList<>();
    ArrayList<String[][]> dumps = new ArrayList<>();
    ArrayList<ReservedFunctionCpuid> functions = new ArrayList<>();

    for( ReservedFunctionCpuid item : STANDARD_FUNCTIONS )
        helperInitAndAddFunction
            ( item, shortNames, longNames, listsUp, lists, dumpsUp, dumps,
              functions );
    for( ReservedFunctionCpuid item : VIRTUAL_FUNCTIONS )
        helperInitAndAddFunction
            ( item, shortNames, longNames, listsUp, lists, dumpsUp, dumps,
              functions );
    for( ReservedFunctionCpuid item : EXTENDED_FUNCTIONS )
        helperInitAndAddFunction
            ( item, shortNames, longNames, listsUp, lists, dumpsUp, dumps,
              functions );
    
    // this operation required before summary screens build
    ReservedFunctionCpuid[] detectedFunctions = functions.toArray
        ( new ReservedFunctionCpuid[ functions.size() ] );
    container.setDetectedFunctions( detectedFunctions );

    // this operations requires detectedFunctions list, valid after previous
    int insertIndex = 0;  // but this screens must be located first, 0,1,2
    for( SummaryCpuid item : SUMMARY_SCREENS )
        {
        shortNames.add( insertIndex, item.getShortName() );
        longNames.add ( insertIndex, item.getLongName() );
        listsUp.add   ( insertIndex, item.getParametersListUp() );
        lists.add     ( insertIndex, item.getParametersList() );
        dumpsUp.add   ( insertIndex, null );
        dumps.add     ( insertIndex, null );
        insertIndex++;
        }
    
    screensShortNames = shortNames.toArray( new String[ shortNames.size() ] );
    screensLongNames  = longNames.toArray ( new String[ longNames.size()  ] );
    screensListsUp    = listsUp.toArray( new String[ listsUp.size() ][] );
    screensLists      = lists.toArray( new String[ lists.size() ][][] );
    screensDumpsUp    = dumpsUp.toArray( new String[ dumpsUp.size() ][] );
    screensDumps      = dumps.toArray( new String[ dumps.size() ][][] );
    return true;
    }

private void helperInitAndAddFunction
    ( ReservedFunctionCpuid x, 
      ArrayList<String> y1, ArrayList<String> y2, 
      ArrayList<String[]> y3, ArrayList<String[][]> y4,
      ArrayList<String[]> y5, ArrayList<String[][]> y6,
      ArrayList<ReservedFunctionCpuid> z )
    {

    int function = x.getFunction();
    EntryCpuid[] ec = container.buildEntries( function );
    x.initData( ec );

    if ( x.isShow() )
        {
        y1.add( x.getShortName() );
        y2.add( x.getLongName() );
        y3.add( x.getParametersListUp() );
        y4.add( x.getParametersList() );
        y5.add( x.getRegistersDumpUp() );
        y6.add( x.getRegistersDump() );
        z.add( x );
        }
    }
}


