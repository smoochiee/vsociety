package io.b1ackr0se.vsociety.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.vsociety.R;
import io.b1ackr0se.vsociety.adapter.ForumAdapter;
import io.b1ackr0se.vsociety.jsoup.Parser;
import io.b1ackr0se.vsociety.model.Forum;
import io.b1ackr0se.vsociety.util.OnItemClickListener;
import io.b1ackr0se.vsociety.widget.SimpleDividerItemDecoration;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ForumActivity extends AppCompatActivity implements OnItemClickListener{

    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.recyclerView)RecyclerView recyclerView;
    @Bind(R.id.progressBar)ProgressBar progressBar;
    @Bind(R.id.fab)FloatingActionButton fab;

    private Parser parser;
    private ArrayList<Object> list;
    private ForumAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        ButterKnife.bind(this);

        parser = new Parser(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String name = getIntent().getStringExtra("name");
        final String url = getIntent().getStringExtra("url");

        setTitle(name);

        constructForum(url);
    }

    private void constructForum(final String url) {
        showProgress();
        Subscriber<List<Object>> subscriber = new Subscriber<List<Object>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(List<Object> objects) {
                hideProgress();
                if (objects != null) {
                    adapter = new ForumAdapter(ForumActivity.this, list, recyclerView);
                    adapter.setOnItemClickListener(ForumActivity.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ForumActivity.this));
                    recyclerView.addItemDecoration(new SimpleDividerItemDecoration(ForumActivity.this));
                    recyclerView.setAdapter(adapter);
                }
            }
        };

        Observable<List<Object>> observable = Observable.create(new Observable.OnSubscribe<List<Object>>() {
            @Override
            public void call(Subscriber<? super List<Object>> subscriber) {
                try {
                    list = parser.getThreadList(url);
                    subscriber.onNext(list);
                } catch (IOException e) {
                    subscriber.onNext(null);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(subscriber);
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideProgress() {
        fab.show();
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        if(fab.getVisibility()==View.INVISIBLE || fab.getVisibility() == View.GONE) fab.show();
        super.onResume();
    }


    private void setTitle(String title) {
        if(getSupportActionBar()!=null) getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        fab.hide();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, Object object) {
        if(object instanceof Forum) {
            fab.hide();
            Intent intent = new Intent(ForumActivity.this, ForumActivity.class);
            intent.putExtra("name", ((Forum)object).getName());
            intent.putExtra("url" , ((Forum)object).getUrl());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
}
