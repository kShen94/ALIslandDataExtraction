

import java.util.HashMap;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class DataMappings {
	static JSONObject manageRestaurant = JsonData.manageRestaurant;
	static JSONObject itemTemplate = JsonData.itemDataTemplate;
	static JSONObject formula = JsonData.formula;
	static JSONObject productionSlot = JsonData.productionSlot;
	static JSONObject productionPlace = JsonData.productionPlace;
	static JSONObject farmSeed = JsonData.farmSeed;
	static JSONObject shopGoods = JsonData.shopGoods;


	
	public static HashMap<Integer,String> getRestaurantMap() {
		//itemTemplateID -> restaurant
		HashMap<Integer,String> restaurantMapping = new HashMap<Integer,String>();
		Set<String> keys = manageRestaurant.keySet();
		String name = "";
		for(String s : keys) {
			JSONObject shop = manageRestaurant.getJSONObject(s);
			name = shop.getString("name_en");
			JSONArray itemID = shop.getJSONArray("item_id");
			for(int i =0; i < itemID.length(); i++) {
				restaurantMapping.put(itemID.getJSONArray(i).getInt(0), name);
				restaurantMapping.put(itemID.getJSONArray(i).getInt(1), name);
			}
		}
		return restaurantMapping;
	}
	
	public static HashMap<String,String> getTemplateMap() {
		//itemTemplateID -> formulaID
		HashMap<String,String> templateMapping = new HashMap<String,String>();
		Set<String> keys = itemTemplate.keySet();
		Set<String> fkeys = formula.keySet();
		for(String s: keys) {
			String itemName = itemTemplate.getJSONObject(s).getString("name");
			for(String f : fkeys) {
				JSONObject fItem = formula.getJSONObject(f);
				if(fItem.getJSONArray("commission_product").isEmpty())
					continue;
				String fname = fItem.getString("name");
				if(itemName.equalsIgnoreCase(fname)) {
					templateMapping.put(s, f);
					break;
				}
			}
		}
		return templateMapping;
	}
	
	public static HashMap<String,String> getProductionMap() {
		//item name to place name
		HashMap<String,String> productionPlaceMapping = new HashMap<String,String>();
		
		productionPlaceMapping.put("Poultry", "Laidback Ranch");
		productionPlaceMapping.put("Pelt", "Laidback Ranch");
		productionPlaceMapping.put("Fresh Honey", "Gathering");
		productionPlaceMapping.put("Matsutake", "Gathering");
		productionPlaceMapping.put("Reed Flowers", "Gathering");
		productionPlaceMapping.put("Peanuts", "Gathering");
		productionPlaceMapping.put("Autumn Chrysanthemum", "Gathering");
		
		
		Set<String> shopKeys = shopGoods.keySet();
		for(String shop: shopKeys) {
			JSONObject shopItem = shopGoods.getJSONObject(shop);
			productionPlaceMapping.put(shopItem.getString("goods_name"), "Shop");
		}
				
		HashMap<String,String> placeIDToPlaceName = new HashMap<String,String>();
		Set<String> placeKeys = productionPlace.keySet();
		for(String s: placeKeys) {
			JSONObject place = productionPlace.getJSONObject(s);
			placeIDToPlaceName.put(s, place.getString("name"));
		}
		
		Set<String> seedKeys = farmSeed.keySet();
		for(String sk: seedKeys) {
			JSONObject seed = farmSeed.getJSONObject(sk);
			productionPlaceMapping.put(seed.getString("name"), "Shop");
		}
		
		JSONObject slotList = productionSlot.getJSONObject("get_id_list_by_place");
		Set<String> slotKeys = slotList.keySet();
		for(String slot: slotKeys) {
			if(slot.equals("702"))
				continue;
			if(slot.equals("102")) {
				JSONArray slotArray = slotList.getJSONArray(slot);
				for(int s=0;s<slotArray.length();s++) {
					String slotID = slotArray.getInt(s)+"";
					JSONObject slotItem = productionSlot.getJSONObject(slotID);
					JSONArray formulaList = slotItem.getJSONArray("formula");
					String place = placeIDToPlaceName.get(slotItem.getInt("place")+"");
					for(int f =0; f < formulaList.length();f++) {
						String formulaID = formulaList.getInt(f)+"";
						JSONObject formulaItem = formula.getJSONObject(formulaID);
						productionPlaceMapping.put(formulaItem.getString("name"), place);
					}
				}
			}
			
			String slotID = slotList.getJSONArray(slot).getInt(slotList.getJSONArray(slot).length()-1)+"";
			JSONObject slotItem = productionSlot.getJSONObject(slotID);
			JSONArray formulaList = slotItem.getJSONArray("formula");
			String place = placeIDToPlaceName.get(slotItem.getInt("place")+"");
			for(int f =0; f < formulaList.length();f++) {
				String formulaID = formulaList.getInt(f)+"";
				JSONObject formulaItem = formula.getJSONObject(formulaID);
				productionPlaceMapping.put(formulaItem.getString("name"), place);
			}
			
			JSONArray activityFormula = slotItem.getJSONArray("activity_formula");
			if(activityFormula.length() == 0)
				continue;
			JSONArray activityItem = slotItem.getJSONArray("activity_formula").getJSONArray(0).getJSONArray(1);
			for(int af = 0; af<activityItem.length();af++) {
				String afItem = activityItem.getInt(af)+"";
				JSONObject formulaItem = formula.getJSONObject(afItem);
				productionPlaceMapping.put(formulaItem.getString("name"), place);
			}
			
		}
		
		
		
		return productionPlaceMapping;
	}
}
