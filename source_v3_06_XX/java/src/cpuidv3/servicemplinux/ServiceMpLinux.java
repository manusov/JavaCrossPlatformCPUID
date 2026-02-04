/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Driver class for interpreting topology information, provided by OS API.
Variant for Linux, based on /sysfs virtual file system as platform data source.
Provides text arrays = F ( Topology information from Linux API ).

*/

package cpuidv3.servicemplinux;

import cpuidv3.pal.PAL.OS_TYPE;
import cpuidv3.servicemp.ServiceMp;
import static cpuidv3.servicemp.HelperBitmap.helperBitmapString;
import static cpuidv3.servicemp.HelperBitmap.longsCount;
import cpuidv3.servicemp.HelperMemorySize.UNITS;
import static cpuidv3.servicemp.HelperMemorySize.writeMemorySize;
import cpuidv3.servicemplinux.EntryCache.CACHE_TYPE_LINUX;
import static cpuidv3.servicemplinux.HelperSummaryBuilding.removeDuplicates;
import static cpuidv3.servicemplinux.HelperSummaryBuilding.removeDuplicatesCaches;
import static cpuidv3.servicemplinux.HelperSysfsParsing.SYS_CPU_PATH;
import static cpuidv3.servicemplinux.HelperSysfsParsing.buildEntry;
import static cpuidv3.servicemplinux.HelperSysfsParsing.decodeList;
import static cpuidv3.servicemplinux.HelperSysfsParsing.readParameter;
import java.util.ArrayList;

public class ServiceMpLinux extends ServiceMp
{
    private boolean initStatus = false;
    private int[] cpuIndexesList;
    
    @Override public boolean initBinary( OS_TYPE osType, int osOpt )
    {
        String cpuList = readParameter( SYS_CPU_PATH + "online/" );
        cpuIndexesList = decodeList( cpuList );
        initStatus = ( cpuIndexesList != null )&&( cpuIndexesList.length > 0 );
        return initStatus;
    }
    
    @Override public boolean parseBinary()
    {
        if ( !initStatus ) return false;
        
        ArrayList<EntryProcessor> processors = new ArrayList<>();
        int cpuCount = cpuIndexesList.length;
        for(int i=0; i<cpuCount; i++)
        {
            EntryProcessor e = buildEntry( cpuIndexesList[i] );
            processors.add(e);
        }

        // Part 1 = Hex dump, not used for Linux variant.
        
        String[] dumpLines = new String[17];
        dumpLines[0] = "n/a";
        for( int i=1; i<17; i++ )
        {
            dumpLines[i] = "";
        }
        String[][] dump = new String[][] { dumpLines };
        
        // Part 2.1 = Build topology entries (affinity objects).
        
        ArrayList<Affinity> cores = new ArrayList<>();
        ArrayList<Affinity> domains = new ArrayList<>();
        ArrayList<Affinity> sockets = new ArrayList<>();
        ArrayList<AffinityCache> caches = new ArrayList<>();
                
        for( int i=0; i<cpuCount; i++ )
        {
            long[] coreSiblings = processors.get(i).thread_siblings;
            int coreIndex = processors.get(i).core_id;
            AffinityCore core = new AffinityCore(coreIndex, coreSiblings);
            cores.add(core);
            
            long[] domainMask = processors.get(i).numa_node_cpumap;
            int domainIndex = processors.get(i).numa_node_id;
            AffinityDomain domain = new AffinityDomain(domainIndex, domainMask);
            domains.add(domain);
            
            long[] socketMask = processors.get(i).core_siblings;
            int socketIndex = processors.get(i).physical_package_id;
            AffinitySocket socket = new AffinitySocket(socketIndex, socketMask);
            sockets.add(socket);

            EntryCache[] perCpuCaches = processors.get(i).caches_visible;
            int perCpuCacheCount = perCpuCaches.length;
            for(int j=0; j<perCpuCacheCount; j++)
            {
                long[] cacheMask = perCpuCaches[j].shared_cpu_map;
                int cacheIndex = perCpuCaches[j].entry_id;
                AffinityCache cache = 
                    new AffinityCache(cacheIndex, cacheMask, perCpuCaches[j]);
                caches.add(cache);
            }
        }
        
        // Part 2.2 = Remove duplicates for affinity objects.
        
        removeDuplicates( cores );
        removeDuplicates( domains );
        removeDuplicates( sockets );
        removeDuplicatesCaches( caches );
        
        // Part 2.3 = Build string arrays from topology entries list.
        
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
        
        nThread = 0;

        for( Affinity a : cores )
        {
            AffinityCore core = (AffinityCore)a;
            ArrayList<String[]> entryList = new ArrayList<>();
            for ( String colName : LIST_TABLE_BEGIN_LINUX )
            {
                entryList.add(new String[]{ colName, "" });
            }
            aShortNames.add( SN_PROCESSOR_CORE + nCore  + "]" );
            aLongNames.add( LN_PROCESSOR_CORE );
            entryList.get( 0 )[1] = "" + ( nCore++ );
            entryList.get( 1 )[1] = LN_PROCESSOR_CORE;
            entryList.get( 2 )[1] = helperBitmapString( core.affinity_mask );

            int[][] affinityList = longsCount( core.affinity_mask );
            nThread += affinityList.length;
            
            aListsUps.add( LIST_UP );
            aLists.add( entryList.toArray( new String[entryList.size()][]) );
            aDumpsUps.add( DUMP_UP );
            aDumps.add( dump );
        }
        
        int i=0;
        for( Affinity a : cores )
        {
            AffinityCore core = (AffinityCore)a;
            int[][] affinityList = longsCount( core.affinity_mask );
            for( int j=0; j<affinityList.length; j++ )
            {
                ArrayList<String[]> entryList = new ArrayList<>();
                for ( String colName : LIST_TABLE_BEGIN_LINUX )
                {
                    entryList.add(new String[]{ colName, "" });
                }
                aShortNames.add( SN_PROCESSOR_THREAD + i + "." + j + "]");
                aLongNames.add( LN_PROCESSOR_THREAD );
                entryList.get( 0 )[1] = "" + j;
                entryList.get( 1 )[1] = LN_PROCESSOR_THREAD;
                entryList.get( 2 )[1] = 
                    helperBitmapString( core.affinity_mask );
                aListsUps.add( LIST_UP );
                aLists.add( entryList.toArray( new String[entryList.size()][]) );
                aDumpsUps.add( DUMP_UP );
                aDumps.add( dump );
            }
            i++;
        }
        
        for( Affinity a : caches )
        {
            AffinityCache cache = (AffinityCache)a;
            ArrayList<String[]> entryList = new ArrayList<>();
            for ( String colName : LIST_TABLE_BEGIN_LINUX )
            {
                entryList.add(new String[]{ colName, "" });
            }
            String tempS;
            EntryCache e = cache.entryCache;
            CACHE_TYPE_LINUX t = e.type;
            int ti = t.ordinal();
            if( ti < N_CACHE_TYPES_LINUX.length )
            {
                tempS = N_CACHE_TYPES_LINUX[ti];
            }
            else
            {
                tempS = "?";
            }
            String longNameCache = 
                String.format( "L%d %s %s" , e.level, tempS, LN_CACHE );
            aShortNames.add( SN_CACHE + nCache  + "]" );
            aLongNames.add( longNameCache );
            entryList.get( 0 )[1] = "" + ( nCache++ );
            entryList.get( 1 )[1] = LN_CACHE;
            entryList.get( 2 )[1] = helperBitmapString( cache.affinity_mask );
            entryList.add( new String[] { "Cache level", "L" + e.level } );
            entryList.add( new String[] { "Cache type", tempS } );
            // entryList.add( new String[] { "Cache size", e.size / 1024 + " KB" } );
            String sizeStr = writeMemorySize( e.size, UNITS.AUTO );
            entryList.add( new String[] { "Cache size", sizeStr } );
            entryList.add( new String[] { "Line size", e.coherency_line_size + " Bytes" } );
            if( e.ways_of_associativity == 0xFF )
            {
                tempS = "Full-associative";
            }
            else
            {
                tempS = String.format("%d-way", e.ways_of_associativity );
            }
            entryList.add( new String[] { "Associativity", tempS } );
            aListsUps.add( LIST_UP );
            aLists.add( entryList.toArray( new String[entryList.size()][]) );
            aDumpsUps.add( DUMP_UP );
            aDumps.add( dump );
        }
        
        for( Affinity a : domains )
        {
            AffinityDomain domain = (AffinityDomain)a;
            ArrayList<String[]> entryList = new ArrayList<>();
            for ( String colName : LIST_TABLE_BEGIN_LINUX )
            {
                entryList.add(new String[]{ colName, "" });
            }
            aShortNames.add( SN_NUMA_NODE + nNode + "]" );
            aLongNames.add( LN_NUMA_NODE );
            entryList.get( 0 )[1] = "" + ( nNode++ );
            entryList.get( 1 )[1] = LN_NUMA_NODE;
            entryList.get( 2 )[1] = helperBitmapString( domain.affinity_mask );
            aListsUps.add( LIST_UP );
            aLists.add( entryList.toArray( new String[entryList.size()][]) );
            aDumpsUps.add( DUMP_UP );
            aDumps.add( dump );
        }
        
        for( Affinity a : sockets )
        {
            AffinitySocket socket = (AffinitySocket)a;
            ArrayList<String[]> entryList = new ArrayList<>();
            for ( String colName : LIST_TABLE_BEGIN_LINUX )
            {
                entryList.add(new String[]{ colName, "" });
            }
            aShortNames.add( SN_PROCESSOR_PACKAGE + nPackage + "]" );
            aLongNames.add( LN_PROCESSOR_PACKAGE );
            entryList.get( 0 )[1] = "" + ( nPackage++ );
            entryList.get( 1 )[1] = LN_PROCESSOR_PACKAGE;
            entryList.get( 2 )[1] = helperBitmapString( socket.affinity_mask );
            aListsUps.add( LIST_UP );
            aLists.add( entryList.toArray( new String[entryList.size()][]) );
            aDumpsUps.add( DUMP_UP );
            aDumps.add( dump );
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
        
        return ( (n1 > 0)&&
                 (n1 == n2)&&(n2 == n3)&&(n3 == n4)&&(n4 == n5)&&(n5 == n6) );
    }
    
    private final static String SUMMARY_METHOD_VALUE =
        "/sysfs virtual file system + SYS_GETAFFINITY";
   
    @Override public String[][] getSummaryAddStrings()
    { 
        String[][] summary = null;
        if ( initStatus )
        {
            ArrayList<String[]> a = new ArrayList<>();
            a.add( new String[]{ S_METHOD_NAME, SUMMARY_METHOD_VALUE } );
            a.add( new String[]{ S_CPU, 
            String.format( "%d cores, %d threads", nCore, nThread ) } );
            a.add( new String[]{ S_DOMAINS, "" + nNode } );
            a.add( new String[]{ S_PACKAGES, "" + nPackage } );
            summary = a.isEmpty() ? null : a.toArray( new String[a.size()][] );
        }
        return summary;
    }
}
