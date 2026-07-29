package com.example.parking.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;

import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import com.example.parking.model.ParkingEvent;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ParkingEventMongoRepositoryTest {

	private static MongoServer server;
	private static InetSocketAddress serverAddress;

	private MongoClient client;
	private ParkingEventMongoRepository eventRepository;
	private MongoCollection<Document> eventCollection;

	@BeforeClass
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		// bind on a random local port
		serverAddress = server.bind();
	}

	@AfterClass
	public static void shutdownServer() {
		server.shutdown();
	}

	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(serverAddress));
		eventRepository = new ParkingEventMongoRepository(client);
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
	public void testFindAllWhenDatabaseIsEmpty() {
		assertThat(eventRepository.findAll()).isEmpty();
	}
	
	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		addTestEventToDatabase("1", false, "2026-01-01T10:00:00");
		addTestEventToDatabase("2", true, "2026-01-01T11:00:00");
		assertThat(eventRepository.findAll())
			.containsExactly(
				new ParkingEvent("1", false, "2026-01-01T10:00:00"),
				new ParkingEvent("2", true, "2026-01-01T11:00:00"));
	}

	private void addTestEventToDatabase(String slotId, boolean occupied, String timestamp) {
		eventCollection.insertOne(
			new Document()
				.append("slotId", slotId)
				.append("occupied", occupied)
				.append("timestamp", timestamp));
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

}