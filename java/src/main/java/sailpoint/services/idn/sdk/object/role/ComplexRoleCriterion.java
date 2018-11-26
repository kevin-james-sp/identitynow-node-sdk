package sailpoint.services.idn.sdk.object.role;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ComplexRoleCriterion {
    @SerializedName("operation")
    public String operation;

    @SerializedName("children")
    public List<RoleCriterion> children;

    public ComplexRoleCriterion (String operation, List<RoleCriterion> children) {
        this.operation = operation;
        this.children = children;
    }
}
