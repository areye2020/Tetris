package assignment;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class TetrisPieceTest {

    private TetrisPiece t = new TetrisPiece(Piece.PieceType.T);
    private TetrisPiece square = new TetrisPiece(Piece.PieceType.SQUARE);
    private TetrisPiece stick = new TetrisPiece(Piece.PieceType.STICK);
    private TetrisPiece leftDog = new TetrisPiece(Piece.PieceType.LEFT_DOG);
    private TetrisPiece rightDog = new TetrisPiece(Piece.PieceType.RIGHT_DOG);
    private TetrisPiece leftL = new TetrisPiece(Piece.PieceType.LEFT_L);
    private TetrisPiece rightL = new TetrisPiece(Piece.PieceType.RIGHT_L);

    @org.junit.jupiter.api.Test
    void calculateSkirt() {
        assertArrayEquals(new int[] {2, 2, 2, 2}, stick.calculateSkirt());
        assertArrayEquals(new int[] {0, 0}, square.calculateSkirt());
        assertArrayEquals(new int[] {1, 1, 2}, rightDog.calculateSkirt());
        assertArrayEquals(new int[] {1, 1, 1}, leftL.calculateSkirt());
    }

    @org.junit.jupiter.api.Test
    void getRotationIndex() {
        assertEquals(1, rightDog.clockwisePiece().getRotationIndex());
        assertEquals(3, rightDog.counterclockwisePiece().getRotationIndex());
    }

    @org.junit.jupiter.api.Test
    void clockwisePiece() {
        assertArrayEquals(new Point[] {new Point(1,2), new Point(1, 1), new Point(1, 0),
                new Point(2, 2)}, leftL.clockwisePiece().getBody());
        Piece temp = leftL.clockwisePiece();
        assertArrayEquals(new Point[] {new Point(2,1), new Point(1, 1), new Point(0, 1),
                new Point(2, 0)}, temp.clockwisePiece().getBody());
        temp = temp.clockwisePiece();
        assertArrayEquals(new Point[] {new Point(1,0), new Point(1, 1), new Point(1, 2),
                new Point(0, 0)}, temp.clockwisePiece().getBody());
        temp = temp.clockwisePiece();
        assertArrayEquals(new Point[] {new Point(0,1), new Point(1, 1), new Point(2, 1),
                new Point(0, 2)}, temp.clockwisePiece().getBody());
    }

    @org.junit.jupiter.api.Test
    void counterclockwisePiece() {
        assertArrayEquals(new Point[] {new Point(1,0), new Point(1, 1), new Point(1, 2),
                new Point(0, 1)}, t.counterclockwisePiece().getBody());
        Piece temp = t.counterclockwisePiece();
        assertArrayEquals(new Point[] {new Point(2,1), new Point(1, 1), new Point(0, 1),
                new Point(1, 0)}, temp.counterclockwisePiece().getBody());
        temp = temp.counterclockwisePiece();
        assertArrayEquals(new Point[] {new Point(1,2), new Point(1, 1), new Point(1, 0),
                new Point(2, 1)}, temp.counterclockwisePiece().getBody());
        temp = temp.counterclockwisePiece();
        assertArrayEquals(new Point[] {new Point(0,1), new Point(1, 1), new Point(2, 1),
                new Point(1, 2)}, temp.counterclockwisePiece().getBody());
    }

    @org.junit.jupiter.api.Test
    void testEquals() {
        assertEquals(false, square.equals(4));
        assertEquals(false, square.equals(t));
        assertEquals(false, t.equals(t.clockwisePiece()));
        Piece testSquare = new TetrisPiece(Piece.PieceType.SQUARE);
        assertEquals(true, testSquare.equals(square));
    }

    @org.junit.jupiter.api.Test
    void lowestSkirt() {
        assertEquals(2, stick.lowestSkirt());
    }
}