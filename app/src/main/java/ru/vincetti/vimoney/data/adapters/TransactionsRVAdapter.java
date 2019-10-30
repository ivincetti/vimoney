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
import ru.vincetti.vimoney.data.models.TransactionListModel;

public class TransactionsRVAdapter extends RecyclerView.Adapter<TransactionsRVAdapter.ViewHolder> {
    private List<TransactionListModel> data;
    private OnTransactionClickListener mListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, date, acc, sum, cur;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.home_transactions_name);
            date = itemView.findViewById(R.id.home_transactions_date);
            acc = itemView.findViewById(R.id.home_transactions_account);
            cur = itemView.findViewById(R.id.home_transactions_currency);
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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionListModel tmpTr = data.get(position);

        holder.name.setText(tmpTr.getDescription());
        holder.date.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(tmpTr.getDate()));
        holder.sum.setText(String.format("%s %s", tmpTr.getTypeString(), tmpTr.getSum()));
        holder.acc.setText(tmpTr.getAccountName());
        holder.cur.setText(tmpTr.getCurSymbol());
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

    public void setTransaction(List<TransactionListModel> transList) {
        data = transList;
        notifyDataSetChanged();
    }
}
