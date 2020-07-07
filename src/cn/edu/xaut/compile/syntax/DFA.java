package cn.edu.xaut.compile.syntax;

import java.util.ArrayList;

public class DFA {

    public ArrayList<DFAState> states = new ArrayList<DFAState>();

    public DFAState get(int i){
        return states.get(i);
    }

    public int size(){
        return states.size();
    }

    public int contains(DFAState state){
        for(int i = 0;i <states.size();i++){
            if(states.get(i).equals(state)){
                return i;
            }
        }
        return -1;
    }

    public void printAllStates(){
        int size = states.size();
        for(int i = 0;i < size;i++){
            System.out.println("I"+i+":");
            states.get(i).print();
        }
    }
}