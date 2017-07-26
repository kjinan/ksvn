package org.ksvn.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNRevisionRange;
import org.tmatesoft.svn.core.wc.SVNWCClient;

public class SvnUtils {
	/**
	 * 进行merge操作
	 * 
	 * @throws Exception
	 */
	public static void doMerge(SVNDiffClient diffClient) throws Exception {
		// 参数
		SVNURL srcUrl = SVNURL.parseURIEncoded(ConfigConstants.svnTrunkUrl);
		SVNRevision pegRevision = SVNRevision.create(1);
		Collection<SVNRevisionRange> revisions = genRevisions();
		java.io.File dstPath = new File(ConfigConstants.localBranchPath);
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
	public static void doRevert(SVNWCClient svnWcClient) throws Exception {
		System.out.println("#################### revert开始 ####################");
		File[] filenames = new File[1];
		filenames[0] = new File(ConfigConstants.localBranchPath);
		svnWcClient.doRevert(filenames, SVNDepth.INFINITY, null);
		System.out.println("#################### revert结束 ####################");
	}

	public static Collection<SVNRevisionRange> genRevisions() throws Exception {
		Collection<SVNRevisionRange> revisions = new ArrayList<SVNRevisionRange>();
		List<Integer> svnNums = FileUtils.readFile(ConfigConstants.localSvnNumFile);
		for (int svnNum : svnNums) {
			revisions.add(new SVNRevisionRange(SVNRevision.create(svnNum - 1), SVNRevision.create(svnNum)));
		}
		return revisions;
	}
}
