package sailpoint.services.idn.sdk.object.role;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Selector {
    @SerializedName("type")
    String type;

    @SerializedName("complexRoleCriterion")
    ComplexRoleCriterion complexRoleCriterion;

    public Selector (String type, ComplexRoleCriterion complexRoleCriterion) {
        this.type = type;
        this.complexRoleCriterion = complexRoleCriterion;
    }
}