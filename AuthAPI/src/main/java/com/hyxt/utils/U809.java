package com.hyxt.utils;

import cn.com.cnpc.vms.common.util.BytesUtil;
import cn.com.cnpc.vms.protocols.tcp.T809.util.T809_Util;
import com.hyxt.DO.pojo.PlatInfo;

public class U809 {

	private static Dao dao = new Dao();

	public static byte[] changeCode(byte[] command, int code) {
		byte[] MSG_GNSSCENTERID = BytesUtil.int2bytes4(code);
		command[11] = MSG_GNSSCENTERID[3];
		command[12] = MSG_GNSSCENTERID[2];
		command[13] = MSG_GNSSCENTERID[1];
		command[14] = MSG_GNSSCENTERID[0];
		return command;
	}

	public static byte[] es(byte[] command) {
		command[0] = 0;
		command[command.length - 1] = 0;
		byte[] CRCCode = T809_Util.check(command);
		command[command.length - 3] = CRCCode[0];
		command[command.length - 2] = CRCCode[1];
		command = T809_Util.escapeData(command);
		command[0] = 0x5b;
		command[command.length - 1] = 0x5d;
		return command;
	}

	public static byte[] create1001(int id) {
		byte[] bytes = BytesUtil.toStringHex("5b00000048000000011"
				+ "001034fb5e3010000000000000000bc614e31323334353637383230322e39362e34322e31313000000000000000000000000000"
				+ "00000000000022b702165d");
		
		PlatInfo info =null;
		try{
		info = dao.getPlatById(id + "");
		}catch(Exception e){
			e.printStackTrace();
		}
		if(info == null)
			return null;
		byte[] userid = BytesUtil.int2bytes4(info.getUserid());
		byte[] pass = BytesUtil.writeStrToAppoint(info.getPassword(), 8);
//		byte[] ip = BytesUtil.writeStrToAppoint("219.143.235.110", 32);
		
		byte[] ip = BytesUtil.writeStrToAppoint(TransferConnection.ip_1001, 32);
	//	byte[] ip = BytesUtil.writeStrToAppoint("111.205.202.82", 32);
		byte[] port = BytesUtil.int2bytes2(TransferConnection.port_1001);
		bytes[23] = userid[3];
		bytes[24] = userid[2];
		bytes[25] = userid[1];
		bytes[26] = userid[0];
		System.arraycopy(pass, 0, bytes, 27, pass.length);
		System.arraycopy(ip, 0, bytes, 35, ip.length);
		bytes[67] = port[0];
		bytes[68] = port[1];
		bytes = U809.changeCode(bytes, Integer.valueOf(info.getCenterid()));
		bytes = U809.es(bytes);
		
		return bytes;
	}

	public static byte[] changeSN(byte[] escape_msg, int responseSn) {
		byte[] sn = BytesUtil.int2bytes4(responseSn);
		escape_msg[5] = sn[3];
		escape_msg[6] = sn[2];
		escape_msg[7] = sn[1];
		escape_msg[8] = sn[0];
		return escape_msg;
	}

	public static byte[] en(byte[] message809_reverseEscapse, int encryptKey, int[] codes) {
		//消息体
		byte[] bodyData = BytesUtil.cutBytes(23, message809_reverseEscapse.length - 26, message809_reverseEscapse);
		//加密消息体
		byte[] enData = T809_Util.encrypt(encryptKey, bodyData, codes[0],codes[1], codes[2]);
		//覆盖
		System.arraycopy(enData, 0, message809_reverseEscapse, 23, enData.length);
		//设置成未加密标识
		message809_reverseEscapse[18] = 0;
		//加转义返回
		return es(message809_reverseEscapse);
	}

	public static int getAccessCode(byte[] command) {
		byte[] MSG_GNSSCENTERID = new byte[4];
		MSG_GNSSCENTERID[0] = command[11];
		MSG_GNSSCENTERID[1] = command[12];
		MSG_GNSSCENTERID[2] = command[13];
		MSG_GNSSCENTERID[3] = command[14];
		return BytesUtil.parseBytesToInt(MSG_GNSSCENTERID);
	}
	
	
	public static void main(String[] args) {
		byte[] bytes = BytesUtil.toStringHex("5b0000003b0000005093000000c4e10100000065cfeb1793010000001b013332303030303530343031000000026000000006312b313da3bf0e955d");
		System.out.println(getAccessCode(bytes));
	}

}
