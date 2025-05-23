package com.AlgorithmSelfSpitting.Unidbgtrace;

import com.AlgorithmSelfSpitting.Unidbgtrace.entity.algorithmType;
import com.AlgorithmSelfSpitting.Unidbgtrace.util.AlgorithmAutoEmitterUtility;
import com.AlgorithmSelfSpitting.Unidbgtrace.util.SaveLog;
import com.AlgorithmSelfSpitting.Unidbgtrace.util.StringUtil;
import com.AlgorithmSelfSpitting.publicclass.publicEntity;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TraceParser_UnidbgTrace {

    /**
     * 读取行，根据行类型做操作
     * @param filePath
     */
    public static void readFileByLine(String filePath){
        Map<String,String> superMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {

                if (line.indexOf("["+publicEntity.libName+"  0x")!=-1) {
//                    String [] split_INSTRUCTION = line.toString().split("  ");
                    Map<String,String> map = new HashMap<>();
                    String nextLine = "";
                    try{
                        reader.mark(10000); // 设置足够大的预读缓冲区
                        nextLine = reader.readLine();
                        reader.reset(); //
//                        System.out.println("nextline:"+nextLine);
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
//                    System.out.println(line);
//                    System.out.println(nextLine);
                    //instructions start
                    int daviationIndex = line.indexOf(".so  ");
                    String deviation =line.substring(daviationIndex+".so  ".length(),daviationIndex+".so  ".length()+8);
                    map.put("deviation",deviation);
//                    System.out.println(deviation);

                    int instructionsIndex = line.indexOf(": \"");
                    String startInstructions = line.substring(instructionsIndex+3,line.length());
                    String instructions =startInstructions.substring(0,startInstructions.indexOf("\""));
                    map.put("instructions",instructions);
//                    System.out.println(instructions);

                    int startArgInex = line.indexOf(instructions)+instructions.length()+2;
                    if (startArgInex !=line.length() && startArgInex<line.length()) {
                        String argval = line.substring(startArgInex,line.length());
                        map.put("argsVal",argval);
//                        System.out.println(argval);
                        if (!nextLine.equals("")){
                            if (nextLine.indexOf("ERROR")!=-1&&nextLine.indexOf("  => ")!=-1){
                                String resultvale = nextLine.substring(nextLine.indexOf("  => ")+5,nextLine.length());
//                                System.out.println("resultvale "+resultvale);
                                map.put("resultVal",resultvale);
                            }
                        }
                    }else {
                        map.put("argsVal","");
                        map.put("resultVal","");
                    }
                    //instructions end
                    superMap = map;
                    if (map.get("argsVal")!=null&&map.get("resultVal")!=null&&!map.get("argsVal").equals("")&&!map.get("resultVal").equals("")) {
                        algorithmType type = AlgorithmAutoEmitterUtility.AlgorithmRecognition(map.get("argsVal"),map);
                        if (type!=algorithmType.NOAlgorithm){
                            SaveLog.storeContentByLine(type.toString() + " :  " + line, publicEntity.path+"/trace_algorithm.log");
                        }
                    }
                }
                else if(line.indexOf(" Memory READ at ")!=-1){
                    //aes找sbox盒
                    AlgorithmAutoEmitterUtility.isAes(line,superMap);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }





    @Test
    public void test(){
        String line = "[02:09:31 873][libAPSE_8.0.0.so  0x226874] [4901094a] 0x40226874: \"eor w9, w10, w9\" w10=0x56 w9=0xd6";
        line ="[02:09:31 874] ERROR [com.github.unidbg.arm.AbstractARM64Emulator] (AbstractARM64Emulator:248) -  => sp=0xbfffdc80";
//        line ="[02:09:31 874][libAPSE_8.0.0.so  0x226d40] [ff430491] 0x40226d40: \"add sp, sp, #0x110\" sp=0xbfffdb70";
//        line = "[02:09:31 874][libAPSE_8.0.0.so  0x2255ac] [d602084a] 0x402255ac: \"eor w22, w22, w8\" w22=0x1a w8=0x43";
        if (line.indexOf("["+publicEntity.libName+"  0x")!=-1) {
//            SaveLog.storeContentByLine(line,publicEntity.path+"/trace_algorithm.log");
            String [] split_INSTRUCTION = line.toString().split(" ");
            Map<String,String> map = new HashMap<>();

            int daviationIndex = line.indexOf(".so  ");
            String deviation =line.substring(daviationIndex+".so  ".length(),daviationIndex+".so  ".length()+8);
            map.put("deviation",deviation);
            System.out.println(deviation);


            int instructionsIndex = line.indexOf(": \"");
            String startInstructions =line.substring(instructionsIndex+3,line.length());
            String instructions =startInstructions.substring(0,startInstructions.indexOf("\""));
            map.put("instructions",instructions);
            System.out.println(instructions);

            int startArgInex = line.indexOf(instructions)+instructions.length()+2;
            String argval =line.substring(startArgInex,line.length());
            map.put("argval",argval);
            System.out.println(argval);
            for (String s : split_INSTRUCTION) {
                String res = s.strip();
                if (!res.equals("")){
                    if (res.startsWith("["+publicEntity.libName+"  0x")){
                        map.put("deviation",res);
                    }else if (res.startsWith("r[")){
                        map.put("argsVal", StringUtil.updateargs(res));
                    }else if (res.startsWith("w[")){
                        map.put("resultVal",StringUtil.updateargs(res));
                    }else {
                        map.put("instructions",res);
                    }
                }
            }
            if (map.get("argsVal")!=null&&map.get("resultVal")!=null&&map.get("argsVal")!=""&&map.get("resultVal")!="") {
                algorithmType type = AlgorithmAutoEmitterUtility.AlgorithmRecognition(map.get("argsVal"),map);
                if (type!=algorithmType.NOAlgorithm){
                    System.out.println(type.toString());
                }
            }
        }
    }



    public static void main(String[] args) {
        readFileByLine(publicEntity.path+"/apse_8.0.0.log");
    }

}
