package com.example.parking.repository.mongo;

import java.util.List;

import com.example.parking.model.ParkingEvent;
import com.example.parking.repository.ParkingEventRepository;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
public class ParkingEventMongoRepository implements ParkingEventRepository {

	public static final String PARKING_DB_NAME = "parking";
	public static final String EVENT_COLLECTION_NAME = "event";

	private MongoCollection<Document> eventCollection;

	public ParkingEventMongoRepository(MongoClient client) {
		eventCollection = client
				.getDatabase(PARKING_DB_NAME)
				.getCollection(EVENT_COLLECTION_NAME);
	}
	@Override
	public List<ParkingEvent> findAll() {
		return StreamSupport
				.stream(eventCollection.find().spliterator(), false)
				.map(this::fromDocumentToEvent)
				.collect(Collectors.toList());
	}

	private ParkingEvent fromDocumentToEvent(Document d) {
		return new ParkingEvent(
				"" + d.get("slotId"),
				(boolean) d.get("occupied"),
				"" + d.get("timestamp"));
	}

	@Override
	public void save(ParkingEvent event) {
		eventCollection.insertOne(
			new Document()
				.append("slotId", event.getSlotId())
				.append("occupied", event.isOccupied())
				.append("timestamp", event.getTimestamp()));
	}

}