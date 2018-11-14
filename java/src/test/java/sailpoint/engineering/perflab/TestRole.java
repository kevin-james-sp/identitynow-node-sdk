package sailpoint.engineering.perflab;

import retrofit2.Response;
import sailpoint.services.idn.sdk.EnvironmentCredentialer;
import sailpoint.services.idn.sdk.IdentityNowService;
import sailpoint.services.idn.sdk.object.entitlement.Entitlement;
import sailpoint.services.idn.sdk.object.entitlement.EntitlementList;
import sailpoint.services.idn.sdk.object.role.*;
import sailpoint.services.idn.sdk.object.source.Source;
import sailpoint.services.idn.sdk.services.EntitlementService;
import sailpoint.services.idn.sdk.services.RoleService;
import sailpoint.services.idn.sdk.services.SourceService;
import sailpoint.services.idn.session.SessionType;
import sailpoint.services.idn.session.UserInterfaceSession;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestRole {

    public static void main(String[] args) throws Exception {


        IdentityNowService ids = new IdentityNowService(EnvironmentCredentialer.getEnvironmentCredentials());
        ids.createSession(SessionType.SESSION_TYPE_UI_USER_BASIC, true);

        RoleService rs = ids.getRoleService();
        SourceService ss = ids.getSourceService();
        EntitlementService es = ids.getEntitlementService();

        try {
            List<Source> sourceList = ss.list().execute().body();
            if (sourceList == null || sourceList.size() == 0) {
                System.out.println("Unable to load sources.");
                return;
            }

            Source perfADSource = sourceList.stream().parallel().filter(source -> source.name.equals("IdN Perf Lab AD source"))
                    .findAny().orElse(null);
            if (perfADSource == null) {
                System.out.println("Unable to find perf AD source. Please create that source first through AbleMan.");
                return;
            }

            EntitlementList entitlementListResponse = es.list(20, perfADSource.externalId).execute().body();//TODO: 2000
            if (entitlementListResponse == null || entitlementListResponse.count == 0) {
                System.out.println("Unable to find entitlements for perf AD source. Please aggregate the source first.");
                return;
            }

            List<Entitlement> entitlementList = entitlementListResponse.items;
            if (entitlementList.size() < 20) {//TODO 1500
                System.out.println("Do not have enough entitlements to construct 3000 groups");
                return;
            }

            int roleCount = 0;

            //Creating roles with single group
            /*for (Entitlement entitlement : entitlementList) {
                Role role = rs.create(new Role("Perf role " + ++roleCount, entitlement.value)).execute().body();
                if (role == null) continue;

                RoleCriterionKey key = new RoleCriterionKey("ENTITLEMENT", "attribute.memberOf", perfADSource.externalId);
                RoleCriterion roleCriterion = new RoleCriterion("EQUALS", key, entitlement.value);
                ComplexRoleCriterion complexRoleCriterion = new ComplexRoleCriterion("OR", Collections.singletonList(roleCriterion));
                Selector selector = new Selector("COMPLEX_CRITERIA", complexRoleCriterion);
                role.setSelector(selector);

                Response<Role> response = rs.update(role).execute();
                if (response.isSuccessful()) {
                    System.out.println("Role " + roleCount + " successfully created.");
                }
            }*/

            roleCount = 30;//TODO: remove

            //Creating roles with two groups
            //roleCount = createRoleWithRandomGroups(rs, entitlementList, 2, perfADSource.externalId, roleCount, 30);//TODO: 2900

            roleCount = createRoleWithRandomGroups(rs, entitlementList, 10, perfADSource.externalId, roleCount, 35);//TODO: 2950
            roleCount = createRoleWithRandomGroups(rs, entitlementList, 20, perfADSource.externalId, roleCount, 40);//TODO: 3000

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int createRoleWithRandomGroups (RoleService roleService, List<Entitlement> entitlementList, int groupsPerRole, String sourceExtId, int roleCount, int targetCount) throws IOException {
        while (++roleCount <= targetCount) {
            Role role = roleService.create(new Role("Perf role " + roleCount, "Role with " + groupsPerRole + " groups.")).execute().body();
            if (role == null) continue;

            Random random = new Random();
            List<Entitlement> selectedEntitlements = IntStream.generate(() -> random.nextInt(entitlementList.size())).distinct().limit(groupsPerRole)
                    .mapToObj(entitlementList::get).collect(Collectors.toList());

            List<RoleCriterion> roleCriterionList = selectedEntitlements.parallelStream()
                    .map(entitlement -> new RoleCriterion("EQUALS", new RoleCriterionKey("ENTITLEMENT", "attribute.memberOf", sourceExtId), entitlement.value))
                    .collect(Collectors.toList());

            role.selector = new Selector("COMPLEX_CRITERIA", new ComplexRoleCriterion("OR", roleCriterionList));

            Response<Role> response = roleService.update(role).execute();
            if (response.isSuccessful()) {
                System.out.println("Role " + roleCount + " successfully created.");
            }
        }
        return roleCount;
    }
}
