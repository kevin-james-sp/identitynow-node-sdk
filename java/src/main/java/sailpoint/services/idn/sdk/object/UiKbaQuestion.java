package sailpoint.services.idn.sdk.object;

import com.google.gson.annotations.SerializedName;

/**
 * This class of data is returned from calls into api/challenge/list
 * 
 * This returns a payload that models what Knowledge Based Authentication
 * questions are available to the end user.  
 * This call returns a List<> of these objects.
 *
 */
public class UiKbaQuestion {
	
	// Example JSON Array of a list of these objects:
	/*
	[
	 {
	  "id":"2978",
	  "text":"What city were you born in?",
	  "hasAnswer":false,
	  "numAnswers":205171
	 },{
	  "id":"2972",
	  "text":"What is the name of the first street you lived on?",
	  "hasAnswer":false,
	  "numAnswers":205171
	 },{
	  "id":"2974",
	  "text":"What is the name of your childhood best friend?",
	  "hasAnswer":false,
	  "numAnswers":205171
	  } ...
	 ]

	 */	
	@SerializedName("id")
	public String id;
	
	@SerializedName("text")
	public String text;
	
	@SerializedName("hasAnswer")
	public boolean hasAnswer;
	
	@SerializedName("numAnswers")
	public int numAnswers;
	
	// Note: Used when POSTing an answer up for Strong Auth.
	@SerializedName("answer")
	public String answer;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isHasAnswer() {
		return hasAnswer;
	}

	public void setHasAnswer(boolean hasAnswer) {
		this.hasAnswer = hasAnswer;
	}

	public int getNumAnswers() {
		return numAnswers;
	}

	public void setNumAnswers(int numAnswers) {
		this.numAnswers = numAnswers;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String userAnswer) {
		this.answer = userAnswer;
	}
	
}
