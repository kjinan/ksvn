package org.ksvn;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.ksvn.handler.ConflictResolverHandler;
import org.ksvn.handler.MergeEventHandler;
import org.ksvn.utils.FileUtils;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNRevisionRange;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SvnMerge {
	// svn的用户名、密码、trunk地址
	private static String svnUsername = "kongxs";
	private static String svnPassword = "kongxs";
	private static String svnTrunkUrl = "https://PC-201610091221/svn/mysvn/trunk";
	// 本地检出的svn分支路径
	private static String localBranchPath = "D:\\svn\\01_test\\";
	// svn版本号文件路径
	private static String localSvnNumFile = "C:\\Users\\Administrator\\Desktop\\svn.txt";

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
		clientManager = SVNClientManager.newInstance(svnClientOptions, svnUsername, svnPassword);

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

	/**
	 * 进行merge操作
	 * 
	 * @throws Exception
	 */
	public static void doMerge() throws Exception {
		// 参数
		SVNURL srcUrl = SVNURL.parseURIEncoded(svnTrunkUrl);
		SVNRevision pegRevision = SVNRevision.create(1);
		Collection<SVNRevisionRange> revisions = genRevisions();
		java.io.File dstPath = new File(localBranchPath);
		SVNDepth svnDepth = SVNDepth.INFINITY; // 递归遍历
		boolean useAncestry = true; // 比较差异时会考虑路径祖先
		boolean force = true; // 强制
		boolean dryRun = false; // true表示现验证能否merge成功；
		boolean recordOnly = false; // true:表示值维护svn记录，不实际操作文件；false表示实际操作文件。我们需要的是false

		System.out.println("#################### merge开始 ####################");
		diffClient.doMerge(srcUrl, pegRevision, revisions, dstPath, svnDepth, useAncestry, force, dryRun, recordOnly);
		System.out.println("#################### merge结束 ####################");

	}

	/**
	 * 进行revert操作
	 * 
	 * @throws Exception
	 */
	public static void doRevert() throws Exception {
		System.out.println("#################### revert开始 ####################");
		File[] filenames = new File[1];
		filenames[0] = new File(localBranchPath);
		svnWcClient.doRevert(filenames, SVNDepth.INFINITY, null);
		System.out.println("#################### revert结束 ####################");
	}

	public static Collection<SVNRevisionRange> genRevisions() throws Exception {
		Collection<SVNRevisionRange> revisions = new ArrayList<SVNRevisionRange>();
		List<Integer> svnNums = FileUtils.readFile(localSvnNumFile);
		for (int svnNum : svnNums) {
			revisions.add(new SVNRevisionRange(SVNRevision.create(svnNum - 1), SVNRevision.create(svnNum)));
		}
		return revisions;
	}

	public static void main(String[] args) throws Exception {
		init(); 
		doMerge();
		doRevert();
	}
}
