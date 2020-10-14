package assignment;

import java.awt.*;
import java.util.HashMap;

/**
 * An immutable representation of a tetris piece in a particular rotation.
 * 
 * All operations on a TetrisPiece should be constant time, except for it's
 * initial construction. This means that rotations should also be fast - calling
 * clockwisePiece() and counterclockwisePiece() should be constant time! You may
 * need to do precomputation in the constructor to make this possible.
 */
public final class TetrisPiece implements Piece {
    private PieceType shapeType; //PieceType of TetrisPiece
    private HashMap<Integer, TetrisPiece> rotations; //HashMap of the rotations of the TetrisPiece
    private int rotationIndex;
    private Point[] body;
    private int[] skirt;

    /**
     * Construct a tetris piece of the given type. The piece should be in it's spawn orientation,
     * i.e., a rotation index of 0.
     * 
     * You may freely add additional constructors, but please leave this one - it is used both in
     * the runner code and testing code.
     */
    public TetrisPiece(PieceType type) {
        shapeType = type;
        rotationIndex = 0;
        body = type.getSpawnBody();
        rotations = getRotations();
        skirt = calculateSkirt();
    }

    //constructs TetrisPiece, only used when generating rotations
    public TetrisPiece(TetrisPiece original, int rotIndex, HashMap<Integer, TetrisPiece> rot, Point[] shape ){
        shapeType = original.getType();
        body = shape;
        rotations = rot;
        rotationIndex = rotIndex;
        skirt = calculateSkirt();
    }

    public HashMap<Integer, TetrisPiece> returnRotations(){
        return rotations;
    }

    //calculates the lowest y-values of each column of the TetrisPiece
    public int[] calculateSkirt(){
        int width = getWidth();
        int [] s = new int[width];
        int temp;
        for (int i = 0; i < width; i++){ //in each column
            temp = Integer.MAX_VALUE;
            for (int p = 0; p < body.length; p++){
                if (body[p].x == i && body[p].y < temp){
                    temp = body[p].y;
                }
            }
            s[i] = temp;
        }
        return s;
    }

    //generates the rotations of the TetrisPiece and puts them in the HashMap
    public HashMap<Integer, TetrisPiece> getRotations(){
        HashMap<Integer, TetrisPiece> rot = new HashMap<>(); //rotation index is key to rotated body
        rot.put(rotationIndex, this);
        Point [] tempBody = this.body;
        for (int i = 1; i <= 3; i++){
            tempBody = rotateBody(tempBody); //rotates the body clockwise 90 degrees
            TetrisPiece temp = new TetrisPiece(this, i, rot, tempBody); //creates the rotated TetrisPiece
            rot.put(i, temp);//puts that TetrisPiece in the HashMap
        }
        return rot;
    }

    //rotates the given body (called original) by 90 degrees clockwise, uses the rotation matrix logic
    public Point[] rotateBody(Point [] original){
        Point[] newBody = new Point[original.length];
        int width = shapeType.getBoundingBox().width;
        Point temp;

        for (int i = 0; i < original.length; i++) {
            temp = new Point();
            temp.x = original[i].y;
            temp.y = width - original[i].x - 1;
            newBody[i] = temp;
        }
        return newBody; //returns the points of the rotated body
    }

    //gets the PieceType of the TetrisPiece
    @Override
    public PieceType getType() {
        return shapeType;
    }

    //gets the rotation index of the TetrisPiece
    @Override
    public int getRotationIndex() {
        return rotationIndex;
    }

    //gets the clockwise version of the TetrisPiece
    @Override
    public Piece clockwisePiece() {
        if (rotationIndex+1 > (rotations.size()-1)){
            return rotations.get(0);
        }
        else{
            return rotations.get(rotationIndex+1);
        }
    }

    //gets the counterclockwise version of the TetrisPiece
    @Override
    public Piece counterclockwisePiece() {
        if (rotationIndex-1 < 0){
            return rotations.get(rotations.size()-1);
        }
        else{
            return rotations.get(rotationIndex-1);
        }
    }

    //gets the width of the TetrisPiece's bounding box
    @Override
    public int getWidth() {
        int width = shapeType.getBoundingBox().width;
        return width;
    }

    //gets the height of the TetrisPiece's bounding box
    @Override
    public int getHeight() {
        int height = shapeType.getBoundingBox().height;
        return height;
    }

    //gets the body of the TetrisPiece
    @Override
    public Point[] getBody() {
        return body;
    }

    //gets the skirt of the TetrisPiece
    @Override
    public int[] getSkirt() {
        return skirt;
    }

    //returns true if the instance TetrisPiece is equal to the other object
    @Override
    public boolean equals(Object other) {
        // Ignore objects which aren't also tetris pieces.
        if(!(other instanceof TetrisPiece)) return false;
        TetrisPiece otherPiece = (TetrisPiece) other;

        if (shapeType.compareTo(otherPiece.getType())==0 && rotationIndex == otherPiece.getRotationIndex()){ //shapeTypes and rotation indexes of the two TetrisPieces must be equal
         return true;
        }
        else {
            return false;
        }
    }

    //gets the set of wallKicks according to the shapeType of the TetrisPiece and
    //whether the TetrisPiece needs to be rotated clockwise (move = 1) or counterclockwise (move = -1)
    public Point[][] getWallKicks(int move){
        Point [][] wallKicks = null;
        if (shapeType.equals(PieceType.SQUARE)){ wallKicks = null;} //no wallKicks for the square PieceType
        else if (shapeType.equals(PieceType.STICK) && move == -1){ //stick's counterclockwise wallKicks
            wallKicks = Piece.I_COUNTERCLOCKWISE_WALL_KICKS;
        }
        else if (shapeType.equals(PieceType.STICK) && move == 1){ //stick's clockwise wallKicks
            wallKicks = Piece.I_CLOCKWISE_WALL_KICKS;
        }
        else if (move == -1){ //the other PieceTypes' counterclockwise wallKicks
            wallKicks = Piece.NORMAL_COUNTERCLOCKWISE_WALL_KICKS;
        }
        else if (move == 1){ //the other PieceTypes' clockwise wallKicks
            wallKicks = Piece.NORMAL_CLOCKWISE_WALL_KICKS;
        }
        return wallKicks;
    }
    public int lowestSkirt (){
        int [] skirt = getSkirt();
        int lowest = skirt[0];
        for (int i: skirt){
            if (i < lowest){
                lowest = i;
            }
        }
        return lowest;
    }
}
