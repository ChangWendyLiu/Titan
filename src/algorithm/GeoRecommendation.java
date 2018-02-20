package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.MySQLConnection;
import entity.Item;

public class GeoRecommendation {
	
	public List<Item> recommendItems(String userId, double lat, double lon) {
		// step 1
		MySQLConnection conn = new MySQLConnection();
		Set<String> favoriteItems = conn.getFavoriteItemIds(userId);
		
		// sep 2
		Set<String> allCategories = new HashSet();
		for(String itemId: favoriteItems) {
			allCategories.addAll(conn.getCategories(itemId));
		}
		// step 3
		Set<Item> recommendedItems = new HashSet<>(); 
		/* 去重 */
		
		for(String category : allCategories) {
			List<Item> items = conn.searchItems(userId, lat, lon, category);
			recommendedItems.addAll(items);
		}
		
		//step 4
		List<Item> filteredItems = new ArrayList<>();
		for(Item item : recommendedItems) {
			if(!favoriteItems.contains(item.getItemId())) {
				filteredItems.add(item);
			}
		}
		
		//step 5
		
		Collections.sort(filteredItems, new Comparator<Item>() {
			@Override
			public int compare(Item item1, Item item2) {
				return Double.compare(item1.getDistance(), item2.getDistance());
			}
		});
		
		
		
		
		
		
		
		return filteredItems;
	  }


}
