/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Helper class for interpreting binary buffer after WinAPI function GLPI, means
GetLogicalProcessorInformation().
Interpreting binary data and generating entries list with TopologyRelationship
entries.

*/

package cpuidv3.serviceosmpwindows;
import static cpuidv3.serviceosmpwindows.HelperBinaryParsing.getVariableSize;
import java.util.ArrayList;

class HelperGlpi 
{
    static boolean interpreterGlpi
        ( boolean status, int HEADER_SIZE, 
          int bytesStruc, int bytesKaffinity, 
          long[] binaryDump, int offset, int limit,
          ArrayList<TopologyRelationship> entries )
    {
        while(( offset < limit ) && status)
        {
            int tempOffset = offset;
            Long entryAffinity = getVariableSize( binaryDump, tempOffset, bytesKaffinity );
            tempOffset += bytesKaffinity;
            Long entryType = getVariableSize( binaryDump, tempOffset, 4 );
            tempOffset += bytesKaffinity;
            if( entryType == null )
            {
                status = false;
                break;
            }

            
            int selector = entryType.intValue();
            switch ( selector )
            {
                case 0:    // 0 = Processor core.
                {
                    Long f = getVariableSize( binaryDump, tempOffset, 1 );
                    GroupAffinity[] gaff = new GroupAffinity[1];
                    gaff[0] = new GroupAffinity( entryAffinity, (short)0 );
                    entries.add( new EntryProcessorCore( offset - HEADER_SIZE,
                            f.byteValue(), (byte)0, (short)1, gaff ) );
                }
                break;
                
                case 1:    // 1 = NUMA node.
                {
                    Long n = getVariableSize( binaryDump, tempOffset, 4 );
                    GroupAffinity[] gaff = new GroupAffinity[1];
                    gaff[0] = new GroupAffinity( entryAffinity, (short)0 );
                    entries.add( new EntryNumaNode( offset - HEADER_SIZE,
                            n.intValue(), (short)1, gaff ) );
                }
                break;
                
                case 2:    // 2 = Cache.
                {
                    Long l = getVariableSize( binaryDump, tempOffset, 1 );
                    Long a = getVariableSize( binaryDump, tempOffset + 1, 1 );
                    Long ls = getVariableSize( binaryDump, tempOffset + 2, 2 );
                    Long cs = getVariableSize( binaryDump, tempOffset + 4, 4 );
                    Long t = getVariableSize( binaryDump, tempOffset + 8, 4 );
                    GroupAffinity[] gaff = new GroupAffinity[1];
                    gaff[0] = new GroupAffinity( entryAffinity, (short)0 );
                    byte level = l.byteValue();
                    byte associativity = a.byteValue();
                    short lineSize = ls.shortValue();
                    int cacheSize = cs.intValue();
                    int tIndex = t.intValue();
                    EntryCache.PROCESSOR_CACHE_TYPE_WINDOWS type;
                    if ( tIndex < EntryCache.PROCESSOR_CACHE_TYPE_WINDOWS.values().length )
                    {
                        type = EntryCache.PROCESSOR_CACHE_TYPE_WINDOWS.values()[tIndex];
                    }
                    else
                    {
                        type = EntryCache.PROCESSOR_CACHE_TYPE_WINDOWS.CacheUnknown;
                    }
                    entries.add( new EntryCache( offset - HEADER_SIZE, 
                              level, associativity, lineSize, cacheSize, type,
                              (short)1, gaff ) );
                }
                break;
                
                case 3:    // 3 = Processor package.
                {
                    GroupAffinity[] gaff = new GroupAffinity[1];
                    gaff[0] = new GroupAffinity( entryAffinity, (short)0 );
                    entries.add( new EntryProcessorPackage
                            ( offset - HEADER_SIZE, (short)1, gaff ) );
                }
                break;
            
                default:
                    break;
            }
            offset += bytesStruc;
        }        
        return status;
    }
}
