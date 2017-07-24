package org.ksvn.svnkit.highlevel.demo;

import java.util.ArrayList;
import java.util.List;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SvnKitHighLevelDemo {
	private static SVNClientManager ourClientManager;

	public static void main(String[] args) throws Exception {
		// 对版本库进行初始化操作(在用版本库进行其他操作前必须进行初始化)
		SVNRepositoryFactoryImpl.setup();

		// 创建SVNClientManager的实例。提供认证信息（用户名，密码） 和驱动选项。
		SVNURL url = SVNURL.parseURIEncoded("https://PC-201610091221/svn/svnkit/trunk/nodeA");// Url
		String userName = "kongxs";// 用户名
		String password = "kongxs";// 密码
		ISVNOptions options = SVNWCUtil.createDefaultOptions(true);// 驱动选项
		ourClientManager = SVNClientManager.newInstance((DefaultSVNOptions) options, userName, password);

		// 得到最新的版本号
		SVNWCClient wcClient = ourClientManager.getWCClient();
		SVNInfo svnInfo = wcClient.doInfo(url, SVNRevision.HEAD, SVNRevision.HEAD);
		long lastRevision = svnInfo.getCommittedRevision().getNumber();
		System.out.println(lastRevision);

		// 显示历史
		SVNLogClient logClient = ourClientManager.getLogClient();
		SimpleISVNLogEntryHandler handler = new SimpleISVNLogEntryHandler(); // 用于保存log日志
		SVNRevision startRevision = SVNRevision.create(0);
		SVNRevision endRevision = SVNRevision.create(lastRevision);
		long showLimit = 10; // 最多显示10条记录
		boolean stopOnCopy = false; // true表示跳过复制的历史记录（起始没明白什么意思~）
		boolean discoverChangedPaths = true; // 是否检索变更目录及文件
		logClient.doLog(url, new String[] {}, endRevision, startRevision, endRevision, stopOnCopy, discoverChangedPaths, showLimit, handler);
		if (null != handler.list && handler.list.size() > 0) {
			System.out.println("变更日志不为空。");
		} else {
			System.out.println("变更日志为空。");
		}

	}
}

/**
 * 用于将svnLog写入其list属性
 * 
 * @author Administrator
 *
 */
class SimpleISVNLogEntryHandler implements ISVNLogEntryHandler {
	public List<SVNLogEntry> list = new ArrayList<SVNLogEntry>();

	public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
		list.add(logEntry);
	}

}
