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
import com.example.parking.model.ParkingSlot;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ParkingSlotMongoRepositoryTest {

	private static MongoServer server;
	private static InetSocketAddress serverAddress;

	private MongoClient client;
	private ParkingSlotMongoRepository slotRepository;
	private MongoCollection<Document> slotCollection;

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
		slotRepository = new ParkingSlotMongoRepository(client);
		MongoDatabase database = client.getDatabase(
				ParkingSlotMongoRepository.PARKING_DB_NAME);
		// make sure we always start with a clean database
		database.drop();
		slotCollection = database.getCollection(
				ParkingSlotMongoRepository.SLOT_COLLECTION_NAME);
	}

	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testFindAllWhenDatabaseIsEmpty() {
		assertThat(slotRepository.findAll()).isEmpty();
	}
	
	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		addTestSlotToDatabase("1", false);
		addTestSlotToDatabase("2", true);
		assertThat(slotRepository.findAll())
			.containsExactly(
				new ParkingSlot("1", false),
				new ParkingSlot("2", true));
	}

	private void addTestSlotToDatabase(String id, boolean occupied) {
		slotCollection.insertOne(
			new Document()
				.append("id", id)
				.append("occupied", occupied));
	}
	
	@Test
	public void testFindByIdNotFound() {
		assertThat(slotRepository.findById("1")).isNull();
	}

	@Test
	public void testFindByIdFound() {
		addTestSlotToDatabase("1", false);
		addTestSlotToDatabase("2", true);
		assertThat(slotRepository.findById("2"))
			.isEqualTo(new ParkingSlot("2", true));
	}
	
	@Test
	public void testSave() {
		ParkingSlot slot = new ParkingSlot("1", false);
		slotRepository.save(slot);
		assertThat(readAllSlotsFromDatabase())
			.containsExactly(slot);
	}

	@Test
	public void testDelete() {
		addTestSlotToDatabase("1", false);
		slotRepository.delete("1");
		assertThat(readAllSlotsFromDatabase())
			.isEmpty();
	}

	private List<ParkingSlot> readAllSlotsFromDatabase() {
		return StreamSupport
			.stream(slotCollection.find().spliterator(), false)
			.map(d -> new ParkingSlot("" + d.get("id"), (boolean) d.get("occupied")))
			.collect(Collectors.toList());
	}

}
