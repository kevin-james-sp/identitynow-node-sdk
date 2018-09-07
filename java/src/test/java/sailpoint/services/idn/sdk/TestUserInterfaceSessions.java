package sailpoint.services.idn.sdk;

import okhttp3.OkHttpClient;
import org.junit.*;
import sailpoint.services.idn.session.UserInterfaceSession;

import java.net.Proxy;

import static org.junit.Assert.*;

public class TestUserInterfaceSessions {

    private static String originalProxyType;
    private static String originalProxyHost;
    private static String originalProxyPort;

    private UserInterfaceSession uiSession;

    @BeforeClass
    public static void backup() {
        // Preserve proxy settings before making changes
        // If this file is executed within suites of other tests, we want to set the proxy back before running other tests, which may be using the original proxy values.
        originalProxyType = System.getProperty("proxyType");
        originalProxyHost = System.getProperty("proxyHost");
        originalProxyPort = System.getProperty("proxyPort");
    }

    @AfterClass
    public static void restore() {
        if (originalProxyType != null) System.setProperty("proxyType", originalProxyType);
        else System.clearProperty("proxyType");

        if (originalProxyHost != null) System.setProperty("proxyHost", originalProxyHost);
        else System.clearProperty("proxyHost");

        if (originalProxyPort != null) System.setProperty("proxyPort", originalProxyPort);
        else System.clearProperty("proxyPort");
    }

    @Before
    public void initSingleton() {
        if (uiSession == null) uiSession = new UserInterfaceSession(EnvironmentCredentialer.getEnvironmentCredentials());
    }

    @Test
    public void testHTTPClientWithoutProxy() {
        // Clear proxy type so that proxy won't be set
        System.clearProperty("proxyType");

        try {
            OkHttpClient.Builder builder = uiSession.getCommonOkClientBuilder();
            assertNull(builder.build().proxy());
        } catch (Exception e) {
            e.printStackTrace();
            fail("The proxy is not set at all but proxy setup exception is thrown.");
        }
    }

    @Test
    public void testHTTPClientWithValidProxy() {
        System.setProperty("proxyType", "HTTP");
        System.setProperty("proxyHost", "192.168.0.1");
        System.setProperty("proxyPort", "80");

        try {
            OkHttpClient.Builder builder = uiSession.getCommonOkClientBuilder();
            assertNotNull(builder.build().proxy());
        } catch (Exception e) {
            e.printStackTrace();
            fail("The proxy is valid but exception is thrown.");
        }

        System.setProperty("proxyType", "SockS");
        try {
            OkHttpClient client = uiSession.getCommonOkClientBuilder().build();
            assertEquals(client.proxy().type(), Proxy.Type.SOCKS);
            assertEquals(client.proxy().address().toString(), "/192.168.0.1:80");
        } catch (Exception e) {
            e.printStackTrace();
            fail("The proxy is valid but exception is thrown.");
        }
    }

    @Test
    public void testHTTPClientWithInvalidProxy() {
        System.setProperty("proxyType", "HTTP");
        System.setProperty("proxyHost", "192.168.0.1");
        System.setProperty("proxyPort", "string");

        try {
            uiSession.getCommonOkClientBuilder();
            fail("The proxy is invalid but exception is not thrown.");
        } catch (Exception e) {
            assertEquals(e.getClass(), NumberFormatException.class);
        }

        System.setProperty("proxyType", "string");
        try {
            uiSession.getCommonOkClientBuilder();
            fail("The proxy is invalid but exception is not thrown.");
        } catch (Exception e) {
            assertEquals(e.getClass(), IllegalArgumentException.class);
        }

    }
}
