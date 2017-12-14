package com.hyxt.utils;

import cn.com.cnpc.vms.common.protocols.common.CommonMessage;
import cn.com.cnpc.vms.common.util.BytesUtil;
import cn.com.cnpc.vms.protocols.tcp.T809.T809_MessageHeader;
import cn.com.cnpc.vms.protocols.tcp.T809.T809_ProtocolAnalysis;
import cn.com.cnpc.vms.protocols.tcp.T809.messageBody.T809_0x9001;
import com.hyxt.DTO.protocol.ProtocolUtil;

/**
 * @author songm
 * @version v1.0
 * @Description 固定格式的809消息
 * @Date: Create in 10:30 2017/12/13
 * @Modifide By:
 **/
public class MessageConstant {
    public static int acceptCode = 10000009;

    public static T809_ProtocolAnalysis analysis = new T809_ProtocolAnalysis();

    /**
     * 关闭主链路应答消息
     * @return
     */
    public static byte[] mainLinkResponseClose(){
        byte[] data = U809.es(U809.changeCode(BytesUtil.toStringHex("5b0000001a00003482100400e508e900000000000004d2aa655d"), acceptCode));
        return data;
    }

    /**
     * 主链路登录应答消息
     * @return
     */
    public static byte[] mainLinkResponseLogin(){
        byte[] data = U809.es(U809.changeCode(BytesUtil.toStringHex("5b0000001f0000000010020000c4e1010000000000000000000004d2c8705d"), acceptCode));
        return data;
    }

    /**
     * 主链路心跳应答消息
     * @return
     */
    public static byte[] mainLinkResponseBeat(){
        byte[] data = U809.es(U809.changeCode(BytesUtil.toStringHex("5b0000001a00003482100600e508e900000000000004d220a35d"), acceptCode));
        return data;
    }
    /**
     * 从链路请求登录消息
     * @return
     */
    public static byte[] subLinkRequestLogin(){
        T809_MessageHeader header = new T809_MessageHeader();
        T809_0x9001 body9001 = new T809_0x9001();
        header.setMessageID(0x9001);
        header.setGnsscentrid(acceptCode);
        body9001.setMessageID(0x9001);
        CommonMessage<T809_0x9001> message = new CommonMessage<T809_0x9001>(0x9001, header, body9001);
        header.setVersonFlag(new byte[] { 1, 0, 0 });
        // pack 0x9001 message
        byte[] data = analysis.pack(message);
        return data;
    }
    /**
     * 从链路心跳消息
     * @return
     */
    public static byte[] subLinkRequestBeat(){
        byte[] data = U809.es(U809.changeCode(BytesUtil.toStringHex("5b0000001a0000463c900500e508e900000000000004d267855d"), acceptCode));
        return data;
    }
    /**
     * 转发链路心跳消息
     * @return
     */
    public static byte[] transferLinkHeartBeat(){
        byte[] data = ProtocolUtil.getMessageByBody((byte) 0x01, null);
        return data;
    }
}
