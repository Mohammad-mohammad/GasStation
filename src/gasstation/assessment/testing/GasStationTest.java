package gasstation.assessment.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import gasstation.assessment.impl.GasStationImpl;
import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

class GasStationTest {

	private static GasStationImpl gasStation;
	private static double DIESEL_PRICE= 10;
	private static double REGULAR_PRICE = 15;
	private static double SUPER_Price= 20;
	
	private static double DIESEL_AMOUNT1= 10000;
	private static double DIESEL_AMOUNT2= 20000;
	private static double DIESEL_AMOUNT3= 30000;
	private static double SUPER_AMOUNT1= 15000;
	private static double SUPER_AMOUNT2= 20000;
	private static double REGULAR_AMOUNT1= 50000;
	
	private static double expectedRevenue=0;
	private static int numberOfPumps ;

	@BeforeAll
	@Test
	static void init() throws Exception {
		gasStation = new GasStationImpl();
		
		numberOfPumps = add_GasPump();
		
		gasStation.setPrice(GasType.DIESEL, DIESEL_PRICE);
		gasStation.setPrice(GasType.REGULAR, REGULAR_PRICE);
		gasStation.setPrice(GasType.SUPER, SUPER_Price);
	}

	static int add_GasPump() {
		List<GasPump> gasPumps = List.of(
			new GasPump(GasType.DIESEL, DIESEL_AMOUNT1),
			new GasPump(GasType.DIESEL, DIESEL_AMOUNT2),
			new GasPump(GasType.SUPER, SUPER_AMOUNT1),
			new GasPump(GasType.DIESEL, DIESEL_AMOUNT3),
			new GasPump(GasType.REGULAR, REGULAR_AMOUNT1),
			new GasPump(GasType.SUPER, SUPER_AMOUNT2)			
		);
		
		gasPumps.stream().forEach(gasPumpItem-> gasStation.addGasPump(gasPumpItem));				
		return gasPumps.size();
	}
	
	@Test
	void test_adding_gaspump_method() {
		Collection<GasPump> expectedGasPumps= gasStation.getGasPumps();	
		assertEquals(expectedGasPumps.size(), numberOfPumps);
	}
	
	@Test
	void price_test() {		
		double expectedDieselPrice = gasStation.getPrice(GasType.DIESEL);
		double expectedRegularPrice= gasStation.getPrice(GasType.REGULAR);
		double expectedSuperPrice  = gasStation.getPrice(GasType.SUPER);
	
		assertEquals(expectedDieselPrice, DIESEL_PRICE);
		assertEquals(expectedRegularPrice, REGULAR_PRICE);
		assertEquals(expectedSuperPrice, SUPER_Price);
	}


	@DisplayName("Successfull customer request for gas:")
	@Test
	void buyGaswithSuccess() throws NotEnoughGasException, GasTooExpensiveException {		
		List<String> data= List.of( 
					"DIESEL:5:10", 
					"SUPER:10:22", 
					"REGULAR:5:17",
					"DIESEL:8:10", 
					"SUPER:10:23", 
					"DIESEL:6:12"
		);
		for (String row : data) {
			String[] gasPumpParms = row.split(":");
			double amount = Double.parseDouble(gasPumpParms[1]);
			double pricePerL = Double.parseDouble(gasPumpParms[2]);
			double payment = gasStation.buyGas(GasType.valueOf(gasPumpParms[0]),amount, pricePerL);
			double expectedPayment=amount*pricePerL;
			expectedRevenue+=expectedPayment;
			assertEquals(expectedPayment, payment);	
		}	
		
	}
	
	@DisplayName("Failed with NotEnoughGasException:")
	@Test
	void buyGasWithNotEnoughGasException() throws NotEnoughGasException, GasTooExpensiveException {		

		double bigAmount = 25000;
		double goodPrice = SUPER_Price+5;
		
		assertThrows(NotEnoughGasException.class, () -> {
			gasStation.buyGas(GasType.SUPER, bigAmount, goodPrice);	
	    });
				
	}
	
	@DisplayName("Failed with GasTooExpensiveException:")
	@Test
	void buyGasWithGasTooExpensiveException() throws NotEnoughGasException, GasTooExpensiveException {		

		double normalAmount = 10;
		double badPrice = REGULAR_PRICE-5;
		
		assertThrows(GasTooExpensiveException.class, () -> {
			gasStation.buyGas(GasType.REGULAR, normalAmount, badPrice);	
	    });
		
		
	}
	
	
//	@Test
//	@AfterAll
//	static void revenueTest() {
//		assertEquals(expectedRevenue, gasStation.getRevenue());
//	}
//	
//	@Test
//	@AfterAll
//	static void NumberOfSalesTest() {
//		assertEquals(6, gasStation.getNumberOfSales());
//	}
//	
//	@Test
//	@AfterAll
//	static void NumberOfCancellationsNoGasTest() {
//		assertEquals(1, gasStation.getNumberOfCancellationsNoGas());
//	}
//	
//	@Test
//	@AfterAll
//	static void NumberOfCancellationsTooExpensiveTest() {
//		assertEquals(1, gasStation.getNumberOfCancellationsTooExpensive());	
//	}
//	
	 @AfterAll
	 @DisplayName("Statistics: ")
	 public static void statistics(){
		assertEquals(expectedRevenue, gasStation.getRevenue());
		assertEquals(6, gasStation.getNumberOfSales());
		assertEquals(1, gasStation.getNumberOfCancellationsNoGas());
		assertEquals(1, gasStation.getNumberOfCancellationsTooExpensive());	
	 }
}
