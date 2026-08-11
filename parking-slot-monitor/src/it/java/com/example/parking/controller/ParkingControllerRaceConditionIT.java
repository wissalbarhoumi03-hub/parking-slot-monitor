package com.example.parking.controller;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.List;
import java.util.stream.IntStream;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.GenericContainer;

import com.example.parking.model.ParkingSlot;
import com.example.parking.repository.ParkingSlotRepository;
import com.example.parking.repository.ParkingEventRepository;
import com.example.parking.repository.mongo.ParkingSlotMongoRepository;
import com.example.parking.repository.mongo.ParkingEventMongoRepository;
import com.example.parking.view.ParkingView;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

public class ParkingControllerRaceConditionIT {

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo =
		new GenericContainer("mongo:5").withExposedPorts(27017);

	@Mock
	private ParkingView parkingView;

	private ParkingSlotRepository slotRepository;
	private ParkingEventRepository eventRepository;

	private MongoClient client;
	private AutoCloseable closeable;

	@Before
	public void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		client = new MongoClient(
			new ServerAddress(
				mongo.getHost(),
				mongo.getMappedPort(27017)));
		MongoDatabase database =
			client.getDatabase(ParkingSlotMongoRepository.PARKING_DB_NAME);
		// make sure we always start with a clean database
		database.drop();
		// A unique index ensures that the indexed field
		// (in this case "id") does not store duplicate values:
		MongoCollection<Document> slotCollection =
			database.getCollection(ParkingSlotMongoRepository.SLOT_COLLECTION_NAME);
		slotCollection.createIndex(
			Indexes.ascending("id"), new IndexOptions().unique(true));
		slotRepository = new ParkingSlotMongoRepository(client,
				ParkingSlotMongoRepository.PARKING_DB_NAME,
				ParkingSlotMongoRepository.SLOT_COLLECTION_NAME);
		eventRepository = new ParkingEventMongoRepository(client,
				ParkingEventMongoRepository.PARKING_DB_NAME,
				ParkingEventMongoRepository.EVENT_COLLECTION_NAME);
	}

	@After
	public void tearDown() throws Exception {
		client.close();
		closeable.close();
	}

	@Test
	public void testAddSlotConcurrent() {
		ParkingSlot slot = new ParkingSlot("1", false);
		// start the threads calling addSlot concurrently
		// on different ParkingController instances, so 'synchronized'
		// methods in the controller will not help...
		List<Thread> threads = IntStream.range(0, 10)
			.mapToObj(i -> new Thread(() -> {
				try {
					new ParkingController(
						parkingView, slotRepository, eventRepository)
						.addSlot(slot);
				} catch (MongoWriteException e) {
					// duplicate key error: another thread already inserted
					e.printStackTrace();
				}
			}))
			.peek(Thread::start)
			.toList();
		// wait for all the threads to finish
		await().atMost(10, SECONDS)
		.until(() -> threads.stream().noneMatch(Thread::isAlive));
		// there should be a single element in the database
		assertThat(slotRepository.findAll())
			.containsExactly(slot);
	}
}