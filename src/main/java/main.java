import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import Utility.FileDownloader;
public class main {
	
	static JSONObject formula = JsonData.formula;
	static JSONObject itemTemplate = JsonData.itemDataTemplate;

	static HashMap<Integer,String> restaurantMapping = new HashMap<Integer,String>();
	//itemTemplateID -> formulaID
	static HashMap<String,String> templateMapping = new HashMap<String,String>();
	
	static HashMap<String,String> productionMapping = new HashMap<String,String>();

	public static void initTemplateMapping() {
		if(templateMapping.isEmpty())
			templateMapping = DataMappings.getTemplateMap();
	}

	public static void initRestaurantMapping() {
		if(restaurantMapping.isEmpty())
			restaurantMapping = DataMappings.getRestaurantMap();
	}
	
	public static void initProductionMapping() {
		if(productionMapping.isEmpty())
			productionMapping = DataMappings.getProductionMap();
	}
	public static void parseFood() {
		Set<String> keys = formula.keySet();
		ArrayList<String> items = new ArrayList<String>();
		ArrayList<String> itemCount = new ArrayList<String>();
		for (String s : keys) {
			if(Integer.valueOf(s) > 1000000)
				continue;
			
			items.clear();
			itemCount.clear();
			JSONObject formulaItem = formula.getJSONObject(s);
			String templateID = formulaItem.getJSONArray("commission_product").getJSONArray(0).getInt(0) +"";
			String foodName= formulaItem.getString("name");
			String workload = formulaItem.getInt("workload")+"";
			String price = itemTemplate.getJSONObject(templateID).getInt("order_price") +"";
			JSONArray cost = formulaItem.getJSONArray("commission_cost");
			for(int x = 0; x < cost.length(); x++) {
				items.add(itemTemplate.getJSONObject(cost.getJSONArray(x).getInt(0)+"").getString("name"));
				itemCount.add(cost.getJSONArray(x).getInt(1)+"");
			}
			String print1 = foodName + "\t" + workload + "\t";
			String print2 = price + "\t\t";
			for(int y = 0; y < items.size(); y++) {
				print1 = print1+ items.get(y) +"\t";
				print2 = print2+ itemCount.get(y)+"\t";
			}
			System.out.println(print1);
			System.out.println(print2 + "\n");

		}
	}
	
	public static void getProductCounts() {
		Set<String> keys = formula.keySet();
		
		for (String s : keys) {
			if(Integer.valueOf(s) > 1000000)
				continue;
			JSONObject formulaItem = formula.getJSONObject(s);
			String foodName= formulaItem.getString("name");
			String productCount = formulaItem.getJSONArray("commission_product").getJSONArray(0).getInt(1)+"";
			System.out.println( foodName + "\t" + productCount);
		}
		
		
	}
	
	public static void getSeasonalStats() {
		initProductionMapping();
		initTemplateMapping();
		Set<String> keys = itemTemplate.keySet();
		System.out.println("Item \t Season Pts \t Workload \t XP \t location");
		for (String s : keys) {
			if(Integer.valueOf(s) > 1000000)
				continue;
			JSONObject templateItem = itemTemplate.getJSONObject(s);
			String itemName = templateItem.getString("name");
			String seasonPts = templateItem.getInt("pt_num") + "";
			String templateID = templateMapping.get(s);
			String location = "N/A";
			int workload = 0;
			int xp = 0;
			if(templateID != null) {
				JSONObject formulaItem = formula.getJSONObject(templateID);
				workload = formulaItem.getInt("workload")/600;
				xp = formulaItem.getInt("ship_exp");
			}
			if(productionMapping.containsKey(itemName))
				location = productionMapping.get(itemName);
			System.out.println(itemName + "\t" + seasonPts + "\t" + workload + "\t" + xp + "\t" + location);
		}
	}
	public static void getFoodDemand() {
		initRestaurantMapping();
		Set<String> keys = itemTemplate.keySet();
		for (String s : keys) {
			if(Integer.valueOf(s) > 1000000)
				continue;
			JSONObject formulaItem = itemTemplate.getJSONObject(s);
			String itemName = formulaItem.getString("name");
			int itemID = Integer.valueOf(s);
			String shop = "";
			if(restaurantMapping.containsKey(itemID)) {
				shop = restaurantMapping.get(itemID);
			}
			String influence = formulaItem.getInt("manage_influence") + "";
			String subAttr = formulaItem.getJSONArray("sub_attribute").optInt(0,0)+"";
			double attrCoeff = formulaItem.getJSONArray("sub_attribute").optDouble(1,0)/100;
			int price = formulaItem.getInt("order_price");
			
			System.out.println(itemName + "\t" + influence + "\t" + subAttr + "\t" + attrCoeff + "\t" + price + "\t" + shop);
			
		}
	}
	
	public static void getLevels() {
		JSONObject islandLevel = JsonData.islandLevel;
		JSONObject charaLevel = JsonData.charaLevel;
		int totalIslandLevel = 0;
		int totalCharaLevel = 0;
		for(int level = 1; level <= 100; level++) {
			String ilevel = level+"";
			int ixp = islandLevel.getJSONObject(ilevel).getInt("island_exp");
			String charaXP = "";
			String cLevel = "";
			String totalCLevel = "";
			int cxp = 0;
			if(level <= 50) {
				cxp = charaLevel.getJSONObject(ilevel).getInt("level_up_exp");
				charaXP = cxp+"";
				cLevel = ilevel;
				totalCLevel = totalCharaLevel+"";
			}
			System.out.println(level + "\t" + ixp + "\t" + totalIslandLevel + "\t\t" + cLevel + "\t" + charaXP + "\t" + totalCLevel);
			totalIslandLevel += ixp;
			totalCharaLevel += cxp;
		}
		
	}
	
	public static void getTextbooks() {
		Set<String> tKeys = itemTemplate.keySet();
		for(String k : tKeys) {
			JSONObject item = itemTemplate.getJSONObject(k);
			String name = item.getString("name");
			if(name.contains("Textbook") && !name.contains("Production")) {
				String usage = item.getString("usage_arg");
				System.out.println(name + "\t"+ usage);
			}
		}
		
	}
	
	public static void getCraftRecipes() {
		Set<String> keys = formula.keySet();
		HashMap<String,Integer> itemCount = new HashMap<String,Integer>();
		for (String s : keys) {
			itemCount.clear();
			JSONObject formulaItems = formula.getJSONObject(s);
			String itemName = formulaItems.getString("name");
			JSONArray commProd = formulaItems.getJSONArray("commission_product");
			if(commProd.isEmpty())
				continue;
			int productCount = commProd.getJSONArray(0).getInt(1);
			int seasonpt = itemTemplate.getJSONObject(commProd.getJSONArray(0).getInt(0)+"").getInt("pt_num");
			JSONArray cost = formulaItems.getJSONArray("commission_cost");
			String out = productCount + "\t" + itemName + "\t" + seasonpt + "\t";
			for(int i =0; i< cost.length();i++) {
				String item = itemTemplate.getJSONObject( cost.getJSONArray(i).getInt(0)+"").getString("name");
				int count = cost.getJSONArray(i).getInt(1);
				out = out + count + "\t" + item + "\t";
			}
			System.out.println(out);
		}
	}
	
	public static void main(String[] args) {
		FileDownloader.updateFiles(false);
		//parseFood();
		//getFoodDemand();
		//getSeasonalStats();
		getProductCounts();
		//getCraftRecipes();
		//getLevels();
		//getTextbooks();
	}

}
