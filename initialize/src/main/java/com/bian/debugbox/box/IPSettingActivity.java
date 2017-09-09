package com.bian.debugbox.box;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bian.debugbox.box.client.OptionsClient;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author 边凌
 * date 2017/3/28 16:23
 * desc ${IP设置Activity}
 */
public class IPSettingActivity extends Activity implements View.OnClickListener, AdapterView
        .OnItemClickListener {
    private static final String KEY_CLIENT = "client";
    private final int[] etIds = new int[]{
            R.id.ipSetting_et1,
            R.id.ipSetting_et2,
            R.id.ipSetting_et3,
            R.id.ipSetting_et4,
            R.id.ipSetting_et5
    };
    private final Pattern pattern = Pattern.compile("((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}" +
            "(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)");
    private TextView currentIp;
    private ListView ipList;
    private EditText[] editTexts = new EditText[etIds.length];
    private IPAdapter ipAdapter;
    private String clientName;

    public static void start(Context context, OptionsClient item) {
        Intent starter = new Intent(context, IPSettingActivity.class);
        starter.putExtra(KEY_CLIENT, item.getOptionsName());
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSetting();
        setContentView(R.layout.activity_ipsetting);
        clientName = getIntent().getStringExtra(KEY_CLIENT);
        findView();
    }

    private void initSetting() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void findView() {
        currentIp = (TextView) findViewById(R.id.ipSetting_currentIp);
        ipList = (ListView) findViewById(R.id.ipSetting_list);
        ipAdapter = new IPAdapter(this);
        ipList.setAdapter(ipAdapter);
        ipList.setOnItemClickListener(this);
        TextView addConfirm = (TextView) findViewById(R.id.ipSetting_addConfirm);
        addConfirm.setOnClickListener(this);
        findViewById(R.id.ipSetting_selectedConfirm).setOnClickListener(this);

        for (int i = 0; i < etIds.length; i++) {
            editTexts[i] = (EditText) findViewById(etIds[i]);
        }
        editTexts[editTexts.length - 1].setOnEditorActionListener(new TextView
                .OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addIPEntity();
                }
                return false;
            }
        });
        TextView title = (TextView) findViewById(R.id.ipSetting_title);
        title.setText(clientName);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ipSetting_addConfirm) {
            addIPEntity();
        } else if (v.getId() == R.id.ipSetting_selectedConfirm) {
            finish();
        }
    }

    private void addIPEntity() {
        String host = "";
        String port = "";
        for (int i = 0; i < editTexts.length; i++) {
            String str = editTexts[i].getText() != null ? editTexts[i].getText().toString() : "";
            if (i < editTexts.length - 1) {
                host = host + str + (i == editTexts.length - 2 ? "" : ".");
            } else {
                port = str;
            }
        }
        if (!checkIp(host)) {
            Toast.makeText(this, R.string.hint_7, Toast.LENGTH_SHORT).show();
            return;
        }

        ipAdapter.clearSelect();
        IPDbManager.getInstance(this).insertIP(clientName, host, port);
        ipAdapter.refresh(this);
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(editTexts[0].getWindowToken(), 0);
        }
        for (EditText editText : editTexts) {
            editText.clearFocus();
            editText.setText("");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int checkedItemPosition = ipList.getCheckedItemPosition();
        ipAdapter.setSelectedPosition(checkedItemPosition);
        ipAdapter.refresh(this);
    }

    private void selectedIp(String ip) {
        getClient().onResult(ip);
        currentIp.setText(String.format(getString(R.string.hint_8), ip));
    }

    private OptionsClient getClient() {
        return OptionsClientManager.getOptionsClient(getIntent().getStringExtra(KEY_CLIENT));
    }

    private boolean checkIp(String host) {
        Matcher matcher = pattern.matcher(host);
        return matcher.matches();
    }

    /**
     * author 边凌
     * date 2017/3/28 17:27
     * desc ${TODO}
     */

    private class IPAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<IPDbManager.IPEntity> ipEntities;
        private Context context;

        IPAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            ipEntities = IPDbManager.getInstance(context).queryListAll(clientName);
            this.context = context;
        }

        @Override
        public int getCount() {
            return ipEntities != null ? ipEntities.size() : 0;
        }

        @Override
        public IPDbManager.IPEntity getItem(int position) {
            return ipEntities != null ? ipEntities.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            IPHolder ipHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_ip, parent, false);
                ipHolder = new IPHolder(convertView);
                convertView.setTag(ipHolder);
            } else {
                ipHolder = (IPHolder) convertView.getTag();
            }
            ipHolder.show(getItem(position));
            return convertView;
        }

        void setSelectedPosition(int selectedPosition) {
            for (int i = 0; i < ipEntities.size(); i++) {
                getItem(i).setSelected(selectedPosition == i);
                IPDbManager.getInstance(context).updateIp(getItem(i));
            }
            refresh(context);
        }

        void refresh(Context context) {
            ipEntities = IPDbManager.getInstance(context).queryListAll(clientName);
            notifyDataSetChanged();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (getCount() == 0) {
                selectedIp("");
            }
        }

        void clearSelect() {
            for (IPDbManager.IPEntity ipEntity : ipEntities) {
                ipEntity.setSelected(false);
                IPDbManager.getInstance(context).updateIp(ipEntity);
            }
        }

        private class IPHolder {
            private TextView ip;
            private TextView delete;
            private View root;

            IPHolder(View root) {
                ip = (TextView) root.findViewById(R.id.item_ip);
                delete = (TextView) root.findViewById(R.id.item_ip_delete);
                this.root = root;
            }

            void show(final IPDbManager.IPEntity ipEntity) {
                if (ipEntity == null) {
                    return;
                }
                ip.setText(ipEntity.getIp());
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IPDbManager.getInstance(context).delete(ipEntity);
                        refresh(context);
                    }
                });
                if (ipEntity.isSelected()) {
                    selectedIp(ipEntity.getIp());
                    root.setBackgroundResource(R.drawable.bg_item_ip);
                } else {
                    root.setBackgroundResource(R.drawable.bg_item_ip_2);
                }

            }
        }
    }
}
