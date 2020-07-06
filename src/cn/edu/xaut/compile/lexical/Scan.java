package cn.edu.xaut.compile.lexical;

import java.io.*;
import java.util.ArrayList;

/*
 *
 */

public class Scan {
    //输入的目录
    private static final String fileContent = "SourceFile/";
    //
    private String input;
    //缓冲区指针,指向即将要处理的字符
    private int point;
    //单词集合
    private ArrayList<Character> words = new ArrayList<>();

    public Scan(String fileName){
        File file = new File(fileContent + fileName);
        try {
            InputStream in = new FileInputStream(file);
            char c1 = ' ';
            char c2 = ' ';
            while (in.available() > 0) {
                if (c2 != ' ') {
                    c1 = c2;
                } else {
                    c1 = (char) in.read();
                }
                if (c1 == '\'') {
                    words.add(c1);
                    words.add((char) in.read());
                    words.add((char) in.read());
                } else if (c1 == '\"') {
                    words.add(c1);
                    while (in.available() > 0) {
                        c1 = (char) in.read();
                        words.add(c1);
                        if (c1 == '\"') break;
                    }
                } else if (c1 == '/') {  //消除注释， /* */和 //两种格式
                    c2 = (char) in.read();
                    if (c2 == '/') {
                        while (in.available() > 0) {
                            c2 = (char) in.read();
                            if (c2 == '\n') break;
                        }
                    } else if (c2 == '*') {
                        while (in.available() > 0) {
                            c1 = (char) in.read();
                            if (c1 == '*') {
                                c2 = (char) in.read();
                                if (c2 == '/') {
                                    c2 = ' ';
                                    break;
                                }
                            }
                        }
                    } else {
                        if (c2 == ' ') {
                            while (c2 == ' ') {
                                c2 = (char) in.read();
                            }
                        }
                        words.add(c1);
                        words.add(c2);
                        c2 = ' ';
                    }
                } else if (c1 == ' ') {
                    if (words.get(words.size() - 1) == ' ') {
                        continue;
                    }
                } else {
                    if ((int) c1 == 13 || (int) c1 == 10 || (int) c1 == 32) { //去除换行
                        c2 = ' ';
                    } else {
                        words.add(c1); //向words输入
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        char chars[] = new char[words.size()];
        for(int i=0;i<words.size();i++){
            chars[i] = words.get(i);
        }
        String result = new String(chars);
        this.input = result;
        this.point = 0;
        System.out.println(input);
        System.out.println((char)0);
    }

    public char getNextChar(){
        if(point == input.length())
            return (char) 0; // (char) 0:nul
        return input.charAt(point++);//
    }

    //回退n
    public void retract(int n){
        point = point - n;
    }

    public int getPoint(){
        return point;
    }

    public int getLength(){
        return this.input.length();
    }


    public String getSubStr(int index,int length){
        if((index+length-1)>=this.input.length()){
            return null;
        } else {
            String result = this.input.substring(index,index+length);
            return result;
        }
    }

    public String getTestString(int index){
        int temp = index;
        int len = 1;
        while(isLetterOrDigit(input.charAt(temp)) && (temp <= (input.length() - 1))){
            temp++;
            len++;
        }
        String result = input.substring(index-1,index-1+len);
        return result;
    }

    private boolean isLetterOrDigit(char c){
        if( c =='_'||(c >= 'a'&& c<= 'z')||(c>='A'&&c<='Z')||(c>='0'&&c<='9')){
            return true;
        } else {
            return false;
        }
    }

    public String getLeftStr(int index){
        if(index == input.length()-1){
            return null;
        } else {
            return input.substring(index);
        }
    }

    public void move(int n){
        point = point + n;
    }

    public String getStringInQuotation(int index){
        int temp = index;
        while(input.charAt(temp-1) != '\"'){
            temp--;
        }
        StringBuilder sb = new StringBuilder();
        while(input.charAt(temp) != '\"'){
            sb.append(input.charAt(temp));
            temp++;
        }
        return sb.toString();
    }


}