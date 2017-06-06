package org.ksvn.svnkit.highlevel.demo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class Test {
	public static void main(String[] args) throws Exception {
		DAVRepositoryFactory.setup();

		String url = "https://PC-201610091221/svn/svnkit/trunk";
		String name = "kongxs";
		char[] password = "kongxs".toCharArray();

		// 权限校验
		SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
		repository.setAuthenticationManager(authManager);

		// 输出根节点 和 UUID
		System.out.println("Repository Root: " + repository.getRepositoryRoot(true));
		System.out.println("Repository UUID: " + repository.getRepositoryUUID(true));

		// 判断请求的地址是文件 or 目录 or 不存在 or 为知
		SVNNodeKind nodeKind = repository.checkPath("", -1);
		if (nodeKind == SVNNodeKind.NONE) {
			System.err.println("请求的地址[" + url + "]不存在");
			System.exit(1);
		} else if (nodeKind == SVNNodeKind.FILE) {
			System.out.println("请求的地址[" + url + "]是个文件");

		} else if (nodeKind == SVNNodeKind.DIR) {
			System.out.println("请求的地址[" + url + "]是个目录");
		} else {
			System.err.println("请求的地址[" + url + "]信息未知");
			System.exit(1);
		}

		// 列出目录树
		listEntries(repository, "");

		// 显示最新版本（显示的是整个svn的最新版本，即从根节点看的最新版本，而不是请求地址的最新版版）
		long latestRevision = repository.getLatestRevision();
		System.out.println("Repository latest revision: " + latestRevision);

		// 显示文件内容
		printFiles(repository, "nodeA/itemA1.txt");

		// 得到历史记录
		long startRevision = 0;
		showHistory(repository, startRevision);

	}

	/**
	 * 打印svn资源的层级结构(注意path只能是url的第一级子目录的文件或目录)
	 * 
	 * @param repository
	 * @param path
	 * @throws SVNException
	 */
	public static void listEntries(SVNRepository repository, String path) throws SVNException {
		Collection entries = repository.getDir(path, -1, null, (Collection) null);
		Iterator iterator = entries.iterator();
		while (iterator.hasNext()) {
			SVNDirEntry entry = (SVNDirEntry) iterator.next();
			System.out.println("/" + (path.equals("") ? "" : path + "/") + entry.getName() + " ( author: '" + entry.getAuthor() + "'; revision: " + entry.getRevision() + "; date: " + entry.getDate()
					+ ")");
			if (entry.getKind() == SVNNodeKind.DIR) {
				listEntries(repository, (path.equals("")) ? entry.getName() : path + "/" + entry.getName());
			}
		}
	}

	/**
	 * 显示文件内容
	 * 
	 * @param repository
	 */
	public static void printFiles(SVNRepository repository, String filePath) throws Exception {
		SVNProperties fileProperties = new SVNProperties();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		repository.getFile(filePath, -1, fileProperties, baos);

		String mimeType = fileProperties.getStringValue(SVNProperty.MIME_TYPE);
		boolean isTextType = SVNProperty.isTextMimeType(mimeType);

		Iterator<String> iterator = fileProperties.asMap().keySet().iterator();
		while (iterator.hasNext()) {
			String propertyName = (String) iterator.next();
			String propertyValue = fileProperties.getStringValue(propertyName);
			System.out.println("File property: " + propertyName + "=" + propertyValue);
		}

		if (isTextType) {
			System.out.println("File contents:");
			System.out.println();
			try {
				baos.writeTo(System.out);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} else {
			System.out.println("Not a text file.");
		}
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
