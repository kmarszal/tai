package tai;

import java.util.logging.Logger;

import com.google.common.collect.ImmutableMap;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

public class UserDAO {
	private static final ColumnFamily<String, String> CF_USER_INFO =
			  new ColumnFamily<String, String>(
					    "Users",              // Column Family Name
					    StringSerializer.get(),   // Key Serializer
					    StringSerializer.get());  // Column Serializer
	
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
	
	private Keyspace keyspace;
	
	public UserDAO() throws ConnectionException {
		AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
		    .forCluster("Test Cluster")
		    .forKeyspace("ks")
		    .withAstyanaxConfiguration(new AstyanaxConfigurationImpl()
		    		        .setCqlVersion("3.4.5")
		    		        .setTargetCassandraVersion("3.11.2")
		        .setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE)
		    )
		    .withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl("MyConnectionPool")
		        .setPort(9160)
		        .setMaxConnsPerHost(1)
		        .setSeeds("127.0.0.1:9160")
		    )
		    .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
		    .buildKeyspace(ThriftFamilyFactory.getInstance());
		context.start();
		this.keyspace = context.getClient();
	}
	
	private void createKeyspaceAndColumnFamily() throws ConnectionException {
		keyspace.createKeyspace(ImmutableMap.<String, Object>builder()
			    .put("strategy_options", ImmutableMap.<String, Object>builder()
			        .put("replication_factor", "1")
			        .build())
			    .put("strategy_class",     "SimpleStrategy")
			        .build()
			     );
		
		keyspace.createColumnFamily(CF_USER_INFO, null);
	}
	
	public boolean insertUser(User user) {
		MutationBatch m = keyspace.prepareMutationBatch();

		m.withRow(CF_USER_INFO, user.getName())
		  .putColumn("password", user.getPassword(), null)
		  .putColumn("type", user.getType(), null);

		try {
		  m.execute();
		  return true;
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public User getUser(String name) {
		OperationResult<ColumnList<String>> result;
		User user = null;
		try {
			result = keyspace.prepareQuery(CF_USER_INFO)
					.getRow(name)
				    .execute();
			user = new User(name, 
					result.getResult().getStringValue("password", ""), 
					result.getResult().getStringValue("type", ""));
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
		return user;
	}
	
	public boolean deleteUser(User user) {
		MutationBatch m = keyspace.prepareMutationBatch();

		m.withRow(CF_USER_INFO, user.getName()).delete();
			
		try {
			m.execute();
		  	LOGGER.info("User " + user.getName() + " deleted successfully");
		  	return true;
		} catch (ConnectionException e) {
			LOGGER.info("User " + user.getName() + " not deleted");
			e.printStackTrace();
		}
		return false;
	}
	
	public void printHello() {
		LOGGER.info("hello");
	}
	
	public static void main(String args[]) {
		UserDAO userDAO;
		try {
			userDAO = new UserDAO();
			userDAO.insertUser(new User("name","pass","user"));
			User user = userDAO.getUser("name");
			LOGGER.info(user.getName() + " " + user.getPassword() + " " + user.getType());
			userDAO.deleteUser(user);
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
	}
}
