package cn.palpitate.forest.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.jsoup.nodes.Document;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import cn.palpitate.forest.Adapter.ForestAdapter;
import cn.palpitate.forest.Class.ForestSeed;
import cn.palpitate.forest.Class.HttpCallBack;
import cn.palpitate.forest.R;
import cn.palpitate.forest.Utils.HttpUtils;

public class MainActivity extends AppCompatActivity implements HttpCallBack {
    public static String requestUrl;
    private RecyclerView recyclerView;
    public static ForestAdapter adapter;
    public  Document document;
    private Handler handler;
    private DrawerLayout drawerLayout;
    private SwipeRefreshLayout refreshLayout;
    public static List<ForestSeed> forestSeedList = new ArrayList<>();
    private FloatingActionButton lastPage;
    private FloatingActionButton nextPage;
    private FloatingActionButton search;
    private int nowPage = 1;        //当前页码数
    private String nowPageUrl;      //当前页面Url地址
    private int homePageItems;
    private TextView nowStatus;
    private String nowClassId = "-1";
    public static boolean hideImage;   //加载图片标记
    private boolean offRemind;         //首页提示
    long currentTimes = 0L;
    public static final String[] HOME_PAGE_TITLE = new String[] {
            "最新更新","无码中文","强奸中文","巨乳中文",
            "乱伦中文","制服中文","人妻中文","调教中文",
            "出轨中文","女优小视频"
    };
    public static List<String> PAGE_NUMS = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String updateTime = intent.getStringExtra("updateTime");
        requestUrl = intent.getStringExtra("requestUrl");

        String[] URL_COLLECTIONS = new String[] {
                requestUrl+"/video/index/cid/1/p",requestUrl+"/video/index/cid/2/p",requestUrl+"/video/index/cid/3/p",
                requestUrl+"/video/index/cid/4/p",requestUrl+"/video/index/cid/5/p",requestUrl+"/video/index/cid/8/p",
                requestUrl+"/video/index/cid/9/p",requestUrl+"/video/index/cid/10/p",requestUrl+"/video/index/cid/11/p"
        };
        Toast.makeText(MainActivity.this, "load:\t"+requestUrl, Toast.LENGTH_SHORT).show();
        //初始化
        Toolbar toolbar = findViewById(R.id.main_toolBar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        nowStatus = findViewById(R.id.nowPage);
        drawerLayout = findViewById(R.id.main_drawerLayout);
        refreshLayout = findViewById(R.id.main_swipeRefresh);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        initNavigation(URL_COLLECTIONS);
        initSwipeRefresh(requestUrl,true,-1,URL_COLLECTIONS);
        initRecycleView();
        initForestLists(requestUrl,true,-1,URL_COLLECTIONS);
        makeRemindToast();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0x111:
                        if (nowClassId.equals("-1")) {
                            nowStatus.setText("1/1");
                        } else {
                            nowStatus.setText(nowPage+"/"+PAGE_NUMS.get(Integer.parseInt(nowClassId)-1));
                        }
                        adapter.setList(forestSeedList);
                       // adapter.notifyDataSetChanged();         //通知adapter更新数据
                        refreshLayout.setRefreshing(false);     //关闭下拉刷新
                        break;
                }
            }
        };
    }
    /**
     *
     * @author 余悸
     * @time 2018/9/26 15:23
     * @Description 提醒弹窗
     */
    private void makeRemindToast() {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName()+"_preferences",MODE_PRIVATE);
        offRemind = sharedPreferences.getBoolean("offRemind",false);
        if (!offRemind) {
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("看片之前了解一下");
            alertDialog.setIcon(R.drawable.star)    ;
            alertDialog.setMessage("\t\t\t\t广大青年一定要坚定理想信念。“功崇惟志，业广惟勤。”理想指引人生方向，信念决定事业成败。没有理想信念，就会导致精神上“缺钙”。中国梦是全国各族人民的共同理想，也是青年一代应该牢固树立的远大理想。中国特色社会主义是我们党带领人民历经千辛万苦找到的实现中国梦的正确道路，也是广大青年应该牢固确立的人生信念。\n大佬们别搞我啊，我什么也不会~");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "明白!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //获取是否加载图片标记
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName()+"_preferences",MODE_PRIVATE);
        hideImage = sharedPreferences.getBoolean("saveFlow",false);
        //-------------------
    }

    /**
     *
     * @author 余悸
     * @time 2018/9/24 19:49
     * @Description 初始化FloatActionButton --实现上下页切换-首页不能上下页切换
     */
    private void initFloatActionButton(final String[] URL_COLLECTIONS) {
        lastPage = findViewById(R.id.lastPage);
        nextPage = findViewById(R.id.nextPage);
        search = findViewById(R.id.search);
        lastPage.setTitle("上一页");
        nextPage.setTitle("下一页");
        lastPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forestSeedList.size() != homePageItems) {       //首页显示为36条数据
                    if (nowPage == 1) {
                        Toast.makeText(MainActivity.this, "已经是第一页了哦~", Toast.LENGTH_SHORT).show();
                    } else {
                        nowPage--;
                        //循环获得classIndex
                        int classIndex = 1;
                        for (String url:URL_COLLECTIONS) {
                            if (nowPageUrl.contains(url)) {
                                break;
                            }
                            classIndex++;
                        }
                        nowClassId = String.valueOf(classIndex);
                        nowPageUrl = URL_COLLECTIONS[classIndex-1] + "/" + nowPage;
                        initForestLists(nowPageUrl,false,classIndex,URL_COLLECTIONS);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "首页无法进行上下页切换哦~", Toast.LENGTH_SHORT).show();
                }

            }
        });
        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forestSeedList.size() != homePageItems) {       //首页显示为36条数据
                    //循环获得classIndex
                    int classIndex = 1;
                    for (String url:URL_COLLECTIONS) {
                        if (nowPageUrl.contains(url)) {
                            break;
                        }
                        classIndex++;
                    }
                    nowClassId = String.valueOf(classIndex);
                    if (nowPage == Integer.parseInt(PAGE_NUMS.get(classIndex-1))) {         //最后一页
                        Toast.makeText(MainActivity.this, "已经是最后一页咯~", Toast.LENGTH_SHORT).show();
                    } else {
                        nowPage++;
                        nowPageUrl = URL_COLLECTIONS[classIndex-1] + "/" + nowPage;
                        initForestLists(nowPageUrl,false,classIndex,URL_COLLECTIONS);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "首页无法进行上下页切换哦~", Toast.LENGTH_SHORT).show();
                }
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(getPackageName()+"_preferences",MODE_PRIVATE);
                final boolean enableSearch = sharedPreferences.getBoolean("enableSearch",false);
                if (enableSearch) {
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "请先在设置中启用搜索功能哦~", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     *
     * @author 余悸
     * @time 2018/9/23 20:07
     * @Description 初始化RecycleView
     */
    private void initRecycleView() {
        recyclerView = findViewById(R.id.main_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ForestAdapter(forestSeedList,MainActivity.this);
        recyclerView.setAdapter(adapter);
    }
    /**
     *
     * @author 余悸
     * @time 2018/9/23 20:07
     * @Description 初始化ForestLists
     */
    private void initForestLists(String requestUrl,boolean isFirstIn,int classIndex,String[] URL_COLLECTIONS) {
        refreshLayout.setRefreshing(true);
        HttpUtils.getDocuments(requestUrl,this,isFirstIn,classIndex,URL_COLLECTIONS);        //发起请求
        initSwipeRefresh(requestUrl,isFirstIn,classIndex,URL_COLLECTIONS);
    }

    /**
     *
     * @author 余悸
     * @time 2018/9/23 19:51
     * @Description 初始化下拉刷新控件
     */
    private void initSwipeRefresh(final String refreshUrl, final boolean isFirstIn, final int classIndex, final String[] URL_COLLECTIONS) {

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HttpUtils.getDocuments(refreshUrl,MainActivity.this,isFirstIn,classIndex,URL_COLLECTIONS);        //发起请求   --注意解决重复数据添加
            }
        });
    }

    /**
     *
     * @author 余悸
     * @time 2018/9/23 19:25
     * @Description 初始化NavigationView --设置按钮监听事件
     */
    private void initNavigation(final String[] URL_COLLECTIONS) {
        final NavigationView navigationView = findViewById(R.id.main_navigationView);
        navigationView.setItemIconTintList(null);
        navigationView.getMenu().getItem(0).setChecked(true);
        //设置监听事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String subUrl;
                switch (item.getItemId()) {
                    case R.id.home:
                        subUrl = requestUrl;
                        nowPageUrl = subUrl;
                        nowPage = 1;
                        nowClassId = "-1";      //首页
                        drawerLayout.closeDrawers();
                        initForestLists(subUrl,true,0,URL_COLLECTIONS);       //首页就按照第一次进入处理-简单
                        break;
                    case R.id.collection:
                        //更新RecycleView数据即可
                        adapter.setList(LitePal.findAll(ForestSeed.class));
                        drawerLayout.closeDrawers();
                        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                Toast.makeText(MainActivity.this, "已经显示完整了哦~", Toast.LENGTH_SHORT).show();
                                refreshLayout.setRefreshing(false);
                            }
                        });
                        nowStatus.setText("1/1");
                        lastPage.setOnClickListener(null);
                        nextPage.setOnClickListener(null);
                        break;
                    case R.id.wm:
                        subUrl = URL_COLLECTIONS[0];
                        nowPageUrl = subUrl;
                        nowPage = 1;
                        nowClassId = "1";
                        drawerLayout.closeDrawers();
                        initForestLists(subUrl,false,1,URL_COLLECTIONS);
                        break;
                    case R.id.qj:
                        subUrl = URL_COLLECTIONS[1];
                        nowPageUrl = subUrl;
                        nowPage = 1;
                        nowClassId = "2";
                        drawerLayout.closeDrawers();
                        initForestLists(subUrl,false,2,URL_COLLECTIONS);
                        break;
                    case R.id.jr:
                        subUrl = URL_COLLECTIONS[2];
                        nowPageUrl = subUrl;
                        nowPage = 1;
                        nowClassId = "3";
                        drawerLayout.closeDrawers();
                        initForestLists(subUrl,false,3,URL_COLLECTIONS);
                        break;
                    case R.id.ll:
                        subUrl = URL_COLLECTIONS[3];
                        nowPageUrl = subUrl;
                        nowPage = 1;
                        nowClassId = "4";
                        drawerLayout.closeDrawers();
                        initForestLists(subUrl,false,4,URL_COLLECTIONS);
                        break;
                    case R.id.zf:
                        subUrl = URL_COLLECTIONS[4];
                        nowPageUrl = subUrl;
                        nowPage = 1;
                        nowClassId = "5";
                        drawerLayout.closeDrawers();
                        initForestLists(subUrl,false,5,URL_COLLECTIONS);
                        break;
                    case R.id.rq:
                        subUrl = URL_COLLECTIONS[5];
                        nowPageUrl = subUrl;
                        nowPage = 1;
                        nowClassId = "6";
                        drawerLayout.closeDrawers();
                        initForestLists(subUrl,false,6,URL_COLLECTIONS);
                        break;
                    case R.id.tj:
                        subUrl = URL_COLLECTIONS[6];
                        nowPageUrl = subUrl;
                        nowPage = 1;
                        nowClassId = "7";
                        drawerLayout.closeDrawers();
                        initForestLists(subUrl,false,7,URL_COLLECTIONS);
                        break;
                    case R.id.cg:
                        subUrl = URL_COLLECTIONS[7];
                        nowPageUrl = subUrl;
                        nowPage = 1;
                        nowClassId = "8";
                        drawerLayout.closeDrawers();
                        initForestLists(subUrl,false,8,URL_COLLECTIONS);
                        break;
                    case R.id.xsp:
                        subUrl = URL_COLLECTIONS[8];
                        nowPageUrl = subUrl;
                        nowPage = 1;
                        nowClassId = "9";
                        drawerLayout.closeDrawers();
                        initForestLists(subUrl,false,9,URL_COLLECTIONS);
                        break;
                    case R.id.setting:
                        //Toast.makeText(MainActivity.this, "设置...", Toast.LENGTH_SHORT).show();
                        Intent SettingIntent = new Intent(MainActivity.this,SettingActivity.class);
                        startActivity(SettingIntent);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.about:
                        Intent aboutIntent = new Intent(MainActivity.this,AboutActivity.class);
                        startActivity(aboutIntent);
                       // Toast.makeText(MainActivity.this, "关于...", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();                       break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    /**
     *
     * @author 余悸
     * @time 2018/9/23 16:59
     * @Description 回调接口(成功)    --判断是首次加载首页数据还是通过item项点击,从而处理不同的逻辑
     */
    @Override
    public void requestSucceed(Document document,boolean isFirstIn,int classIndex,String[] URL_COLLECTIONS) {        //classIndex在通过item项点击才生效
        this.document = document;
        if (isFirstIn) {
            for (int i = 1, j = 0; i <= 19; i += 2, j++) {
                HttpUtils.parseDocumentForHomePage(i, j, document, forestSeedList);
            }
            HttpUtils.parseDocumentForPagenums(document,PAGE_NUMS);
            initFloatActionButton(URL_COLLECTIONS);
            homePageItems = forestSeedList.size();
        } else {
            //每页21组数据
            HttpUtils.parseDocumentForCommon(classIndex,document,forestSeedList);
        }
        Message message = new Message();
        message.what = 0x111;
        handler.sendMessage(message);
    }

    @Override
    public void requestSucceed(Document document) {
        //不作处理
    }

    @Override
    public void requestSucceed(Document document, int nowPage) {

    }

    /**
     *
     * @author 余悸
     * @time 2018/9/23 19:26
     * @Description 回调接口(失败)
     */
    @Override
    public void requestFailed() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - currentTimes > 2000) {
            Toast.makeText(this, "再按一次退出程序~", Toast.LENGTH_SHORT).show();
            currentTimes = System.currentTimeMillis();
        } else {
            Toast.makeText(this, "下次再会~", Toast.LENGTH_SHORT).show();
            finish();
        }
        return;
    }
}
