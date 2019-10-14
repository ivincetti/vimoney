package ru.vincetti.vimoney.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    public static void start(Context context) {
        context.startActivity(new Intent(context, DashboardActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);

        findViewById(R.id.setting_navigation_back_btn).setOnClickListener(view -> finish());
        dashboardBtn.setOnClickListener(
                v -> mPresenter.changeText("Уже как бы октябрь")
        );
    }

    @Override
    public void changeText(String text){
        dashboardText.setText(text);
    }
}
