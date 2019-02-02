package net.sf.jetro.object.deserializer.beans;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

public class DateBean {
	private LocalDateTime dateTime;
	private Date date;
	
	public LocalDateTime getDateTime() {
		return dateTime;
	}
	
	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, dateTime);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DateBean other = (DateBean) obj;
		return Objects.equals(date, other.date) && Objects.equals(dateTime, other.dateTime);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DateBean [dateTime=").append(dateTime)
			.append(", date=").append(date).append("]");
		return builder.toString();
	}
}
