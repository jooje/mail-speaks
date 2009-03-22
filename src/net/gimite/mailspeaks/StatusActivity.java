package net.gimite.mailspeaks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class StatusActivity extends Activity {

    private Handler handler = new Handler();
    private MailChecker mailChecker;
    private Timer timer;
    private CheckBox notifyBox;
    private TextView globalStatusLabel;
    private ListView accountsView;
    private ArrayList<HashMap<String, String>> accountsData =
        new ArrayList<HashMap<String, String>>();
    private SimpleAdapter accountsAdapter;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);
        mailChecker = new MailChecker(this);

        globalStatusLabel = (TextView)findViewById(R.id.globalStatusLabel);
        notifyBox = (CheckBox)findViewById(R.id.notifyBox);
        notifyBox.setOnCheckedChangeListener(onNotifyBoxClick);
        accountsView = (ListView)findViewById(R.id.accountsView);
        accountsAdapter = new SimpleAdapter(
                this,
                accountsData,
                android.R.layout.simple_list_item_2,
                new String[] { "email", "status" },
                new int[] { android.R.id.text1, android.R.id.text2 }
                );
        accountsView.setAdapter(accountsAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();
        timer.schedule(new UpdateTask(), 0, 1000);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mailChecker.destroy();
    }

    private void startService() {
        Intent i = new Intent();
        i.setClass(this, MailCheckerService.class);
        startService(i);
    }
    
    private void stopService() {
        Intent i = new Intent();
        i.setClass(this, MailCheckerService.class);
        stopService(i);
    }
    
    private OnCheckedChangeListener onNotifyBoxClick = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked) {
            if (isChecked) {
                startService();
            } else {
                stopService();
            }
        }
    };
    
    private class UpdateTask extends TimerTask {
        public void run() {
            handler.post(new Runnable() {
                public void run() {
                    globalStatusLabel.setText(mailChecker.getGlobalStatus());
                    mailChecker.getAccountsStatus(accountsData);
                    accountsAdapter.notifyDataSetChanged();
                }
            });
        }
    };

}