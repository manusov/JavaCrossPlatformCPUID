/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Decoder class for interpreting topology information, provided by OS API.
Variant for MS Windows, based on GetLogicalProcessorInformation() and
GetLogicalProcessorInformationEx() WinAPI functions as platform data source.
Provides text arrays = F ( Topology information from Windows API ).

How convert long[] array to byte[] array.
https://stackoverflow.com/questions/29927238/java-efficiently-converting-an-array-of-longs-to-an-array-of-bytes
But variable-size access is required for this data type, means
conversion to bytes or other fixed size is non optimal.

*/

package cpuidv3.servicempwindows;

import cpuidv3.pal.PAL.OS_TYPE;
import cpuidv3.servicemp.ServiceMp;
import cpuidv3.servicemp.HelperMemorySize;
import static cpuidv3.servicemp.HelperMemorySize.writeMemorySize;
import cpuidv3.servicempwindows.EntryCache.PROCESSOR_CACHE_TYPE_WINDOWS;
import static cpuidv3.servicempwindows.HelperBinaryParsing.getVariableSize;
import static cpuidv3.servicempwindows.HelperGlpi.interpreterGlpi;
import static cpuidv3.servicempwindows.HelperGlpiEx.interpreterGlpiEx;
import static cpuidv3.servicempwindows.HelperStrings.affinityCount;
import static cpuidv3.servicempwindows.HelperStrings.bitmapString;

import java.util.ArrayList;

public class ServiceMpWindows extends ServiceMp
{
    private long[] binaryDump;
    private int binaryStatus;
    private int binaryBytes;
    private boolean initStatus = false;
    private int osOptions;
    
    private int bytesKaffinity = 0;
    private int bytesGroupAffinity = 0;
    private int bytesGroupInfo = 0;
    private int bytesStruc = 0;
    private final static int GROUP_OFFSET = 32;
    
    @Override public void setBinary( long[] x )
    {
        binaryDump = x;
    }
    
    @Override public long[] getBinary()
    { 
        return binaryDump;
    }
    
    @Override public boolean initBinary( OS_TYPE osType, int osOpt )
    {
        initStatus = false;
        osOptions = osOpt;    // 0=Standard topology, 1=Extended topology.
        
        if ( osType == null )
        {
            return false;
        }
        else switch ( osType )
        {
            case WIN32:
                bytesKaffinity = 4;
                bytesGroupAffinity = 12;
                bytesGroupInfo = 44;
                bytesStruc = 24;
                break;
            case WIN64:
                bytesKaffinity = 8;
                bytesGroupAffinity = 16;
                bytesGroupInfo = 48;
                bytesStruc = 32;
                break;
            default:
                return false;
        }
        
        if ( ( binaryDump != null )&&( binaryDump.length > 0 ) )
        {
            binaryStatus = (int)(binaryDump[0] >>> 32);
            binaryBytes = (int)(binaryDump[0] & 0xFFFFFFFFL);
            initStatus = ( binaryStatus == 0 )&&( binaryBytes > 0 )&&
                         ( binaryBytes <= binaryDump.length * 8 );
        }
        return initStatus;
    }

    @Override public boolean parseBinary()
    {
        if( ( !initStatus )||( bytesKaffinity <= 0 ) ) return false;
        
        boolean status = true;
        final int HEADER_SIZE = 16;

    // Part 1 = Hex dump of results GetLogicalProcessorInformationEx().
    
        ArrayList<String[]> dumpLines = new ArrayList<>();
        int offset = HEADER_SIZE;
        int limit = binaryBytes + HEADER_SIZE;
        
        while(( offset < limit ) && status)
        {
            String[] line = new String[17];
            for(int i=0; i<17; i++)
            {
                line[i] = "";
            }
            line[0] = String.format( "%08X", ( offset - HEADER_SIZE ));
            for(int i=0; i<16; i++)
            {
                Long data = getVariableSize( binaryDump, offset, 1 );
                if(data == null)
                {
                    status = false;
                    break;
                }
                line[i + 1] = String.format( "%02X", data );
                offset++;
                if( offset >= limit ) break;
            }
            dumpLines.add( line );
        }
        String[][] dump = dumpLines.toArray( new String[dumpLines.size()][] );
        
    // Part 2 = Topology entries list by GetLogicalProcessorInformationEx().
    // Part 2.1 = Build topology entries.
    
        ArrayList<TopologyRelationship> entries = new ArrayList<>();
        offset = HEADER_SIZE;

        if( osOptions == 0 )
        {
            status = interpreterGlpi( status, HEADER_SIZE, 
                bytesStruc, bytesKaffinity, 
                binaryDump, offset, limit, entries );
        }
        else
        {
            status = interpreterGlpiEx( status, HEADER_SIZE, GROUP_OFFSET,
                bytesKaffinity, bytesGroupAffinity, bytesGroupInfo,
                binaryDump, offset, limit, entries );
        }
        
    // Part 2.2 = Build string arrays from topology entries list.
    
        ArrayList<String> aShortNames = new ArrayList<>();
        ArrayList<String> aLongNames  = new ArrayList<>();
        ArrayList<String[]> aListsUps = new ArrayList<>();
        ArrayList<String[][]> aLists  = new ArrayList<>();
        ArrayList<String[]> aDumpsUps = new ArrayList<>();
        ArrayList<String[][]> aDumps  = new ArrayList<>();
                
        nCore = 0;
        nNode = 0;
        nCache = 0;
        nPackage = 0;
        nGroup = 0;
        nDie = 0;
        nNodeEx = 0;
        nModule = 0;
        
        nThread = 0;

        for( TopologyRelationship entry : entries )
        {
            ArrayList<String[]> entryList = new ArrayList<>();
            for ( String colName : LIST_TABLE_BEGIN_WINDOWS )
            {
                entryList.add( new String[]{ colName, "" });
            }
            
            if ( entry instanceof EntryProcessorCore )
            {
                EntryProcessorCore e = (EntryProcessorCore)entry;
                aShortNames.add( SN_PROCESSOR_CORE + nCore  + "]" );
                aLongNames.add( LN_PROCESSOR_CORE );
                entryList.get( 0 )[1] = String.format("%08Xh", e.offset );
                entryList.get( 1 )[1] = "" + ( nCore++ );
                entryList.get( 2 )[1] = LN_PROCESSOR_CORE;
                entryList.get( 3 )[1] = bitmapString( e.groups );
                entryList.add( new String[] { "SMT flag", "" + e.flags } );
                entryList.add( new String[] 
                    { "Efficiency class", "" + e.efficiencyClass } );

                int[][] affinityList = affinityCount( e.groups );
                nThread += affinityList.length;
            }
            
            else if ( entry instanceof EntryNumaNode )
            {
                EntryNumaNode e = (EntryNumaNode)entry;
                aShortNames.add( SN_NUMA_NODE + ( nNode ) + "]"  );
                aLongNames.add( LN_NUMA_NODE );
                entryList.get( 0 )[1] = String.format("%08Xh", e.offset );
                entryList.get( 1 )[1] = "" + ( nNode++ );
                entryList.get( 2 )[1] = LN_NUMA_NODE;
                entryList.get( 3 )[1] = bitmapString( e.groups );
                entryList.add( new String[] { "Node number", "" + e.nodeNumber } );
            }
            
            else if ( entry instanceof EntryCache )
            {
                EntryCache e = (EntryCache)entry;
                String tempS;
                PROCESSOR_CACHE_TYPE_WINDOWS t = e.type;
                int ti = t.ordinal();
                if( ti < N_CACHE_TYPES_WINDOWS.length )
                {
                    tempS = N_CACHE_TYPES_WINDOWS[ti];
                }
                else
                {
                    tempS = "?";
                }
                String longNameCache = 
                        String.format( "L%d %s %s" , e.level, tempS, LN_CACHE );
                aShortNames.add( SN_CACHE + ( nCache ) + "]"  );
                aLongNames.add( longNameCache );
                entryList.get( 0 )[1] = String.format("%08Xh", e.offset );
                entryList.get( 1 )[1] = "" + ( nCache++ );
                entryList.get( 2 )[1] = LN_CACHE;
                entryList.get( 3 )[1] = bitmapString( e.groups );
                entryList.add( new String[] { "Cache level", "L" + e.level } );
                entryList.add( new String[] { "Cache type", tempS } );
                // entryList.add( new String[] { "Cache size", e.cacheSize / 1024 + " KB" } );
                String sizeStr = writeMemorySize( e.cacheSize, HelperMemorySize.UNITS.AUTO );
                entryList.add( new String[] { "Cache size", sizeStr } );
                entryList.add( new String[] { "Line size", e.lineSize + " Bytes" } );
                if( e.associativity == 0xFF )
                {
                    tempS = "Full-associative";
                }
                else
                {
                    tempS = String.format("%d-way", e.associativity );
                }
                entryList.add( new String[] { "Associativity", tempS } );
            }
            
            else if ( entry instanceof EntryProcessorPackage )
            {
                EntryProcessorPackage e = (EntryProcessorPackage)entry;
                aShortNames.add( SN_PROCESSOR_PACKAGE + ( nPackage ) + "]"  );
                aLongNames.add( LN_PROCESSOR_PACKAGE );
                entryList.get( 0 )[1] = String.format("%08Xh", e.offset );
                entryList.get( 1 )[1] = "" + ( nPackage++ );
                entryList.get( 2 )[1] = LN_PROCESSOR_PACKAGE;
                entryList.get( 3 )[1] = bitmapString( e.groups );
            }
            
            else if ( entry instanceof EntryGroup )
            {
                EntryGroup e = (EntryGroup)entry;
                aShortNames.add( SN_PROCESSOR_GROUP + ( nGroup ) + "]"  );
                aLongNames.add( LN_PROCESSOR_GROUP );
                entryList.get( 0 )[1] = String.format("%08Xh", e.offset );
                entryList.get( 1 )[1] = "" + ( nGroup++ );
                entryList.get( 2 )[1] = LN_PROCESSOR_GROUP;
                entryList.get( 3 )[1] = bitmapString( e.ginfo );
            }
            
            else if ( entry instanceof EntryProcessorDie )
            {
                EntryProcessorDie e = (EntryProcessorDie)entry;
                aShortNames.add( SN_PROCESSOR_DIE + ( nDie++ ) + "]"  );
                aLongNames.add( LN_PROCESSOR_DIE );
                // Reserved.
            }
            
            else if ( entry instanceof EntryNumaNodeEx )
            {
                EntryNumaNodeEx e = (EntryNumaNodeEx)entry;
                aShortNames.add( SN_NUMA_NODE_EX + ( nNodeEx++ ) + "]"  );
                aLongNames.add( LN_NUMA_NODE_EX );
                // Reserved.
            }
            
            else if ( entry instanceof EntryProcessorModule )
            {
                EntryProcessorModule e = (EntryProcessorModule)entry;
                aShortNames.add( SN_PROCESSOR_MODULE + ( nModule++ ) + "]" );
                aLongNames.add( LN_PROCESSOR_MODULE );
                // Reserved.
            }
            
            else
            {
                aShortNames.add( SN_UNKNOWN + "]");
                aLongNames.add( LN_UNKNOWN + "]");
                // Reserved.
            }

            aListsUps.add( LIST_UP );
            aLists.add( entryList.toArray( new String[entryList.size()][]) );
            aDumpsUps.add( DUMP_UP );
            aDumps.add( dump );
        }
        
        // Additional cycle for generating threads entries for cores.
        int i = 0;
        for( TopologyRelationship entry : entries )
        {
            if ( entry instanceof EntryProcessorCore )
            {
                EntryProcessorCore e = (EntryProcessorCore)entry;
                int[][] affinityList = affinityCount( e.groups );
                for( int j=0; j<affinityList.length; j++ )
                {
                    ArrayList<String[]> entryList = new ArrayList<>();
                    for ( String colName : LIST_TABLE_BEGIN_WINDOWS )
                    {
                        entryList.add( new String[]{ colName, "" });
                    }
                    
                    aShortNames.add( SN_PROCESSOR_THREAD + i + "." + j + "]");
                    aLongNames.add( LN_PROCESSOR_THREAD );
                    entryList.get( 0 )[1] = String.format("%08Xh", e.offset );
                    entryList.get( 1 )[1] = "" + j;
                    entryList.get( 2 )[1] = LN_PROCESSOR_THREAD;
                    entryList.get( 3 )[1] = "" + affinityList[j][0];
                    entryList.add( new String[] { "SMT flag", "" + e.flags } );
                    entryList.add( new String[] 
                        { "Efficiency class", "" + e.efficiencyClass } );
                    
                    aListsUps.add( LIST_UP );
                    aLists.add
                        ( entryList.toArray( new String[entryList.size()][]) );
                    aDumpsUps.add( DUMP_UP );
                    aDumps.add( dump );
                }
                i++;
            }
        }

    // Verify results and build output.
    
        int n1 = aShortNames.size();
        int n2 = aLongNames.size();
        int n3 = aListsUps.size();
        int n4 = aLists.size();
        int n5 = aDumpsUps.size();
        int n6 = aDumps.size();
        
        shortNames = aShortNames.toArray( new String[n1] );
        longNames  = aLongNames.toArray ( new String[n2] );
        listsUps   = aListsUps.toArray( new String[n3][] );
        lists      = aLists.toArray( new String[n4][][] );
        dumpsUps   = aDumpsUps.toArray( new String[n5][] );
        dumps      = aDumps.toArray( new String[n6][][] );
        
        return ( status && (n1 > 0)&&
                 (n1 == n2)&&(n2 == n3)&&(n3 == n4)&&(n4 == n5)&&(n5 == n6) );
    }
    
    private final static String SUMMARY_METHOD_VALUE =
        "GetLogicalProcessorInformation()";
    private final static String SUMMARY_METHOD_VALUE_EX =
        "GetLogicalProcessorInformationEx()";

    @Override public String[][] getSummaryAddStrings()
    { 
        String methodName = null;
        if ( ( initStatus )&&( osOptions == 0 ) )
        {
            methodName = SUMMARY_METHOD_VALUE;
        }
        else if ( ( initStatus )&&( osOptions == 1 ) )
        {
            methodName = SUMMARY_METHOD_VALUE_EX;
        }
        String[][] summary = null;
        if ( methodName != null )
        {
            ArrayList<String[]> a = new ArrayList<>();
            a.add( new String[]{ S_METHOD_NAME, methodName } );
            a.add( new String[]{ S_CPU, 
                String.format( "%d cores, %d threads", nCore, nThread ) } );
            a.add( new String[]{ S_DOMAINS, "" + nNode } );
            a.add( new String[]{ S_GROUPS, "" + nGroup } );
            a.add( new String[]{ S_PACKAGES, "" + nPackage } );
            summary = a.isEmpty() ? null : a.toArray( new String[a.size()][] );
        }
        return summary;
    }

    
}
