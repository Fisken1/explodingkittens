package communication.messages;

import java.io.Serializable;
import java.util.List;

public interface Message extends Serializable {

    String getMessage();

    List<Choice> getChoices();

    String getChoicesAsString();

    boolean choicesContainedAnswer(String answer);

}
