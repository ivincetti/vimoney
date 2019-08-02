package ru.vincetti.vimoney.data.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.models.Account;

public class CardsListViewAdapter extends RecyclerView.Adapter<CardsListViewAdapter.ViewHolder> {
    private ArrayList<Account> data;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView accName;
        TextView accType;
        TextView accBalance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            accName = itemView.findViewById(R.id.home_acc_name);
            accType = itemView.findViewById(R.id.home_acc_type);
            accBalance = itemView.findViewById(R.id.home_acc_balance);
        }
    }

    public CardsListViewAdapter(ArrayList<Account> list) {
        this.data = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cards_list
                , parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Account tmpAcc = data.get(position);
        holder.accName.setText(tmpAcc.getName());
        holder.accType.setText(tmpAcc.getType());
        holder.accBalance.setText(String.valueOf(tmpAcc.getSum()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
