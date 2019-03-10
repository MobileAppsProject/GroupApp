package team6.uw.edu.amessage.contact;

import java.util.ArrayList;
import java.util.List;

public final class ContactGenerator {

    public static final List<ContactDetail> CONTACT_DETAIL_LIST;
    public static final int COUNT = 20;


    static {
        CONTACT_DETAIL_LIST = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            CONTACT_DETAIL_LIST.add(i, new ContactDetail
                    .Builder("",
                    "BillyJean" + (i + 1))
//                    ("Hey whats up are you free too........")
                    .addEmail("Not working, " + i)
                    .build());
        }
    }


    private ContactGenerator() { }


}

//package tcss450.uw.edu.phishapp.blog;
//
//public final class ChatRoomGenerator {
//
//    public static final ChatRoom<> POSTS;
//    public static final int COUNT = 20;
//
//
//    static {
//        POSTS = new ChatRoom[COUNT];
//        for (int i = 0; i < POSTS.length; i++) {
//            POSTS[i] = new ChatRoom
//                    .Builder("2016-10-03 12:59 pm",
//                    "Mystery Jam Monday Part 242: Blog Post #" + (i + 1))
//                    .addTeaser("<p>Phish got right down to business last night at Dick&rsquo;s&hellip; so we&rsquo;ll do the same. Roaring out of the gates with &ldquo;Ghost,&rdquo; the band offered only the second show-opening Ghost since the &lsquo;90s, the other also being at Dick&rsquo;s (<a href=\\\"http://phish.net/setlists/?d=2013-08-30&amp;highlight=222\\\">8/31/13</a>, the &ldquo;MOST SHOWS SPELL SOMETHING&rdquo; gig). Rounding out at a little over ten minutes, it was still too early to sense that this was a night where IT was happening.</p><p><img  src=\\\"http://smedia.pnet-static.com/img/herschel_1.png\\\" /><br /><small>Photo by Herschel Gelman.</small></p>")
//                    .addEmail("http://phish.net/blog/1472930164/dicks1-when-mercury-comes-out-at-night")
//                    .build();
//        }
//    }
//
//
//    private ChatRoomGenerator() { }
//
//
//}
