package assignment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class OurBrain implements Brain{

    private ArrayList<Board.Action> firstMoves;
    private HashMap<Integer, ArrayList<Board>> options;
    private Board.Action nextAction;
    private boolean toClear;

    @Override
    public Board.Action nextMove(Board currentBoard) {
        if (nextAction != null && toClear == false){
            toClear = true;
            return nextAction;
        }
        else if (nextAction != null && toClear == true){
            nextAction = null;
            toClear = false;
        }
        options = new HashMap<>();
        firstMoves = new ArrayList<>();
        enumerateOptions(currentBoard);

        int index = -1;
        int min = currentBoard.getHeight();
        for (int i = 0; i < options.keySet().size(); i++){
            ArrayList<Board> moves = options.get(i);
            for (int move = 0; move < moves.size(); move++){
                Board toCheck = moves.get(move);
                if (evaluation(toCheck) < min){
                    index = move;
                    min = evaluation(toCheck);
                }
            }
            if (index == -1){
                index = index + moves.size();
            }
        }
        if (index < options.get(0).size()){
            return firstMoves.get(index);
        }
        else if (index < options.get(1).size()){
            nextAction = firstMoves.get(index);
            return Board.Action.CLOCKWISE;
        }
        else if (index < options.get(2).size()){
            nextAction = Board.Action.CLOCKWISE;
            return Board.Action.CLOCKWISE;
        }
        else{
            nextAction = firstMoves.get(index);
            return Board.Action.COUNTERCLOCKWISE;
        }
    }

    /* HashMap: Integer ---> ArrayList<Board>
    *           0       ---> drop, left(x times), right(x times)
    *           1       ---> drop, left(x times), right(x times)
    *           2       ---> drop, left(x times), right(x times)
    *           3       ---> drop, left(x times), right(x times)
    * */
    private void enumerateOptions(Board currentBoard) {
        TetrisPiece current = (TetrisPiece) currentBoard.getCurrentPiece();
        // We can always drop our current Piece
        for (int rotIndex = 0; rotIndex < 2; rotIndex++){
            ArrayList<Board> moves = rotationMoves(currentBoard, rotIndex);
            options.put(rotIndex, moves);
        }
    }

    public ArrayList<Board> rotationMoves(Board currentBoard, int index){
        ArrayList<Board> moves = new ArrayList<>();
        Board rotated = currentBoard;
        for (int i = 0; i < index; i++){
            rotated = rotated.testMove(Board.Action.CLOCKWISE);
        }

        firstMoves.add(Board.Action.DROP);
        moves.add(rotated.testMove(Board.Action.DROP));

        Board left = rotated.testMove(Board.Action.LEFT);
        while (left.getLastResult() == Board.Result.SUCCESS) {
            moves.add(left.testMove(Board.Action.DROP));
            firstMoves.add(Board.Action.LEFT);
            left.move(Board.Action.LEFT);
        }

        Board right = rotated.testMove(Board.Action.RIGHT);
        while (right.getLastResult() == Board.Result.SUCCESS) {
            moves.add(right.testMove(Board.Action.DROP));
            firstMoves.add(Board.Action.RIGHT);
            right.move(Board.Action.RIGHT);
        }

        return moves;
    }

    public int evaluation (Board test){
        return test.getMaxHeight();
    }
    /* Brain - look at each possible move (left, right, drop, c, cc)
    * for each rotation (rotIndex = 0, 1, 2, 3), check left, right, and drop
    * about 12 different possibilities
    * record each possibility and max height of each possibility
    * pick the possibility with the least max height
    * one turn = left, right, or drop (required) and/or cc or c (optional)
    * if debating between two or more possibilities, then look at # of rowsCleared
    * and holes are bad (holes that cannot be filled):
    *    * *    *        *     *    * *        * *    * * *    < pieces
    *    * *    *        *     *      * *    * *        *
    *           * *    * *     *
    *                          *
    *
    * */
}
