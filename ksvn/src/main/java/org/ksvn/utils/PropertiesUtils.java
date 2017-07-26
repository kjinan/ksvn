package org.ksvn.utils;

import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtils {
	private static Properties prop = new Properties();

	static {
		InputStreamReader isr = null;
		try {
			String classzName = new Object() {
				public String getClassName() {
					String clazzName = this.getClass().getName();
					return clazzName.substring(0, clazzName.lastIndexOf('$'));
				}
			}.getClassName();
			isr = new InputStreamReader(Class.forName(classzName).getResourceAsStream("/config.properties"), "UTF-8");
			prop.load(isr);
			isr.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (isr != null) {
					isr.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String getString(String key) {
		String value = prop.getProperty(key, "");
		return value;
	}
}
