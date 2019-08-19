package ru.vincetti.vimoney.check;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.adapters.AllCardsListRVAdapter;

public class ChecksListActivity extends AppCompatActivity {
    private AllCardsListRVAdapter mAdapter;

    public static void start(Context context) {
        context.startActivity(new Intent(context, ChecksListActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checks_list);

        findViewById(R.id.setting_navigation_back_btn).setOnClickListener(view -> finish());
        findViewById(R.id.check_list_fab)
                .setOnClickListener(view -> AddCheckActivity.start(this));

        // список карт/счетов
        mAdapter = new AllCardsListRVAdapter(itemId -> CheckActivity.start(this, itemId));
        RecyclerView cardsListView = findViewById(R.id.check_list_recycle_view);
        //cardsListView.setHasFixedSize(true);
        LinearLayoutManager cardsLayoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        cardsListView.setLayoutManager(cardsLayoutManager);
        cardsListView.setAdapter(mAdapter);
        accountsLoadFromDB();
    }

    private void accountsLoadFromDB(){
        AccountsViewModel viewModel = ViewModelProviders.of(this).get(AccountsViewModel.class);
        viewModel.getAccounts().observe(this, accounts -> {
            mAdapter.setList(accounts);
        });
    }
}
