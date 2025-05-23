package com.AlgorithmSelfSpitting.Unidbgtrace.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

@Data
@JSONType(orders = {"belongToSo", "targetSo","startAddr", "endAddr","jumpAddr","algorithm"})
public class Program {

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