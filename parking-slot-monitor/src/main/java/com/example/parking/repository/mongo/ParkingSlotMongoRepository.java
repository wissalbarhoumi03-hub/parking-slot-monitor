package com.example.parking.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.example.parking.model.ParkingSlot;
import com.example.parking.repository.ParkingSlotRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class ParkingSlotMongoRepository implements ParkingSlotRepository {

	public static final String PARKING_DB_NAME = "parking";
	public static final String SLOT_COLLECTION_NAME = "slot";

	private MongoCollection<Document> slotCollection;

	public ParkingSlotMongoRepository(MongoClient client) {
		slotCollection = client
				.getDatabase(PARKING_DB_NAME)
				.getCollection(SLOT_COLLECTION_NAME);
	}

	
	@Override
	public List<ParkingSlot> findAll() {
		return StreamSupport
				.stream(slotCollection.find().spliterator(), false)
				.map(this::fromDocumentToSlot)
				.collect(Collectors.toList());
	}

	private ParkingSlot fromDocumentToSlot(Document d) {
		return new ParkingSlot("" + d.get("id"), (boolean) d.get("occupied"));
	}

	@Override
	public ParkingSlot findById(String id) {
		Document d = slotCollection.find(Filters.eq("id", id)).first();
		if (d != null)
			return fromDocumentToSlot(d);
		return null;
	}

	@Override
	public void save(ParkingSlot slot) {
		slotCollection.insertOne(
			new Document()
				.append("id", slot.getId())
				.append("occupied", slot.isOccupied()));
	}

	@Override
	public void delete(String id) {
		slotCollection.deleteOne(Filters.eq("id", id));
	}
}