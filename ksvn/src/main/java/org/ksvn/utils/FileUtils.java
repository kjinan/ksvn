package org.ksvn.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {
	/**
	 * 读取svn版本号所在文件
	 * 
	 * @param svnNumFilePath
	 * @return
	 * @throws Exception
	 */
	public static List<Integer> readFile(String svnNumFilePath) throws Exception {
		List<Integer> svnNums = new ArrayList<Integer>();
		BufferedReader reader = new BufferedReader(new FileReader(new File(svnNumFilePath)));
		String lineStr = null;
		int line = 1;
		while ((lineStr = reader.readLine()) != null) {
			if (null != lineStr && !"".equals(lineStr)) {
				if (!isNumber(lineStr)) {
					System.err.println("line " + line + ": " + lineStr + "不是数字！");
				} else {
					svnNums.add(Integer.parseInt(lineStr));
				}
			}

			line++;
		}
		reader.close();
		Collections.sort(svnNums);
		return svnNums;
	}

	/**
	 * 判断是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
