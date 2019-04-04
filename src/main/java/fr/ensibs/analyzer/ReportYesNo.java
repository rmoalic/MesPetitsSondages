package fr.ensibs.analyzer;

/**
 * Represents a report for a yes/no question
 * 
 * @author Maxime
 *
 */
public class ReportYesNo extends Report {

	/**
	 * The number of positive answers
	 */
	private int nbYes = 0;
	
	/**
	 * The number of negative answers
	 */
	private int nbNo = 0;
	
	/**
	 * Increments by 1 the number of positive answers
	 */
	public void incrementNbYes() {
		nbYes++;
	}
	
	/**
	 * Increments by 1 the number of negative answers
	 */
	public void incrementNbNo() {
		nbNo++;
	}
	
	/**
	 * Gets the number of positive answers
	 * 
	 * @return
	 */
	public int getYesAnswers() {
		return this.nbYes;
	}
	
	/**
	 * Gets the number of negative answers
	 * 
	 * @return
	 */
	public int getNoAnswers() {
		return this.nbNo;
	}

}
