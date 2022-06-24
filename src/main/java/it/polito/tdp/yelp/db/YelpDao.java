package it.polito.tdp.yelp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.yelp.model.Adiacenza;
import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Review;
import it.polito.tdp.yelp.model.User;

public class YelpDao {

	public List<Business> getAllBusiness(){
		String sql = "SELECT * FROM Business";
		List<Business> result = new ArrayList<Business>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						res.getString("city"),
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				result.add(business);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Review> getAllReviews(){
		String sql = "SELECT * FROM Reviews";
		List<Review> result = new ArrayList<Review>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Review review = new Review(res.getString("review_id"), 
						res.getString("business_id"),
						res.getString("user_id"),
						res.getDouble("stars"),
						res.getDate("review_date").toLocalDate(),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("review_text"));
				result.add(review);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<User> getAllUsers(){
		String sql = "SELECT * FROM Users";
		List<User> result = new ArrayList<User>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				User user = new User(res.getString("user_id"),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("name"),
						res.getDouble("average_stars"),
						res.getInt("review_count"));
				
				result.add(user);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<String> getAllCities() {
		String sql ="SELECT DISTINCT city "
				+ "FROM business "
				+ "ORDER BY city";
		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				String city = res.getString("city");
				
				result.add(city);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void getVertici(Integer year, String city, Map<String, Business> idMap) {
		String sql = "SELECT DISTINCT b.* "
				+ "FROM business b, reviews r "
				+ "WHERE b.business_id = r.business_id "
				+ "AND YEAR(r.review_date) = ? "
				+ "AND b.city = ? "
				+ "";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, year);
			st.setString(2, city);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				if(!idMap.containsKey(res.getString("b.business_id"))) {
					Business business = new Business(res.getString("business_id"), 
							res.getString("full_address"),
							res.getString("active"),
							res.getString("categories"),
							res.getString("city"),
							res.getInt("review_count"),
							res.getString("business_name"),
							res.getString("neighborhoods"),
							res.getDouble("latitude"),
							res.getDouble("longitude"),
							res.getString("state"),
							res.getDouble("stars"));
					idMap.put(res.getString("b.business_id"), business);
				}
			}
			res.close();
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public List<Adiacenza> getAdiacenze(Integer year, String city, Map<String, Business> idMap){
		String sql = "SELECT DISTINCT b1.business_id AS id1, b2.business_id AS id2, AVG(r1.stars)-AVG(r2.stars) AS peso "
				+ "FROM business b1, business b2, reviews r1, reviews r2 "
				+ "WHERE b1.business_id < b2.business_id "
				+ "AND b1.business_id = r1.business_id AND b2.business_id = r2.business_id "
				+ "AND b1.city = b2.city AND b1.city = ? "
				+ "AND YEAR(r1.review_date) = YEAR(r2.review_date) AND YEAR(r1.review_date) = ? "
				+ "GROUP BY id1, id2 "
				+ "HAVING peso != 0";
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, city);
			st.setInt(2, year);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(idMap.containsKey(res.getString("id1")) && idMap.containsKey(res.getString("id2"))) {
					Adiacenza a = new Adiacenza(idMap.get(res.getString("id1")), idMap.get(res.getString("id2")), res.getInt("peso"));
					result.add(a);
				}
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
