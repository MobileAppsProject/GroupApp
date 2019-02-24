package team6.uw.edu.amessage.chat;

import java.util.ArrayList;
import java.util.List;

public final class ChatGenerator {

    public static final List<ChatMessage> BLOGS;
    public static final int COUNT = 20;


    static {
        BLOGS = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            BLOGS.add(i, new ChatMessage
                    .Builder("",
                        "Bob" + (i + 1))
                    .addTeaser("Hey whats up are you free too........")
                    .addUrl("Not working.")
                    .build());
        }
    }


    private ChatGenerator() { }


}

//package tcss450.uw.edu.phishapp.blog;
//
//public final class ChatGenerator {
//
//    public static final ChatMessage<> POSTS;
//    public static final int COUNT = 20;
//
//
//    static {
//        POSTS = new ChatMessage[COUNT];
//        for (int i = 0; i < POSTS.length; i++) {
//            POSTS[i] = new ChatMessage
//                    .Builder("2016-10-03 12:59 pm",
//                    "Mystery Jam Monday Part 242: Blog Post #" + (i + 1))
//                    .addTeaser("<p>Phish got right down to business last night at Dick&rsquo;s&hellip; so we&rsquo;ll do the same. Roaring out of the gates with &ldquo;Ghost,&rdquo; the band offered only the second show-opening Ghost since the &lsquo;90s, the other also being at Dick&rsquo;s (<a href=\\\"http://phish.net/setlists/?d=2013-08-30&amp;highlight=222\\\">8/31/13</a>, the &ldquo;MOST SHOWS SPELL SOMETHING&rdquo; gig). Rounding out at a little over ten minutes, it was still too early to sense that this was a night where IT was happening.</p><p><img  src=\\\"http://smedia.pnet-static.com/img/herschel_1.png\\\" /><br /><small>Photo by Herschel Gelman.</small></p>")
//                    .addUrl("http://phish.net/blog/1472930164/dicks1-when-mercury-comes-out-at-night")
//                    .build();
//        }
//    }
//
//
//    private ChatGenerator() { }
//
//
//}
