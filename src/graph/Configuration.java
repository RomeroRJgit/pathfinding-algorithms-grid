package graph;

public class Configuration {

	static boolean canShowNumbers = false;
	static boolean canShowAnimation = true;

	static int simulationRate = 25;
	
	static public boolean getCanShowNumbers() {
		return canShowNumbers;
	}
	
	public static void setCanShowNumbers(boolean showNumbers) {
		Configuration.canShowNumbers = showNumbers;
	}
	
	static public boolean getCanShowAnimation() {
		return canShowAnimation;
	}
	
	public static void setCanShowAnimation(boolean canShowAnimation) {
		Configuration.canShowAnimation = canShowAnimation;
	}
	
	public static int getSimulationRate() {
		return simulationRate;
	}
	
	public static void setSimulationRate(int simulationRate) {
		Configuration.simulationRate = simulationRate;
	}
	
}
