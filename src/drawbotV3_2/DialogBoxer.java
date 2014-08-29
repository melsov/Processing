package drawbotV3_2;

import javax.swing.JOptionPane;

public class DialogBoxer {

	
	
	public static int QuestionWithOptions(String question, String[] options) {
		return JOptionPane.showOptionDialog(null,
				question,
				"Please choose: ",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[1]);
	}
	
	public static boolean QuestionWithTwoOptions(String question, String falseOp, String trueOp) {
		Object[] options = {falseOp, trueOp};
		int n = JOptionPane.showOptionDialog(null,
				question,
				"I have a question",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[1]);
		return n == 1;
	}
	
	public static void Message(String message, String onlyOp) {
		Object[] options = {onlyOp};
		int n = JOptionPane.showOptionDialog(null,
				message,
				"I have a message",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]);
	}
}
