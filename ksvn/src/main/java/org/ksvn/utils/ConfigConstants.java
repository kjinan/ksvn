package org.ksvn.utils;

import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class ConfigConstants {
	// svn的用户名、密码、trunk地址
	public static String svnUsername;
	public static String svnPassword;
	public static String svnTrunkUrl;
	// 本地检出的svn分支路径
	public static String localBranchPath;
	// svn版本号文件路径
	public static String localSvnNumFile;
	

	public static SVNClientManager clientManager;

	static {
		// 配置的参数
		svnUsername = PropertiesUtils.getString("svn.username");
		svnPassword = PropertiesUtils.getString("svn.password");
		svnTrunkUrl = PropertiesUtils.getString("svn.trunk.url");
		localBranchPath = PropertiesUtils.getString("svn.branch.localpath");
		localSvnNumFile = PropertiesUtils.getString("svn.local.svnNum.file");
		
		// 得到SvnClientManager
		DAVRepositoryFactory.setup();
		DefaultSVNOptions svnClientOptions = SVNWCUtil.createDefaultOptions(true); // 驱动选项
		clientManager = SVNClientManager.newInstance(svnClientOptions, ConfigConstants.svnUsername, ConfigConstants.svnPassword);
	}
}
