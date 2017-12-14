package com.hyxt.DTO.protocol;

/**
 * @Description 上行认证(0x00)
 * @author jakiro
 * @version V1.0
 * @Date 2016年5月19日 下午11:59:32
 * @mail terrorbladeyang@gmail.com
 */
public class JTBZF_0x00 extends JTBZFBody {
	
	//初始化设置消息ID
	public JTBZF_0x00() {
		setId((byte)0x00);
	}
	
	//鉴权码,由转发服务平台分配
	private String code = "";

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}
