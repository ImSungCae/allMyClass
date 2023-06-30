package sec03.brd07;

import java.util.List;

public class BoardService {
	BoardDAO boardDAO;
	public BoardService() {
		boardDAO = new BoardDAO();
	}
	public List<ArticleVO> listArticles() {
		List<ArticleVO> articlesList = boardDAO.selectAllArticles();
		return articlesList;
	}
	
	public int addArticle(ArticleVO article) {
		return boardDAO.insertNewArticle(article);
	}
	public ArticleVO viewArticle(int articleNO) {
		ArticleVO article = null;
		article = boardDAO.selectArticle(articleNO);
		return article;
	}
	public void modArticle(ArticleVO article) {
		boardDAO.updateArticle(article);
	}
	public List<Integer> removeArticle(int articleNO) {
//		글을 삭제하기 전 글 번호들을 ArrayList 객체에 저장합니다.
		List<Integer> articleNOList = boardDAO.selectRemovedArticles(articleNO);
		boardDAO.deleteArticle(articleNO);
//		삭제한 글 번호 목록을 컨트롤러로 반환합니다.
		return articleNOList;
	}
	public int addReply(ArticleVO article) {
		return boardDAO.insertNewArticle(article);
	}
}
