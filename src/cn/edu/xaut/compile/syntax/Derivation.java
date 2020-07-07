package cn.edu.xaut.compile.syntax;

import java.util.ArrayList;

public class Derivation {
    //产生式左部
    public String left;
    //右部
    public ArrayList<String> list = new ArrayList<>();

    public Derivation(String p) {
        String[] splits = p.split("->");
        this.left = splits[0];
        String[] s = splits[1].split(" ");
        for(int i = 0;i < s.length;i++){
            list.add(s[i]);
        }
    }

    public String toString(){
        String result = left + "->";
        for(String s : list){
            result += s;
            result += " ";
        }
        return result.trim();
    }

    public boolean equalTo(Derivation p){
        if(this.toString().equals(p.toString()))
            return true;
        return false;
    }

    public void print(){
        System.out.println(this.toString());
    }
}
