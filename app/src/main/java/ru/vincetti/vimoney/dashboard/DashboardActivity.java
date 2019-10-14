package ru.vincetti.vimoney.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;
import ru.vincetti.vimoney.R;

public class DashboardActivity extends MvpAppCompatActivity implements DashboardView {

    @InjectPresenter
    DashboardPresenter mPresenter;

    @BindView(R.id.dashboard_txt)
    TextView dashboardText;

    @BindView(R.id.dashboard_btn)
    Button dashboardBtn;

    @BindView(R.id.dashboard_container)
    View container;

    @BindView(R.id.dashboard_progress)
    ProgressBar progressBar;

    public static void start(Context context) {
        context.startActivity(new Intent(context, DashboardActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.setting_navigation_back_btn)
    void back() {
        finish();
    }

    @OnClick(R.id.dashboard_btn)
    void btnClick() {
        mPresenter.changeText("Уже как бы октябрь");
    }

    @Override
    public void changeText(String text) {
        dashboardText.setText(text);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
    }
}
