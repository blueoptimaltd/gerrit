package com.google.gerrit.common.replication;

import com.google.gerrit.common.GerritEventFactory;
import org.junit.BeforeClass;

import java.util.Map;
import java.util.Properties;

import static com.google.gerrit.common.replication.ReplicationConstants.GERRIT_REPLICATED_EVENT_WORKER_POOL_MIN_SIZE;
import static com.google.gerrit.common.replication.ReplicationConstants.GERRIT_REPLICATED_EVENT_WORKER_POOL_SIZE;
import static com.google.gerrit.common.replication.ReplicationConstants.REPLICATION_DISABLED;


public class AbstractReplicationTesting {

  static TestingReplicatedEventsCoordinator dummyTestCoordinator;

  @BeforeClass
  public static void beforeClass() throws Exception {
    setupReplicatedEventsCoordinatorProps();
  }

  /**
   * Keep default constructor - allowing for replication with simple override properties.
   *
   * It is passed no extra properties, and runs in a simple replication disabled fashion
   * which is the expected behaviour for most tests. i.e. no gitms requirements, and
   * we are ok with the system default configuration values
   *
   * @throws Exception
   */
  public static void setupReplicatedEventsCoordinatorProps() throws Exception {

    // Allow for testing specific properties by default which restrict the worker pool size etc.
    setupReplicatedEventsCoordinatorProps(true, null, false);
  }

  public static void setupReplicatedEventsCoordinatorProps(boolean replicationDisabled, Properties extraProperties, boolean ignoreDefaultTestingProperties) throws Exception {
    // make sure to clear - really we want to call disable in before class and only enable for one test.
    SingletonEnforcement.clearAll();
    SingletonEnforcement.setDisableEnforcement(true);

    Properties testingProperties = new Properties();

    // SET our pool to 2 items, plus the 2 core projects.
    if( !ignoreDefaultTestingProperties ) {
      testingProperties.put(GERRIT_REPLICATED_EVENT_WORKER_POOL_MIN_SIZE, "2");
      testingProperties.put(GERRIT_REPLICATED_EVENT_WORKER_POOL_SIZE, "2");
    }

    testingProperties.put(REPLICATION_DISABLED, replicationDisabled);

    if (extraProperties != null && !extraProperties.isEmpty()) {
      testingProperties.putAll(extraProperties);
    }

    dummyTestCoordinator = new TestingReplicatedEventsCoordinator(testingProperties);

    // Initialize our event factory gson...
    GerritEventFactory.setupEventWrapper();
  }
}
