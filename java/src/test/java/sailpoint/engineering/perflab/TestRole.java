package sailpoint.engineering.perflab;

import retrofit2.Response;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.sdk.IdentityNowService;
import sailpoint.services.idn.sdk.object.role.*;
import sailpoint.services.idn.sdk.services.RoleService;
import sailpoint.services.idn.session.SessionType;
import sailpoint.services.idn.session.UserInterfaceSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class TestRole {

    public static void main(String[] args) throws Exception {


        IdentityNowService ids = new IdentityNowService(EnvironmentCredentialer.getEnvironmentCredentials());
        ids.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC, true);

        RoleService rs = ids.getRoleService();

        try {
            Response<Role> response = rs.create(new Role("aaafsdfdsfdsfdsfsdf", "it works!")).execute();

            int code = response.code();
            Role role = response.body();

            System.out.println(response.body().toString());

            RoleCriterionKey key = new RoleCriterionKey("ENTITLEMENT", "attribute.memberOf", "2c91808366e5f06c0166e9a81b380a24");
            RoleCriterion roleCriterion = new RoleCriterion("EQUALS", key, "CN=group260,OU=groups,DC=TestAutomationAD,DC=local");
            ComplexRoleCriterion complexRoleCriterion = new ComplexRoleCriterion("OR", Collections.singletonList(roleCriterion));
            Selector selector = new Selector("COMPLEX_CRITERIA", complexRoleCriterion);
            role.setSelector(selector);

            response = rs.update(role).execute();

            code = response.code();
            role = response.body();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
