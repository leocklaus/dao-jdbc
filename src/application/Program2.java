package application;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class Program2 {

	public static void main(String[] args) {
		
		DepartmentDao departmentDao = DaoFactory.createDepartmentDao();
		
		List<Department> departments = departmentDao.findAll();
		
		departments.forEach(dep -> System.out.println(dep));
		
		Department department = departmentDao.findById(2);
		System.out.println(department);

	}

}
