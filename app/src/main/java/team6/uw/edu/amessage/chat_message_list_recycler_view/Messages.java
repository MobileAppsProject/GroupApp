package team6.uw.edu.amessage.chat_message_list_recycler_view;

import java.io.Serializable;

/**
 * Class to encapsulate a Phish.net Blog Post. Building an Object requires a publish date and title.
 *
 * Optional fields include URL, teaser, and Author.
 *
 *
 * @author Charles Bryan
 * @version 14 September 2018
 */
public class Messages implements Serializable {
    //
    private final String mUserId;
    private final String mMessage;
    private final String mTimeStamp;
    private final String mUserEmail;

    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     */
    public static class Builder {
        private final String mUserId;
        private final String mMessage;
        private String mTimeStamp = "";
        private String mUserEmail = "";


        /**
         * Constructs a new Builder.
         *
         * @param theUserId the published date of the blog post
         * @param theMsg the title of the blog post
         */
        public Builder(String theUserId, String theMsg) {
            this.mUserId = theUserId;
            this.mMessage = theMsg;
        }

        /**
         * Add an optional url for the full blog post.
         * @param theTimeStamp an optional url for the full blog post
         * @return the Builder of this ChatRoom
         */
        public Builder addTimeStamp(final String theTimeStamp) {
            mTimeStamp = theTimeStamp;
            return this;
        }

        /**
         * Add an optional teaser for the full blog post.
         * @param theUserEmail an optional url teaser for the full blog post.
         * @return the Builder of this ChatRoom
         */
        public Builder addUserEmail(final String theUserEmail) {
            mUserEmail = theUserEmail;
            return this;
        }


        public Messages build() {
            return new Messages(this);
        }

    }

    private Messages(final Builder builder) {
        this.mUserId = builder.mUserId;
        this.mMessage = builder.mMessage;
        this.mTimeStamp = builder.mTimeStamp;
        this.mUserEmail = builder.mUserEmail;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getTimeStamp() {
        return mTimeStamp;
    }

    public String getUserEmail() {
        return mUserEmail;
    }



}

