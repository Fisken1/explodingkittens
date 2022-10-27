package communication.messages;

import java.util.List;

public class RhetoricalQuestion implements Message {
    private String message;
    private List<Choice> choices;
    private static final long serialVersionUID = 1L;

    public RhetoricalQuestion(String message, List<Choice> choices) {
        this.message = message;
        this.choices = choices;
    }


    public String getChoicesAsString() {
        String choices = "";
        for (Choice c : this.choices) {
            choices = choices + (c.getChoice() + " " + c.getChoiceDescription());
        }
        return choices;
    }


    public String getMessage() {
        return message;
    }

    public List<Choice> getChoices() {
        return choices;
    }


    public boolean choicesContainedAnswer(String answer) {
        for (Choice c : this.choices) {
            if (c.getChoice().contains(answer)) {
                return true;
            }
        }
        return false;
    }
    

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((choices == null) ? 0 : choices.hashCode());
        return result;
    }


    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RhetoricalQuestion other = (RhetoricalQuestion) obj;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (choices == null) {
            if (other.choices != null)
                return false;
        } else if (!choices.equals(other.choices))
            return false;
        return true;
    }
}
