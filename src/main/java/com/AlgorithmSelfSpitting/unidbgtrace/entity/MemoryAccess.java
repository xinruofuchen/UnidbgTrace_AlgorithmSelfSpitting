package com.AlgorithmSelfSpitting.unidbgtrace.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

@Data
@JSONType(orders = {"type","readType", "addr", "value"})
public class MemoryAccess {

//    @JSONField(name = "so")
//    String so = "libc.so";
    //类型（read/write）

    @JSONField(name = "type")
    String type = "MemoryReadWrite";
    //读取类型
    @JSONField(name = "readType")
    String readType;
    //存取地址
    @JSONField(name = "addr")
    String addr;
    //存取内容
    @JSONField(name = "value")
    String value;
}
