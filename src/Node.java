import java.util.ArrayList;
import java.util.List;

public class Node implements Comparable<Node> {
    private Board board;
    private Node parent;
    private int pieceIdx;     // index yang digerakkan
    private int moveAmount;   // banyak langkah yang digeser
    private int cost;         // jumlah langkah 
    private int heuristic;    

    // Node awal
    public Node(Board board, int heuristicChoice) {
        this.board = board;
        this.parent = null;
        this.pieceIdx = -1;
        this.moveAmount = 0;
        this.cost = 0;
    }

    // Node hasil dari pergerakan
    public Node(Node parent, Board board, int pieceIdx, int moveAmount, int heuristicChoice) {
        this.board = board;
        this.parent = parent;
        this.pieceIdx = pieceIdx;
        this.moveAmount = moveAmount;
        this.cost = parent.cost + 1;
    }

    public List<Node> getPath() {
        List<Node> path = new ArrayList<>();
        Node current = this;
        while (current != null) {
            path.add(0, current);
            current = current.parent;
        }
        return path;
    }

    public int getFValue() {
        return cost + heuristic;
    }

    public String getMoveDescription() {
        if (parent == null) return "Initial state";
        Piece piece = board.getPieces().get(pieceIdx);
        String arah = piece.isHorizontal() ?
                (moveAmount > 0 ? "kanan" : "kiri") :
                (moveAmount > 0 ? "bawah" : "atas");
        return String.format("%c-%s", piece.getId(), arah);
    }

    public Board getBoard() {
        return board;
    }

    public Node getParent() {
        return parent;
    }

    public int getPieceIdx() {
        return pieceIdx;
    }

    public int getMoveAmount() {
        return moveAmount;
    }

    public int getCost() {
        return cost;
    }

    public int getHeuristic() {
        return heuristic;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.getFValue(), other.getFValue());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node other = (Node) obj;
        return board.equals(other.board);
    }

    @Override
    public int hashCode() {
        return board.hashCode();
    }
}