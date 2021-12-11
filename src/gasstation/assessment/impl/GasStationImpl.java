package gasstation.assessment.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

public class GasStationImpl implements GasStation{
	
	private double revenue=0.0;
	private int numberOfSales=0;
	private int numberOfCancellationsNoGas=0;
	private int numberOfCancellationsTooExpensive=0;
	private int gasPumbNumbers=0;
	
	private List<GasPumpWrapper> gasPumpWList= new ArrayList<>();
	private Map<GasType, Double> gasPriceTbl= new HashMap<>();
	

	@Override
	public void addGasPump(GasPump pump) {
		// check that id is not already exist 
		int id= gasPumbNumbers;
		gasPumpWList.add(new GasPumpWrapper(pump, id));
		gasPumbNumbers++;
		
	}

	@Override
	public Collection<GasPump> getGasPumps() {
		return gasPumpWList.stream().map(e-> e.getGasPump()).collect(Collectors.toList());
	}

	@Override
	public double buyGas(GasType type, double amountInLiters, double maxPricePerLiter) throws NotEnoughGasException, GasTooExpensiveException {
		
		double gasTypePricePerLiter = this.getPrice(type);
		
		if(maxPricePerLiter < gasTypePricePerLiter) {
			numberOfCancellationsTooExpensive++;
			throw new GasTooExpensiveException();		
		}
		
		double payment=0;
		
		Optional<GasPumpWrapper> selectedGasPumpOpt = gasPumpWList.stream()
							.filter(pumpW-> pumpW.getGasPump().getGasType()==type)
							.filter(pumpW-> pumpW.getGasPumpStaus()== GasPumpStatus.FREE)
							.filter(pumpW-> pumpW.getGasPump().getRemainingAmount()>= amountInLiters)
							.findAny();
		
		if(!selectedGasPumpOpt.isPresent()) {
			numberOfCancellationsNoGas++;
			throw new NotEnoughGasException();
		}else {
						
			GasPumpWrapper selectedGasPumpW = selectedGasPumpOpt.get();
			
			selectedGasPumpW.setGasPumpStaus(GasPumpStatus.BUZY);			
			synchronized (selectedGasPumpW) {
				Thread pumpingGasProcess = new Thread(){
				    public void run(){
				    	selectedGasPumpW.getGasPump().pumpGas(amountInLiters);
				    	selectedGasPumpW.setGasPumpStaus(GasPumpStatus.FREE);
				    }
				  };
				  pumpingGasProcess.start();				
			}				
			numberOfSales++;
			payment= amountInLiters*maxPricePerLiter;
			revenue=revenue+payment;			
		}		
		return payment;
	}

	@Override
	public double getRevenue() {
		return this.revenue;
	}

	@Override
	public int getNumberOfSales() {
		return this.numberOfSales;
	}

	@Override
	public int getNumberOfCancellationsNoGas() {
		return this.numberOfCancellationsNoGas;
	}

	@Override
	public int getNumberOfCancellationsTooExpensive() {
		return this.numberOfCancellationsTooExpensive;
	}

	@Override
	public double getPrice(GasType type) {
		return this.gasPriceTbl.get(type);
	}

	@Override
	public void setPrice(GasType type, double price) {
		this.gasPriceTbl.put(type, price);
	}
	
}
