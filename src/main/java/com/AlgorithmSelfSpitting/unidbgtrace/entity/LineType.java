package com.AlgorithmSelfSpitting.unidbgtrace.entity;

/**
 * 行类型
 */
public enum LineType {
        MEMORY_ACCESS,//寄存器存取 READ/WRITE
        INSTRUCTION, //指令操作
        INSTRUCTION_RESULT, //指令结果
        EMPTY_INSTRUCTION_RESULT,//空的指令结果
        UNKNOWN, //未知指令
        }
