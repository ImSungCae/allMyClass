package sec02.ex03;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@WebServlet("/member/*")
public class MemberController extends HttpServlet {
	MemberDAO memberDAO;
	public void init(ServletConfig config) throws ServletException {
		memberDAO = new MemberDAO();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}
	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String nextPage = null;
		String action = request.getPathInfo();
		if(action.equals("/addMember.do")) {
			MemberVO memberVO = new MemberVO(request.getParameter("id"),request.getParameter("pwd"),
					request.getParameter("name"),
					request.getParameter("email"));
			memberDAO.addMember(memberVO);
			request.setAttribute("msg", "addMember");
			nextPage = "/member/listMembers.do";
		}else if(action.equals("/memberForm.do")) {
			nextPage = "/test03/memberForm.jsp";
		}else if(action.equals("/modMemberForm.do")) {
			MemberVO memInfo = memberDAO.findMember(request.getParameter("id"));
			request.setAttribute("memInfo", memInfo);
			nextPage = "/test03/modMemberForm.jsp";
		}else if(action.equals("/modMember.do")) {
			MemberVO memberVO = new MemberVO(request.getParameter("id"),request.getParameter("pwd"),
					request.getParameter("name"),
					request.getParameter("email"));
			memberDAO.modMember(memberVO);
			request.setAttribute("msg", "modified");
			nextPage = "/member/listMembers.do";
		}else if(action.equals("/delMember.do")){
			memberDAO.delMember(request.getParameter("id"));
			request.setAttribute("msg", "deleted");
			nextPage = "/member/listMembers.jsp";
		}else {
			List<MemberVO> membersList = memberDAO.listMembers();
			request.setAttribute("membersList", membersList);
			nextPage = "/test03/listMembers.jsp";
		}
		
		
		RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
		dispatch.forward(request, response);
		
	}

}
