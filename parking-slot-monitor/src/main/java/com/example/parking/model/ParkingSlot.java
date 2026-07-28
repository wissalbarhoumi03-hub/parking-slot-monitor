package com.example.parking.model;

import java.util.Objects;

public class ParkingSlot {
	private String id;
	private boolean occupied;

	public ParkingSlot() {
	}

	public ParkingSlot(String id, boolean occupied) {
		this.id = id;
		this.occupied = occupied;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, occupied);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParkingSlot other = (ParkingSlot) obj;
		return Objects.equals(id, other.id) && occupied == other.occupied;
	}

	@Override
	public String toString() {
		return "ParkingSlot [id=" + id + ", occupied=" + occupied + "]";
	}

}