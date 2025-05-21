package test.util;

import org.bouncycastle.util.encoders.Hex;

import java.util.zip.CRC32;

public class CustomCRC32 {
    private static final int[] TABLE = new int[256];

    static {
        // 初始化CRC表（与C++版本一致）
        for (int i = 0; i < 256; i++) {
            int crc = i;
            for (int j = 0; j < 8; j++) {
                crc = ((crc & 1) != 0) ? (crc >>> 1) ^ 0xEDB88320 : crc >>> 1;
            }
            TABLE[i] = crc;
        }
    }

    public static long calculate(byte[] data) {
        int crc = 0xFFFFFFFF;
        for (byte b : data) {
            int idx = (crc ^ (b & 0xFF)) & 0xFF;
            crc = (crc >>> 8) ^ TABLE[idx];
        }

        return (crc ^ 0xFFFFFFFF) & 0xFFFFFFFFL;
    }

//    public static void main(String[] args) {
//        byte[] b1 = {0x24,0x01,0x00,0x00,0x00,0x01,0x00,(byte)0x8b,0x4a,0x7b,0x7d,0x77,0x00,0x49,0x2d,0x39,(byte)0x87,0x6d,(byte)0xea,0x29,0x23,0x77,0x74,0x13,0x69,0x1a,0x5b,(byte)0x93,0x04,0x56,0x6c,0x1e,0x20,0x77,0x33,(byte)0xfb,(byte)0xb0,0x71,(byte)0xd3,(byte)0xc8,0x6c,0x2a,0x52,0x46,(byte)0xfe,0x6c,0x70,(byte)0x8b,0x4e,(byte)0xc5,(byte)0xac,(byte)0xcd,(byte)0x87,(byte)0xf8,(byte)0xb7,0x43,0x7b,0x19,0x30,0x6e,0x0c,(byte)0xe1,(byte)0xed,(byte)0xb4,0x20,(byte)0xea,0x19,0x6f,(byte)0xda,(byte)0x92,0x22};
//        System.out.println(b1.length);
//        byte[] b = {0x24,0x1f,0x00,0x00,(byte) 0xf1,0x01,0x00,(byte)0x8b,0x4a,0x7b,0x7d,0x77,0x00,0x49,0x2d,0x39,(byte)0x87,0x6d,(byte)0xea,0x29,0x23,0x77,0x74,0x4e,(byte)0xcd,0x61,(byte)0xe7,0x05,(byte)0xe6,0x15,0x35,0x43,(byte)0x84,0x0c,(byte)0x8f,0x6d,0x52,0x32,(byte)0x8a,0x78,(byte)0xaa,(byte)0x9c,(byte)0xf5,(byte)0xf9,(byte)0xbc,(byte)0xaa,0x6f,0x04,(byte)0xf3,(byte)0xe0,(byte)0xd4,(byte)0x83,(byte)0xeb,(byte)0xf1,(byte)0xf3,0x56,(byte)0xef,0x0f,0x5e,(byte)0xb3,(byte)0x93,0x6d,0x3a,0x5b,(byte)0xa2,(byte)0x8d,(byte)0xfb,(byte)0xc7,(byte)0xd5,0x5f,0x5b};
//        long result = calculate(b);
//        System.out.println("手动实现结果: 0x" + Long.toHexString(result));
//        //第三方
//        CRC32 crc32 = new CRC32();
//        crc32.update(b);
//        System.out.println("第三方实现结果"+Long.toHexString(crc32.getValue()));
//    }


    public static void main(String[] args) {
        long signTime = 0x6774337338e28L; //288e333743770600转换后
        long genSm4IvKey = 0xF100001FL;//1f0000f1 转换后
        byte[] sm4Iv = Hex.decode(genSm4Iv(signTime + genSm4IvKey));
        System.out.println("sm4Iv:" + Hex.toHexString(sm4Iv));
    }




    public static String genSm4Iv(long input) {
        long result;
        long shift1 = input << 7;
        long shift2 = input << 0x1a;
        long shift3 = input >>> 0x26;
        long combined1 = shift2 | shift3;

        result = combined1 ^ shift1;

        long shift4 = input << 0x2b;
        long shift5 = input >>> 0x15;
        long combined2 = shift4 | shift5;
        result ^= combined2;

        long shift6 = input << 0x11;
        long shift7 = input >>> 0x2f;
        long combined3 = shift6 | shift7;
        result ^= combined3;

        long shift8 = input << 0x26;
        long shift9 = input >>> 0x1a;
        long combined4 = shift8 | shift9;
        result ^= combined4;

        long shift10 = input << 0x3b;
        long shift11 = input >>> 0x5;
        long combined5 = shift10 | shift11;
        result ^= combined5;

        result ^= input;
        long iv1 = bswap64(result ^ 0x60913771c57d1561L);
        long iv2 = bswap64(result ^ 0xdfb68319ad105c6cL);
        return String.format("%016x%016x", iv1, iv2);
    }
    private static long bswap64(long x) {
        return ((x & 0xff00000000000000L) >>> 56) |
                ((x & 0x00ff000000000000L) >>> 40) |
                ((x & 0x0000ff0000000000L) >>> 24) |
                ((x & 0x000000ff00000000L) >>> 8) |
                ((x & 0x00000000ff000000L) << 8) |
                ((x & 0x0000000000ff0000L) << 24) |
                ((x & 0x000000000000ff00L) << 40) |
                ((x & 0x00000000000000ffL) << 56);
    }
}