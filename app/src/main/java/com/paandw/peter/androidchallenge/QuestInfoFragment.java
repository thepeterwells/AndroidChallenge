package com.paandw.peter.androidchallenge;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuestInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuestInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "questID";
    private static final String ARG_PARAM2 = "questNAME";
    private static final String ARG_PARAM3 = "questDESC";
    private static final String ARG_PARAM4 = "questIMAGE";
    private static final String ARG_PARAM5 = "questGIVERID";

    // TODO: Rename and change types of parameters
    private int questID, questGIVERID;
    private String questNAME, questDESC, questIMAGE;

    private OnFragmentInteractionListener mListener;

    public QuestInfoFragment() {
        // Required empty public constructor
    }

    public static QuestInfoFragment newInstance(int id, String name, String desc, String img , int giverID) {
        QuestInfoFragment fragment = new QuestInfoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, id);
        args.putString(ARG_PARAM2, name);
        args.putString(ARG_PARAM3, desc);
        args.putString(ARG_PARAM4, img);
        args.putInt(ARG_PARAM5, giverID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questID = getArguments().getInt(ARG_PARAM1);
            questNAME = getArguments().getString(ARG_PARAM2);
            questDESC = getArguments().getString(ARG_PARAM3);
            questIMAGE = getArguments().getString(ARG_PARAM4);
            questGIVERID = getArguments().getInt(ARG_PARAM5);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_quest_info, container, false);

        if (getArguments() != null) {
            questID = getArguments().getInt(ARG_PARAM1);
            questNAME = getArguments().getString(ARG_PARAM2);
            questDESC = getArguments().getString(ARG_PARAM3);
            questIMAGE = getArguments().getString(ARG_PARAM4);
            questGIVERID = getArguments().getInt(ARG_PARAM5);
        }

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
