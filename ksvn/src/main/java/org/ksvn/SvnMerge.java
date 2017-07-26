package org.ksvn;

import org.ksvn.handler.ConflictResolverHandler;
import org.ksvn.handler.MergeEventHandler;
import org.ksvn.utils.ConfigConstants;
import org.ksvn.utils.SvnUtils;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SvnMerge {
	// 需要使用的全局变量
	private static DefaultSVNOptions svnClientOptions = null;
	private static SVNClientManager clientManager = null;
	private static SVNDiffClient diffClient = null;
	private static SVNWCClient svnWcClient = null;

	/**
	 * 初始化client
	 */
	public static void init() {
		DAVRepositoryFactory.setup();
		// 获取SVNClientManager
		svnClientOptions = SVNWCUtil.createDefaultOptions(true); // 驱动选项
		clientManager = SVNClientManager.newInstance(svnClientOptions, ConfigConstants.svnUsername, ConfigConstants.svnPassword);

		// 获取SVNDiffClient
		diffClient = clientManager.getDiffClient();
		diffClient.setIgnoreExternals(false);
		diffClient.setEventHandler(new MergeEventHandler());

		// 获取SVNWCClient
		svnWcClient = clientManager.getWCClient();
		svnWcClient.setIgnoreExternals(false);

		// 设置SVNDiffClient的冲突解决句柄
		DefaultSVNOptions diffClientOptions = (DefaultSVNOptions) diffClient.getOptions();
		diffClientOptions.setConflictHandler(new ConflictResolverHandler());
	}






	public static void main(String[] args) throws Exception {
		init(); 
		SvnUtils.doMerge(diffClient);
		SvnUtils.doRevert(svnWcClient);
	}
}
