package com.hyxt.DO.pojo;

import java.util.List;

public class Vehicle {
	private int vehicle_id;// id 车辆id
	private String vcode;
	private String id_number;// vin 车牌号
	private String color_id_number_id;// color 车牌颜色
	private int region_property;// distrct 车籍地
	private String transport_permits;// transportNo 道路运输证号
	private int org_id;// ownerCode 归属企业编号
	private List<SuperiorPlatInfo> plat; // 上报平台
	private String org_license; // 归属企业经营许可证

	/**
	 * @Description 获得 org_license
	 */
	public String getOrg_license() {
		return org_license;
	}

	/**
	 * @Description 设置 org_license
	 */
	public void setOrg_license(String org_license) {
		this.org_license = org_license;
	}

	public String getVcode() {
		return vcode;
	}

	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

	public List<SuperiorPlatInfo> getPlat() {
		return plat;
	}

	public void setPlat(List<SuperiorPlatInfo> plat) {
		this.plat = plat;
	}

	public int getVehicle_id() {
		return vehicle_id;
	}

	public void setVehicle_id(int vehicle_id) {
		this.vehicle_id = vehicle_id;
	}

	public String getId_number() {
		return id_number;
	}

	public void setId_number(String id_number) {
		this.id_number = id_number;
	}

	public String getColor_id_number_id() {
		return color_id_number_id;
	}

	public void setColor_id_number_id(String color_id_number_id) {
		this.color_id_number_id = color_id_number_id;
	}

	public int getRegion_property() {
		return region_property;
	}

	public void setRegion_property(int region_property) {
		this.region_property = region_property;
	}

	public String getTransport_permits() {
		return transport_permits;
	}

	public void setTransport_permits(String transport_permits) {
		this.transport_permits = transport_permits;
	}

	public int getOrg_id() {
		return org_id;
	}

	public void setOrg_id(int org_id) {
		this.org_id = org_id;
	}

}
