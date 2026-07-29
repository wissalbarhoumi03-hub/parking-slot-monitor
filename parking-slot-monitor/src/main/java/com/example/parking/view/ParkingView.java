package com.example.parking.view;

import com.example.parking.model.ParkingSlot;

public interface ParkingView {

	void slotAdded(ParkingSlot slot);
	
	void slotUpdated(ParkingSlot slot);

	void showError(String message, ParkingSlot slot);

}