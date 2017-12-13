/**
 * 
 */
package com.hyxt.DTO.protocol;

/**
 * @author hadoop
 * 
 */
public class JTBZFHeader {

	private byte id;
	private int num;
	private short length;
	private byte check;

	/**
	 * @Description 获得 id
	 */
	public byte getId() {
		return id;
	}

	/**
	 * @Description 设置 id
	 */
	public void setId(byte id) {
		this.id = id;
	}

	/**
	 * @Description 获得 num
	 */
	public int getNum() {
		return num;
	}

	/**
	 * @Description 设置 num
	 */
	public void setNum(int num) {
		this.num = num;
	}


	/**
	 * @Description 获得 length
	 */
	public short getLength() {
		return length;
	}

	/**
	 * @Description 设置 length 
	 */
	public void setLength(short length) {
		this.length = length;
	}

	/**
	 * @Description 获得 check
	 */
	public byte getCheck() {
		return check;
	}

	/**
	 * @Description 设置 check
	 */
	public void setCheck(byte check) {
		this.check = check;
	}

}
