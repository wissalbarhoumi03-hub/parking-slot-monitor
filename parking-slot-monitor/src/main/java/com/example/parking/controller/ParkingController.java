package com.example.parking.controller;

import com.example.parking.model.ParkingSlot;
import com.example.parking.repository.ParkingSlotRepository;
import com.example.parking.view.ParkingView;

public class ParkingController {

	private ParkingView parkingView;
	private ParkingSlotRepository slotRepository;

	public ParkingController(ParkingView parkingView, ParkingSlotRepository slotRepository) {
		this.parkingView = parkingView;
		this.slotRepository = slotRepository;
	}

	public void addSlot(ParkingSlot slot) {
		ParkingSlot existingSlot = slotRepository.findById(slot.getId());
		if (existingSlot != null) {
			parkingView.showError("Already existing slot with id " + slot.getId(), existingSlot);
			return;
		}
		slotRepository.save(slot);
		parkingView.slotAdded(slot);
	}

}
