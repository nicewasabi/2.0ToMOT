package com.hyxt.DTO.protocol;

/**
 * @Description 删除企业信息(0x12) 车辆管理系统->数据转发平台 车辆管理系统中企业信息删除实时交换
 *              !!!!数据转发平台收到指令后回复通用应答
 * @author jakiro
 * @version V1.0
 * @Date 2016年5月20日 上午12:27:21
 * @mail terrorbladeyang@gmail.com
 */
public class JTBZF_0x12 extends JTBZFBody {

	public JTBZF_0x12() {
		setId((byte)0x12);
	}
	private String code ;
	/**
	 * @Description 获得 code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @Description 设置 code 
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
	
//	//主键编号
//	private int code;
//
//	public int getCode() {
//		return code;
//	}
//
//	public void setCode(int code) {
//		this.code = code;
//	}
	
}
