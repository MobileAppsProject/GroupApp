package team6.uw.edu.amessage.chat_room;

import java.io.Serializable;

/**
 * This class is allows for a user to store information about a chat room.
 */
public class ChatRoom implements Serializable {
    //
    private final String mChatName;
    private final int mChatId;
    private final String mUrl;
    private final String mTeaser;
    private final String mAuthor;

    /**
     * Helper class for building chat room.
     */
    public static class Builder {
        private final String mChatName;
        private final int mChatId;
        private String mUrl = "";
        private String mTeaser = "";
        private String mAuthor = "";


        /**
         * Constructs a builder.
         *
         * @param chatId   the chat id.
         * @param chatName the chat name.
         */
        public Builder(int chatId, String chatName) {
            this.mChatId = chatId;
            this.mChatName = chatName;
        }

        /**
         * Add an optional url for the full blog post.
         *
         * @param val an optional url for the full blog post
         * @return the Builder of this ChatRoom
         */
        public Builder addUrl(final String val) {
            mUrl = val;
            return this;
        }

        /**
         * Add an optional teaser for the full blog post.
         *
         * @param val an optional url teaser for the full blog post.
         * @return the Builder of this ChatRoom
         */
        public Builder addTeaser(final String val) {
            mTeaser = val;
            return this;
        }

        /**
         * Add an optional author of the chat room.
         *
         * @param val an optional author of the blog post.
         * @return the Builder of this ChatRoom
         */
        public Builder addAuthor(final String val) {
            mAuthor = val;
            return this;
        }

        /**
         * Create a builder object all packaged up.
         *
         * @return the builder.
         */
        public ChatRoom build() {
            return new ChatRoom(this);
        }

    }

    /**
     * This will build the entire chat room info.
     *
     * @param builder the object of all the information.
     */
    private ChatRoom(final Builder builder) {
        this.mChatName = builder.mChatName;
        this.mChatId = builder.mChatId;
        this.mUrl = builder.mUrl;
        this.mTeaser = builder.mTeaser;
        this.mAuthor = builder.mAuthor;
    }

    /**
     * This will get hte chat name.
     *
     * @return the chat name.
     */
    public String getChatName() {
        return mChatName;
    }

    /**
     * This will get the chat id.
     *
     * @return the chat id.
     */
    public int getChatId() {
        return mChatId;
    }

    /**
     * This will get the url.
     *
     * @return the url.
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * This will get the teaser.
     *
     * @return the teaser.
     */
    public String getTeaser() {
        return mTeaser;
    }

    /**
     * This will get the author.
     *
     * @return the author.
     */
    public String getAuthor() {
        return mAuthor;
    }
}
