package com.example.parking.model;

import java.util.Objects;

public class ParkingEvent {
	private String slotId;
	private boolean occupied;
	private String timestamp;

	public ParkingEvent() {
	}

	public ParkingEvent(String slotId, boolean occupied, String timestamp) {
		this.slotId = slotId;
		this.occupied = occupied;
		this.timestamp = timestamp;
	}

	public String getSlotId() {
		return slotId;
	}

	public void setSlotId(String slotId) {
		this.slotId = slotId;
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int hashCode() {
		return Objects.hash(occupied, slotId, timestamp);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParkingEvent other = (ParkingEvent) obj;
		return occupied == other.occupied && Objects.equals(slotId, other.slotId)
				&& Objects.equals(timestamp, other.timestamp);
	}

	@Override
	public String toString() {
		return "ParkingEvent [slotId=" + slotId + ", occupied=" + occupied + ", timestamp=" + timestamp + "]";
	}

}