package com.hyxt.DTO.protocol;

/**
 * @Description 上行809 方向:车辆管理系统->数据转发平台
 * @author jakiro
 * @version V1.0
 * @Date 2016年5月20日 上午10:01:37
 * @mail terrorbladeyang@gmail.com
 */
public class JTBZF_0x03 extends JTBZFBody {

	
	public JTBZF_0x03() {
		setId((byte)0x03);
	}
	
     //平台编号或车辆编号(车辆类指令为车辆唯一编号,非车辆类指令为平台唯一编号)
	 private int uid;
	 //业务类型或者消息编号(1202,1002) 优先业务类型,无业务类型传消息编号
	 private byte[] business;
	 
	 //809协议完整指令
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
