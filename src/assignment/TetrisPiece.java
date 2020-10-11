package assignment;

import java.awt.*;
import java.util.Arrays;
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
    private PieceType shapeType; //new instance of piece type
    //private int height;
    //private int width;
    //private LinkedList rotations;
    private HashMap<Integer, TetrisPiece> rotations;
    private int rotationIndex;
    private Point[] body;
    private int[] skirt;

    public static void main(String args[]){
        TetrisPiece trial = new TetrisPiece(PieceType.RIGHT_DOG);
        trial = (TetrisPiece)trial.clockwisePiece();
        //HashMap<Integer, TetrisPiece> trialRotations = trial.getRotations();
        /*for(int i = 0; i < trialRotations.size(); i++){
            System.out.println("tetris piece rotation index = " + trialRotations.get(i).rotationIndex);
            Point[] b = trialRotations.get(i).body;
            System.out.print("body = ");
            for (Point p : b){
                System.out.print(p.toString() + " ");
            }
            System.out.print("\n");
        }*/

        /*System.out.println(trial.getRotationIndex());

        Point[] b = trial.getBody();
        System.out.print("body = ");
        for (Point p : b){
            System.out.print(p.toString() + " ");
        }*/

        /*int [] s = trial.getSkirt();
        System.out.println(trial.getRotationIndex());
        System.out.print("skirt = ");
        for (int num : s) {
            System.out.print(num + " ");
        }
        trial = (TetrisPiece)trial.counterclockwisePiece();
        s = trial.getSkirt();
        System.out.println(trial.getRotationIndex());
        System.out.print("skirt = ");
        for (int num : s) {
            System.out.print(num + " ");
        }*/

        TetrisPiece p = new TetrisPiece(PieceType.STICK);
        p = (TetrisPiece)p.clockwisePiece();
        p = (TetrisPiece)p.clockwisePiece();
        System.out.println(trial.equals(p));
    }
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
        //rotations = new LinkedList();
        rotations = getRotations();
        skirt = calculateSkirt();
    }

    public TetrisPiece(TetrisPiece original, int rotIndex, HashMap<Integer, TetrisPiece> rot, Point[] shape ){
        shapeType = original.getType();
        body = shape;
        rotations = rot;
        rotationIndex = rotIndex;
        skirt = calculateSkirt();
    }

    public int[] calculateSkirt(){
        int width = getWidth();
        int [] s = new int[width];
        int temp;
        for (int i = 0; i < width; i++){
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

    public HashMap<Integer, TetrisPiece> getRotations(){
        HashMap<Integer, TetrisPiece> rot = new HashMap<>();
        rot.put(rotationIndex, this);
        Point [] tempBody = this.body;
        for (int i = 1; i <= 3; i++){
            tempBody = rotateBody(tempBody);
            TetrisPiece temp = new TetrisPiece(this, i, rot, tempBody);
            rot.put(i, temp);
        }
        return rot;
    }

    public Point[] rotateBody(Point [] original){
        Point[] newBody = new Point[original.length];
        int height = shapeType.getBoundingBox().height;
        int width = shapeType.getBoundingBox().width;
        Point temp = new Point();

        for (int i = 0; i < original.length; i++) {
            temp = new Point();
            temp.x = original[i].y;
            temp.y = width - original[i].x - 1;
            newBody[i] = temp;
        }
        return newBody;
    }

    @Override
    public PieceType getType() {
        return shapeType;
    }

    @Override
    public int getRotationIndex() {
        return rotationIndex;
    }

    @Override
    public Piece clockwisePiece() {
        if (rotationIndex+1 > (rotations.size()-1)){
            return rotations.get(0);
        }
        else{
            return rotations.get(rotationIndex+1);
        }
    }

    @Override
    public Piece counterclockwisePiece() {
        if (rotationIndex-1 < 0){
            return rotations.get(rotations.size()-1);
        }
        else{
            return rotations.get(rotationIndex-1);
        }
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
        return body;
    }

    @Override
    public int[] getSkirt() {
        return skirt;
    }

    @Override
    public boolean equals(Object other) {
        // Ignore objects which aren't also tetris pieces.
        if(!(other instanceof TetrisPiece)) return false;
        TetrisPiece otherPiece = (TetrisPiece) other;

        if (shapeType.compareTo(otherPiece.getType())==0 && rotationIndex == otherPiece.getRotationIndex()){
         return true;
        }
        else {
            return false;
        }
    }

    public Point[][] getWallKicks(int move){
        Point [][] wallKicks = null;
        if (shapeType.equals(PieceType.SQUARE)){ wallKicks = null;}
        else if (shapeType.equals(PieceType.STICK) && move == -1){
            wallKicks = Piece.I_COUNTERCLOCKWISE_WALL_KICKS;
        }
        else if (shapeType.equals(PieceType.STICK) && move == 1){
            wallKicks = Piece.I_CLOCKWISE_WALL_KICKS;
        }
        else if (move == -1){
            wallKicks = Piece.NORMAL_COUNTERCLOCKWISE_WALL_KICKS;
        }
        else if (move == 1){
            wallKicks = Piece.NORMAL_CLOCKWISE_WALL_KICKS;
        }
        return wallKicks;
    }
}
