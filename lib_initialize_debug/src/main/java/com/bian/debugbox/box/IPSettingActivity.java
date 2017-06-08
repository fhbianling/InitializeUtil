package com.bian.debugbox.box;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author 边凌
 * date 2017/3/28 16:23
 * desc ${IP设置Activity}
 */
public class IPSettingActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private TextView currentIp;
    private ListView ipList;
    private int[] etIds = new int[]{
            R.id.ipsetting_et1,
            R.id.ipsetting_et2,
            R.id.ipsetting_et3,
            R.id.ipsetting_et4,
            R.id.ipsetting_et5
    };
    private EditText[] editTexts = new EditText[etIds.length];

    private TextView addConfirm;
    private IPAdapter ipAdapter;
    private Pattern pattern = Pattern.compile("((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)");

    public static void start(Context context) {
        Intent starter = new Intent(context, IPSettingActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipsetting);
        findView();
    }

    private void findView() {
        currentIp = (TextView) findViewById(R.id.ipsetting_currentIp);
        ipList = (ListView) findViewById(R.id.ipsetting_list);
        ipAdapter = new IPAdapter(this);
        ipList.setAdapter(ipAdapter);
        ipList.setOnItemClickListener(this);
        addConfirm = (TextView) findViewById(R.id.ipsetting_addConfirm);
        addConfirm.setOnClickListener(this);
        findViewById(R.id.ipsetting_selectedConfirm).setOnClickListener(this);

        for (int i = 0; i < etIds.length; i++) {
            editTexts[i] = (EditText) findViewById(etIds[i]);
        }
        editTexts[editTexts.length - 1].setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addIPEntity();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ipsetting_addConfirm) {
            addIPEntity();
        } else if (v.getId() == R.id.ipsetting_selectedConfirm) {
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
            Toast.makeText(this, "请输入正确的IP地址,", Toast.LENGTH_SHORT).show();
            return;
        }

        ipAdapter.clearSelect();
        IPDbManager.getInstance(this).insertIP(host, port);
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

    public void selectedIp(String ip) {
        OptionsClientManager.getIpSettingClient().onIpSelected(ip);
        currentIp.setText(String.format("当前选中IP：%s", ip));
    }

    public boolean checkIp(String host) {
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
            ipEntities = IPDbManager.getInstance(context).queryListAll();
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
            ipEntities = IPDbManager.getInstance(context).queryListAll();
            notifyDataSetChanged();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (getCount()==0){
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
            TextView ip;
            TextView delete;
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
