package assignment;
import java.awt.event.*;

/**
 * JTetris presents a tetris game in a window.
 * It handles the GUI and the animation.
 * The Piece and Board classes handle the
 * lower-level computations.
 */
public class JBrainTetris extends JTetris {

    public static void main(String[] args) {
        createGUI(new JBrainTetris());
    }

    // Board data structure
    protected Brain brain;

    JBrainTetris() {
        super();
        brain = new MassiveBrain();
        resetKeyboardActions();
        timer = new javax.swing.Timer(DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(brain.nextMove(board));
            }
        });
    }
}

