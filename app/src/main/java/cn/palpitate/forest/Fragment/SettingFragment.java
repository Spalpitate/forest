package cn.palpitate.forest.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.jsoup.nodes.Document;
import org.litepal.LitePal;

import cn.palpitate.forest.Activity.AboutActivity;
import cn.palpitate.forest.Activity.MainActivity;
import cn.palpitate.forest.Class.DataSaveSucceed;
import cn.palpitate.forest.Class.HttpCallBack;
import cn.palpitate.forest.Class.LocalForestSeed;
import cn.palpitate.forest.R;
import cn.palpitate.forest.Utils.HttpUtils;

/**
 * Package : cn.palpitate.forest.Fragment
 * Created : 余悸
 * Date : 2018/9/25 13:48
 */
public class SettingFragment extends PreferenceFragment implements HttpCallBack, DataSaveSucceed { ;
    public static final String requestUrl = MainActivity.requestUrl + "/search/index/kd//p";
    private int totalPage;
    private Handler handler;
    private ProgressDialog progressDialog;
    private int nowPage = 1;        //当前加载进度
    private boolean enableSave = true;      //是否点击了取消按钮

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_setting);
        progressDialog = new ProgressDialog(getActivity());
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0x110:
                        progressDialog.setMessage("当前进度:" + nowPage + "/" + totalPage+"\n\n数据较多请耐心等待~");
                        break;
                }
            }
        };
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if ("enableDownload".equals(preference.getKey())) {
            Toast.makeText(getActivity(), "功能暂未开放，等待更新吧~", Toast.LENGTH_SHORT).show();
            SwitchPreference switchPreference = (SwitchPreference) findPreference("enableDownload");
            EditTextPreference editTextPreference = (EditTextPreference) findPreference("downloadPath");
            switchPreference.setChecked(false);
            editTextPreference.setEnabled(switchPreference.isChecked());
        } else if ("saveFlow".equals(preference.getKey())) {
            SwitchPreference switchPreference = (SwitchPreference) findPreference("saveFlow");
            if (switchPreference.isChecked()) {
                Toast.makeText(getActivity(), "开启成功,建议重启程序查看完整效果。", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "关闭成功,建议重启程序查看完整效果。", Toast.LENGTH_SHORT).show();
            }
        } else if ("vip".equals(preference.getKey())) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
            dialog.setIcon(R.drawable.setting);
            dialog.setTitle("哦哦哦骗你的");
            dialog.setMessage("Forest是一款完全免费的软件,不会收取任何费用!仅用于学习交流!Forest是一款完全免费的软件,不会收取任何费用!仅用于学习交流!Forest是一款完全免费的软件,不会收取任何费用!仅用于学习交流!怎么样,开心吧~");
            dialog.show();
        } else if ("about".equals(preference.getKey())) {
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
        } else if ("useLocalData".equals(preference.getKey())) {
            //使用本地数据库
            Toast.makeText(getActivity(), "功能暂未开放，等待更新吧~", Toast.LENGTH_SHORT).show();
            SwitchPreference switchPreference = (SwitchPreference) findPreference("useLocalData");
            switchPreference.setChecked(false);
        } else if ("initSearchList".equals(preference.getKey())) {
            //获取源站所有片源数据-每次应清空原有数据
            LitePal.deleteAll(LocalForestSeed.class);
            progressDialog.setTitle("数据本地化");
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "等不及啦", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getActivity(), "等不及了?当前获取的内容也可以搜索哦~", Toast.LENGTH_SHORT).show();
                    enableSave = false;
                    progressDialog.dismiss();
                }
            });
            progressDialog.setMessage("连接源站中...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            setLocalForestData();
        } else if ("enableSearch".equals(preference.getKey())) {
            SwitchPreference switchPreference = (SwitchPreference) findPreference("enableSearch");
            Preference initPreference = findPreference("initSearchList");
            initPreference.setEnabled(switchPreference.isChecked());
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void setLocalForestData() {
        for (int i = 0; i < MainActivity.PAGE_NUMS.size(); i++) {
            totalPage += Integer.parseInt(MainActivity.PAGE_NUMS.get(i));
        }
        HttpUtils.getDocuments(requestUrl + "/" + nowPage, this, nowPage);
    }

    @Override
    public void requestSucceed(Document document, boolean isFirstIn, int classIndex, String[] URL_COLLECTIONS) {
        //不作处理
    }

    @Override
    public void requestSucceed(Document document) {

    }

    @Override
    public void requestSucceed(Document document, final int nowPage) {
        HttpUtils.parseDocumentForCommon(document, this);
        Message message = new Message();
        message.what = 0x110;
        handler.sendMessage(message);
    }

    @Override
    public void requestFailed() {

    }

    /**
     *
     * @author 余悸
     * @time 2018/9/26 10:38
     * @Description 数据存储成功回调-继续下一次存储
     */
    @Override
    public void goOnParseSave() {
        if (nowPage == totalPage) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "获取成功~", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            if (enableSave) {
                nowPage++;
                HttpUtils.getDocuments(requestUrl + "/" + nowPage, this, nowPage);
            }
        }
    }
}
