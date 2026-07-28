package com.example.parking.repository;

import java.util.List;

import com.example.parking.model.ParkingEvent;

public interface ParkingEventRepository {

	public List<ParkingEvent> findAll();

	public void save(ParkingEvent event);

}