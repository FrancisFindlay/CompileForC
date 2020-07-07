package cn.edu.xaut.compile.syntax;

/*
 * 拓广文法G
 */
public class LRDerivation implements java.lang.Cloneable{
    //d为一个产生式
    public Derivation d;
    //lr为LR1文法右端的终结符
    public String lr;
    //LR1项目点的位置
    public int index;

    public LRDerivation(Derivation d,String lr,int index){
        this.d = d;
        this.lr = lr;
        this.index = index;
    }

    public String toString(){
        String result = d.left+"->";
        int length = d.list.size();
        for(int i = 0;i < length;i++){
            result += " ";
            if(i == index){
                result += ".";
            }
            result += d.list.get(i);
        }
        if(index == length){
            result += ".";
        }
        result += " ,";
        result += lr;
        return result;
    }

    public boolean equalTo(LRDerivation lrd){
        if(d.equalTo(lrd.d)&&lr.hashCode()==lrd.lr.hashCode()&&index==lrd.index){
            return true;
        } else {
            return false;
        }
    }

    public void print(){
        System.out.println(this.toString());
    }

    public Object clone(){
        return new LRDerivation(d,lr,index);
    }

}