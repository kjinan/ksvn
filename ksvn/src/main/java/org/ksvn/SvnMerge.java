package org.ksvn;

import org.ksvn.utils.SvnUtils;

public class SvnMerge {
	public static void main(String[] args) throws Exception {
		SvnUtils.doMerge();
		SvnUtils.doRevert();
	}
}
