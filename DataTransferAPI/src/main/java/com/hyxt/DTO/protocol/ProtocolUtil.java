package com.hyxt.DTO.protocol;

import com.hyxt.DTO.protocol.JTBZF_0x82.DataIterm;
import cn.com.cnpc.vms.common.exception.ProtocolEscapeExeption;
import cn.com.cnpc.vms.common.util.ByteUtil;
import cn.com.cnpc.vms.common.util.BytesUtil;
import com.hyxt.utils.AreaUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description 协议工具类 各种解析 各种打包
 * @author jakiro
 * @version V1.0
 * @Date 2016年5月20日 下午5:58:12
 * @mail terrorbladeyang@gmail.com
 */
public class ProtocolUtil {

	private final static Log log = LogFactory.getLog(ProtocolUtil.class);

	// 流水号
	private static AtomicInteger seq = new AtomicInteger(0);

	// 标志位
	static byte mark = 0x7E;

	/**
	 * 获取消息头
	 * 
	 * @param data
	 * @return
	 */
	public static JTBZFHeader getHeader(byte[] data) {
		JTBZFHeader header = new JTBZFHeader();
		byte id = BytesUtil.getByte(1, data);
		int num = BytesUtil.parseBytesToInt(BytesUtil.getBigDWord(2, data));
		short length = BytesUtil.parseBytesToShort(BytesUtil.getBigDWord(6,
				data));
		byte check = BytesUtil.getByte(data.length - 2, data);
		header.setId(id);
		header.setNum(num);
		header.setLength(length);
		header.setCheck(check);
		return header;
	}

	/**
	 * @Description:此方法的作用是获取校验码
	 * @param :data 每一个data都表示一个8位的字节数组
	 * @return
	 * @throws Exception
	 */
	public static byte check(byte[] data) {
		// 计算校验码
		byte checked = (byte) (data[1] ^ data[2]);
		for (int i = 3; i < (data.length - 2); i++) {
			checked = (byte) (checked ^ data[i]);
		}
		return checked;
	}

	/**
	 * 转义还原
	 * 
	 * @param data
	 * @return
	 * @throws ProtocolEscapeExeption
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static byte[] reverseEscapeData(byte[] data)
			throws ProtocolEscapeExeption {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] reverseEscapedDatabytes;
		for (int i = 0; i < (data.length - 1); i++) {
			// 判断消息尾
			if (data[i] == 0x7d && data[i + 1] == 0x02) {
				// 转义
				bos.write((byte) 0x7e);
				i++;
			} else if (data[i] == 0x7d && data[i + 1] == 0x01) {
				// 转义
				bos.write((byte) 0x7d);
				i++;
			} else if ((data[i] == 0x7d && data[i + 1] != 0x02)
					|| (data[i] == 0x7d && data[i + 1] != 0x01)) {
				reverseEscapedDatabytes = null;
				try {
					// 关闭数据流
					bos.close();
				} catch (IOException e) {
					log.warn("", e);
				}
				// 抛出异常
				throw new ProtocolEscapeExeption(ByteUtil.bytes2BCDStr(data));
			} else {
				bos.write(data[i]);
			}
		}
		// 如果数据倒数第二位不是数据位
		if (data[data.length - 2] != 0x7d) {
			if (data[data.length - 1] != 0x7d) {
				bos.write(data[data.length - 1]);
			} else {
				reverseEscapedDatabytes = null;
				try {
					// 关闭数据流
					bos.close();
				} catch (IOException e) {
					log.warn("", e);
				}
				// 抛出异常
				throw new ProtocolEscapeExeption(ByteUtil.bytes2BCDStr(data));
			}
		} else {
			// 倒数第二位等于125 则最后一位是转义位，不需要写入转以后的数据中
		}
		reverseEscapedDatabytes = bos.toByteArray();
		try {
			// 关闭数据流
			bos.close();
		} catch (IOException e) {
			log.warn("", e);
		}
		return reverseEscapedDatabytes;
	}

	/**
	 * @Description:此方法的作用是处理消息头、消息体和校验码里面的常规的0xXX和不常规的0x7d、0x7e的处理
	 * @param :data 每一个data都表示一个8位的字节数组
	 * @return
	 * @throws Exception
	 */
	public static byte[] escapeData(byte[] data) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] escapedDatabytes;
		for (int i = 0; i < data.length; i++) {
			// 判断数据头
			if (data[i] == 0x7e) {
				// 转义
				bos.write((byte) 0x7d);
				bos.write((byte) 0x02);
			} else if (data[i] == 0x7d) {
				// 转义
				bos.write((byte) 0x7d);
				bos.write((byte) 0x01);
			} else {
				// 写入数据流
				bos.write(data[i]);
			}
		}
		// 获取byte数
		escapedDatabytes = bos.toByteArray();
		try {
			// 关闭数据流
			bos.close();
		} catch (IOException e) {
			log.warn("", e);
		}
		// 返回byte数组
		return escapedDatabytes;
	}

	/**
	 * 解析,通过传过来的消息ID和消息体的byte[] 得到对应的消息体对象
	 * */
	public static JTBZFBody getBody(byte messageID, byte[] messageBody) {

		JTBZFBody commonBody = null;
		// 上来直接分辨一下
		switch (messageID) {
		case (byte) 0x80:// 认证应答

			// 新定义对象
			commonBody = new JTBZF_0x80();
			// 对应的上行消息的流水号
			byte[] sn_0x80 = BytesUtil.cutBytes(0, 4, messageBody);
			// 认证结果
			byte result = BytesUtil.cutBytes(4, 1, messageBody)[0];
			// 赋值
			// 流水号(对应上行信息的)
			((JTBZF_0x80) commonBody).setSn(BytesUtil.bytes2int4(sn_0x80));
			// 结果
			((JTBZF_0x80) commonBody).setResult(result);

			break;
		case (byte) 0x81:// 心跳应答

			// 定义新对象
			commonBody = new JTBZF_0x81();
			// 对应的上行的流水号
			byte[] sn_0x81 = messageBody;
			((JTBZF_0x81) commonBody).setSn(BytesUtil.bytes2int4(sn_0x81));
			break;

		case (byte) 0x82:// 平台链路信息

			// 定义新对象
			commonBody = new JTBZF_0x82();
			// 各种解析
			// 获取时间
			byte[] longTime_0x82 = BytesUtil.cutBytes(0, 8, messageBody);
			// 数据项数
			byte itemNum_0x82 = BytesUtil.cutBytes(8, 1, messageBody)[0];
			// 设置时间
			((JTBZF_0x82) commonBody).setLongtime(BytesUtil
					.parseBytesToLong(longTime_0x82));
			// 设置数据项数
			((JTBZF_0x82) commonBody).setItem(itemNum_0x82);
			// 新搞一个集合
			List<DataIterm> dataItermList = new ArrayList<JTBZF_0x82.DataIterm>();
			// 循环的把剩下的东西数据项加到集合中
			for (int i = 0; i < itemNum_0x82; i++) {
				DataIterm iterm = new JTBZF_0x82().new DataIterm();
				byte[] platid_0x82 = BytesUtil.getDWord(0,
						BytesUtil.cutBytes(9 + i * 5, 4, messageBody));
				byte item_0x82 = BytesUtil.cutBytes(13 + i * 5, 1, messageBody)[0];
				// 采取行政区划编号
				iterm.setPlatId(BytesUtil.bytes2int4(platid_0x82));
				// 链路情况
				iterm.setItem(item_0x82);
				dataItermList.add(iterm);
			}
			((JTBZF_0x82) commonBody).setDataItermList(dataItermList);

			break;

		case (byte) 0x84:// 下行809指令

			// 转换对象
			commonBody = new JTBZF_0x84();
			// 获取809下行指令
			byte[] bytes_809_0x84 = messageBody;
			// 设置
			((JTBZF_0x84) commonBody).setBytes_809(bytes_809_0x84);

			break;

		case (byte) 0x20:// 通用应答

			// 转换对象
			commonBody = new JTBZF_0x20();
			// 应答流水号
			byte[] sn_0x20 = BytesUtil.cutBytes(0, 4, messageBody);
			// 对应的消息ID
			byte msgid_0x20 = BytesUtil.cutBytes(4, 1, messageBody)[0];
			// 结果
			byte result_0x20 = BytesUtil.cutBytes(5, 1, messageBody)[0];
			// 各种设置
			((JTBZF_0x20) commonBody).setSn(BytesUtil.bytes2int4(sn_0x20));
			((JTBZF_0x20) commonBody).setMsgid(msgid_0x20);
			((JTBZF_0x20) commonBody).setResult(result_0x20);

			break;

		default:

			break;
		}

		return commonBody;
	}

	public static byte[] getMessageByBody(byte messageID, JTBZFBody body) {

		// Map<Integer, byte[]> returnValue = new HashMap<Integer, byte[]>();
		// 创建ByteArrayOutputStream 以及合成消息体
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		byte[] result = null;

		int seqNumber = 0;
		// 分辨一下
		switch (messageID) {
		case 0x00: // 上行认证
			try {
				// 转换成正常消息体
				JTBZF_0x00 body_0x00 = (JTBZF_0x00) body;
				// 鉴权码
				byte[] code_0x00 = BytesUtil.writeStrToAppoint(
						body_0x00.getCode(), 20);
				// 开始拼一下
				// 消息体长度
				int dataLength_0x00 = code_0x00.length;
				// 填充部分消息头信息
				seqNumber = fillInMessageHeader(bos, (byte) messageID,
						dataLength_0x00);
				// 消息体填充
				bos.write(code_0x00);

			} catch (IOException e) {
				e.printStackTrace();
			}

			break;

		case (byte) 0x80:// 认证应答

			// JTBZF_0x80 body_0x80=(JTBZF_0x80)body;
			//
			// //流水号
			// byte[] seq=BytesUtil.int2bytes4(body_0x80.getSn());
			//
			// //result
			// byte resu=body_0x80.getResult();
			break;
		case 0x01:// 上行心跳 没有消息体 就不转消息体了

			// 消息体 长度是0
			int dataLength_0x01 = 0;
			// 填充部分信息
			seqNumber = fillInMessageHeader(bos, messageID, dataLength_0x01);

			break;

		case 0x11:// 上报运输企业信息(0x11)

			// 转换消息体
			JTBZF_0x11 body_0x11 = (JTBZF_0x11) body;
			// 得到消息体长度
			int dataLength_0x11 = 182;
			// 填充部分消息头信息
			seqNumber = fillInMessageHeader(bos, messageID, dataLength_0x11);
			// 填写消息体

			// // 主键编号
			// byte[] code_0x11 = BytesUtil.getBigDWord(0,
			// BytesUtil.int2bytes4(body_0x11.getCode()));
			// 经营许可号 定长30
			byte[] code_0x11 = BytesUtil.writeStrToAppoint(body_0x11.getCode(),
					30);

			// 名称 定长40
			byte[] name_0x11 = BytesUtil.writeStrToAppoint(body_0x11.getName(),
					40);

			// 地址 定长64
			byte[] address_0x11 = BytesUtil.writeStrToAppoint(
					body_0x11.getAddress(), 64);
			// 联系人 定长18
			byte[] person_0x11 = BytesUtil.writeStrToAppoint(
					body_0x11.getPerson(), 18);
			// 联系电话 定长30
			byte[] phone_0x11 = BytesUtil.writeStrToAppoint(
					body_0x11.getPhone(), 30);

			try {
				bos.write(code_0x11);
				// bos.write(licenseNo_0x11);
				bos.write(name_0x11);
				bos.write(address_0x11);
				bos.write(person_0x11);
				bos.write(phone_0x11);
			} catch (IOException e) {
				e.printStackTrace();
			}

			break;

		case 0x12:// 删除企业信息

			JTBZF_0x12 body_0x12 = (JTBZF_0x12) body;

			// int code = body_0x12.getCode();

			// int dataLength_0x12 = 4;
			int dataLength_0x12 = 30;

			seqNumber = fillInMessageHeader(bos, messageID, dataLength_0x12);
			if (body_0x12.getCode() == null) {
				body_0x12.setCode("");
			}
			byte[] code_0x12 = BytesUtil.writeStrToAppoint(body_0x12.getCode(),
					30);

			try {
				bos.write(code_0x12);
			} catch (IOException e2) {
				e2.printStackTrace();
			}

			break;
		case 0x13:// 上报车辆信息

			// 转换消息体
			JTBZF_0x13 body_0x13 = (JTBZF_0x13) body;
			// 得到消息长度
			int dataLength_0x13 = body_0x13.getTransNum() * 4 + 55 + 6 + 26;
			// 填充部分消息
			seqNumber = fillInMessageHeader(bos, messageID, dataLength_0x13);
			// 填写消息体
			// byte[] vid_0x13 = BytesUtil.getBigDWord(0,
			// BytesUtil.int2bytes4(body_0x13.getVid()));
			byte[] vid_0x13 = AreaUtil.getVechileCode(body_0x13.getVin(),
					body_0x13.getColor() + "").getBytes();
			// 车牌号
			byte[] vin_0x13 = BytesUtil.writeStrToAppoint(body_0x13.getVin(),
					21);
			// 车牌颜色
			byte color_0x13 = body_0x13.getColor();
			// 车籍地
			byte[] district_0x13 = BytesUtil.getBigDWord(0,
					BytesUtil.int2bytes4(body_0x13.getDistrict()));
			// 道路运输证号
			byte[] transportNo_0x13 = BytesUtil.writeStrToAppoint(
					body_0x13.getTransportNo(), 20);
			// 归属企业编号
			// byte[] ownerCode_0x13 = BytesUtil.getBigDWord(0,
			// BytesUtil.int2bytes4(body_0x13.getOwnerCode()));
			byte[] ownerCode_0x13 = BytesUtil.writeStrToAppoint(
					body_0x13.getOwnerCode(), 30);
			// 转发上级平台数量
			byte transNum_0x13 = body_0x13.getTransNum();

			try {
				bos.write(vid_0x13);
				bos.write(vin_0x13);
				bos.write(color_0x13);
				bos.write(district_0x13);
				bos.write(transportNo_0x13);
				bos.write(ownerCode_0x13);
				bos.write(transNum_0x13);
				// 转发上级平台编号 循环写入
				for (int transplant : body_0x13.getList()) {
					bos.write(BytesUtil.getBigDWord(0,
							BytesUtil.int2bytes4(transplant)));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			break;

		case 0x14:// 删除车辆信息

			// 转换消息体
			JTBZF_0x14 body_0x14 = (JTBZF_0x14) body;
			// 得到数据长度
			int dataLength_0x14 = 4 + 6;
			// 填写部分消息
			seqNumber = fillInMessageHeader(bos, messageID, dataLength_0x14);
			// 写消息体
			try {
				// bos.write(BytesUtil.getBigDWord(0,
				// BytesUtil.int2bytes4(body_0x14.getVid())));
				bos.write(AreaUtil.getVechileCode(body_0x14.getVin(),
						body_0x14.getColor() + "").getBytes());
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			break;

		case 0x15:// 上报上级平台信息(0x15)

			// 转换消息体
			JTBZF_0x15 body_0x15 = (JTBZF_0x15) body;
			// 数据体长度 Fuck 算了好几遍 ＝。＝
			int dataLength_0x15 = 328;
			// 填充部分消息
			seqNumber = fillInMessageHeader(bos, messageID, dataLength_0x15);

			// 填写消息体
			// 行政区划编号
			byte[] uid_0x15 = BytesUtil.getBigDWord(0,
					BytesUtil.int2bytes4(body_0x15.getUid()));
			// 上级平台名称
			byte[] name_0x15 = BytesUtil.writeStrToAppoint(body_0x15.getName(),
					60);
			// 平台类型
			byte typeID_0x15 = body_0x15.getTypeId();
			// 上级平台IP
			byte[] ip_0x15 = BytesUtil.writeStrToAppoint(body_0x15.getIp(), 32);
			// 端口
			byte[] port_0x15 = BytesUtil.int2bytes2(body_0x15.getPort());
			// 用户名
			byte[] userid_0x15 = BytesUtil.getBigDWord(0,
					BytesUtil.int2bytes4(body_0x15.getUserID()));
			// 密码
			byte[] password_0x15 = BytesUtil.writeStrToAppoint(
					body_0x15.getPassword(), 8);
			// 唯一接入码
			byte[] centerid_0x15 = BytesUtil.writeStrToAppoint(
					body_0x15.getCenterid(), 11);
			// 加密参数M1
			byte[] M1_0x15 = BytesUtil.getBigDWord(0,
					BytesUtil.int2bytes4(body_0x15.getM1()));
			// 加密参数IA1
			byte[] IA1_0x15 = BytesUtil.getBigDWord(0,
					BytesUtil.int2bytes4(body_0x15.getIA1()));
			// 加密参数IC1
			byte[] IC1_0x15 = BytesUtil.getBigDWord(0,
					BytesUtil.int2bytes4(body_0x15.getIC1()));
			// 版本号
			String versionStr_0x15 = body_0x15.getVersion();
			// 1.0.0--> 010000
			versionStr_0x15 = "0" + versionStr_0x15.replace(".", "") + "00";
			byte[] version_0x15 = BytesUtil.writeStrToAppoint(versionStr_0x15,
					6);
			// 上级平台管理单位
			byte[] org_0x15 = BytesUtil.writeStrToAppoint(body_0x15.getOrg(),
					40);
			// 上级平台联系人
			byte[] person_0x15 = BytesUtil.writeStrToAppoint(
					body_0x15.getPerson(), 18);
			// 上级平台联系电话
			byte[] phone_0x15 = BytesUtil.writeStrToAppoint(
					body_0x15.getPhone(), 30);
			// 备注信息
			byte[] remark_0x15 = BytesUtil.writeStrToAppoint(
					body_0x15.getRemark(), 100);

			// 写入消息体
			try {
				bos.write(uid_0x15);
				bos.write(name_0x15);
				bos.write(typeID_0x15);
				bos.write(ip_0x15);
				bos.write(port_0x15);
				bos.write(userid_0x15);
				bos.write(password_0x15);
				bos.write(centerid_0x15);
				bos.write(M1_0x15);
				bos.write(IA1_0x15);
				bos.write(IC1_0x15);
				bos.write(version_0x15);
				bos.write(org_0x15);
				bos.write(person_0x15);
				bos.write(phone_0x15);
				bos.write(remark_0x15);

			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case 0x16:// 删除上级平台信息

			// 转换对象
			JTBZF_0x16 body_0x16 = (JTBZF_0x16) body;
			// 消息体长度
			int dataLength_0x16 = 4;
			// 填充部分消息
			seqNumber = fillInMessageHeader(bos, messageID, dataLength_0x16);
			// 填充消息体
			byte[] uid_0x16 = BytesUtil.getBigDWord(0,
					BytesUtil.int2bytes4(body_0x16.getUid()));

			try {
				bos.write(uid_0x16);
			} catch (IOException e) {
				e.printStackTrace();
			}

			break;

		case 0x03:// 上行809指令(0x03)

			// 转换对象
			JTBZF_0x03 body_0x03 = (JTBZF_0x03) body;
			// 数据长度
			int dataLength_0x03 = 6 + body_0x03.getBytes_809().length;
			// 填充部分信息
			seqNumber = fillInMessageHeader(bos, messageID, dataLength_0x03);
			// 填充消息体
			// 平台编号或车辆编号
			byte[] uid_0x03 = BytesUtil.getBigDWord(0,
					BytesUtil.int2bytes4(body_0x03.getUid()));
			// 业务类型或消息编号 这个位置 必须是两位长度
			byte[] business_0x03 = body_0x03.getBusiness();
			// 上来的809指令
			byte[] bytes_809_0x03 = body_0x03.getBytes_809();
			try {
				bos.write(uid_0x03);
				bos.write(business_0x03);
				bos.write(bytes_809_0x03);
			} catch (IOException e) {
				e.printStackTrace();
			}

			break;

		case 0x20:// 通用应答

			// 转换对象
			JTBZF_0x20 body_0x20 = (JTBZF_0x20) body;
			// 数据长度
			int dataLength_0x20 = 6;
			// 填充部分信息
			seqNumber = fillInMessageHeader(bos, messageID, dataLength_0x20);
			// 填充消息体
			// 应答流水号
			byte[] sn_0x20 = BytesUtil.getBigDWord(0,
					BytesUtil.int2bytes4(body_0x20.getSn()));
			// 对应的消息ID
			byte msgid_0x20 = body_0x20.getMsgid();
			// 结果
			byte result_0x20 = body_0x20.getResult();

			try {
				bos.write(sn_0x20);
				bos.write(msgid_0x20);
				bos.write(result_0x20);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case 0x04:// 从链路上行809指令

			// 转换对象
			JTBZF_0x04 body_0x04 = (JTBZF_0x04) body;
			// 数据长度
			int dataLength_0x04 = 6 + body_0x04.getBytes_809().length;
			// 填充部分信息
			seqNumber = fillInMessageHeader(bos, messageID, dataLength_0x04);

			try {
				bos.write(BytesUtil.getBigDWord(0,
						BytesUtil.int2bytes4(body_0x04.getUid())));
				bos.write(body_0x04.getBusiness());
				bos.write(body_0x04.getBytes_809());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			break;

		case 0x05: // 链路控制指令

			// 转换对象
			JTBZF_0x05 body_0x05 = (JTBZF_0x05) body;
			// 数据长度
			int dataLength_0x05 = 6;
			// 填充消息头部分
			seqNumber = fillInMessageHeader(bos, messageID, dataLength_0x05);
			// 填充消息体
			try {
				bos.write(BytesUtil.getBigDWord(0,
						BytesUtil.int2bytes4(body_0x05.getPlatID())));
				bos.write(body_0x05.getMasterChannel());
				bos.write(body_0x05.getSlaveChannel());
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			break;
		default:
			break;
		}

		// 填写剩余信息 处理[7E-消息体]之后的部分
		result = fillXorAndEnd(bos);

		try {
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		log.info("打包数据:" + BytesUtil.bytesToHexString(result));
		// result = BytesUtil.bytesToHexString(result).getBytes();
		return result;
	}

	/**
	 * 填充消息头 填充 消息体 之前的内容 标志位,消息ID,流水号,消息体长度
	 * */
	public static int fillInMessageHeader(ByteArrayOutputStream bos,
			byte messageID, int dataLength) {
		// 标志位
		bos.write((byte) 0x00);
		// 消息ID
		bos.write(messageID);
		// 流水号
		int _seq = seq.incrementAndGet();

		try {
			// 流水号
			bos.write(BytesUtil.getBigDWord(0, BytesUtil.int2bytes4(_seq)));
			// 消息体长度
			bos.write(BytesUtil.int2bytes2(dataLength));
			// 返回流水号
			return _seq;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 填充消息尾 先随意填充一个XOR校验码 填充结尾标志位 转换成二进制数组 得出校验码 重新赋位 转义并返回结果
	 * */
	public static byte[] fillXorAndEnd(ByteArrayOutputStream bos) {
		// XOR校验
		bos.write((byte) 0x00);
		// 标志位
		bos.write((byte) 0x00);
		// 转换成二进制消息
		byte[] message = bos.toByteArray();
		// 检查一下校验码XOR
		byte xor = check(message);
		// 重新赋值
		// 转义 赋值
		message[message.length - 2] = xor;
		byte[] result = escapeData(message);
		result[result.length - 1] = 0x7e;
		result[0] = 0x7e;
		return result;
	}

	public static void main(String[] args) {
		JTBZF_0x00 uplinkAuthen = new JTBZF_0x00();
		// 设置上行认证
		uplinkAuthen.setCode("100000");
		byte[] a =ProtocolUtil.getMessageByBody((byte) 0x00, uplinkAuthen);
		System.out.println(BytesUtil.bytesToHexString(a));
		
		JTBZF_0x01 bb = new JTBZF_0x01();
		byte[] c =ProtocolUtil.getMessageByBody((byte) 0x01, bb);
		System.out.println(BytesUtil.bytesToHexString(c));
		
	}
}
