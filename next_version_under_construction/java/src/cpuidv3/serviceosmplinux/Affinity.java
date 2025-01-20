/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Parent class for platform topology summary objects representation.
Summarized by affinity masks. Linux variant.

*/

package cpuidv3.serviceosmplinux;

public class Affinity 
{
    public final int affinity_id;
    public final long[] affinity_mask;
    
    public Affinity(int id, long[] mask)
    {
        affinity_id = id;
        affinity_mask = mask;
    }
    
    public boolean isMaskEqual(Affinity a)
    {
        if (a == null) return true;
        long[] mask = a.affinity_mask;
        if ( (mask == null) || (affinity_mask == null)) return true;
        
        boolean b = true;
        int count1 = affinity_mask.length;
        int count2 = mask.length;
        int countBoth = Integer.min(count1, count2);
        int index;
        
        for(index=0; index<countBoth; index++)
        {
            if(affinity_mask[index] != mask[index] )
            {
                b = false;
                break;
            }
        }
        
        if(b && (count1 > count2))
        {
            for(; index<count1; index++)
            {
                if(affinity_mask[index] != 0)
                {
                    b = false;
                    break;
                }
            }
        }
        
        else if( b && (count2 > count1))
        {
            for(; index<count2; index++)
            {
                if(mask[index] != 0)
                {
                    b = false;
                    break;
                }
            }
        }
        
        return b;
    }

    public boolean isMaskCross(long[] mask)
    {
        if ((mask == null) || (affinity_mask == null)) return true;
        
        boolean b = false;
        int count1 = affinity_mask.length;
        int count2 = mask.length;
        int countBoth = Integer.min(count1, count2);
        
        for(int index=0; index<countBoth; index++)
        {
            if((affinity_mask[index] & mask[index]) != 0)
            {
                b = true;
                break;
            }
        }

        return b;
    }

}
