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
Class with static helpers methods for GUI buttons generation at panels.
*/

package cpuidv2.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class HelperButton 
{
public static void downButtons  // overloaded: with tooltips and mnemonics
     ( JPanel p, JButton[] buttons, 
       String[] buttonsNames, String[] buttonsTips, int[] buttonsKeys, int nb,
       ActionListener[] actionsListeners, SpringLayout sl1,
       int bDown, int bRight, int bInterval, Dimension db )
    {
    downButtons( p, buttons, buttonsNames, nb, actionsListeners, sl1,
                 bDown, bRight, bInterval, db );
    // add tooltips and mnemonics
    for( int i=0; i<nb; i++ )
        {
        if ( ( buttonsTips != null ) && ( buttonsTips[i] != null ) )
            {
            buttons[i].setToolTipText( buttonsTips[i] );
            }
/*            
        // yet disabled, because conflicts with root menu
        if ( ( BUTTONS_KEYS != null ) && ( BUTTONS_KEYS[i] != 0 ) )
            {
            buttons[i].setMnemonic( BUTTONS_KEYS[i] );
            }
*/
        }
    }
            
public static void downButtons  // overloaded: without tooltips
    ( JPanel p, JButton[] buttons, String[] buttonsNames, int nb,
      ActionListener[] actionsListeners, SpringLayout sl1,
      int bDown, int bRight, int bInterval, Dimension db )
    {
    Font font = new Font( "Verdana", Font.PLAIN, 10 );
    for ( int i=nb-1; i>=0; i-- )
        {
        buttons[i] = new JButton( buttonsNames[i] );
        buttons[i].setPreferredSize( db );
        buttons[i].setFont( font );
        buttons[i].addActionListener( actionsListeners[i] );
        p.add( buttons[i] );
        HelperLayout.springSouth ( sl1, p, buttons[i], bDown );
        }
    for ( int i=nb-1; i>=0; i-- ) 
        {
        if (i == nb-1) { HelperLayout.springEast 
                            ( sl1, p, buttons[i], bRight ); }
        else { HelperLayout.springEastWest 
                            ( sl1, buttons[i+1], buttons[i], bInterval ); }
        }
    }
}
