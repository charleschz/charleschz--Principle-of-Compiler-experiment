import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class recursive {//递归子程序
   /** 更新：E1改为G  T1改为H   +为a -为b  *为c  /为d
     * 改变符号后：
     * E-> TG  ① {I,(}
     * G-> aTG ② {+} | bTG ③ {-} |  ε ④ {),#}
     * T-> FH  ⑤ {I,(}
     * H-> cFH ⑥ {*} | dFH ⑦ {/} |  ε ⑧ {+，-,),#}
     * F-> I   ⑨ {I} | (E) ⑩ {（}
    */
    public lexAna lexA = new lexAna();
    public int lexListPos=0;//用来记录当前处理到第几个
    public int isRight = 1;//用来记录程序是否出错 -1出错 1正确
    public int isFinish=0;//用来记录程序是否结束 0则没有结束 1则结束了
    public Stack<String> SEM = new Stack<>();//语义栈
    public ArrayList<String> QT = new ArrayList<>();//用来存四元式
    public int QTNum=1;//用来标记四元式的个数


    public void E()throws IOException{//程序入口
        if(isFinish==1||isRight==-1){return;}
        //System.out.println("E");
        T();
        G();
    }

    public void T()throws IOException{
        if(isFinish==1||isRight==-1){return;}
        F();
        H();
    }

    public void G()throws IOException{
        if(isFinish==1||isRight==-1){return;}
        if(lexA.lexList.get(lexListPos).get("type").equals("opt")){
            if(lexA.lexList.get(lexListPos).get("index").equals("0")){
                //如果遇到+那么匹配成功，继续
                lexListPos++;
                //System.out.println(lexListPos);
                if(lexListPos==lexA.lexList.size()){isFinish=1;}
                T();
                G();
                if(SEM.size()<2){isRight=-1;return;}
                GEQ('+');
            }
            else if(lexA.lexList.get(lexListPos).get("index").equals("1")){
                //如果遇到-那么匹配成功，继续
                lexListPos++;
                //System.out.println(lexListPos);
                if(lexListPos==lexA.lexList.size()){isFinish=1;}
                T();
                G();
                if(SEM.size()<=1){isRight=-1;return;}
                GEQ('-');
            }
            else if(lexA.lexList.get(lexListPos).get("index").equals("10")||lexA.lexList.get(lexListPos).get("index").equals("11")
                    ||lexA.lexList.get(lexListPos).get("index").equals("2")||lexA.lexList.get(lexListPos).get("index").equals("3")){
                //如果是（）那么没关系 走向出口
                return;
            }
            else{
                //读到了不正常的符号 出错
                isRight=-1;
            }
        }
        else if(lexA.lexList.get(lexListPos).get("type").equals("num")||lexA.lexList.get(lexListPos).get("type").equals("id")){
            //读到标识符或者数字，走向出口
            return;
        }
        else{isRight=-1;}//否则有问题
    }

    public void H()throws IOException{
        if(isFinish==1||isRight==-1){return;}
        if(lexA.lexList.get(lexListPos).get("type").equals("opt")){
            if(lexA.lexList.get(lexListPos).get("index").equals("2")){
                //如果遇到*那么匹配成功，继续
                lexListPos++;
                //System.out.println(lexListPos);
                if(lexListPos==lexA.lexList.size()){isFinish=1;}
                F();
                H();
                if(SEM.size()<2){isRight=-1;return;}
                GEQ('*');
            }
            else if(lexA.lexList.get(lexListPos).get("index").equals("3")){
                //如果遇到/那么匹配成功，继续
                lexListPos++;
               // System.out.println(lexListPos);
                if(lexListPos==lexA.lexList.size()){isFinish=1;}
                F();
                H();
                if(SEM.size()<=1){isRight=-1;return;}
                GEQ('/');
            }
            else if(lexA.lexList.get(lexListPos).get("index").equals("10")||lexA.lexList.get(lexListPos).get("index").equals("11")
            ||lexA.lexList.get(lexListPos).get("index").equals("0")||lexA.lexList.get(lexListPos).get("index").equals("1")){
                //如果是（）那么没关系 走向出口
                return;
            }
            else{
                //读到了不正常的符号 出错
                isRight=-1;
            }
        }
        else if(lexA.lexList.get(lexListPos).get("type").equals("num")||lexA.lexList.get(lexListPos).get("type").equals("id")){
            //读到标识符或者数字，走向出口
            return;
        }
        else{isRight=-1;}//否则有问题
    }

    public void F()throws IOException{
        if(isFinish==1||isRight==-1){return;}
        if(lexA.lexList.get(lexListPos).get("type").equals("num")||lexA.lexList.get(lexListPos).get("type").equals("id")){
            //如果遇标识符和数字那么匹配成功，继续
            //标识符或数字进栈
            SEM.push(lexA.lexList.get(lexListPos).get("value").toString());
            lexListPos++;
           // System.out.println(lexListPos);
            if(lexListPos==lexA.lexList.size()){isFinish=1;}
        }
        else if(lexA.lexList.get(lexListPos).get("type").equals("opt")){
            if(lexA.lexList.get(lexListPos).get("index").equals("10")){
                //如果是（则说明匹配正确
                lexListPos++;
               // System.out.println(lexListPos);
                if(lexListPos==lexA.lexList.size()){isFinish=1;}
                E();
                if(isFinish==1){isRight=-1;return;}
                if(lexA.lexList.get(lexListPos).get("index").equals("11")){
                    lexListPos++;
                   // System.out.println(lexListPos);
                    if(lexListPos==lexA.lexList.size()){isFinish=1;}
                }
                else{isRight=-1;}
            }
            else{isRight=-1;}
        }
        else{isRight=-1;}
    }

    public void setOption()throws IOException {
        /**
         * 此函数用于先进行词法分析得到token串
         */
        lexA.judge();
    }

    public void GEQ(char type){
        String  opNum1;
        String  opNum2;
        switch (type){
            case '+':
                opNum2 = SEM.pop();
                opNum1 = SEM.pop();
                QT.add("+"+","+opNum1+","+opNum2+","+"t"+QTNum);
                SEM.push("t"+QTNum);
                QTNum++;
                break;
            case '-':
                opNum2 = SEM.pop();
                opNum1 = SEM.pop();
                QT.add("-"+","+opNum1+","+opNum2+","+"t"+QTNum);
                SEM.push("t"+QTNum);
                QTNum++;
                break;
            case '*':
                opNum2 = SEM.pop();
                opNum1 = SEM.pop();
                QT.add("*"+","+opNum1+","+opNum2+","+"t"+QTNum);
                SEM.push("t"+QTNum);
                QTNum++;
                break;
            case '/':
                opNum2 = SEM.pop();
                opNum1 = SEM.pop();
                QT.add("/"+","+opNum1+","+opNum2+","+"t"+QTNum);
                SEM.push("t"+QTNum);
                QTNum++;
                break;

            default:break;
        }

    }


    public static void main(String[] args)throws IOException{
        recursive re = new recursive();
        re.setOption();
        re.E();
        if(re.SEM.size()>=2){re.isRight=-1;}
        if(re.lexListPos<re.lexA.lexList.size()){re.isRight=-1;}
        if(re.isRight==-1){System.out.println("false");}
        else{System.out.println("true");}
        for(String ele:re.QT){System.out.println(ele);}
    }
}
