package assignment;

import java.awt.*;

/**
 * An immutable representation of a tetris piece in a particular rotation.
 * 
 * All operations on a TetrisPiece should be constant time, except for it's
 * initial construction. This means that rotations should also be fast - calling
 * clockwisePiece() and counterclockwisePiece() should be constant time! You may
 * need to do precomputation in the constructor to make this possible.
 */
public final class TetrisPiece implements Piece {
    private PieceType shapeType; //new instance of piece type
    private int height;
    private int width;
    private LinkedList rotations;


    /**
     * Construct a tetris piece of the given type. The piece should be in it's spawn orientation,
     * i.e., a rotation index of 0.
     * 
     * You may freely add additional constructors, but please leave this one - it is used both in
     * the runner code and testing code.
     */
    public TetrisPiece(PieceType type) {
        shapeType = type;
        rotations = new LinkedList();
    }

    public LinkedList generateRotations(){
        return rotations;
    }

    @Override
    public PieceType getType() {
        return shapeType;
    }

    @Override
    public int getRotationIndex() {
        // TODO: Implement me.
        return -1;
    }

    @Override
    public Piece clockwisePiece() {
        return rotations.getClockwise();
    }

    @Override
    public Piece counterclockwisePiece() {
        return rotations.getCounterClockwise();
    }

    @Override
    public int getWidth() {
        int width = shapeType.getBoundingBox().width;
        return width;
    }

    @Override
    public int getHeight() {
        int height = shapeType.getBoundingBox().height;
        return height;
    }

    @Override
    public Point[] getBody() {
        // TODO: Implement me.
        return null;
    }

    @Override
    public int[] getSkirt() {
        // TODO: Implement me.
        return null;
    }

    @Override
    public boolean equals(Object other) {
        // Ignore objects which aren't also tetris pieces.
        if(!(other instanceof TetrisPiece)) return false;
        TetrisPiece otherPiece = (TetrisPiece) other;

        // TODO: Implement me.
        return false;
    }
}
