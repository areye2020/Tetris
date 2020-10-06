package assignment;

public class LinkedList {
    private Node head;
    private Node tail;
    private Node current;
    public LinkedList(){
        head = null;
        tail = null;
        current = null;
    }
    public void addNode(TetrisPiece addPiece){
        Node nodeToAdd = new Node(addPiece);
        if(head == null){
            head = nodeToAdd;
            tail = nodeToAdd;
            current = head;
            nodeToAdd.setRight(head);
            nodeToAdd.setLeft(tail);
        }
        else{
            tail.setRight(nodeToAdd);
            tail = nodeToAdd;
            head.setLeft(tail);
            tail.setRight(head);
        }
    }
    public TetrisPiece getClockwise(){
        current = current.getRight();
        return current.getPiece();
    }

    public TetrisPiece getCounterClockwise(){
        current = current.getLeft();
        return current.getPiece();
    }
}
class Node{
    private TetrisPiece element;
    private Node right;
    private Node left;
    public Node(TetrisPiece p){
        element = p;
        right = null;
        left = null;
    }
    public void setRight(Node n){
        right = n;
    }
    public void setLeft(Node n){
        left = n;
    }
    public TetrisPiece getPiece(){
        return element;
    }
    public Node getRight(){
        return right;
    }
    public Node getLeft(){
        return left;
    }
}