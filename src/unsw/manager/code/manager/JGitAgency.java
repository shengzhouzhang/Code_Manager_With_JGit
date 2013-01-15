package unsw.manager.code.manager;

import unsw.manager.code.interfaces.GitAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;

import org.eclipse.jgit.lib.CommitBuilder;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.RefUpdate.Result;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache.FileKey;
import org.eclipse.jgit.lib.TreeFormatter;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.FS;

public class JGitAgency implements GitAPI {
	
	private FolderManager folderManager = null;
	private Repository repository = null;
	private String fileName = "scripts";
	
	public JGitAgency(){
		folderManager = new FolderManager();
	}

	@Override
	public void init_repository(String filePath) throws IOException {
		File repoFolder = null;

		repoFolder = folderManager.CreateFolder(filePath);
		FileKey fileKey = FileKey.exact(repoFolder, FS.DETECTED);
		repository = fileKey.open(false);
		repository.create(true);
	}

	@Override
	public void open_repository(String filePath) throws IOException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		
		repository = builder.setGitDir(folderManager.FindFolder(filePath))
		  .readEnvironment() // scan environment GIT_* variables
		  .findGitDir() // scan up the file system tree
		  .build();
	}

	@Override
	public void commit(String username, String email, String data, String message) throws IOException {
		byte[] file = data.getBytes();
		ObjectInserter inserter = repository.newObjectInserter();
		try{
			ObjectId fileId = inserter.insert(Constants.OBJ_BLOB, file, 0, file.length);
			TreeFormatter formatter = new TreeFormatter();
			formatter.append(fileName, FileMode.REGULAR_FILE, fileId);
			
			//Get old Commits
			Ref oldHEAD = repository.getAllRefs().get(Constants.HEAD);
			
			
			ObjectId treeId = inserter.insert(formatter);
			PersonIdent person = new PersonIdent(username, email);
			
			CommitBuilder commit = new CommitBuilder();
	        commit.setTreeId(treeId);
	        commit.setAuthor(person);
	        commit.setCommitter(person);
	        if (oldHEAD != null)
	        	commit.setParentIds(new ObjectId[] { oldHEAD.getObjectId() });
	        commit.setMessage(message);
	        
	        ObjectId commitId = inserter.insert(commit);
	        inserter.flush();
	        
	        RefUpdate ru = repository.updateRef(Constants.HEAD);
	        ru.setForceUpdate(true);
	        ru.setRefLogIdent(person);
	        ru.setNewObjectId(commitId);
	        ru.setRefLogMessage(message, false);
	        
	        if (oldHEAD != null){
				ru.setExpectedOldObjectId(oldHEAD.getObjectId());
	        }else{
	        	ru.setExpectedOldObjectId(ObjectId.zeroId());
	        }
	        
	        Result result = ru.update();
	        
	        System.out.println("fileId: " + fileId);
	        System.out.println("treeId: " + treeId);
	        System.out.println("commitId: " + commitId);
	        System.out.println("result: " + result);
		}catch(IOException e){
			throw new IOException(e.getMessage());
		}finally{
			inserter.release();
		}
    }

	@Override
	public void close_repository() throws IOException {
		repository.close();
	}

	@Override
	public String readObject(String sha1) throws ParseException, IOException {
		ObjectId objectId = null;
		if(ObjectId.isId(sha1)){
			OutputStream out = new ByteArrayOutputStream();
			objectId = ObjectId.fromString(sha1);
			ObjectLoader loader = repository.open(objectId);
			loader.copyTo(out);
			return out.toString();
		}
		
		return null;
	}

	@Override
	public String readLatest() throws IOException {

		OutputStream out = new ByteArrayOutputStream();
		//get HEAD
		ObjectId lastCommitId = repository.resolve(Constants.HEAD);
		//get commit
		RevWalk revWalk = new RevWalk(repository);
		RevCommit commit = revWalk.parseCommit(lastCommitId);
		//get tree
		RevTree tree = commit.getTree();
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(tree);
		treeWalk.setRecursive(false);
		treeWalk.setFilter(PathFilter.create(fileName));
		if (!treeWalk.next())
			return null;
		//get blob
		ObjectId objectId = treeWalk.getObjectId(0);
		ObjectLoader loader = repository.open(objectId);
		loader.copyTo(out);
		return out.toString();
	}
}
