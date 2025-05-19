import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class AStarSearch extends Solver {
    
    public AStarSearch(Board initialBoard, int heuristicChoice) {
        super(initialBoard, heuristicChoice);
    }
    
    @Override
    public Solution solve() {
        // Antrian prioritas berdasarkan f(n) = g(n) + h(n) terendah
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Board> closedSet = new HashSet<>(); // Jika sudah dikunjungi
        
        Node startNode = new Node(initialBoard, heuristicChoice);
        openSet.add(startNode);
        
        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            nodesVisited++;
            
            if (current.getBoard().isSolved()) {
                return new Solution(current.getPath());
            }
            closedSet.add(current.getBoard());
            
            List<int[]> legalMoves = current.getBoard().getLegalMoves();
            for (int[] move : legalMoves) {
                int vehicleIdx = move[0];
                int moveAmount = move[1];
                
                Board newBoard = current.getBoard().applyMove(vehicleIdx, moveAmount);
                if (closedSet.contains(newBoard)) {
                    continue;
                }
                
                Node successor = new Node(current, newBoard, vehicleIdx, moveAmount, heuristicChoice);
                
                boolean foundBetter = false;
                for (Node openNode : openSet) {
                    if (openNode.getBoard().equals(newBoard) && openNode.getCost() <= successor.getCost()) {
                        foundBetter = true;
                        break;
                    }
                }
                
                if (!foundBetter) {
                    openSet.add(successor);
                }
            }
        }
        return null; // Jika tidak ditemukan solusi
    }
    
    @Override
    public String getName() {
        return "A* Search";
    }
}