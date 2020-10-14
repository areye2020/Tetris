package assignment;

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
    void returnRotations() {
    }

    @org.junit.jupiter.api.Test
    void calculateSkirt() {
        assertArrayEquals(new int[] {2, 2, 2, 2}, stick.calculateSkirt());
    }

    @org.junit.jupiter.api.Test
    void getRotations() {
    }

    @org.junit.jupiter.api.Test
    void rotateBody() {
    }

    @org.junit.jupiter.api.Test
    void getType() {
    }

    @org.junit.jupiter.api.Test
    void getRotationIndex() {
    }

    @org.junit.jupiter.api.Test
    void clockwisePiece() {
    }

    @org.junit.jupiter.api.Test
    void counterclockwisePiece() {
    }

    @org.junit.jupiter.api.Test
    void getWidth() {
    }

    @org.junit.jupiter.api.Test
    void getHeight() {
    }

    @org.junit.jupiter.api.Test
    void getBody() {
    }

    @org.junit.jupiter.api.Test
    void getSkirt() {
    }

    @org.junit.jupiter.api.Test
    void testEquals() {
    }

    @org.junit.jupiter.api.Test
    void getWallKicks() {
    }

    @org.junit.jupiter.api.Test
    void lowestSkirt() {
    }
}