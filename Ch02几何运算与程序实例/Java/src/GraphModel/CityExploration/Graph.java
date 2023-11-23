package GraphModel.CityExploration;


import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Graph {
    public  edge[] edges=null;
    public  node[] nodes=null;
    public  int edgesNum=0;
    public  int vertexNum=0;
    public  PVector[]positions=null;
    //    public boolean isSubGraph=false;
    public Graph() {

    }
    public Graph(edge[] edges_, boolean isSubGraph) {
        this.edges=edges_;
        this.edgesNum=edges_.length;
        HashMap<Integer,WB_Point>nodeHash=new HashMap<>();
        ArrayList<Integer>index=new ArrayList<>();
        for(int i=0;i<edges_.length;i++){
            if(!nodeHash.containsKey(edges_[i].start.fatherIndex)){
                nodeHash.put(edges_[i].start.fatherIndex,edges_[i].start.position);
                index.add(edges_[i].start.fatherIndex);
            }else if(!nodeHash.containsKey(edges_[i].end.fatherIndex)){
                nodeHash.put(edges_[i].end.fatherIndex,edges_[i].end.position);
                index.add(edges_[i].end.fatherIndex);
            }
        }
        int[]ind=new int[index.size()];
        for(int i=0;i<index.size();i++){
            ind[i]=index.get(i);
        }
        bubbleSort(ind);

        this.vertexNum=nodeHash.size();
        if(isSubGraph=true){
            this.nodes=new node[vertexNum];
            for(int i=0;i<vertexNum;i++){
                this.nodes[i]=new node(i,ind[i],nodeHash.get(ind[i]));
            }
        }

    }

    public ArrayList<node> getNodesList(){
        ArrayList<node>nodeList=new ArrayList<>();
        for(node n:nodes){
            nodeList.add(n);
        }
        return nodeList;
    }
    public static ArrayList<int[]> readTxtFile(String filePath){
        ArrayList<int[]>cities=new ArrayList<>();
        try {
            String encoding="GBK";
            File file=new File(filePath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                    String[]points=lineTxt.split(" ");
                    int[]lineInts=new int[points.length];
                    for(int i=0;i<points.length;i++){
                        lineInts[i]=Integer.parseInt(points[i]);
                    }
                    cities.add(lineInts);
                }
                read.close();
            }else{
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return  cities;
    }
    public Integer getVertexNum(){
        return vertexNum;
    }

    public static void bubbleSort(int[] numbers)
    {
        int temp = 0;
        int size = numbers.length;
        for(int i = 0 ; i < size-1; i ++)
        {
            for(int j = 0 ;j < size-1-i ; j++)
            {
                if(numbers[j] > numbers[j+1])  //��������λ��
                {
                    temp = numbers[j];
                    numbers[j] = numbers[j+1];
                    numbers[j+1] = temp;
                }
            }
        }
    }
    public static Graph getWeightedGraph(String filename,PApplet app,int width,int height){
        ArrayList<int[]>cities=readTxtFile(filename);
        edge[] edges_=new edge[cities.size()];

        ArrayList<Integer>index=new ArrayList<>();
        for(int i=0;i<edges_.length;i++){
            if(!index.contains(cities.get(i)[0])){
                index.add(cities.get(i)[0]);
            }else if(!index.contains(cities.get(i)[1])){
                index.add(cities.get(i)[1]);
            }
        }
        int[]ind=new int[index.size()];
        for(int i=0;i<index.size();i++){
            ind[i]=index.get(i);
        }
        bubbleSort(ind);
        node[]nodes_=new node[ind.length];
        for(int i=0;i<ind.length;i++){
            nodes_[i]=new node(i,ind[i],new WB_Point(app.random(1)*width,app.random(1)*height));
        }
        for(int i=0;i<edges_.length;i++){
            if(cities.get(i).length<=3) {
                edges_[i] = new edge(nodes_[getNodeID(nodes_, cities.get(i)[0])], nodes_[getNodeID(nodes_, cities.get(i)[1])], cities.get(i)[2], i);
            }else {
                double[]datas=new double[cities.get(i).length-3];
                for(int j=0;j<cities.get(i).length-3;j++){
                    datas[j]=cities.get(i)[j+3];
                }
                edges_[i] = new edge(nodes_[getNodeID(nodes_, cities.get(i)[0])], nodes_[getNodeID(nodes_, cities.get(i)[1])], cities.get(i)[2],datas, i);
            }
        }
        return new Graph(edges_,false);
    }

    public static int getNodeID(node[] nodes_,int fatherIndex){
        int index=-1;
        for(int i=0;i<nodes_.length;i++){
            if(nodes_[i].fatherIndex==fatherIndex){
                index=nodes_[i].index;
            }
        }
        return index;
    }

    public ArrayList<Integer> getNeighbour(int index){
        ArrayList<Integer>ids=new ArrayList<>();
        for(int i=0;i<edges.length;i++){
            if(edges[i].start.index==index){
                ids.add(edges[i].end.index);
            }else if(edges[i].end.index==index){
                ids.add(edges[i].start.index);
            }
        }
        return ids;
    }

    public Graph subGraph(ArrayList<Integer>subset){
        ArrayList<edge>edges_sub=new ArrayList<>();
        for(int i=0;i<this.edges.length;i++){
            if(subset.contains(this.edges[i].start.index)&&subset.contains(this.edges[i].end.index)){
                edges_sub.add(this.edges[i]);
            }
        }
        Graph g_sub=new Graph(edges_sub.toArray(new edge[edges_sub.size()]),true);
        return g_sub;
    }

    public Integer getEdgeID(int index1,int index2){
        Integer id=-1;
        for(int i=0;i<edges.length;i++){
            if((edges[i].start.index==index1&&edges[i].end.index==index2)||(edges[i].start.index==index2&&edges[i].end.index==index1)){
                id=i;
            }
        }
        return id;
    }
    public  void draw(PApplet app){
        for(int i=0;i<edges.length;i++){
            app.pushStyle();
            app.stroke(150,150,255);
            app.line((float) edges[i].start.x,(float) edges[i].start.y,(float) edges[i].end.x,(float) edges[i].end.y);
            app.popStyle();

            app.fill(0);
            app.textSize(10);
            app.textAlign(PConstants.CENTER,PConstants.CENTER);
            String s=String.valueOf(edges[i].weight);
            if(edges[i].dataAttach!=null){
                for(int j=0;j<edges[i].dataAttach.length;j++){
                    s+=" ; "+edges[i].dataAttach[j];
                }
            }

            WB_Point p1=new WB_Point(edges[i].start.x,edges[i].start.y);
            WB_Point p2=new WB_Point(edges[i].end.x,edges[i].end.y);
            WB_Vector v=new WB_Vector(p1,p2);

            double a=WB_Vector.getAngle(v,new WB_Point(1,0));

            if(a>Math.PI/2&&v.xf()<0&&v.yf()<0){
                a=Math.PI-a;
            }else if(a>Math.PI/2&&v.xf()<0&&v.yf()>0){
                a=-(Math.PI-a);
            }else if(a<=Math.PI/2&&v.xf()>0&&v.yf()<0){
                a=-a;
            }
            WB_Vector mid=new WB_Point((edges[i].start.x+edges[i].end.x)/2,(edges[i].start.y+edges[i].end.y)/2);

            app.pushMatrix();
            app.translate(mid.xf(),mid.yf());
            app.rotate((float) a);
            app.pushStyle();
            app.noStroke();
            app.fill(255);

            app.rectMode(PConstants.CENTER);
            app.rect(0,0,app.textWidth(s)+2,10);
            app.popStyle();

            app.text(s,0,0);
            app.popMatrix();

        }
        app.pushStyle();
        for(int i=0;i<vertexNum;i++){
            app.fill(150,150,255);
            app.ellipse((float) nodes[i].x,(float) nodes[i].y,15,15);
            app.fill(0);
            app.textAlign(PConstants.CENTER,PConstants.CENTER);
            app.textSize(10);
            app.text(nodes[i].fatherIndex,(float) nodes[i].x,(float) nodes[i].y);
        }
        app.popStyle();
    }

    public static class edge{
        node start;
        node end;
        public double weight;
        public int id;
        double[]dataAttach=null;
        public  edge(){

        }

        public  edge(node start,node end,int id){
            this.start=start;
            this.end=end;
            this.weight=1;
            this.id=id;
        }

        public  edge(node start,node end,double weight,int id){
            this.start=start;
            this.end=end;
            this.weight=weight;
            this.id=id;
        }
        public  edge(node start,node end,double weight,double[]dataAttach,int id){
            this.start=start;
            this.end=end;
            this.weight=weight;
            this.id=id;
            this.dataAttach=dataAttach;
        }
    }

    public static class node{
        int index;
        int fatherIndex;
        WB_Point position;
        double x;
        double y;
        double z;

        public  node(){

        }

        public  node(Integer index,Integer fatherIndex,WB_Point pos){
            this.index=index;
            this.fatherIndex=fatherIndex;
            this.position=pos;
            initialPos();
        }
        public void initialPos(){
            x=position.xf();
            y=position.yf();
            z=position.zf();
        }

        public Integer getIndex(){
            return index;
        }
        public Integer getFatherIndex(){
            return fatherIndex;
        }
        public WB_Point getPosition(){
            return position;
        }
    }
}
