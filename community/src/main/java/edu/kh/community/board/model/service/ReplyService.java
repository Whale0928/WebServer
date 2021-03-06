package edu.kh.community.board.model.service;
import static edu.kh.community.common.JDBCTemplate.*;

import java.sql.Connection;
import java.util.List;

import edu.kh.community.board.model.dao.ReplyDAO;
import edu.kh.community.board.model.vo.Reply;
import edu.kh.community.common.Util;

public class ReplyService {
	ReplyDAO dao = new ReplyDAO();

	/**댓글 목록 조회
	 * @param boardNo
	 * @return rList
	 * @throws Exception
	 */
	public List<Reply> selectReplyList(int boardNo)throws Exception{
		Connection conn = getConnection();
		
		List<Reply> rList = dao.selectReplyList(conn,boardNo);

		close(conn);
		
		return rList;
	}

	/**댓글 삽입
	 * @param reply
	 * @return return
	 * @throws Exception
	 */
	public int insertReply(Reply reply)throws Exception{
		Connection conn = getConnection();
		 
		//개행문자 변경 처리.
		//textarea의 줄바꿈 문자 입력 시  \n ,\r ,\r\n ,\n\r 중 하나로 입력된다.
		//html에서 줄바꿈으로 인식할수 잇도록 <br> 태그로 변경
	
//댓글 등록/수정
//게시글 등록 / 수정
//내소개 / 상품정보같이 띄어쓰기 있는 모든 분야에 사용.
//reply.setReplyContent(reply.getReplyContent().replaceAll("(\n|\r|\r\n|\n\r)", "<br>"));

//Cross site Script(ing) (크로스 사이트 스크립트(팅)) 공격 방지 처리 -> util에 작성
reply.setReplyContent(Util.XssHandling(reply.getReplyContent()));

		// 여기서는 html < br > 으로 변경하기 때문에 개행문자 변환 메서드를 후순위로 호출해야한다.
		//static으로 선언해둔 개행문자 변경후 반환하는 메서드 사용
		reply.setReplyContent(Util.newLineHandling(reply.getReplyContent()));
		
		
		
		
		int result = dao.insertReply(conn,reply);
		
		if(result>0)commit(conn);
		else rollback(conn);

		
		close(conn);
		return result;
	}

	/**댓글 삭제
	 * @param replyNo
	 * @return result
	 * @throws Exception
	 */
	public int deleteReply(int replyNo)throws Exception{
		Connection conn = getConnection();
		
		int result = dao.deleteReply(conn,replyNo);
		
		if(result>0)commit(conn);
		else rollback(conn);

		close(conn);
		return result;
	}

	/** 댓글 삭제 service
	 * @param replyNo
	 * @param replyContent
	 * @return result
	 * @throws Exception
	 */
	public int updateReply(int replyNo, String replyContent)throws Exception{
	Connection conn = getConnection();
		
	
	replyContent=(Util.XssHandling(replyContent));
	replyContent=(Util.newLineHandling(replyContent));
		
		int result = dao.updateReply(conn,replyNo,replyContent);
		
		if(result>0)commit(conn);
		else rollback(conn);

		close(conn);
		return result;
	} 
}
