package com.boolan.news.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.boolan.news.R;
import com.boolan.news.beans.Channel;
import com.boolan.news.service.DownloadService;
import com.boolan.news.ui.adapter.NewsPagerAdapter;
import com.boolan.news.utils.NewsDb;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int CODE_DATA_CHANGED = 1000;

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initData() {
        final List<String> titles = new ArrayList<>();
        final List<NewsListFragment> fragments = new ArrayList<>();

        /**
         * 数据绑定1：
         * 初试化新闻数据，获得 subscribed channel list，绑定到TabLayout的title、Fragment上
         *
         * 因为要从数据库读取，是I/O操作，使用子线程
         */
        new Thread() {
            @Override
            public void run() {
                super.run();
                //从数据库读取被订阅的Channel List
                NewsDb newsDb = new NewsDb(MainActivity.this);
                List<Channel> channels = newsDb.getSubscribedChannelList();
                for (Channel channel : channels) {
                    /**
                     * Activity和Fragment之间，通过bundle来传数据
                     */
                    Bundle args = new Bundle();
                    args.putString("channelId", channel.getId());//对Fragment来说，只需要channel id，根据id获取news list

                    NewsListFragment fragment = new NewsListFragment();
                    fragment.setArguments(args);//向Fragment传入数据

                    //添加到MainActivity的Fragments list中
                    fragments.add(fragment);
                    //添加TabLayout的titles
                    titles.add(channel.getName());
                }

                /**
                 * 操作Viewpager（UI控件),要在主线程执行
                 */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * viewpager装配数据的流程：
                         * 1. 数据源（一个或多个数据的list，一般是 titles、Fragments，或只有Fragments)
                         * 2. 将数据源加载到 PagerAdapter
                         * 3. 给viewpager绑定pagerAdapter，完成数据与viewpager控件的数据绑定
                         */
                        NewsPagerAdapter pagerAdapter = new NewsPagerAdapter
                                (getSupportFragmentManager(), fragments, titles);
                        viewPager.setAdapter(pagerAdapter);
                    }
                });
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //解析目标菜单文件（系统会自动装载该菜单）
        getMenuInflater().inflate(R.menu.main_activity, menu);
        /**
         * 我们其中一个菜单项是有搜索功能的，所以这里要进行 搜索菜单项 的配置
         */
        //获得某个菜单项（这里我们是要获得search）
        MenuItem menuItem = menu.findItem(R.id.search);
        /**
         * 通过该item，实例化 searchview（点击该search菜单项时，会自动展开一个EditText作为搜索框
         */
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        //搜索 文本查询 事件 监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("title", query);
                startActivity(intent);
                //Called when this view is collapsed as an action view. //复原该搜索菜单选项
                searchView.onActionViewCollapsed();//关掉searchviwew
                return true; //return true 拦截事件传递？
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                break;
            case R.id.download:
                Intent intent = new Intent(this, DownloadService.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startService(intent);
                break;
            case R.id.manage:
                startActivityForResult(new Intent(this, ChannelActivity.class), CODE_DATA_CHANGED);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == CODE_DATA_CHANGED) {
            initData();
        }
    }
}
