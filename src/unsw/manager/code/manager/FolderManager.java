package unsw.manager.code.manager;

import java.io.File;
import java.io.IOException;

public class FolderManager {
	
	private File workspace = null;
	private File repo_folder = null;
	
	public FolderManager(){
		System.out.println("Opening directory: " + System.getProperty("catalina.base"));
		workspace = new File(System.getProperty("catalina.base"));
		
		repo_folder = new File(workspace, "repositories");
		if(!repo_folder.exists())
			repo_folder.mkdir();
	}
	
	public File CreateFolder(String folderName) throws IOException{
		
		File folder = new File(repo_folder, folderName);
		
		if (folder.exists()) {
			throw new IOException("Make sure " + folderName + " does not exist!");
	    }
		
		return folder;
	}
	
	public File FindFolder(String folderName) throws IOException{
		
		File folder = new File(repo_folder, folderName);
		
		if (!folder.exists()) {
			throw new IOException("Make sure " + folderName + " does not exist!");
	    }
		
		return folder;
	}
}
