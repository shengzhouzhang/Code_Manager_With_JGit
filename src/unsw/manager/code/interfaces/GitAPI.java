package unsw.manager.code.interfaces;

import java.io.IOException;
import java.text.ParseException;

public interface GitAPI {
	
	/*
	 * Create a bare repository
	 */
	void init_repository(String filePath) throws IOException;
	
	/*
	 * Open a bare repository
	 */
	void open_repository(String filePath) throws IOException;
	
	/*
	 * Commit an Object
	 */
	void commit(String username, String email, String data, String message) throws IOException;
	
	/*
	 * Get content by object's Id
	 */
	String readObject(String sha1) throws ParseException, IOException;
	
	/*
	 * Get newest content
	 */
	String readLatest() throws IOException;
	
	/*
	 * Close repository
	 */
	void close_repository() throws IOException;
}
