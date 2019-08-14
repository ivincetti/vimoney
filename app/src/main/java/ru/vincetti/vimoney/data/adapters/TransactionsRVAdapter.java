package ru.vincetti.vimoney.data.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.models.Transaction;

public class TransactionsRVAdapter extends RecyclerView.Adapter<TransactionsRVAdapter.ViewHolder> {
    private ArrayList<Transaction> data;
    OnTransactionClickListener mListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, date, sum;
        OnTransactionClickListener listener;

        public ViewHolder(@NonNull View itemView, OnTransactionClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.home_transactions_name);
            date = itemView.findViewById(R.id.home_transactions_date);
            sum = itemView.findViewById(R.id.home_transactions_balance);

            this.listener = listener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onTrClick(getAdapterPosition());
        }
    }

    public TransactionsRVAdapter(ArrayList<Transaction> list, OnTransactionClickListener listener) {
        this.data = list;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transactions_list
                , parent, false);
        return new ViewHolder(view, mListener);
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

    public interface OnTransactionClickListener{
        void onTrClick(int position);
    }
}
