package team6.uw.edu.amessage.chat_message_list_recycler_view;

import java.util.ArrayList;
import java.util.List;

public final class MessagesGenerator {

    public static final List<Messages> CHAT_MESSAGES;
    public static final int COUNT = 0;


    static {
        CHAT_MESSAGES = new ArrayList<>();
        int user = 1;
        for (int i = 0; i < COUNT; i++) {
            if (i % 2 == 0) {
                user = 1;
            } else {
                user = 24;
            }
            CHAT_MESSAGES.add(i, new Messages
                    .Builder("" + user,
                    "H")
                    .addTimeStamp("12:24")
                    .addUserEmail("athera@uw.edu")
                    .build());
        }
    }


    private MessagesGenerator() { }


}

