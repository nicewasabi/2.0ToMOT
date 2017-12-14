package com.hyxt.DTO.protocol;

/**
 * @Description 心跳应答 
 * @author jakiro
 * @version V1.0
 * @Date 2016年5月20日 上午12:14:17
 * @mail terrorbladeyang@gmail.com
 */
public class JTBZF_0x81 extends JTBZFBody {

	public JTBZF_0x81() {
		setId((byte)0x81);
	}
	
	//对应的上行的消息的流水号
	private int sn;

	public int getSn() {
		return sn;
	}

	public void setSn(int sn) {
		this.sn = sn;
	}
	
}
