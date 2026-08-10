package com.example.parking.app.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;

@RunWith(GUITestRunner.class)
public class ParkingSwingAppE2E extends AssertJSwingJUnitTestCase {

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo =
		new GenericContainer("mongo:5").withExposedPorts(27017);

	private static final String DB_NAME = "test-db";
	private static final String SLOT_COLLECTION_NAME = "test-slot-collection";
	private static final String EVENT_COLLECTION_NAME = "test-event-collection";

	private static final String SLOT_FIXTURE_1_ID = "1";
	private static final String SLOT_FIXTURE_2_ID = "2";

	private MongoClient mongoClient;

	private FrameFixture window;

	@Override
	protected void onSetUp() {
		String containerIpAddress = mongo.getHost();
		Integer mappedPort = mongo.getMappedPort(27017);
		mongoClient = new MongoClient(containerIpAddress, mappedPort);
		// always start with an empty database
		mongoClient.getDatabase(DB_NAME).drop();
		// add some slots to the database
		addTestSlotToDatabase(SLOT_FIXTURE_1_ID, false);
		addTestSlotToDatabase(SLOT_FIXTURE_2_ID, true);
		// start the Swing application
		application("com.example.parking.app.swing.ParkingSwingApp")
			.withArgs(
				"--mongo-host=" + containerIpAddress,
				"--mongo-port=" + mappedPort.toString(),
				"--db-name=" + DB_NAME,
				"--slot-collection=" + SLOT_COLLECTION_NAME,
				"--event-collection=" + EVENT_COLLECTION_NAME
			)
			.start();
		// get a reference of its JFrame
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Parking View".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
	}

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	private void addTestSlotToDatabase(String id, boolean occupied) {
		mongoClient
			.getDatabase(DB_NAME)
			.getCollection(SLOT_COLLECTION_NAME)
			.insertOne(
				new Document()
					.append("id", id)
					.append("occupied", occupied));
	}

	@Test @GUITest
	public void testOnStartAllDatabaseElementsAreShown() {
		assertThat(window.list("slotList").contents())
			.anySatisfy(e -> assertThat(e).contains(SLOT_FIXTURE_1_ID))
			.anySatisfy(e -> assertThat(e).contains(SLOT_FIXTURE_2_ID));
	}

	@Test @GUITest
	public void testAddButtonSuccess() {
		window.textBox("idTextBox").enterText("10");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list("slotList").contents())
			.anySatisfy(e -> assertThat(e).contains("10"));
	}

	@Test @GUITest
	public void testAddButtonError() {
		window.textBox("idTextBox").enterText(SLOT_FIXTURE_1_ID);
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.label("errorMessageLabel").text())
			.contains(SLOT_FIXTURE_1_ID);
	}

	@Test @GUITest
	public void testMarkOccupiedButtonSuccess() {
		window.list("slotList")
			.selectItem(java.util.regex.Pattern.compile(".*id=" + SLOT_FIXTURE_1_ID + ".*"));
		window.button(JButtonMatcher.withText("Mark Occupied")).click();
		assertThat(window.list("slotList").contents())
			.anySatisfy(e -> assertThat(e).contains(SLOT_FIXTURE_1_ID, "true"));
	}

	@Test @GUITest
	public void testMarkFreeButtonSuccess() {
		window.list("slotList")
			.selectItem(java.util.regex.Pattern.compile(".*id=" + SLOT_FIXTURE_2_ID + ".*"));
		window.button(JButtonMatcher.withText("Mark Free")).click();
		assertThat(window.list("slotList").contents())
			.anySatisfy(e -> assertThat(e).contains(SLOT_FIXTURE_2_ID, "false"));
	}
}