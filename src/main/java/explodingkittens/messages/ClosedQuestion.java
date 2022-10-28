package messages;

import java.util.List;

public class ClosedQuestion implements Message {
    private String message;
    private List<Choice> choices;
    private static final long serialVersionUID = 1L;

    public ClosedQuestion(String message, List<Choice> arrayList) {
        this.message = message;
        this.choices = arrayList;
    }

    @Override
    public String getChoicesAsString() {
        String choices = "";
        for (Choice c : this.choices) {
            choices = choices + (c.getChoice() + " " + c.getChoiceDescription() + "\n");
        }
        return choices;
    }

    @Override
    public String getContents() {
        return message;
    }

    @Override
    public List<Choice> getChoices() {

        return choices;
    }

    @Override
    public boolean choicesContainedAnswer(String answer) {
        for (Choice c : this.choices) {
            if (c.getChoice().equals(answer)) {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     * 
     * Auto generated hashcode function used in testing
     */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((choices == null) ? 0 : choices.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     * 
     * Auto generated equals function used in testing
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClosedQuestion other = (ClosedQuestion) obj;
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
