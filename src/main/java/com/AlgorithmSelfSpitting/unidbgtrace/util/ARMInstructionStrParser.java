package com.AlgorithmSelfSpitting.unidbgtrace.util;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ARMInstructionStrParser {
    public static void main(String[] args) {
        // 示例输入指令和寄存器值
        //Unsupported address format with multiple registers: [x0, w4, uxtw #3]
        //str w8, [x10, w9, uxtw #2] ,w8=0xf440,x10=0x411b7d84,w9=0x3
        String[][] inputs = {
//                {"str x19, [x0, w4, uxtw #3]","x19=0x40909000,x0=0x40902f60,w4=0x9"},
                {"str w8, [x10, w9, uxtw #2]]","w8=0xf440,x10=0x411b7d84,w9=0x3"},
                {"strb w5, [x0, #-1]!" ,"w5=0x35,x0=0xbfffbfcc"},
                {"str wzr, [x3, #-4]!","x3=0xbfffe4d0"},
                {"strh w8, [x20, #6]","w8=0x0,x20=0x40922f70"},
                {"str x1, [x29, #0x78]","x1=0x0,fp=0xbfffe490"},
                {"strb w15, [x2, w18, sxtw]","w15=0x62,x2=0x40a3bd60,w18=0x0"},
                {"strb w9, [x8, w10, uxtw]","w9=0xc9,x8=0xbfffdf28,w10=0x0"},
                {" str x8, [x9, x22, lsl #3]", "x8=0x3,x9=0xbffff190,x22=0x2"},
                {"str x0, [sp]", "x0=0xbfffef30,sp=0xbfffe790"},
                {"str x23, [sp, #-0x40]!", "x23=0x643b1d11,sp=0xbffff600"},
                {"str x0, [x20, #0x10]", "x0=0x40a30280,x20=0x40a3c810"}
        };

        for (String[] input : inputs) {
            JSONObject result = parseInstruction(input[0], input[1]);
            if (result != null) {
                System.out.println(result.toString(4)); // 使用 toString(int) 格式化输出
            } else {
                System.out.println("Failed to parse instruction: " + input[0] + " " + input[1]);
            }
        }
    }

    private static long parseOffset(String offsetStr) {
        boolean isNegative = offsetStr.startsWith("-");
        if (isNegative) {
            offsetStr = offsetStr.toLowerCase().replaceFirst("^-?0x", ""); // 移除 '-0x'
        } else {
            offsetStr = offsetStr.toLowerCase().replaceFirst("^0x", ""); // 移除 '0x'
        }
        long offset = Long.parseLong(offsetStr, 16);
        return isNegative ? -offset : offset;
    }

    public static JSONObject parseInstruction(String instructionPart, String registerValues) {
        Map<String, String> registers = buildRegisterMap(registerValues);

        String[] parts = instructionPart.trim().split(" ", 2);
        if (parts.length < 2 || !"str".equalsIgnoreCase(parts[0]) && !"strb".equalsIgnoreCase(parts[0]) && !"strh".equalsIgnoreCase(parts[0]) && !"strd".equalsIgnoreCase(parts[0])) {
            return new JSONObject()
                    .put("type", "Error")
                    .put("message", "Invalid instruction format");
        }

        // 解析操作数部分
        String operands = parts[1].trim();
        String destOperand = extractDestinationOperand(operands);
        String sourceRegister = extractSourceRegister(instructionPart);

        AddressInfo addrInfo = parseAddress(destOperand, registers);
        if (addrInfo == null) {
            return new JSONObject()
                    .put("type", "Error")
                    .put("message", "Failed to parse address");
        }

        // 提取源寄存器的值
        String value = registers.getOrDefault(sourceRegister, "0").toLowerCase().replaceAll("^0x", "");
        if ("strb".equalsIgnoreCase(parts[0])) {
            long byteValue = parseRegisterValue(value) & 0xFF;
            value = String.format("0x%02x", byteValue);
        } else {
            value = String.format("0x%x", parseRegisterValue(value));
        }
        // 返回结果 JSON
        return new JSONObject()
                .put("type", "NewMemoryReadWrite")
                .put("readType", "WRITE")
                .put("addr", addrInfo.getAddressString())
                .put("value", value);
    }

    private static Map<String, String> buildRegisterMap(String registerValues) {
        Map<String, String> registers = new HashMap<>();
        if (registerValues != null && !registerValues.trim().isEmpty()) {
            for (String regValue : registerValues.split(",")) {
                String[] parts = regValue.split("=");
                if (parts.length == 2) {
                    // fp:x29 lr:x30
                    if ("fp".equalsIgnoreCase(parts[0].trim())) {
                        registers.put("x29", parts[1].trim());
                    } else if ("lr".equalsIgnoreCase(parts[0].trim())) {
                        registers.put("x30", parts[1].trim());
                    }else {
                        registers.put(parts[0].trim(), parts[1].trim());
                    }
                }
            }
        }
        return registers;
    }

    private static String extractDestinationOperand(String operands) {
        int commaIndex = operands.indexOf(',');
        if (commaIndex != -1) {
            return operands.substring(commaIndex + 1).trim();
        }
        return operands;
    }

    private static String extractSourceRegister(String instructionPart) {
        String[] parts = instructionPart.split(",")[0].trim().split(" ");
        if (parts.length > 1) {
            return parts[1];
        }
        return "";
    }

    private static long parseRegisterValue(String regValueStr) {
        // 移除可能存在的 "0x" 或 "0X" 前缀，并转换为小写
        String trimmedValue = regValueStr.toLowerCase().replaceAll("^0x", "");
        return Long.parseUnsignedLong(trimmedValue, 16);
    }
    private static long parseShiftedRegister(String shiftStr, Map<String, String> registers) {
        String[] parts = shiftStr.split(", ");
        if (parts.length != 2 || !(parts[1].startsWith("lsl #") || parts[1].startsWith("lsr #") || parts[1].equals("sxtw") || parts[1].equals("uxtw")||parts[1].startsWith("uxtw #"))) {
            throw new IllegalArgumentException("Invalid shift or extend format: " + shiftStr);
        }
        String regName = parts[0].trim();
        String regValueStr = registers.get(regName);
        if (regValueStr == null) {
            throw new IllegalArgumentException("Register value not found for: " + regName);
        }

        long regValue = parseRegisterValue(regValueStr);

        if (parts[1].startsWith("lsl #")) {
            int shiftAmount = Integer.parseInt(parts[1].replaceFirst("lsl #", ""));
            return regValue << shiftAmount;
        } else if (parts[1].startsWith("lsr #")) {
            int shiftAmount = Integer.parseInt(parts[1].replaceFirst("lsr #", ""));
            return regValue >>> shiftAmount; // 使用无符号右移
        } else if (parts[1].equals("sxtw")) {
            if (regValue > 0xFFFFFFFFL) {
                throw new IllegalArgumentException("Cannot sign extend a value larger than 32 bits");
            }
            int signedValue = (int) regValue;
            return signedValue & 0xFFFFFFFFL;
        } else if (parts[1].equals("uxtw")) {
            if (regValue < 0 || regValue > 0xFFFFFFFFL) {
                throw new IllegalArgumentException("Cannot unsigned extend a value out of 32-bit range");
            }
            return regValue & 0xFFFFFFFFL;
        }else if (parts[1].startsWith("uxtw #")) { // 处理 uxtw #n
            int shiftAmount = Integer.parseInt(parts[1].replaceFirst("uxtw #", ""));
            if (shiftAmount < 0 || shiftAmount >= 64) {
                throw new IllegalArgumentException("Invalid shift amount for uxtw: " + shiftAmount);
            }
            // 先进行移位操作，再进行无符号扩展
            return (regValue & ((1L << 32) - 1)) << shiftAmount; // 确保值在32位范围内并按指定数量左移
        }
        throw new IllegalStateException("Unexpected state in parseShiftedRegister");
    }

    private static AddressInfo parseAddress(String destOperand, Map<String, String> registers) {
        try {
            boolean updateBase = destOperand.endsWith("!");
            destOperand = destOperand.replaceAll("!", "").trim();
            if (destOperand.startsWith("[x") || destOperand.startsWith("[w")) {
                String regName = destOperand.replace("[", "").replace("]", "").trim();
                if (regName.contains(", ")) {
                    String[] regNames = regName.split(", ");
                    if (regNames.length == 3 && (regNames[2].startsWith("lsl #") || regNames[2].startsWith("lsr #") || regNames[2].equals("uxtw"))) {
                        String baseRegValueStr = registers.get(regNames[0].trim());
                        long shiftedOrExtendedOffset = parseShiftedRegister(regNames[1] + ", " + regNames[2], registers);

                        if (baseRegValueStr == null) {
                            System.err.println("Register value not found for: " + regNames[0]);
                            return null;
                        }
                        long baseRegValue = parseRegisterValue(baseRegValueStr);
                        long finalAddr = baseRegValue + shiftedOrExtendedOffset;

                        if (updateBase) {
                            registers.put(regNames[0].trim(), Long.toHexString(finalAddr));
                        }
                        return new AddressInfo(finalAddr);
                    }
                }
            }
            if (destOperand.equals("[sp]")) {
                String spValueStr = registers.get("sp");
                if (spValueStr == null) {
                    System.err.println("SP value not found in metadata.");
                    return null;
                }
                long finalAddr = parseRegisterValue(spValueStr);
                if (updateBase) {
                    registers.put("sp", Long.toHexString(finalAddr));
                }
                return new AddressInfo(finalAddr);
            } else if (destOperand.startsWith("[sp, #")) {
                String[] spParts = destOperand.replace("[", "").replace("]", "").split(", #");
                if (spParts.length != 2) {
                    System.err.println("Invalid SP offset format: " + destOperand);
                    return null;
                }
                String spValueStr = registers.get("sp");
                if (spValueStr == null) {
                    System.err.println("SP value not found in metadata.");
                    return null;
                }
                long spValue = parseRegisterValue(spValueStr);
                String offsetStr = spParts[1].trim();
                long offset = parseOffset(offsetStr);

                long finalAddr = spValue + offset;
                if (updateBase) {
                    registers.put("sp", Long.toHexString(finalAddr));
                }
                return new AddressInfo(finalAddr);
            } else if (destOperand.startsWith("[x") || destOperand.startsWith("[w")) {
                String regName = destOperand.replace("[", "").replace("]", "").trim();
                if (regName.contains(", #")) {
                    String[] parts = regName.split(", #");
                    if (parts.length == 2) {
                        String baseRegName = parts[0].trim();
                        String baseRegValueStr = registers.get(baseRegName);
                        if (baseRegValueStr == null) {
                            System.err.println("Register value not found for: " + baseRegName);
                            return null;
                        }
                        long baseRegValue = parseRegisterValue(baseRegValueStr);
                        long offset = parseOffset(parts[1].trim());

                        long finalAddr = baseRegValue + offset;
                        if (updateBase) {
                            registers.put(baseRegName, Long.toHexString(finalAddr));
                        }
                        return new AddressInfo(finalAddr);
                    } else {
                        System.err.println("Unsupported address format with immediate offset: " + destOperand);
                        return null;
                    }
                } else if (regName.contains(", ")) {
                    String[] regNames = regName.split(", ");
                    if (regNames.length == 2) {
                        String baseRegValueStr = registers.get(regNames[0].trim());
                        String offsetRegValueStr = registers.get(regNames[1].trim());

                        if (baseRegValueStr == null || offsetRegValueStr == null) {
                            System.err.println("Register value not found for one of the registers: " + regName);
                            return null;
                        }
                        long baseRegValue = parseRegisterValue(baseRegValueStr);
                        long offsetRegValue = parseRegisterValue(offsetRegValueStr);

                        long finalAddr = baseRegValue + offsetRegValue;
                        if (updateBase) {
                            registers.put(regNames[0].trim(), Long.toHexString(finalAddr));
                        }
                        return new AddressInfo(finalAddr);
                    } else if (regNames.length == 3 && (regNames[2].startsWith("lsl #") || regNames[2].startsWith("lsr #") || regNames[2].equals("sxtw")|| regNames[2].startsWith("uxtw #"))) {
                        String baseRegValueStr = registers.get(regNames[0].trim());
                        long shiftedOrExtendedOffset = parseShiftedRegister(regNames[1] + ", " + regNames[2], registers);
                        if (baseRegValueStr == null) {
                            System.err.println("Register value not found for: " + regNames[0]);
                            return null;
                        }
                        long baseRegValue = parseRegisterValue(baseRegValueStr);
                        long finalAddr = baseRegValue + shiftedOrExtendedOffset;
                        if (updateBase) {
                            registers.put(regNames[0].trim(), Long.toHexString(finalAddr));
                        }
                        return new AddressInfo(finalAddr);
                    } else {
                        System.err.println("Unsupported address format with multiple registers: " + destOperand);
                        return null;
                    }
                } else {
                    String regValue = registers.get(regName);
                    if (regValue == null) {
                        System.err.println("Register value not found for: " + regName);
                        return null;
                    }
                    long finalAddr = parseRegisterValue(regValue);
                    return new AddressInfo(finalAddr);
                }
            } else {
                System.err.println("Unsupported address format: " + destOperand);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error parsing address: " + e.getMessage());
            return null;
        }
    }




    private static class AddressInfo {
        private final long address;
        public AddressInfo(long address) {
            this.address = address;
        }
        public String getAddressString() {
            return String.format("0x%x", address);
        }
    }
}