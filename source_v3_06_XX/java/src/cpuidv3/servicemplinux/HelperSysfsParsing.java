/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class provides helper methods for parsing platform topology data.
Linux variant, interpreting system information results from
virtual file system "/sysfs", used at Linux.

*/

package cpuidv3.servicemplinux;

import cpuidv3.servicemplinux.EntryCache.CACHE_TYPE_LINUX;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class HelperSysfsParsing 
{
    final static String SYS_CPU_PATH = "/sys/devices/system/cpu/";
    final static int FILE_BUFFER_SIZE = 4096;
    final static int MAX_NODE = 255;
    final static int MAX_CACHE_INDEX = 4;
    
    private final static class CacheTypeDetector
    {
        EntryCache.CACHE_TYPE_LINUX type;
        String name;
        CacheTypeDetector( EntryCache.CACHE_TYPE_LINUX t, String n )
        {
            type = t;
            name = n;
        }
    }
    
    private final static CacheTypeDetector[] CACHE_TYPE_DETECTORS = 
    { 
        new CacheTypeDetector
            ( EntryCache.CACHE_TYPE_LINUX.DATA        , "Data"        ),
        new CacheTypeDetector
            ( EntryCache.CACHE_TYPE_LINUX.INSTRUCTION , "Instruction" ),
        new CacheTypeDetector
            ( EntryCache.CACHE_TYPE_LINUX.UNIFIED     , "Unified"     ),
        new CacheTypeDetector
            ( EntryCache.CACHE_TYPE_LINUX.TRACE       , "Trace"       )
    };

    static EntryProcessor buildEntry(int cpuIndex)
    {
        EntryProcessor entry = new EntryProcessor();
        entry.entry_id = cpuIndex;
        String pathTopology = SYS_CPU_PATH + "cpu" + cpuIndex + "/topology/";
        
        String s = readParameter(pathTopology + "core_id/");
        entry.core_id = decodeValue(s);
        
        s = readParameter(pathTopology + "physical_package_id");
        entry.physical_package_id = decodeValue(s);
        
        s = readParameter(pathTopology + "core_siblings_list");
        entry.core_siblings = decodeListMap(s);  // TODO. Use core_siblings if list not found.
        
        s = readParameter(pathTopology + "thread_siblings_list");
        entry.thread_siblings = decodeListMap(s);  // TODO. Use thread_siblings if list not found.
        
        for(int nodeIndex=0; nodeIndex<=MAX_NODE; nodeIndex++)
        {
            String pathNumaNode = SYS_CPU_PATH + "cpu" + cpuIndex + 
                    "/node" + nodeIndex + "/cpulist";
            s = readParameter(pathNumaNode);
            if (s != null)
            {
                entry.numa_node_id = nodeIndex;
                entry.numa_node_cpumap = decodeListMap(s);
                break;
            }
        }
        
        ArrayList<EntryCache> caches = new ArrayList<>();
        for(int cacheIndex=0; cacheIndex<=MAX_CACHE_INDEX; cacheIndex++)
        {
            String pathCache = SYS_CPU_PATH + "cpu" + cpuIndex + 
                    "/cache/index" + cacheIndex;
            s = readParameter(pathCache  + "/type");
            if (s != null)
            {
                EntryCache entryCache = new EntryCache();
                int n = CACHE_TYPE_DETECTORS.length;
                for(int i=0; i<n; i++)
                {
                    if ( s.equals( CACHE_TYPE_DETECTORS[i].name ) )
                    {
                        entryCache.type = CACHE_TYPE_DETECTORS[i].type;
                        break;
                    }
                    if (entryCache.type == null)
                    {
                        entryCache.type = CACHE_TYPE_LINUX.UNKNOWN;
                    }
                }
                
                s  = readParameter(pathCache + "/level");
                entryCache.level = decodeValue(s);
                
                s = readParameter(pathCache + "/size");
                entryCache.size = decodeSize(s);
                
                s = readParameter(pathCache + "/coherency_line_size");
                entryCache.coherency_line_size = (int)decodeSize(s);
                
                s = readParameter(pathCache + "/number_of_sets");
                entryCache.number_of_sets = (int)decodeSize(s);
                
                s = readParameter(pathCache + "/ways_of_associativity");
                entryCache.ways_of_associativity = (int)decodeSize(s);
                
                s = readParameter(pathCache + "/physical_line_partition");
                entryCache.physical_line_partition = (int)decodeSize(s);
                
                s = readParameter(pathCache + "/shared_cpu_list");
                entryCache.shared_cpu_map = decodeListMap(s);
                
                caches.add(entryCache);
            }
        }
        int n = caches.size();
        entry.caches_visible = new EntryCache[n];
        for(int i=0; i<n; i++)
        {
            entry.caches_visible[i] = caches.get(i);
        }
        
        return entry;
    }
    
    static String readParameter(String fileName)
    {
        StringBuilder fileData = null;
        File file = new File(fileName);
        if ( file.exists() && ! file.isDirectory() )
        {
            FileInputStream fis;
            int readSize;
            byte[] array = new byte[FILE_BUFFER_SIZE];
            try 
            { 
                fis = new FileInputStream( file );
                readSize = fis.read( array ); 
                fis.close();
            }
            catch ( IOException e ) { readSize = 0; }
            char c1;
            if ( readSize>0 )
            {
                fileData = new StringBuilder( "" );
                for ( int j=0; j<readSize; j++ )
                { 
                    c1 = (char)array[j];
                    if ( ! ( ( c1 == '\n' ) || (c1 == '\r' ) ) )
                    { 
                        fileData.append( c1 ); 
                    }
                }
            }
        }
        if ( fileData == null ) return null;
        return fileData.toString();
    }
    
    static int[] decodeList(String list)
    {
        if (list == null)
        {
            return new int[0];
        }
        
        int lstCount = list.length();
        List<StringBuilder> sbLst = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<lstCount; i++)
        {
            char c = list.charAt(i);
            if (Character.isDigit(c))
            {
                sb.append(c);
            }
            else if (sb.length() > 0)
            {
                sbLst.add(sb);
                if(c == '-')
                {
                    sb = new StringBuilder();
                    sb.append("-1");
                    sbLst.add(sb);
                }
                sb = new StringBuilder();
            }
        }
        if (sb.length() > 0)
        {
            sbLst.add(sb);
        }

        int numCount = sbLst.size();
        int a[] = new int[numCount];
        for(int i=0; i<numCount; i++)
        {
            a[i] = Integer.parseInt(sbLst.get(i).toString());
        }
        
        ArrayList<Integer> b = new ArrayList<>();
        for(int i=0; i<numCount; i++)
        {
            int n = a[i];
            if(n>=0)
            {
                b.add(n);
            }
            else
            {
                int left = a[i-1];
                int right = a[i+1];
                for(int j=left+1; j<right; j++)
                {
                    b.add(j);
                }
            }
        }
        
        int total = b.size();
        int[] c = new int[total];
        for(int i=0; i<total; i++)
        {
            c[i] = b.get(i);
        }
        return c;
    }

    static int decodeValue(String s)
    {
        int a = -1;
        if ( s != null )
        {
            try { a = Integer.parseInt(s); }
            catch (NumberFormatException e) { a = -1; }
        }
        return a;
    }
    
    static long decodeSize(String memSize)
    {
        StringBuilder sb = new StringBuilder();
        int count = memSize.length();
        long multiplier = 1L;
        for (int i=0; i<count; i++)
        {
            char c = memSize.charAt(i);
            if ((Character.isDigit(c)) || (c == '-'))
            {
                sb.append(c);
            }
            else if (multiplier > 0)
            {
                switch(c)
                {
                    case 'K':
                        multiplier = 1024L;
                        break;
                    case 'M':
                        multiplier = 1024*1024L;
                        break;
                    case 'G':
                        multiplier = 1024*1024*1024L;
                        break;
                    case 'T':
                        multiplier = 1024*1024*1024*1024L;
                        break;
                    default:
                        multiplier = -1L;
                        break;
                }
            }
        }

        long result = -1;
        try
        {
            if(sb.length() > 0)
            {
                long mantissa = Long.parseLong(sb.toString()); 
                if (mantissa >= 0)
                {
                    result = mantissa * multiplier;
                }
            }
        }
        catch (NumberFormatException e) { }
        
        return result;
    }
    
    static long[] decodeListMap(String list)
    {
        int[] a = decodeList(list);
        int amax = a[0];
        if ((a != null) && (a.length > 0))
        {
            for(int i=0; i<a.length; i++)
            {
                if (a[i] > amax)
                {
                    amax = a[i];
                }
            }
        }
        
        int argumentLength = 0;
        if(a != null )
        {
            argumentLength = a.length;
        }
        int resultLength = amax / 64 + 1;
        long[] result = new long[resultLength];
        
        for(int i=0; i<argumentLength; i++)
        {
            int index1 = a[i] & 0x3F;
            int index2 = a[i] >> 6;
            long mask = 1L << index1;
            result[index2] = result[index2] | mask;
        }
        
        return result;
    }

    static long[] decodeMap(String hexMap)
    {
        StringBuilder sb = new StringBuilder();
        int charCount = hexMap.length();
        boolean appendZero = false;
        
        for(int i=0; i<charCount; i++)
        {
            char c = hexMap.charAt(i);
            boolean b1 = Character.isDigit(c);
            boolean b2 = ((c >= 'a') && (c <= 'f'));
            boolean b3 = ((c >= 'A') && (c <= 'F'));
            if( b1 || b2 || b3 )
            {
                if (( c != '0') || appendZero)
                {
                    sb.append(c);
                    appendZero = true;
                }
            }
        }
        
        charCount = sb.length();
        int longCount = charCount / 16;
        if ( charCount % 16 > 0)
        {
            longCount++;
        }
        long[] result = new long[longCount];
        for(int i=0; i<charCount; i++)
        {
            String s = sb.substring(i, i + 1);
            long digit = Long.parseUnsignedLong(s, 16);
            
            for(int j=0; j<longCount; j++)
            {
                long previous = result[j] >>> 60;
                result[j] = ( result[j] << 4 ) + digit;
                digit = previous;
            }
        }
        return result;
    }
}
