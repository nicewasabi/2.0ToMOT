package com.hyxt.utils;

public class Color {

	public static String conColor(String color) {
		// 部标 1 蓝色,2 黄色,3 黑色,4 白色
		// 系统 白色 1 蓝色 2 黄色 3 黑色 4
		if ("3".equals(color)) {
			return "2";
		} else if ("2".equals(color)) {
			return "1";
		} else if ("4".equals(color)) {
			return "3";
		} else if ("1".equals(color)) {
			return "4";
		} else {
			return "2";
		}
	}

	public static String abcColor(String color) {

		if ("2".equals(color)) {
			return "3";
		} else if ("1".equals(color)) {
			return "2";
		} else if ("3".equals(color)) {
			return "4";
		} else if ("4".equals(color)) {
			return "1";
		} else {
			return "3";
		}
	}
}
