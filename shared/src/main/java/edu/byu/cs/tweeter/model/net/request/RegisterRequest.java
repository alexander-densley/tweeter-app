package edu.byu.cs.tweeter.model.net.request;

public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String alias;
    private String password;
    private String imageUrl;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private RegisterRequest() {}

    public RegisterRequest(String firstName, String lastName, String alias, String password, String imageUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.alias = alias;
        this.password = password;
        this.imageUrl = imageUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
