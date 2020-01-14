package smartspace.dao.rdb;

public class GeneratedId {
	private static long numOfElements = 0;
	private static long numOfActions = 0;
	
	public GeneratedId() {
	}
	
	public static long getNumOfElements() {
		return numOfElements;
	}

	public static void setNumOfElements(long numOfElements) {
		GeneratedId.numOfElements = numOfElements;
	}

	public static long getNumOfActions() {
		return numOfActions;
	}

	public static void setNumOfActions(long numOfActions) {
		GeneratedId.numOfActions = numOfActions;
	}
	

	public static long getNextElementValue() {
		numOfElements++;
		return numOfElements;
	}
	
	public static long getNextActionValue() {
		numOfActions++;
		return numOfActions;
	}
	
	public static void resetAll() {
		numOfElements = 0;
		numOfActions = 0;
	}

}
