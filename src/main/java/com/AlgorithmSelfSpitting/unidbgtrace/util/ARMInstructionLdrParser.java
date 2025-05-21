package com.AlgorithmSelfSpitting.unidbgtrace.util;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ARMInstructionLdrParser {

    public static void main(String[] args) {
        // 示例输入指令和寄存器值
        String[][] inputs = {
                {"ldr d8, [sp], #0x70","sp=0xbffff550","sp=0xbffff5c0"},
                {"ldrh w8, [x26, #0x4a]","x26=0x404fe7a0","w8=0x9a5f"},
                {"ldrsw x8, [x9, x8, lsl #2]","x9=0x4041facc,x8=0x1","x8=0xffffffffffca0590"},
                {"ldur w9, [x28, #-4]", "x28=0x411b8330", "w9=0xd0"},
                {"ldur w9, [x28, #-4] ", "x28=0x411b8330", "w9=0xd0"},
                {"ldr x2, [x2, #0x138]", "x2=0x404ed000", "x2=0x4054c500"},

        };

        for (String[] input : inputs) {
            JSONObject result = ARMInstructionLdrParser.parseLdrInstruction(input[0], input[1], input[2]);
            if (result != null) {
                System.out.println(result.toString(4)); // 使用 toString(int) 格式化输出
            } else {
                System.out.println("Failed to parse instruction: " + input[0] + " " + input[1] + " " + input[2]);
            }
        }
    }
    // 解析 LDR 指令
    public static JSONObject parseLdrInstruction(String instructionPart, String registerValuesBefore, String registerValuesAfter) {
        Map<String, String> registersBefore = buildRegisterMap(registerValuesBefore);
        Map<String, String> registersAfter = buildRegisterMap(registerValuesAfter);

        String[] parts = instructionPart.trim().split(" ", 2);
        if (parts.length < 2 ||
                !"ldr".equalsIgnoreCase(parts[0]) &&
                        !"ldrb".equalsIgnoreCase(parts[0]) &&
                        !"ldrsh".equalsIgnoreCase(parts[0]) &&
                        !"ldur".equalsIgnoreCase(parts[0])  &&
                        !"ldrsw".equalsIgnoreCase(parts[0])&&
                        !"ldrh".equalsIgnoreCase(parts[0])) {
            return new JSONObject()
                    .put("type", "Error")
                    .put("message", "Invalid instruction format");
        }

        // 解析操作数部分
        String operands = parts[1].trim();
        String destOperand = extractDestinationOperand(operands);
        String destinationRegister = extractDestinationRegister(instructionPart); // 提取目标寄存器

        AddressInfo addrInfo = parseAddress(destOperand, registersBefore);
        if (addrInfo == null) {
            return new JSONObject()
                    .put("type", "Error")
                    .put("message", "Failed to parse address");
        }

        // 获取目标寄存器的值
        String valueBefore = registersBefore.getOrDefault(destinationRegister, "0").toLowerCase().replaceAll("^0x", "");
        String valueAfter = registersAfter.getOrDefault(destinationRegister, "0").toLowerCase().replaceAll("^0x", "");

        long byteValueBefore = parseRegisterValue(valueBefore);
        long byteValueAfter = parseRegisterValue(valueAfter);


        // 返回结果 JSON
        return new JSONObject()
                .put("readType", "READ")
                .put("type", "MemoryReadWrite")
                .put("addr", addrInfo.getAddressString())
                .put("value", String.format("0x%x", byteValueAfter));
    }

    private static Map<String, String> buildRegisterMap(String registerValues) {
        Map<String, String> registers = new HashMap<>();
        if (registerValues != null && !registerValues.trim().isEmpty()) {
            for (String regValue : registerValues.split(",")) {
                String[] parts = regValue.split("=");
                if (parts.length == 2) {
                    if ("fp".equalsIgnoreCase(parts[0].trim())) {
                        registers.put("x29", parts[1].trim());
                    } else if ("lr".equalsIgnoreCase(parts[0].trim())) {
                        registers.put("x30", parts[1].trim());
                    } else {
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

    private static String extractDestinationRegister(String instructionPart) {
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
                    } else if (regNames.length == 3 && (regNames[2].startsWith("lsl #") || regNames[2].startsWith("lsr #") || regNames[2].equals("sxtw") || regNames[2].startsWith("uxtw #"))) {
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

    private static long parseShiftedRegister(String shiftStr, Map<String, String> registers) {
        String[] parts = shiftStr.split(", ");
        if (parts.length != 2 || !(parts[1].startsWith("lsl #") || parts[1].startsWith("lsr #") || parts[1].equals("sxtw") || parts[1].startsWith("uxtw #"))) {
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
        } else if (parts[1].startsWith("uxtw #")) { // 处理 uxtw #n
            int shiftAmount = Integer.parseInt(parts[1].replaceFirst("uxtw #", ""));
            if (shiftAmount < 0 || shiftAmount >= 64) {
                throw new IllegalArgumentException("Invalid shift amount for uxtw: " + shiftAmount);
            }
            return (regValue & ((1L << 32) - 1)) << shiftAmount; // 确保值在32位范围内并按指定数量左移
        }
        throw new IllegalStateException("Unexpected state in parseShiftedRegister");
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