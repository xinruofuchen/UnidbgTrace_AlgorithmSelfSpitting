package com.AlgorithmSelfSpitting.unidbgtrace;

import com.AlgorithmSelfSpitting.unidbgtrace.entity.Instructions;
import com.AlgorithmSelfSpitting.unidbgtrace.entity.LineType;
import com.AlgorithmSelfSpitting.unidbgtrace.entity.MemoryAccess;
import com.AlgorithmSelfSpitting.unidbgtrace.util.ARMInstructionLdrParser;
import com.AlgorithmSelfSpitting.unidbgtrace.util.LineTruncation;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class TraceParser {

    private static final Log showlog = LogFactory.getLog(TraceParser.class);
    private static boolean isShowLog = true;
    private static String superLine = "";
    private static LineType superLineType = LineType.UNKNOWN;

    /**
     * 读取行，根据行类型做操作
     * @param filePath
     */
    public static void readFileByLine(String filePath){
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            Instructions supperInstructions = null;
            String line;
            while ((line = reader.readLine()) != null) {
                LineType lineType = LineTruncation.getLineType(line);
                switch (lineType){
                    case INSTRUCTION:
                        Instructions instructions = LineTruncation.genInstructions(line);
                        String logInstructions= JSON.toJSONString(instructions);
                        supperInstructions = instructions;
                        setSuberLine(LineType.INSTRUCTION,logInstructions);
                        break;
                    case MEMORY_ACCESS:
                        MemoryAccess memoryAccess = LineTruncation.genmomoryAccess(line);
                        String logMemoryAccess = JSON.toJSONString(memoryAccess);
                        setSuberLine(LineType.MEMORY_ACCESS,logMemoryAccess);
                        break;
                    case INSTRUCTION_RESULT:
                        String result = LineTruncation.genResultVal(line);
                        supperInstructions.setResultval(result);
                        logInstructions =  JSON.toJSONString(supperInstructions);
                        setSuberLine(LineType.INSTRUCTION_RESULT,logInstructions);
                        break;
                    case EMPTY_INSTRUCTION_RESULT:
                        setSuberLine(LineType.EMPTY_INSTRUCTION_RESULT,"");
                        break;
                    case UNKNOWN:
                        System.out.println("");
                        break;
                }
            } } catch (IOException e) {
            e.printStackTrace();
        }

    }



    /**
     *判断是否需要输出日志
     * @param line
     */
    public static void setSuberLine(LineType lineType,String line){
            if(lineType==LineType.INSTRUCTION){
                LineTruncation.DelUpLine(superLine,line);
                superLine = line;
            }else{
                if(lineType == LineType.INSTRUCTION_RESULT){
                    sendLog(line);
                    //在这里输出然后解析ldr
                 if(line.indexOf("ldr")!=-1){
                     JSONObject json = JSON.parseObject(line);
                     String instruction =  json.getString("instructions");
                     String argsVal = json.getString("argsVal");
                     String resultVal = json.getString("resultVal");
                    org.json.JSONObject result = ARMInstructionLdrParser.parseLdrInstruction(instruction, argsVal, resultVal);
                    if (result != null) {
                        String resultJson = result.toString();
//                        System.out.println(resultJson); // 使用 toString(int) 格式化输出
                        sendLog(resultJson);
                    } else {
                        System.out.println("Failed to parse instruction: " + instruction + " " + argsVal + " " + resultVal);
                    }
                }
                }else{
                    sendLog(superLine);
                    if(line.length()>0){
                        sendLog(line);
                    }
                }
            }
            superLineType = lineType;
    }


    /**
     *  输出日志 TODO：根据需求是否更改为log4j
     * @param log
     */
    public static void sendLog(String log){
        if (isShowLog){
//            System.out.println(log);
            showlog.debug(log);
        }
    }










    public static void main(String[] args) {
        readFileByLine("/yourpath/apse_8.0.0_pay_t.log");

    }



}

