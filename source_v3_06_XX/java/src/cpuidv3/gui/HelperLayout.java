/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class with static helpers methods for GUI components layouts.
Use spring layout.

*/

package cpuidv3.gui;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

class HelperLayout 
{
    // Centering component c at relative component p (parent, panel),
    // use offsets values up, down, left, right.
    static void springCenter ( SpringLayout s, 
            JComponent p, JComponent c, int up, int down, int left, int right )
    {
        s.putConstraint( SpringLayout.NORTH, c, up,    SpringLayout.NORTH, p );
        s.putConstraint( SpringLayout.SOUTH, c, down,  SpringLayout.SOUTH, p );
        s.putConstraint( SpringLayout.WEST,  c, left,  SpringLayout.WEST,  p );
        s.putConstraint( SpringLayout.EAST,  c, right, SpringLayout.EAST,  p );
    }
    // Overloaded method, input parameters set different, reduced.
    static void springCenter ( SpringLayout s, JPanel p, JComponent c, 
            int up, int left )
    {
        s.putConstraint( SpringLayout.NORTH, c, up,    SpringLayout.NORTH, p );
        s.putConstraint( SpringLayout.WEST,  c, left,  SpringLayout.WEST,  p );
    }
    // Overloaded method, input parameters set different
    // locate component c relative components p1, p2.
    static void springCenter ( SpringLayout s, 
            JComponent p1, JComponent p2, JComponent c, 
            int up, int down, int left, int right      )
    {
        s.putConstraint( SpringLayout.NORTH, c, up,    SpringLayout.SOUTH, p2 );
        s.putConstraint( SpringLayout.SOUTH, c, down,  SpringLayout.SOUTH, p1 );
        s.putConstraint( SpringLayout.WEST,  c, left,  SpringLayout.WEST,  p1 );
        s.putConstraint( SpringLayout.EAST,  c, right, SpringLayout.EAST,  p1 );
    }
    // Locate component c relative panel p by down offset.
    static void springSouth ( SpringLayout s, JPanel p, 
                                      JComponent c, int down )
    {
        s.putConstraint( SpringLayout.SOUTH, c, down, SpringLayout.SOUTH, p );
    }
    // locate component c relative panel p by left offset.
    static void springWest ( SpringLayout s, JPanel p, 
                                     JComponent c, int left )
    {
        s.putConstraint( SpringLayout.WEST, c, left, SpringLayout.WEST, p );
    }
    // Locate component c2 relative component c1 by west-to-east offset.
    static void springWestEast
        ( SpringLayout s, JComponent c1, JComponent c2, int inv )
    {
        s.putConstraint( SpringLayout.WEST, c2, inv, SpringLayout.EAST, c1 );
    }
    // Locate component c2 relative component c1 by east-to-east offset.
    static void springEast
        ( SpringLayout s, JComponent c1, JComponent c2, int inv )
    {
    s.putConstraint( SpringLayout.EAST, c2, inv, SpringLayout.EAST, c1 );
    }
    // Locate component c2 relative component c1 by east-to-west offset.
    static void springEastWest
        ( SpringLayout s, JComponent c1, JComponent c2, int inv )
    {
        s.putConstraint( SpringLayout.EAST, c2, inv, SpringLayout.WEST, c1 );
    }
}
