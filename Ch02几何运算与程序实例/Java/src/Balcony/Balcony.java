package Balcony;

import gurobi.GRBLinExpr;
import gurobi.GRBVar;
import javafx.scene.transform.Translate;
import processing.core.PApplet;

import java.security.PublicKey;
import java.util.ArrayList;

public class Balcony {
    int w;               //宽度格数
    int h;               //长度格数
    int index;           //模板索引
    int S;                //面积，即模板占据格数
    int MinCount;         //最小使用次数
    int MaxCount;         //最大使用次数
    int ConcavityRate;        //每个模板的凹凸值
    PApplet app;


    public Balcony (int w, int h, int index,int S,int MinCount, int MaxCount, int ConcavityRate, PApplet app){

        this.w=w;
        this.h=h;
        this.index=index;
        this.S=S;
        this.app=app;
        this.MinCount=MinCount;
        this.MaxCount=MaxCount;
        this.ConcavityRate=ConcavityRate;

    }

    public int getWidth(){
        return w;
    }

    public int getHeight(){
        return h;
    }

    public int getMinCount(){
        return MinCount;
    }

    public int getMaxCount(){
        return MaxCount;
    }

    public int getS(){
        return S;
    }

    public int getConcavityRate(){
        return ConcavityRate;
    }

    public void CBOneTemplate(int i, int j, GRBLinExpr expr , GRBVar[][][] status ){             //每个模板的判定区域
        for(int m=0;m<getWidth();m++){
            for(int n=0;n<getHeight();n++){
                expr.addTerm(1.0, status[i-m][j-n][index]);
            }
        }
    }

    public void DefineBetween(int i, int j, ArrayList<Balcony> AllBalcony, GRBLinExpr expr , GRBVar[][][] status){       //模板左右一格不能有模板
        for (int m=0;m<AllBalcony.size();m++){
            for (int q = 0; q < h; q++) {
                AllBalcony.get(m).CBOneTemplate(i - 1, j + q, expr, status);
                AllBalcony.get(m).CBOneTemplate(i + w, j + q, expr, status);
            }
        }
    }

    public void DefineBetween4_1(int i, int j, ArrayList<Balcony> AllBalcony, GRBLinExpr expr , GRBVar[][][] status){

        for (int m=0;m<AllBalcony.size();m++){
            AllBalcony.get(m).CBOneTemplate(i+2,j,expr,status);
            AllBalcony.get(m).CBOneTemplate(i-1,j,expr,status);
            AllBalcony.get(m).CBOneTemplate(i-1,j-1,expr,status);
            AllBalcony.get(m).CBOneTemplate(i+1,j-1,expr,status);
        }
    }

    public void DefineBetween4_2(int i, int j, ArrayList<Balcony> AllBalcony, GRBLinExpr expr , GRBVar[][][] status){

        for (int m=0;m<AllBalcony.size();m++){
            AllBalcony.get(m).CBOneTemplate(i+2,j,expr,status);
            AllBalcony.get(m).CBOneTemplate(i-1,j,expr,status);
            AllBalcony.get(m).CBOneTemplate(i-1,j-1,expr,status);
            AllBalcony.get(m).CBOneTemplate(i+1,j-1,expr,status);
        }
    }

    public void drawModel(int i, int j, int col ,int size){              //画出模板
        app.fill(col);
        for(int m=0;m<getWidth();m++){
            for(int n=0;n<getHeight();n++){
                app.rect((i+m)*size,(j+n)*size,size,size);
            }
        }
    }

    public void drawThisBalcony_0(int i, int j, int col ,int size){            //画出阳台
        app.fill(col);
        app.rect(i*size,(j+0.9f)*size,size,0.1f*size);

        app.pushMatrix();
        app.translate((i+0.5f)*size,((j+0.9f)+0.05f)*size,0.25f*size);
        app.box(size,0.1f*size,0.5f*size);
        app.popMatrix();

        app.fill(col,100);
        app.rect(i*size,(j+0.5f)*size,size,0.4f*size);

        app.pushMatrix();
        app.translate((i+0.5f)*size,(j+0.5f+0.2f)*size,0.25f*size);
        app.box(size,0.4f*size,0.5f*size);
        app.popMatrix();
    }

    public void drawThisBalcony_3(int i, int j, int col ,int size){
        app.fill(col);
        app.rect(i*size,(j+0.9f)*size,size,0.1f*size);

        app.pushMatrix();
        app.translate((i+0.5f)*size,((j+0.9f)+0.05f)*size,0.25f*size);
        app.box(size,0.1f*size,0.5f*size);
        app.popMatrix();

        app.rect(i*size,(j+1)*size,size,size);

        app.pushMatrix();
        app.translate((i+0.5f)*size,((j+1f)+0.5f)*size,0.25f*size);
        app.box(size,size,0.5f*size);
        app.popMatrix();



        app.fill(col,100);
        app.rect(i*size,(j+0.5f)*size,size,0.4f*size);

        app.pushMatrix();
        app.translate((i+0.5f)*size,(j+0.5f+0.2f)*size,0.25f*size);
        app.box(size,0.4f*size,0.5f*size);
        app.popMatrix();
    }

    public void drawThisBalcony_4(int i, int j, int col ,int size){
        app.fill(col);
        app.rect(i*size,(j+0.9f)*size,size,0.1f*size);

        app.pushMatrix();
        app.translate((i+0.5f)*size,((j+0.9f)+0.05f)*size,0.25f*size);
        app.box(size,0.1f*size,0.5f*size);
        app.popMatrix();

        app.rect((i+1)*size,(j+0.9f)*size,size,0.1f*size);

        app.pushMatrix();
        app.translate((i+1+0.5f)*size,((j+0.9f)+0.05f)*size,0.25f*size);
        app.box(size,0.1f*size,0.5f*size);
        app.popMatrix();

        app.rect(i*size,(j+1)*size,size,size);

        app.pushMatrix();
        app.translate((i+0.5f)*size,((j+1f)+0.5f)*size,0.25f*size);
        app.box(size,size,0.5f*size);
        app.popMatrix();

        app.rect((i+1)*size,(j+1)*size,size,size);

        app.pushMatrix();
        app.translate((i+1+0.5f)*size,((j+1f)+0.5f)*size,0.25f*size);
        app.box(size,size,0.5f*size);
        app.popMatrix();

        app.fill(col,100);
        app.rect(i*size,(j+0.5f)*size,size,0.4f*size);

        app.pushMatrix();
        app.translate((i+0.5f)*size,(j+0.5f+0.2f)*size,0.25f*size);
        app.box(size,0.4f*size,0.5f*size);
        app.popMatrix();

        app.rect((i+1)*size,(j+0.5f)*size,size,0.4f*size);

        app.pushMatrix();
        app.translate((i+1+0.5f)*size,(j+0.5f+0.2f)*size,0.25f*size);
        app.box(size,0.4f*size,0.5f*size);
        app.popMatrix();
    }

    public void drawThisBalcony_5(int i, int j, int col ,int size){
        app.fill(col);
        app.rect(i*size,(j+0.9f)*size,size,0.1f*size);

        app.pushMatrix();
        app.translate((i+0.5f)*size,((j+0.9f)+0.05f)*size,0.25f*size);
        app.box(size,0.1f*size,0.5f*size);
        app.popMatrix();

        app.rect((i+1)*size,(j+0.9f)*size,size,0.1f*size);

        app.pushMatrix();
        app.translate((i+1+0.5f)*size,((j+0.9f)+0.05f)*size,0.25f*size);
        app.box(size,0.1f*size,0.5f*size);
        app.popMatrix();

        app.fill(col,100);
        app.rect(i*size,(j+0.5f)*size,size,0.4f*size);

        app.pushMatrix();
        app.translate((i+0.5f)*size,(j+0.5f+0.2f)*size,0.25f*size);
        app.box(size,0.4f*size,0.5f*size);
        app.popMatrix();

        app.rect((i+1)*size,(j+0.5f)*size,size,0.4f*size);

        app.pushMatrix();
        app.translate((i+1+0.5f)*size,(j+0.5f+0.2f)*size,0.25f*size);
        app.box(size,0.4f*size,0.5f*size);
        app.popMatrix();
    }

}
