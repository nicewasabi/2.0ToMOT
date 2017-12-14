package com.hyxt.DTO.protocol;

/**
 * @Description 上报运输企业信息(0x11) 方向:中石油车辆管理系统->数据转发服务平台 
 *              车辆管理系统中企业信息发生添加,修改时实时交换。
 * @author jakiro
 * @version V1.0
 * @Date 2016年5月20日 上午12:16:04
 * @mail terrorbladeyang@gmail.com
 */
public class JTBZF_0x11 extends JTBZFBody {

	public JTBZF_0x11() {
		setId((byte)0x11);
	}
	
	//主键编号
	private String code ;
	//名称
	private String name;
//	//经营许可证号
//	private String licenseNo;
	//地址
	private String address;
	//联系人
	private String person;
	//联系电话
	private String phone;
	

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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
//	public String getLicenseNo() {
//		return licenseNo;
//	}
//	public void setLicenseNo(String licenseNo) {
//		this.licenseNo = licenseNo;
//	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPerson() {
		return person;
	}
	public void setPerson(String person) {
		this.person = person;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
