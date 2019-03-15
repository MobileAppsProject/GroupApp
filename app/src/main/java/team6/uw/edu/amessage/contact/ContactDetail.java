package team6.uw.edu.amessage.contact;

import java.io.Serializable;

/**
 * Class to display all the information about a user.
 */
public class ContactDetail implements Serializable {

    private final String mFirstName;
    private final String mLastName;
    private final String mEmail;
    private final String mUserId;
    private final String mAuthor;
    private boolean isSelected = false;

    /**
     * Helper class for building Credentials.
     */
    public static class Builder {
        private final String mFirstName;
        private final String mLastName;
        private String mEmail = "";
        private String mUserId = "";
        private String mAuthor = "";


        /**
         * Constructs a new Builder.
         *
         * @param pubDate the published date of the blog post
         * @param title   the title of the blog post
         */
        public Builder(String pubDate, String title) {
            this.mFirstName = pubDate;
            this.mLastName = title;
        }

        /**
         * Add email for contact.
         *
         * @return the Builder of this ContactDetail
         */
        public Builder addEmail(final String val) {
            mEmail = val;
            return this;
        }

        /**
         * Add user ID.
         *
         * @return the Builder of this ContactDetail
         */
        public Builder addUserId(final String val) {
            mUserId = val;
            return this;
        }

        /**
         * Add an optional author of the blog post.
         *
         * @return the Builder of this ContactDetail
         */
        public Builder addAuthor(final String val) {
            mAuthor = val;
            return this;
        }

        /**
         * This will build the builder object.
         *
         * @return the new contact.
         */
        public ContactDetail build() {
            return new ContactDetail(this);
        }

    }

    /**
     * Sets corresponding fields.
     *
     * @param builder the new builder.
     */
    private ContactDetail(final Builder builder) {
        this.mFirstName = builder.mFirstName;
        this.mLastName = builder.mLastName;
        this.mEmail = builder.mEmail;
        this.mUserId = builder.mUserId;
        this.mAuthor = builder.mAuthor;
    }

    /**
     * Get first name of contact.
     *
     * @return first name
     */
    public String getFirstName() {
        return mFirstName;
    }

    /**
     * Get last name of contact.
     *
     * @return last name
     */
    public String getLastName() {
        return mLastName;
    }

    /**
     * Get email of contact.
     *
     * @return email
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Get user ID of contact.
     *
     * @return ID
     */
    public String getUserId() {
        return mUserId;
    }

    /**
     * Get author.
     *
     * @return
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Set selected.
     *
     * @param selected
     */
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    /**
     * Returns if selected.
     *
     * @return
     */
    public boolean isSelected() {
        return isSelected;
    }


}
