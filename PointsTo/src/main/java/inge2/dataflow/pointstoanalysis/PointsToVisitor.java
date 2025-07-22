package inge2.dataflow.pointstoanalysis;

import soot.jimple.*;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JimpleLocal;

import java.util.HashSet;
import java.util.Set;

public class PointsToVisitor extends AbstractStmtSwitch<Void> {

    private final PointsToGraph pointsToGraph;

    public PointsToVisitor(PointsToGraph pointsToGraph) {
        this.pointsToGraph = pointsToGraph;
    }

    @Override
    public void caseAssignStmt(AssignStmt stmt) {
        boolean isLeftLocal = stmt.getLeftOp() instanceof JimpleLocal;
        boolean isRightLocal = stmt.getRightOp() instanceof JimpleLocal;

        boolean isLeftField = stmt.getLeftOp() instanceof JInstanceFieldRef;
        boolean isRightField = stmt.getRightOp() instanceof JInstanceFieldRef;

        boolean isRightNew = stmt.getRightOp() instanceof AnyNewExpr;

        if (isRightNew) { // x = new A()
            processNewObject(stmt);
        } else if (isLeftLocal && isRightLocal) { // x = y
            processCopy(stmt);
        } else if (isLeftField && isRightLocal) { // x.f = y
            processStore(stmt);
        } else if (isLeftLocal && isRightField) { // x = y.f
            processLoad(stmt);
        }
    }

    private void processNewObject(AssignStmt stmt) {
        String leftVariableName = stmt.getLeftOp().toString();
        Node nodeName = pointsToGraph.getNodeName(stmt);
        Set<Node> a = new HashSet<>();
        a.add(nodeName); // creamos conjunto de nodos con el numero de linea

         pointsToGraph.setNodesForVariable(leftVariableName,a); // agregamos el nodo como alcanzable por la variable
         pointsToGraph.nodes.add(nodeName); // agregamos el nuevo nodo al grafo
    }

    private void processCopy(AssignStmt stmt) { // x=y
        String leftVariableName = stmt.getLeftOp().toString();
        String rightVariableName = stmt.getRightOp().toString();

        pointsToGraph.mapping.put(leftVariableName, pointsToGraph.mapping.get(rightVariableName)); // L'(x)=L(y)
    }

    private void processStore(AssignStmt stmt) { // x.f = y
        JInstanceFieldRef leftFieldRef = (JInstanceFieldRef) stmt.getLeftOp();
        String leftVariableName = leftFieldRef.getBase().toString();
        String fieldName = leftFieldRef.getField().getName();
        String rightVariableName = stmt.getRightOp().toString();

        for(Node nodo1: pointsToGraph.mapping.get(rightVariableName)){ //para cada par de nodos n1 en L(x) y n2 en L(y), agregamos una arista (n1,f,n2)
            for(Node nodo2: pointsToGraph.mapping.get(leftVariableName)){
                pointsToGraph.addEdge(nodo2, fieldName, nodo1);
            }
        }
    }

    private void processLoad(AssignStmt stmt) { // x = y.f
        String leftVariableName = stmt.getLeftOp().toString();
        JInstanceFieldRef rightFieldRef = (JInstanceFieldRef) stmt.getRightOp();
        String rightVariableName = rightFieldRef.getBase().toString();
        String fieldName = rightFieldRef.getField().getName();
        Set<Node> a = new HashSet<>();

        for(Node nodo: pointsToGraph.mapping.get(rightVariableName)){ //agregamos los nodos alcanzables por y usando f
            a.addAll(pointsToGraph.getReachableNodesByField(nodo, fieldName));
        }
        pointsToGraph.mapping.put(leftVariableName,a);
    }
}
