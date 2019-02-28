package team6.uw.edu.amessage.contact;

import java.io.Serializable;

/**
 * Class to encapsulate a ContactDetail. Building an Object requires a publish date and title.
 *
 * Optional fields include URL, teaser, and Author.
 *
 *
 * @author Charles Bryan
 * @version 14 September 2018
 */
public class ContactDetail implements Serializable {

    private final String mFirstName;
    private final String mLastName;
    private final String mEmail;
    private final String mUserId;
    private final String mAuthor;

    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     */
    public static class Builder {
        private final String mFirstName;
        private final String mLastName;
        private  String mEmail = "";
        private  String mUserId = "";
        private  String mAuthor = "";


        /**
         * Constructs a new Builder.
         *
         * @param pubDate the published date of the blog post
         * @param title the title of the blog post
         */
        public Builder(String pubDate, String title) {
            this.mFirstName = pubDate;
            this.mLastName = title;
        }

        /**
         * Add an optional url for the full blog post.
         * @param val an optional url for the full blog post
         * @return the Builder of this ContactDetail
         */
        public Builder addEmail(final String val) {
            mEmail = val;
            return this;
        }

        /**
         * Add an optional teaser for the full blog post.
         * @param val an optional url teaser for the full blog post.
         * @return the Builder of this ContactDetail
         */
        public Builder addUserId(final String val) {
            mUserId = val;
            return this;
        }

        /**
         * Add an optional author of the blog post.
         * @param val an optional author of the blog post.
         * @return the Builder of this ContactDetail
         */
        public Builder addAuthor(final String val) {
            mAuthor = val;
            return this;
        }

        public ContactDetail build() {
            return new ContactDetail(this);
        }

    }

    private ContactDetail(final Builder builder) {
        this.mFirstName = builder.mFirstName;
        this.mLastName = builder.mLastName;
        this.mEmail = builder.mEmail;
        this.mUserId = builder.mUserId;
        this.mAuthor = builder.mAuthor;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getAuthor() {
        return mAuthor;
    }


}
