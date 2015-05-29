package org.testeditor.teamshare.git.util;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitHelper {
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Repository openJGitCookbookRepository() throws IOException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.readEnvironment() // scan environment
															// GIT_* variables
				.findGitDir() // scan up the file system tree
				.build();
		return repository;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Repository createNewRepository() throws IOException {
		// prepare a new folder
		File localPath = File.createTempFile("TestGitRepository", "");
		localPath.delete();

		// create the directory
		Repository repository = FileRepositoryBuilder.create(new File(localPath, ".git"));
		repository.create();

		return repository;
   }

}
