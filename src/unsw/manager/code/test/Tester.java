package unsw.manager.code.test;

import java.io.IOException;
import java.text.ParseException;

import org.eclipse.jgit.errors.MissingObjectException;

import unsw.manager.code.manager.*;

public class Tester {
	
	static private JGitAgency agency = new JGitAgency();
	static private String path = "testRepo5";
	
	public static void main (String args[]) {
		
//		Open_Commit();
		
//		readObj(agency);
		
		readLatest();
	}
	
	static public void init_Commit(){
		try {
			agency.init_repository(path);
			agency.commit("szha246", "", "123567hahahaha~~~!!!\n", "commit 2");
			agency.close_repository();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static public void Open_Commit(){
		try {
			agency.open_repository(path);
			agency.commit("szha246", "", "123567hahahahaNew~~~!!!\n", "commit 2");
			agency.close_repository();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static public void readObj(){
		try {
			agency.open_repository(path);
			String value = agency.readObject("74243e758635bf6a713e1f2c8088c8634a74c461");
			System.out.println(value);
		} catch (MissingObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public void readLatest(){
		try {
			agency.open_repository(path);
			String value = agency.readLatest();
			System.out.println(value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
