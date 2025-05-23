package com.AlgorithmSelfSpitting.QBDItrace;


import com.AlgorithmSelfSpitting.QBDItrace.entity.algorithmType;
import com.AlgorithmSelfSpitting.QBDItrace.util.AlgorithmAutoEmitterUtility;
import com.AlgorithmSelfSpitting.QBDItrace.util.SaveLog;
import com.AlgorithmSelfSpitting.QBDItrace.util.StringUtil;
import com.AlgorithmSelfSpitting.publicclass.publicEntity;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.AlgorithmSelfSpitting.QBDItrace.util.AlgorithmAutoEmitterUtility.moreSbox;


public class TraceParser_QBDITrace {


    /**
     * 读取行，根据行类型做操作
     * @param filePath
     */
    public static void readFileByLine(String filePath){
        Map<String,String> superMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("0x")) {
                   String [] split_INSTRUCTION = line.toString().split("  ");
                    Map<String,String> map = new HashMap<>();
                    for (String s : split_INSTRUCTION) {
                        String res = s.strip();
                        if (!res.equals("")){
                            if (res.startsWith("0x")){
                                map.put("deviation",res);
                            }else if (res.startsWith("_")){
//                                map.put("",res);
                            }else if (res.startsWith("r[")){
                                map.put("argsVal", StringUtil.updateargs(res));
                            }else if (res.startsWith("w[")){
                                map.put("resultVal",StringUtil.updateargs(res));
                            }else {
                                map.put("instructions",res);
                            }
//                            System.out.println(res);
                        }
                    }
                    superMap = map;
                    if (map.get("argsVal")!=null&&map.get("resultVal")!=null&&map.get("argsVal")!=""&&map.get("resultVal")!="") {
                        algorithmType type = AlgorithmAutoEmitterUtility.AlgorithmRecognition(map.get("argsVal"),map);
                        if (type!=algorithmType.NOAlgorithm){
//                            System.out.println(type.toString()+" : "+line);
                            SaveLog.storeContentByLine(type.toString() + " :  " + line, publicEntity.path+"/trace_algorithm.log");
                        }

                    }

//                    System.out.println("============");
                }
                else if(line.startsWith("memory read  at")){
                    //aes找sbox盒
                   AlgorithmAutoEmitterUtility.isAes(line,superMap);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        finally {
//            int index = AlgorithmAutoEmitterUtility.aesSboxMapList.size();
//            for (int i=0;i<index;i++){
//                if (AlgorithmAutoEmitterUtility.aesSboxMapList.get(i).getAesSboxindex()>=2){
//                    //随机搜索sbox内容 如果都符合说明实锤aes
//                    //TODO：展示位置只能在最后
//                    boolean b1 = moreSbox(i,205);
//                    boolean b2 = moreSbox(i,145);
//                    boolean b3 = moreSbox(i,123);
//                    boolean b4 = moreSbox(i,255);
//                    if(b1 && b2 && b3 && b4){
//                        //TODO:后续需要想办法获取偏移 目前只能拿到它的行号
//                    }
//                }
//
//            }
//        }
    }



//    public static void searchInDirectory(String dirPath, String keyword) {
//        Collection<File> files = FileUtils.listFiles(new File(dirPath), new String[]{"txt", "log"}, true);
//        files.forEach(file -> {
//            try {
//                if (FileUtils.readFileToString(file, "UTF-8").contains(keyword)) {
//                    System.out.println("Found in file: " + file.getAbsolutePath());
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//    }



@Test
public void test(){
        String line = "0x1fcfc8  _e001000b  \tadd\tw0, w15, w0       r[w0=98badcfe w15=67452301]   w[w0=ffffffff]";
        line = "0x1fcfc8  _e001000b  \tadd\tw0, w15, w0       r[w0=98badcfe w15=67452301]   w[w0=ffffffff]";
        line = "0x224df4  _a84359b8  \tldur\tw8, [x29, #-108]   r[fp=6fcae6b1b0]   w[w8=4f519485]";
    if (line.startsWith("0x")) {
        SaveLog.storeContentByLine(line,publicEntity.path+"/trace_algorithm.log");
        String [] split_INSTRUCTION = line.toString().split("  ");
        Map<String,String> map = new HashMap<>();
        for (String s : split_INSTRUCTION) {
            String res = s.strip();
            if (!res.equals("")){
                if (res.startsWith("0x")){
                    map.put("deviation",res);
                }else if (res.startsWith("_")){
//                                map.put("",res);
                }else if (res.startsWith("r[")){
                    map.put("argsVal", StringUtil.updateargs(res));
                }else if (res.startsWith("w[")){
                    map.put("resultVal",StringUtil.updateargs(res));
                }else {
                    map.put("instructions",res);
                }
//                            System.out.println(res);
            }
        }
        if (map.get("argsVal")!=null&&map.get("resultVal")!=null&&map.get("argsVal")!=""&&map.get("resultVal")!="") {
            algorithmType type = AlgorithmAutoEmitterUtility.AlgorithmRecognition(map.get("argsVal"),map);
            if (type!=algorithmType.NOAlgorithm){
                System.out.println(type.toString());
            }

        }

//                    System.out.println("============");
    }
}



    public static void main(String[] args) {
        readFileByLine(publicEntity.path+"/apse_8.0.0.log");
    }



}

