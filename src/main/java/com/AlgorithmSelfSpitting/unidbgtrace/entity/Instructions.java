package com.AlgorithmSelfSpitting.unidbgtrace.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

@Data
@JSONType(orders = {"so", "deviation", "type", "instructions", "argsVal", "resultVal"})
public class Instructions {
    //所属so
    @JSONField(name = "so")
    String so;
    //偏移
    @JSONField(name = "deviation")
    String deviation;
    //类型(固定为instructions)
    @JSONField(name = "type")
    String type ="instructions";
    //instructions
    @JSONField(name = "instructions")
    String instructions;
    //参数内容
    @JSONField(name = "argsVal")
    String argsval;
    //结果
    @JSONField(name = "resultVal")
    String resultval;
}
