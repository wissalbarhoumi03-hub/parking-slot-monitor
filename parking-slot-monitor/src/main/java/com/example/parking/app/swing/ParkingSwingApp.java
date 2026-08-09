package com.example.parking.app.swing;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import com.example.parking.controller.ParkingController;
import com.example.parking.repository.ParkingEventRepository;
import com.example.parking.repository.ParkingSlotRepository;
import com.example.parking.repository.mongo.ParkingEventMongoRepository;
import com.example.parking.repository.mongo.ParkingSlotMongoRepository;
import com.example.parking.view.swing.ParkingSwingView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class ParkingSwingApp implements Callable<Void> {

	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";

	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;

	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "parking";

	@Option(names = { "--slot-collection" }, description = "Slot collection name")
	private String slotCollectionName = "slot";

	@Option(names = { "--event-collection" }, description = "Event collection name")
	private String eventCollectionName = "event";

	public static void main(String[] args) {
		new CommandLine(new ParkingSwingApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				MongoClient client =
					new MongoClient(new ServerAddress(mongoHost, mongoPort));
				ParkingSlotRepository slotRepository =
					new ParkingSlotMongoRepository(
						client, databaseName, slotCollectionName);
				ParkingEventRepository eventRepository =
					new ParkingEventMongoRepository(
						client, databaseName, eventCollectionName);
				ParkingSwingView parkingView = new ParkingSwingView();
				ParkingController parkingController = new ParkingController(
					parkingView, slotRepository, eventRepository);
				parkingView.setParkingController(parkingController);
				parkingView.setVisible(true);
				parkingController.allSlots();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return null;
	}
}