package com.hyxt.DTO.protocol;

/**
 * @Description 删除上级平台信息 方向:车辆管理系统->数据转发服务平台 车辆管理系统中上级平台信息删除时实时交换
 *              !!!!!数据转发服务平台收到指令后回复通用应答
 * @author jakiro
 * @version V1.0
 * @Date 2016年5月20日 上午9:17:46
 * @mail terrorbladeyang@gmail.com
 */
public class JTBZF_0x16 extends JTBZFBody {

	public JTBZF_0x16() {
		setId((byte)0x16);
	}
	
	private int uid;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}
	
}
