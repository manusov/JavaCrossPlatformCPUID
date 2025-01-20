/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class provides helper methods for building summary information
strings after parsing platform topology data. Linux variant.

*/

package cpuidv3.serviceosmplinux;

import cpuidv3.serviceosmplinux.EntryCache.CACHE_TYPE_LINUX;
import java.util.ArrayList;

class HelperSummaryBuilding 
{
    static void removeDuplicates(ArrayList<Affinity> array)
    {
        if ((array != null) && (!array.isEmpty()))
        {
            boolean b = true;
            while(b)
            {
                b = false;
                int count = array.size();
                for(int i=0; i<count; i++)
                {
                    Affinity a1 = array.get(i);
                    for(int j=0; j<count; j++)
                    {
                        Affinity a2 = array.get(j);
                        if ((i != j) && (a1 != null) && (a2 != null) && (a1.isMaskEqual(a2)))
                        {
                            array.remove(j);
                            b = true;
                        }
                        if(b) break;
                    }
                    if(b) break;
                }
            }
        }
    }

    static void removeDuplicatesCaches(ArrayList<AffinityCache> array)
    {
        if ((array != null) && (!array.isEmpty()))
        {
            boolean b = true;
            while(b)
            {
                b = false;
                int count = array.size();
                for(int i=0; i<count; i++)
                {
                    AffinityCache a1 = array.get(i);
                    for(int j=0; j<count; j++)
                    {
                        AffinityCache a2 = array.get(j);
                        
                        CACHE_TYPE_LINUX t1 = a1.entryCache.type;
                        CACHE_TYPE_LINUX t2 = a2.entryCache.type;
                        int l1 = a1.entryCache.level;
                        int l2 = a2.entryCache.level;
                        
                        if ((i != j) && 
                            (t1 == t2) && (l1 == l2) && (a1.isMaskEqual(a2)))
                        {
                            array.remove(j);
                            b = true;
                        }
                        if(b) break;
                    }
                    if(b) break;
                }
            }
        }
    }
}
