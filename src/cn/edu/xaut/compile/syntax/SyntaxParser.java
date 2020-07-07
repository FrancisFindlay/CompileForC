package cn.edu.xaut.compile.syntax;


import java.util.ArrayList;
import java.util.Stack;

import cn.edu.xaut.compile.lexical.LexicalAnalyzer;
import cn.edu.xaut.compile.lexical.Token;
import cn.edu.xaut.compile.lexical.Type;

public class SyntaxParser {

    private LexicalAnalyzer lex;//词法分析器
    private ArrayList<Token> tokenList;//从词法分析器获得的所有token,相当于模型中的输入
    private int length;//tokenlist的长度
    private int index;//现在所指的token位置

    private AnalyzeTable table;//构造的语法分析表
    private Stack<Integer> stateStack;//用于存储相应的状态

    private Error error = null;

    public static void main(String[] args){
        SyntaxParser parser = new SyntaxParser("test.c");
        parser.analyze();
    }

    public SyntaxParser(String filename){
        this.lex = new LexicalAnalyzer(filename);
        this.tokenList = lex.getTokens();
        this.tokenList.add(new Token(-1,"$"));
        this.length = this.tokenList.size();
        this.index = 0;
        this.table = new AnalyzeTable();
        this.stateStack = new Stack<Integer>();
        this.stateStack.push(0);
        this.table.dfa.printAllStates();
        this.table.print();
        for(int i = 0;i < tokenList.size();i++){
            System.out.println(tokenList.get(i).toString());
        }
    }

    public Token readToken(){
        if(index < length){
            return tokenList.get(index++);
        } else {
            return null;
        }
    }

    public void analyze(){
        while(true){
            Token token = readToken();
            int valueType = token.type;
            String value = getValue(valueType);
            int state = stateStack.lastElement();
            String action = table.ACTION(state, value);
            System.out.println(action);
            if(action.startsWith("s")){
                int newState = Integer.parseInt(action.substring(1));
                stateStack.push(newState);
                System.out.print("移入"+"\t");
                System.out.print("状态表:"+stateStack.toString()+"\t");
                System.out.print("输入:");
                printInput();
                System.out.println();
                System.out.println();
            } else if(action.startsWith("r")){
                Derivation derivation = CFG.F.get(Integer.parseInt(action.substring(1)));
                int r = derivation.list.size();
                index--;
                for(int i = 0;i < r;i++){
                    stateStack.pop();
                }
                int s = table.GOTO(stateStack.lastElement(), derivation.left);
                stateStack.push(s);
                System.out.print("规约"+"\t");
                System.out.print("状态表:"+stateStack.toString()+"\t");
                System.out.print("输入:");
                printInput();
                System.out.println();
            } else if(action.equals(AnalyzeTable.acc)){
                System.out.print("语法分析完成"+"\t");
                System.out.print("状态表:"+stateStack.toString()+"\t");
                System.out.print("输入:");
                printInput();
                System.out.println();
                return;
            } else {
                error();
                return;
            }


        }
    }


    private String getValue(int valueType){
        switch(valueType){
            case Type.ADD:
                return "+";
            case Type.SUB:
                return "-";
            case Type.MUL:
                return "*";
            case Type.DIV:
                return "/";
            case Type.ID:
                return "<id>";
            case Type.NUM:
                return "<num>";
            case Type.IF:
                return "if";
            case Type.ELSE:
                return "else";
            case Type.SEMICOLON:
                return ";";
            case Type.PARENTHESIS_L:
                return "(";
            case Type.PARENTHESIS_R:
                return ")";
            case Type.GE:
                return ">=";
            case Type.ASSIGN:
                return "=";
            case -1:
                return "$";
            default:
                return null;
        }
    }
    /**
     * 出错
     */
    public void error(){
        System.out.println("第"+(index-1)+"词法分析元素处发现了错误:"+tokenList.get(index-1).toString());
    }

    private void printInput(){
        String output = "";
        for(int i = index;i < tokenList.size();i++){
            output += tokenList.get(i).value;
            output += " ";
        }
        System.out.print(output);
    }

}