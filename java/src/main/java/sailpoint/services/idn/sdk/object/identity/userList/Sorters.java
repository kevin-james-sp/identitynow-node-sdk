package sailpoint.services.idn.sdk.object.identity.userList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Sorters {

	@SerializedName("sorters")
	@Expose
	private List<Sorter> sorterList;

	public Sorters(List<Sorter> sorterList) {
		this.sorterList = sorterList;
	}

	public List<Sorter> getSorterList() {
		return sorterList;
	}

	public void setSorterList(List<Sorter> sorterList) {
		this.sorterList = sorterList;
	}
}
