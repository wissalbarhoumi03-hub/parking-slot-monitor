package com.example.parking.controller;

import com.example.parking.model.ParkingEvent;
import com.example.parking.model.ParkingSlot;
import com.example.parking.repository.ParkingSlotRepository;
import com.example.parking.view.ParkingView;
import com.example.parking.repository.ParkingEventRepository;

public class ParkingController {

	private ParkingView parkingView;
	private ParkingSlotRepository slotRepository;
	private ParkingEventRepository eventRepository;

	public ParkingController(ParkingView parkingView, ParkingSlotRepository slotRepository,
			ParkingEventRepository eventRepository) {
		this.parkingView = parkingView;
		this.slotRepository = slotRepository;
		this.eventRepository = eventRepository;
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

	public void markOccupied(String id, String timestamp) {
		if (slotRepository.findById(id) == null) {
			parkingView.showError("No existing slot with id " + id, null);
			return;
		}
		ParkingSlot occupiedSlot = new ParkingSlot(id, true);
		slotRepository.delete(id);
		slotRepository.save(occupiedSlot);
		eventRepository.save(new ParkingEvent(id, true, timestamp));
		parkingView.slotUpdated(occupiedSlot);
	}

	public void markFree(String id, String timestamp) {
		if (slotRepository.findById(id) == null) {
			parkingView.showError("No existing slot with id " + id, null);
			return;
		}
		ParkingSlot freeSlot = new ParkingSlot(id, false);
		slotRepository.delete(id);
		slotRepository.save(freeSlot);
		eventRepository.save(new ParkingEvent(id, false, timestamp));
		parkingView.slotUpdated(freeSlot);
	}

	public void allSlots() {
		parkingView.showAllSlots(slotRepository.findAll());
		
	}

	public void showHistory() {
		parkingView.showHistory(eventRepository.findAll());
		
	}

	public void countSlots() {
		int occupied = 0;
		int free = 0;
		for (ParkingSlot slot : slotRepository.findAll()) {
			if (slot.isOccupied()) {
				occupied++;
			} else {
				free++;
			}
		}
		parkingView.showCounts(free, occupied);
	}
}
