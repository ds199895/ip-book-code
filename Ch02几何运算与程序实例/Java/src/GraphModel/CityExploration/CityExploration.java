package GraphModel.CityExploration;

import gurobi.*;

import java.util.ArrayList;

public class CityExploration {
    int s;
    int e;
    int T;
    public CityExploration(int s, int e, int T){
        this.s=s;
        this.e=e;
        this.T=T;
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

    public ArrayList<boolean[]> opti(Graph g){
        boolean[] dispatch =new boolean[g.edgesNum];
        boolean[] dispatchV =new boolean[g.vertexNum];
        ArrayList<Integer>vertexes=new ArrayList<>();
        ArrayList<int[]>cities= Graph.readTxtFile("E://city_graph.txt");
        int[] citytimes=new int[cities.size()];
        for(int i=0;i<cities.size();i++){
            citytimes[i]=cities.get(i)[3];
        }

        int m=g.edgesNum;
        int n =g.vertexNum;

        for(int i=0;i<m;i++){
            vertexes.add(i);
        }

        try {
            GRBEnv env   = new GRBEnv();
            GRBModel model = new GRBModel(env);

            // Create variables
            GRBVar[] x=model.addVars(m,GRB.BINARY);
            GRBVar[] v=model.addVars(n,GRB.BINARY);

            GRBQuadExpr obj=new GRBQuadExpr();
            for(int i=0;i<m;i++){
                obj.addTerm(g.edges[i].weight,x[i]);
            }

            model.setObjective(obj,GRB.MAXIMIZE);

            // 时间约束
            GRBLinExpr expr2 = new GRBLinExpr();
            for (int i = 0; i <m; i++) {
                expr2.addTerm(citytimes[i], x[i]);
            }
            model.addConstr(expr2, GRB.LESS_EQUAL, T, "times");

            //连接约束
            for (int i = 0; i < n; i++) {
                if(i!=s&&i!=e) {
                    GRBQuadExpr expr3 = new GRBQuadExpr();
                    for (Integer j : g.getNeighbour(i))
                        expr3.addTerm(1.0 / 2, x[g.getEdgeID(i, j)]);
                    model.addQConstr(expr3, GRB.EQUAL, v[i], "deg2_" + String.valueOf(i));
                }else if(i==s||i==e){
                    GRBQuadExpr expr4 = new GRBQuadExpr();
                    for (Integer j : g.getNeighbour(i))
                        expr4.addTerm(1.0 , x[g.getEdgeID(i, j)]);
                    model.addQConstr(expr4, GRB.EQUAL, 1, "deg2_" + String.valueOf(i));
                }
            }
            model.optimize();

            String edgeS="edges: ";
            String vertexS="nodes: "+String.valueOf(s);
            for(int i=0;i<x.length;i++){
                if(x[i].get(GRB.DoubleAttr.X)==0){
                    dispatch[i]=false;

                }else {
                    dispatch[i]=true;
                    edgeS+="("+String.valueOf(g.edges[i].start.index)+","+String.valueOf(g.edges[i].end.index)+")";
                }
            }

            for(int i=0;i<v.length;i++){
                if(v[i].get(GRB.DoubleAttr.X)==0){
                    dispatchV[i]=false;

                }else {
                    dispatchV[i]=true;
                    vertexS+=","+String.valueOf(g.nodes[i].index);
                }
            }
            vertexS+=","+String.valueOf(e);
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
