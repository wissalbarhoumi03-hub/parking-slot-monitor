package com.example.parking.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import com.example.parking.model.ParkingEvent;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ParkingEventMongoRepositoryTestcontainersIT {

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo =
		new GenericContainer("mongo:5").withExposedPorts(27017);

	private MongoClient client;
	private ParkingEventMongoRepository eventRepository;
	private MongoCollection<Document> eventCollection;

	@Before
	public void setup() {
		client = new MongoClient(
			new ServerAddress(
				mongo.getHost(),
				mongo.getMappedPort(27017)));
		eventRepository = new ParkingEventMongoRepository(client,
				ParkingEventMongoRepository.PARKING_DB_NAME,
				ParkingEventMongoRepository.EVENT_COLLECTION_NAME);
		MongoDatabase database = client.getDatabase(
				ParkingEventMongoRepository.PARKING_DB_NAME);
		// make sure we always start with a clean database
		database.drop();
		eventCollection = database.getCollection(
				ParkingEventMongoRepository.EVENT_COLLECTION_NAME);
	}

	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void test() {
		// just to check that we can connect to the container
	}

	@Test
	public void testFindAll() {
		addTestEventToDatabase("1", false, "2026-01-01T10:00:00");
		addTestEventToDatabase("2", true, "2026-01-01T11:00:00");
		assertThat(eventRepository.findAll())
			.containsExactly(
				new ParkingEvent("1", false, "2026-01-01T10:00:00"),
				new ParkingEvent("2", true, "2026-01-01T11:00:00"));
	}

	@Test
	public void testSave() {
		ParkingEvent event = new ParkingEvent("1", true, "2026-01-01T10:00:00");
		eventRepository.save(event);
		assertThat(readAllEventsFromDatabase())
			.containsExactly(event);
	}

	private List<ParkingEvent> readAllEventsFromDatabase() {
		return StreamSupport
			.stream(eventCollection.find().spliterator(), false)
			.map(d -> new ParkingEvent(
					"" + d.get("slotId"),
					(boolean) d.get("occupied"),
					"" + d.get("timestamp")))
			.collect(Collectors.toList());
	}

	private void addTestEventToDatabase(String slotId, boolean occupied, String timestamp) {
		eventCollection.insertOne(
			new Document()
				.append("slotId", slotId)
				.append("occupied", occupied)
				.append("timestamp", timestamp));
	}

}