import java.io.FileInputStream;

import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonData {

		public static JSONObject formula;
		public static JSONObject itemDataTemplate;
		public static JSONObject manageRestaurant;
		public static JSONObject productionSlot;
		public static JSONObject productionPlace;
		public static JSONObject farmSeed;
		public static JSONObject shopGoods;
		public static JSONObject islandLevel;
		public static JSONObject charaLevel;
		
		static {
			try {
				formula = new JSONObject(new JSONTokener(new FileInputStream("./src/main/resources/island_formula.json")));
				formula.remove("all");
				itemDataTemplate = new JSONObject(new JSONTokener(new FileInputStream("./src/main/resources/island_item_data_template.json")));
				manageRestaurant = new JSONObject(new JSONTokener(new FileInputStream("./src/main/resources/island_manage_restaurant.json")));
				manageRestaurant.remove("all");
				productionSlot = new JSONObject(new JSONTokener(new FileInputStream("./src/main/resources/island_production_slot.json")));
				productionPlace = new JSONObject(new JSONTokener(new FileInputStream("./src/main/resources/island_production_place.json")));
				productionPlace.remove("all");
				productionPlace.remove("get_id_list_by_map_id");
				farmSeed = new JSONObject(new JSONTokener(new FileInputStream("./src/main/resources/island_farm_seed.json")));
				farmSeed.remove("all");
				shopGoods = new JSONObject(new JSONTokener(new FileInputStream("./src/main/resources/island_shop_goods.json")));
				islandLevel = new JSONObject(new JSONTokener(new FileInputStream("./src/main/resources/island_level.json")));
				islandLevel.remove("all");
				charaLevel = new JSONObject(new JSONTokener(new FileInputStream("./src/main/resources/island_chara_level.json")));
				charaLevel.remove("all");
				
			}catch(Exception e) {
				System.out.print(e.getMessage());
			}
		}
	
}
