package team6.uw.edu.amessage.chat;

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
public class ChatMessage implements Serializable {
    //
    private final String mPubDate;
    private final int mChatId;
    private final String mUrl;
    private final String mTeaser;
    private final String mAuthor;

    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     */
    public static class Builder {
        private final String mPubDate;
        private final int mChatId;
        private String mUrl = "";
        private String mTeaser = "";
        private String mAuthor = "";


        /**
         * Constructs a new Builder.
         *
         * @param pubDate the published date of the blog post
         * @param title the title of the blog post
         */
        public Builder(String pubDate, int title) {
            this.mPubDate = pubDate;
            this.mChatId = title;
        }

        /**
         * Add an optional url for the full blog post.
         * @param val an optional url for the full blog post
         * @return the Builder of this ChatMessage
         */
        public Builder addUrl(final String val) {
            mUrl = val;
            return this;
        }

        /**
         * Add an optional teaser for the full blog post.
         * @param val an optional url teaser for the full blog post.
         * @return the Builder of this ChatMessage
         */
        public Builder addTeaser(final String val) {
            mTeaser = val;
            return this;
        }

        /**
         * Add an optional author of the blog post.
         * @param val an optional author of the blog post.
         * @return the Builder of this ChatMessage
         */
        public Builder addAuthor(final String val) {
            mAuthor = val;
            return this;
        }

        public ChatMessage build() {
            return new ChatMessage(this);
        }

    }

    private ChatMessage(final Builder builder) {
        this.mPubDate = builder.mPubDate;
        this.mChatId = builder.mChatId;
        this.mUrl = builder.mUrl;
        this.mTeaser = builder.mTeaser;
        this.mAuthor = builder.mAuthor;
    }

    public String getPubDate() {
        return mPubDate;
    }

    public int getTitle() {
        return mChatId;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getTeaser() {
        return mTeaser;
    }

    public String getAuthor() {
        return mAuthor;
    }


}
