package sailpoint.services.idn.sdk.object.identity.userList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import sailpoint.services.idn.sdk.object.Identity;

import java.util.List;

public class IdentityList {

	@SerializedName("total")
	@Expose
	private Integer total;

	@SerializedName("items")
	@Expose
	private List<Identity> items = null;

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public List<Identity> getItems() {
		return items;
	}

	public void setItems(List<Identity> items) {
		this.items = items;
	}

}
