package com.example.parking.repository;

import java.util.List;

import com.example.parking.model.ParkingSlot;

public interface ParkingSlotRepository {

	public List<ParkingSlot> findAll();

	public ParkingSlot findById(String id);

	public void save(ParkingSlot slot);

	public void delete(String id);

}