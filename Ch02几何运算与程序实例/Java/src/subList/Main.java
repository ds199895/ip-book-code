package subList;

import processing.core.PApplet;

import java.util.ArrayList;

public class Main extends PApplet {
    public static void main(String[] args) {
        ArrayList<Integer>numd=new ArrayList<>();
        numd.add(3);
        numd.add(4);
        numd.add(5);

        ArrayList<ArrayList<Integer>>subLists=getSubsets(numd);
        for(ArrayList<Integer>sub:subLists){
            System.out.println(sub);
        }

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
}
