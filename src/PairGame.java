import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * PairGame is a game that consists of two clients(ServerHandle) objects
 * This is sort of the game controller that keeps score and other information
 *
 */
public class PairGame {
	
	/**
	 * User one in the current game
	 */
	private ServerHandle userOne;
	
	/**
	 * user two in the current game
	 */
	private ServerHandle userTwo;
	
	/**
	 * The score of user one
	 */
	private int userOnePoint = 0;
	
	/**
	 * Score of user two
	 */
	private int userTwoPoint = 0;
	
	/**
	 * The correct answer to the current question
	 */
	private String answer;
	
	/**
	 * The answer received from user one
	 */
	private String userOneAnswer;
	
	/**
	 * The answer received from user two
	 */
	private String userTwoAnswer;
	
	/**
	 * Number of answers received for a question if = 2 then all clients have answered
	 */
	private int numberOfAnswers = 0;
	
	/**
	 * Number of questions that has been played
	 */
	private int numberOfQuestion = 0;

	/**
	 * HashMap containing all the questions and answers
	 */
	private static HashMap<String,String> questions = new HashMap<String, String>();

	/**
	 * Constructor takes two ServerHandle objects (clients) to create a pair for
	 * the game. Creats a Hashmap of all the questions and answers from the file
	 * "src/question.txt"
	 * 
	 * @param userOne ServerHandle object for user one
	 * @param userTwo ServerHandle object for user two
	 */
	public PairGame(ServerHandle userOne, ServerHandle userTwo) {
		this.userOne = userOne;
		this.userTwo = userTwo;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("src/question.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		String inputkey;
		String inputValue;
		try {
			while ((inputkey = br.readLine()) != null) {
				inputValue = br.readLine();
				questions.put(inputkey, inputValue);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Gets a String containing a random question from the HashMap of the questions.
	 * Stores the answer (toLowerCase) in the answer field
	 * @return
	 */
	public String getQuestion(){
		String question = null;
		int randomNum =  (int) ((Math.random())* questions.size());
		int counter = 0;
		for(String key : questions.keySet()){
			if(counter == randomNum){
				question = key;
				answer = questions.get(key).toLowerCase();
				break;
			}
			counter++;
		}
		// Remove the question from the map so it doesn't reappear
		questions.remove(question);
		System.out.println("'PairGame' Correct Answer is: " + answer );
		return question;
	}
	
	/**
	 * Get a question and send it to both clients
	 */
	public void sendQuestionToClient(){
		numberOfQuestion++;
		String question = getQuestion();
		userOne.sendQuestion(question + " " + getUserOnePoint());
		userTwo.sendQuestion(question + " " + getUserTwoPoint());
	}
	
	/**
	 * Increase the points of player one
	 */
	public void increaseUserOnePoint(){
		userOnePoint++;
	}
	
	/**
	 * Get the current score of user one
	 * @return the score of user one stored in userOnePoint field
	 */
	public int getUserOnePoint(){
		return userOnePoint;
	}
	
	/**
	 * Increase the score of user two
	 */
	public void increaseUserTwoPoint(){
		userTwoPoint++;
	}
	/**
	 * Get the current score of user two
	 * @return the score of user one stored in userTwoPoint field
	 */
	public int getUserTwoPoint(){
		return userTwoPoint;
	}
	
	/**
	 * Check the received answers and if a user has correct answer. increase
	 * that users score. Reset the number of answers and if still not played all
	 * questions send a new question to the clients.
	 * If the game is over check the results
	 */
	public void checkAnswer(){
		if(getUserOneAnswer().toLowerCase().equals(answer)){
			increaseUserOnePoint();
		}
		if(getUserTwoAnswer().toLowerCase().equals(answer)){
			increaseUserTwoPoint();
		}
		setNumberOfAnswerToZero();
		
		if(numberOfQuestion <= 2){
			sendQuestionToClient();
		}else{
			result();
		}
		
	}
	
	/**
	 * Check which user is the winner and send the users the result by calling
	 * the methods corresponding.
	 */
	public void result() {
		if (getUserOnePoint() > getUserTwoPoint()) {
			userOne.isWinner("You have " + getUserOnePoint() + " "
					+ "score but " + userTwo.getUserName() + " " + "has "
					+ getUserTwoPoint() + " " + "score!");
			userTwo.isLoser("You have " + getUserTwoPoint() + " "
					+ "score but " + userOne.getUserName() + " " + "has "
					+ getUserOnePoint() + " " + "score!");
		} else if (getUserOnePoint() < getUserTwoPoint()) {
			userTwo.isWinner("You have " + getUserTwoPoint() + " "
					+ "score but " + userOne.getUserName() + " " + "has "
					+ getUserOnePoint() + " " + "score!");
			userOne.isLoser("You have " + getUserOnePoint() + " "
					+ "score but " + userTwo.getUserName() + " " + "has "
					+ getUserTwoPoint() + " " + "score!");
		} else {
			userTwo.samePoint("You have " + getUserTwoPoint() + " "
					+ "score and " + userOne.getUserName() + " " + "has "
					+ getUserOnePoint() + " " + "score!");
			userOne.samePoint("You have " + getUserOnePoint() + " "
					+ "score and " + userTwo.getUserName() + " " + "has "
					+ getUserTwoPoint() + " " + "score!");

		}
	}
	
	/**
	 * Setter for the user one answer field
	 * @param answer the answer received from user one
	 */
	public void setUserOneAnswer(String answer){
		userOneAnswer = answer.toLowerCase();
		increaseNumberOfAnswers();
	}

	/**
	 * Setter for the user two answer field
	 * @param answer the answer received from user one
	 */
	public void setUserTwoAnswer(String answer){
		userTwoAnswer = answer.toLowerCase();
		increaseNumberOfAnswers();
	}
	
	/**
	 * Getter for the user One answer field
	 * @return the value of the userOneAnswer
	 */
	public String getUserOneAnswer(){
		return userOneAnswer;
	}	
	
	/**
	 * Getter for the user two answer field
	 * 
	 * @return the value of the userTwoAnswer
	 */
	public String getUserTwoAnswer(){
		return userTwoAnswer;
	}
	
	/**
	 * Increases the number of answers received
	 */
	public void increaseNumberOfAnswers(){
		numberOfAnswers++;
	}
	
	/**
	 * Get the number of answers received
	 * 
	 * @return the number of answers currently in the field numberOfAnswers
	 */
	public int getNumberOfAnswer() {
		return numberOfAnswers;
	}

	/**
	 * Reset the number of answers to zero. Used when a new question is
	 * generated
	 */
	public void setNumberOfAnswerToZero() {
		numberOfAnswers = 0;
	}
	
	/**
	 * Boolean that returns true if numberOfAnswers ==2
	 * @return true or false
	 */
	public boolean ifAllUserHasAnswered(){
		if(getNumberOfAnswer() == 2){
			return true;
		}		
		return false;
	}
	
	/**
	 * When a user leaves the game this method is called to inform the other user in the game of this
	 * @param userName the userName of the leaving client
	 */
	public void leavesGame(String userName){
		System.out.println("'PairGame' UserName leaves Game " + userName);
		if(userOne.getUserName().equals(userName)){
			System.out.println("'PairGame' if userTwo " + userTwo.getUserName());
			userOne.leavesGame(userTwo.getUserName().toString());
		}else{
			System.out.println("'PairGame' if userOne " + userOne.getUserName());
			userTwo.leavesGame(userOne.getUserName().toString());
		}
	}
	

}
