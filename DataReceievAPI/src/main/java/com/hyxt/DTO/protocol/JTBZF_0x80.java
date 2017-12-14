package com.hyxt.DTO.protocol;


/**
 * @Description 认证应答
 * @author jakiro
 * @version V1.0
 * @Date 2016年5月20日 上午12:01:15
 * @mail terrorbladeyang@gmail.com
 */
public class JTBZF_0x80 extends JTBZFBody {
	
	public JTBZF_0x80() {
		setId((byte)0x80);
	}
	
	//对应的上行消息的流水号
	private int sn;
	
	//认证结果,含义如下: 1=IP错误 2=端口错误 3=鉴权码错误 4=其他错误 0＝成功
	private byte result;

	public int getSn() {
		return sn;
	}

	public void setSn(int sn) {
		this.sn = sn;
	}

	public byte getResult() {
		return result;
	}

	public void setResult(byte result) {
		this.result = result;
	}
	
}
