package com.hyxt.DTO.protocol;

/**
 * @Description 下行809指令(0x84) 方向:数据转发服务平台->车辆管理系统 无需应答
 * @author jakiro
 * @version V1.0
 * @Date 2016年5月20日 上午10:31:07
 * @mail terrorbladeyang@gmail.com
 */
public class JTBZF_0x84 extends JTBZFBody{

	public JTBZF_0x84() {
		setId((byte)0x84);
	}
	
	//809协议完整指令
	private byte[] bytes_809;

	public byte[] getBytes_809() {
		return bytes_809;
	}

	public void setBytes_809(byte[] bytes_809) {
		this.bytes_809 = bytes_809;
	}
	
}
