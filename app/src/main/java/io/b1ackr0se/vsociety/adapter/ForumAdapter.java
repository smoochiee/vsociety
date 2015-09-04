package io.b1ackr0se.vsociety.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.vsociety.R;
import io.b1ackr0se.vsociety.model.Forum;
import io.b1ackr0se.vsociety.model.Thread;
import io.b1ackr0se.vsociety.util.OnItemClickListener;
import io.b1ackr0se.vsociety.util.OnLoadMoreListener;

public class ForumAdapter extends RecyclerView.Adapter implements View.OnClickListener{
    private final int VIEW_SUBFORUM = 0;
    private final int VIEW_THREAD = 1;
    private final int VIEW_PROGRESS = 2;

    private Context context;

    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean isLoading;
    private OnLoadMoreListener onLoadMoreListener;
    private OnItemClickListener onItemClickListener;
    private int linkColor;
    private int stickyColor;

    private List<Object> list;

    public ForumAdapter(Context c, List<Object> thread, RecyclerView recyclerView) {
        context = c;
        this.list = thread;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!isLoading && totalItemCount < (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null)
                            onLoadMoreListener.onLoadMore();
                        isLoading = true;
                    }
                }
            });
        }
        linkColor = ContextCompat.getColor(context,R.color.blue_link);
        stickyColor = ContextCompat.getColor(context,R.color.red_link);
    }

    @Override
    public int getItemViewType(int position) {
        Object object = list.get(position);
        if(object==null) return VIEW_PROGRESS;
        else {
            if(object instanceof Thread) return VIEW_THREAD;
            else return VIEW_SUBFORUM;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_THREAD) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thread, parent, false);
            v.setOnClickListener(this);
            vh = new ThreadViewHolder(v);
        } else if (viewType == VIEW_SUBFORUM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subforum, parent, false);
            v.setOnClickListener(this);
            vh = new SubForumViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_progress, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object item = list.get(position);
        if(holder instanceof ThreadViewHolder) {
            Thread thread = (Thread) list.get(position);
            ((ThreadViewHolder)holder).threadName.setText(thread.getName());
            if(thread.isSticky())
                ((ThreadViewHolder)holder).threadName.setTextColor(stickyColor);
            else ((ThreadViewHolder)holder).threadName.setTextColor(linkColor);
            ((ThreadViewHolder)holder).threadStarter.setText(thread.getStarter());
            ((ThreadViewHolder)holder).threadLatest.setText(thread.getLatestReply());
            ((ThreadViewHolder)holder).threadReplies.setText(thread.getNoOfReplies() + " replies");
            holder.itemView.setTag(thread);
        } else if (holder instanceof SubForumViewHolder)  {
            Forum forum = (Forum) list.get(position);
            ((SubForumViewHolder)holder).subForumName.setText(forum.getName());
            holder.itemView.setTag(forum);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public void onClick(final View view) {
        if (onItemClickListener != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onItemClickListener.onItemClick(view, view.getTag());
                }
            }, 200);
        }
    }

    protected static class ThreadViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.threadName)TextView threadName;
        @Bind(R.id.threadStarter)TextView threadStarter;
        @Bind(R.id.threadReplies)TextView threadReplies;
        @Bind(R.id.threadLatest)TextView threadLatest;

        public ThreadViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    protected static class SubForumViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.subForumName)TextView subForumName;

        public SubForumViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.progressBar)ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}
