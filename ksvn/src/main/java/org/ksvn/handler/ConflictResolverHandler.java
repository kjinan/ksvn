package org.ksvn.handler;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNConflictHandler;
import org.tmatesoft.svn.core.wc.SVNConflictChoice;
import org.tmatesoft.svn.core.wc.SVNConflictDescription;
import org.tmatesoft.svn.core.wc.SVNConflictResult;
import org.tmatesoft.svn.core.wc.SVNMergeFileSet;

public class ConflictResolverHandler implements ISVNConflictHandler {

	public SVNConflictResult handleConflict(SVNConflictDescription conflictDescription) throws SVNException {		
		SVNMergeFileSet mergeFiles = conflictDescription.getMergeFiles();
		System.err.println("发现冲突1:"+ mergeFiles.getWCFile());
		
		SVNConflictChoice choice = SVNConflictChoice.MINE_FULL;
		return new SVNConflictResult(choice, mergeFiles.getResultFile());

	}
}