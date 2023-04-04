package application;

import java.util.List;

import db.DB;
import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		
		SellerDao sellerDao = DaoFactory.createSellerDao();
		
		/* Department department = new Department(2, null);
		
		Seller newSeller = new Seller(null, "Greg", "greg@gmail.com", LocalDateTime.now(), 4000.00, department);
		
		sellerDao.insert(newSeller);
		
		System.out.println("Inserted. New Id: " + newSeller.getId()); */
		
		/*Seller seller = sellerDao.findById(1);
		seller.setName("Martha Waine");
		
		sellerDao.update(seller);*/
		
		sellerDao.deleteById(7);
		
		
		List<Seller> sellers = sellerDao.findAll();
		
		sellers.forEach(sl -> System.out.println(sl));
		
		DB.closeConnection();

	}

}
