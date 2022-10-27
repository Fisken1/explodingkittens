package communication.messages;

import java.util.List;

public class Information implements Message {
    private String message;
    private static final long serialVersionUID = 1L;

    public Information(String message) {
        this.message = message;
    }


    public String getMessage() {
        return message;
    }


    public List<Choice> getChoices() {
        return null;
    }

    


    public String getChoicesAsString() {
        return "";
    }


    public boolean choicesContainedAnswer(String answer) {
        return false;
    }


    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        return result;
    }


    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Information other = (Information) obj;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        return true;
    }

}
