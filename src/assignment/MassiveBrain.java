package assignment;

import java.util.ArrayList;
import java.util.HashMap;

public class MassiveBrain implements Brain {
    private ArrayList<Board.Action> firstMoves;
    private HashMap<Integer, ArrayList<Board>> options;
    private ArrayList<Integer> indexes;

    @Override
    public Board.Action nextMove(Board currentBoard) {
        options = new HashMap<>();
        firstMoves = new ArrayList<>();
        indexes = new ArrayList<>();
        enumerateOptions(currentBoard);

        int index = -1;
        int max = 0;
        for (int i = 0; i < options.keySet().size(); i++) {
            ArrayList<Board> moves = options.get(i);
            for (int move = 0; move < moves.size(); move++) {
                Board toCheck = moves.get(move);
                if (evaluation(toCheck) > max) {
                    index = move;
                    max = evaluation(toCheck);
                }
            }
            if (index == -1) {
                index = index + moves.size();
            }
        }
        System.out.println(((TetrisBoard) currentBoard).getTotalCleared());
        if (index < options.get(0).size()) {
            return firstMoves.get(index);
        } else{
            return Board.Action.CLOCKWISE;
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

        for (int rotIndex = 0; rotIndex < 4; rotIndex++) {
            ArrayList<Board> moves = rotationMoves(currentBoard, rotIndex);
            options.put(rotIndex, moves);
        }
    }

    public ArrayList<Board> rotationMoves(Board currentBoard, int index) {
        ArrayList<Board> moves = new ArrayList<>();
        Board rotated = currentBoard;
        for (int i = 0; i < index; i++) {
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
            int score = test.getHeight() - test.getMaxHeight();
            return score;
        }
}
