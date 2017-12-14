package com.hyxt.DTO.protocol;

/**
 * @Description 通用应答
 * @author jakiro
 * @version V1.0
 * @Date 2016年5月20日 上午10:35:17
 * @mail terrorbladeyang@gmail.com
 */
public class JTBZF_0x20 extends JTBZFBody {
	
	//初始化设置消息ID
	public JTBZF_0x20() {
		setId((byte)0x20);
	}
	
	//应答流水号
	private int sn;
	
	//对应消息ID
	private byte msgid;
	
	//结果 0:成功/确认 1:失败 2:消息有误 3:不支持
    private byte result;
    
	public int getSn() {
		return sn;
	}

	public void setSn(int sn) {
		this.sn = sn;
	}

	public byte getMsgid() {
		return msgid;
	}

	public void setMsgid(byte msgid) {
		this.msgid = msgid;
	}

	public byte getResult() {
		return result;
	}

	public void setResult(byte result) {
		this.result = result;
	}
	
}
