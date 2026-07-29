package com.example.parking.view;

import java.util.List;

import com.example.parking.model.ParkingEvent;
import com.example.parking.model.ParkingSlot;

public interface ParkingView {
	
	void showAllSlots(List<ParkingSlot> slots);

	void showHistory(List<ParkingEvent> events);
	
	void showCounts(int free, int occupied);
	
	void slotAdded(ParkingSlot slot);
	
	void slotUpdated(ParkingSlot slot);

	void showError(String message, ParkingSlot slot);

}