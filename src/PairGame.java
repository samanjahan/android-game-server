import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


public class PairGame {
	private ServerHandle userOne;
	private ServerHandle userTow;
	private int userOnePoint = 0;
	private int userTwoPoint = 0;
	private String answer;
	private String userOneAnswer;
	private String userTowAnswer;
	private int numberOfAnswed = 0;
	private int numberOfQuestion = 0;

	private static HashMap<String,String> question = new HashMap<String, String>();

	public PairGame(ServerHandle userOne, ServerHandle userTow){
		// TODO Auto-generated constructor stub
		this.userOne = userOne;
		this.userTow = userTow;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("/home/syst3m/SchoolStuff/workspace/ID1222Pro/src/question.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String inputkey;
		String inputValue;
		try {
			while((inputkey = br.readLine()) != null){
				inputValue = br.readLine();	
				question.put(inputkey, inputValue);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public String getQuestion(){
		String quest = null;
		int randomNum =  (int) ((Math.random())* question.size());
		int counter = 0;
		for(String key : question.keySet()){
			if(counter == randomNum){
				quest = key;
				answer = question.get(key).toLowerCase();
				return quest;
			}
			counter++;
		}
		return quest;
	}
	
	public void sendQuestionToClient(){
		numberOfQuestion++;
		String question = getQuestion();
		userOne.sendQuestion(question + " " + getUserOnePointt());
		userTow.sendQuestion(question + " " + getUserTowPointt());
	}
	
	public void increaseUserOnePoint(){
		userOnePoint++;
	}
	
	public int getUserOnePointt(){
		return userOnePoint;
	}
	public void increaseUserTowPoint(){
		userTwoPoint++;
	}
	
	public int getUserTowPointt(){
		return userTwoPoint;
	}
	public void chechAnsewr(){
		if(getUserOneAnswer().equals(answer)){
			increaseUserOnePoint();
		}
		if(getUserTowAnswer().equals(answer)){
			increaseUserTowPoint();
		}
		setNumberOfAnswerToZero();
		
		if(numberOfQuestion <= 2){
			sendQuestionToClient();
		}else{
			result();
		}
		
	}
	
	public void result(){
		 if (getUserOnePointt() > getUserTowPointt()){
				userOne.isWinner("You have " + getUserOnePointt() + " " + "score but " + userTow.getUserName() + " " + "has " + getUserTowPointt() + " " + "score!");
				userTow.isLoser("You have " + getUserTowPointt() + " " + "score but " + userOne.getUserName() + " " + "has " + getUserOnePointt() + " " + "score!");
		 }else if(getUserOnePointt() < getUserTowPointt()){
				userTow.isWinner("You have " + getUserTowPointt() + " " + "score but " + userOne.getUserName() +" " + "has " + getUserOnePointt() + " " + "score!");
				userOne.isLoser("You have " + getUserOnePointt() + " " + "score but " + userTow.getUserName() + " " + "has " + getUserTowPointt() + " " + "score!");
		 }else{
			 userTow.samePoint("You have " + getUserTowPointt() + " " + "score and " + userOne.getUserName() + " " + "has "  + getUserOnePointt() + " " + "score!" );
			 userOne.samePoint("You have " +getUserOnePointt() +" " + "score and " + userTow.getUserName() + " " +"has "  + getUserTowPointt()+ " " + "score!");

		 }
	}
	
	public void setUserOneAnswer(String answer){
		userOneAnswer = answer.toLowerCase();
		increaseNumberOfAnswer();
	}
	
	public void setUserTowAnswer(String answer){
		userTowAnswer = answer.toLowerCase();
		increaseNumberOfAnswer();
	}
	public String getUserOneAnswer(){
		return userOneAnswer;
	}
	public String getUserTowAnswer(){
		return userTowAnswer;
	}
	
	public void increaseNumberOfAnswer(){
		numberOfAnswed++;
	}
	
	public int getNumberOfAnser(){
		return numberOfAnswed;
	}
	
	public void setNumberOfAnswerToZero(){
		numberOfAnswed = 0;
	}
	
	public boolean ifAllUserHasAnswered(){
		if(getNumberOfAnser() == 2){
			return true;
		}		
		return false;
	}
	
	public void leavesGame(String userName){
		System.out.println("UserName Gammeeeee " + userName);
		if(userOne.getUserName().equals(userName)){
			System.out.println("IFFFFFFFF userTow " + userTow.getUserName());
			userOne.leavesGame(userTow.getUserName().toString());
		}else{
			System.out.println("IFFFFFFFF userOne " + userOne.getUserName());
			userTow.leavesGame(userOne.getUserName().toString());
		}
	}
	

}
