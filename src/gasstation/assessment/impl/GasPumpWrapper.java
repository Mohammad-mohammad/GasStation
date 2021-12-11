package gasstation.assessment.impl;

import java.util.Objects;

import net.bigpoint.assessment.gasstation.*;

public class GasPumpWrapper {
	private GasPump gasPump;
	private GasPumpStatus gasPumpStaus;
	private int id;
	
	
	public void setGasPumpStaus(GasPumpStatus gasPumpStaus) {
		this.gasPumpStaus = gasPumpStaus;
	}

	

	public GasPumpWrapper(GasPump gasPump, int id) {
		super();
		this.gasPumpStaus = GasPumpStatus.FREE;
		this.gasPump = gasPump;
		this.id = id;
	}

	
	public GasPump getGasPump() {
		return gasPump;
	}


	public GasPumpStatus getGasPumpStaus() {
		return gasPumpStaus;
	}


	public int getId() {
		return id;
	}


	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GasPumpWrapper other = (GasPumpWrapper) obj;
		return id == other.id;
	}
	
		
	
}
