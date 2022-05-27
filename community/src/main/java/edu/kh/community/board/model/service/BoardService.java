package edu.kh.community.board.model.service;
import static edu.kh.community.common.JDBCTemplate.close;
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
	
}
