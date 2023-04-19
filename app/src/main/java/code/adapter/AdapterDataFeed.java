package code.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.material.components.R;

import java.util.ArrayList;
import java.util.List;

import code.model.DataFeedItem;

public class AdapterDataFeed extends RecyclerView.Adapter<AdapterDataFeed.DataFeedViewHolder> {

    private List<DataFeedItem> listData = new ArrayList<>();


    @NonNull
    @Override
    public DataFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_feed, parent, false);
        return new DataFeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DataFeedViewHolder holder, int position) {
        DataFeedItem item = listData.get(position);

        holder.tvDatetime.setText(item.getDateTime());
        holder.tvValue.setText(item.getValue());
        holder.tvFeedId.setText(item.getFeedId());
    }

    public void addData(DataFeedItem newItem) {
        listData.add(newItem);

        this.notifyItemInserted(listData.size() - 1);
    }

    @Override
    public int getItemCount() {
        if (listData == null) {
            return 0;
        } else {
            return listData.size();
        }
    }

    public class DataFeedViewHolder extends RecyclerView.ViewHolder {
        TextView tvDatetime, tvValue, tvFeedId;

        public DataFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDatetime = itemView.findViewById(R.id.tvDatetime);
            tvValue = itemView.findViewById(R.id.tvValue);
            tvFeedId = itemView.findViewById(R.id.tvFeedId);
        }
    }
}
