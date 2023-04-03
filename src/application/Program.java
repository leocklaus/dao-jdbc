package application;

import java.time.LocalDateTime;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		
		SellerDao sellerDao = DaoFactory.createSellerDao();
		
		Department department = new Department(2, null);
		
		Seller newSeller = new Seller(null, "Greg", "greg@gmail.com", LocalDateTime.now(), 4000.00, department);
		
		sellerDao.insert(newSeller);
		
		System.out.println("Inserted. New Id: " + newSeller.getId());
		
		List<Seller> sellers = sellerDao.findAll();
		
		sellers.forEach(seller -> System.out.println(seller));

	}

}
