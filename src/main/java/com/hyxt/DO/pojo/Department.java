package com.hyxt.DO.pojo;

import java.sql.Date;
import java.sql.Timestamp;

public class Department {
	private String org_code;
	private String org_name;
	private String gerp_code;
	private int plate_id;
	private String contacter;
	private String phone;
	private int plateinner_id;
	private String remark;
	private int deleted;
	private int city_id;
	private String address;
	private double lon;
	private double lat;
	private int index_id;
	private Timestamp record_time;
	private String recordor;
	private String recordor_name;
	private Date modify_time;
	private String license;
	
	

	/**
	 * @Description 获得 license
	 */
	public String getLicense() {
		return license;
	}

	/**
	 * @Description 设置 license 
	 */
	public void setLicense(String license) {
		this.license = license;
	}

	public String getOrg_code() {
		return org_code;
	}

	public void setOrg_code(String org_code) {
		this.org_code = org_code;
	}

	public String getOrg_name() {
		return org_name;
	}

	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}

	public String getGerp_code() {
		return gerp_code;
	}

	public void setGerp_code(String gerp_code) {
		this.gerp_code = gerp_code;
	}

	public int getPlate_id() {
		return plate_id;
	}

	public void setPlate_id(int plate_id) {
		this.plate_id = plate_id;
	}

	public String getContacter() {
		return contacter;
	}

	public void setContacter(String contacter) {
		this.contacter = contacter;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getPlateinner_id() {
		return plateinner_id;
	}

	public void setPlateinner_id(int plateinner_id) {
		this.plateinner_id = plateinner_id;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public int getCity_id() {
		return city_id;
	}

	public void setCity_id(int city_id) {
		this.city_id = city_id;
	}

	

	/**
	 * @Description 获得 address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @Description 设置 address 
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public int getIndex_id() {
		return index_id;
	}

	public void setIndex_id(int index_id) {
		this.index_id = index_id;
	}

	public Timestamp getRecord_time() {
		return record_time;
	}

	public void setRecord_time(Timestamp record_time) {
		this.record_time = record_time;
	}

	public String getRecordor() {
		return recordor;
	}

	public void setRecordor(String recordor) {
		this.recordor = recordor;
	}

	public String getRecordor_name() {
		return recordor_name;
	}

	public void setRecordor_name(String recordor_name) {
		this.recordor_name = recordor_name;
	}

	public Date getModify_time() {
		return modify_time;
	}

	public void setModify_time(Date modify_time) {
		this.modify_time = modify_time;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getModifier_name() {
		return modifier_name;
	}

	public void setModifier_name(String modifier_name) {
		this.modifier_name = modifier_name;
	}

	public Integer getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Integer org_id) {
		this.org_id = org_id;
	}

	public int getIs_hide() {
		return is_hide;
	}

	public void setIs_hide(int is_hide) {
		this.is_hide = is_hide;
	}

	public String getErp_code() {
		return erp_code;
	}

	public void setErp_code(String erp_code) {
		this.erp_code = erp_code;
	}

	public String getData_from() {
		return data_from;
	}

	public void setData_from(String data_from) {
		this.data_from = data_from;
	}

	public String getTemp_org_code() {
		return temp_org_code;
	}

	public void setTemp_org_code(String temp_org_code) {
		this.temp_org_code = temp_org_code;
	}

	private String modifier;
	private String modifier_name;
	private Integer org_id;
	private int is_hide;
	private String erp_code;
	private String data_from;
	private String temp_org_code;
}
