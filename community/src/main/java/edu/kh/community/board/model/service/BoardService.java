package edu.kh.community.board.model.service;
import static edu.kh.community.common.JDBCTemplate.*;
import static edu.kh.community.common.JDBCTemplate.getConnection;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.kh.community.board.model.dao.BoardDAO;
import edu.kh.community.board.model.vo.Board;
import edu.kh.community.board.model.vo.BoardDetail;
import edu.kh.community.board.model.vo.BoardImage;
import edu.kh.community.board.model.vo.Pagination;
import edu.kh.community.common.Util;

public class BoardService {
	private BoardDAO dao = new BoardDAO();

	/**게시글 목록 조회
	 * @param type
	 * @param cp
	 * @return selectBoardList
	 * @throws Exception
	 */
	public Map<String, Object> selectBoardList(int type, int cp)throws Exception{
		Connection conn = getConnection();
		
		//1. 게시판 이름 조회 DAO 호출
		String boardName = dao.selectBoardName(conn,type);
		
		//2 - 1. 특정 게시판의 전체 게시글 수 조회
		int listCount = dao.selectListCount(conn,type);
		
		//2 - 2. 전체 게시글수 + 현재 페이지(CP)를 이용해 페이지네이션 객체 생성
		Pagination pagination = new Pagination(cp, listCount);
		
		//3 게시글 목록 조회
		List<Board> boardList = dao.selectBoardList(conn,pagination,type);
		
		//4.map 객체를 생성하여 1 , 2 , 3의 결과 객체를 모두 저장
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("boardName", boardName);
		map.put("pagination", pagination);
		map.put("boardList", boardList);
		
		close(conn);
		
		return map; 
	}

	/**게시글 상세조회 service
	 * @param boardNo
	 * @return detail
	 * @throws Exception
	 */
	public BoardDetail selectBoardDetail(int boardNo)throws Exception {
		Connection conn = getConnection();
		
		//1) 게시글 ( BOARD 테이블 ) 내용만 조회
		BoardDetail detail = dao.selectBoardDetail(conn,boardNo);
		
		//2) 게시글에 첨부된 이미지(BOARD_IMG 테이블)
		if(detail !=null) {			
			List<BoardImage> imageList = dao.selectImageList(conn,boardNo);
			detail.setImageList(imageList);
		}
		
		/*
		 * Map<String, Object> map = new HashMap<String, Object>;
		 */
		
		close(conn);
		
		return detail;
	}

	/**게시글 등록 serviec
	 * @param detail
	 * @param imageList
	 * @param boardCode
	 * @return boardNo
	 * @throws Exception
	 */
	public int insertBoard(BoardDetail detail, List<BoardImage> imageList, int boardCode)throws Exception{
	Connection conn = getConnection();
	
	//1.다음 작성할 게시글 번호 얻어오기
	//Board 테이블 INSERT 할때 / BOARD_IMG 테이블에 INSERT할때/ 반환 값(상세조회번호)
	int boardNo=dao.nextBoardNo(conn);
	
	//2. 게시'글' 부분만 삽입(detail,boardCode 사용)
	detail.setBoardNo(boardNo);
	//2-1) xss 방지 처리 (제목 내용)
	detail.setBoardTitle(Util.XssHandling(detail.getBoardTitle()));
	detail.setBoardContent(Util.XssHandling(detail.getBoardContent()));
	//2-2) 개행 문자 처리(내용만 해줘도 됨)
	detail.setBoardContent(Util.newLineHandling(detail.getBoardContent()));
	
	int result = dao.insertBoard(conn,detail,boardCode);
	
	if(result>0) { //게시글 삽입에 성공했다면
		//3 이미지 정보만 삽입(imageList)
		for(BoardImage image : imageList){ //하나씩 꺼내에서 삽입 수행.
			image.setBoardNo(boardNo); // 게시글 번호 세팅
			result=dao.insertBoardImage(conn,image);
			
			if(result == 0) {//이미지 삽입이 실패한 경우.
				break; //하나라도 실패하면 프로그램이 오류인 부분
			}
			
		} //for 끝
	} //if 끝
	
	//트랜잭션 처리
	
	if(result >0) {
		commit(conn);
	}else { //2,3번에서 한번이라도 실패한 경우
		rollback(conn);
		boardNo = 0; // 게시글 번호를 0으로 바꿔서 실패했음을 컨트롤러로 전달
	}
	
	close(conn);
		
		return boardNo;
	}

	/**게시글 수정 Service
	 * @param detail
	 * @param imageList
	 * @param deleteList
	 * @return result
	 * @throws Excpetion
	 */
	public int updateBoard(BoardDetail detail, List<BoardImage> imageList, String deleteList)throws Exception{
		Connection conn = getConnection();
		
		
		//1. 게시글 부분(제목/내용/마지막 수정일) 수정
		//1.1) XSS 방지 처리
		detail.setBoardTitle(Util.XssHandling(detail.getBoardTitle()));
		detail.setBoardContent(Util.XssHandling(detail.getBoardContent()));
		
		//1.2) 개행 문자 처리
		detail.setBoardContent(Util.newLineHandling(detail.getBoardContent()));
		
		//1.3) DAO 호출
			int result = dao.updateBoard(conn,detail);
		
		if(result>0) {
			
			//2. 이미지 부분 수정(기존->변경, 없다가 추가)
			for(BoardImage img : imageList){ //하나씩 꺼내에서 삽입 수행.
				img.setBoardNo(detail.getBoardNo());
				result = dao.updateBoardImage(conn, img);
				
				//result == 1 ( 수정 성공 )
				//result == 2 ( 수정 실패 )
				if(result == 0) {
					result = dao.insertBoardImage(conn, img);
				}
				
			} //향상된 for 끝 (update/insert 수행 완료)
			
			//3.이미지 삭제.
			//deleteList 삭제. (1,2,3 이런 모양 없으면""(빈 문자열)
			if(!deleteList.equals("")) { //삭제된 이미지 레벨이 기록되어 있을 때만 삭제.
				result = dao.deleteBoardImage(conn,deleteList,detail.getBoardNo());
			}
			
		} //게시글 수정 성공시.
		
		
		if(result>0) {			commit(conn);		}
		else {			rollback(conn);		}
		
		
		close(conn);
		
		return result;
	}

	/**게시글 삭제 Service
	 * @param boardNo
	 * @return result
	 * @throws Exception
	 */
	public int deleteBoard(int boardNo)throws Exception{
		Connection conn = getConnection();
		
		int result = dao.deleteBoard(conn,boardNo);
		
		if(result>0) commit(conn);
		else rollback(conn);
		
		close(conn);
		
		return result;
	}

	/**검색 목록 조회
	 * @param type
	 * @param cp
	 * @param key
	 * @param query
	 * @return map
	 * @throws Exception
	 */
	public Map<String, Object> searchBoardList(int type, int cp, String key, String query)throws Exception{
		Connection conn = getConnection();
		
		//기존 목록 조회 Service , DAO , SQL 참고
		
		//1. 게시판 이름 조회 DAO 호출
		String boardName = dao.selectBoardName(conn,type);
		
		//2. SQL조건절에 추가될 구문 가공(key,query사용)
		String condition = null; //조건
		switch(key) {
			case "t":condition=" AND BOARD_TITLE LIKE '%"+query+"%' "; break;
			case "c":condition=" AND BOARD_CONTENT LIKE '%"+query+"%' "; break;
			case "tc":condition=" AND (BOARD_CONTENT LIKE '%"+query+"%' OR BOARD_TITLE LIKE '%"+query+"%') "; break;
			case "w":condition=" AND MEMBER_NICK LIKE '%"+query+"%' "; break;
		}
		//3-1 조건에 충족하는 게시글 조회
		int listCount = dao.searchListCount(conn, type, condition);
		
		//3-2 listCount를 이용해서 Paginaion객체 생성
		Pagination pagination = new Pagination(cp, listCount);
		
		//4 게시글 목록 조회
		List<Board> boardList = dao.searchBoardList(conn,pagination,type,condition);
		
		//5.map 객체를 생성하여 1 , 2 , 3의 결과 객체를 모두 저장
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("boardName", boardName);
		map.put("pagination", pagination);
		map.put("boardList", boardList);
		
		close(conn);
		
		return map; 
	}
	
}
