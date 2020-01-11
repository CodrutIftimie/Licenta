package ga.servicereq;

import android.util.Log;

public class Post {
    private String posterId;
    private String firstName;
    private String lastName;
    private String postDate;
    private String description;
    private String descriptionImageURL;
    private String profileImageURL;

    public Post(String[] args) {
        try {
            posterId = args[0];
            firstName = args[1];
            lastName = args[2];
            postDate = args[3];
            description = args[4];
            descriptionImageURL = args[5];
            profileImageURL = args[6];
        }
        catch (NullPointerException e) {
            Log.e("Post Constructor","Insufficient arguments in array");
        }
    }

    public Post(String posterId, String firstName, String lastName, String postDate, String description, String descriptionImageURL, String profileImageURL) {
        this.posterId = posterId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.postDate = postDate;
        this.description = description;
        this.descriptionImageURL = descriptionImageURL;
        this.profileImageURL = profileImageURL;
    }

    public Post(String posterId, String firstName, String lastName, String description) {
        this.posterId = posterId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
    }

    public Post(String posterId, String firstName, String lastName, String postDate, String description) {
        this.posterId = posterId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.postDate = postDate;
        this.description = description;
    }

    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    public String getFirstName() {
        if(firstName == null)
            return "";
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        if(lastName == null)
            return "";
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPostDate() {
        if(postDate == null)
            return "";
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getDescription() {
        if(description == null)
            return "";
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionImageURL() {
        if(descriptionImageURL == null)
            return "";
        return descriptionImageURL;
    }

    public void setDescriptionImageURL(String descriptionImageURL) {
        this.descriptionImageURL = descriptionImageURL;
    }

    public String getProfileImageURL() {
        if(profileImageURL == null)
            return "";
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }
}
