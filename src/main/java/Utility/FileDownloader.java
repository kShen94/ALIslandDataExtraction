package Utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public class FileDownloader {
	private static final String REPO_DIR = "AzurLaneTools/AzurLaneData";
	//private static final String REPO_DIR = "JustNagami/al-json";
	private static final String TARGET_FILE_DIRECTORY = "./src/main/resources/";
	private static final String BINARY_URL = "https://raw.githubusercontent.com/"+REPO_DIR+"/refs/heads/main/EN/";
	private static final String SHARECFG_BINARY_URL = "https://raw.githubusercontent.com/"+REPO_DIR+"/refs/heads/main/EN/ShareCfg/";
	private static final String LAST_MODIFIED_FILE = "lastModified.txt";
	private static HashMap<String, String> modifiedMap = new HashMap<String,String>();
	private static Boolean skipUpdate = false;
	
	
	public static void updateFiles(Boolean force) {
		try {
			readModifiedMap();
			if((skipUpdate && !force))
				return;
			for(String file:SHARECFG_FILE_LIST) {
				if (isUpdateAvailable(SHARECFG_BINARY_URL+file)) {
					downloadFile(SHARECFG_BINARY_URL+file, file);
				}
				else {
					System.out.println("Skipping "+file);
				}
			}

			saveModifiedMap();
		} catch (Exception e) {
			System.out.println("Error thrown, saving modified map");
			saveModifiedMap();
			e.printStackTrace();
		}
	}
	
	private static final String[] SHARECFG_FILE_LIST = {
			"island_chara_level.json",
			"island_farm_seed.json",
			"island_formula.json",
			"island_item_data_template.json",
			"island_level.json",
			"island_manage_restaurant.json",
			"island_production_place.json",
			"island_production_slot.json",
			"island_shop_goods.json"
	};
	
	private static void saveModifiedMap() {
		try(BufferedWriter wr = new BufferedWriter(new FileWriter(TARGET_FILE_DIRECTORY+LAST_MODIFIED_FILE,false))){
			wr.write(new Date().getTime()+"");
			wr.newLine();
			Set<String> keys = modifiedMap.keySet();
			for(String k:keys) {
				wr.write(k+","+modifiedMap.get(k));
				wr.newLine();
			}
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	private static void readModifiedMap() {
		Path file = Paths.get(TARGET_FILE_DIRECTORY+LAST_MODIFIED_FILE);
		if(Files.exists(file)) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(file.toFile()));
				String line;
				String[] pair;
				Date date = new Date(Long.valueOf(br.readLine())+432000000);
				// today - 5 days
				Date today = new Date(System.currentTimeMillis());
				if(date.after(today))
					skipUpdate = true;
				while((line = br.readLine()) !=null) {
					pair = line.split(",");
					modifiedMap.put(pair[0],pair[1]);
				}
				br.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static boolean isUpdateAvailable(String URL) throws IOException {
		String etag = modifiedMap.get(URL);
		HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
		connection.setRequestMethod("HEAD");
		connection.connect();
		Optional<String> requestedETag = Optional.of(connection.getHeaderField("ETag").replaceAll("\"",""));
		connection.disconnect();
		if(requestedETag.isEmpty() || (requestedETag.isPresent() && requestedETag.get().equals(etag))) {
			//do not update
			return false;
		}
		//update etag
		modifiedMap.put(URL, requestedETag.get());
		// Update available
		return true; 
	}
	
	private static void downloadFile(String URL, String fileName) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
		try (InputStream inputStream = connection.getInputStream();
				FileOutputStream outputStream = new FileOutputStream(TARGET_FILE_DIRECTORY+fileName)) {

			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			System.out.println(fileName +" Download complete.");
		}
	}
}
