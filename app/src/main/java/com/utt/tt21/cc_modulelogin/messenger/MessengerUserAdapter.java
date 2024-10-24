package com.utt.tt21.cc_modulelogin.messenger;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utt.tt21.cc_modulelogin.R;

import java.util.List;

public class MessengerUserAdapter extends RecyclerView.Adapter<MessengerUserAdapter.MessengerUserHolder> {
    private List<MessengerUserModel> list;

    public MessengerUserAdapter(List<MessengerUserModel> list, Activity context) {
        this.list = list;
        this.context = context;
    }

    Activity context;
    @NonNull
    @Override
    public MessengerUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.messenger_item, parent, false);
        return new MessengerUserAdapter.MessengerUserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessengerUserHolder holder, int position) {
        MessengerUserModel model = list.get(position);
        //holder..setText(model.getId());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MessengerUserHolder extends RecyclerView.ViewHolder {
        public MessengerUserHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
