package EquipmentPlace;

import gurobi.*;
import processing.core.*;

import java.util.ArrayList;
import java.util.List;


public class Main extends PApplet {
    public static void main(String[] args) {
        PApplet.main("EquipmentPlace.Main");
    }
    int placeNum=5;
    int equipNum=4;
    PVector[][] pos=new PVector[equipNum][4];
    double[][] c=new double[equipNum][placeNum];
    List<PVector> posList=new ArrayList<>();
    boolean[][]choose=new boolean[equipNum][placeNum];
    public void setup(){
        size(800,800);
        //生成每个设施可选位置
        for(int i=0;i<equipNum;i++){
            List<PVector> sub=new ArrayList<>();
            int count=0;
            while (count<placeNum){
                PVector p=new PVector(random(200,700),random(200,700));
                boolean dis=true;
                for(int j=0;j<posList.size();j++){
                    if(p.dist(posList.get(j))<50){
                        dis=false;
                    }
                }

                if(dis){
                    posList.add(p);
                    sub.add(p);
                    count++;
                }
            }
            pos[i]=sub.toArray(new PVector[placeNum]);
        }

        //生成每个设施选址的代价
        for(int i=0; i < equipNum; i++){
            for(int j=0; j < placeNum;j++){
                c[i][j]=random(2,5);
            }
        }
        //优化
        try {
            choose=optiPlace();
        } catch (GRBException e) {
            e.printStackTrace();
        }

    }

    public void draw(){
        background(255);
        noFill();
        //画出每个设施的备用选址
        for(int i=0;i<equipNum;i++){
            for(int j=0;j<placeNum;j++) {
                rectMode(PConstants.CENTER);
                rect(pos[i][j].x, pos[i][j].y, 50, 30);
                pushStyle();
                textSize(10);
                fill(0,255,0);
                text("X"+String.valueOf(i)+","+String.valueOf(j),pos[i][j].x-12, pos[i][j].y+12);
                textAlign(PConstants.CENTER);
                popStyle();
            }
        }
        //标识最终的设施选址，黑点
        for(int i=0;i<equipNum;i++){
            for(int j=0;j<placeNum;j++){
                if(choose[i][j]){
                    fill(0);
                    ellipse(pos[i][j].x,pos[i][j].y,6,6);
                    pushStyle();
                    fill(255,0,0);
                    textSize(15);
                    text(i,pos[i][j].x-16,pos[i][j].y+4);
                    popStyle();
                }
            }
        }
    }

    public boolean[][] optiPlace() throws GRBException {
        boolean[][] choose=new boolean[equipNum][placeNum];
        GRBEnv env=new GRBEnv();
        env.set("logFile","Main.log");
        GRBModel model=new GRBModel(env);

        GRBVar[][] x=new GRBVar[equipNum][placeNum];
        for(int i=0;i<equipNum;i++){
            for(int j=0;j<placeNum;j++){
                x[i][j]=model.addVar(0,1,0,GRB.BINARY,"choose");
            }
        }

        GRBLinExpr obj=new GRBLinExpr();
        for(int i=0; i < equipNum; i++) {
            for (int j = 0; j < placeNum; j++) {
                obj.addTerm(c[i][j],x[i][j]);
            }
        }

        model.setObjective(obj,GRB.MINIMIZE);

        //每个设置只选择一次位置
        for(int i=0;i<equipNum;i++){
            GRBLinExpr expr1=new GRBLinExpr();
            for(int j=0;j<placeNum;j++){
                expr1.addTerm(1,x[i][j]);
            }
            model.addConstr(expr1,GRB.EQUAL,1,"c1");
        }

        //每个位置只被选中一次
        for(int j=0;j<placeNum;j++){
            GRBLinExpr expr2=new GRBLinExpr();

            for(int i=0;i<equipNum;i++){
                expr2.addTerm(1,x[i][j]);
            }
            model.addConstr(expr2,GRB.LESS_EQUAL,1,"c2");
        }
        model.optimize();

        //输出结果
        for(int i=0; i < equipNum; i++) {
            for (int j = 0; j < placeNum; j++) {
                if(x[i][j].get(GRB.DoubleAttr.X)==1){

                    choose[i][j]=true;
                    println(choose[i][j]);
                }
            }
        }
        return choose;
    }

}
