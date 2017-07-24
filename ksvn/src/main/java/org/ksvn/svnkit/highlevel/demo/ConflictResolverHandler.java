package org.ksvn.svnkit.highlevel.demo;

import java.util.Scanner;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNConflictHandler;
import org.tmatesoft.svn.core.wc.SVNConflictChoice;
import org.tmatesoft.svn.core.wc.SVNConflictDescription;
import org.tmatesoft.svn.core.wc.SVNConflictReason;
import org.tmatesoft.svn.core.wc.SVNConflictResult;
import org.tmatesoft.svn.core.wc.SVNMergeFileSet;

public class ConflictResolverHandler implements ISVNConflictHandler {

	public SVNConflictResult handleConflict(SVNConflictDescription conflictDescription) throws SVNException {

		SVNConflictReason reason = conflictDescription.getConflictReason();
		SVNMergeFileSet mergeFiles = conflictDescription.getMergeFiles();

		System.out.println("Conflict discovered in:" + mergeFiles.getWCFile());
		// System.out.println(reason);
		System.out.print("Select: (p) postpone, (mf) mine-full, (tf) theirs-full     ");

		SVNConflictChoice choice = SVNConflictChoice.POSTPONE;

		Scanner reader = new Scanner(System.in);
		if (reader.hasNextLine()) {
			String sVNConflictChoice = reader.nextLine();
			if (sVNConflictChoice.equalsIgnoreCase("mf")) {
				choice = SVNConflictChoice.MINE_FULL;
			} else if (sVNConflictChoice.equalsIgnoreCase("tf")) {
				choice = SVNConflictChoice.THEIRS_FULL;
			}
		}
		reader.close();

		return new SVNConflictResult(choice, mergeFiles.getResultFile());

	}
}