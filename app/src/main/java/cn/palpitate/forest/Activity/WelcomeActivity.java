package cn.palpitate.forest.Activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.palpitate.forest.Class.LocalForestSeed;
import cn.palpitate.forest.R;

public class WelcomeActivity extends AppCompatActivity {
    private static final String GET_UPDATE = "https://91zmw.github.io/zmw/";
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";
    private List<String> updateInfo = new ArrayList<>();
    private static String updateTime;
    private TextView statusTextView;
    private Handler handler;
    private ImageView loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        statusTextView = findViewById(R.id.status);
        loading = findViewById(R.id.loading);
        Glide.with(WelcomeActivity.this).load(R.drawable.loading).into(loading);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0x110:
                        statusTextView.setText("正在发起网络请求...");
                        break;
                    case 0x111:
                        statusTextView.setText("请求成功!获取访问地址...");
                        break;
                    case 0x112:
                        statusTextView.setText("最新地址获取成功!正在进入...");
                        break;
                    case 0x113:
                        statusTextView.setText("正在加载搜索数据库...");
                        break;
                    default:
                        break;
                }
            }
        };
        getUpdateInfo();
    }
    /**
     *
     * @author 余悸
     * @time 2018/9/21 23:33
     * @Description 解析最新地址发布页，得到相关信息。
     */
    private void getUpdateInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Message sendRequestMessage = new Message();
                    sendRequestMessage.what = 0x110;
                    handler.sendMessage(sendRequestMessage);
                    Connection.Response response = Jsoup.connect(GET_UPDATE)
                            .userAgent(USER_AGENT)
                            .timeout(10000)     //10s超时
                            .execute();
                    if (response.statusCode() == 200) {     //请求成功
                        Document doc = response.parse();
                        String updateUrl;
                        updateTime = doc.select("body > div > h1").text();
                        for (int i=1;i<=5;i++) {
                            updateUrl = doc.select("body > div > div > div > ul > li:nth-child(2) > a:nth-child("+i+")").attr("href");
                            updateInfo.add(updateUrl);
                        }
                        Message requestSucceedMessage = new Message();
                        requestSucceedMessage.what = 0x111;
                        handler.sendMessage(requestSucceedMessage);
                        startMainActivity();
                    } else {
                        //请求失败
                        finishWithError("200");
                    }

                } catch (IOException e) {
                    //获取失败--执行退出程序操作
                    finishWithError("network");
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     *
     * @author 余悸
     * @time 2018/9/21 23:41
     * @Description 请求地址--任意地址成功即跳转
     */
    private void startMainActivity() throws IOException {
        String requestUrl;  //循环获得请求地址--依次判断
        boolean isGet = false;
        for (int i=0;i<5;i++) {
            requestUrl = updateInfo.get(i);
            Document doc = Jsoup.connect(requestUrl)
                    .userAgent(USER_AGENT)
                    .timeout(50000)
                    .get();
            if (doc.title().contains("Yellow字幕网")) {        //判断网站标题是否正确-不正确则退出
                Message loadSucceedMessage = new Message();
                loadSucceedMessage.what = 0x112;
                handler.sendMessage(loadSucceedMessage);
                SharedPreferences sharedPreferences = getSharedPreferences(getPackageName()+"_preferences",MODE_PRIVATE);
                boolean enableSearch = sharedPreferences.getBoolean("enableSearch",false);
                if (enableSearch) {
                    Message message = new Message();
                    message.what = 0x113;
                    handler.sendMessage(message);
                    SearchActivity.allForestSeedItem = LitePal.findAll(LocalForestSeed.class);
                }
                isGet = true;
                Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
                intent.putExtra("updateTime",updateTime);
                intent.putExtra("requestUrl",requestUrl);
                startActivity(intent);
                finish();
                break;
            }

        }
        if (!isGet) {
            finishWithError("url");
        }
    }
    /**
     *
     * @author 余悸
     * @time 2018/9/21 23:58
     * @Description 请求出错退出程序..
     */
    private void finishWithError(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (error) {
                    case "200":
                        Toast.makeText(getApplicationContext(), "源网站请求码错误...正在退出", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case "network":
                        Toast.makeText(getApplicationContext(), "网络连接状况较差...正在退出", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case "url":
                        Toast.makeText(getApplicationContext(), "网站解析错误...正在退出", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    default:
                        break;
                }
            }
        });

    }
}
