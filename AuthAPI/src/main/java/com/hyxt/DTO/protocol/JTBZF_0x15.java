package com.hyxt.DTO.protocol;

/**
 * @Description 上报上级平台信息(0x15) 方向:车辆管理系统->数据转发服务平台 车辆管理系统中上级平台信息发生添加、修改时实时交换
 *              !!!!!数据转发服务平台收到指令后回复通用应答指令
 * @author jakiro
 * @version V1.0
 * @Date 2016年5月20日 上午8:49:59
 * @mail terrorbladeyang@gmail.com
 */
public class JTBZF_0x15 extends JTBZFBody {

	public JTBZF_0x15() {
		setId((byte)0x15);
	}
	
	//采用行政区划编号作为主键 6位行政区域编号 如110000
	private int uid;
	//上级平台名称
	private String name;
	//平台类型 1=省级平台 2=地市级平台
	private byte typeId;
	//上级平台IP
	private String ip;
	//端口
	private int port;
	//用户名
    private int userID;
    //密码
    private String password;
    //唯一接入码
    private String centerid;
    //加密参数1
    private int M1;
    //加密参数2
	private int IA1;
	//加密参数3
	private int IC1;
	//版本号 数据库存取1.0.0,协议中需要转换成010000
	private String version;
	//上级平台管理单位
	private String org;
	//上级平台联系人
	private String person;
	//上级平台联系电话
	private String phone;
	//备注信息
	private String remark;
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public byte getTypeId() {
		return typeId;
	}
	public void setTypeId(byte typeId) {
		this.typeId = typeId;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCenterid() {
		return centerid;
	}
	public void setCenterid(String centerid) {
		this.centerid = centerid;
	}
	public int getM1() {
		return M1;
	}
	public void setM1(int m1) {
		M1 = m1;
	}
	public int getIA1() {
		return IA1;
	}
	public void setIA1(int iA1) {
		IA1 = iA1;
	}
	public int getIC1() {
		return IC1;
	}
	public void setIC1(int iC1) {
		IC1 = iC1;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getOrg() {
		return org;
	}
	public void setOrg(String org) {
		this.org = org;
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
