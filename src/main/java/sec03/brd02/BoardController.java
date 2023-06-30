package sec03.brd02;

import java.io.File;
import java.io.IOException;
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
			// /addArticle.do로 요청 시 새 글 추가 작업을 수행
			if(action.equals("/addArticle.do")) {
				// 파일 업로드 기능을 사용하기 위해 upload()로 요청을 전달합니다.
				Map<String,String> articleMap = upload(req, resp);
				articleVO.setParentNO(0);
				articleVO.setId("hong");
				articleVO.setTitle(articleMap.get("title"));
				articleVO.setContent(articleMap.get("content"));
				articleVO.setImageFileName(articleMap.get("imageFileName"));
				boardService.addArticle(articleVO);
				nextPage = "/board/listArticles.jsp";
				// action값 /articleForm.do로 요청 시 글쓰기창이 나타납니다.
			}else if(action.equals("/articleForm.do")) {
				nextPage = "/board02/articleForm.jsp";
			}else {
				articlesList = boardService.listArticles();
				req.setAttribute("articlesList", articlesList);
				nextPage = "/board02/listArticles.jsp";
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
		
		// 글 이미지 저장 폴더에 대해 파일 객체를 생성
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
						File uploadFile = new File(currentDirPath + "\\" + fileName);
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
