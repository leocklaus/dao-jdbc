package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	
	private Connection conn;
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("INSERT INTO seller\r\n"
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId)\r\n"
					+ "VALUES\r\n"
					+ "(?, ?, ?, ?, ?)", 
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			LocalDateTime birthdate = obj.getBirthDate();
			st.setTimestamp(3, Timestamp.valueOf(birthdate));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			int rowsAffected = st.executeUpdate();
			
			if(rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Unexpected Error! No rows affected!");
			}
			
		} catch(SQLException e ) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("UPDATE seller\r\n"
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ?\r\n"
					+ "WHERE Id = ?");
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			LocalDateTime birthdate = obj.getBirthDate();
			st.setTimestamp(3, Timestamp.valueOf(birthdate));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());
			
			st.executeUpdate();
						
		} catch(SQLException e ) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("DELETE FROM seller\r\n"
					+ "WHERE Id = ?");
			
			st.setInt(1, id);
			
			int rowsAffected =  st.executeUpdate();
			
			if(rowsAffected == 0) {
				throw new DbException("ID " + id + " is not valid.");
			}
			
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE seller.Id = ?");
			
			st.setInt(1, id);
			
			rs = st.executeQuery();
			
			if(rs.next()) {
				Department dep = instantiateDepartment(rs);
				Seller obj = instantiateSeller(rs, dep);
				
				return obj;
			}
			
			return null;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
			
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}

	}

	@Override
	public List<Seller> findAll() {
		Map<Integer, Department> depMap = new HashMap<>();
		PreparedStatement st = null;
		ResultSet rs = null;
		List<Seller> sellers = new ArrayList<>();
		
		try {
			
			st = conn.prepareStatement("SELECT seller.*,department.Name as DepName\r\n"
					+ "FROM seller INNER JOIN department\r\n"
					+ "ON seller.DepartmentId = department.Id\r\n"
					+ "ORDER BY Name");
			
			rs = st.executeQuery();
			
			while (rs.next()) {
				Integer id = rs.getInt("DepartmentId");
				Department dep = depMap.get(id);
				
				if(dep == null) {			
					depMap.put(id,instantiateDepartment(rs));
					dep = depMap.get(id);
				}
				
				sellers.add(instantiateSeller(rs, dep));
			}
			
			return sellers;
			
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
	
	@Override
	public List<Seller> findByDepartment(Department obj) {
		
		Map<Integer, Department> depMap = new HashMap<>();
		PreparedStatement st = null;
		ResultSet rs = null;
		List<Seller> sellers = new ArrayList<>(); 
		
		try {
									
			st = conn.prepareStatement("SELECT seller.*,department.Name as DepName\r\n"
					+ "FROM seller INNER JOIN department\r\n"
					+ "ON seller.DepartmentId = department.Id\r\n"
					+ "WHERE DepartmentId = ?\r\n"
					+ "ORDER BY Name");
			
			st.setInt(1, obj.getId());
			
			rs = st.executeQuery();
			
			while (rs.next()) {
				Department dep = depMap.get(obj.getId());
				
				if(dep == null) {			
					depMap.put(obj.getId(),instantiateDepartment(rs));
					dep = depMap.get(obj.getId());
				}
				
				sellers.add(instantiateSeller(rs, dep));
			}
			
			return sellers;
			
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
	}
	
	private static Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		
		return dep;
	}
	
	private static Seller instantiateSeller(ResultSet rs, Department department) throws SQLException {
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		Date date = (rs.getDate("BirthDate"));
		Timestamp timestamp = new Timestamp(date.getTime());
		obj.setBirthDate(timestamp.toLocalDateTime());
		obj.setDepartment(department);
		
		return obj;
	}


}
