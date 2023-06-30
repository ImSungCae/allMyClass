package sec03.brd08;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

@WebServlet("/board/*")
public class BoardController extends HttpServlet {
	// 글에 첨부한 이미지 저장 위츠를 상수로 선언
	private static String ARTICLE_IMAGE_REPO = "C:\\board\\article_image";
	BoardService boardService;
	ArticleVO articleVO;

	public void init() throws ServletException {
		boardService = new BoardService();
		articleVO = new ArticleVO();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doHandle(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doHandle(req, resp);
	}

	private void doHandle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String nextPage = "";
		HttpSession session;
		String action = req.getPathInfo();
		System.out.println("action: " + action);
		try {
			List<ArticleVO> articlesList = new ArrayList<ArticleVO>();
			if(action.equals("/addArticle.do")) {
				int articleNO = 0;
				Map<String,String> articleMap = upload(req, resp);
				String imageFileName = articleMap.get("imageFileName");
				articleVO.setParentNO(0);
				articleVO.setId("hong");
				articleVO.setTitle(articleMap.get("title"));
				articleVO.setContent(articleMap.get("content"));
				articleVO.setImageFileName(imageFileName);
				articleNO = boardService.addArticle(articleVO);
				if(imageFileName!=null&&imageFileName.length()!=0) {
					File srcFile = new File(ARTICLE_IMAGE_REPO+"\\temp\\"+ imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO+"\\"+articleNO);
					destDir.mkdir();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
				PrintWriter pw = resp.getWriter();
				pw.print("<script>"
						+ " alert('새글을 추가했습니다.');"
						+ " location.href='" + req.getContextPath() +"/board/listArticles.do';"
								+ "</script>");
				return;
			}else if(action.equals("/articleForm.do")) {
				nextPage = "/board07/articleForm.jsp";
			}else if(action.equals("/viewArticle.do")) {
				String articleNO = req.getParameter("articleNO");
				articleVO = boardService.viewArticle(Integer.parseInt(articleNO));
				req.setAttribute("article", articleVO);
				nextPage = "/board07/viewArticle.jsp";
			}else if(action.equals("/modArticle.do")) {
				Map<String, String> articleMap = upload(req, resp);
				int articleNO = Integer.parseInt(articleMap.get("articleNO"));
				articleVO.setArticleNO(articleNO);
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				articleVO.setParentNO(0);
				articleVO.setId("hong");
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				boardService.modArticle(articleVO);
				if(imageFileName!=null&&imageFileName.length()!=0) {
					String originalFileName = articleMap.get("originalFileName");
					File srcFile = new File(ARTICLE_IMAGE_REPO +"\\temp\\"+imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO+"\\"+articleNO);
					destDir.mkdir();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
					File oldFile = new File(ARTICLE_IMAGE_REPO +"\\" + articleNO +"\\"+ originalFileName);
					oldFile.delete();
				}
				PrintWriter pw = resp.getWriter();
				pw.print("<script>"
						+ "alert('글을 수정했습니다.');"
						+ "location.href='"
						+ req.getContextPath()
						+ "/board/viewArticle.do?articleNO=" + articleNO + "';" + "</script>");
				return;
			}else if(action.equals("/removeArticle.do")) {
				int articleNO = Integer.parseInt(req.getParameter("articleNO"));
//				articleNO 값에 대한 글을 삭제한 후 삭제된 부모 글과 자식 글의 articleNO 목록을 가져옵니다.
				List<Integer> articleNOList = boardService.removeArticle(articleNO);
				for(int _articleNO : articleNOList) {
					File imgDir = new File(ARTICLE_IMAGE_REPO+"\\"+_articleNO);
					if(imgDir.exists()) {	// 파일이 존재하는지 불린값으로 반환
						FileUtils.deleteDirectory(imgDir);
					}
				}
				PrintWriter pw = resp.getWriter();
				pw.print("<script>" + "  alert('글을 삭제했습니다.');" + " location.href='" + req.getContextPath()
						+ "/board/listArticles.do';" + "</script>");
				return;
			}else if(action.equals("/replyForm.do")) {
				int parentNO = Integer.parseInt(req.getParameter("parentNO"));
				session = req.getSession();
				session.setAttribute("parentNO", parentNO);
				nextPage = "/board07/replyForm.jsp";
			}else if(action.equals("/addReply.do")) {
				session = req.getSession();
				int parentNO = (Integer)session.getAttribute("parentNO");
				session.removeAttribute("parentNO");
				Map<String,String> articleMap = upload(req, resp);
				String imageFileName = articleMap.get("imageFileName");
				articleVO.setParentNO(parentNO);
				articleVO.setId("lee");
				articleVO.setTitle(articleMap.get("title"));
				articleVO.setContent(articleMap.get("content"));
				articleVO.setImageFileName(imageFileName);
				int articleNO = boardService.addReply(articleVO);
				if(imageFileName!=null&&imageFileName.length()!=0) {
					File srcFile = new File(ARTICLE_IMAGE_REPO+"\\temp\\"+ imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO+"\\"+articleNO);
					destDir.mkdir();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
				PrintWriter pw = resp.getWriter();
				pw.print("<script>"
						+ " alert('답글을 추가했습니다..');"
						+ " location.href='" + req.getContextPath() +"/board/listArticles.do?articleNO="
								+ articleNO + "';"
								+ "</script>");
				return;
			}else {
				String _section = req.getParameter("section");
				String _pageNum = req.getParameter("pageNum");
//				최초 요청 시 section 값과 pageNum 값이 없으면 각각 1로 초기화합니다.
				int section = Integer.parseInt(((_section==null)? "1":_section));
				int pageNum = Integer.parseInt(((_pageNum==null)? "1":_pageNum));
				Map<String,Integer> pagingMap = new HashMap<>();
				pagingMap.put("section", section);
				pagingMap.put("pageNum", pageNum);
				Map articlesMap = boardService.listArticles(pagingMap);
				articlesMap.put("section", section);
				articlesMap.put("pageNum", pageNum);
				req.setAttribute("articlesMap", articlesMap);
				nextPage = "/board07/listArticles.jsp";
				
			}

			RequestDispatcher dispatch = req.getRequestDispatcher(nextPage);
			dispatch.forward(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public Map<String,String> upload(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String,String> articleMap = new HashMap<String,String>();
		String encoding = "utf-8";
		File currentDirPath = new File(ARTICLE_IMAGE_REPO);
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(currentDirPath);
		factory.setSizeThreshold(1024 * 1024);
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List items = upload.parseRequest(req);
			for (int i = 0; i < items.size(); i++) {
				FileItem fileItem = (FileItem)items.get(i);
				if(fileItem.isFormField()) {
					System.out.println(fileItem.getFieldName() + "=" + fileItem.getString(encoding));
					articleMap.put(fileItem.getFieldName(), fileItem.getString(encoding));
					// 파일 업로드로 같이 전송된 새 글 관련 매개변수를 Map에 (key,value)로 저장 한 후
					// 반환하고, 새 글과 관련된 title, content를 Map에 저장
				}else {
					System.out.println("파라미터명:" + fileItem.getFieldName());
					System.out.println("파일크기:"+fileItem.getSize()+"bytes");
					if(fileItem.getSize()>0) {
						int idx = fileItem.getName().lastIndexOf("\\");
						if(idx == -1) {
							idx = fileItem.getName().lastIndexOf("/");
						}
						String fileName = fileItem.getName().substring(idx+1);
						System.out.println("파일명:" + fileName);
						articleMap.put(fileItem.getFieldName(), fileName);
						File uploadFile = new File(currentDirPath + "\\temp\\" + fileName);
						fileItem.write(uploadFile);
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return articleMap;
	}
}
