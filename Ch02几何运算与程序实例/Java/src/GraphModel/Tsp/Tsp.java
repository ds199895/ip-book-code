package GraphModel.Tsp;

import gurobi.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tsp {

  public Tsp(){

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

      // Create variables
      GRBVar[] x = new GRBVar[m];

      for (int i = 0; i < m; i++){
          x[i] = model.addVar(0.0, 1.0, g.edges[i].weight,GRB.BINARY,"edge"+String.valueOf(i));
      }

      // 每个节点处的度为2
      for (int i = 0; i < n; i++) {
        GRBLinExpr expr2 = new GRBLinExpr();
        for (Integer j:g.getNeighbour(i))
            expr2.addTerm(1.0, x[g.getEdgeID(i,j)]);
        model.addConstr(expr2, GRB.EQUAL, 2.0, "deg2_"+String.valueOf(i));
      }

      // 割约束
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
      List<Graph.edge> edges=new ArrayList<>();
      for(int i=0;i<x.length;i++){
        if(x[i].get(GRB.DoubleAttr.X)==0){
          dispatch[i]=false;
        }else {
          dispatch[i]=true;
          edgeS+="("+String.valueOf(g.edges[i].start.index)+","+String.valueOf(g.edges[i].end.index)+")";
          edges.add(g.edges[i]);
        }
      }
      String vertexS="nodes: ";
      int count=0;
      List<Graph.node> nodes=new ArrayList<>();
      for(int i=0;i<x.length;i++){
        if(x[i].get(GRB.DoubleAttr.X)==0){
          dispatch[i]=false;
        }else {
          dispatch[i] = true;
//          if(!nodes.contains(g.edges[i].start.index)&&!nodes.contains(g.edges[i].end.index)){
            nodes.add(g.edges[i].start);
            nodes.add(g.edges[i].end);
//          }
          count++;
        }
      }
      boolean[] dis=new boolean[edges.size()];
      Arrays.fill(dis,false);
      List<Integer>ns=new ArrayList<>();
      ns.add(edges.get(0).start.index);
      ns.add(edges.get(0).end.index);
      for(int i=1;i<dis.length;i++){
        if(!dis[i]){
          for(int j=i+1;j<dis.length;j++){
            if(edges.get(i).end.index==edges.get(j).start.index){
              ns.add(edges.get(i).start.index);
              ns.add(edges.get(i).end.index);
              dis[j]=true;
            }else if(edges.get(i).end.index==edges.get(j).end.index){
              ns.add(edges.get(i).end.index);
              ns.add(edges.get(i).start.index);
              dis[j]=true;
            }
          }
        }

      }
      for(int e : ns){
        System.out.println(e);
      }
      System.out.println(edgeS);
      System.out.println(vertexS);
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
