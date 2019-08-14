package ru.vincetti.vimoney.data.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.List;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.models.TransactionModel;

public class TransactionsRVAdapter extends RecyclerView.Adapter<TransactionsRVAdapter.ViewHolder> {
    private List<TransactionModel> data;
    OnTransactionClickListener mListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, date, sum;

        public ViewHolder(@NonNull View itemView, OnTransactionClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.home_transactions_name);
            date = itemView.findViewById(R.id.home_transactions_date);
            sum = itemView.findViewById(R.id.home_transactions_balance);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onTrClick(data.get(getAdapterPosition()).getId());
        }
    }

    public TransactionsRVAdapter(OnTransactionClickListener listener) {
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
        TransactionModel tmpTr = data.get(position);

        holder.name.setText(tmpTr.getDescription());
        holder.date.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(tmpTr.getDate()));
        if (tmpTr.getType() == TransactionModel.TRANSACTION_TYPE_INCOME) {
            holder.sum.setText("+" + tmpTr.getSum());
        } else {
            holder.sum.setText("-" + tmpTr.getSum());
        }
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public interface OnTransactionClickListener {
        void onTrClick(int itemId);
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setTransaction(List<TransactionModel> transList) {
        data = transList;
        notifyDataSetChanged();
    }
}
