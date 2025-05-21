package com.AlgorithmSelfSpitting.unidbgtrace.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

@Data
@JSONType(orders = {"belongToSo", "targetSo","startAddr", "endAddr","jumpAddr","algorithm"})
public class Program {
//    @JSONField(name = "start")
//    int start; //开始行数
//    @JSONField(name = "end")
//    int end; //结束行数
    @JSONField(name = "belongToSo")
    String belongToSo; //所属so
    @JSONField(name = "targetSo")
    String targetSo; //目标so（跳转后）
    @JSONField(name = "startAddr")
    String startAddr;//起始偏移 可以理解为跳转前偏移
    @JSONField(name = "endAddr")
    String endAddr;//结束偏移可以理解为跳转后偏移
    @JSONField(name = "jumpAddr")
    String jumpAddr;//跳转偏移可以理解为跳转后偏移
    @JSONField(name = "algorithm")
    String algorithm;//算法
}

// 有开始结束 属于 so todo 可能跳转地址 跳转指定 so