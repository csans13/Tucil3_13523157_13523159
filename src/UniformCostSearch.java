import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class UniformCostSearch extends Solver {
    
    public UniformCostSearch(Board initialBoard) {
        super(initialBoard, 0); // UCS tidak menggunakan heuristic
    }
    
    @Override
    public Solution solve() {
        // Antrian prioritas berdasarkan cost terendah
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(Node::getCost));
        Set<Board> closedSet = new HashSet<>(); // Jika sudah dikunjungi
        
        Node startNode = new Node(initialBoard, 0);
        openSet.add(startNode);
        
        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            nodesVisited++;
            
            if (current.getBoard().isSolved()) {
                return new Solution(current.getPath());
            }
            
            if (closedSet.contains(current.getBoard())) {
                continue;
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
                
                Node successor = new Node(current, newBoard, vehicleIdx, moveAmount, 0);
                
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
        return "Uniform Cost Search";
    }
}