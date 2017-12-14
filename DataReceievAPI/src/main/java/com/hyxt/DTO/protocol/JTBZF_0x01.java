package com.hyxt.DTO.protocol;

/**
 * @Description 上行心跳 方向:中石油管理系统->数据转发服务平台 周期1次/30s 
 * @author jakiro
 * @version V1.0
 * @Date 2016年5月20日 上午12:10:56
 * @mail terrorbladeyang@gmail.com
 */
public class JTBZF_0x01 extends JTBZFBody {

	public JTBZF_0x01() {
		setId((byte)0x01);
	}
	
}
