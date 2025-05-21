package com.AlgorithmSelfSpitting.unidbgtrace.util;

import com.AlgorithmSelfSpitting.unidbgtrace.TraceParser;
import com.AlgorithmSelfSpitting.unidbgtrace.entity.Instructions;
import com.AlgorithmSelfSpitting.unidbgtrace.entity.LineType;
import com.AlgorithmSelfSpitting.unidbgtrace.entity.MemoryAccess;
import com.AlgorithmSelfSpitting.unidbgtrace.entity.Program;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import com.AlgorithmSelfSpitting.unidbgtrace.entity.algorithmType;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineTruncation {
    private static String superAddr;

    /**
     * 获取SO
     * @param logEntry
     * @return 如果为空就是有异常，如果不为空就是正常返回
     */
    private static String  getSo(String logEntry){
        // 使用正则表达式提取
        Pattern pattern = Pattern.compile("\\[([^\\]]+\\.so)");
        Matcher matcher = pattern.matcher(logEntry);
        if (matcher.find()) {
            String libraryName = matcher.group(1);
            System.out.println(libraryName);
            return libraryName;
        }else{
            return "";
        }
    }



    /**
     * 判断每行类型
     * @param line
     * @return
     */
    public static LineType getLineType(String line) {
//        System.out.printf("Line  %s%n", line.trim());
        String pattern = "\\[\\d{2}:\\d{2}:\\d{2} \\d{3}\\]\\[.*?\\.so\\s+0x[\\da-fA-F]+\\] \\[[\\da-fA-F]+\\] 0x[\\da-fA-F]+: \".*?\".*";
        String patternVal = "\\[.*?\\] \\w+ \\[.*?\\] \\([^:]+:[^:]+\\) -  => .*";
        String patternNoVal ="\\[.*?\\] \\w+ \\[.*?\\] \\([^:]+:[^:]+\\) - ";

        if (line.contains("Memory READ at ") || line.contains("Memory WRITE at ")) {
            return LineType.MEMORY_ACCESS;
        } else if (line.matches(pattern)) {
            return LineType.INSTRUCTION;
        } else if (line.matches(patternVal)) {
            return LineType.INSTRUCTION_RESULT;
        } else if (line.matches(patternNoVal)) {
            return LineType.EMPTY_INSTRUCTION_RESULT;
        }
        return LineType.UNKNOWN;
    }


    /**
     * 当类型为MEMORY_ACCESS时转化为实体类
     * @param line
     * @return
     */
    public static MemoryAccess genmomoryAccess(String line){
        MemoryAccess memoryAccess = new MemoryAccess();
        Pattern pattern = Pattern.compile("(0x[a-fA-F0-9]+).*?(0x[a-fA-F0-9]+)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {

            String deviation = matcher.group(1);
            String value = matcher.group(2);
            String type = line.contains("Memory WRITE at ") ? "WRITE" : "READ";
//            memoryAccess.setSo(getSo(line));
            memoryAccess.setReadType(type);
            memoryAccess.setAddr(deviation);
            memoryAccess.setValue(value);
        }
        return memoryAccess;

    }


    /**
     * 当类型为Instructions时转化为实体类
     * @param line
     * @return
     */
    public static Instructions genInstructions(String line) {
        Instructions instructions = new Instructions();
        // 调整后的正则表达式：处理库名称后的空格
        Pattern pattern = Pattern.compile("\\[(\\S+\\.so)\\s+0x([0-9a-fA-F]+)\\].*?\"(.*?)\"");
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            String libraryName = matcher.group(1).trim(); // 提取库名称并去除多余空格
            String deviation = matcher.group(2);          // 提取偏移地址
            String instruction = matcher.group(3);        // 提取指令

            instructions.setSo(libraryName);
            instructions.setDeviation("0x"+deviation);
            instructions.setInstructions(instruction);


            // 使用正则表达式提取所有寄存器值
            Pattern regPattern = Pattern.compile("(\\w+)=0x[0-9a-fA-F]+");
            Matcher regMatcher = regPattern.matcher(line);
            List<String> argsValList = new ArrayList<>();

            while (regMatcher.find()) {
                argsValList.add(regMatcher.group());
            }

            //  将寄存器值列表连接成一个字符串，用逗号分隔
            String argsVal = String.join(",", argsValList);

            if (!argsVal.isEmpty()) {
                instructions.setArgsval(argsVal);
                if(instruction.indexOf("str")!=-1){
                    //如果是str指令：
//                    System.out.println("-");
                    org.json.JSONObject result = ARMInstructionStrParser.parseInstruction(instruction, argsVal);
                    if (result != null) {
                        System.out.println(result);
                        TraceParser.sendLog(result.toString());

                    } else {
                        System.out.println("Failed to parse instruction: " + instruction + " " + argsVal);
                    }

                }

//                else if(instruction.indexOf("ldr")!=-1){
//                    //如果是str指令：
//                    org.json.JSONObject result = ARMInstructionLdrParser.parseLdrInstruction(instruction, argsVal, input[2]);
//                    if (result != null) {
//                        System.out.println(result.toString(4)); // 使用 toString(int) 格式化输出
//                    } else {
//                        System.out.println("Failed to parse instruction: " + instruction + " " + argsVal + " " + input[2]);
//                    }
//                }

            } else {
                instructions.setArgsval("N/A");
            }


            return instructions;
        } else {
            System.out.println("No match found.");
        }
        return null;
    }



    /**
     * 指令结果
     * @param line
     * @return
     */
    public static String genResultVal(String line){
        // 使用正则表达式匹配寄存器值的格式
        Pattern regPattern = Pattern.compile("(\\w+)=0x[0-9a-fA-F]+");
        Matcher regMatcher = regPattern.matcher(line);
        if (regMatcher.find()) {
            return regMatcher.group(); // 返回第一个匹配的寄存器值
        } else {
            return "No register value found.";
        }

    }

    /**
     * hex转int类型
     * @param str
     * @return
     */
    public static int Hex2int(String str){
        String offsetStr = str.trim().replaceFirst("^0x", "");

        int decimalValue = Integer.parseInt(offsetStr, 16);
        return decimalValue;
    }

    /**
     * 判断两次偏移差距是否是10以内如果为10以内可以认为他是一个新的
     * @param updeviation
     * @param deviation
     * @return
     */
    public static boolean isNewFun(String updeviation ,String deviation){
        int updev = Hex2int(updeviation);
        int dev = Hex2int(deviation);
        boolean result = false;
        if(updev>dev){
            result = updev - dev <10? true :false;
        }else {
            result = dev - updev <10? true :false;
        }
        return result;
    }

    /**
     * 判断两次so是否相等
     * @param upSo
     * @param so
     * @return
     */
    public static boolean isEqSo(String upSo,String so){
        return upSo.equals(so);
    }

    /**
     * 判断上一次so是否是跳转
     * @param upInstructions
     * @return
     */
    public static boolean isJump(String upInstructions){
        return getInstructionsType(upInstructions);
    }

    /**
     * 判断是否为跳转
     * @param line
     * @return
     */
    public static boolean getInstructionsType(String line) {
        if (!line.isEmpty() && line.charAt(0) == 'b') {
            return true;
        }
        return false;
    }

    /**
     * TODO:存储resultValue，删除上一行日志，需要对比一下是否和要输出的内容除resultVal外一致
     */
    public static void DelUpLine(String superLineJson,String lineJson){

        JSONObject json = JSON.parseObject(lineJson);
        String type = json.getString("type");
        if(type.equals("instructions")){
            JSONObject superJson = JSON.parseObject(superLineJson);
            if (superJson==null){
                superAddr =  json.getString("deviation");
                return;
            }
            String superSo = superJson.getString("so");
            String so = json.getString("so");

            String EndDeviation = superJson.getString("deviation");
            String JumpDeviation = json.getString("deviation");
            String superInstructions = superJson.getString("instructions");
            algorithmType at = algorithmType.NOAlgorithm;
            String result = "";
            try{
                String argsval = json.getString("argsVal");
                algorithmType aType = AlgorithmAutoEmitterUtility.AlgorithmRecognition(argsval,lineJson);
                if (aType != algorithmType.AlgorithmUNKNOWN && aType!= algorithmType.NOAlgorithm){
                    at = aType;
                }
                String resultVal = json.getString("resultVal");

                //
                algorithmType aType1 = AlgorithmAutoEmitterUtility.AlgorithmRecognition(resultVal,lineJson);
                if (aType1 != algorithmType.AlgorithmUNKNOWN && aType1!= algorithmType.NOAlgorithm){
                    at = aType1;
                }
            }catch (Exception e){

            }finally {
               result =  AlgorithmOperation(at);
            }
            //判断上一次是否跳转
            boolean isjump = isJump(superInstructions);
            //有跳转之后开始进入判断
            if(isjump){
                //是否是同一个so
                boolean iseqso = isEqSo(superSo,so);
                //判断两次的偏移差是否超过10 没超过默认它为同一个函数
                boolean isNewFun = isNewFun(EndDeviation,JumpDeviation);
                if(!iseqso||!isNewFun){
//                    System.out.println("新的函数");
                    Program program = new Program();
                    program.setBelongToSo(superSo);
                    program.setStartAddr(superAddr);
                    program.setEndAddr(EndDeviation);
                    program.setTargetSo(so);
                    program.setJumpAddr(JumpDeviation);//下一个开始
                    if(!result.equals("")){
                        program.setAlgorithm(result);
                    }
                    superAddr = JumpDeviation;
                    //todo：这个输出单独拎出来
                    storeContentByLine(JSON.toJSONString(program),"/yourpath/trace.log");
                }

            }
        }

    }

    /**
     * 算法相关操作
     * @param at
     * @return
     */
    public static String  AlgorithmOperation(algorithmType at){
        String result = "";
        if (at != algorithmType.AlgorithmUNKNOWN && at!= algorithmType.NOAlgorithm){
            System.out.println("有算法");
            switch (at) {
                case AlgorithmSHA1 -> {
                    result = "处理SHA1算法相关逻辑后得到的值";
                    // 针对SHA1算法的具体操作可在此添加
                    result = "SHA1";
                    break;
                }
                case AlgorithmMD5 -> {
                    result = "处理MD5算法相关逻辑后得到的值";
                    // 这里可以添加更多针对MD5算法的具体操作，比如调用相关方法进行计算等
                    // 示例中假设经过一些操作后得到一个要返回的字符串值
                    result = "MD5";
                    break;
                }
                case AlgorithmCRC32 -> {
                    result = "处理CRC32算法相关逻辑后得到的值";
                    // 同样可添加针对CRC32算法的具体操作
                    result = "CRC32";
                    break;
                }
                case AlgorithmSM4 -> {
                    result = "处理SM4算法相关逻辑后得到的值";
                    // 针对SM4算法的具体操作可在此添加
                    result = "SM4";
                    break;
                }
                case AlgorithmAES -> {
                    result = "处理AES算法相关逻辑后得到的值";
                    // 针对AES算法的具体操作可在此添加
                    break;
                }

            }
            if (!result.equals("")){
                System.out.println(result);
            }
        }
        return result;
    }

    /**
     * 按行存储传入的一行内容到指定文件，多次调用可实现日志追加
     *
     * @param lineContent 要存储的一行内容
     * @param filePath 文件路径
     */
    public static void storeContentByLine(String lineContent, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(lineContent);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    void Test(){
        String str ="b.hi #0x40650c20";
        getInstructionsType(str);
    }

}
