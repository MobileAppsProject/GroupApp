package team6.uw.edu.amessage.chat_message_list_recycler_view;

import java.io.Serializable;

/**
 * A class to hold all the information about a current message.
 */
public class Messages implements Serializable {
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
         * @param theMsg    the title of the blog post
         */
        public Builder(String theUserId, String theMsg) {
            this.mUserId = theUserId;
            this.mMessage = theMsg;
        }

        /**
         * Add an optional url for the full blog post.
         *
         * @param theTimeStamp an optional url for the full blog post
         * @return the Builder of this ChatRoom
         */
        public Builder addTimeStamp(final String theTimeStamp) {
            mTimeStamp = theTimeStamp;
            return this;
        }

        /**
         * Add an optional teaser for the full blog post.
         *
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

    /**
     * Constructor for building the object allowing for more security and
     * more optional parameters.
     *
     * @param builder the incoming builder to be constructed.
     */
    private Messages(final Builder builder) {
        this.mUserId = builder.mUserId;
        this.mMessage = builder.mMessage;
        this.mTimeStamp = builder.mTimeStamp;
        this.mUserEmail = builder.mUserEmail;
    }

    /**
     * Allows to get the user id.
     *
     * @return the user id.
     */
    public String getUserId() {
        return mUserId;
    }

    /**
     * This message of the current user.
     *
     * @return the message.
     */
    public String getMessage() {
        return mMessage;
    }

    /**
     * The time stamp of message sent.
     *
     * @return the time.
     */
    public String getTimeStamp() {
        return mTimeStamp;
    }

    /**
     * The user email sending the message.
     *
     * @return the email.
     */
    public String getUserEmail() {
        return mUserEmail;
    }


}

