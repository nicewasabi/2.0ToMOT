package com.hyxt.DTO.protocol;

/**
 * @Description 删除车辆信息 车辆管理系统->数据转发服务平台 车辆管理系统中车辆信息删除时实时交换
 *              !!!!!!数据转发服务平台收到指令后回复通用应答指令
 * @author jakiro
 * @version V1.0
 * @Date 2016年5月20日 上午8:44:26
 * @mail terrorbladeyang@gmail.com
 */
public class JTBZF_0x14 extends JTBZFBody {

	/**
	 * @Description 获得 vin
	 */
	public String getVin() {
		return vin;
	}

	/**
	 * @Description 设置 vin 
	 */
	public void setVin(String vin) {
		this.vin = vin;
	}

	/**
	 * @Description 获得 color
	 */
	public int getColor() {
		return color;
	}

	/**
	 * @Description 设置 color 
	 */
	public void setColor(int color) {
		this.color = color;
	}

	public JTBZF_0x14() {
		setId((byte)0x14);
	}
	private String vin;
	
	private int color;
	
	
//	
//	private int vid;
//
//	public int getVid() {
//		return vid;
//	}
//
//	public void setVid(int vid) {
//		this.vid = vid;
//	}
	
}
