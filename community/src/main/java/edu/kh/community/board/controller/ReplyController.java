package edu.kh.community.board.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import edu.kh.community.board.model.service.ReplyService;
import edu.kh.community.board.model.vo.Reply;



// Controller : 요청에 따라 알맞은 서비스를 호출하고 
//요청 처리 결과를 응답할 (View)를 선택


// *** Front Controller 패턴**
//하나의 Servlet이 여러 요청을 받아들이고 제어하는 패턴

@WebServlet("/reply/*") //  /reply로 시작하는 모든 요청을 받은다.
public class ReplyController extends HttpServlet{
	//  /reply/selectReplyList
	//  /reply/insert
	//  /reply/update
	//  /reply/delete
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//get방식 요청 처리	
	  String uri = req.getRequestURI();
	  String contextPath = req.getContextPath();
	  String command = uri.substring(  (contextPath + "/reply/").length()  );
	  // /community/reply/update
	  // /community + /reply/ 여기서 부터 끝까지 잘라낸다-> update
	  // update
	 
	  ReplyService service = new ReplyService();
	  
	  try {
		  if(command.equals("selectReplyList")){ // 요청받은 주소가 selectReplyList인 경우
			  //js 형태라 정수형태로 파싱.
			  int boardNo = Integer.parseInt(req.getParameter("boardNo"));
			 
			  //댓글 목록 조회 서비스 호출후 리스트로 형태로 저장
			  List<Reply> rList = service.selectReplyList(boardNo);
			  
			  //JSON으로 변환하면서 응답
			  new Gson().toJson(rList,resp.getWriter());
		  }
		  
		  // 댓글 삽입
		  if(command.equals("insert")){
			  String replyContent = req.getParameter("replyContent");
			  int memberNo = Integer.parseInt(req.getParameter("memberNo"));
			  int boardNo = Integer.parseInt(req.getParameter("boardNo"));
			  
			  //reply 객체를 생성해서 파라미터 담기
			  Reply reply = new Reply();
			  reply.setReplyContent(replyContent);
			  reply.setMemberNo(memberNo);
			  reply.setBoardNo(boardNo);
			 
			  //댓글 등록(insert) 서비스 호출후 결과 반환 받기 
			  int result = service.insertReply(reply);
			  
			  //서비스 호출 결과 그대로 응답 데이터로 내보냄
			  resp.getWriter().print(result);
		  }
		  
		  
		  //댓글 삭제.
		  if(command.equals("delete")){
			  int replyNo = Integer.parseInt(req.getParameter("replyNo"));
			  int result = service.deleteReply(replyNo);
			  resp.getWriter().print(result);
		  }
		  
		  //댓글 수정.
		  if(command.equals("update")){
			  int replyNo = Integer.parseInt(req.getParameter("replyNo"));
			  String replyContent = req.getParameter("replyContent");
			  
			  int result = service.updateReply(replyNo,replyContent);
			  resp.getWriter().print(result);
		  }
		  
		  
	  }catch(Exception e){
		  e.printStackTrace();
		  
		  //ajax error 속성 활용을 위해서 강제로 500에러 전달
		  resp.setStatus(500); //500번 server 에러
		  resp.getWriter().print(e.getMessage());//에러용을 출력
	  }
	  
	  
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//post요청 처리
		doGet(req,resp); //post 전달된 요청을 doGet()으로 전달하여 수행
		
	}
}
