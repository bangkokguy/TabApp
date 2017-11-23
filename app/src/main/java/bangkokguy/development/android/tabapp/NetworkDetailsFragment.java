package bangkokguy.development.android.tabapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bangkokguy.development.android.tabapp.DummyContent.DummyItem;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class NetworkDetailsFragment extends Fragment {

    final static private String TAG = NetworkDetailsFragment.class.getSimpleName();

    // TODO: Customize parameter argument names
    static final String ARG_COLUMN_COUNT = "column-count";
    static final String ARG_MESSENGER = "messenger";

    // TODO: Customize parameters
    private int mColumnCount = 1;
    private static OnListFragmentInteractionListener mListener;
    //private obsolete_ListViewAdapter mListViewAdapter;

    private IncomingHandler incomingHandler = null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NetworkDetailsFragment() {}

    IncomingHandler createHandler() {
        incomingHandler = new IncomingHandler();
        return incomingHandler;
    }

    IncomingHandler getHandler() {
        return incomingHandler;
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static NetworkDetailsFragment newInstance(int columnCount) {
        NetworkDetailsFragment fragment = new NetworkDetailsFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putParcelable(ARG_MESSENGER, new Messenger(fragment.createHandler()));
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Handler of incoming messages from clients.
     */
    static class IncomingHandler extends Handler {
        RecyclerView recyclerView;
        Context context;

        void setRecyclerView(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage->"+msg.toString());
            if(recyclerView != null && context != null)
                switch (msg.what) {
                case 1:
                    Log.v (TAG, "   Object->" + msg.obj);
                    recyclerView.setAdapter(
                            new NetworkDetailsRecyclerViewAdapter(
                                    new NetworkDetailsContent(context).getItems(), mListener));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        Context context = null;
        RecyclerView recyclerView = null;

        // Set the adapter
        if (view instanceof RecyclerView) {
            context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, 1/*mColumnCount*/));
            }
            recyclerView.setAdapter(
                    new NetworkDetailsRecyclerViewAdapter(
                            new NetworkDetailsContent(context).getItems(), mListener));
        }
        getHandler().setRecyclerView(recyclerView);
        getHandler().setContext(context);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
/*        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
