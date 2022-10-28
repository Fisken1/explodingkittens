package messages;

import java.io.Serializable;

public class Choice implements Serializable {
    private String choice;
    private String choiceDescription;
    // Best practice to init serialVersionUID in Serializable classes also makes it
    // possible to test
    private static final long serialVersionUID = 1L;

    public Choice(String choice, String choiceDescription) {
        this.choice = choice;
        this.choiceDescription = choiceDescription;
    }

    /**
     * @return the choice as a string
     */
    public String getChoice() {
        return this.choice;
    }

    /**
     * @return the description of the choice
     */
    public String getChoiceDescription() {
        return this.choiceDescription;
    }

}
