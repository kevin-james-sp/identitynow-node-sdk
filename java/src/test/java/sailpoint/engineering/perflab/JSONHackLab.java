package sailpoint.engineering.perflab;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import sailpoint.services.idn.sdk.object.IAI.recommender.Responses;

public class JSONHackLab {

	public JSONHackLab() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		System.out.println("Hello World");
		
		String rawJson = "{\"responses\":[{\"request\":{\"identityId\":\"8a80828f643d484f01643d8e4f552169\",\"item\":{\"id\":\"8a808094696804590169732f112e371a\",\"type\":\"ENTITLEMENT\"}},\"recommendation\":\"NO\",\"interpretations\":[\"No similar users have this access.\"]}]}";
		// String rawJson = "responses:[{\"request\":{\"identityId\":\"8a80828f643d484f01643d8e4f552169\",\"item\":{\"id\":\"8a808094696804590169732f112e371a\",\"type\":\"ENTITLEMENT\"}},\"recommendation\":\"NO\",\"interpretations\":[\"No similar users have this access.\"]}]";
		
		System.out.println("rawJson:" + rawJson);
		
		Gson gson = new Gson();
		
		Responses r = new Responses();
		
		Responses rsp = gson.fromJson(rawJson, Responses.class);
		System.out.println("responses:" + rsp);
		
		Gson gsonpp = new GsonBuilder().setPrettyPrinting().create();
		String json = gsonpp.toJson(rsp);
		System.out.println("pretty printed:\n" + json);
		
		
		
		

	}

}
