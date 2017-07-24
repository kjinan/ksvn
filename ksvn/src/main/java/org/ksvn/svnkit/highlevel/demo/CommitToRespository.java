package org.ksvn.svnkit.highlevel.demo;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * lowLevel示例
 * 
 * @author Administrator
 *
 */
public class CommitToRespository {
	public static void main(String[] args) throws Exception {
		// 根据访问协议初始化工厂
		FSRepositoryFactory.setup();
		// 初始化仓库，即初始化root节点
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
			System.out.println("访问地址[" + url + "]不存在，程序退出！");
			System.exit(1);
		} else if (nodeKind == SVNNodeKind.FILE) {
			System.out.println("访问地址[" + url + "]是个文件而不是目录，程序退出！");
			System.exit(1);
		} else if (nodeKind == SVNNodeKind.DIR) {
			System.out.println("访问地址[" + url + "]是个目录。");
		}

		// 得到提交前的最新版本号
		long latestRevision = repository.getLatestRevision();
		System.out.println("资源最新版本号 (提交之前): " + latestRevision);

		// 添加目录和文件
		ISVNEditor editor = repository.getCommitEditor("测试添加目录和文件", null); // 第一个参数表示要提交时的备注内容
		try {
			byte[] contents = "我爱中国".getBytes();
			SVNCommitInfo commitInfo = addDir(editor, "test", "test/file.txt", contents);
			System.out.println("目录添加成功: " + commitInfo);
		} catch (SVNException svne) {
			editor.abortEdit();
			throw svne;
		}

		// 修改文件
		editor = repository.getCommitEditor("测试修改文件", null);
		try {
			byte[] contents = "我爱中华人民共和国".getBytes();
			byte[] modifiedContents = "我爱中华人民共和国，也爱中国共产党。".getBytes();
			SVNCommitInfo commitInfo = modifyFile(editor, "test", "test/file.txt", contents, modifiedContents);
			System.out.println("文件修改成功: " + commitInfo);
		} catch (SVNException svne) {
			editor.abortEdit();
			throw svne;
		}

		// 目录复制
		editor = repository.getCommitEditor("测试复制目录", null);
		String absoluteSrcPath = repository.getRepositoryPath("test");
		long srcRevision = repository.getLatestRevision();
		try {
			SVNCommitInfo commitInfo = copyDir(editor, absoluteSrcPath, "test2", srcRevision);
			System.out.println("目录复制成功: " + commitInfo);
		} catch (SVNException svne) {
			editor.abortEdit();
			throw svne;
		}

		// 目录删除
		editor = repository.getCommitEditor("测试删除目录", null);
		try {
			SVNCommitInfo commitInfo = deleteDir(editor, "test");
			System.out.println("目录删除成功: " + commitInfo);
		} catch (SVNException svne) {
			editor.abortEdit();
			throw svne;
		}

		// 删除刚才复制的目录
		editor = repository.getCommitEditor("测试删除刚才复制的目录", null);
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

	/**
	 * 新增目录
	 * 
	 * @param editor 编辑器
	 * @param revisionNo 修订版本号
	 * @return SVNCommitInfo 提交结果信息
	 * @throws SVNException
	 */
	private static SVNCommitInfo addDir(ISVNEditor editor, long revisionNo) throws SVNException {
		// 进入Root节点，即nodeB
		editor.openRoot(revisionNo);
		// 新增目录
		editor.addDir("nodeD", null, revisionNo);
		editor.closeDir();// nodeD
		editor.closeDir();// nodeB
		return editor.closeEdit();
	}

	/**
	 * 新增文件
	 * 
	 * @param editor
	 * @param revisionNo
	 * @return
	 * @throws SVNException
	 */
	private static SVNCommitInfo addFile(ISVNEditor editor, long revisionNo) throws SVNException {
		// 进入Root节点，即nodeB
		editor.openRoot(revisionNo);
		// .进入nodeC节点
		editor.openDir("nodeC", revisionNo);
		// 新增itemC2文件
		editor.addFile("nodeC/itemC2", null, revisionNo);
		// 确保客户端这个文件的内容和服务端的是一样的，如果不一致的话是不允许提交的。底层实现使用MD5
		String itemC2Path = "nodeC/itemC2";
		String baseChecksum = null;
		editor.applyTextDelta(itemC2Path, baseChecksum);
		// 提交文件变更的数据,windows默认是100kb大小
		byte[] oldData = new byte[] {};// 旧数据
		byte[] newData = null;// 新数据
		try {
			newData = "我来测试一下 - addFile".getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		ByteArrayInputStream baseData = new ByteArrayInputStream(oldData);
		ByteArrayInputStream workingData = new ByteArrayInputStream(newData);
		SVNDeltaGenerator svnDeltaGenerator = new SVNDeltaGenerator();// 100KB-windows
																		// generator
		String checksum = svnDeltaGenerator.sendDelta(itemC2Path, baseData, 0, workingData, editor, true);
		// 设置文件的属性，key是字符串，值被包装成SVNProperyValue了
		editor.changeFileProperty("nodeC/itemC2", "properName1", SVNPropertyValue.create("properValue1"));
		editor.changeFileProperty("nodeC/itemC2", "properName2", SVNPropertyValue.create("properValue2"));
		System.out.println("checksum:" + checksum);
		// 关闭文件
		editor.closeFile("nodeC/itemC2", checksum);
		// 关闭目录nodeC
		editor.closeDir();
		// 关闭root
		editor.closeDir();
		return editor.closeEdit();
	}

	/**
	 * 编辑文件
	 * 
	 * @param editor 编辑器
	 * @param revisionNo 修订版版本号
	 * @return SVNCommitInfo 提交结果信息
	 * @throws SVNException
	 */
	private static SVNCommitInfo modifyFile(ISVNEditor editor, long revisionNo) throws SVNException {
		// 进入Root节点，即nodeB
		editor.openRoot(revisionNo);
		// .进入nodeC节点
		editor.openDir("nodeC", revisionNo);
		// 编辑nodeC/itemC1的内容
		String itemC1Path = "nodeC/itemC1";// 路径都是相对于root的
		editor.openFile(itemC1Path, revisionNo);
		// 确保客户端这个文件的内容和服务端的是一样的，如果不一致的话是不允许提交的。底层实现使用MD5
		String baseChecksum = null;
		editor.applyTextDelta(itemC1Path, baseChecksum);
		// 提交文件变更的数据,windows默认是100kb大小
		byte[] oldData = new byte[] {};
		byte[] newData = null;
		try {
			newData = "我来测试一下编辑2".getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		ByteArrayInputStream baseData = new ByteArrayInputStream(oldData);
		ByteArrayInputStream workingData = new ByteArrayInputStream(newData);
		SVNDeltaGenerator svnDeltaGenerator = new SVNDeltaGenerator();// 100KB-windows
																		// generator
		String checksum = svnDeltaGenerator.sendDelta(itemC1Path, baseData, 0, workingData, editor, true);
		// 关闭文件
		editor.closeFile(itemC1Path, checksum);
		// 关闭目录nodeC
		editor.closeDir();
		// 关闭根目录nodeB
		editor.closeDir();
		// 关闭编辑器，并返回执行结果
		return editor.closeEdit();
	}

	/**
	 * 删除文件
	 * 
	 * @param editor 编辑器
	 * @param revisionNo 修订版版本号
	 * @return SVNCommitInfo 提交结果信息
	 * @throws SVNException
	 */
	private static SVNCommitInfo deleteFile(ISVNEditor editor, long revisionNo) throws SVNException {
		// 进入Root节点，即nodeB
		editor.openRoot(revisionNo);
		// 4.3.删除文件
		editor.deleteEntry("itemB1", revisionNo);
		// 操作完成要关闭编辑器，并返回操作结果
		return editor.closeEdit();
	}

}
