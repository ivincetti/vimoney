package ru.vincetti.vimoney.data.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.models.Account;

public class CardsListRVAdapter extends RecyclerView.Adapter<CardsListRVAdapter.ViewHolder> {
    private ArrayList<Account> data;
    private OnCardClickListener mListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView accName, accType, accBalance;
        OnCardClickListener listener;

        public ViewHolder(@NonNull View itemView, OnCardClickListener listener) {
            super(itemView);
            accName = itemView.findViewById(R.id.home_acc_name);
            accType = itemView.findViewById(R.id.home_acc_type);
            accBalance = itemView.findViewById(R.id.home_acc_balance);

            this.listener = listener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onCardClick(getAdapterPosition());
        }
    }

    public CardsListRVAdapter(ArrayList<Account> list, OnCardClickListener listener) {
        this.data = list;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cards_list
                , parent, false);
        return new ViewHolder(view, mListener);
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

    public interface OnCardClickListener{
        void onCardClick(int position);
    }
}
