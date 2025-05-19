import java.util.List;

public class Solution {
    private List<Node> path;

    public Solution(List<Node> path) {
        this.path = path;
    }

    public List<Node> getMoves() {
        return path.subList(1, path.size()); 
    }

    public void printSteps() {
        System.out.println("Papan Awal:");
        System.out.println(path.get(0).getBoard().toString());

        for (int i = 1; i < path.size(); i++) {
            Node move = path.get(i);
            Piece piece = move.getBoard().getPieces().get(move.getPieceIdx());

            String direction;
            if (piece.isHorizontal()) {
                direction = move.getMoveAmount() > 0 ? "right" : "left";
            } else {
                direction = move.getMoveAmount() > 0 ? "down" : "up";
            }

            System.out.println("Gerakan " + i + ": " + piece.getId() + "-" + direction);
            System.out.println(move.getBoard().toString());
        }
    }
}