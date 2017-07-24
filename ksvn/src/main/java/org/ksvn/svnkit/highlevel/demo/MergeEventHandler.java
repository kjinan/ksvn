package org.ksvn.svnkit.highlevel.demo;

import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNConflictHandler;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNConflictDescription;
import org.tmatesoft.svn.core.wc.SVNConflictResult;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNStatusType;

public class MergeEventHandler implements ISVNEventHandler,ISVNConflictHandler {
	public void handleEvent(SVNEvent event, double progress) throws SVNException {
		System.out.println("called");
		SVNStatusType contentsStatus = event.getContentsStatus();
		String pathChangeType = " ";
		if (contentsStatus == SVNStatusType.STATUS_MODIFIED) {
			pathChangeType = "M";
		} else if (contentsStatus == SVNStatusType.STATUS_CONFLICTED) {
			pathChangeType = "C";
		} else if (contentsStatus == SVNStatusType.STATUS_DELETED) {
			pathChangeType = "D";
		} else if (contentsStatus == SVNStatusType.STATUS_ADDED) {
			pathChangeType = "A";
		} else if (contentsStatus == SVNStatusType.CHANGED) {
			pathChangeType = "U";
		} else if (contentsStatus == SVNStatusType.STATUS_UNVERSIONED) {
			pathChangeType = "?";
		} else if (contentsStatus == SVNStatusType.STATUS_EXTERNAL) {
			pathChangeType = "X";
		} else if (contentsStatus == SVNStatusType.STATUS_IGNORED) {
			pathChangeType = "I";
		} else if (contentsStatus == SVNStatusType.STATUS_MISSING || contentsStatus == SVNStatusType.STATUS_INCOMPLETE) {
			pathChangeType = "!";
		} else if (contentsStatus == SVNStatusType.STATUS_OBSTRUCTED) {
			pathChangeType = "~";
		} else if (contentsStatus == SVNStatusType.STATUS_REPLACED) {
			pathChangeType = "R";
		} else if (contentsStatus == SVNStatusType.STATUS_NONE || contentsStatus == SVNStatusType.STATUS_NORMAL) {
			pathChangeType = " ";
		}
		if (contentsStatus != SVNStatusType.UNCHANGED && contentsStatus != SVNStatusType.INAPPLICABLE) {
			System.out.println(pathChangeType + "   " + event.getFile());
		}
	}

	public void checkCancelled() throws SVNCancelException {
		System.out.println("checkCancelled");
	}

	public SVNConflictResult handleConflict(SVNConflictDescription conflictDescription) throws SVNException {
		System.out.println("handleConflict");
		return null;
	}

}