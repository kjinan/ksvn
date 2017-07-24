package org.ksvn.svnkit.highlevel.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

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

public class TestSvnMerge {
	public static void main(String[] args) throws Exception {
		DAVRepositoryFactory.setup();

		// 用户名密码
		String url = "https://PC-201610091221/svn/mysvn/branch/01_test";
		String name = "kongxs";
		String password = "kongxs";

		// 获取SVNClientManager
		DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true); // 驱动选项
		SVNClientManager clientManager = SVNClientManager.newInstance(options, name, password);

		// 获取SVNDiffClient
		MergeEventHandler eventHandler = new MergeEventHandler();
		SVNDiffClient diffClient = clientManager.getDiffClient();
		diffClient.setIgnoreExternals(false);
		diffClient.setEventHandler(eventHandler);

		// 参数
		SVNURL url1 = SVNURL.parseURIEncoded("https://PC-201610091221/svn/mysvn/trunk");
		SVNRevision pegRevision = SVNRevision.create(10);
		Collection<SVNRevisionRange> revisions = new ArrayList<SVNRevisionRange>();
		revisions.add(new SVNRevisionRange(SVNRevision.create(10), SVNRevision.create(11)));
		java.io.File dstPath = new File("D:\\svn\\01_test\\");
		SVNDepth svnDepth = SVNDepth.INFINITY; // 递归遍历
		boolean useAncestry = true; // 比较差异时会考虑路径祖先
		boolean force = true; // 强制
		boolean dryRun = false; // true表示现验证能否merge成功；
		boolean recordOnly = true; // 只是把文件merge到目录下，其实还是需要提交的。

		System.out.println("begin");
		diffClient.doMerge(url1, pegRevision, revisions, dstPath, svnDepth, useAncestry, force, dryRun, recordOnly);
		System.out.println("end");

		// revert回去
		SVNWCClient svnWcClient = clientManager.getWCClient();
		svnWcClient.setIgnoreExternals(true);
		
		// revert
		System.out.println("begin to revert");
		File[] filenames=new File[1];
		filenames[0]=new File("D:\\svn\\01_test\\");
		svnWcClient.doRevert(filenames, SVNDepth.UNKNOWN, null);
		System.out.println("revert finished");

	}
}
