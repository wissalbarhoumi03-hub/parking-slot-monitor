package com.example.parking.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;


import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import com.example.parking.model.ParkingSlot;

import java.util.List;
import java.util.stream.StreamSupport;

public class ParkingSlotMongoRepositoryTestcontainersIT {

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo =
		new GenericContainer("mongo:5").withExposedPorts(27017);

	private MongoClient client;
	private ParkingSlotMongoRepository slotRepository;
	private MongoCollection<Document> slotCollection;

	@Before
	public void setup() {
		client = new MongoClient(
			new ServerAddress(
				mongo.getHost(),
				mongo.getMappedPort(27017)));
		slotRepository = new ParkingSlotMongoRepository(client,
				ParkingSlotMongoRepository.PARKING_DB_NAME,
				ParkingSlotMongoRepository.SLOT_COLLECTION_NAME);
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
	public void test() {
		// just to check that we can connect to the container
	}

	@Test
	public void testFindAll() {
		addTestSlotToDatabase("1", false);
		addTestSlotToDatabase("2", true);
		assertThat(slotRepository.findAll())
			.containsExactly(
				new ParkingSlot("1", false),
				new ParkingSlot("2", true));
	}

	@Test
	public void testFindById() {
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
			.toList();
	}
	
	private void addTestSlotToDatabase(String id, boolean occupied) {
		slotCollection.insertOne(
			new Document()
				.append("id", id)
				.append("occupied", occupied));
	}

}