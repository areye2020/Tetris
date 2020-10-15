package assignment;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class TetrisBoardTest {

    private TetrisBoard board = new TetrisBoard(5, 10);
    private TetrisPiece t = new TetrisPiece(Piece.PieceType.T);
    private TetrisPiece square = new TetrisPiece(Piece.PieceType.SQUARE);
    private TetrisPiece stick = new TetrisPiece(Piece.PieceType.STICK);
    private TetrisPiece leftDog = new TetrisPiece(Piece.PieceType.LEFT_DOG);
    private TetrisPiece rightDog = new TetrisPiece(Piece.PieceType.RIGHT_DOG);
    private TetrisPiece leftL = new TetrisPiece(Piece.PieceType.LEFT_L);
    private TetrisPiece rightL = new TetrisPiece(Piece.PieceType.RIGHT_L);

    @Test
    void getNum() {
        assertEquals(0, board.getNum(null));
        assertEquals(3, board.getNum(Piece.PieceType.STICK));
    }

    @Test
    void move(){
        setBoard(board);
        board.nextPiece(square, new Point (0, 2));
        assertEquals(Board.Result.OUT_BOUNDS, board.move(Board.Action.LEFT));

        board.nextPiece(square, new Point (2, 2));
        assertEquals(Board.Result.SUCCESS, board.move(Board.Action.LEFT));

        board.nextPiece(square, new Point (3, 6));
        assertEquals(Board.Result.OUT_BOUNDS, board.move(Board.Action.RIGHT));

        board.nextPiece(square, new Point (0, 2));
        assertEquals(Board.Result.SUCCESS, board.move(Board.Action.RIGHT));

        board.nextPiece(stick.clockwisePiece(), new Point(0, 0));
        assertEquals(Board.Result.OUT_BOUNDS, board.move(Board.Action.CLOCKWISE));

        board.nextPiece(stick.clockwisePiece(), new Point(-1, 2));
        assertEquals(Board.Result.OUT_BOUNDS, board.move(Board.Action.COUNTERCLOCKWISE));

        assertEquals(Board.Result.SUCCESS, board.move(Board.Action.NOTHING));

    }

    @Test
    void clockwise() {
        setBoard(board);
        board.nextPiece(leftL, new Point(1,2) );

        board.move(Board.Action.CLOCKWISE);
        assertEquals(new Point(0,3), board.getCurrentPiecePosition());

        board.move(Board.Action.CLOCKWISE);
        assertEquals(new Point(1,2), board.getCurrentPiecePosition());

        board.move(Board.Action.CLOCKWISE);
        assertEquals(new Point(2,0), board.getCurrentPiecePosition());

        board.move(Board.Action.CLOCKWISE);
        assertEquals(new Point(1,2), board.getCurrentPiecePosition());
    }

    @Test
    void counterClockwise() {
        setBoard(board);
        board.nextPiece(leftL, new Point(1,2) );

        board.move(Board.Action.COUNTERCLOCKWISE);
        assertEquals(new Point(2,0), board.getCurrentPiecePosition());

        board.move(Board.Action.COUNTERCLOCKWISE);
        assertEquals(new Point(1,2), board.getCurrentPiecePosition());

        board.move(Board.Action.COUNTERCLOCKWISE);
        assertEquals(new Point(0,3), board.getCurrentPiecePosition());

        board.move(Board.Action.COUNTERCLOCKWISE);
        assertEquals(new Point(1,2), board.getCurrentPiecePosition());
    }

    @Test
    void getFirstColumn() {
        board.nextPiece(square, new Point (0, 2));
        assertEquals(0, board.getFirstColumn());
    }

    @Test
    void getLastColumn() {
        board.nextPiece(square, new Point (0, 2));
        assertEquals(1, board.getLastColumn());
    }

    @Test
    void validLeft() {
        setBoard(board);
        board.nextPiece(square, new Point (2, 0));
        assertEquals(false, board.validLeft());

        board.nextPiece(square, new Point (2, 2));
        assertEquals(true, board.validLeft());
    }

    @Test
    void validRight() {
        setBoard(board);
        board.nextPiece(square, new Point (0, 2));
        assertEquals(true, board.validRight());

        board.nextPiece(square, new Point (2, 2));
        assertEquals(false, board.validRight());
    }

    @Test
    void moveLeft() {
        setBoard(board);
        board.nextPiece(square, new Point (1, 2));
        board.move(Board.Action.LEFT);
        assertEquals(new Point (0, 2), board.getCurrentPiecePosition());
    }

    @Test
    void moveRight() {
        setBoard(board);
        board.nextPiece(square, new Point (0, 2));
        board.move(Board.Action.RIGHT);
        assertEquals(new Point (1, 2), board.getCurrentPiecePosition());
    }

    @Test
    void moveDown() {
        setBoard(board);
        board.nextPiece(stick, new Point (0, 1));
        board.move(Board.Action.DOWN);
        assertEquals(new Point (0, 0), board.getCurrentPiecePosition());
    }

    @Test
    void getCurrentPiece() {
        setBoard(board);
        board.nextPiece(t, new Point (2, 7));
        assertEquals(t, board.getCurrentPiece());
    }

    @Test
    void getCurrentPiecePosition() {
        board.nextPiece(t, new Point (2, 7));
        assertEquals(new Point(2,7), board.getCurrentPiecePosition());
    }

    @Test
    void nextPiece() {
        setBoard(board);
        try{
            board.nextPiece(t, new Point (0, 11));
        }
        catch(IllegalArgumentException e ){
            System.err.println(e);
        }
        try{
            board.nextPiece(t, new Point (7, 0));
        }
        catch(IllegalArgumentException e ){
            System.err.println(e);
        }
        try{
            board.nextPiece(t, new Point (0, 0));
        }
        catch(IllegalArgumentException e ){
            System.err.println(e);
        }
        board.nextPiece(t, new Point (2, 7));
    }

    @Test
    void testEquals() {
        TetrisBoard other = new TetrisBoard(5, 10);
        assertEquals(true, board.equals(other));
        setBoard(board);
        assertEquals(false, board.equals(t));
        assertEquals(false, board.equals(other));
        setBoard(other);
        other.nextPiece(t, new Point (1,7));
        assertEquals(false, board.equals(other));
        other.nextPiece(t, new Point (2,7));
        assertEquals(false, board.equals(other));
        board.nextPiece(t, new Point (2,7));
        assertEquals(true, board.equals(other));
        board.setGrid(2, 0, board.getOfficialGrid(), 3);
        assertEquals(false, board.equals(other));

    }

    @Test
    void getLastResult() {
        setBoard(board);
        board.nextPiece(stick, new Point (0, 0));
        board.move(Board.Action.DOWN);
        assertEquals(Board.Result.PLACE,board.getLastResult());
    }

    @Test
    void getLastAction() {
        setBoard(board);
        board.nextPiece(stick, new Point (0, 0));
        board.move(Board.Action.DOWN);
        assertEquals(Board.Action.DOWN,board.getLastAction());
    }

    @Test
    void getRowsCleared() {
        setBoard(board);
        board.nextPiece(stick, new Point (0, 0));
        board.move(Board.Action.DOWN);
        assertEquals(1, board.getRowsCleared());
        assertEquals(1, board.getRowWidth(2));
    }

    @Test
    void getWidth() {
        assertEquals(5, board.getWidth());
    }

    @Test
    void getHeight() {
        assertEquals(10, board.getHeight());
    }

    @Test
    void getMaxHeight() {
        setBoard(board);
        assertEquals(6, board.getMaxHeight());
    }

    @Test
    void dropHeight() {
        setBoard(board);
        board.nextPiece(square, new Point(3,8));
        assertEquals(6, board.dropHeight(square, 3));
    }

    @Test
    void drop() {
        setBoard(board);
        board.nextPiece(square, new Point(3,8));
        board.drop();
        assertEquals(2, board.getRowWidth(6));
        assertEquals(2, board.getRowWidth(7));
        assertEquals(0, board.getRowWidth(8));

    }

    @Test
    void getColumnHeight() {
        setBoard(board);
        assertEquals(2, board.getColumnHeight(0));
    }

    @Test
    void getRowWidth() {
        setBoard(board);
        assertEquals(3, board.getRowWidth(0));
    }

    @Test
    void gridOperations() {
        board.setGrid(1,2, board.getOfficialGrid(), board.getNum(Piece.PieceType.T));
        assertEquals(Piece.PieceType.T, board.getGrid(1,2));
    }

    void setBoard(TetrisBoard b){
        for (Point p : square.getBody()) {
            b.setGrid(0 + p.x, 0 + p.y, b.getOfficialGrid(), b.getNum(square.getType()));
        }
        Piece stickRot1 = stick.clockwisePiece();
        for (Point p : stickRot1.getBody()) {
            b.setGrid(2 + p.x, 0 + p.y, b.getOfficialGrid(), b.getNum(stickRot1.getType()));
        }
        for (Point p : t.getBody()) {
            b.setGrid(2 + p.x, 3 + p.y, b.getOfficialGrid(), b.getNum(t.getType()));
        }
    }
}