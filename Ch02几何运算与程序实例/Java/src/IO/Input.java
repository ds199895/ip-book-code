package IO;

import processing.core.PVector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Input {
    public static void main(String[] args) {
        ArrayList<double[][]>out=readFile("E://circles.xml","callback");
        for(int i=0;i<out.size();i++){
            for(int j=0;j<out.get(i).length;j++){
//                for(int k=0;k<out.get(i)[j].length;k++){
                    System.out.println("circle:"+" x: "+out.get(i)[j][0]+" y: "+out.get(i)[j][1]+" r: "+out.get(i)[j][2]);
//                }
            }
        }
    }
    public static ArrayList<PVector> readTxtFile(String filePath,String split){
        ArrayList<PVector>cities=new ArrayList<>();
        try {
            String encoding="GBK";
            File file=new File(filePath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                    String[]points=lineTxt.split(",");
                    cities.add(new PVector((float)Double.parseDouble(points[0]),(float) Double.parseDouble(points[1])));
//          System.out.println(lineTxt);
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
    public static ArrayList<double[][]> readFile(String filePath,String split){
        ArrayList<double[][]>outcomes=new ArrayList<>();
        String[]strings;
        try {
            String encoding="GBK";
            File file=new File(filePath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                String s="";
                while((lineTxt = bufferedReader.readLine()) != null){
//                    String[]points=lineTxt.split(",");
//                    cities.add(new PVector((float)Double.parseDouble(points[0]),(float) Double.parseDouble(points[1])));
                    s+=lineTxt;

                }
                strings=s.split(split);

                for(int i=0;i<strings.length;i++){
                    System.out.println(strings[i]);

                    String[]s_sub1=strings[i].split("circle");
                    double[][]infos=new double[s_sub1.length][3];
                    for(int j=0;j<s_sub1.length;j++){
                        System.out.println(s_sub1[j]);
                        String[]s_sub2=s_sub1[j].split(",");
                        for(int k=0;k<s_sub2.length;k++){

//                            double num=Double.parseDouble(s_sub2[k]);
//                            Float.parseFloat(s_sub2[k]);
                            System.out.println(s_sub2[k].toCharArray());
//                            infos[j][k]=Double.parseDouble(s_sub2[k]);
                        }
                    }
//                    outcomes.add(infos);
                }
                read.close();
            }else{
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return  outcomes;
    }
}

