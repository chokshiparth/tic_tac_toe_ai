/*
 * User Interface Class
 * */
import java.io.IOException;
import java.sql.SQLException;
import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/UserInterface")
public class UserInterface extends HttpServlet {
 
	private static final long serialVersionUID = 1L;
	private static int flag=0;
	private String userInput;
    private BotMove bm;
	private char board[];
	private int botMovePos;
	private int userWiningProb;
	private int botWiningProb;
	
	/*
	 * ****************************************************************************************************
	 *  constructor
	 * ***************************************************************************************************
	 **/
    public UserInterface()throws SQLException {
        super();
        this.userInput=null;
        this.bm = new BotMove();
        char j='1';
        this.board = new char[9];
        for(int i=0;i<9;i++) {
        	this.board[i]=j;
        	j++;
        }
    }
    
    /*
	 * ****************************************************************************************************
	 *  display board method
	 * ***************************************************************************************************
	 **/
    private void displayBoard(HttpServletResponse response)throws ServletException,IOException {
		PrintWriter pr = response.getWriter(); 
		response.setContentType("text/html");
		pr.println("<body style=\"text-align:center\">");
		pr.println("<h3><u><i>TIC TAC TOE</i></u></h3>");
		pr.println("<form name='gameBoard' action='userNum' method='post'>");
		pr.println("<table align='center' border='1'>");
		int i=0;
		for(int j=0;j<3;j++) {
			pr.println("<tr>");
			while(i<9) {
				if(this.board[i]=='X')
					pr.print("<td><font style=\'color:red\'>"+this.board[i]+"</font></td>");
				else if(this.board[i]=='O')
					pr.print("<td><font style=\'color:green\'>"+this.board[i]+"</font></td>");
				else
					pr.print("<td><font style=\'color:black\'>"+this.board[i]+"</font></td>");
				if((i+1)%3==0) {
					i++;
					break;
				}
				else
					i++;
			}
			pr.println("</tr>");
		}
		pr.println("</table>");
		pr.println("<br>");
		pr.println("<input type='text' name='user_input' placeholder='enter your move'/>");
		pr.println("<br><br>");
		pr.println("<input type='submit' name='SUBMIT' value='SUBMIT'>");
		pr.println("</form>");
		pr.println("</body>");
    }
    
    /*
	 * ****************************************************************************************************
	 *  insert value into board
	 * ***************************************************************************************************
	 **/
    private void putValue(char sign)throws ServletException,IOException {
    	if(sign=='O') {
    		int move = Integer.parseInt(this.userInput)-1;
    		this.board[move]=sign;
    	}
    	else if(sign=='X') {
    		int move = this.botMovePos;
    		this.board[move]=sign;
    	}
    }

    /*
	 * ****************************************************************************************************
	 *  doGet method
	 * ***************************************************************************************************
	 **/
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.userInput = request.getParameter("user_input");
		if(this.userInput!=null) {
			
			try {
				this.putValue('O');
				bm = new BotMove();
				if(flag==0)
				{
					bm.MakeDb();
					flag=1;
				}
				this.bm.setUserMove(userInput);
				this.bm.makeBotMove();
				this.botMovePos = this.bm.getBotMove();
				this.userWiningProb = bm.getUserWinProb();
				this.botWiningProb = bm.getBotWinProb();
				this.putValue('X');
				if(userWiningProb > botWiningProb && userWiningProb != 1) {
						response.getWriter().println("<H1><i>USER WON</i></H1>");
					}
				else if(userWiningProb<botWiningProb && botWiningProb != 1) {
						response.getWriter().println("<H1><i>COMPUTER WON</i></H1>");
					}
			}
			catch(Exception e) {
				for(int i=0;i<e.getStackTrace().length;i++) {
					response.getWriter().println("Error Reason : "+e.getLocalizedMessage());
					response.getWriter().println("<br>Error Line:"+e.getStackTrace()[i].getLineNumber());
					response.getWriter().println("<br>Error Class:"+e.getStackTrace()[i].getClassName());
					response.getWriter().println("<br>Error Method:"+e.getStackTrace()[i].getMethodName());
					response.getWriter().println("<br>Error File:"+e.getStackTrace()[i].getFileName()+"<br>");
				}
			}
		}
		this.displayBoard(response);
	}
	
	/*
	 * ****************************************************************************************************
	 *  doPost method
	 * ***************************************************************************************************
	 **/
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
