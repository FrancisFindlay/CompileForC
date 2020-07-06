package cn.edu.xaut.compile.lexical;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class LexicalAnalyzer {

    private static Scan scan;
    //词法分析其输出结果
    private static String output = "ResultFile/";
    //关键字
    private String[] keyword ={
            "auto","double","int","struct","break","else","long","switch",
            "case","enum","register","typedef","char","return","union","const",
            "extern","float","short","unsigned","continue","for","signed","void",
            "default","goto","sizeof","volatile","do","if","static","while"
    };
    //判别引号中的字符串
    private boolean flag = false;


    public LexicalAnalyzer(String fileName) {
        this.scan = new Scan(fileName);
    }

    public static void main(String[] args) {
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer("test.c");
        ArrayList<Token> tokens = lexicalAnalyzer.getTokens();
        try {
            lexicalAnalyzer.output(tokens,"result.c");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //输出结果集
    @SuppressWarnings("resource")
    public void output(ArrayList<Token> list,String filename) throws FileNotFoundException, IOException {
        filename = LexicalAnalyzer.output + filename;
        File file = new File(filename);
        while(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(file);
        for(int i = 0;i < list.size();i++){
            String str = "<"+list.get(i).type+","+list.get(i).value+">";
            pw.println(str);
        }
        pw.close();
    }


    //得到token
    public ArrayList<Token> getTokens(){
        ArrayList<Token> tokens = new ArrayList<>();
        int index = 0;
        while(index < scan.getLength()){
            Token token = analyze(index);
            tokens.add(token);
            index = scan.getPoint();
        }
        scan.retract(scan.getLength() - 1);
        return tokens;
    }

    //有限自动机
    public Token analyze(int index){
        int length = scan.getLength();
        int type = -1;
        String value = "";
        while(index < length){
            char ch = scan.getNextChar();
            index++;
            char ch1 = '\0';
            if(isDigit(ch)){ //是否为数字
                if(Type.isCalc(type)){
                    scan.retract(1);
                    break;
                }
                if(value == ""){
                    value = new Character(ch).toString();
                    type = Type.NUM;
                } else {
                    value += new Character(ch).toString();
                }

            } else if (isLetter(ch)){
                if(Type.isCalc(type)){
                    scan.retract(1);
                    break;
                }
                if(flag){
                    value = scan.getStringInQuotation(index);
                    type = Type.ID;
                    scan.move(value.length()-1);
                    return new Token(type,value);
                }
                if(type == Type.ID){
                    value += new Character(ch).toString();
                    continue;
                }
                String str = scan.getTestString(index);//index++过，因此，getTestString在探索下一个字符
                String val = null;
                if(str.startsWith("include")){
                    val = "include";
                    type = Type.INCLUDE;
                } else {
                    for(int i = 0;i < keyword.length;i++){
                        if(str.startsWith(keyword[i])){
                            val = keyword[i];
                            type = i;
                            break;
                        }
                    }
                }
                if(val == null){
                    type = Type.ID;
                    if(value == ""){
                        value = new Character(ch).toString();
                    } else {
                        value += new Character(ch).toString();
                    }
                } else {
                    value = val;
                    scan.move(value.length()-1);
                    return new Token(type,value);
                }

            } else {
                if(type == Type.NUM || type == Type.ID){
                    scan.retract(1);
                    return new Token(type,value);
                }
                switch(ch){
                    case '='://==,=
                        if(type == -1){
                            type = Type.ASSIGN;
                            value = "=";
                        } else if(type == Type.LT){//<=
                            type = Type.LE;
                            value = "<=";
                            return new Token(type,value);
                        } else if(type == Type.GT){//>=
                            type = Type.GE;
                            value = ">=";
                            return new Token(type,value);
                        } else if(type == Type.ASSIGN){//==
                            type = Type.EQUAL;
                            value = "==";
                            return new Token(type,value);
                        } else if(type == Type.NOT){//!=
                            type = Type.NE;
                            value = "!=";
                            return new Token(type,value);
                        } else if(type == Type.ADD){//+=
                            type = Type.INCREASEBY;
                            value = "+=";
                            return new Token(type,value);
                        } else if(type == Type.SUB){//-=
                            type = Type.DECREASEBY;
                            value = "-=";
                            return new Token(type,value);
                        } else if(type == Type.DIV){///=
                            type = Type.DIVBY;
                            value = "/=";
                            return new Token(type,value);
                        } else if(type == Type.MUL){//*=
                            type = Type.MULBY;
                            value = "*=";
                            return new Token(type,value);
                        }
                        break;
                    case '+':
                        if(type == -1){
                            type = Type.ADD;
                            value = "+";
                        } else if(type == Type.ADD){//++
                            type = Type.INCREASE;
                            value = "++";
                            return new Token(type,value);
                        }
                        break;
                    case '-':
                        if(type == -1){
                            type = Type.SUB;
                            value = "-";
                        } else if(type == Type.SUB){//--
                            type = Type.DECREASEBY;
                            value = "--";
                            return new Token(type,value);
                        }
                        break;
                    case '*':
                        if(type == -1){
                            type = Type.MUL;
                            value = "*";
                        }
                        break;
                    case '/':
                        if(type == -1){
                            type = Type.DIV;
                            value = "/";
                        }
                        break;
                    case '<':
                        if(type == -1){
                            type = Type.LT;
                            value = "<";
                        }
                        break;
                    case '>':
                        if(type == -1){
                            type = Type.GT;
                            value = ">";
                        }
                        break;
                    case '!':
                        if(type == -1){
                            type = Type.NOT;
                            value = "!";
                        }
                        break;
                    case '|':
                        if(type == -1){
                            type = Type.OR_1;
                            value = "|";
                        } else if(type == Type.OR_1){
                            type = Type.OR_2;
                            value = "||";
                            return new Token(type,value);
                        }
                        break;
                    case '&':
                        if(type == -1){
                            type = Type.AND_1;
                            value = "&";
                        } else if(type == Type.AND_1){
                            type = Type.AND_2;
                            value = "&&";
                            return new Token(type,value);
                        }
                        break;
                    case ';':
                        if(type == -1){
                            type = Type.SEMICOLON;
                            value = ";";
                        }
                        break;
                    case '{':
                        if(type == -1){
                            type = Type.BRACE_L;
                            value = "{";
                        } else if(Type.isCalc(type)){
                            scan.retract(1);
                            return new Token(type,value);
                        }
                        break;
                    case '}':
                        if(type == -1){
                            type = Type.BRACE_R;
                            value = "}";
                        } else if(Type.isCalc(type)){
                            scan.retract(1);
                            return new Token(type,value);
                        }
                        break;
                    case '[':
                        if(type == -1){
                            type = Type.BRACKET_L;
                            value = "[";
                        } else if(Type.isCalc(type)){
                            scan.retract(1);
                            return new Token(type,value);
                        }
                        break;
                    case ']':
                        if(type == -1){
                            type = Type.BRACKET_R;
                            value = "]";
                        } else if(Type.isCalc(type)){
                            scan.retract(1);
                            return new Token(type,value);
                        }
                        break;
                    case '(':
                        if(type == -1){
                            type = Type.PARENTHESIS_L;
                            value = "(";
                        } else if(Type.isCalc(type)){
                            scan.retract(1);
                            return new Token(type,value);
                        }
                        break;
                    case ')':
                        if(type == -1){
                            type = Type.PARENTHESIS_R;
                            value = ")";
                        } else if(Type.isCalc(type)){
                            scan.retract(1);
                            return new Token(type,value);
                        }
                        break;
                    case '#':
                        if(type == -1){
                            type = Type.POUND;
                            value = "#";
                        }
                        break;
                    case ',':
                        if(type == -1){
                            type = Type.COMMA;
                            value = ",";
                        }
                        break;
                    case '\'':
                        if(type == -1){
                            type = Type.SINGLE_QUOTAOTION;
                            value = "\'";
                        }
                        break;
                    case '"':
                        if(flag == false){
                            flag = true; //表明这是配对的双引号中的第一个
                        } else {
                            flag = false;
                        }
                        if(type == -1){
                            type = Type.DOUBLE_QUOTATION;
                            value = "\"";
                        }
                        break;
                    default:
                        break;
                }
                if(!Type.isCalc(type)){
                    break;
                }
            }
        }
        if(value.length()>1){
            scan.move(value.length()-1);
        }
        Token token = new Token(type,value);
        return token;
    }


    public boolean isDigit(char c){
        if((c <= '9' && c >= '0')|| c == '.')
            return true;
        return false;
    }

    public boolean isLetter(char c){
        if((c >= 'a' && c <= 'z')|| c == '_' || (c >= 'A' && c<= 'Z')){
            return true;
        }
        return false;
    }

}