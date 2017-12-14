package com.hyxt.DTO.handler;


import cn.com.cnpc.vms.common.cache.LocalCacheManager;
import cn.com.cnpc.vms.common.exception.ProtocolEscapeExeption;
import cn.com.cnpc.vms.common.protocols.common.CommonMessage;
import cn.com.cnpc.vms.common.util.BytesUtil;
import cn.com.cnpc.vms.protocols.tcp.T809.T809_MessageHeader;
import cn.com.cnpc.vms.protocols.tcp.T809.T809_ProtocolAnalysis;
import cn.com.cnpc.vms.protocols.tcp.T809.messageBody.T809_0x9001;
import cn.com.cnpc.vms.protocols.tcp.T809.util.T809_Util;
import com.hyxt.DO.pojo.PlatInfo;
import com.hyxt.DTO.protocol.JTBZF_0x03;
import com.hyxt.DTO.protocol.ProtocolUtil;
import com.hyxt.boot.DataInitialize;
import com.hyxt.utils.Dao;
import com.hyxt.utils.IRedisMessageListener;
import com.hyxt.utils.U809;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author songm
 * @version v1.0
 * @Description 主链路消息处理类（接受来自2.0通过主链路发送的消息）
 * @Date: Create in 18:50 2017/12/12
 * @Modifide By:
 **/
@Component
public class MainLinkHandler extends IRedisMessageListener {

    private static final Log log = LogFactory.getLog(MainLinkHandler.class);
    public static T809_ProtocolAnalysis analysis = new T809_ProtocolAnalysis();
    public static int acceptCode = 10000009;
    public static Timer timer = new Timer();
    public static String subChannelLocalPort;
    public static boolean open = false;
    @Value("${whileList}")
    public static String authCode;
    @Value("${subChannelAddress}")
    public static String clientAddress;
    @Value("${ip_1001}")
    public static String ip_1001;
    @Value("${port_1001}")
    public static String port_1001;

    /**
     * 监听缓存收到的消息
     *
     * @param channel
     * @param message
     */
    public void onMessage(String channel, String message) {
        try {
            // read data
            byte[] data = message.getBytes();
            byte[] data_reverse = T809_Util.reverseEscapeData(data);
            int messageID = T809_Util.getMessageID(data_reverse, null);

            if (data_reverse[9] == 0x10 && data_reverse[10] == 0x01) {// receive
                mainChannelResponseLogin();
                subChannelRequestLogin();
                Uplink809Command(12, messageID, data_reverse);
            } else if (data_reverse[9] == 0x10 && data_reverse[10] == 0x03) {// receive
                mainChannelResponseClose();
            } else if (data_reverse[9] == 0x10 && data_reverse[10] == 0x05) {// receive
                mainChannelResponseBeat();
                Uplink809Command(12, messageID, data_reverse);
            } else {
                if (authCode.equals("510000")) {
                    if (messageID == 0x1202) {
                        int hour = Integer.valueOf(new SimpleDateFormat("HH").format(new Date()));
                        if (6 <= hour && hour < 22) {
                            byte[] a = BytesUtil.int2bytes2(80);
                            data_reverse[77] = a[0];
                            data_reverse[78] = a[1];
                        } else {
                            byte[] a = BytesUtil.int2bytes2(80);
                            data_reverse[77] = a[0];
                            data_reverse[78] = a[1];
                        }
                        System.out.println("1202-----");
                    }
                }
                Uplink809Command(12, messageID, data_reverse);
            }
        } catch (ProtocolEscapeExeption protocolEscapeExeption) {
            protocolEscapeExeption.printStackTrace();
        }
    }

    public static void mainChannelResponseLogin() {
        byte[] data = U809.es(U809.changeCode(BytesUtil.toStringHex("5b0000001f0000000010020000c4e1010000000000000000000004d2c8705d"), acceptCode));
        // response 0x1002
        Connection.serverChannel.writeAndFlush(data);
        log.debug(">>>809主链路发送信息:" + BytesUtil.bytesToHexString(data));
    }

    private static void subChannelRequestLogin0() {


        String[] add = clientAddress.split(":");

        if (Connection.clientChannel != null) {
            Connection.clientChannel.disconnect();
            Connection.clientChannel.close();
        }

        // sub channel try to connect
        ChannelFuture cf = null;

        if (subChannelLocalPort == null) {
            cf = Connection.netty809Client.connect(new InetSocketAddress(add[0], Integer.valueOf(add[1])));
        } else {
            // sub channel try to connect
            cf = Connection.netty809Client.connect(new InetSocketAddress(add[0], Integer.valueOf(add[1])), new InetSocketAddress(Integer.valueOf(subChannelLocalPort)));
        }
        try {
            cf.get();
            Connection.clientChannel = cf.channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        T809_MessageHeader header = new T809_MessageHeader();
        T809_0x9001 body9001 = new T809_0x9001();
        header.setMessageID(0x9001);
        header.setGnsscentrid(acceptCode);
        body9001.setMessageID(0x9001);
        CommonMessage<T809_0x9001> message = new CommonMessage<T809_0x9001>(0x9001, header, body9001);
        header.setVersonFlag(new byte[]{1, 0, 0});
        // pack 0x9001 message
        byte[] data = analysis.pack(message);
        // send out
        Connection.clientChannel.writeAndFlush(data);
        log.debug(">>>809从链路发送信息:" + BytesUtil.bytesToHexString(data));
    }

    public static void subChannelRequestBeat() {
        byte[] data = U809.es(U809.changeCode(BytesUtil.toStringHex("5b0000001a0000463c900500e508e900000000000004d267855d"), acceptCode));
        // send 0x9005 beatheat data
        Connection.clientChannel.writeAndFlush(data);
        log.debug(">>>809从链路发送信息:" + BytesUtil.bytesToHexString(data));
    }

    public static void subChannelRequestLogin() {
        // sub channel try to connect and login
        subChannelRequestLogin0();
        // timer task to check sub channel whether it is connected
        if (timer == null) {
            timer = new Timer();
        } else {
            timer.cancel();
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (Connection.clientChannel != null && Connection.clientChannel.isOpen() && Connection.clientChannel.isActive()) {
                        subChannelRequestBeat();
                    } else {
                        subChannelRequestLogin0();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 60 * 1000, 60 * 1000);

    }

    public static void Uplink809Command(int uid, int messageID, byte[] command) {
        JTBZF_0x03 body_0x03 = new JTBZF_0x03();
        // 这个位置 应该有一个<车牌号+颜色,List<企业ID>>
        //主动上报报警处理结果信息
        if (messageID == 0x1403) {
            log.info("1403!!!!!!!!!");
        }
        if (DataInitialize.commandList.contains(messageID + "")) {
            // 这样的话说明是车辆类指令业务[先解析出车牌号,然后拿到所有车牌对应的平台]
            // 1601、1501、1502、1503、1504、1505、1401、1402、1201、
            // 1202、1203、1205、1206、1207、1208、1209、120A、120B、120C、120D
            // 车牌号
            byte[] plateNumber = BytesUtil.cutBytes(23, 21, command);
            // 车牌颜色
            byte color = BytesUtil.cutBytes(44, 1, command)[0];
            // 车牌号+车牌颜色唯一标识
            String key = "";
            try {
                key = new String(plateNumber, "GBK").trim() + color;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            // System.out.println(key);

            // 通过车辆获取到平台列表
            List<Integer> businesseList = (List<Integer>) DataInitialize.plateToBusinessCache.get(key);
            log.debug("key:" + key + " plats:" + businesseList + " whileList:" + LocalCacheManager.getCache("_plateWhiteList").get("whileList"));
            // 遍历所有平台进行转发(因为一个车对应的平台很多,平台ID不一样,需要遍历车对应的每一个平台,改了平台ID,再发)
            //上面的注释是设计系统的构想,实际情况白名单只有一个
            if (businesseList != null) {
                for (int businessID : businesseList) {
                    // log.info("id"+businessID+":"+key);
                    if (!((Set<String>) LocalCacheManager.getCache("_plateWhiteList").get("whileList")).contains(businessID + "")) {
                        continue;
                    }
                    // log.info("ok:" + key);
                    byte[] messageID_bytes = BytesUtil.int2bytes2(messageID);
                    //交通部协议中规定的是车辆类指令是车辆唯一标识,非车辆类指令是平台id,后来全都改成了平台id
                    body_0x03.setUid(businessID);// 平台id
                    body_0x03.setBusiness(messageID_bytes);// 消息id
                    Set keySet = DataInitialize.accessCodeToBusinessCache.keySet();
                    String aaa = null;
                    // 通过平台id获取接入码，然后把原消息接入码替换掉
                    for (Object object : keySet) {
                        if (DataInitialize.accessCodeToBusinessCache.get(object).equals(businessID + "")) {
                            command = U809.changeCode(command, Integer.valueOf((String) object));
                            aaa = (String) object;
                            break;
                        }
                    }
                    final JTBZF_0x03 bodyfinal = body_0x03;
                    final byte[] commandfinal = command;
                    final String aaafinal = aaa;
//
                    body_0x03.setBytes_809(U809.es(command));
                    // 判断各平台是否连接成功，以此依据发送数据
                    if (aaa != null) {
                        // 得到打包的消息
                        final byte[] message = ProtocolUtil.getMessageByBody((byte) 0x03, body_0x03);
                        // log.info("准备发送:"+key);
                        transferChannel.writeAndFlush(message).addListener(new ChannelFutureListener() {

                            @Override
                            public void operationComplete(ChannelFuture future) throws Exception {

                                if (future.isSuccess()) {
                                    // 这就是发送成功
                                    saveLimitedCatch(message);
                                    log.info(">>>交通部转发平台链路发送信息:" + BytesUtil.bytesToHexString(message));
                                } else {
                                    // 这个位置推送前台 TODO
                                }
                            }
                        });
                    }
                }
            }

        } else if (messageID == 0x1001 || messageID == 0x1003 || messageID == 0x1005) {
            Set keySet = DataInitialize.accessCodeToBusinessCache.keySet();
            for (Object object : keySet) {
                final int businessID = Integer.valueOf((String) DataInitialize.accessCodeToBusinessCache.get(object));
                final byte[] messageID_bytes = BytesUtil.int2bytes2(messageID);
                body_0x03.setUid(businessID);
                body_0x03.setBusiness(messageID_bytes);

                if (!((Set<String>) LocalCacheManager.getCache("_plateWhiteList").get("whileList")).contains(businessID + "")) {
                    continue;
                }
                if (messageID == 0x1001) {
                    //这里为什么还要查询数据库？？？？缓存里不是有了吗？
                    Dao dao = new Dao();
                    //通过接入码查询平台
                    PlatInfo info = dao.selectPlatByCode(object.toString());
                    //改的是用户名密码IP端口,为什么改,咱们这边查数据库查的是对的.2.0发过来的可能不一样
                    byte[] userid = BytesUtil.int2bytes4(info.getUserid());
                    byte[] pass = BytesUtil.writeStrToAppoint(info.getPassword(), 8);
                    // byte[] ip =
                    // BytesUtil.writeStrToAppoint("219.143.235.110", 32);
                    byte[] ip = BytesUtil.writeStrToAppoint(ip_1001, 32);
                    // 219.143.235.110
                    // 111.205.202.82
                    byte[] port = BytesUtil.int2bytes2(Integer.parseInt(port_1001));
                    command[23] = userid[3];
                    command[24] = userid[2];
                    command[25] = userid[1];
                    command[26] = userid[0];
                    System.arraycopy(pass, 0, command, 27, pass.length);
                    System.arraycopy(ip, 0, command, 35, ip.length);
                    command[67] = port[0];
                    command[68] = port[1];
                    command = U809.changeCode(command, Integer.valueOf((String) object));
                    body_0x03.setBytes_809(U809.es(command));

                    // 得到打包的消息
                    final byte[] message = ProtocolUtil.getMessageByBody((byte) 0x03, body_0x03);

                    transferChannel.writeAndFlush(message).addListener(new ChannelFutureListener() {

                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {

                            if (future.isSuccess()) {
                                // 这就是发送成功
                                saveLimitedCatch(message);
                                log.info(">>>交通部转发平台链路发送信息:" + BytesUtil.bytesToHexString(message));

                            } else {
                                // 这个位置推送前台 TODO
                            }
                        }
                    });

                } else if (messageID == 0x1003) {
                    Dao dao = new Dao();
                    PlatInfo info = dao.selectPlatByCode(object.toString());
                    byte[] userid = BytesUtil.int2bytes4(info.getUserid());
                    byte[] pass = BytesUtil.writeStrToAppoint(info.getPassword(), 8);
                    command[23] = userid[3];
                    command[24] = userid[2];
                    command[25] = userid[1];
                    command[26] = userid[0];
                    System.arraycopy(pass, 0, command, 27, pass.length);
                    command = U809.changeCode(command, Integer.valueOf((String) object));
                    body_0x03.setBytes_809(U809.es(command));

                    // 得到打包的消息
                    final byte[] message = ProtocolUtil.getMessageByBody((byte) 0x03, body_0x03);

                    transferChannel.writeAndFlush(message).addListener(new ChannelFutureListener() {

                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {

                            if (future.isSuccess()) {
                                // 这就是发送成功
                                saveLimitedCatch(message);
                                log.info(">>>交通部转发平台链路发送信息:" + BytesUtil.bytesToHexString(message));
                            } else {
                                // 这个位置推送前台 TODO
                            }
                        }
                    });
                } else {

                    // if
                    // (TransferHandler.upFlag.containsKey(Integer.valueOf((String)
                    // object) + "")) {
                    command = U809.changeCode(command, Integer.valueOf((String) object));

                    body_0x03.setBytes_809(U809.es(command));

                    // 得到打包的消息
                    final byte[] message = ProtocolUtil.getMessageByBody((byte) 0x03, body_0x03);

                    transferChannel.writeAndFlush(message).addListener(new ChannelFutureListener() {

                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {

                            if (future.isSuccess()) {
                                // 这就是发送成功
                                saveLimitedCatch(message);
                                log.info(">>>交通部转发平台链路发送信息:" + BytesUtil.bytesToHexString(message));
                            } else {
                                // 这个位置推送前台 TODO
                            }
                        }
                    });
                }
                // }

            }

        } else {
            // 非车辆指令信息 采用平台唯一编号 这个时候呢 要去找流水号-->接入码-->企业ID
            //809指令中的报文序列号就是交通部协议中的流水号
            byte[] seq809_bytes = BytesUtil.cutBytes(5, 4, command);
            // 得到流水号
            int seq = BytesUtil.bytes2int4(seq809_bytes);
            // 获得接入码

            int accessCode = 0;
            if (messageID == 0x1301 || messageID == 0x1401) {
                //平台查岗应答  报警督办应答
                // System.out.println("!!!!!!!!!"+seq);
                // String a = (String) pcache.remove(seq + "");
                // String[] b = a.split(":");
                // accessCode = Integer.valueOf(b[0]).intValue();
                // SnapshotUtil.createSnapshot(pcache);
                accessCode = U809.getAccessCode(command);
            } else {
                try {
                    accessCode = U809.getAccessCode(command);
                } catch (Exception e) {
                    e.initCause(new Exception(BytesUtil.bytesToHexString(command)));
                    e.printStackTrace();
                    accessCode = U809.getAccessCode(command);
                }

            }
            // 拿到企业ID
            int businessID = Integer.valueOf((String) DataInitialize.accessCodeToBusinessCache.get(accessCode + ""));

            // 收拾收拾发送
            byte[] messageID_bytes = BytesUtil.int2bytes2(messageID);
            if (!((Set<String>) LocalCacheManager.getCache("_plateWhiteList").get("whileList")).contains(businessID + "")) {
                return;
            }
            body_0x03.setUid(businessID);
            body_0x03.setBusiness(messageID_bytes);
            command = U809.changeCode(command, accessCode);
            body_0x03.setBytes_809(U809.es(command));

            // 得到打包的消息
            final byte[] message = ProtocolUtil.getMessageByBody((byte) 0x03, body_0x03);

            transferChannel.writeAndFlush(message).addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if (future.isSuccess()) {
                        // 这就是发送成功
                        saveLimitedCatch(message);
                        log.info(">>>交通部转发平台链路发送信息:" + BytesUtil.bytesToHexString(message));
                    } else {
                        // 这个位置推送前台 TODO
                    }
                }
            });

        }
    }

    public static void mainChannelResponseBeat() {
        byte[] data = U809.es(U809.changeCode(BytesUtil.toStringHex("5b0000001a00003482100600e508e900000000000004d220a35d"), acceptCode));
        // response 0x1006
        Connection.serverChannel.writeAndFlush(data);
        log.debug(">>>809主链路发送信息:" + BytesUtil.bytesToHexString(data));
    }

    public static void mainChannelResponseClose() {
        try {
            byte[] data = U809.es(U809.changeCode(BytesUtil.toStringHex("5b0000001a00003482100400e508e900000000000004d2aa655d"), acceptCode));
            Connection.serverChannel.writeAndFlush(data).sync();
            open = false;
            log.debug(">>>809主链路发送信息:" + BytesUtil.bytesToHexString(data));
            if (Connection.clientChannel != null) {
                Connection.clientChannel.disconnect();
                Connection.clientChannel.close();
            }
            if (timer != null)
                timer.cancel();
            timer = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
