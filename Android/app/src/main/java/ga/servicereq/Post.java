package ga.servicereq;

public class Post {
    private String posterId;
    private String firstName;
    private String lastName;
    private String postDate;
    private String description;
    private String descriptionImageURL;
    private String profileImageURL;
    private String category;
    private String location;
    private boolean isSolved;
    private boolean isHelper;

    public Post(String posterId, String firstName, String lastName, String postDate, String description, String location, String descriptionImageURL, String profileImageURL, String category, String isSolved, boolean isHelper) {
        this.posterId = posterId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.postDate = postDate;
        this.description = description;
        this.location = location;
        this.descriptionImageURL = descriptionImageURL;
        this.profileImageURL = profileImageURL;
        this.category = category;
        this.isSolved = isSolved.equals("YES");
        this.isHelper = isHelper;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public void setSolved(boolean solved) {
        isSolved = solved;
    }

    public boolean isHelper() {
        return isHelper;
    }

    public void setHelper(boolean helper) {
        isHelper = helper;
    }
}


