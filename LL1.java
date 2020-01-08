import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

public class LL1 {
    /**
     * @author charleschz
     * 使用的文法：(根据PPT,更改符号的表示)
     * 原来的文法
     *   E -> T E1 ① {I,(}
     *   E1-> ω0 T E1 ② {ω0 }|ε ③{),#}
     *   T -> F T1 ④ {I,(}
     *   T1-> ω1 F T1 ⑤{ω1}|ε⑥{ω0,),#}
     *   F -> I ⑦{I} | ( E ) ⑧{(}
     *
     *   W0(+,-)  W1(*,/)
     *
     * 更新：E1改为G  T1改为H   +为a -为b  *为c  /为d
     * 改变符号后：
     * E-> TG  ① {I,(}
     * G-> aTG ② {+} | bTG ③ {-} |  ε ④ {),#}
     * T-> FH  ⑤ {I,(}
     * H-> cFH ⑥ {*} | dFH ⑦ {/} |  ε ⑧ {+，-,),#}
     * F-> I   ⑨ {I} | (E) ⑩ {（}
     *
     *
     *   根据此文法可以构造LL1分析表
     *        I     a     b     c     d     (     )     #
     * E      1                             1
     * G            2     3                       4     4
     * T      5                             5
     * H            8     8     6     7           8     8
     * F      9                             10
     *
     */
     public static int anaMatrix[][] = new int [5][8];//用来储存分析表
     public lexAna lexA = new lexAna();
     public Stack<String> ana = new Stack<>();//分析栈
     public Stack<String> SEM = new Stack<>();//语义栈
     public ArrayList<String> QT = new ArrayList<>();//用来存四元式
     public int QTNum=1;//用来标记四元式的个数





    public void setAnaMatrix(){
        /**
         * 此函数作用是将分析表存入到一个二位数组中
         */
        anaMatrix[0][0] = 1;
        anaMatrix[0][5] = 1;
        anaMatrix[1][1] = 2;
        anaMatrix[1][2] = 3;
        anaMatrix[1][6] = 4;
        anaMatrix[1][7] = 4;
        anaMatrix[2][0] = 5;
        anaMatrix[2][5] = 5;
        anaMatrix[3][1] = 8;
        anaMatrix[3][2] = 8;
        anaMatrix[3][3] = 6;
        anaMatrix[3][4] = 7;
        anaMatrix[3][6] = 8;
        anaMatrix[3][7] = 8;
        anaMatrix[4][0] = 9;
        anaMatrix[4][5] = 10;
        for(int i=0;i<anaMatrix.length;i++){
            for(int j=0;j<anaMatrix[i].length;j++){
                if(anaMatrix[i][j]<1){anaMatrix[i][j]=-1;}
            }
        }
    }

    public void setOption()throws IOException {
        /**
         * 此函数用于先进行词法分析以及建立分析表
         */
        setAnaMatrix();
        lexA.judge();
    }

    /**
     * Integer.valueOf((String)lexA.lexList.get(i).get("index"))==11   ？？？
     * @return
     * @throws IOException
     */
    public String judge()throws IOException{
        setOption();
        ana.push("E");//E先进栈
        int i=0;
        while(i<lexA.lexList.size()){
            //System.out.println(ana);
            if(ana.size()==0){return "false";}
            String anaWord = ana.pop();
            //System.out.println((Integer.valueOf((Integer) lexA.lexList.get(i).get("index"))==11));
            //System.out.println(anaWord);
            //System.out.println(lexA.lexList.get(i).get("type")+" "+lexA.lexList.get(i).get("index"));
            int xPos=getXPos(anaWord);
            //System.out.println(xPos);
            if(lexA.lexList.get(i).get("type").equals("id")){//处理标识符
                if(anaWord=="I"){//成功匹配
                    SEM.push(lexA.lexList.get(i).get("value").toString());//标识符入栈
                    i++;
                    //System.out.println("匹配ID成功");
                    continue;
                }
                else if(isGeq(anaWord)==1){
                    GEQ(anaWord);
                    continue;
                }
                //System.out.println(xPos);
                //System.out.println(anaMatrix[xPos][0]);
                if(anaMatrix[xPos][0]==-1){return "false";}//如果没有生成式则报错
                else{
                    operation(anaMatrix[xPos][0]);
                }
            }
            else if(lexA.lexList.get(i).get("type").equals("opt")&&lexA.lexList.get(i).get("index").equals("0")){//处理+
                //System.out.println("+");
                if(anaWord=="a"){i++;continue;}//匹配成功
                else if(isGeq(anaWord)==1){
                    GEQ(anaWord);
                    continue;
                }
                if(anaMatrix[xPos][1]==-1){return "false";}
                else{
                    operation(anaMatrix[xPos][1]);
                }
            }
            else if(lexA.lexList.get(i).get("type").equals("opt")&&lexA.lexList.get(i).get("index").equals("1")){//处理-
                //System.out.println("-");
                if(anaWord=="b"){i++;continue;}//匹配成功
                else if(isGeq(anaWord)==1){
                    GEQ(anaWord);
                    continue;
                }
                if(anaMatrix[xPos][2]==-1){return "false";}
                else{
                    operation(anaMatrix[xPos][2]);
                }
            }
            else if(lexA.lexList.get(i).get("type").equals("opt")&&lexA.lexList.get(i).get("index").equals("2")){//处理*
                //System.out.println("*");
                if(anaWord=="c"){i++;continue;}//匹配成功
                else if(isGeq(anaWord)==1){
                    GEQ(anaWord);
                    continue;
                }
                if(anaMatrix[xPos][3]==-1){return "false";}
                else{
                    operation(anaMatrix[xPos][3]);
                }
            }
            else if(lexA.lexList.get(i).get("type").equals("opt")&&lexA.lexList.get(i).get("index").equals("3")){//处理/
                //System.out.println("/");
                if(anaWord=="d"){i++;continue;}//匹配成功
                else if(isGeq(anaWord)==1){
                    GEQ(anaWord);
                    continue;
                }
                if(anaMatrix[xPos][4]==-1){return "false";}
                else{
                    operation(anaMatrix[xPos][4]);
                }
            }
            else if(lexA.lexList.get(i).get("type").equals("opt")&&lexA.lexList.get(i).get("index").equals("10")){//处理(
                //System.out.println("(");
                if(anaWord=="("){i++;continue;}//匹配成功
                else if(isGeq(anaWord)==1){
                    GEQ(anaWord);
                    continue;
                }
                if(anaMatrix[xPos][5]==-1){return "false";}
                else{
                    operation(anaMatrix[xPos][5]);
                }
            }
            else if(lexA.lexList.get(i).get("type").equals("opt")&&lexA.lexList.get(i).get("index").equals("11")){//处理)
                if(anaWord==")"){i++;continue;}//匹配成功
                else if(isGeq(anaWord)==1){
                    GEQ(anaWord);
                    continue;
                }
                if(anaMatrix[xPos][6]==-1){return "false";}
                else{
                    operation(anaMatrix[xPos][6]);
                }
            }
            else if(lexA.lexList.get(i).get("type").equals("num")){
                if(anaWord=="I"){//成功匹配
                    SEM.push(lexA.lexList.get(i).get("value").toString());//数字入栈
                    i++;
                    continue;
                }
                else if(isGeq(anaWord)==1){
                    GEQ(anaWord);
                    continue;
                }
                if(anaMatrix[xPos][0]==-1){return "false";}//如果没有生成式则报错
                else{
                    operation(anaMatrix[xPos][0]);
                }
            }
            else{return "false";}
        }
        while(ana.size()!=0){
            String anaWord=ana.pop();
            //System.out.println(ana);
            if(isGeq(anaWord)==1) {
                GEQ(anaWord);
            }
            if(anaWord==")"||anaWord=="("||anaWord=="I"||anaWord=="a"||anaWord=="b"||anaWord=="c"||anaWord=="d"){
                return "false";
            }

        }
        return "true";
    }

    public int getXPos(String word){
        int xPos=-1;
        switch (word){
            case "E":
                xPos=0;
                break;
            case "G":
                xPos=1;
                break;
            case "T":
                xPos=2;
                break;
            case "H":
                xPos=3;
                break;
            case "F":
                xPos=4;
                break;
        }
        return xPos;
    }

    /**
     * 此函数作用是对栈进行相应操作
     * @param op
     */
    public void operation(int op){
        if(op==1){//逆序进栈
            ana.push("G");
            ana.push("T");
            //System.out.println("进栈成功");
        }
        else if(op==2){
            ana.push("G");
            ana.push("GEQa");
            ana.push("T");
            ana.push("a");
        }
        else if(op==3){
            ana.push("G");
            ana.push("GEQb");
            ana.push("T");
            ana.push("b");
        }
        else if(op==5){
            ana.push("H");
            ana.push("F");
        }
        else if(op==6){
            ana.push("H");
            ana.push("GEQc");
            ana.push("F");
            ana.push("c");
        }
        else if(op==7){
            ana.push("H");
            ana.push("GEQd");
            ana.push("F");
            ana.push("d");
        }
        else if(op==9){
            ana.push("I");
            //ana.push("PushI");
        }
        else if(op==10){
            ana.push(")");
            ana.push("E");
            ana.push("(");
        }

    }

    /**
     * 如果是生成四元式操作 则返回1
     * 如果不是，则返回0
     * @param op
     * @return int
     */
    public int isGeq(String op){
        if(op.equals("GEQa")||op.equals("GEQb")||op.equals("GEQc")||op.equals("GEQd")){
            return 1;
        }
        return 0;
    }

    public void GEQ(String type){
        String  opNum1;
        String  opNum2;
        switch (type){
            case "GEQa":
                opNum2 = SEM.pop();
                opNum1 = SEM.pop();
                QT.add("+"+","+opNum1+","+opNum2+","+"t"+QTNum);
                SEM.push("t"+QTNum);
                QTNum++;
                break;
            case "GEQb":
                opNum2 = SEM.pop();
                opNum1 = SEM.pop();
                QT.add("-"+","+opNum1+","+opNum2+","+"t"+QTNum);
                SEM.push("t"+QTNum);
                QTNum++;
                break;
            case "GEQc":
                opNum2 = SEM.pop();
                opNum1 = SEM.pop();
                QT.add("*"+","+opNum1+","+opNum2+","+"t"+QTNum);
                SEM.push("t"+QTNum);
                QTNum++;
                break;
            case "GEQd":
                opNum2 = SEM.pop();
                opNum1 = SEM.pop();
                QT.add("/"+","+opNum1+","+opNum2+","+"t"+QTNum);
                SEM.push("t"+QTNum);
                QTNum++;
                break;

            default:break;
        }
    }

    public static void main(String[] args){
        try {
            LL1 llone=new LL1();
            String result=llone.judge();
            System.out.println(result);
            for(String ele:llone.QT){System.out.println(ele);}
        }catch (IOException e){}

    }
}
