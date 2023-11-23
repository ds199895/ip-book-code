package GraphModel.MST;

import gurobi.*;
import java.util.ArrayList;

public class MST {

  public MST(){

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

  public static boolean[] opti(Graph g){
    boolean[] dispatch =new boolean[g.edgesNum];
    ArrayList<Integer>vertexes=new ArrayList<>();

    int n =g.vertexNum;
    int m=g.edgesNum;
    for(int i=0;i<m;i++){
      vertexes.add(i);
    }

    ArrayList<ArrayList<Integer>>subLists=getSubsets(vertexes);

    try {
      GRBEnv   env   = new GRBEnv();
      GRBModel model = new GRBModel(env);

      // Must set LazyConstraints parameter when using lazy constraints
      model.set(GRB.IntParam.LazyConstraints, 1);

      // Create variables
      GRBVar[] x = new GRBVar[m];

      for (int i = 0; i < m; i++){
          x[i] = model.addVar(0.0, 1.0, g.edges[i].weight,GRB.BINARY,"edge"+String.valueOf(i));
      }

      // 边的总数为n-1
      GRBLinExpr expr2 = new GRBLinExpr();
      for (int i = 0; i <m; i++) {
            expr2.addTerm(1.0, x[i]);
      }
      model.addConstr(expr2, GRB.EQUAL, n-1, "alledges");

      //割约束
      for(int i=2;i<n;i++){
        for(ArrayList<Integer>sub:subLists){
          if(sub.size()==i){
            GRBLinExpr expr3=new GRBLinExpr();
            Graph g_sub=g.subGraph(sub);
            for(int j=0;j<g_sub.edgesNum;j++){
              expr3.addTerm(1,x[g_sub.edges[j].id]);
            }
            model.addConstr(expr3,GRB.LESS_EQUAL,sub.size()-1,"cosub");
          }
        }
      }
      model.optimize();

      String edgeS="edges: ";

      for(int i=0;i<x.length;i++){
        if(x[i].get(GRB.DoubleAttr.X)==0){
          dispatch[i]=false;
        }else {
          dispatch[i]=true;
          edgeS+="("+String.valueOf(g.edges[i].start.index)+","+String.valueOf(g.edges[i].end.index)+")";
        }
      }
      System.out.println(edgeS);
      // Dispose of model and environment
      model.dispose();
      env.dispose();

    } catch (GRBException e) {
      System.out.println("Error code: " + e.getErrorCode() + ". " +
              e.getMessage());
      e.printStackTrace();
    }
    return dispatch;
  }
}
