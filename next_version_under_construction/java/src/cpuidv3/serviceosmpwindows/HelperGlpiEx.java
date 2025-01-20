/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Helper class for interpreting binary buffer after WinAPI function GLPI EX, 
means GetLogicalProcessorInformationEx().
Interpreting binary data and generating entries list with TopologyRelationship
entries.

*/

package cpuidv3.serviceosmpwindows;
import static cpuidv3.serviceosmpwindows.HelperBinaryParsing.getVariableSize;
import java.util.ArrayList;

class HelperGlpiEx 
{
    static boolean interpreterGlpiEx
        ( boolean status, int HEADER_SIZE, int GROUP_OFFSET,
          int bytesKaffinity, int bytesGroupAffinity, int bytesGroupInfo,
          long[] binaryDump, int offset, int limit,
          ArrayList<TopologyRelationship> entries )
    {
        while(( offset < limit ) && status)
        {
            Long entryType = getVariableSize( binaryDump, offset, 4 );
            Long entrySize = getVariableSize( binaryDump, offset + 4, 4 );
            if((entryType == null) || (entrySize == null))
            {
                status = false;
                break;
            }
            
            int selector = entryType.intValue();
            switch ( selector )
            {
                case 0:    // 0 = Processor core.
                {    
                    Long f = getVariableSize( binaryDump, offset + 8, 1 );
                    Long e = getVariableSize( binaryDump, offset + 9, 1 );
                    Long g = getVariableSize( binaryDump, offset + 30, 2 );
                    if((f != null)&&(e != null)&&(g != null))
                    {
                        byte flags = f.byteValue();
                        byte efficiencyClass = e.byteValue();
                        short groupsCount = g.shortValue();
                        GroupAffinity[] gaff = new GroupAffinity[groupsCount];
                        int groupOffset = offset + GROUP_OFFSET;
                        for(int i=0; i<groupsCount; i++)
                        {
                            Long k = getVariableSize
                                ( binaryDump, groupOffset, bytesKaffinity );
                            Long r = getVariableSize
                                ( binaryDump, groupOffset + bytesKaffinity, 2 );
                            if((k != null)&&(r != null))
                            {
                                long kaffinity = k;
                                short group = r.shortValue();
                                gaff[i] = new GroupAffinity( kaffinity, group );
                            }
                            groupOffset += bytesGroupAffinity;
                        }
                        entries.add( new EntryProcessorCore
                            ( offset - HEADER_SIZE, flags, efficiencyClass, groupsCount, gaff ) );
                    }
                }
                break;
                
                case 1:    // 1 = NUMA node.
                {
                    Long n = getVariableSize( binaryDump, offset + 8, 4 );
                    Long g = getVariableSize( binaryDump, offset + 22, 2 );
                    if((n != null)&&(g != null))
                    {
                        int nodeNumber = n.intValue();
                        short groupsCount = g.shortValue();
                        if(groupsCount == 0)
                        {
                            groupsCount = 1;
                        }

                        GroupAffinity[] gaff = new GroupAffinity[groupsCount];
                        int groupOffset = offset + GROUP_OFFSET;
                        for(int i=0; i<groupsCount; i++)
                        {
                            Long k = getVariableSize
                                ( binaryDump, groupOffset, bytesKaffinity );
                            Long r = getVariableSize
                                ( binaryDump, groupOffset + bytesKaffinity, 2 );
                            if((k != null)&&(r != null))
                            {
                                long kaffinity = k;
                                short group = r.shortValue();
                                gaff[i] = new GroupAffinity(kaffinity, group);
                            }
                            groupOffset += bytesGroupAffinity;
                        }
                        entries.add( new EntryNumaNode
                            ( offset - HEADER_SIZE, nodeNumber, groupsCount, gaff ) );
                    }
                }
                break;
                
                case 2:    // 2 = Cache.
                {
                    Long l = getVariableSize( binaryDump, offset + 8, 1 );
                    Long a = getVariableSize( binaryDump, offset + 9, 1 );
                    Long ls = getVariableSize( binaryDump, offset + 10, 2 );
                    Long cs = getVariableSize( binaryDump, offset + 12, 4 );
                    Long t = getVariableSize( binaryDump, offset + 16, 4 );
                    Long g = getVariableSize( binaryDump, offset + 38, 2 );
                    if( (l != null)&&(a != null)&&(ls != null)&&
                        (cs != null)&&(t != null)&&(g != null) )
                    {
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
                        
                        short groupsCount = g.shortValue();
                        if(groupsCount == 0)
                        {
                            groupsCount = 1;
                        }
                        GroupAffinity[] gaff = new GroupAffinity[groupsCount];
                        int groupOffset = offset + 40;
                        for(int i=0; i<groupsCount; i++)
                        {
                            Long k = getVariableSize
                                ( binaryDump, groupOffset, bytesKaffinity );
                            Long r = getVariableSize
                                ( binaryDump, groupOffset + bytesKaffinity, 2 );
                            if((k != null)&&(r != null))
                            {
                                long kaffinity = k;
                                short group = r.shortValue();
                                gaff[i] = new GroupAffinity( kaffinity, group );
                            }
                        groupOffset += bytesGroupAffinity;
                        }
                        entries.add( new EntryCache
                            ( offset - HEADER_SIZE, 
                              level, associativity, lineSize, cacheSize, type,
                              groupsCount, gaff ) );
                    }
                }
                break;
                    
                case 3:    // 3 = Processor package.
                {
                    Long g = getVariableSize( binaryDump, offset + 30, 2 );
                    if(g != null)
                    {
                        short groupsCount = g.shortValue();
                        if(groupsCount == 0)
                        {
                            groupsCount = 1;
                        }
                        GroupAffinity[] gaff = new GroupAffinity[groupsCount];
                        int groupOffset = offset + GROUP_OFFSET;
                        for(int i=0; i<groupsCount; i++)
                        {
                            Long k = getVariableSize
                                ( binaryDump, groupOffset, bytesKaffinity );
                            Long r = getVariableSize
                                ( binaryDump, groupOffset + bytesKaffinity, 2 );
                            if((k != null)&&(r != null))
                            {
                                long kaffinity = k;
                                short group = r.shortValue();
                                gaff[i] = new GroupAffinity( kaffinity, group );
                            }
                        groupOffset += bytesGroupAffinity;
                        }
                        entries.add( new EntryProcessorPackage
                            ( offset - HEADER_SIZE, groupsCount, gaff ) );
                    }
                }
                break;
                    
                case 4:    // 4 = Processor group.
                {
                    Long maxG = getVariableSize( binaryDump, offset + 8, 2 );
                    Long actG = getVariableSize( binaryDump, offset + 10, 2 );
                    if( (maxG != null)&&(actG != null) )
                    {
                        short maxGroups = maxG.shortValue();
                        short actGroups = actG.shortValue();
                        int count = ( entrySize.intValue() - GROUP_OFFSET ) / bytesGroupAffinity;
                        GroupInfo[] groups = new GroupInfo[count];
                        int groupOffset = offset + GROUP_OFFSET;
                        for(int i=0; i<count; i++)
                        {
                            Long maxP = getVariableSize( binaryDump, groupOffset, 1);
                            Long actP = getVariableSize( binaryDump, groupOffset + 1, 1);
                            Long m = getVariableSize( binaryDump, groupOffset + 40, bytesKaffinity);
                            if( (maxP != null)&&(actP != null)&&(m != null) )
                            {
                                byte maxProcessors = maxP.byteValue();
                                byte activeProcessors = actP.byteValue();
                                long kaffinity = m;
                                groups[i] = new GroupInfo
                                    (maxProcessors, activeProcessors, kaffinity);
                                groupOffset += bytesGroupInfo;
                            }
                        }
                        entries.add( new EntryGroup( offset - HEADER_SIZE,
                                maxGroups, actGroups, groups));
                    }
                }
                break;
                    
                case 5:    // 5 = Processor die. This entry type yet reserved.
                    entries.add( new EntryProcessorDie( offset - HEADER_SIZE ));
                    break;
                    
                case 6:    // 6 = NUMA node extended. This entry type yet reserved.
                    entries.add( new EntryNumaNodeEx( offset - HEADER_SIZE ));
                    break;
                    
                case 7:    // 7 = Processor module. This entry type yet reserved.
                    entries.add( new EntryProcessorModule( offset - HEADER_SIZE ));
                    break;
                    
                default:    // >7 = Unknown topology entry ID.
                    break;
            }
            offset += entrySize;
        }
        return status;
    }
}
