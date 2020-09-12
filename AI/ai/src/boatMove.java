/*
 * botMove class extends from mainProcess abstract class.
 * this class will identify user winning probability and it's own
 * winning probability and based on that it will decide to do defense or
 * attack.
 * **********************************************************************
 * Here defense means to stop user making it's pattern.
 * while attack means to make own pattern.
 * */

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

public class boatMove extends mainProcess{
	
	//private final static data members
	private final static String DBNAME = "tictoctoe_ai";
	private final static String USERTABLE = "user_move";
	private final static String BOATABLE = "boat_move";
	
	private connectToDB conDB;
	private static Connection con=null;
	
	//private static data members
	private static String boatPattern;
	private static Statement stmt;
	private static ArrayList<Integer> expectedMoves=new ArrayList<Integer>();
	private static int botMovePos=-1;
	private static int userWiningProb=0;
	private static int boatWiningProb=0;
	private static ArrayList<Integer> boatExpectedMoves=new ArrayList<Integer>();
	
	//private data members
	private String query;
	private String userInput;
	private String boatMove;
	
	//constructor
	boatMove()throws SQLException {
		conDB= new connectToDB();
		con = conDB.getConectionDB();
		this.query=null;
		this.userInput=null;
		this.boatMove=null;
	}
	
	//make pattern for boat that will followed by boat
	private void makeBoatPattern()throws SQLException {
			
			stmt = con.createStatement();
			stmt.executeUpdate("USE "+DBNAME);
			query = "SELECT "+BOATABLE+".state FROM "+BOATABLE;
			stmt.executeQuery(query);
			ResultSet rs2 = stmt.executeQuery(query);
			rs2.next();
			boatPattern=rs2.getString("state");
			StringTokenizer pattern = new StringTokenizer(boatPattern);
			
			while(pattern.hasMoreTokens()) {
				boatExpectedMoves.add(Integer.parseInt(pattern.nextToken()));
			}
	}
	
	/*
	 * ***************************************************************************************************************
	 * CHECK BOAT PATTERN STILL EXIST OR NOT
	 * ***************************************************************************************************************
	 * */
	private boolean checkBoatPatternStatus() throws SQLException {
		stmt = con.createStatement();
		stmt.executeUpdate("USE "+DBNAME);
		query = "SELECT state FROM "+BOATABLE+" WHERE state LIKE '"+boatPattern+"'";
		int ans = stmt.executeUpdate(query);
		if(ans!=0)
			return true;
		else
			return false;
	}
	
	/*
	 * ****************************************************************************************************
	 *  make defense to stop user making it's pattern
	 * ****************************************************************************************************
	 * */
	private void makeDefense()throws SQLException {
		stmt=con.createStatement();
		stmt.executeUpdate("USE "+DBNAME);
		query = "SELECT state,move FROM "+USERTABLE+" WHERE move NOT LIKE '"+this.userInput+"'";
		ResultSet rs=stmt.executeQuery(query);
		
		while(rs.next())
		{
			String stateList = rs.getString("state");
			if(stateList.indexOf(this.userInput)!=-1) {
				StringTokenizer str = new StringTokenizer(stateList);
				while(str.hasMoreTokens()) {
					String value = str.nextToken();
					String userMove = rs.getString("move");
					if(!(value.equals(userInput)) && !(value.equals(userMove))) {
						expectedMoves.add(Integer.parseInt(value)-1);
					}
				}
			}
		}
	}

	/*
	 * ****************************************************************************************************
	 *  make boat move
	 * ***************************************************************************************************
	 * */
	public void makeBotMove()throws SQLException {
		super.makeMoveMap(userInput, 1);
		userWiningProb=super.findProb(1);
		
		//first move
		if(botMovePos==-1) {
			Random rm = new Random();
			botMovePos = rm.nextInt(8);
			while(botMovePos==Integer.parseInt(this.userInput)-1) {
				botMovePos = rm.nextInt(8);
			}
		}
		
		//after 2nd move
		else {
			//if boat wining probability is higher 
			if(boatWiningProb>=userWiningProb) {
				if(boatExpectedMoves.size()==0 || checkBoatPatternStatus()) 
					makeBoatPattern();
				botMovePos=boatExpectedMoves.get(0);
				boatExpectedMoves.remove(0);
			}
			
			//if user wining probability is higher
			else if(userWiningProb>boatWiningProb) {	
				int size = expectedMoves.size();
				
				if(size>=1) {
					
					//if it is expected move from user
					if(expectedMoves.get(0)!=Integer.parseInt(this.userInput)-1) {
						botMovePos = expectedMoves.get(0);
						expectedMoves.remove(0);
					}
					
					//if it is not expected one
					else {
						if(boatExpectedMoves.size()==0 || checkBoatPatternStatus()) 
							makeBoatPattern();
						botMovePos=boatExpectedMoves.get(0);
						boatExpectedMoves.remove(0);
					}
				}
				
				else if(size==0) {
					makeDefense();
					botMovePos = expectedMoves.get(0);
					expectedMoves.remove(0);
				}
			}
		}
		
		this.setBotInput(Integer.toString(botMovePos+1));
		super.makeMoveMap(Integer.toString(botMovePos+1),2);
		boatWiningProb=super.findProb(2);
		
		System.out.println("USER:"+this.userInput+" BOAT:"+(this.botMovePos+1)+" USER WINING :"+userWiningProb+" BOAT WINING : "+boatWiningProb);
		super.deletePattenrns();
	}

	/*
	 * ****************************************************************************************************
	 *  set value of userInput
	 * ***************************************************************************************************
	 * */
	public void setUserMove(String input) {
		this.userInput=input;
		super.setUserInput(input);
	}
	
	/*
	 * ****************************************************************************************************
	 *  return boat move
	 * ***************************************************************************************************
	 * */
	public int getBotMove() {
		return botMovePos;
	}
	
	/*
	 * ****************************************************************************************************
	 *  return expected possible moves of user
	 * ***************************************************************************************************
	 * */
	public ArrayList<Integer> getExpectedMoves() {
		return expectedMoves;
	}
	
	/*
	 * ****************************************************************************************************
	 *  return user winning probability
	 * ***************************************************************************************************
	 * */
	public int getUserWinProb() {
		return this.userWiningProb;
	}
	
	/*
	 * ****************************************************************************************************
	 *  return boat winning probability
	 * ***************************************************************************************************
	 * */
	public int getBoatWinProb() {
		return this.boatWiningProb;
	}
}
