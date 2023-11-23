package Balcony;

import gurobi.*;
import peasy.PeasyCam;
import processing.core.PApplet;

import java.util.ArrayList;

public class BalconyArrangement2 extends PApplet {
    public static void main(String[] args) {
        PApplet.main("week6.BalconyArrangement2");
    }

    PeasyCam camera;

    int x_facade1=9;     //每层楼长度
    int x_facade2=7;     //每层楼宽度
    int c=4;        //周边缩进
    int x_num=2*(x_facade1+x_facade2)+2*c, y_num=22;           //格子数量
    int size = 50;           //格子尺寸
    int solutionCount=5;            //最终解池数量
    int Num=0;
    int n=6;  //填充模板数量


    Balcony balcony_0;                     //创建五个模板对象
    BalconyOtherType_1 balcony_1;
    BalconyOtherType_2 balcony_2;
    Balcony balcony_3;
    Balcony balcony_4;
    Balcony balcony_5;

    ArrayList<int[]> Q1;                       //超出场地的格子集合
    ArrayList<int[]> Q2;                       //场地周边一圈的格子集合
    ArrayList<Balcony> AllBalcony;                       //所有模板集合
    int[]CrossLine={c+x_facade1,c+x_facade1+x_facade2,c+2*x_facade1+x_facade2};

    int[][][]solutions=new int[solutionCount][x_num][y_num];             //储存五个解


    public void setup(){
        size(x_num*size,y_num*size,P3D);
        getOutIndex();                //将超出边界范围的点加入集合
        getOutLineIndex();            //将边界周围一圈点加入集合

        AllBalcony=new ArrayList<>();                       //创建模板的集合
        balcony_0=new Balcony(1,1,0,1,2,12,1,this);
        AllBalcony.add( balcony_0);
        balcony_1=new BalconyOtherType_1(2,2,1,3,5,50,4,this);
        AllBalcony.add( balcony_1);
        balcony_2=new BalconyOtherType_2(2,2,2,3,5,60,4,this);
        AllBalcony.add( balcony_2);
        balcony_3=new Balcony(1,2,3,2,8,100,3,this);
        AllBalcony.add( balcony_3);
        balcony_4=new Balcony(2,2,4,4,6,14,6,this);
        AllBalcony.add( balcony_4);
        balcony_5=new Balcony(2,1,5,2,2,10,2,this);
        AllBalcony.add( balcony_5);


        try {
            this.setGRB();
        } catch (GRBException e) {
            e.printStackTrace();
        }

        camera = new PeasyCam(this,0.5*width,0.5*height,0,50);
    }

    public void draw(){
        background(255);

//        directionalLight(255, 255, 255, 1, 1, -1);
//        directionalLight(127, 127, 127, -1, -1, 1);

        fill(50,150);
        noStroke();
        for(int i=0;i< Q1.size();i++) {
            rect(Q1.get(i)[0]*size,Q1.get(i)[1]*size,size,size);
        }
        drawBalcony();
        strokeWeight(3);
        stroke(50,100);
        line((x_facade1+c)*size,0,(x_facade1+c)*size,y_num*size);
        line((x_facade1+x_facade2+c)*size,0,(x_facade1+x_facade2+c)*size,y_num*size);
        line((2*x_facade1+x_facade2+c)*size,0,(2*x_facade1+x_facade2+c)*size,y_num*size);
    }



    GRBVar[][][]status=new GRBVar[x_num][y_num][n];

    private void setGRB() throws GRBException {
        GRBEnv env = new GRBEnv("BalconyArrangement.log");
        GRBModel model = new GRBModel(env);

        // Create variables        创建变量
        for(int i=0;i<x_num;i++) {
            for(int j=0;j<y_num;j++) {
                for(int k=0;k<n;k++) {
                    status[i][j][k] = model.addVar(0, 1, 0, GRB.BINARY, "o" + String.valueOf(i + j));     //对于每一个格点，其是否被其中一个模板占据，01变量
                }
            }
        }


        //Set objective     创建目标：被占用格子数量最多
        GRBQuadExpr obj = new GRBQuadExpr();

        for(int i=c;i<x_num-c;i++) {
            for(int j=c;j<y_num-c;j++) {
                for(int k=0;k<AllBalcony.size();k++) {
                    obj.addTerm(AllBalcony.get(k).getS(), status[i][j][k]);
                }
            }
        }
        model.setObjective(obj,GRB.MAXIMIZE);


        // Add constraint :
        //约束：超出场地边界的地方不能作为基准点
        for(int i=0;i<Q1.size();i++) {
            GRBLinExpr expr1 = new GRBLinExpr();
            for(int k=0;k<n;k++) {
                expr1.addTerm(1.0, status[Q1.get(i)[0]][Q1.get(i)[1]][k]);
            }
            model.addConstr(expr1, GRB.EQUAL, 0, "c0" + String.valueOf(i));
        }


        //约束：当边界外一圈的点(Q2),其不能被模板所覆盖，即不能有模块超出边界,
        // 方法：当基准点在边界外一圈时，判定区域合为0
        for(int i=0;i<Q2.size();i++) {
            GRBLinExpr expr1 = new GRBLinExpr();
            for(int m=0;m<AllBalcony.size();m++) {
                AllBalcony.get(m).CBOneTemplate(Q2.get(i)[0], Q2.get(i)[1], expr1, status);             //每个模板判定区域带入计算
            }
            model.addConstr(expr1, GRB.EQUAL, 0, "c0" + String.valueOf(i));
        }


        // Add constraint : 每个模板最大和最小的使用次数
        for(int k=0;k<n;k++) {
            GRBLinExpr expr0 = new GRBLinExpr();
            for(int i = c;i<x_num-c;i++) {
                for (int j = c; j < y_num - c; j++) {
                    expr0.addTerm(1.0, status[i][j][k]);
                }
            }
            model.addConstr(expr0, GRB.LESS_EQUAL, AllBalcony.get(k).getMaxCount(), "c0" + String.valueOf(k));
            model.addConstr(expr0, GRB.GREATER_EQUAL, AllBalcony.get(k).getMinCount(), "c0" + String.valueOf(k));
        }


        // Add constraint : 凹凸率控制          //凹为0，凸窗为2，阳台为1
        GRBLinExpr expr10 = new GRBLinExpr();
        for(int k=0;k<n;k++) {
            for(int i = c;i<x_num-c;i++) {
                for (int j = c; j < y_num - c; j++) {
                    expr10.addTerm(AllBalcony.get(k).getConcavityRate(), status[i][j][k]);
                }
            }
        }
        int CRate=(x_num-2*c)*(y_num-2*c)*2;    //所有格点为凸窗的值
        model.addConstr(expr10, GRB.LESS_EQUAL, 0.8*CRate, "c0" + String.valueOf(1));
        model.addConstr(expr10, GRB.GREATER_EQUAL, 0.2*CRate, "c0" + String.valueOf(1));


        //Add constraint : 每个单元至多被一个模板单元所覆盖
        for(int i = c;i<x_num-c;i++) {
            for (int j = c; j < y_num - c; j++) {
                GRBLinExpr expr0 = new GRBLinExpr();
                for(int m=0;m<AllBalcony.size();m++) {
                    AllBalcony.get(m).CBOneTemplate(i, j, expr0, status);          //每个模板判定区域带入计算
                }
                model.addConstr(expr0, GRB.LESS_EQUAL, 1, "c0" + String.valueOf(i));
            }
        }


//        // Add constraint : 每个模板左右至少空一格
//        for (int i = c; i < x_num - c; i++) {
//            for (int j = c; j < y_num - c; j++) {
//                for(int k=0;k<AllBalcony.size();k++) {
//                    GRBLinExpr expr0 = new GRBLinExpr();
//                    AllBalcony.get(k).DefineBetween(i, j, AllBalcony, expr0, status);
//                    model.addGenConstrIndicator(status[i][j][k], 1, expr0, GRB.LESS_EQUAL, 0, "c0" + String.valueOf(i));
//
//                }
//            }
//        }

        //模板不跨越折角的地方
        GRBLinExpr expr11 = new GRBLinExpr();
        //模板0，3不考虑
        for (int i = 0; i < CrossLine.length; i++) {
            for (int j = c; j < y_num - c; j++) {
                expr11.addTerm(1.0, status[CrossLine[i]][j][1]);          //模板一不能踩线,即不能出现在某一列
                expr11.addTerm(1.0, status[CrossLine[i]-1][j][2]);        //模板二不能踩线
                expr11.addTerm(1.0, status[CrossLine[i]-1][j][4]);        //模板四不能踩线
                expr11.addTerm(1.0, status[CrossLine[i]-1][j][5]);        //模板五不能踩线
            }
        }
        model.addConstr(expr11, GRB.EQUAL, 0, "c0" + String.valueOf(0));

        //模板1,2不出现在折角的某一边上
        GRBLinExpr expr15 = new GRBLinExpr();
        for (int i = 0; i < CrossLine.length; i++) {
            for (int j = c; j < y_num - c; j++) {
                expr15.addTerm(1.0, status[CrossLine[i]+1][j][1]);          //模板一不能出现在某一列
                expr15.addTerm(1.0, status[CrossLine[i]-2][j][2]);          //模板二不能出现在某一列
            }
        }

        for (int j = c; j < y_num - c; j++) {
            expr15.addTerm(1.0, status[c+1][j][1]);          //模板一不能出现在某一列(左右边界)
            expr15.addTerm(1.0, status[x_num-c-2][j][2]);          //模板二不能出现在某一列
        }
        model.addConstr(expr15, GRB.EQUAL, 0, "c0" + String.valueOf(0));

        //一个单个阳台出现在转角，旁边必有一个凸窗
        for (int i = 0; i < CrossLine.length; i++) {
            for (int j = c; j < y_num - c; j++) {
                GRBLinExpr expr12 = new GRBLinExpr();
                expr12.addTerm(1.0, status[CrossLine[i]][j][2]);          //模板0在线左边时
                expr12.addTerm(1.0, status[CrossLine[i]][j-1][3]);
                expr12.addTerm(1.0, status[CrossLine[i]][j-1][4]);
                model.addGenConstrIndicator(status[CrossLine[i]-1][j][0],1,expr12,GRB.GREATER_EQUAL,1,"c0" + String.valueOf(i));

                expr12.addTerm(1.0, status[CrossLine[i]-1][j][1]);           //模板0在线右边时
                expr12.addTerm(1.0, status[CrossLine[i]-1][j-1][3]);
                expr12.addTerm(1.0, status[CrossLine[i]-2][j-1][4]);
                model.addGenConstrIndicator(status[CrossLine[i]][j][0],1,expr12,GRB.GREATER_EQUAL,1,"c0" + String.valueOf(i));

                expr12.addTerm(1.0, status[CrossLine[i]][j][2]);          //模板5在线左边时
                expr12.addTerm(1.0, status[CrossLine[i]][j-1][3]);
                expr12.addTerm(1.0, status[CrossLine[i]][j-1][4]);
                model.addGenConstrIndicator(status[CrossLine[i]-2][j][5],1,expr12,GRB.GREATER_EQUAL,1,"c0" + String.valueOf(i));

                expr12.addTerm(1.0, status[CrossLine[i]-1][j][1]);           //模板5在线右边时
                expr12.addTerm(1.0, status[CrossLine[i]-1][j-1][3]);
                expr12.addTerm(1.0, status[CrossLine[i]-2][j-1][4]);
                model.addGenConstrIndicator(status[CrossLine[i]][j][5],1,expr12,GRB.GREATER_EQUAL,1,"c0" + String.valueOf(i));
            }
        }

        for (int j = c; j < y_num - c; j++) {
            GRBLinExpr expr12 = new GRBLinExpr();
            expr12.addTerm(1.0, status[c][j][2]);          //模板0在边界线左边时
            expr12.addTerm(1.0, status[c][j-1][3]);
            expr12.addTerm(1.0, status[c][j-1][4]);
            model.addGenConstrIndicator(status[x_num-c-1][j][0],1,expr12,GRB.GREATER_EQUAL,1,"c0" + String.valueOf(0));

            expr12.addTerm(1.0, status[x_num-c-1][j][1]);          //模板0在边界线右边时
            expr12.addTerm(1.0, status[x_num-c-1][j-1][3]);
            expr12.addTerm(1.0, status[x_num-c-2][j-1][4]);
            model.addGenConstrIndicator(status[c][j][0],1,expr12,GRB.GREATER_EQUAL,1,"c0" + String.valueOf(0));

            expr12.addTerm(1.0, status[c][j][2]);          //模板5在线左边时
            expr12.addTerm(1.0, status[c][j-1][3]);
            expr12.addTerm(1.0, status[c][j-1][4]);
            model.addGenConstrIndicator(status[x_num-c-2][j][5],1,expr12,GRB.GREATER_EQUAL,1,"c0" + String.valueOf(1));

            expr12.addTerm(1.0, status[x_num-c-1][j][1]);           //模板5在线右边时
            expr12.addTerm(1.0, status[x_num-c-1][j-1][3]);
            expr12.addTerm(1.0, status[x_num-c-2][j-1][4]);
            model.addGenConstrIndicator(status[c][j][5],1,expr12,GRB.GREATER_EQUAL,1,"c0" + String.valueOf(1));

        }

        //避免凸窗与凸窗相互之间相接
        for (int i = 0; i < CrossLine.length; i++) {
            for (int j = c; j < y_num - c; j++) {
                GRBLinExpr expr12 = new GRBLinExpr();
                expr12.addTerm(1.0, status[CrossLine[i]][j][2]);          //模板1在线左边时
                expr12.addTerm(1.0, status[CrossLine[i]][j - 1][3]);
                expr12.addTerm(1.0, status[CrossLine[i]][j - 1][4]);
                model.addGenConstrIndicator(status[CrossLine[i] - 1][j][1], 1, expr12, GRB.EQUAL, 0, "c0" + String.valueOf(i));
                GRBLinExpr expr121 = new GRBLinExpr();
                expr121.addTerm(1.0, status[CrossLine[i] - 1][j][1]);
                expr121.addTerm(1.0, status[CrossLine[i] - 1][j - 1][3]);          //模板2在线右边时
                expr121.addTerm(1.0, status[CrossLine[i] - 2][j - 1][4]);
                model.addGenConstrIndicator(status[CrossLine[i] ][j][2], 1, expr121, GRB.EQUAL, 0, "c0" + String.valueOf(i));
                GRBLinExpr expr122 = new GRBLinExpr();
                expr122.addTerm(1.0, status[CrossLine[i]][j][3]);          //模板3在线左边时
                expr122.addTerm(1.0, status[CrossLine[i]][j ][4]);
                model.addGenConstrIndicator(status[CrossLine[i]- 1 ][j][3], 1, expr122, GRB.EQUAL, 0, "c0" + String.valueOf(i));
                GRBLinExpr expr123 = new GRBLinExpr();
                expr123.addTerm(1.0, status[CrossLine[i] - 1][j][3]);          //模板3在线右边时
                expr123.addTerm(1.0, status[CrossLine[i] - 2][j ][4]);
                model.addGenConstrIndicator(status[CrossLine[i]][j][3], 1, expr123, GRB.EQUAL, 0, "c0" + String.valueOf(i));

                GRBLinExpr expr124 = new GRBLinExpr();
                expr124.addTerm(1.0, status[CrossLine[i]][j][4]);          //模板4在线左边时
                model.addGenConstrIndicator(status[CrossLine[i]-2][j][4], 1, expr124, GRB.EQUAL, 0, "c0" + String.valueOf(i));
            }
        }

         //避免凸窗与凸窗相互之间相接(左右边界情况)
        for (int j = c; j < y_num - c; j++) {
            GRBLinExpr expr12 = new GRBLinExpr();
            expr12.addTerm(1.0, status[c][j][2]);          //模板1在左右边界线线左边时
            expr12.addTerm(1.0, status[c][j - 1][3]);
            expr12.addTerm(1.0, status[c][j - 1][4]);
            model.addGenConstrIndicator(status[x_num-c-1][j][1], 1, expr12, GRB.EQUAL, 0, "c0" + String.valueOf(0));

            expr12.addTerm(1.0, status[x_num-c-1][j][1]);
            expr12.addTerm(1.0, status[x_num-c-1][j - 1][3]);          //模板2在线右边时
            expr12.addTerm(1.0, status[x_num-c-2][j - 1][4]);
            model.addGenConstrIndicator(status[c][j][2], 1, expr12, GRB.EQUAL, 0, "c0" + String.valueOf(0));

            expr12.addTerm(1.0, status[c][j + 1][2]);
            expr12.addTerm(1.0, status[c][j][3]);          //模板3在线左边时
            expr12.addTerm(1.0, status[c][j ][4]);
            model.addGenConstrIndicator(status[x_num-c-1][j][3], 1, expr12, GRB.EQUAL, 0, "c0" + String.valueOf(0));

            expr12.addTerm(1.0, status[x_num-c-1][j + 1][1]);
            expr12.addTerm(1.0, status[x_num-c-1][j][3]);          //模板3在线右边时
            expr12.addTerm(1.0, status[x_num-c-2][j ][4]);
            model.addGenConstrIndicator(status[c][j][3], 1, expr12, GRB.EQUAL, 0, "c0" + String.valueOf(0));


            expr12.addTerm(1.0, status[c][j][4]);          //模板4在线左边时
            model.addGenConstrIndicator(status[x_num-c-2][j][4], 1, expr12, GRB.EQUAL, 0, "c0" + String.valueOf(0));
        }



        //每个面出现的模板最小次数
        GRBLinExpr expr19 = new GRBLinExpr();
        //模板3每个面最小出现一次
        for (int i = c; i < c+x_facade1; i++) {
            for (int j = c; j < y_num - c; j++) {
                expr19.addTerm(1.0, status[i][j][3]);
            }
        }
        model.addConstr(expr19, GRB.GREATER_EQUAL, 1, "c0" + String.valueOf(0));
        GRBLinExpr expr22 = new GRBLinExpr();
        for (int i = c+x_facade1; i < c+x_facade1+x_facade2; i++) {
            for (int j = c; j < y_num - c; j++) {
                expr22.addTerm(1.0, status[i][j][3]);
            }
        }
        model.addConstr(expr22, GRB.GREATER_EQUAL, 2, "c0" + String.valueOf(0));
        GRBLinExpr expr23 = new GRBLinExpr();
        for (int i = c+x_facade1+x_facade2; i < c+x_facade1*2+x_facade2; i++) {
            for (int j = c; j < y_num - c; j++) {
                expr23.addTerm(1.0, status[i][j][3]);
            }
        }
        model.addConstr(expr23, GRB.GREATER_EQUAL, 1, "c0" + String.valueOf(0));
        GRBLinExpr expr24 = new GRBLinExpr();
        for (int i = c+x_facade1*2+x_facade2; i < c+x_facade1*2+x_facade2*2; i++) {
            for (int j = c; j < y_num - c; j++) {
                expr24.addTerm(1.0, status[i][j][3]);
            }
        }
        model.addConstr(expr24, GRB.GREATER_EQUAL, 2, "c0" + String.valueOf(0));

        //模板4每个面最小出现一次
        GRBLinExpr expr25 = new GRBLinExpr();
        for (int i = c; i < c+x_facade1; i++) {
            for (int j = c; j < y_num - c; j++) {
                expr25.addTerm(1.0, status[i][j][4]);
            }
        }
        model.addConstr(expr25, GRB.GREATER_EQUAL, 4, "c0" + String.valueOf(0));
        GRBLinExpr expr26 = new GRBLinExpr();
        for (int i = c+x_facade1; i < c+x_facade1+x_facade2; i++) {
            for (int j = c; j < y_num - c; j++) {
                expr26.addTerm(1.0, status[i][j][4]);
            }
        }
        model.addConstr(expr26, GRB.GREATER_EQUAL, 2, "c0" + String.valueOf(0));
        GRBLinExpr expr27 = new GRBLinExpr();
        for (int i = c+x_facade1+x_facade2; i < c+x_facade1*2+x_facade2; i++) {
            for (int j = c; j < y_num - c; j++) {
                expr27.addTerm(1.0, status[i][j][4]);
            }
        }
        model.addConstr(expr27, GRB.GREATER_EQUAL, 4, "c0" + String.valueOf(0));
        GRBLinExpr expr28 = new GRBLinExpr();
        for (int i = c+x_facade1*2+x_facade2; i < c+x_facade1*2+x_facade2*2; i++) {
            for (int j = c; j < y_num - c; j++) {
                expr28.addTerm(1.0, status[i][j][4]);
            }
        }
        model.addConstr(expr28, GRB.GREATER_EQUAL, 2, "c0" + String.valueOf(0));
        // Add constraint : 每个模板左右至少空一格
        for (int i = c; i < x_num - c; i++) {
            for (int j = c; j < y_num - c; j++) {

                //模板0左右不能有模板，在转角地方除外
                GRBLinExpr expr0 = new GRBLinExpr();

                for (int m=0;m<AllBalcony.size();m++){
                    if (i == c + x_facade1 - 1 || i == c + x_facade1 + x_facade2 - 1 || i == c + 2 * x_facade1 + x_facade2 - 1) {
                        AllBalcony.get(m).CBOneTemplate(i-1,j,expr0,status);
                    }else if (i == c + x_facade1  || i == c + x_facade1 + x_facade2  || i == c + 2 * x_facade1 + x_facade2 ) {
                        AllBalcony.get(m).CBOneTemplate(i-1,j,expr0,status);
                    }else {
                        AllBalcony.get(m).CBOneTemplate(i-1,j,expr0,status);
                        AllBalcony.get(m).CBOneTemplate(i+1,j,expr0,status);
                    }
                }
                model.addGenConstrIndicator(status[i][j][0],1,expr0,GRB.LESS_EQUAL,0,"c0" + String.valueOf(i));

                //模板一左右不能有模板，在转角地方除外
                GRBLinExpr expr1 = new GRBLinExpr();
                for (int m=0;m<AllBalcony.size();m++) {

                    if(i == x_num-c-1){
                        AllBalcony.get(m).CBOneTemplate(i - 2, j, expr1, status);
                        AllBalcony.get(m).CBOneTemplate(i - 1, j - 1, expr1, status);
                    }

                    if (i == c + x_facade1 - 1 || i == c + x_facade1 + x_facade2 - 1 || i == c + 2 * x_facade1 + x_facade2 - 1) {
                        AllBalcony.get(m).CBOneTemplate(i - 2, j, expr1, status);
                        AllBalcony.get(m).CBOneTemplate(i - 1, j - 1, expr1, status);
                    } else if (i == c + x_facade1 + 1 || i == c + x_facade1 + x_facade2 + 1 || i == c + 2 * x_facade1 + x_facade2 + 1) {
                        AllBalcony.get(m).CBOneTemplate(i - 1, j - 1, expr1, status);
                        AllBalcony.get(m).CBOneTemplate(i + 1, j, expr1, status);
                        AllBalcony.get(m).CBOneTemplate(i + 1, j - 1, expr1, status);
                    } else {
                        AllBalcony.get(m).CBOneTemplate(i - 2, j, expr1, status);
                        AllBalcony.get(m).CBOneTemplate(i - 1, j - 1, expr1, status);
                        AllBalcony.get(m).CBOneTemplate(i + 1, j, expr1, status);
                        AllBalcony.get(m).CBOneTemplate(i + 1, j - 1, expr1, status);
                    }
                }
                model.addGenConstrIndicator(status[i][j][1],1,expr1,GRB.LESS_EQUAL,0,"c0" + String.valueOf(i));

                //模板二左右不能有模板，在转角地方除外
                GRBLinExpr expr2 = new GRBLinExpr();

                for (int m=0;m<AllBalcony.size();m++){

                    if(i == c){
                        AllBalcony.get(m).CBOneTemplate(i+2,j,expr2,status);
                        AllBalcony.get(m).CBOneTemplate(i+1,j-1,expr2,status);
                    }
                    if (i == c + x_facade1 - 2 || i == c + x_facade1 + x_facade2 - 2 || i == c + 2 * x_facade1 + x_facade2 - 2) {       //允许转角处与模板相连
                        AllBalcony.get(m).CBOneTemplate(i-1,j,expr2,status);
                        AllBalcony.get(m).CBOneTemplate(i-1,j-1,expr2,status);
                        AllBalcony.get(m).CBOneTemplate(i+1,j-1,expr2,status);
                    } else if (i == c + x_facade1  || i == c + x_facade1 + x_facade2  || i == c + 2 * x_facade1 + x_facade2 ) {
                        AllBalcony.get(m).CBOneTemplate(i + 2, j, expr2, status);
                        AllBalcony.get(m).CBOneTemplate(i + 1, j - 1, expr2, status);
                    } else {
                        AllBalcony.get(m).CBOneTemplate(i+2,j,expr2,status);
                        AllBalcony.get(m).CBOneTemplate(i-1,j,expr2,status);
                        AllBalcony.get(m).CBOneTemplate(i-1,j-1,expr2,status);
                        AllBalcony.get(m).CBOneTemplate(i+1,j-1,expr2,status);
                    }
                }
                model.addGenConstrIndicator(status[i][j][2],1,expr2,GRB.LESS_EQUAL,0,"c0" + String.valueOf(i));

                //模板三左右不能有模板，在转角地方除外
                GRBLinExpr expr3 = new GRBLinExpr();

                for (int m=0;m<AllBalcony.size();m++) {

                    if(i == x_num-c-1 ){
                        AllBalcony.get(m).CBOneTemplate(i-1,j,expr3,status);
                        AllBalcony.get(m).CBOneTemplate(i-1,j+1,expr3,status);
                    }else if(i == c){
                        AllBalcony.get(m).CBOneTemplate(i+1,j,expr3,status);
                        AllBalcony.get(m).CBOneTemplate(i+1,j+1,expr3,status);
                    }

                    if (i == c + x_facade1 - 1 || i == c + x_facade1 + x_facade2 - 1 || i == c + 2 * x_facade1 + x_facade2 - 1) {
                        AllBalcony.get(m).CBOneTemplate(i - 1, j, expr3, status);
                        AllBalcony.get(m).CBOneTemplate(i - 1, j + 1, expr3, status);

                    } else if (i == c + x_facade1  || i == c + x_facade1 + x_facade2  || i == c + 2 * x_facade1 + x_facade2 ) {
                        AllBalcony.get(m).CBOneTemplate(i + 1, j, expr3, status);
                        AllBalcony.get(m).CBOneTemplate(i + 1, j + 1, expr3, status);

                    } else {
                        AllBalcony.get(m).CBOneTemplate(i-1,j,expr3,status);
                        AllBalcony.get(m).CBOneTemplate(i-1,j+1,expr3,status);
                        AllBalcony.get(m).CBOneTemplate(i+1,j,expr3,status);
                        AllBalcony.get(m).CBOneTemplate(i+1,j+1,expr3,status);
                    }
                }
                model.addGenConstrIndicator(status[i][j][3],1,expr3,GRB.LESS_EQUAL,0,"c0" + String.valueOf(i));

                //模板四左右不能有模板，在转角地方除外
                GRBLinExpr expr4 = new GRBLinExpr();

                for (int m=0;m<AllBalcony.size();m++){
                    if(i == x_num-c-2 ){
                        AllBalcony.get(m).CBOneTemplate(i-1,j,expr4,status);
                        AllBalcony.get(m).CBOneTemplate(i-1,j+1,expr4,status);
                    }else if(i == c){
                        AllBalcony.get(m).CBOneTemplate(i+2,j,expr4,status);
                        AllBalcony.get(m).CBOneTemplate(i+2,j+1,expr4,status);
                    }


                    if (i == c + x_facade1 - 2 || i == c + x_facade1 + x_facade2 - 2 || i == c + 2 * x_facade1 + x_facade2 - 2) {
                        AllBalcony.get(m).CBOneTemplate(i - 1, j, expr4, status);
                        AllBalcony.get(m).CBOneTemplate(i - 1, j + 1, expr4, status);

                    } else if (i == c + x_facade1  || i == c + x_facade1 + x_facade2  || i == c + 2 * x_facade1 + x_facade2 ) {
                            AllBalcony.get(m).CBOneTemplate(i + 2, j, expr4, status);
                            AllBalcony.get(m).CBOneTemplate(i + 2, j + 1, expr4, status);
                    } else {
                        AllBalcony.get(m).CBOneTemplate(i-1,j,expr4,status);
                        AllBalcony.get(m).CBOneTemplate(i-1,j+1,expr4,status);
                        AllBalcony.get(m).CBOneTemplate(i+2,j,expr4,status);
                        AllBalcony.get(m).CBOneTemplate(i+2,j+1,expr4,status);
                    }
                }
                model.addGenConstrIndicator(status[i][j][4],1,expr4,GRB.LESS_EQUAL,0,"c0" + String.valueOf(i));

                //模板五左右不能有模板
                GRBLinExpr expr5 = new GRBLinExpr();

                for (int m=0;m<AllBalcony.size();m++) {
                    if (i == c + x_facade1 - 2 || i == c + x_facade1 + x_facade2 - 2 || i == c + 2 * x_facade1 + x_facade2 - 2) {
                        AllBalcony.get(m).CBOneTemplate(i-1,j,expr5,status);
                    } else if (i == c + x_facade1  || i == c + x_facade1 + x_facade2  || i == c + 2 * x_facade1 + x_facade2 ) {
                        AllBalcony.get(m).CBOneTemplate(i+2,j,expr5,status);
                    } else {
                        AllBalcony.get(m).CBOneTemplate(i-1,j,expr5,status);
                        AllBalcony.get(m).CBOneTemplate(i+2,j,expr5,status);
                    }
                }
                model.addGenConstrIndicator(status[i][j][5],1,expr5,GRB.LESS_EQUAL,0,"c0" + String.valueOf(i));
            }
        }

        model.set(GRB.IntParam.PoolSearchMode,2);
        model.set(GRB.IntParam.PoolSolutions,solutionCount);  //解数量
        model.set(GRB.DoubleParam.PoolGap,10);
        model.set(GRB.DoubleParam.TimeLimit,10);
        model.optimize();

        //储存运算结果
        for(int k=0;k<solutionCount;k++) {
            model.set(GRB.IntParam.SolutionNumber, k);
            System.out.println(model.get(GRB.DoubleAttr.PoolObjVal));
            for (int i = 0; i < x_num ; i++) {
                for (int j = 0; j < y_num ; j++) {
                    if (status[i][j][0].get(GRB.DoubleAttr.Xn) == 1) {
                        solutions[k][i][j]=-1;
                    } else if (status[i][j][1].get(GRB.DoubleAttr.Xn) == 1) {
                        solutions[k][i][j]=1;
                    } else if (status[i][j][2].get(GRB.DoubleAttr.Xn) == 1) {
                        solutions[k][i][j]=2;
                    } else if (status[i][j][3].get(GRB.DoubleAttr.Xn) == 1) {
                        solutions[k][i][j]=3;
                    } else if (status[i][j][4].get(GRB.DoubleAttr.Xn) == 1) {
                        solutions[k][i][j] = 4;
                    } else if (status[i][j][5].get(GRB.DoubleAttr.Xn) == 1) {
                        solutions[k][i][j] = 5;
                    }
                }
            }
        }
        model.dispose();
        env.dispose();

    }




    public void getOutIndex(){              //场地外所有点集合
        Q1=new ArrayList();
        //将超出场地范围的点加入集合
        for(int i=0;i<x_num;i++) {
            for (int j = 0; j < c; j++) {
                Q1.add(new int[]{i, j});
            }
        }
        for(int i=0;i<x_num;i++) {
            for (int j = y_num-c; j < y_num; j++) {
                Q1.add(new int[]{i, j});
            }
        }
        for(int i=0;i<c;i++) {
            for (int j = c; j < y_num-c; j++) {
                Q1.add(new int[]{i, j});
            }
        }
        for(int i=x_num-c;i<x_num;i++) {
            for (int j = c; j < y_num-c; j++) {
                Q1.add(new int[]{i, j});
            }
        }
    }

    public void getOutLineIndex() {              //场地范围外面一圈的集合
        Q2=new ArrayList();
        //将边界外一圈的点角标添加入数组
        for (int i = c; i < x_num - c; i++) {
            Q2.add(new int[]{i, c-1});
            Q2.add(new int[]{i, y_num - c});
        }
        for (int j = c; j < y_num - c; j++) {
            Q2.add(new int[]{c-1, j});
            Q2.add(new int[]{x_num - c, j});
        }

    }


    public void drawBalcony(){             //画出阳台

        noStroke();
        for (int i = 0; i < x_num; i++) {
            for (int j = 0; j < y_num ; j++) {
                if(solutions[Num][i][j]==-1){
                    int col=color (150, 84, 86);
                    balcony_0.drawThisBalcony_0(i,j,col,size);
                }else if(solutions[Num][i][j]==1){
                    int col=color (245, 219, 189);
                    balcony_1.drawThisBalcony_1(i,j,col,size);
                }else if(solutions[Num][i][j]==2) {
                    int col=color (158,170,186);
                    balcony_2.drawThisBalcony_2(i,j,col,size);
                }else if(solutions[Num][i][j]==3) {
                    int col=color (124, 139, 116);
                    balcony_3.drawThisBalcony_3(i,j,col,size);
                }else if(solutions[Num][i][j]==4) {
                    int col=color (151, 154, 163);
                    balcony_4.drawThisBalcony_4(i,j,col,size);
                }else if(solutions[Num][i][j]==5) {
                    int col=color (214, 142, 128);
                    balcony_5.drawThisBalcony_5(i,j,col,size);
                }
            }
        }
    }



    public void mousePressed() {
        if (mouseButton == LEFT) {
            ++Num;
            Num %= solutionCount;
        }
        frameCount = 0;

        fill(255);
        rect(0, 0, width, height);
        redraw();
    }

}