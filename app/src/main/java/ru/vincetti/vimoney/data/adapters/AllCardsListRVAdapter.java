package ru.vincetti.vimoney.data.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.models.AccountListModel;

public class AllCardsListRVAdapter extends RecyclerView.Adapter<AllCardsListRVAdapter.CardsViewHolder> {
    private List<AccountListModel> data;
    private OnCardClickListener mListener;

    class CardsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView accName, accType, accBalance, isArchive, accSymbol;
        View accContainer;

        CardsViewHolder(@NonNull View itemView) {
            super(itemView);
            accName = itemView.findViewById(R.id.acc_name);
            accType = itemView.findViewById(R.id.acc_type);
            accBalance = itemView.findViewById(R.id.acc_balance);
            isArchive = itemView.findViewById(R.id.acc_archive);
            accSymbol = itemView.findViewById(R.id.acc_symbol);
            accContainer = itemView.findViewById(R.id.acc_list_container);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onCardClick(data.get(getAdapterPosition()).getId());
        }
    }

    public AllCardsListRVAdapter(OnCardClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public CardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_cards_list
                , parent, false);
        return new CardsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardsViewHolder holder, int position) {
        AccountListModel tmpAcc = data.get(position);
        holder.accName.setText(tmpAcc.getName());
        holder.accType.setText(tmpAcc.getType());
        holder.accBalance.setText(String.valueOf(tmpAcc.getSum()));
        holder.accSymbol.setText(tmpAcc.getSymbol());
        holder.accContainer.setBackgroundColor(ColorUtils.setAlphaComponent(Color.parseColor(tmpAcc.getColor()), 35));
        if(!tmpAcc.isArhive()){
            holder.isArchive.setVisibility(View.INVISIBLE);
        } else {
            holder.isArchive.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public void setList(List<AccountListModel> accList) {
        data = accList;
        notifyDataSetChanged();
    }

    public interface OnCardClickListener {
        void onCardClick(int itemId);
    }
}
