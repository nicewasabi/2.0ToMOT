package com.hyxt.DTO.protocol;

import java.util.List;

/**
 * @Description 上报信息车辆(0x13) 车辆管理系统->转发服务平台 车辆管理系统中车辆信息发生添加、修改时实时交换
 * @author jakiro
 * @version V1.0
 * @Date 2016年5月20日 上午12:29:40
 * @mail terrorbladeyang@gmail.com
 */
public class JTBZF_0x13 extends JTBZFBody {

	public JTBZF_0x13() {
		setId((byte)0x13);
	}
	
	//车辆ID,为了保持整洁 统一8位,从10000000开始计算
	private int vid;
	//车牌号
	private String vin;
	//车牌颜色
	private byte color;
	//车籍地,6位行政区划编码
	private int district;
	//道路运输证号
	private String transportNo;
	//归属企业编号(与Owner表具有表关联关系)
//	private int ownerCode;
	private String ownerCode;
	//转发上级平台数量
	private byte transNum;
	//转发上级平台编号,编号和Plat表具有关联关系
//	private int transplant_1;
	
    private List<Integer> list;
    
	public int getVid() {
		return vid;
	}
	public void setVid(int vid) {
		this.vid = vid;
	}
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	public byte getColor() {
		return color;
	}
	public void setColor(byte color) {
		this.color = color;
	}
	public int getDistrict() {
		return district;
	}
	public void setDistrict(int district) {
		this.district = district;
	}
	public String getTransportNo() {
		return transportNo;
	}
	public void setTransportNo(String transportNo) {
		this.transportNo = transportNo;
	}
//	public int getOwnerCode() {
//		return ownerCode;
//	}
//	public void setOwnerCode(int ownerCode) {
//		this.ownerCode = ownerCode;
//	}
	
	public byte getTransNum() {
		return transNum;
	}
	/**
	 * @Description 获得 ownerCode
	 */
	public String getOwnerCode() {
		return ownerCode;
	}
	/**
	 * @Description 设置 ownerCode 
	 */
	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	public void setTransNum(byte transNum) {
		this.transNum = transNum;
	}
	public List<Integer> getList() {
		return list;
	}
	public void setList(List<Integer> list) {
		this.list = list;
	}
	
}
