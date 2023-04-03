package application;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		
		SellerDao sellerDao = DaoFactory.createSellerDao();
		
		List<Seller> sellers = sellerDao.findAll();
		
		sellers.forEach(seller -> System.out.println(seller));

	}

}
