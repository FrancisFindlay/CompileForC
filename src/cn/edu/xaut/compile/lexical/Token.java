package cn.edu.xaut.compile.lexical;

public class Token {
    public int type;
    public String value;
    public Token(int type,String value){
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "<"+this.type+","+this.value+">";
    }
}
