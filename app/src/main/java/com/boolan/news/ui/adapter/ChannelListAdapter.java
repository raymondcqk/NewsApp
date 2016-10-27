package com.boolan.news.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.boolan.news.R;
import com.boolan.news.beans.Channel;

import java.util.List;

/**
 * Created by SpaceRover on 2016/9/18.
 */
public class ChannelListAdapter extends RecyclerView.Adapter<ChannelListAdapter.ChannelViewHolder> {

    private List<Channel> channelList;
    private OnCheckedChangeListener onCheckedChangeListener;

    public ChannelListAdapter(List<Channel> channelList) {
        this.channelList = channelList;
    }

    @Override
    public ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChannelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ChannelViewHolder holder, int position) {
        holder.textView.setText(channelList.get(position).getName());
        holder.checkBox.setChecked(channelList.get(position).getSubscribed() == 1);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (onCheckedChangeListener != null) {
                    onCheckedChangeListener.onCheckedChanged(channelList.get(holder.getAdapterPosition()).getId(),b);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (channelList != null) {
            return channelList.size();
        }
        return 0;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(String channelId, boolean checked);
    }

    public static class ChannelViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private CheckBox checkBox;

        public ChannelViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }
    }
}
