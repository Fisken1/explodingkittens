package communication.messages;

import java.io.Serializable;

public class Choice implements Serializable {
    private String choice;
    private String choiceDescription;
    // Best practice to init serialVersionUID in Serializable classes
    private static final long serialVersionUID = 1L;

    public Choice(String choice, String choiceDescription) {
        this.choice = choice;
        this.choiceDescription = choiceDescription;
    }

    public String getChoice() {
        return this.choice;
    }

    public String getChoiceDescription() {
        return this.choiceDescription;
    }

}
