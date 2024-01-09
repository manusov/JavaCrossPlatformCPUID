/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2024.
-------------------------------------------------------------------------------
Class for get JVM properties array and convert JVM properties array to
array of viewable text strings. Argument is JVM properties array.
Result is array of text strings consumed by GUI table and text reports.
*/

package cpuidv2.services;

import java.util.Enumeration;
import java.util.Properties;

public class ServiceJvmInfo 
{
public String[][] getJvmInfo()
    {
    Properties p = System.getProperties();
    int n = p.size();                       // n = length of properties list.
    String[][] text = new String[n][2];     // Create array for text table.
    Enumeration keys = p.keys();            // Enumeration for properties.
    // Cycle for properties list elements.
    for ( int i=0; i<n; i++ )
        {
        if ( keys.hasMoreElements() )
            {
            text[i][0] = (String)keys.nextElement();
            if ( text[i][0].equals( "line.separator" ) )
                {  // Special support for correct print separator char(s).
                text[i][1] = (String)p.get( text[i][0] );
                int n1 = text[i][1].length();
                StringBuilder sb = new StringBuilder ( "" );
                for ( int j=0; j<n1; j++ )
                    {
                    sb.append( (int)( text[i][1].charAt(j) ) );
                    if ( j < n1-1 ) { sb.append( ", " ); }
                    }
                text[i][1] = sb.toString();
                }
            else
                {  // This used if not a separator char(s).
                text[i][1] = (String)p.get( text[i][0] );
                }
            }
        else    // Unexpected ( because i<n ) absence of property.
            {
            text[i][0] = "?";
            text[i][1] = "?";
            }
        // Limit too long left string.
        if ( text[i][0].length() > 40 )
            {
            text[i][0] = text[i][0].substring( 0, 39 ) + "...";
            }
        // Limit too long right string.
        if ( text[i][1].length() > 100 )
            {
            text[i][1] = text[i][1].substring( 0, 99 ) + "...";
            }
        }
    return text;    
    }
}
