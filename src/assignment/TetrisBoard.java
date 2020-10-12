package assignment;

import java.awt.*;
import java.util.ArrayList;

/**
 * Represents a Tetris board -- essentially a 2-d grid of piece types (or nulls). Supports
 * tetris pieces and row clearing.  Does not do any drawing or have any idea of
 * pixels. Instead, just represents the abstract 2-d board.
 */
public final class TetrisBoard implements Board {

    private static final int EMPTY = 0;
    private static final int T = 1;
    private static final int SQUARE = 2;
    private static final int STICK = 3;
    private static final int LEFT_L = 4;
    private static final int RIGHT_L = 5;
    private static final int LEFT_DOG = 6;
    private static final int RIGHT_DOG = 7;

    private int[][] tempGrid;
    private int[][] officialGrid;
    private Board.Action lastAction;
    private Board.Result lastResult;
    private Piece currentPiece;
    private int xPosition;
    private int yPosition;
    private int[] blocksInRow;
    private int[] blocksInColumn;
    private int maxHeight;
    private int rowsCleared;

    // JTetris will use this constructor
    public TetrisBoard(int width, int height) {
        tempGrid = new int[height][width];
        officialGrid = new int[height][width];
        blocksInRow = new int[height];
        blocksInColumn = new int[width];
        maxHeight = 0;
        rowsCleared = 0;
    }

    public int[][] getOfficialGrid(){
        return officialGrid;
    }

    public int getNum(Piece.PieceType p){
        if (p == null){return EMPTY;}
        else if (p.equals(Piece.PieceType.T)){return T;}
        else if (p.equals(Piece.PieceType.SQUARE)){return SQUARE;}
        else if (p.equals(Piece.PieceType.STICK)){return STICK;}
        else if (p.equals(Piece.PieceType.LEFT_L)){return LEFT_L;}
        else if (p.equals(Piece.PieceType.RIGHT_L)){return RIGHT_L;}
        else if (p.equals(Piece.PieceType.LEFT_DOG)){return LEFT_DOG;}
        else {return RIGHT_DOG;}
    }

    @Override
    public Result move(Action act) {
        Result toReturn = null;
        rowsCleared = 0;
        if (act.equals(Action.LEFT)){
            if (xPosition+getFirstColumn()-1<0){
                toReturn = Result.OUT_BOUNDS;
            }
            else if (!validLeft()){
                toReturn = Result.OUT_BOUNDS;
            }
            else{
                moveLeft();
                toReturn = Result.SUCCESS;
            }
        }
        else if (act.equals(Action.RIGHT)){
            if (xPosition+getLastColumn()+1>=tempGrid[0].length){
                toReturn = Result.OUT_BOUNDS;
            }
            else if (!validRight()){
                toReturn = Result.OUT_BOUNDS;
            }
            else{
                moveRight();
                toReturn = Result.SUCCESS;
            }
        }
        else if (act.equals(Action.DOWN)){
            if (placePiece()){
                toReturn = Result.PLACE;
            }
            else{
                moveDown();
                toReturn = Result.SUCCESS;
            }
        }
        else if (act.equals(Action.DROP)){
            int height = dropHeight(currentPiece, xPosition);
            System.out.println(height);
            toReturn = Result.PLACE;
        }
        else if (act.equals(Action.CLOCKWISE)){
            if (clockwise()){
                toReturn = Result.SUCCESS;
            }
            else{
                toReturn = Result.OUT_BOUNDS;
            }
        }
        else if (act.equals(Action.COUNTERCLOCKWISE)){
            if (counterClockwise()){
                toReturn = Result.SUCCESS;
            }
            else{
                toReturn = Result.OUT_BOUNDS;
            }
        }
        else if (act.equals(Action.NOTHING)){

        }
        lastAction = act;
        lastResult = toReturn;
        return toReturn;
    }

    public boolean clockwise(){
        int initialOrientation = currentPiece.getRotationIndex();
        TetrisPiece rotated = (TetrisPiece) currentPiece.clockwisePiece();
        Point [] body = rotated.getBody();
        Point [] wallKicks;
        Point officialRot = null;
        boolean rotate = true;

        if (rotated.getWallKicks((1))==null){
            for (Point b : body){
                if (getGrid(xPosition + b.x, yPosition + b.y)!=null){
                    rotate = false;
                }
            }
            officialRot = new Point (0, 0);
        }
        else{
            wallKicks = rotated.getWallKicks(1)[initialOrientation];
            for (int kick = 0; kick < wallKicks.length; kick++){
                if (officialRot == null){
                    rotate = true;
                    for (Point b : body) {
                        if (getGrid(xPosition + b.x + wallKicks[0].x, yPosition + b.y + wallKicks[0].y) != null) {
                            rotate = false;
                        }
                    }
                    if (rotate){
                        officialRot = wallKicks[kick];
                    }
                }
            }
        }

        if (rotate){
            Point [] originalBody = currentPiece.getBody();
            for (Point b : originalBody){
                setGrid(xPosition+b.x, yPosition + b.y, tempGrid, EMPTY);
            }
            currentPiece = rotated;
            for (Point b : body){
                setGrid(xPosition+b.x+officialRot.x, yPosition + b.y+officialRot.y, tempGrid, getNum(currentPiece.getType()));
            }
            return true;
        }
        return false;
    }

    public boolean counterClockwise(){
        int initialOrientation = currentPiece.getRotationIndex();
        TetrisPiece rotated = (TetrisPiece) currentPiece.counterclockwisePiece();
        Point [] body = rotated.getBody();
        Point [] wallKicks;
        Point officialRot = null;
        boolean rotate = true;

        if (rotated.getWallKicks((-1))==null){
            for (Point b : body){
                if (getGrid(xPosition + b.x, yPosition + b.y)!=null){
                    rotate = false;
                }
            }
            officialRot = new Point (0, 0);
        }
        else{
            wallKicks = rotated.getWallKicks(-1)[initialOrientation];
            for (int kick = 0; kick < wallKicks.length; kick++){
                if (officialRot == null){
                    rotate = true;
                    for (Point b : body) {
                        if (getGrid(xPosition + b.x + wallKicks[0].x, yPosition + b.y + wallKicks[0].y) != null) {
                            rotate = false;
                        }
                    }
                    if (rotate){
                        officialRot = wallKicks[kick];
                    }
                }
            }
        }

        if (rotate){
            Point [] originalBody = currentPiece.getBody();
            for (Point b : originalBody){
                setGrid(xPosition + b.x,yPosition + b.y , tempGrid, EMPTY);
            }
            currentPiece = rotated;
            for (Point b : body){
                setGrid(xPosition + b.x + officialRot.x, yPosition + b.y + officialRot.y, tempGrid, getNum(currentPiece.getType()));
            }
            return true;
        }
        return false;
    }

    public int getFirstColumn(){
        int [] skirt = currentPiece.getSkirt();
        int firstColumn = 0;
        while(skirt[firstColumn] == Integer.MAX_VALUE){
            firstColumn++;
        }
        return firstColumn;
    }

    public int getLastColumn(){
        int [] skirt = currentPiece.getSkirt();
        int lastColumn = skirt.length-1;
        while(skirt[lastColumn] == Integer.MAX_VALUE){
            lastColumn--;
        }
        return lastColumn;
    }

    public boolean validLeft(){
        Point[] body = currentPiece.getBody();
        boolean validMove = false;
        for (Point b : body){
            if (getGrid((xPosition+b.x-1),(yPosition+b.y)) == null){
                validMove = true;
            }
            else{
                 return false;
            }
        }
        return validMove;
    }

    public boolean validRight(){
        Point[] body = currentPiece.getBody();
        boolean validMove = false;
        for (Point b : body){
            if (getGrid((xPosition+b.x+1),(yPosition+b.y)) == null){
                validMove = true;
            }
            else{
                return false;
            }
        }
        return validMove;
    }

    public void moveLeft(){
        Point[] body = currentPiece.getBody();
        for (Point b : body){
            setGrid(xPosition+b.x-1, yPosition+b.y, tempGrid, getNum(currentPiece.getType()));
            setGrid(xPosition+b.x, yPosition+b.y, tempGrid, EMPTY);
        }
        xPosition = xPosition -1;
    }

    public void moveRight(){
        Point[] body = currentPiece.getBody();
        for (Point b : body){
            setGrid(xPosition+b.x+1, yPosition+b.y, tempGrid, getNum(currentPiece.getType()));
            setGrid(xPosition+b.x, yPosition+b.y, tempGrid, EMPTY);
        }
        xPosition = xPosition + 1;
    }

    public void moveDown(){
        Point[] body = currentPiece.getBody();
        for (Point b : body){
            setGrid(xPosition+b.x, yPosition+b.y-1, tempGrid, getNum(currentPiece.getType()));
            setGrid(xPosition+b.x, yPosition+b.y, tempGrid, EMPTY);
        }
        yPosition = yPosition - 1;
    }

    public int lowestSkirt (){
        int [] skirt = currentPiece.getSkirt();
        int lowest = skirt[0];
        for (int i: skirt){
            if (i < lowest){
                lowest = i;
            }
        }
        return lowest;
    }

    public boolean placePiece() {
        Point[] body = currentPiece.getBody();
        boolean set = false;
        ArrayList<Integer> fullRows = new ArrayList<>();

        //if (yPosition-lowestSkirt()-1<0){
            //set = true;
        //}
        //else{
            for (Point b : body){
                if (yPosition+lowestSkirt()-1<0){
                    set = true;
                }
                else if (getGrid(xPosition+b.x, yPosition+b.y-1)!=null){
                    set = true;
                }
            }
        //}

        if (set){
            for (Point b : body) {
                setGrid(xPosition+b.x, yPosition+b.y, officialGrid, getNum(currentPiece.getType()));
                if (yPosition+b.y+1>blocksInColumn[xPosition+b.x]){
                    blocksInColumn[xPosition+b.x]= yPosition+b.y+1;
                }
                blocksInRow[yPosition+b.y]++;
                if (blocksInColumn[xPosition+b.x]>maxHeight){
                    maxHeight = blocksInColumn[xPosition+b.x];
                }
                if (blocksInRow[yPosition+b.y]==getWidth()){
                    fullRows.add(yPosition+b.y);
                }
            }
            if (fullRows.size()>0){
                for (int i : fullRows)
                clearRow(i);
            }
            return true;
        }
        return false;
    }

    public void clearRow(int rowToClear){
        for (int i = 0; i < getWidth(); i++){
            setGrid(i, rowToClear, officialGrid, EMPTY);
            blocksInColumn[i]--;
        }
        for (int y = rowToClear; y < maxHeight; y++){
            for (int x = 0; x < blocksInColumn.length; x++){
                if (y == getHeight() - 1){
                    setGrid(x, y, officialGrid, EMPTY);
                    blocksInRow[y] = 0;

                } else{
                    int value = getNum(getGrid(x, y + 1));
                    setGrid(x, y, officialGrid, value);
                    blocksInRow[y] = blocksInRow[y + 1];
                }
            }
        }
        maxHeight -= 1;
        System.out.println(maxHeight);
    }

    @Override
    public Board testMove(Action act) { return null; }

    @Override
    public Piece getCurrentPiece() { return currentPiece; }

    @Override
    public Point getCurrentPiecePosition() { return new Point(xPosition, yPosition); }

    @Override
    public void nextPiece(Piece p, Point spawnPosition) {
        currentPiece = p;
        xPosition = spawnPosition.x;
        yPosition = spawnPosition.y;
        Point[] body = currentPiece.getBody();
        for (Point a : body){
            if (yPosition+a.y<0 | yPosition+a.y>tempGrid.length){
                throw new IllegalArgumentException("spawnPosition is out of bounds.");
            }
            else if(xPosition+a.x<0 | xPosition+a.x > tempGrid[0].length){
                throw new IllegalArgumentException("spawnPosition is out of bounds.");
            }
            else if (getGrid(xPosition+a.x, yPosition+a.y) != null){
                throw new IllegalArgumentException("spawnPosition is occupied.");
            }
            setGrid(xPosition+a.x, yPosition+a.y, tempGrid, getNum(p.getType()));
        }
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof TetrisBoard)) return false;
        TetrisBoard otherBoard = (TetrisBoard) other;
        if(!(currentPiece.equals((otherBoard).getCurrentPiece()))){
            return false;
        }
        else if(!(xPosition == (otherBoard).xPosition && yPosition == (otherBoard).yPosition)){
            return false;
        }
        else if(!(equalGrid(otherBoard))){
            return false;
        }
        return true;
    }

    public boolean equalGrid(TetrisBoard other){
        int test[][] = other.getOfficialGrid();
        if(!(officialGrid.length == test.length && officialGrid[0].length == test[0].length)){
            return false;
        }
        for(int y = 0; y < officialGrid.length; y++){
            for(int x = 0; x < officialGrid[0].length; x++){
                if(!(other.getGrid(x,y).equals(getGrid(x,y)))){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    // constant time
    public Result getLastResult() { return lastResult; }

    @Override
    // constant time
    public Action getLastAction() { return lastAction; }

    @Override
    // constant time
    public int getRowsCleared() { return -1; }

    @Override
    // constant time
    public int getWidth() { return officialGrid[0].length; }

    @Override
    // constant time
    public int getHeight() { return officialGrid.length; }

    @Override
    // constant time
    public int getMaxHeight() {
        //System.out.println(maxHeight);
        return maxHeight;
    }

    @Override
    public int dropHeight(Piece piece, int x) {
        /*int [] skirt = piece.getSkirt();
        int dropHeight = 0;
        for (int i = 0; i < skirt.length; i++){
            if (skirt[i] != Integer.MAX_VALUE && getColumnHeight(x+i) > dropHeight){
                dropHeight = getColumnHeight(x+i);
                System.out.println(dropHeight);
            }
        }
        return dropHeight;*/

        int dropHeight = yPosition+lowestSkirt();
        while(!placePiece()){
            moveDown();
            dropHeight--;
        }
        return dropHeight;
    }

    /*public void drop(){
        int y = dropHeight(currentPiece,xPosition);
        ArrayList<Integer> fullRows = new ArrayList<>();
        Point[] body = currentPiece.getBody();

        while(!placePiece()||yPosition+lowestSkirt()>y){
            moveDown();
        }
        *//*while (yPosition+lowestSkirt()>y){
            moveDown();
        }
        placePiece();*//*
        *//*for (Point b : body){
            setGrid(xPosition+b.x, y+b.y, officialGrid, getNum(currentPiece.getType()));
            setGrid(xPosition+b.x, yPosition+b.y, officialGrid, EMPTY);
        }
        yPosition = y;

        for (Point b : body) {
            if (yPosition+b.y+1>blocksInColumn[xPosition+b.x]){
                blocksInColumn[xPosition+b.x]= y+b.y+1;
            }
            blocksInRow[yPosition+b.y]++;
            if (blocksInColumn[xPosition+b.x]>maxHeight){
                maxHeight = blocksInColumn[xPosition+b.x];
            }
            if (blocksInRow[yPosition+b.y]==getWidth()){
                fullRows.add(yPosition+b.y);
            }
        }
        if (fullRows.size()>0){
            for (int i : fullRows)
                clearRow(i);
        }*//*
    }*/

    @Override
    // constant time
    public int getColumnHeight(int x) { return blocksInColumn[x]; }

    @Override
    // constant time
    public int getRowWidth(int y) {
        return blocksInRow[y];
    }

    @Override
    // constant time
    public Piece.PieceType getGrid(int x, int y) {
        int value = officialGrid[officialGrid.length-1-y][x];
        String name = "";

        if (value == T){ name = "T";}
        else if (value == SQUARE){ name = "SQUARE";}
        else if (value == STICK){ name = "STICK";}
        else if (value == LEFT_L){ name = "LEFT_L";}
        else if (value == RIGHT_L){ name = "RIGHT_L";}
        else if (value == LEFT_DOG){ name = "LEFT_DOG";}
        else if (value == RIGHT_DOG){ name = "RIGHT_DOG";}
        else if (value == EMPTY){ return null;}

        return Piece.PieceType.valueOf(name);
    }

    public void setGrid(int x, int y, int[][] arr, int type) {
        arr[tempGrid.length-y-1][x] = type;
    }
}

class Test{
    public static void main(String args[]){
        TetrisBoard b = new TetrisBoard(5, 4);
        //System.out.println(b.getTheGrid()[0][0]);
        b.getOfficialGrid()[1][2] = 3;
        System.out.println(b.getGrid(2,2));
    }
}
