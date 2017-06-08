package com.bian.debugbox.box;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * author 边凌
 * date 2017/3/28 15:40
 * desc ${TODO}
 */

public class InitializeActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private final static int REQUEST_TEXTCLIENT = 0X22;
    private ListView clientList;
    private ClientAdapter clientAdapter;
    private boolean first = true;

    public static void start(Context context) {
        Intent starter = new Intent(context.getApplicationContext(), InitializeActivity.class);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        permissionCheck();
        findView();
        initClientList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (clientAdapter != null && !first) {
            clientAdapter.reload();
        } else {
            first = false;
        }
    }

    private void permissionCheck() {
        try {
            int code = PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (code != PermissionChecker.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initClientList() {
        clientAdapter = new ClientAdapter(this);
        clientList.setAdapter(clientAdapter);
        clientList.setOnItemClickListener(this);
    }

    private void findView() {
        findViewById(R.id.debug_goLauncher).setOnClickListener(this);
        clientList = (ListView) findViewById(R.id.debug_list);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.debug_goLauncher) {
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OptionsClientWrap item = clientAdapter.getItem(position);
        if (item instanceof IpSettingClientWrap) {
            IPSettingActivity.start(this);
        } else {
            TextClientActivity.start(REQUEST_TEXTCLIENT, this, item);
        }
    }

    private static class ClientAdapter extends BaseAdapter {
        private List<OptionsClientWrap> clients = OptionsClientManager.getClients();

        private Context context;

        ClientAdapter(Context context) {
            this.context = context;
        }

        private void reload() {
            clients = OptionsClientManager.getClients();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return clients.size();
        }

        @Override
        public OptionsClientWrap getItem(int position) {
            return clients.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ClientHolder clientHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_debug, parent, false);
                clientHolder = new ClientHolder(convertView);
                convertView.setTag(clientHolder);
            } else {
                clientHolder = (ClientHolder) convertView.getTag();
            }
            OptionsClientWrap item = getItem(position);
            if (item != null) {
                clientHolder.debugOptionsName.setText(String.format("设置：%s", item.getOptionsName()));
                clientHolder.debugStates.setText(item.getCurrentState());
            }
            return convertView;
        }

        private class ClientHolder {
            TextView debugOptionsName;
            TextView debugStates;

            ClientHolder(View convertView) {
                debugOptionsName = (TextView) convertView.findViewById(R.id.item_debug_tv);
                debugStates = (TextView) convertView.findViewById(R.id.item_debug_tv2);
            }
        }

    }

}
