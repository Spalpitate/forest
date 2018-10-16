package cn.palpitate.forest.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import cn.palpitate.forest.Adapter.SearchAdapter;
import cn.palpitate.forest.Class.LocalForestSeed;
import cn.palpitate.forest.R;

/**
 * Package : cn.palpitate.forest.Activity
 * Created : 余悸
 * Date : 2018/9/25 19:58
 */
public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    private List<LocalForestSeed> searchList = new ArrayList<>();
    private List<LocalForestSeed> filterForestSeed;
    public static List<LocalForestSeed> allForestSeedItem = new ArrayList<>();
    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        initActionbar();
        TextView textView = findViewById(R.id.remind_text);
        textView.setText("搜索不到想要的结果?"+"\n\n"+"试试进行打开设置-初始化搜索数据库再来吧~");
        initRecycleView();
    }

    private void initRecycleView() {
        recyclerView = findViewById(R.id.search_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter(searchList,SearchActivity.this);
        recyclerView.setAdapter(adapter);
    }

    /**
     *
     * @author 余悸
     * @time 2018/9/25 20:12
     * @Description 初始化SearchView
     */
    private void initActionbar() {
        Toolbar toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        //通过MenuItem得到SearchView
        searchView = (SearchView) searchItem.getActionView();
       // searchView.setIconifiedByDefault(false );
        searchView.setSubmitButtonEnabled(true);
        //搜索框展开时后面叉叉按钮的点击事件
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                findViewById(R.id.search_title).setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        //搜索图标按钮(打开搜索框的按钮)的点击事件
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.search_title).setVisibility(View.GONE);
            }
        });
        //搜索框文字变化监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                searchList.clear();
                searchList = LitePal.where("title like ?","%"+s+"%").find(LocalForestSeed.class);
                //initRecycleView();
                adapter.setList(searchList);
                searchView.clearFocus();        //  收起键盘
                //searchView.onActionViewCollapsed();     //收起SearchView视图
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterForestSeed = filter(allForestSeedItem,s);
                adapter.setFilter(filterForestSeed);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private List<LocalForestSeed> filter(List<LocalForestSeed> allForestSeedItem,String s) {
        filterForestSeed = new ArrayList<>();
        for (LocalForestSeed tmpForestSeed:allForestSeedItem) {
            if(tmpForestSeed.getTitle().contains(s)) {
                filterForestSeed.add(tmpForestSeed);
            }
        }
        return filterForestSeed;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearData();
    }

    private void clearData() {
        if (allForestSeedItem.size() > 0 && searchList.size() > 0 && filterForestSeed.size() > 0) {
            filterForestSeed.clear();
            searchList.clear();
        }
    }
}
