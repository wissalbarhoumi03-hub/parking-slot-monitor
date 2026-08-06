package com.example.parking.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.parking.controller.ParkingController;
import com.example.parking.model.ParkingSlot;
import com.example.parking.repository.mongo.ParkingSlotMongoRepository;
import com.example.parking.repository.mongo.ParkingEventMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

@RunWith(GUITestRunner.class)
public class ParkingSwingViewIT extends AssertJSwingJUnitTestCase {

	private static MongoServer server;
	private static InetSocketAddress serverAddress;

	private MongoClient mongoClient;

	private FrameFixture window;
	private ParkingSwingView parkingSwingView;
	private ParkingController parkingController;
	private ParkingSlotMongoRepository slotRepository;
	private ParkingEventMongoRepository eventRepository;

	@BeforeClass
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}

	@AfterClass
	public static void shutdownServer() {
		server.shutdown();
	}

	@Override
	protected void onSetUp() {
		mongoClient = new MongoClient(new ServerAddress(serverAddress));
		slotRepository = new ParkingSlotMongoRepository(mongoClient);
		eventRepository = new ParkingEventMongoRepository(mongoClient);
		// explicit empty the database through the repository
		for (ParkingSlot slot : slotRepository.findAll()) {
			slotRepository.delete(slot.getId());
		}
		GuiActionRunner.execute(() -> {
			parkingSwingView = new ParkingSwingView();
			parkingController = new ParkingController(
				parkingSwingView, slotRepository, eventRepository);
			parkingSwingView.setParkingController(parkingController);
			return parkingSwingView;
		});
		window = new FrameFixture(robot(), parkingSwingView);
		window.show();
	}

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	@Test @GUITest
	public void testAllSlots() {
		ParkingSlot slot1 = new ParkingSlot("1", false);
		ParkingSlot slot2 = new ParkingSlot("2", true);
		slotRepository.save(slot1);
		slotRepository.save(slot2);
		GuiActionRunner.execute(() -> parkingController.allSlots());
		assertThat(window.list("slotList").contents())
			.containsExactly(slot1.toString(), slot2.toString());
	}

	@Test @GUITest
	public void testAddButtonSuccess() {
		window.textBox("idTextBox").enterText("1");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list("slotList").contents())
			.containsExactly(new ParkingSlot("1", false).toString());
	}

	@Test @GUITest
	public void testAddButtonError() {
		slotRepository.save(new ParkingSlot("1", false));
		window.textBox("idTextBox").enterText("1");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list("slotList").contents())
			.isEmpty();
		window.label("errorMessageLabel")
			.requireText("Already existing slot with id 1: "
				+ new ParkingSlot("1", false));
	}

	@Test @GUITest
	public void testMarkOccupiedButtonSuccess() {
		GuiActionRunner.execute(
			() -> parkingController.addSlot(new ParkingSlot("1", false)));
		window.list("slotList").selectItem(0);
		window.button(JButtonMatcher.withText("Mark Occupied")).click();
		assertThat(window.list("slotList").contents())
			.containsExactly(new ParkingSlot("1", true).toString());
	}

	@Test @GUITest
	public void testMarkFreeButtonSuccess() {
		GuiActionRunner.execute(
			() -> parkingController.addSlot(new ParkingSlot("1", true)));
		window.list("slotList").selectItem(0);
		window.button(JButtonMatcher.withText("Mark Free")).click();
		assertThat(window.list("slotList").contents())
			.containsExactly(new ParkingSlot("1", false).toString());
	}
}