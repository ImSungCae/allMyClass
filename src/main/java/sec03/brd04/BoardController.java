package sec03.brd04;

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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

//@WebServlet("/board/*")
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
				nextPage = "/board02/articleForm.jsp";
			}else if(action.equals("/viewArticle.do")) {
				String articleNO = req.getParameter("articleNO");
				articleVO = boardService.viewArticle(Integer.parseInt(articleNO));
				req.setAttribute("article", articleVO);
				nextPage = "/board03/viewArticle.jsp";
			}else {
				articlesList = boardService.listArticles();
				req.setAttribute("articlesList", articlesList);
				nextPage = "/board03/listArticles.jsp";
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
						fileName = URLEncoder.encode(fileName);
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
