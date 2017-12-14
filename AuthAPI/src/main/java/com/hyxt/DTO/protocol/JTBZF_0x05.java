package com.hyxt.DTO.protocol;

/**
 * @Description 链路控制
 * @author jakiro
 * @version V1.0
 * @Date 2016年6月7日 下午4:10:47
 * @mail terrorbladeyang@gmail.com
 */
public class JTBZF_0x05 extends JTBZFBody{

	public JTBZF_0x05() {
		setId((byte)0x05);
	}
	
	private int platID;
	
    private byte masterChannel;
    
    private byte slaveChannel;

	public int getPlatID() {
		return platID;
	}

	public void setPlatID(int platID) {
		this.platID = platID;
	}

	public byte getMasterChannel() {
		return masterChannel;
	}

	public void setMasterChannel(byte masterChannel) {
		this.masterChannel = masterChannel;
	}

	public byte getSlaveChannel() {
		return slaveChannel;
	}

	public void setSlaveChannel(byte slaveChannel) {
		this.slaveChannel = slaveChannel;
	}
    
}
