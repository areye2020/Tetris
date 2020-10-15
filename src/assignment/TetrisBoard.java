package assignment;

import assignment.Board.Action;

import java.awt.*;
import java.util.ArrayList;

/**
 * Represents a Tetris board -- essentially a 2-d grid of piece types (or nulls). Supports
 * tetris pieces and row clearing.  Does not do any drawing or have any idea of
 * pixels. Instead, just represents the abstract 2-d board.
 */
public final class TetrisBoard implements Board {

    //constants representing content of a specific position on the grid
    private static final int EMPTY = 0;
    private static final int T = 1;
    private static final int SQUARE = 2;
    private static final int STICK = 3;
    private static final int LEFT_L = 4;
    private static final int RIGHT_L = 5;
    private static final int LEFT_DOG = 6;
    private static final int RIGHT_DOG = 7;

    private int[][] tempGrid; //grid including the current piece
    private int[][] officialGrid; //grid not including the current piece
    private Action lastAction;
    private Board.Result lastResult;
    private Piece currentPiece;
    private int xPosition; //current piece's x coordinate
    private int yPosition; //current piece's y coordinate
    private int[] blocksInRow; //the number of blocks in each row
    private int[] blocksInColumn; //the y coordinate of the highest block in each column
    private int maxHeight;
    private int rowsCleared; //number of rows cleared by the last action

    // JTetris will use this constructor
    public TetrisBoard(int width, int height) {
        tempGrid = new int[height][width];
        officialGrid = new int[height][width];
        blocksInRow = new int[height];
        blocksInColumn = new int[width];
        maxHeight = 0;
        rowsCleared = 0;
        lastAction = Action.NOTHING;
    }

    public int[][] getOfficialGrid(){
        return officialGrid;
    }

    public int[][] getTempGrid(){
        return tempGrid;
    }

    //returns the constant representing the PieceType p
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

    //moving the current piece
    @Override
    public Result move(Action act) {
        Result toReturn = null;
        if (act.equals(Action.LEFT)){ //if currentPiece needs to move left
            if (xPosition+getFirstColumn()-1<0){ //makes sure that the piece is not at the left edge of the grid
                toReturn = Result.OUT_BOUNDS;
            }
            else if (!validLeft()){ //makes sure that there is no other piece directly left of the currentPiece
                toReturn = Result.OUT_BOUNDS;
            }
            else{
                moveLeft();
                toReturn = Result.SUCCESS;
            }
            rowsCleared = 0;
        }
        else if (act.equals(Action.RIGHT)){ //if currentPiece needs to move right
            if (xPosition+getLastColumn()+1>=tempGrid[0].length){ //makes sure that the piece is not at the right edge of the grid
                toReturn = Result.OUT_BOUNDS;
            }
            else if (!validRight()){ //makes sure that there is no other piece directly right of the currentPiece
                toReturn = Result.OUT_BOUNDS;
            }
            else{
                moveRight();
                toReturn = Result.SUCCESS;
            }
            rowsCleared = 0;
        }
        else if (act.equals(Action.DOWN)){ //if currentPiece needs to move down
            if (placePiece()){ //if the currentPiece is at the bottom of the grid or there is a piece directly below the currentPiece
                toReturn = Result.PLACE;
            }
            else{
                moveDown(); //moves the currentPiece down one position
                toReturn = Result.SUCCESS;
                rowsCleared = 0;
            }
        }
        else if (act.equals(Action.DROP)){ //if currentPiece needs to drop
            drop(); //drops the currentPiece
            toReturn = Result.PLACE;
        }
        else if (act.equals(Action.CLOCKWISE)){ //if the currentPiece needs to rotate clockwise once
            if (clockwise()){ //rotates the currentPiece if it can be rotated
                toReturn = Result.SUCCESS;
            }
            else{ //if currentPiece cannot be rotated (including wallKicks)
                toReturn = Result.OUT_BOUNDS;
            }
            rowsCleared = 0;
        }
        else if (act.equals(Action.COUNTERCLOCKWISE)){ //if the currentPiece needs to rotate counterclockwise once
            if (counterClockwise()){ //rotates the currentPiece if it can be rotated
                toReturn = Result.SUCCESS;
            }
            else{ //if the currentPiece cannot be rotated (including wallKicks)
                toReturn = Result.OUT_BOUNDS;
            }
            rowsCleared = 0;
        }
        else if (act.equals(Action.NOTHING)){ //if nothing needs to be done
            toReturn = Result.SUCCESS;
            rowsCleared = 0;
        }
        lastAction = act;
        lastResult = toReturn;
        return toReturn;
    }

    //returns true if the currentPiece can be rotated clockwise and rotates the currentPiece
    public boolean clockwise(){
        int initialOrientation = currentPiece.getRotationIndex();
        TetrisPiece rotated = (TetrisPiece) currentPiece.clockwisePiece();
        Point [] body = rotated.getBody();
        Point [] wallKicks;
        Point officialWallKick = null;
        boolean rotate = true;

        //if there are no wallKicks (like for the square piece)
        if (rotated.getWallKicks((1))==null){
            for (Point b : body){
                if (getGrid(xPosition + b.x, yPosition + b.y)!=null){ //checks if the positions that the rotated piece will occupy is already occupied
                    rotate = false;
                }
            }
            officialWallKick = new Point (0, 0); //symbolizes no wallKicks (0, 0)
        }
        else{ //if there are wallKicks
            wallKicks = rotated.getWallKicks(1)[initialOrientation];
            for (int kick = 0; kick < wallKicks.length; kick++){ //for each wallKick
                if (officialWallKick == null){ //if there is not a wallKick that works yet (officialWallKick)
                    rotate = true;
                    for (Point b : body) { //checks if the positions that the rotated piece will occupy (with the wallKick) is already occupied
                        if ((xPosition + b.x + wallKicks[kick].x) >= getWidth()-1 || (xPosition + b.x + wallKicks[kick].x) < 0){
                            rotate = false;
                        }
                        else if ((yPosition + b.y + wallKicks[kick].y) >= getHeight()-1 || (yPosition + b.y + wallKicks[kick].y) < 0){
                            rotate = false;
                        }
                        else if (getGrid(xPosition + b.x + wallKicks[kick].x, yPosition + b.y + wallKicks[kick].y) != null) {
                            rotate = false;
                        }
                    }
                    if (rotate){ //if all the positions are empty
                        officialWallKick = wallKicks[kick]; //set the officialWallKick
                    }
                }
            }
        }

        if (rotate){ //if the currentPiece can be rotated clockwise
            Point [] originalBody = currentPiece.getBody();
            for (Point b : originalBody){
                setGrid(xPosition+b.x, yPosition + b.y, tempGrid, EMPTY); //each of the currentPiece's position is set to empty
            }
            currentPiece = rotated;
            for (Point b : body){ //sets the new currentPiece's positions to the correct constant
                setGrid(xPosition+b.x+officialWallKick.x, yPosition + b.y+officialWallKick.y, tempGrid, getNum(currentPiece.getType()));
            }
            xPosition = xPosition + officialWallKick.x;
            yPosition = yPosition + officialWallKick.y;
            return true; //if currentPiece is rotated
        }
        return false; //currentPiece is not rotated
    }

    //returns true if the currentPiece can be rotated counterclockwise and rotates the currentPiece
    public boolean counterClockwise(){
        int initialOrientation = currentPiece.getRotationIndex();
        TetrisPiece rotated = (TetrisPiece) currentPiece.counterclockwisePiece();
        Point [] body = rotated.getBody();
        Point [] wallKicks;
        Point officialRot = null;
        boolean rotate = true;

        //if there are no wallKicks (like for the square piece)
        if (rotated.getWallKicks((-1))==null){
            for (Point b : body){ //checks if the positions that the rotated piece will occupy is already occupied
                if (getGrid(xPosition + b.x, yPosition + b.y)!=null){
                    rotate = false;
                }
            }
            officialRot = new Point (0, 0); //symbolizes no wallKicks (0, 0)
        }
        else{ //if there are wallKicks
            wallKicks = rotated.getWallKicks(-1)[initialOrientation];
            for (int kick = 0; kick < wallKicks.length; kick++){ //for each wallKick
                if (officialRot == null){ //if there is not a wallKick that works yet (officialWallKick)
                    rotate = true;
                    for (Point b : body) { //checks if the positions that the rotated piece will occupy (with the wallKick) is already occupied
                        if (xPosition + b.x + wallKicks[kick].x >= getWidth()-1 || xPosition + b.x + wallKicks[kick].x < 0){
                            rotate = false;
                        }
                        else if (yPosition + b.y + wallKicks[kick].y >= getHeight()-1 || yPosition + b.y + wallKicks[kick].y <0){
                            rotate = false;
                        }
                        else if (getGrid(xPosition + b.x + wallKicks[kick].x, yPosition + b.y + wallKicks[kick].y) != null) {
                            rotate = false;
                        }
                    }
                    if (rotate){ //if all the positions are empty
                        officialRot = wallKicks[kick]; //set the officialWallKick
                    }
                }
            }
        }

        if (rotate){ //if the currentPiece can be rotated clockwise
            Point [] originalBody = currentPiece.getBody();
            for (Point b : originalBody){
                setGrid(xPosition + b.x,yPosition + b.y , tempGrid, EMPTY); //each of the currentPiece's position is set to empty
            }
            currentPiece = rotated;
            for (Point b : body){ //sets the new currentPiece's positions to the correct constant
                setGrid(xPosition + b.x + officialRot.x, yPosition + b.y + officialRot.y, tempGrid, getNum(currentPiece.getType()));
            }
            xPosition = xPosition + officialRot.x;
            yPosition = yPosition + officialRot.y;
            return true; //if currentPiece is rotated
        }
        return false; //currentPiece is not rotated
    }

    //get the first left column of the currentPiece's bounding box that actually contains a block
    public int getFirstColumn(){
        int [] skirt = currentPiece.getSkirt();
        int firstColumn = 0;
        while(skirt[firstColumn] == Integer.MAX_VALUE){
            firstColumn++;
        }
        return firstColumn;
    }

    //get the first right column of the currentPiece's bounding box that actually contains a block
    public int getLastColumn(){
        int [] skirt = currentPiece.getSkirt();
        int lastColumn = skirt.length-1;
        while(skirt[lastColumn] == Integer.MAX_VALUE){
            lastColumn--;
        }
        return lastColumn;
    }

    //returns true if the currentPiece can be shifted left
    public boolean validLeft(){
        Point[] body = currentPiece.getBody();
        boolean validMove = false;
        for (Point b : body){ //for each block in the currentPiece's body
            if (getGrid((xPosition+b.x-1),(yPosition+b.y)) == null){ //if the coordinate to the left is empty
                validMove = true;
            }
            else{ //if that coordinate is not empty
                 return false; //the currentPiece cannot be moved left
            }
        }
        return validMove;
    }

    //returns true if the currentPiece can be shifted right
    public boolean validRight(){
        Point[] body = currentPiece.getBody();
        boolean validMove = false;
        for (Point b : body){ //for each block in the currentPiece's body
            if (getGrid((xPosition+b.x+1),(yPosition+b.y)) == null){ //if the coordinate to the right is empty
                validMove = true;
            }
            else{ //if that coordinate is not empty
                return false; //the currentPiece cannot be moved left
            }
        }
        return validMove;
    }

    //moves the currentPiece to the left
    public void moveLeft(){
        Point[] body = currentPiece.getBody();
        for (Point b : body){ //for each block in the currentPiece's body
            setGrid(xPosition+b.x-1, yPosition+b.y, tempGrid, getNum(currentPiece.getType())); //sets the left coordinate to the constant represented by the currentPiece's pieceType
            setGrid(xPosition+b.x, yPosition+b.y, tempGrid, EMPTY); //sets the current coordinate to empty
        }
        xPosition = xPosition -1; //the x position of the currentPiece is shifted left by one
    }

    //moves the currentPiece to the right
    public void moveRight(){
        Point[] body = currentPiece.getBody();
        for (Point b : body){ //for each block in the currentPiece's body
            setGrid(xPosition+b.x+1, yPosition+b.y, tempGrid, getNum(currentPiece.getType())); //sets the right coordinate to the constant represented by the currentPiece's pieceType
            setGrid(xPosition+b.x, yPosition+b.y, tempGrid, EMPTY); //sets the current coordinate to empty
        }
        xPosition = xPosition + 1; //the x position of the currentPiece is shifted right by one
    }

    //moves the currentPiece down
    public void moveDown(){
        Point[] body = currentPiece.getBody();
        for (Point b : body){ //for each block in the currentPiece's body
            setGrid(xPosition+b.x, yPosition+b.y-1, tempGrid, getNum(currentPiece.getType())); //sets the coordinate to the bottom to the constant represented by the currentPiece's pieceType
            setGrid(xPosition+b.x, yPosition+b.y, tempGrid, EMPTY); //sets the current coordinate to empty
        }
        yPosition = yPosition - 1; //the y position of the currentPiece is shifted down by one
    }

    //officially places the currentPiece
    public boolean placePiece() {
        Point[] body = currentPiece.getBody();
        int lowestSkirt = ((TetrisPiece)currentPiece).lowestSkirt();
        boolean set = false; //true if the currentPiece should be set
        ArrayList<Integer> fullRows = new ArrayList<>();

        for (Point b : body) {
            if (yPosition + lowestSkirt - 1 < 0) { //if the currentPiece is at the bottom
                set = true;
            } else if (getGrid(xPosition + b.x, yPosition + b.y - 1) != null) {//if there is a piece directly below the currentPiece
                set = true;
            }
        }

        //
        if (set){
            for (Point b : body) {
                setGrid(xPosition+b.x, yPosition+b.y, officialGrid, getNum(currentPiece.getType())); //sets the currentPiece in the officialGrid
                if (yPosition+b.y+1>getColumnHeight(xPosition+b.x)){
                    blocksInColumn[xPosition+b.x]= yPosition+b.y+1; //changes the blocks in the column of the specific Point
                }
                blocksInRow[yPosition+b.y]++; //changes the number of blocks in the specific Point's row
                if (getColumnHeight(xPosition+b.x)>maxHeight){
                    maxHeight = blocksInColumn[xPosition+b.x]; //changes max height
                }
                if (getRowWidth(yPosition+b.y)==getWidth()){ //checks if the row is full
                    fullRows.add(yPosition+b.y);
                }
            }
            rowsCleared = fullRows.size();
            if (fullRows.size()>0){ //if there are fullRows
                for (int i : fullRows)
                clearRow(i); //clear each row
            }
            return true;
        }
        return false;
    }

    //clearing row rowToClear
    public void clearRow(int rowToClear){
        for (int i = 0; i < getWidth(); i++){ //that row is set to empty
            setGrid(i, rowToClear, officialGrid, EMPTY);
            blocksInColumn[i]--;
        }
        for (int y = rowToClear; y < maxHeight; y++){ //from rowToClear to the row containing the max height
            for (int x = 0; x < blocksInColumn.length; x++){
                if (y == getHeight() - 1){ //if y is the top most row
                    setGrid(x, y, officialGrid, EMPTY); //y should be empty
                    blocksInRow[y] = 0;

                } else{ //y is not the top most row
                    int value = getNum(getGrid(x, y + 1)); //retrieves the content of the coordinate above the current coordinate
                    setGrid(x, y, officialGrid, value); //sets the current coordinate to that value
                    blocksInRow[y] = blocksInRow[y + 1]; //changes the number of blocks in row y
                }
            }
        }
        maxHeight -= 1; //changes maxHeight
    }

    @Override
    public Board testMove(Action act) {
        TetrisBoard b = new TetrisBoard(this);
        b.move(act);
        return b;
    }

    public TetrisBoard(TetrisBoard original){
        officialGrid = new int[original.getHeight()][original.getWidth()];
        tempGrid = new int[original.getHeight()][original.getWidth()];
        for (int i = 0; i < officialGrid.length; i++){
            System.arraycopy(original.officialGrid[i], 0, officialGrid[i], 0, officialGrid[i].length);
            System.arraycopy(original.tempGrid[i], 0, tempGrid[i], 0, tempGrid[i].length);
        }
        blocksInColumn = new int[original.blocksInColumn.length];
        System.arraycopy(original.blocksInColumn, 0, blocksInColumn, 0, blocksInColumn.length);
        blocksInRow = new int[original.blocksInRow.length];
        System.arraycopy(original.blocksInRow, 0, blocksInRow, 0, blocksInRow.length);
        lastAction = original.getLastAction();
        lastResult = getLastResult();
        currentPiece = new TetrisPiece((TetrisPiece) original.getCurrentPiece(), original.getCurrentPiece().getRotationIndex(), ((TetrisPiece)original.getCurrentPiece()).returnRotations(), original.getCurrentPiece().getBody());
        xPosition = original.xPosition;
        yPosition = original.yPosition;
        maxHeight = original.getMaxHeight();
        rowsCleared = original.rowsCleared;
    }

    //returns the currentPiece
    @Override
    public Piece getCurrentPiece() {
        return currentPiece;
    }

    //returns the currentPiece's position
    @Override
    public Point getCurrentPiecePosition() {
        return new Point(xPosition, yPosition);
    }

    //adds the next piece p at the spawnPosition
    @Override
    public void nextPiece(Piece p, Point spawnPosition) {
        currentPiece = p;
        xPosition = spawnPosition.x;
        yPosition = spawnPosition.y;
        Point[] body = currentPiece.getBody();
        for (Point a : body){
            if (yPosition+a.y<0 | yPosition+a.y>tempGrid.length){ //if spawnPosition is above the grid or below the grid
                throw new IllegalArgumentException("spawnPosition is out of bounds.");
            }
            else if(xPosition+a.x<0 | xPosition+a.x > tempGrid[0].length){ //if spawnPosition is to the left or right of the grid
                throw new IllegalArgumentException("spawnPosition is out of bounds.");
            }
            else if (getGrid(xPosition+a.x, yPosition+a.y) != null){ //if the spawnPosition is already occupied
                throw new IllegalArgumentException("spawnPosition is occupied.");
            }
            setGrid(xPosition+a.x, yPosition+a.y, tempGrid, getNum(p.getType()));
        }
    }

    //returns true if the current TetrisBoard and other object are equal
    @Override
    public boolean equals(Object other) {
        if(!(other instanceof TetrisBoard)) return false;
        TetrisBoard otherBoard = (TetrisBoard) other;
        if(currentPiece != null && (otherBoard).getCurrentPiece() == null){
            return false;
        }
        if(currentPiece == null && (otherBoard).getCurrentPiece() != null){
            return false;
        }
        if (currentPiece == null && (otherBoard).getCurrentPiece() == null){
           if (!(equalGrid(otherBoard))){
               return false;
           }
           else{
               return true;
           }
        }
        if(!(currentPiece.equals((otherBoard).getCurrentPiece()))){ //if the currentPieces are not equal
            return false;
        }
        else if(!(xPosition == (otherBoard).xPosition && yPosition == (otherBoard).yPosition)){ //if the currentPieces' positions are not equal
            return false;
        }
        else if(!(equalGrid(otherBoard))){ //if the contents of the otherBoard and current TetrisBoard are not equal
            return false;
        }
        return true;
    }

    //checks if the contents of the current TetrisBoard and other TetrisBoard are equal
    public boolean equalGrid(TetrisBoard other){
        int test[][] = other.getOfficialGrid();
        if(!(officialGrid.length == test.length && officialGrid[0].length == test[0].length)){ //if the heights and widths are not equal
            return false;
        }
        for(int y = 0; y < officialGrid.length; y++){
            for(int x = 0; x < officialGrid[0].length; x++){
                if (other.getGrid(x,y) != null && getGrid(x,y) ==null){
                    return false;
                }
                else if (other.getGrid(x,y) == null && getGrid(x,y) !=null){
                    return false;
                }
                if(other.getGrid(x,y) == null && getGrid(x,y) ==null){

                }
                else if(!(other.getGrid(x,y).equals(getGrid(x,y)))){ //if the contents at position (x,y) are not equal
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    //returns the last result
    public Result getLastResult() {
        return lastResult;
    }

    @Override
    // returns the last action
    public Action getLastAction() {
        return lastAction;
    }

    @Override
    // returns the rows cleared by the last action
    public int getRowsCleared() {
        return rowsCleared;
    }

    @Override
    //returns the width of the grid
    public int getWidth() {
        return officialGrid[0].length;
    }

    @Override
    //returns the height of the grid
    public int getHeight() {
        return officialGrid.length;
    }

    @Override
    //returns the highest row number that contains a block
    public int getMaxHeight() {
        int max = getColumnHeight(0);
        for (int x = 1; x < getWidth(); x++){
            if (getColumnHeight(x)>max){
                max = getColumnHeight(x);
            }
        }
        maxHeight = max;
        return maxHeight;
    }

    //returns the y value to which the currentPiece is dropped and drops the CurrentPiece to that height
    @Override
    public int dropHeight(Piece p, int x){
        int lowestSkirt = ((TetrisPiece)p).lowestSkirt();
        int y = yPosition;
        Point [] body = p.getBody();
        boolean set = false;
        while (!set){
            for (Point b : body) {
                if (y + lowestSkirt - 1 < 0) { //if the piece is at the bottom
                    set = true;
                } else if (getGrid(x + b.x, y + b.y - 1) != null) {//if the square directly below the piece is occupied
                    set = true;
                }
            }
            if (!set){
                y--;
            }
        }
        return (y+lowestSkirt);
    }

    public void drop(){
        int y = dropHeight(currentPiece, xPosition);
        int lowestSkirt = ((TetrisPiece)currentPiece).lowestSkirt();
        while(yPosition+lowestSkirt>y){
            moveDown();
        }
        placePiece();
    }

    @Override
    //returns the y value of the highest block in column x
    public int getColumnHeight(int x) {
        int height = getHeight()-1;
        while (getGrid(x, height) == null){
            height--;
        }
        blocksInColumn[x] = height+1;
        return blocksInColumn[x];
    }

    @Override
    //returns the number of blocks in row y
    public int getRowWidth(int y) {
        int blocks = 0;
        for (int i = 0; i < officialGrid[y].length; i++){
            if (getGrid(i, y) != null){
                blocks++;
            }
        }
        blocksInRow[y] = blocks;
        return blocksInRow[y];
    }

    @Override
    //returns the PieceType represented in position (x,y)
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

    //sets position (x, y) of grid to type
    public void setGrid(int x, int y, int[][] grid, int type) {
        grid[tempGrid.length-y-1][x] = type;
    }
}
