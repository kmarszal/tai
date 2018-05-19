package tai;

import java.util.logging.Logger;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

public class UserManager {
	private static final ColumnFamily<String, String> CF_USER_INFO =
			  new ColumnFamily<String, String>(
					    "Users",              // Column Family Name
					    StringSerializer.get(),   // Key Serializer
					    StringSerializer.get());  // Column Serializer
    private static final Logger LOGGER = Logger.getLogger(ProfilingFilter.class.getName());
	
    //private KeyspaceManager keyspaceManager;
	private Keyspace keyspace;
	
	public UserManager() throws ConnectionException {
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
		/*
		//create keyspace
		keyspace.createKeyspace(ImmutableMap.<String, Object>builder()
			    .put("strategy_options", ImmutableMap.<String, Object>builder()
			        .put("replication_factor", "1")
			        .build())
			    .put("strategy_class",     "SimpleStrategy")
			        .build()
			     );
		
		//create column family
		keyspace.createColumnFamily(CF_USER_INFO, null);
		*/
	}
	
	public void insertUser() {
		MutationBatch m = keyspace.prepareMutationBatch();

		m.withRow(CF_USER_INFO, "111")
		  .putColumn("name", "john", null)
		  .putColumn("password", "smith", null)
		  .putColumn("type", "user", null);

		try {
		  OperationResult<Void> result = m.execute();
		  LOGGER.info("inserted");
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
	}
	
	public void getUser() {
		OperationResult<ColumnList<String>> result;
		try {
			result = keyspace.prepareQuery(CF_USER_INFO)
			    .getKey("111")
			    .execute();
			
			for (Column<String> c : result.getResult()) {
			  LOGGER.info(c.getStringValue());
			}
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteUser() {
		MutationBatch m = keyspace.prepareMutationBatch();

		m.withRow(CF_USER_INFO, "111").delete();

		try {
		  OperationResult<Void> result = m.execute();
		  LOGGER.info("deleted");
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
	}
	
	public void printHello() {
		LOGGER.info("hello");
	}
	
	public static void main(String args[]) {
		UserManager userManager;
		try {
			userManager = new UserManager();
			userManager.insertUser();
			userManager.getUser();
			userManager.deleteUser();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
	}
}
