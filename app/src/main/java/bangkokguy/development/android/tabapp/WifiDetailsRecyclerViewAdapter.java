package bangkokguy.development.android.tabapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import bangkokguy.development.android.tabapp.WifiDetailsContent.ConfigItem;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ConfigItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class WifiDetailsRecyclerViewAdapter extends
        RecyclerView.Adapter<WifiDetailsRecyclerViewAdapter.ViewHolder> {

    private final List<ConfigItem> mValues;
    private final OnListFragmentInteractionListener<ConfigItem> mListener;

    WifiDetailsRecyclerViewAdapter(
            List<ConfigItem> items,
            OnListFragmentInteractionListener<ConfigItem> listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wifi_details_fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mPicView.setImageResource(mValues.get(position).getPic()); //   R.drawable.ic_menu_camera);
        holder.mIdView.setText(mValues.get(position).getId());
        holder.mContentView.setText(mValues.get(position).getContent());

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
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView mPicView;
        final TextView mIdView;
        final TextView mContentView;
        ConfigItem mItem;

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
