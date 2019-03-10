package team6.uw.edu.amessage.chat_message_list_recycler_view;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import team6.uw.edu.amessage.LoginFragment;
import team6.uw.edu.amessage.R;


import java.util.List;


    public class chatMessageListRecyclerViewAdapter extends RecyclerView.Adapter {
        private static final int VIEW_TYPE_MESSAGE_SENT = 1;
        private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

        private List<Messages> mMessageList;

        public chatMessageListRecyclerViewAdapter(List<Messages> messageList) {
//            mContext = context;
            mMessageList = messageList;
//            Log.d("recView", "Set the list in the recycler: ");
        }

        @Override
        public int getItemCount() {
            Log.d("recView", "List Size: " + mMessageList.size());
            return mMessageList.size();
        }

        // Determines the appropriate ViewType according to the sender of the message.
        @Override
        public int getItemViewType(int position) {
            Messages message = (Messages) mMessageList.get(position);
            Log.d("recView", "theMessage: " + message.getUserId());

            //Check list index to see if that messages is from the same user logged in to the app.
            if (Integer.parseInt(message.getUserId()) == Integer.parseInt(LoginFragment.mUserId)) {
                // If the current user is the sender of the message
                return VIEW_TYPE_MESSAGE_SENT;
            } else {
                // If some other user sent the message
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
        }

        // Inflates the appropriate layout according to the ViewType.
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;

            if (viewType == VIEW_TYPE_MESSAGE_SENT) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_box_sent, parent, false);
                return new SentMessageHolder(view);
            } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_box_received, parent, false);
                return new ReceivedMessageHolder(view);
            }

            return null;
        }

        // Passes the message object to a chatMessageListRecyclerViewAdapter so that the contents can be bound to UI.
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Messages message = (Messages) mMessageList.get(position);

            switch (holder.getItemViewType()) {
                case VIEW_TYPE_MESSAGE_SENT:
                    ((SentMessageHolder) holder).bind(message);
                    break;
                case VIEW_TYPE_MESSAGE_RECEIVED:
                    ((ReceivedMessageHolder) holder).bind(message);
            }
        }

        private class SentMessageHolder extends RecyclerView.ViewHolder {
            TextView messageText, timeText;

            SentMessageHolder(View itemView) {
                super(itemView);
                Log.d("recView", "Set text field in sent ");
                //This will set the text fields of the sent message
                messageText = (TextView) itemView.findViewById(R.id.text_message_body);
                timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            }

            void bind(Messages message) {
                //This will set the text of those text fields.
                messageText.setText(message.getMessage());
                timeText.setText(message.getTimeStamp());
            }
        }

        private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
            TextView messageText, timeText, nameText;

            ReceivedMessageHolder(View itemView) {
                super(itemView);
                Log.d("recView", "Set text field in recieved ");

                messageText = (TextView) itemView.findViewById(R.id.text_message_body);
                timeText = (TextView) itemView.findViewById(R.id.text_message_time);
                nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            }

            void bind(Messages message) {

                //This will set the text fields of the received holder.
                messageText.setText(message.getMessage());
                timeText.setText(message.getTimeStamp());
                nameText.setText(message.getUserEmail());

            }
        }

    }