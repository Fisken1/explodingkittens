package messages;

import java.io.Serializable;
import java.util.List;

public interface Message extends Serializable {

    /**
     * @return the content of the messgae as a string
     */
    String getContents();

    /**
     * @return a list of choices in the message
     */
    List<Choice> getChoices();

    /**
     * @return all the choices of the message as a string
     */
    String getChoicesAsString();

    /**
     * @param answer the asnwer we want to see if the choices contain
     * @return true if the choices contain the answer else false
     */
    boolean choicesContainedAnswer(String answer);

}
