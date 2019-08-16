package sailpoint.services.idn.sdk.object.identity.userList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Filters {

	//Accepts OR, AND
	@SerializedName("joinOperator")
	@Expose
	private String joinOperator;

	@SerializedName("filter")
	@Expose
	private List<Filter> filterList;

	public Filters(String joinOperator, List<Filter> filterList) {
		this.joinOperator = joinOperator;
		this.filterList = filterList;
	}

	public String getJoinOperator() {
		return joinOperator;
	}

	public void setJoinOperator(String joinOperator) {
		this.joinOperator = joinOperator;
	}

	public List<Filter> getFilterList() {
		return filterList;
	}

	public void setFilterList(List<Filter> filterList) {
		this.filterList = filterList;
	}
}
