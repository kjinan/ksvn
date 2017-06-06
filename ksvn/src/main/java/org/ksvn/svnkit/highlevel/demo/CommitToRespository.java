package org.ksvn.svnkit.highlevel.demo;

import java.io.ByteArrayInputStream;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class CommitToRespository {
	public static void main(String[] args) throws Exception {
		FSRepositoryFactory.setup();
		SVNURL url = SVNURL.parseURIEncoded("https://PC-201610091221/svn/svnkit/trunk/nodeA");
		String userName = "kongxs";
		char[] userPassword = "kongxs".toCharArray();

		// 校验权限
		SVNRepository repository = SVNRepositoryFactory.create(url);
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, userPassword);
		repository.setAuthenticationManager(authManager);

		// 判断资源是目录还是文件
		SVNNodeKind nodeKind = repository.checkPath("", -1);
		if (nodeKind == SVNNodeKind.NONE) {
			System.out.println("No entry at URL " + url);
			System.exit(1);
		} else if (nodeKind == SVNNodeKind.FILE) {
			System.out.println("Entry at URL " + url + " is a file while directory was expected");
			System.exit(1);
		}

		// 得到提交前的最新版本号
		long latestRevision = repository.getLatestRevision();
		System.out.println("资源最新版本号 (提交之前): " + latestRevision);

		// 添加目录和文件
		ISVNEditor editor = repository.getCommitEditor("directory and file added", null);
		try {
			byte[] contents = "This is a new file".getBytes();
			SVNCommitInfo commitInfo = addDir(editor, "test", "test/file.txt", contents);
			System.out.println("目录添加成功: " + commitInfo);
		} catch (SVNException svne) {
			editor.abortEdit();
			throw svne;
		}

		// 修改文件
		editor = repository.getCommitEditor("file contents changed", null);
		try {
			byte[] contents = "This is a new file".getBytes();
			byte[] modifiedContents = "This is the same file but modified a little.".getBytes();
			SVNCommitInfo commitInfo = modifyFile(editor, "test", "test/file.txt", contents, modifiedContents);
			System.out.println("文件修改成功: " + commitInfo);
		} catch (SVNException svne) {
			editor.abortEdit();
			throw svne;
		}

		// 目录复制
		String absoluteSrcPath = repository.getRepositoryPath("test");
		long srcRevision = repository.getLatestRevision();
		editor = repository.getCommitEditor("directory copied", null);
		try {
			SVNCommitInfo commitInfo = copyDir(editor, absoluteSrcPath, "test2", srcRevision);
			System.out.println("目录复制成功: " + commitInfo);
		} catch (SVNException svne) {
			editor.abortEdit();
			throw svne;
		}

		// 目录删除
		editor = repository.getCommitEditor("directory deleted", null);
		try {
			SVNCommitInfo commitInfo = deleteDir(editor, "test");
			System.out.println("目录删除成功: " + commitInfo);
		} catch (SVNException svne) {
			editor.abortEdit();
			throw svne;
		}

		// 删除刚才复制的目录
		editor = repository.getCommitEditor("copied directory deleted", null);
		try {
			SVNCommitInfo commitInfo = deleteDir(editor, "test2");
			System.out.println("复制的目录删除成功: " + commitInfo);
		} catch (SVNException svne) {
			editor.abortEdit();
			throw svne;
		}

		latestRevision = repository.getLatestRevision();
		System.out.println("资源最新版本号 (提交之后): " + latestRevision);
	}

	/**
	 * 添加目录和文件
	 * 
	 * @param editor
	 * @param dirPath
	 * @param filePath
	 * @param data
	 * @return
	 * @throws SVNException
	 */
	private static SVNCommitInfo addDir(ISVNEditor editor, String dirPath, String filePath, byte[] data) throws SVNException {
		editor.openRoot(-1);
		editor.addDir(dirPath, null, -1);
		editor.addFile(filePath, null, -1);
		editor.applyTextDelta(filePath, null);
		SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
		String checksum = deltaGenerator.sendDelta(filePath, new ByteArrayInputStream(data), editor, true);
		editor.closeFile(filePath, checksum);
		// Closes dirPath.
		editor.closeDir();
		// Closes the root directory.
		editor.closeDir();
		return editor.closeEdit();
	}

	/**
	 * 修改文件
	 * 
	 * @param editor
	 * @param dirPath
	 * @param filePath
	 * @param oldData
	 * @param newData
	 * @return
	 * @throws SVNException
	 */
	private static SVNCommitInfo modifyFile(ISVNEditor editor, String dirPath, String filePath, byte[] oldData, byte[] newData) throws SVNException {
		editor.openRoot(-1);
		editor.openDir(dirPath, -1);
		editor.openFile(filePath, -1);
		editor.applyTextDelta(filePath, null);
		SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
		String checksum = deltaGenerator.sendDelta(filePath, new ByteArrayInputStream(oldData), 0, new ByteArrayInputStream(newData), editor, true);
		// Closes filePath.
		editor.closeFile(filePath, checksum);
		// Closes dirPath.
		editor.closeDir();
		// Closes the root directory.
		editor.closeDir();
		return editor.closeEdit();
	}

	/**
	 * 复制目录
	 * 
	 * @param editor
	 * @param srcDirPath
	 * @param dstDirPath
	 * @param revision
	 * @return
	 * @throws SVNException
	 */
	private static SVNCommitInfo copyDir(ISVNEditor editor, String srcDirPath, String dstDirPath, long revision) throws SVNException {
		editor.openRoot(-1);
		editor.addDir(dstDirPath, srcDirPath, revision);
		// Closes dstDirPath.
		editor.closeDir();
		// Closes the root directory.
		editor.closeDir();
		return editor.closeEdit();
	}

	/**
	 * 删除目录
	 * 
	 * @param editor
	 * @param dirPath
	 * @return
	 * @throws SVNException
	 */
	private static SVNCommitInfo deleteDir(ISVNEditor editor, String dirPath) throws SVNException {
		editor.openRoot(-1);
		editor.deleteEntry(dirPath, -1);
		// Closes the root directory.
		editor.closeDir();
		return editor.closeEdit();
	}
}
