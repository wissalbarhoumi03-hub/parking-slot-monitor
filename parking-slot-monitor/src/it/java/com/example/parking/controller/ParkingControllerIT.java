package com.example.parking.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.GenericContainer;

import com.example.parking.model.ParkingEvent;
import com.example.parking.model.ParkingSlot;
import com.example.parking.repository.ParkingEventRepository;
import com.example.parking.repository.ParkingSlotRepository;
import com.example.parking.repository.mongo.ParkingEventMongoRepository;
import com.example.parking.repository.mongo.ParkingSlotMongoRepository;
import com.example.parking.view.ParkingView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class ParkingControllerIT {

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo =
		new GenericContainer("mongo:5").withExposedPorts(27017);

	@Mock
	private ParkingView parkingView;

	private ParkingSlotRepository slotRepository;
	private ParkingEventRepository eventRepository;
	private ParkingController parkingController;

	private MongoClient client;
	private AutoCloseable closeable;

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		client = new MongoClient(
			new ServerAddress(
				mongo.getHost(),
				mongo.getMappedPort(27017)));
		// make sure we always start with a clean database
		client.getDatabase(ParkingSlotMongoRepository.PARKING_DB_NAME).drop();
		slotRepository = new ParkingSlotMongoRepository(client);
		eventRepository = new ParkingEventMongoRepository(client);
		parkingController = new ParkingController(parkingView, slotRepository, eventRepository);
	}
	
	@After
	public void tearDown() throws Exception {
		client.close();
		closeable.close();
	}

	@Test
	public void testAddSlot() {
		ParkingSlot slot = new ParkingSlot("1", false);
		parkingController.addSlot(slot);
		verify(parkingView).slotAdded(slot);
	}

	@Test
	public void testMarkOccupied() {
		slotRepository.save(new ParkingSlot("1", false));
		parkingController.markOccupied("1", "2026-01-01T10:00:00");
		verify(parkingView).slotUpdated(new ParkingSlot("1", true));
	}

	@Test
	public void testMarkFree() {
		slotRepository.save(new ParkingSlot("1", true));
		parkingController.markFree("1", "2026-01-01T10:00:00");
		verify(parkingView).slotUpdated(new ParkingSlot("1", false));
	}

	@Test
	public void testAllSlots() {
		ParkingSlot slot = new ParkingSlot("1", false);
		slotRepository.save(slot);
		parkingController.allSlots();
		verify(parkingView).showAllSlots(asList(slot));
	}

	@Test
	public void testShowHistory() {
		slotRepository.save(new ParkingSlot("1", false));
		parkingController.markOccupied("1", "2026-01-01T10:00:00");
		parkingController.showHistory();
		verify(parkingView).showHistory(asList(
			new ParkingEvent("1", true, "2026-01-01T10:00:00")));
	}

	@Test
	public void testCountSlots() {
		slotRepository.save(new ParkingSlot("1", false));
		slotRepository.save(new ParkingSlot("2", true));
		parkingController.countSlots();
		verify(parkingView).showCounts(1, 1);
	}

}