package sailpoint.services.idn.sdk.object.identity.userList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Filter {

	@SerializedName("property")
	@Expose
	private String property;

	@SerializedName("value")
	@Expose
	private String value;

	public Filter(String property, String value) {
		this.property = property;
		this.value = value;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
