package cn.edu.xaut.compile.syntax;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import cn.edu.xaut.compile.syntax.Derivation;

public class CFG {

    public static String emp = "ε";

    public static String end = "$";

    public static TreeSet<String> keywords = new TreeSet<String>();//保留字集

    public static TreeSet<String> VN = new TreeSet<String>();//非终结符集
    public static TreeSet<String> VT = new TreeSet<String>();//终结符集
    public static ArrayList<Derivation> F = new ArrayList<Derivation>();//产生式集

    public static HashMap<String,TreeSet<String> > firstMap = new HashMap<String,TreeSet<String> >();//first
    public static HashMap<String,TreeSet<String> > followMap = new HashMap<String,TreeSet<String> >();//follow

    static{
        //从文件中读取文法，添加相应的产生式
        try {
            read("cfg.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //添加C语言的保留字
        String[] keyword ={
                "auto","double","int","struct","break","else","long","switch",
                "case","enum","register","typedef","char","return","union","const",
                "extern","float","short","unsigned","continue","for","signed","void",
                "default","goto","sizeof","volatile","do","if","static","while"
        };
        for(String k:keyword){
            keywords.add(k);
        }
//		S->if B S;|if B S; else S;|<id>=E|S;S
//		B->B >= B|<num>|<id>
//		E->E+E|E*E|<num>|<id>
        //添加非终结符
        VN.add("S'");VN.add("S");VN.add("B");VN.add("E");
        VT.add("if");
        VT.add("else");
        VT.add(";");
        VT.add("=");
        VT.add(">=");
        VT.add("<num>");
        VT.add("<id>");
        VT.add("*");
        VT.add("+");
        VT.add("(");
        VT.add(")");

        addFirst();
        //addFollow();
    }

    /**
     * 从文件中读取文法并且存储到CFG类的静态容器中，编号就是容器的index
     * @param filename
     * @throws FileNotFoundException
     */
    private static void read(String filename) throws FileNotFoundException{
        File file = new File("SourceFile/"+filename);
        Scanner scanner = new Scanner(file);
        while(scanner.hasNext()){
            String line = scanner.nextLine();
            String[] div = line.split("->");
            String[] right = div[1].split("\\|");//将合并书写的多个表达式解析成多个
            for(String r:right){
                Derivation derivation = new Derivation(div[0]+"->"+r);
                F.add(derivation);//存储到静态的容器中
            }
        }
        scanner.close();
    }

    /**
     * 计算所有符号的first集合
     * 中间需要若干步推导的使用一个递归方法解决问题
     */
    private static void addFirst(){
        //将所有的终结符的first都设为本身
        Iterator<String> iterVT = VT.iterator();
        while(iterVT.hasNext()){
            String vt = iterVT.next();
            firstMap.put(vt,new TreeSet<String>());
            firstMap.get(vt).add(vt);
        }
        //计算所有非终结符的first集合
        Iterator<String> iterVN = VN.iterator();
        while(iterVN.hasNext()){
            String vn = iterVN.next();
            firstMap.put(vn, new TreeSet<String>());//因为后续操作没有交叉涉及firstMap，所以不必分成两个while循环，合成一趟即可
            int dSize = F.size();
            for(int i = 0;i < dSize;i++){
                Derivation d = F.get(i);
                if(d.left.equals(vn)){//其实可以到后面抽象成一个方法获取，这里懒得改了
                    if(VT.contains(d.list.get(0))){//如果是产生式右端第一个文法符号是一个终结符，则直接添加
                        firstMap.get(vn).add(d.list.get(0));
                    } else {//如果产生式右端第一个文法符号是个非终结符，则需要进行递归查找
                        firstMap.get(vn).addAll(findFirst(d.list.get(0)));
                    }
                }
            }
        }
    }

    /**
     * 一个用于查找first的递归函数
     * @param vn
     * @return
     */
    private static TreeSet<String> findFirst(String vn){
        TreeSet<String> set = new TreeSet<String>();
        for(Derivation d:F){
            if(d.left.equals(vn)){
                if(VT.contains(d.list.get(0))){//如果是个终结符，则直接加入
                    set.add(d.list.get(0));
                } else {
                    if(!vn.equals(d.list.get(0))){//去除类似于E->E*E这样的左递归，从而有效避免栈溢出
                        set.addAll(findFirst(d.list.get(0)));//再次递归
                    }
                }
            }
        }
        return set;
    }

	/**
	 * 用于计算非终结符的follow
	 */
    private static void addFollow() {
        Iterator<String> iterVN = VN.iterator();
        HashMap<String, ArrayList<String>> hashmap = new HashMap<String, ArrayList<String>>();
        while (iterVN.hasNext()) {
            String vn = iterVN.next();
            followMap.put(vn, new TreeSet<String>());
            hashmap.put(vn, new ArrayList<String>());
            for (Derivation d : F) {
                if (d.list.contains(vn)) {//这里用一个循环是因为考虑了产生式右端存在多个相同元素
                    ArrayList<Integer> index = new ArrayList<Integer>();
                    for (int i = 0; i < d.list.size(); i++) {
                        if (d.list.get(i).equals(vn)) {
                            index.add(i);
                        }
                    }
                    for (int i : index) {
                        if (i == (d.list.size() - 1)) {//如果在一个产生式右端的末尾，则需要加入$
                            followMap.get(vn).add(CFG.end);
                            hashmap.get(vn).add(d.left);
                        } else {//如果不在末尾，则直接加上后一个元素的first
                            TreeSet<String> add = new TreeSet<String>();
                            Iterator<String> iter = firstMap.get(d.list.get(i + 1)).iterator();
                            while (iter.hasNext()) {
                                String value = iter.next();
                                if (!value.equals(CFG.emp)) {//滤掉空串
                                    add.add(value);
                                }
                            }
                            followMap.get(vn).addAll(add);
                        }
                    }
                }
            }
            Iterator<String> iter = hashmap.keySet().iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                ArrayList<String> value = hashmap.get(key);
                if (value.size() != 0) {
                    for (String v : value) {
                        followMap.get(key).addAll(followMap.get(v));
                        followMap.get(v).addAll(followMap.get(key));
                    }
                }
            }
        }
    }

}