package io.b1ackr0se.vsociety.adapter;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.vsociety.R;
import io.b1ackr0se.vsociety.model.Forum;

public class IndexAdapter extends RecyclerView.Adapter<IndexAdapter.ViewHolder> implements View.OnClickListener {

    private ArrayList<Forum> forumList;
    private OnItemClickListener onItemClickListener;

    public IndexAdapter(ArrayList<Forum> list) {
        forumList = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(final View v) {
        // Give some time to the ripple to finish the effect
        if (onItemClickListener != null)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onItemClickListener.onItemClick(v, (Forum) v.getTag());
                }
            }, 300);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forum, parent, false);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Forum forum = forumList.get(position);
        holder.forumName.setText(forum.getName());
        holder.itemView.setTag(forum);
    }

    @Override
    public int getItemCount() {
        return forumList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.forumName) TextView forumName;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void add(Forum f,int position) {
        position = position == -1 ? getItemCount()  : position;
        forumList.add(position,f);
        notifyItemInserted(position);
    }

    public void remove(int position){
        if (position < getItemCount()  ) {
            forumList.remove(position);
            notifyItemRemoved(position);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view, Forum forum);

    }
}
