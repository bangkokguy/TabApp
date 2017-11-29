package bangkokguy.development.android.tabapp;

import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bangkokguy.development.android.tabapp.NetworkDetailsContent.ConfigItem;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ConfigItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class NetworkActivityRecyclerViewAdapter extends
        RecyclerView.Adapter<NetworkActivityRecyclerViewAdapter.ViewHolder> {

    final static private String TAG = NetworkActivityRecyclerViewAdapter.class.getSimpleName();

    private ArrayList<NetworkInfo> mValues;
    private final OnListFragmentInteractionListener<NetworkInfo> mListener;

    NetworkActivityRecyclerViewAdapter(
            ArrayList<NetworkInfo> items,
            OnListFragmentInteractionListener<NetworkInfo> listener) {
        mValues = items;
        mListener = listener;
    }

    void setValues(ArrayList<NetworkInfo> mValues) {
        this.mValues = mValues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.network_activity_fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        //holder.mPicView.setImageResource(mValues.get(position).getPic()); //   R.drawable.ic_menu_camera);
        //holder.mIdView.setText(mValues.get(position).getId());
        holder.mContentView.setText(mValues.get(position).toString());
        holder.mIdView.setText(Integer.toString(position));

        holder.mView.setOnClickListener(view -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mValues == null)
            return 0;
        else {
            Log.d(TAG, "getItemCount-->" + Integer.toString(mValues.size()));
            return mValues.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView mPicView;
        final TextView mIdView;
        final TextView mContentView;
        NetworkInfo mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mPicView = (ImageView) view.findViewById(R.id.pic);
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
