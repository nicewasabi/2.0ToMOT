package com.hyxt.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class AreaUtil {

	private static Properties prop = new Properties();

	static {
		try {
			prop.load(AreaUtil.class.getResourceAsStream("/area.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getAreaCode(String p) {
		if(p == null){
			p = "999999";
		}
		String name = p.substring(0, 1);
		String code = prop.getProperty(name);
		if (code == null) {
			code = "999999";
		}
		return code;
	}

	public static String getVechileCode(String p, String c) {
		if(p==null){
			return "0000000000";
		}
		String areaCode = getAreaCode(p).substring(0, 2);
		String colorCode = c;
		String plateCode = "000000";
		if (p.length() > 6) {
			plateCode = p.substring(1, 7);
		}
		return 1 + colorCode + areaCode + plateCode;
	}

	public static String getWebIp(String phoneNumer) {
			          try {
			 
			           URL url = new URL("http://www.ip138.com:8080/search.asp?mobile="+phoneNumer+"&action=mobile");
			 
			           InputStream inStream = url.openStream();
			 
			            
			           String webContent = "";
			 
			           ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
			           byte[] buffer = new byte[1024];  
			           int len = -1;  
			           while ((len = inStream.read(buffer)) != -1) {  
			               outSteam.write(buffer, 0, len);  
			           }  
			           outSteam.close();  
			           inStream.close();  
			           
			           webContent = new String(outSteam.toByteArray(),"GBK");
			          int a = webContent.indexOf("卡号归属地");
			          webContent = webContent.substring(a,a+ 120);
			        //  System.out.println(webContent);
			        //  System.out.println(webContent.indexOf("nbsp"));
			          webContent = webContent.substring(0,webContent.indexOf("&nbsp"));
			          int l = webContent.lastIndexOf(">");
			          webContent = webContent.substring(l+1);
			       //  System.out.println("l"+l);
			           return webContent;
			 
			          } catch (Exception e) {
			           e.printStackTrace();
			           return "error";
			 
			          }
			    }
	public static void main(String[] args) {
//		System.out.println(AreaUtil.getVechileCode("京A88888", "2").getBytes().length);
//		System.out.println(AreaUtil.getVechileCode("京A88888", "2"));
		
		System.out.println(AreaUtil.getAreaCode("川R29222"));
		System.out.println(getWebIp("18308896900"));
	
//		try {
//			System.out.println(BytesUtil.bytesToHexString("鄂AF3531".getBytes("gbk")));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
