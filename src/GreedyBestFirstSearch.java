import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class GreedyBestFirstSearch extends Solver {
    
    public GreedyBestFirstSearch(Board initialBoard, int heuristicChoice) {
        super(initialBoard, heuristicChoice);
    }
    
    @Override
    public Solution solve() {
        // Antrian prioritas berdasarkan heuristic terendah
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(Node::getHeuristic));
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
                
                boolean alreadyInOpenSet = false;
                for (Node openNode : openSet) {
                    if (openNode.getBoard().equals(newBoard)) {
                        alreadyInOpenSet = true;
                        break;
                    }
                }
                
                if (!alreadyInOpenSet) {
                    openSet.add(successor);
                }
            }
        }
        return null; // Jika tidak ditemukan solusi
    }
    
    @Override
    public String getName() {
        return "Greedy Best First Search";
    }
}