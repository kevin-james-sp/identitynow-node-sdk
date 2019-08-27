package sailpoint.services.idn.sdk.object.identity.userList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sorter {

	@SerializedName("property")
	@Expose
	private String property;

	//Accepts ASC
	@SerializedName("direction")
	@Expose
	private String direction;

	public Sorter(String property, String direction) {
		this.property = property;
		this.direction = direction;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}
}
