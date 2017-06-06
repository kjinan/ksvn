package org.ksvn.crms.test;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class CrmsTest {
	public static void main(String[] args) throws Exception {
		DAVRepositoryFactory.setup();

		String url = "https://10.1.5.159/svn/crms/trunk/crms";
		String name = "kongxiangsheng";
		char[] password = "2008Kxsh@yinkder".toCharArray();

		// 权限校验
		SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
		repository.setAuthenticationManager(authManager);

		// 得到历史记录
		long startRevision = 16617;
		showHistory(repository, startRevision);
	}

	/**
	 * 显示svn历史记录
	 * 
	 * @param repository
	 * @param startRevision
	 * @throws Exception
	 */
	public static void showHistory(SVNRepository repository, long startRevision) throws Exception {
		showHistory(repository, startRevision, -1);
	}

	/**
	 * 显示svn历史记录
	 * 
	 * @param repository
	 * @param startRevision
	 * @param endRevision
	 * @throws Exception
	 */
	public static void showHistory(SVNRepository repository, long startRevision, long endRevision) throws Exception {
		Collection logEntries = repository.log(new String[] { "" }, null, startRevision, endRevision, true, true);
		for (Iterator entries = logEntries.iterator(); entries.hasNext();) {
			SVNLogEntry logEntry = (SVNLogEntry) entries.next();
			System.out.println("---------------------------------------------");
			System.out.println("revision: " + logEntry.getRevision());
			System.out.println("author: " + logEntry.getAuthor());
			System.out.println("date: " + logEntry.getDate());
			System.out.println("log message: " + logEntry.getMessage());

			if (logEntry.getChangedPaths().size() > 0) {
				System.out.println();
				System.out.println("changed paths:");
				Set changedPathsSet = logEntry.getChangedPaths().keySet();

				for (Iterator changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
					SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
					System.out.println(" " + entryPath.getType() + " " + entryPath.getPath()
							+ ((entryPath.getCopyPath() != null) ? " (from " + entryPath.getCopyPath() + " revision " + entryPath.getCopyRevision() + ")" : ""));
				}
			}
		}
	}
}
