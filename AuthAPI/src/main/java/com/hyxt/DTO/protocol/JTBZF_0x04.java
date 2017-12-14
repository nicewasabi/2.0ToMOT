package com.hyxt.DTO.protocol;

/**
 * @Description 从链路上行809指令 方向: 车联网管理系统->转发平台
 * @author jakiro
 * @version V1.0
 * @Date 2016年6月7日 下午4:04:34
 * @mail terrorbladeyang@gmail.com
 */
public class JTBZF_0x04 extends JTBZFBody{

	
	public JTBZF_0x04() {
		setId((byte)0x04);
	}
	
	//平台编号
	private int uid;
	//指令编号
	private byte[] business;
	//完整809协议指令
	private byte[] bytes_809;
	
	
	public int getUid() {
		return uid;
	}
	
	public void setUid(int uid) {
		this.uid = uid;
	}
	
	public byte[] getBusiness() {
		return business;
	}
	
	public void setBusiness(byte[] business) {
		this.business = business;
	}
	
	public byte[] getBytes_809() {
		return bytes_809;
	}
	
	public void setBytes_809(byte[] bytes_809) {
		this.bytes_809 = bytes_809;
	}
	
}
