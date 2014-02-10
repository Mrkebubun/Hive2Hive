package org.hive2hive.core.test.processes.implementations.common;

import java.util.ArrayList;
import java.util.List;

import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

import org.hive2hive.core.H2HConstants;
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.model.Locations;
import org.hive2hive.core.network.NetworkManager;
import org.hive2hive.core.processes.implementations.common.GetUserLocationsStep;
import org.hive2hive.core.processes.implementations.context.interfaces.IProvideLocations;
import org.hive2hive.core.test.H2HJUnitTest;
import org.hive2hive.core.test.network.NetworkTestUtil;
import org.hive2hive.core.test.processes.util.UseCaseTestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the generic step that puts the location into the DHT
 * 
 * @author Nico, Seppi
 * 
 */
public class GetLocationStepTest extends H2HJUnitTest {

	private static List<NetworkManager> network;
	private static final int networkSize = 2;

	@BeforeClass
	public static void initTest() throws Exception {
		testClass = GetLocationStepTest.class;
		beforeClass();
		network = NetworkTestUtil.createNetwork(networkSize);
	}

	@Test
	public void testStepSuccess() throws InterruptedException, NoPeerConnectionException {
		NetworkManager getter = network.get(0); // where the process runs
		NetworkManager proxy = network.get(1); // where the user profile is stored

		// create the needed objects
		String userId = proxy.getNodeId();
		Locations newLocations = new Locations(userId);
		newLocations.addPeerAddress(getter.getPeerAddress());

		Number160 lKey = Number160.createHash(userId);
		Number160 dKey = Number160.ZERO;
		Number160 cKey = Number160.createHash(H2HConstants.USER_LOCATIONS);

		// put the locations to the DHT
		proxy.getDataManager().put(lKey, dKey, cKey, newLocations, null).awaitUninterruptibly();

		GetLocationsContext context = new GetLocationsContext();
		GetUserLocationsStep step = new GetUserLocationsStep(userId, context, getter.getDataManager());
		UseCaseTestUtil.executeProcess(step);

		// verify if both objects are the same
		Assert.assertEquals(userId, context.locations.getUserId());

		List<PeerAddress> onlinePeers = new ArrayList<PeerAddress>(context.locations.getPeerAddresses());
		Assert.assertEquals(getter.getPeerAddress(), onlinePeers.get(0));
	}

	@Test
	public void testStepSuccessWithNoLocations() throws NoPeerConnectionException {
		NetworkManager getter = network.get(0); // where the process runs
		NetworkManager proxy = network.get(1); // where the user profile is stored

		// create the needed objects, put no locations
		String userId = proxy.getNodeId();

		GetLocationsContext context = new GetLocationsContext();
		GetUserLocationsStep step = new GetUserLocationsStep(userId, context, getter.getDataManager());
		UseCaseTestUtil.executeProcess(step);

		Assert.assertNull(context.locations);
	}

	@AfterClass
	public static void endTest() {
		NetworkTestUtil.shutdownNetwork(network);
		afterClass();
	}

	private class GetLocationsContext implements IProvideLocations {

		public Locations locations;

		@Override
		public void provideLocations(Locations locations) {
			this.locations = locations;
		}

	}
}