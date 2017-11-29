package bangkokguy.development.android.tabapp;

import android.content.Context;
import android.net.NetworkInfo;
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

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class NetworkActivityFragment extends Fragment {

    final static private String TAG = NetworkActivityFragment.class.getSimpleName();
    final static private String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener<NetworkInfo> mListener;

    RecyclerView recyclerView;
    ArrayList<NetworkInfo> networkInfos = null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NetworkActivityFragment() {}

    static
    public NetworkActivityFragment newInstance(int columnCount) {
        NetworkActivityFragment fragment = new NetworkActivityFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public void setNetworkInfos(ArrayList<NetworkInfo> networkInfos) {
        Log.d(TAG, "setNetworkInfos");
        this.networkInfos = networkInfos;
        NetworkActivityRecyclerViewAdapter a = (NetworkActivityRecyclerViewAdapter) recyclerView.getAdapter();
        a.setValues(networkInfos);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        MainActivity m = (MainActivity) getActivity();
        this.networkInfos = m.networkInfos;
        Log.d(TAG, "onCreate networkInfos-->" + networkInfos.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
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
            recyclerView.setAdapter(new NetworkActivityRecyclerViewAdapter(
                    networkInfos, mListener));
        }
        this.recyclerView = recyclerView;
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        MainActivity m = (MainActivity) getActivity();
        m.pagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
