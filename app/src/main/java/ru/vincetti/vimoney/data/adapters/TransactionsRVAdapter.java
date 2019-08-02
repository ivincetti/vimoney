package ru.vincetti.vimoney.data.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.models.Transaction;

public class TransactionsRVAdapter extends RecyclerView.Adapter<TransactionsRVAdapter.ViewHolder> {
    private ArrayList<Transaction> data;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView date;
        TextView sum;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.home_transactions_name);
            date = itemView.findViewById(R.id.home_transactions_date);
            sum = itemView.findViewById(R.id.home_transactions_balance);
        }
    }

    public TransactionsRVAdapter(ArrayList<Transaction> list) {
        this.data = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transactions_list
                , parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction tmpTr = data.get(position);

        holder.name.setText(tmpTr.getName());
        holder.date.setText(tmpTr.getDate());
        if (tmpTr.getType() == Transaction.TRANSACTION_TYPE_INCOME) {
            holder.sum.setText("+" + tmpTr.getSum());
        } else {
            holder.sum.setText("-" + tmpTr.getSum());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
