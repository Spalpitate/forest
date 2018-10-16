package cn.palpitate.forest.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jsoup.nodes.Document;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import cn.palpitate.forest.Adapter.ContentRVAdapter;
import cn.palpitate.forest.Class.ForestSeed;
import cn.palpitate.forest.Class.HttpCallBack;
import cn.palpitate.forest.Class.LocalForestSeed;
import cn.palpitate.forest.R;
import cn.palpitate.forest.Utils.HttpUtils;

public class PlayActivity extends AppCompatActivity implements HttpCallBack {
    private String videoUrl;
    private ImageView videoImage;
    private TextView videoText;
    private LinearLayout download;
    private RecyclerView recyclerView;
    private ContentRVAdapter adapter;
    private String requestUrl,imageURL;
    public static List<ForestSeed> randomForestSeedList = new ArrayList<>();
    private int pos;
    private String which;
    private ForestSeed getForestSeed;
    private LocalForestSeed getLocalForestSeed;
    private FloatingActionButton floatingActionButton;
    private boolean isCollected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        pos = intent.getIntExtra("position",-1);
        which = intent.getStringExtra("which");
        if (which.equals("Main")) {
            getForestSeed = (ForestSeed) intent.getSerializableExtra("ForestData");
            requestUrl = getForestSeed.getAddress();
            imageURL = getForestSeed.getImageUrl();
        } else if (which.equals("Play")) {
            getForestSeed = (ForestSeed) intent.getSerializableExtra("ForestData");
            requestUrl = getForestSeed.getAddress();
            imageURL = getForestSeed.getImageUrl();
        } else if (which.equals("Search")) {
            getLocalForestSeed = (LocalForestSeed) intent.getSerializableExtra("ForestData");
            requestUrl = getLocalForestSeed.getAddress();
            imageURL = getLocalForestSeed.getImageUrl();
        }
        initView();
        HttpUtils.getDocuments(requestUrl,this);
    }

    private void initView() {
        setContentView(R.layout.activity_play);
        initRandomList();
        initActionBar();
        initRecycleView();
        download = findViewById(R.id.downloadVideo);
        floatingActionButton = findViewById(R.id.content_collect);
        //判断是否收藏--imageUrl
        List<ForestSeed> collectionForestSeeds = LitePal.where("imageUrl = ?",imageURL).find(ForestSeed.class);
        if (collectionForestSeeds.size() > 0) {
            isCollected = true;
            floatingActionButton.setImageResource(R.drawable.collection_on);
        } else {
            isCollected = false;
            floatingActionButton.setImageResource(R.drawable.collection_off);
        }
    }

    /**
     *
     * @author 余悸
     * @time 2018/9/24 11:01
     * @Description 初始化RecycleView
     */
    private void initRecycleView() {
        recyclerView = findViewById(R.id.content_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        adapter = new ContentRVAdapter(PlayActivity.this,randomForestSeedList);
        recyclerView.setAdapter(adapter);
    }
    /**
     *
     * @author 余悸
     * @time 2018/9/24 12:33
     * @Description 添加随机6个推荐
     */
    private void initRandomList() {
        randomForestSeedList.clear();
        int lastIndex = -1;
        Random random = new Random();
        for (int i=0;i<6;i++) {
            int index = random.nextInt(MainActivity.forestSeedList.size());
            while (lastIndex == index)
                index = random.nextInt(MainActivity.forestSeedList.size());
            randomForestSeedList.add(MainActivity.forestSeedList.get(index));
            lastIndex = index;
        }
    }
    /**
     *
     * @author 余悸
     * @time 2018/9/24 11:00
     * @Description 初始化Actionbar
     */
    private void initActionBar() {
        Toolbar toolbar = findViewById(R.id.content_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if ( actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (which.equals("Main")) {
                actionBar.setTitle(getForestSeed.getClassFor());
            } else if (which.equals("Play")) {
                actionBar.setTitle(randomForestSeedList.get(pos).getClassFor());
            } else if (which.equals("Search")) {
                actionBar.setTitle("搜索结果");
            } else if (which.equals("collect")) {

            }
        }
    }

    @Override
    public void requestSucceed(Document document, boolean isFirstIn, int classIndex,String[] URL_COLLECTIONS) {
        //不作处理
    }
    /**
     *
     * @author 余悸
     * @time 2018/9/23 23:30
     * @Description 回调函数
     */
    @Override
    public void requestSucceed(Document document) {
        Document newDocument = document;
        videoUrl = HttpUtils.parseDocumentForVideo(newDocument);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //请求必要的权限
                        if (ContextCompat.checkSelfPermission(PlayActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(PlayActivity.this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                        } else {
                            startDownload();
                        }
                    }
                });
                JzvdStd player = findViewById(R.id.player);
                if (which.equals("Main")) {
                    player.setUp(videoUrl,getForestSeed.getTitle(), Jzvd.SCREEN_WINDOW_NORMAL);
                } else if (which.equals("Play")) {
                    player.setUp(videoUrl,getForestSeed.getTitle(), Jzvd.SCREEN_WINDOW_NORMAL);
                } else if (which.equals("Search")) {
                    player.setUp(videoUrl,getLocalForestSeed.getTitle(), Jzvd.SCREEN_WINDOW_NORMAL);

                }
                videoImage = findViewById(R.id.video_image);
                if (!MainActivity.hideImage) {
                    Glide.with(PlayActivity.this).load(imageURL).into(videoImage);
                }
                videoText = findViewById(R.id.videoUrl);
                videoText.setText(requestUrl);
                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isCollected) {     //收藏
                            floatingActionButton.setImageResource(R.drawable.collection_on);
                            ForestSeed collectionForestSeed = new ForestSeed();
                            if (which.equals("Main")) {
                                collectionForestSeed.setTitle(MainActivity.forestSeedList.get(pos).getTitle());
                                collectionForestSeed.setImageUrl(MainActivity.forestSeedList.get(pos).getImageUrl());
                                collectionForestSeed.setAddress(MainActivity.forestSeedList.get(pos).getAddress());
                                collectionForestSeed.setClassFor(MainActivity.forestSeedList.get(pos).getClassFor());
                                collectionForestSeed.setUpdateTime(MainActivity.forestSeedList.get(pos).getUpdateTime());
                                collectionForestSeed.setViewers(MainActivity.forestSeedList.get(pos).getViewers());
                                collectionForestSeed.save();
                            } else if (which.equals("Play")) {
                                collectionForestSeed.setTitle(getForestSeed.getTitle());
                                collectionForestSeed.setImageUrl(getForestSeed.getImageUrl());
                                collectionForestSeed.setAddress(getForestSeed.getAddress());
                                collectionForestSeed.setClassFor(getForestSeed.getClassFor());
                                collectionForestSeed.setUpdateTime(getForestSeed.getUpdateTime());
                                collectionForestSeed.setViewers(getForestSeed.getViewers());
                                collectionForestSeed.save();
                            } else if (which.equals("Search")) {
                                collectionForestSeed.setTitle(getLocalForestSeed.getTitle());
                                collectionForestSeed.setImageUrl(getLocalForestSeed.getImageUrl());
                                collectionForestSeed.setAddress(getLocalForestSeed.getAddress());
                                collectionForestSeed.setClassFor("搜索推荐");
                                collectionForestSeed.setUpdateTime(getLocalForestSeed.getUpdateTime());
                                collectionForestSeed.setViewers(getLocalForestSeed.getViewers());
                                collectionForestSeed.save();
                            } else if (which.equals("collect")) {

                            }
                            Toast.makeText(PlayActivity.this, "收藏成功~", Toast.LENGTH_SHORT).show();
                            isCollected = true;
                        } else {
                            floatingActionButton.setImageResource(R.drawable.collection_off);
                            LitePal.deleteAll(ForestSeed.class,"imageUrl = ?",imageURL);
                            Toast.makeText(PlayActivity.this, "成功取消收藏~", Toast.LENGTH_SHORT).show();
                            MainActivity.adapter.setList(LitePal.findAll(ForestSeed.class));        //更新Adapter
                            isCollected = false;
                        }
                    }
                });
            }
        });
    }

    @Override
    public void requestSucceed(Document document, int nowPage) {

    }

    /**
     *
     * @author 余悸
     * @time 2018/9/24 14:22
     * @Description 开始下载
     */
    private void startDownload() {
        Toast.makeText(PlayActivity.this, "Come soon~", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void requestFailed() {

    }
    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDownload();
                } else {
                    Toast.makeText(this, "不给权限还想下载.没门~", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
