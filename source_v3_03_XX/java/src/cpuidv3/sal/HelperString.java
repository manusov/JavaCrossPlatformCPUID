/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class with static helpers methods for long strings
representations at tables as multiple strings.

*/

package cpuidv3.sal;

import java.util.ArrayList;

class HelperString 
{
    static String[] helperSeparate
        ( String s, String separator, String terminator, int lineMax )
    {
        ArrayList<String> resultSplit = new ArrayList<>();
        if(( s != null )&&(s.length() > 1)&&(s.contains( separator )))
        {
            String[] a = s.split( separator );
            for ( String a1 : a ) 
            {
                if (( terminator == null ) || ( ! a1.equals(terminator) )) 
                {
                //  resultSplit.add( a1 + separator ); // If ";" after line.
                    resultSplit.add( a1 );
                }
            }
        }
        else
        {
            resultSplit.add(s);
        }
        
        ArrayList<String> resultCut = new ArrayList<>();
        for( int i=0; i<resultSplit.size(); i++ )
        {
            String s1 = resultSplit.get( i );
            int cutCount = s1.length() / lineMax;
            int cutMod = s1.length() % lineMax;
            if(( cutCount < 2 ) && (cutMod == 0))
            {
                resultCut.add( s1 );
            }
            else
            {
                int cutFrom = 0;
                while(cutFrom < s1.length())
                {
                    int cutTo = cutFrom + lineMax;
                    if( cutTo > s1.length() )
                    {
                        cutTo = s1.length();
                    }
                    String s2 = s1.substring(cutFrom, cutTo);
                    cutFrom = cutTo;
                    resultCut.add( s2 );
                }
            }
        }
        
        return resultCut.toArray( new String[resultCut.size()] );
    }
}

