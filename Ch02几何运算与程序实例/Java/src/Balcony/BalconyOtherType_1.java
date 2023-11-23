package Balcony;

import gurobi.GRBLinExpr;
import gurobi.GRBVar;
import processing.core.PApplet;

import java.util.ArrayList;

public class BalconyOtherType_1 extends Balcony{                   //正L形模板


    public BalconyOtherType_1(int w, int h, int index,int S,int MinCount, int MaxCount, int ConcavityRate, PApplet app) {
        super(w, h, index,S, MinCount,MaxCount,ConcavityRate,app);
    }

    public void CBOneTemplate(int i, int j, GRBLinExpr expr , GRBVar[][][] status ) {             //每个模板的判定区域
        expr.addTerm(1.0, status[i][j][1]);
        expr.addTerm(1.0, status[i + 1][j][1]);
        expr.addTerm(1.0, status[i][j + 1][1]);
    }

    public void DefineBetween(int i,int j, ArrayList<Balcony> AllBalcony,GRBLinExpr expr , GRBVar[][][] status){       //模板一左右不能有其他模板
        for (int m=0;m<AllBalcony.size();m++){
            AllBalcony.get(m).CBOneTemplate(i-2,j,expr,status);
            AllBalcony.get(m).CBOneTemplate(i-1,j-1,expr,status);
            AllBalcony.get(m).CBOneTemplate(i+1,j,expr,status);
            AllBalcony.get(m).CBOneTemplate(i+1,j-1,expr,status);
        }
    }

    public void DefineBetween4_1(int i, int j, ArrayList<Balcony> AllBalcony, GRBLinExpr expr , GRBVar[][][] status){

        for (int m=0;m<AllBalcony.size();m++){
            AllBalcony.get(m).CBOneTemplate(i-2,j,expr,status);
            AllBalcony.get(m).CBOneTemplate(i-1,j-1,expr,status);
            AllBalcony.get(m).CBOneTemplate(i+1,j-1,expr,status);
        }
    }

    public void DefineBetween4_2(int i, int j, ArrayList<Balcony> AllBalcony, GRBLinExpr expr , GRBVar[][][] status){

        for (int m=0;m<AllBalcony.size();m++){
            AllBalcony.get(m).CBOneTemplate(i-1,j-1,expr,status);
            AllBalcony.get(m).CBOneTemplate(i+1,j,expr,status);
            AllBalcony.get(m).CBOneTemplate(i+1,j-1,expr,status);
        }
    }

    public void NCrassLine(int i,int j, ArrayList<Balcony> AllBalcony,GRBLinExpr expr , GRBVar[][][] status){
        for (int m=0;m<AllBalcony.size();m++){
            AllBalcony.get(m).CBOneTemplate(i-2,j,expr,status);
            AllBalcony.get(m).CBOneTemplate(i-1,j-1,expr,status);
            AllBalcony.get(m).CBOneTemplate(i+1,j-1,expr,status);
        }
    }

    public void drawModel(int i, int j, int col ,int size){
        app.fill(col);
        app.rect(i*size,j*size,size,size);
        app.rect((i-1)*size,j*size,size,size);
        app. rect(i*size,(j-1)*size,size,size);
    }

    public void drawThisBalcony_1(int i, int j, int col ,int size){
        app.fill(col);
        app.rect(i*size,((j-1)+0.9f)*size,size,0.1f*size);
        app.pushMatrix();
        app.translate((i+0.5f)*size,((j-1+0.9f)+0.05f)*size,0.25f*size);
        app.box(size,0.1f*size,0.5f*size);
        app.popMatrix();

        app.rect((i-1)*size,(j+0.9f)*size,size,0.1f*size);
        app.pushMatrix();
        app.translate((i-1+0.5f)*size,((j+0.9f)+0.05f)*size,0.25f*size);
        app.box(size,0.1f*size,0.5f*size);
        app.popMatrix();

        app.rect(i*size,j*size,size,size);

        app.pushMatrix();
        app.translate((i+0.5f)*size,(j+0.5f)*size,0.25f*size);
        app.box(size,size,0.5f*size);
        app.popMatrix();

        app.fill(col,100);
        app.rect((i-1)*size,(j+0.5f)*size,size,0.4f*size);

        app.pushMatrix();
        app.translate((i-1+0.5f)*size,(j+0.5f+0.2f)*size,0.25f*size);
        app.box(size,0.4f*size,0.5f*size);
        app.popMatrix();

        app.rect(i*size,((j-1)+0.5f)*size,size,0.4f*size);

        app.pushMatrix();
        app.translate((i+0.5f)*size,(j-1+0.5f+0.2f)*size,0.25f*size);
        app.box(size,0.4f*size,0.5f*size);
        app.popMatrix();
    }



}
