package com.hyxt.DTO.protocol;

import java.util.List;

/**
 * @Description 平台链路信息(0x82) 方向:数据转发服务平台->车辆管理系统 周期:定时 如30s。
 * @author jakiro
 * @version V1.0
 * @Date 2016年5月20日 上午9:20:54
 * @mail terrorbladeyang@gmail.com
 */
public class JTBZF_0x82 extends JTBZFBody {
	
	public JTBZF_0x82() {
		setId((byte)0x82);
	}

	//获取时间
	private long longtime; 
	//数据项数
	private byte item;
	//数据列表
	private List<DataIterm> dataItermList;
	
	
	public class DataIterm{
		
		//采用行政区划编号作为主键6位行政区域编码 如 110000
		private int platId;
		//链路情况 [主,从链路都断开 0],[主正常,从断开 1],[从正常,主断开 2],[主,从链路都正常 3]
		private byte item;
		public int getPlatId() {
			return platId;
		}
		public void setPlatId(int platId) {
			this.platId = platId;
		}
		public byte getItem() {
			return item;
		}
		public void setItem(byte item) {
			this.item = item;
		}
	}


	public long getLongtime() {
		return longtime;
	}

	public void setLongtime(long longtime) {
		this.longtime = longtime;
	}

	public byte getItem() {
		return item;
	}

	public void setItem(byte item) {
		this.item = item;
	}

	public List<DataIterm> getDataItermList() {
		return dataItermList;
	}

	public void setDataItermList(List<DataIterm> dataItermList) {
		this.dataItermList = dataItermList;
	}
	
}
