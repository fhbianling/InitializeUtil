package com.bian.debugbox.box;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bian.debugbox.box.client.IpSettingClient;
import com.bian.debugbox.box.client.OptionsClient;

import java.util.List;

/**
 * author 边凌
 * date 2017/3/28 15:40
 * desc ${TODO}
 */

public class InitializeHomeActivity extends Activity
        implements View.OnClickListener, AdapterView.OnItemClickListener {
    private final static int REQUEST_TEXT_CLIENT = 0X22;
    private static boolean sExisting;
    private ListView clientList;
    private ClientAdapter clientAdapter;
    private boolean first = true;

    public static void start(Context context) {
        Intent starter = new Intent(context.getApplicationContext(), InitializeHomeActivity.class);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    public static boolean isExisting() {
        return sExisting;
    }

    private static void setExisting(boolean sExisting) {
        InitializeHomeActivity.sExisting = sExisting;
        FloatingButton.setVisible(!sExisting);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindowSetting();
        setContentView(R.layout.activity_debug);
        permissionCheck();
        findView();
        initClientList();
        setExisting(true);
    }

    private void initWindowSetting() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
            int code = PermissionChecker.checkSelfPermission(this,
                                                             Manifest.permission
                                                                     .WRITE_EXTERNAL_STORAGE);
            if (code != PermissionChecker.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } catch (Exception e) {
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
        OptionsClient item = clientAdapter.getItem(position);
        if (item instanceof IpSettingClient) {
            IPSettingActivity.start(this, item);
        } else {
            TextClientActivity.start(REQUEST_TEXT_CLIENT, this, item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.d("onDestroy");
        setExisting(false);
    }

    private static class ClientAdapter extends BaseAdapter {
        private SharedPrefUtil sharedPrefUtil;
        private List<OptionsClient> clients = OptionsClientManager.getClients();

        private Context context;

        ClientAdapter(Context context) {
            this.context = context;
            sharedPrefUtil = SharedPrefUtil.getInstance(context);
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
        public OptionsClient getItem(int position) {
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
                convertView = LayoutInflater.from(context)
                                            .inflate(R.layout.item_debug, parent, false);
                clientHolder = new ClientHolder(convertView);
                convertView.setTag(clientHolder);
            } else {
                clientHolder = (ClientHolder) convertView.getTag();
            }
            OptionsClient item = getItem(position);
            if (item != null) {
                setCurrentValue(clientHolder, item);
            }
            return convertView;
        }

        private void setCurrentValue(ClientHolder clientHolder, OptionsClient item) {
            clientHolder.debugOptionsName.setText(
                    String.format(context.getString(R.string.hint_13), item.getOptionsName()));

            String formatOfCurrentSetting = context.getString(R.string.hint_11);
            String formatOfDefaultSetting = context.getString(R.string.hint_12);

            if (!(item instanceof IpSettingClient)) {
                String currentValue = sharedPrefUtil.getString(item);
                String showInfo;
                if (TextUtils.isEmpty(currentValue)) {
                    showInfo = String.format(formatOfDefaultSetting,
                                             String.valueOf(item.getDefaultValue()));
                } else {
                    showInfo = currentValue;
                }
                clientHolder.debugStates.setText(
                        String.format(formatOfCurrentSetting, showInfo));
            } else {
                IPDbManager.IPEntity ipEntity = IPDbManager.getInstance(context)
                                                           .querySelected(item.getOptionsName());

                clientHolder.debugStates.setText(
                        String.format(formatOfCurrentSetting, ipEntity != null ?
                                                              ipEntity.getIp() :
                                                              String.format(formatOfDefaultSetting,
                                                                            ((IpSettingClient) item)
                                                                                    .getDefaultValue())));
            }
        }

        private static class ClientHolder {
            TextView debugOptionsName;
            TextView debugStates;

            ClientHolder(View convertView) {
                debugOptionsName = (TextView) convertView.findViewById(R.id.item_debug_tv);
                debugStates = (TextView) convertView.findViewById(R.id.item_debug_tv2);
            }
        }

    }
}
