package team6.uw.edu.phishapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import team6.uw.edu.phishapp.chat.ChatMessage;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatMessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ChatMessageFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ChatMessage myBlogPost;

    public ChatMessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat_message, container, false);

        savedInstanceState = getArguments();

        if (savedInstanceState != null) {
            myBlogPost = (ChatMessage) savedInstanceState.getSerializable("ChatMessage");

            TextView title = v.findViewById(R.id.fragChatMessage_msgTitle_textView);
            title.setText(myBlogPost.getTitle());
//            Log.wtf("WRONG", b.getAuthor());

//            TextView publishDate = v.findViewById(R.id.fragBlogPost_publishDate_textView);
//            publishDate.setText(myBlogPost.getPubDate());

            TextView fullTeaser = v.findViewById(R.id.fragBlogPost_smallTeaser_textView);
            fullTeaser.setText(Html.fromHtml(myBlogPost.getTeaser()));

            Button b = (Button) v.findViewById(R.id.fragChatMessage_sendMessage_button);
//            b.setOnClickListener(this::viewFullPost);

        }


        return v;
    }

    private void viewFullPost(View view) {
        if (mListener != null) {
            mListener.onUrlBlogPostFragmentInteraction(myBlogPost.getUrl());
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onUrlBlogPostFragmentInteraction(String url);
    }
}
