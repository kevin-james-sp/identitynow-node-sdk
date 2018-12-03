package sailpoint.engineering.perflab;

import com.jcraft.jsch.*;

import java.sql.*;

public class TwoMFADriver {

    private static final String SSH_USERNAME = "fangmingning";
    private static final String JUMP_BOX_URL = "jb1-dev02-useast1.cloud.sailpoint.com";
    private static final String CC_INSTANCE_URL = "10.0.38.30";
    private static final String CC_RDS_MYSQL_URL = "dev02-useast1-cc.ce7gg2eo7hdc.us-east-1.rds.amazonaws.com";
    private static final String CC_RDS_MYSQL_USERNAME = "admin20170151";
    private static final String CC_RDS_MYSQL_PASSWORD = "a946c53336";



    public static void main(String[] args) throws Exception {

        JSch jsch=new JSch();
        jsch.addIdentity("~/.ssh/id_rsa");

        //Connecting to jump box
        Session jumpBoxSession = jsch.getSession(SSH_USERNAME, JUMP_BOX_URL);
        jumpBoxSession.setConfig("StrictHostKeyChecking", "no");
        jumpBoxSession.connect();

        //Port forwarding to the ssh port on CC instance
        int ccSshPort = jumpBoxSession.setPortForwardingL(0, CC_INSTANCE_URL, 22);

        //Connecting to CC instance
        Session serverSession = jsch.getSession(SSH_USERNAME, "localhost", ccSshPort);
        serverSession.setConfig("StrictHostKeyChecking", "no");
        serverSession.connect();

        //Port forwarding to the mysql port on mysql instance
        int mysqlPort = serverSession.setPortForwardingL(0, CC_RDS_MYSQL_URL, 3306);

        //Run mysql query
        try {
            Class.forName("com.mysql.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:" + mysqlPort, CC_RDS_MYSQL_USERNAME, CC_RDS_MYSQL_PASSWORD)){
                try (Statement statement = connection.createStatement()) {

                    ResultSet resultSet = statement.executeQuery("select description from cloudcommander.proc");
                    while(resultSet.next()) {
                        System.out.println(resultSet.getString(1));
                    }


                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //Disconnect session in order
        serverSession.disconnect();
        jumpBoxSession.disconnect();

    }

}
