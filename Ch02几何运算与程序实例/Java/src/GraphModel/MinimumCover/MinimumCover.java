package GraphModel.MinimumCover;

import gurobi.*;
import java.util.ArrayList;

public class MinimumCover {
    public MinimumCover(){

    }
    public static ArrayList<ArrayList<Integer>> getSubsets(ArrayList<Integer> subList) {
        ArrayList<ArrayList<Integer>> allsubsets = new ArrayList<ArrayList<Integer>>();
        int max = 1 << subList.size();
        for(int loop = 0; loop < max; loop++) {
            int index = 0;
            int temp = loop;
            ArrayList<Integer> currentCharList = new ArrayList<Integer>();
            while(temp > 0) {
                if((temp & 1) > 0) {
                    currentCharList.add(subList.get(index));
                }
                temp>>=1;
                index++;
            }
            allsubsets.add(currentCharList);
        }
        return allsubsets;
    }

    public static ArrayList<boolean[]> opti(Graph g){
        boolean[] dispatch =new boolean[g.edgesNum];
        boolean[] dispatchV =new boolean[g.vertexNum];
        ArrayList<Integer>vertexes=new ArrayList<>();
        ArrayList<int[]>cities=Graph.readTxtFile("E://city_graph.txt");
        int[] citytimes=new int[cities.size()];
        for(int i=0;i<cities.size();i++){
            citytimes[i]=cities.get(i)[3];
        }

        int m=g.edgesNum;
        int n =g.vertexNum;

        for(int i=0;i<m;i++){
            vertexes.add(i);
        }

        ArrayList<ArrayList<Integer>>subLists=getSubsets(vertexes);

        try {
            GRBEnv env   = new GRBEnv();
            GRBModel model = new GRBModel(env);

            // Create variables

            GRBVar[] x=model.addVars(m,GRB.BINARY);
            GRBVar[] v=model.addVars(n,GRB.BINARY);

            GRBQuadExpr obj=new GRBQuadExpr();
            for(int i=0;i<m;i++){
                obj.addTerm(1,x[i]);
            }

            model.setObjective(obj,GRB.MINIMIZE);

            // or 若与顶点i相连的边xj为true，则vi为true
            for(int i=0;i<n;i++){
                GRBLinExpr expr2 = new GRBLinExpr();
                int numNei=g.getNeighbour(i).size();
                for(int j:g.getNeighbour(i))
                    expr2.addTerm(1, x[g.getEdgeID(i, j)]);
                expr2.addTerm(-numNei, v[i]);
                model.addConstr(expr2, GRB.LESS_EQUAL, 0, "OR_Right");
                model.addConstr(expr2, GRB.GREATER_EQUAL, 1-numNei, "OR_Left");
            }

            //覆盖所有顶点
            GRBLinExpr expr3=new GRBLinExpr();
            for (int i = 0; i < n; i++) {
                expr3.addTerm(1, v[i]);
            }
            model.addConstr(expr3, GRB.EQUAL, n, "All Nodes");

            model.optimize();

            String edgeS="edges: ";
            String vertexS="nodes: ";
            for(int i=0;i<x.length;i++){
                if(x[i].get(GRB.DoubleAttr.X)==0){
                    dispatch[i]=false;
                }else {
                    dispatch[i]=true;
                    edgeS+="("+String.valueOf(g.edges[i].start.index)+","+String.valueOf(g.edges[i].end.index)+")";
                }
            }
            int count=0;
            for(int i=0;i<v.length;i++){
                if(v[i].get(GRB.DoubleAttr.X)==0){
                    dispatchV[i]=false;
                }else {
                    dispatchV[i] = true;
                    if(count>0) {
                        vertexS += "," + String.valueOf(g.nodes[i].index);
                    }else {
                        vertexS +=String.valueOf(g.nodes[i].index);
                    }
                    count++;
                }
            }
            System.out.println(edgeS+"\n"+vertexS);
            // Dispose of model and environment
            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". " +
                    e.getMessage());
            e.printStackTrace();
        }
        ArrayList<boolean[]>Dispatch=new ArrayList<>();
        Dispatch.add(dispatch);
        Dispatch.add(dispatchV);
        return Dispatch;
    }
}
