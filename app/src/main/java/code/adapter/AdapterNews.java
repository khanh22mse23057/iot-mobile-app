package code.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.material.components.R;

import java.util.ArrayList;
import java.util.List;

import code.model.News;
import code.utils.Tools;

public class AdapterNews extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
    private List<News> items = new ArrayList<>();

    private final int VIEW_ITEM = 2;
    private final int VIEW_PROG = 0;
    private boolean loading = true;
    private boolean showHeader = true;
    private String status;
    private int page = 0;

    private AdapterListener<News> listener;

    public void setListener(AdapterListener<News> listener) {
        this.listener = listener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterNews(Context context, RecyclerView view, int page) {
        this.page = page;
        ctx = context;
        lastItemViewDetector(view);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView title, desc, date;
        public ImageView image;
        public View lytParent;

        public OriginalViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            desc = v.findViewById(R.id.description);
            date = v.findViewById(R.id.date);
            image = v.findViewById(R.id.image_post);
            lytParent = v.findViewById(R.id.lyt_parent);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progress_loading;
        public TextView status_loading;

        public ProgressViewHolder(View v) {
            super(v);
            progress_loading = v.findViewById(R.id.progress_loading);
            status_loading = v.findViewById(R.id.status_loading);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
            vh = new OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        final News news = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            if (news == null || news.title == null) return;
            OriginalViewHolder v = (OriginalViewHolder) holder;

            v.title.setText(Html.fromHtml(news.title));
            String summary = Html.fromHtml(news.description).toString();
            int max_length = Math.min(summary.length(), 255);
            summary = summary.substring(0, max_length).trim().replaceAll("\n", "");
            v.desc.setText(summary);
            v.date.setText(Tools.getDateTime(news.created_at, true));
            Tools.displayImage(ctx, v.image, news.image);

            v.lytParent.setOnClickListener(view -> {
                if (listener == null) return;
                listener.onClick(view, null, news, position);
            });
        } else {

            final ProgressViewHolder v = (ProgressViewHolder) holder;
            v.progress_loading.setVisibility(status == null ? View.VISIBLE : View.INVISIBLE);
            v.status_loading.setVisibility(status == null ? View.INVISIBLE : View.VISIBLE);

            if (status == null) return;
            v.status_loading.setText(status);
            v.status_loading.setOnClickListener(view -> {
                setLoaded();
                onLoadMore();
            });
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void insertData(List<News> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void setLoaded() {
        status = null;
        loading = false;
        int last_index = getItemCount() - 1;
        if (last_index > -1 && items.get(last_index) == null) {
            items.remove(last_index);
            notifyItemRemoved(last_index);
        }
    }

    public void setLoadingOrFailed(String status) {
        setLoaded();
        this.status = status;
        this.items.add(null);
        notifyItemInserted(getItemCount() - 1);
        loading = true;
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    boolean scrollDown = false;

    private void lastItemViewDetector(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE || !scrollDown) return;
                    int lastPos = layoutManager.findLastVisibleItemPosition();
                    boolean bottom = lastPos >= getItemCount() - page;
                    if (!loading && bottom && listener != null) {
                        onLoadMore();
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    scrollDown = dy > 0;
                }
            });

            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    int spanCount = layoutManager.getSpanCount();
                    return type == VIEW_PROG ? spanCount : 1;
                }
            });
        }
    }

    private void onLoadMore() {
        int current_page = getItemCount() / page;
        listener.onLoadMore(current_page);
        loading = true;
        status = null;
    }

}