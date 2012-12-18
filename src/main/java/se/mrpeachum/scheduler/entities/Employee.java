/**
 * 
 */
package se.mrpeachum.scheduler.entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import se.mrpeachum.scheduler.service.Normalizer;

/**
 * @author mrpeachum
 * 
 */
@Entity
public class Employee extends BaseEntity implements Comparable<Employee> {

	@Id
	@GeneratedValue
	private Long id;

	private String firstName;

	private String lastName;

	@OneToMany(cascade = { CascadeType.REMOVE }, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<Shift> shifts;

	@Column(name = "sortOrder")
	private Integer order;

	@ManyToOne
	@JoinColumn(name = "userId")
	private User user;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the shifts
	 */
	public Set<Shift> getShifts() {
		return shifts;
	}

	/**
	 * @param shifts
	 *            the shifts to set
	 */
	public void setShifts(Set<Shift> shifts) {
		this.shifts = shifts;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Shift> getShiftsForDay(String millisString) {
		Long millis = Long.parseLong(millisString);
		return getShiftsForDayMillis(millis);
	}

	public List<Shift> getShiftsForDayMillis(Long millis) {
		Date day = Normalizer.dateNormalizer(new Date(millis));
		List<Shift> shifts = new ArrayList<>();
		Calendar shiftCal = Calendar.getInstance(Locale.ENGLISH);
		Calendar testCal = Calendar.getInstance(Locale.ENGLISH);
		
		for (Shift shift : this.shifts) {
			shiftCal.setTime(shift.getDay());
			testCal.setTime(day);
			if (shiftCal.get(Calendar.YEAR) == testCal.get(Calendar.YEAR) 
					&& shiftCal.get(Calendar.MONTH) == testCal.get(Calendar.MONTH)
					&& shiftCal.get(Calendar.DATE) == testCal.get(Calendar.DATE)) {
				shifts.add(shift);
			}
		}
		return shifts;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		return true;
	}

	@Override
	public int compareTo(Employee other) {
		return this.getOrder().compareTo(other.getOrder());
	}

}
