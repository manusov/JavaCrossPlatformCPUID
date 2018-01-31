/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
Static helper library: built horizontal array of down buttons.
*/

package cpuid.applications.guipanels;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class SetupButtons 
{

public static void downButtons  // overloaded: with tooltips and mnemonics
     ( JPanel p,        
       JButton[] buttons,
       String[] BUTTONS_NAMES, String[] BUTTONS_TIPS, int[] BUTTONS_KEYS, 
       int NB,
       ActionListener[] actionsListeners, SpringLayout sl1,
       int B_DOWN, int B_RIGHT, int B_INTERVAL, Dimension DB )
    {
    downButtons( p, buttons, BUTTONS_NAMES, NB, actionsListeners, sl1,
                 B_DOWN, B_RIGHT, B_INTERVAL, DB );
    // add tooltips and mnemonics
        {
        for( int i=0; i<NB; i++ )
            {
            if ( ( BUTTONS_TIPS != null ) && ( BUTTONS_TIPS[i] != null ) )
                {
                buttons[i].setToolTipText( BUTTONS_TIPS[i] );
                }
            if ( ( BUTTONS_KEYS != null ) && ( BUTTONS_KEYS[i] != 0 ) )
                {
                // yet disabled, because conflicts with root menu
                // buttons[i].setMnemonic( BUTTONS_KEYS[i] );
                }
            }
        }
    }
            
public static void downButtons  // overloaded: without tooltips
    ( JPanel p,        
      JButton[] buttons,
      String[] BUTTONS_NAMES, 
      int NB,
      ActionListener[] actionsListeners, SpringLayout sl1,
      int B_DOWN, int B_RIGHT, int B_INTERVAL, Dimension DB )
    {
    Font font = new Font("Verdana", Font.PLAIN, 10);
    for ( int i=NB-1; i>=0; i-- )
        {
        buttons[i] = new JButton( BUTTONS_NAMES[i] );
        buttons[i].setPreferredSize( DB );
        buttons[i].setFont(font);
        buttons[i].addActionListener( actionsListeners[i] );
        p.add( buttons[i] );
        SetupLayout.springSouth ( sl1, p, buttons[i], B_DOWN );
        }
    for ( int i=NB-1; i>=0; i-- ) 
        {
        if (i == NB-1) { SetupLayout.springEast 
                            ( sl1, p, buttons[i], B_RIGHT ); }
        else { SetupLayout.springEastWest 
                            ( sl1, buttons[i+1], buttons[i], B_INTERVAL ); }
        }
    }
    
}
