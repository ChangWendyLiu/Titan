package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

public class YelpAPI {
	private static final String API_HOST = "https://api.yelp.com";
 	private static final String DEFAULT_TERM = "";
	private static final int SEARCH_LIMIT = 20;
	private static final String SEARCH_PATH = "/v3/businesses/search";
	// Use student's own id and secret in teaching.
	private static final String TOKEN_TYPE = "Bearer";
	private static final String API_KEY = "b5jjuOk8dC8ZjV-_eqNVUKWnxZSoen0YEj0yg6IOyuT71IO7OcsffAwzMajsyAfvSjMLnX_tty021rFhbcAlposSsqrGnrA5opB2REvpHlvdg0LXjXQHbrKlwilYWnYx";

	public List<Item >search(double lat, double lon, String term) {
		String latitude = lat + "";
		String longitude = lon + "";
		
		try {
			if(term == null || term.isEmpty()) {
				term = DEFAULT_TERM;
			}
			term = java.net.URLEncoder.encode(term,"UTF-8");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		String url = API_HOST + SEARCH_PATH;
		String query = String.format("term=%s&latitude=%s&longitude=%s&limit=%s", term, latitude, longitude,SEARCH_LIMIT);

		try{
			HttpURLConnection connection = (HttpURLConnection) new URL(url + "?" + query).openConnection();
			
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization", TOKEN_TYPE + " " + API_KEY);
			
			int responseCode = connection.getResponseCode();
			System.out.println("Response code " + responseCode);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			JSONObject obj = new JSONObject (response.toString());
			JSONArray businesses = (JSONArray) obj.get("businesses");
			return getItemList(businesses);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Helper methods
	 */
	// Convert JSONArray to a list of item objects.
	private List<Item> getItemList(JSONArray array) throws JSONException {
		List<Item> list = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			// Parse json object fetched from Yelp API specifically.
			ItemBuilder builder = new ItemBuilder();
			// Builder pattern gives us flexibility to construct an item.
			builder.setItemId(object.getString("id"));
			JSONArray jsonArray = (JSONArray) object.get("categories");
			Set<String> categories = new HashSet<>();
			for (int j = 0; j < jsonArray.length(); j++) {
				JSONObject subObejct = jsonArray.getJSONObject(j);
				categories.add(subObejct.getString("title"));
			}
			builder.setCategories(categories);
			builder.setName(object.getString("name"));
			builder.setImageUrl(object.getString("image_url"));
			builder.setRating(object.getDouble("rating"));
			JSONObject coordinates = (JSONObject) object.get("coordinates");
			builder.setLatitude(coordinates.getDouble("latitude"));
			builder.setLongitude(coordinates.getDouble("longitude"));
			JSONObject location = (JSONObject) object.get("location");
			builder.setCity(location.getString("city"));
			builder.setState(location.getString("state"));
			builder.setZipcode(location.getString("zip_code"));
			JSONArray addresses = (JSONArray) location.get("display_address");
			String fullAddress = addresses.join(",");
			builder.setAddress(fullAddress);
			builder.setDistance(object.getDouble("distance"));
			// Uses this builder pattern we can freely add fields.
			Item item = builder.build();
			list.add(item);
		}
		return list;
	}

	private void queryAPI(double lat, double lon) {
		List<Item> itemList = search(lat, lon, null);
		try {
			for (Item item : itemList) {
				JSONObject jsonObject = item.toJSONObject();
				System.out.println(jsonObject);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Main entry for sample Yelp API requests.
	 */
	public static void main(String[] args) {
		YelpAPI tmApi = new YelpAPI();
		tmApi.queryAPI(37.38, -122.08);
	}

	
	

}
