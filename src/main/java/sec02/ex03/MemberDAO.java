package sec02.ex03;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class MemberDAO {
	private Connection conn;
	private PreparedStatement pstmt;
	private DataSource dataFactory;
	
	public MemberDAO() {
		try {
			Context ctx = new InitialContext();
			Context envContext = (Context)ctx.lookup("java:/comp/env");
			dataFactory = (DataSource)envContext.lookup("jdbc/oracle");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<MemberVO> listMembers() {
		List<MemberVO> membersList = new ArrayList<MemberVO>();
		try {
			conn = dataFactory.getConnection();
			String query = "select * from t_member";
			pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				MemberVO memberVO = new MemberVO(rs.getString("id"),
						rs.getString("pwd"),
						rs.getString("name"),
						rs.getString("email"),
						rs.getDate("joinDate"));
				membersList.add(memberVO);
			}
			conn.close();
			rs.close();
			pstmt.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return membersList;
	}
	
	public void addMember(MemberVO m) {
		try {
			conn = dataFactory.getConnection();
			String query = "insert into t_member(id,pwd,name,email) values(?,?,?,?)";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, m.getId());
			pstmt.setString(2, m.getPwd());
			pstmt.setString(3, m.getName());
			pstmt.setString(4, m.getEmail());
			pstmt.executeQuery();
			pstmt.close();
			conn.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public MemberVO findMember(String _id) {
		MemberVO memInfo=null;
		try {
			conn = dataFactory.getConnection();
			String query = "select * from t_member where id=?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, _id);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			memInfo = new MemberVO(rs.getString("id"),rs.getString("pwd"),rs.getString("name"),
					rs.getString("email"),
					rs.getDate("joinDate"));
			pstmt.close();
			conn.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return memInfo;
	}
	public void modMember(MemberVO memberVO) {
		try {
			conn = dataFactory.getConnection();
			String query = "update t_member set pwd=?,name=?,email=? where id=?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, memberVO.getPwd());
			pstmt.setString(2, memberVO.getName());
			pstmt.setString(3, memberVO.getEmail());
			pstmt.setString(4, memberVO.getId());
			pstmt.executeQuery();
			pstmt.close();
			conn.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void delMember(String id) {
		try {
			conn = dataFactory.getConnection();
			String query = "delete from t_member where id=?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, id);
			pstmt.executeQuery();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
