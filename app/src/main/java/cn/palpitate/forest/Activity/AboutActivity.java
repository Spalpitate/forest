package cn.palpitate.forest.Activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.palpitate.forest.Adapter.OpenSourceAdapter;
import cn.palpitate.forest.R;

public class AboutActivity extends AppCompatActivity {
    private final String[] OPNAME = new String[] {
            "Jsoup:",
            "Glide:",
            "JiaoZiVideoPlayer:",
            "FloatingActionButton:"
    };
    private final String[] OPAUTHOR = new String[] {
            "jhy","bumptech","lipangit","futuresimple"
    };
    private final String[] OPURL = new String[] {
            "Java HTML Parser\nhttps://github.com/jhy/jsoup",
            "An image loading and caching library\nhttps://github.com/bumptech/glide",
            "Highly customizable Android video player\nhttps://github.com/lipangit/JiaoZiVideoPlayer",
            "Floating Action Button for Android\nhttps://github.com/futuresimple/android-floating-action-button"
    };
    private RecyclerView recyclerView;
    private OpenSourceAdapter adapter;
    private List<Map<String,String>> openSourceList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initActionBar();
        initRecycleView();
    }
    /**
     *
     * @author 余悸
     * @time 2018/9/25 11:09
     * @Description 初始化RecycleView
     */
    private void initRecycleView() {
        for (int i=0;i<OPNAME.length;i++) {
            Map<String,String> opContent = new HashMap<>();
            opContent.put("opName",OPNAME[i]);
            opContent.put("opAuthor",OPAUTHOR[i]);
            opContent.put("opUrl",OPURL[i]);
            openSourceList.add(opContent);
        }
        recyclerView = findViewById(R.id.about_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OpenSourceAdapter(AboutActivity.this,openSourceList);
        recyclerView.setAdapter(adapter);
    }

    /**
     *
     * @author 余悸
     * @time 2018/9/25 9:23
     * @Description 初始化ActionBar
     */
    private void initActionBar() {
        Toolbar toolbar = findViewById(R.id.about_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("关于");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}
