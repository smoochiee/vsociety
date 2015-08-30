package io.b1ackr0se.vsociety.activity;

import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.vsociety.R;
import io.b1ackr0se.vsociety.jsoup.Parser;
import io.b1ackr0se.vsociety.model.Forum;

public class ForumActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.recyclerView)RecyclerView recyclerView;
    @Bind(R.id.progressBar)ProgressBar progressBar;
    @Bind(R.id.fab)FloatingActionButton fab;

    private Parser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        ButterKnife.bind(this);

        parser = new Parser(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String name = getIntent().getStringExtra("name");
        String url = getIntent().getStringExtra("url");

        setTitle(name);

        new GetSubForumList().execute(url);

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

    public class GetSubForumList extends AsyncTask<String, Void, ArrayList<Forum>> {

        @Override
        public void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        @Override
        protected ArrayList<Forum> doInBackground(String... strings) {
            try {
                return parser.getSubForumList(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void onPostExecute(ArrayList<Forum> result) {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            fab.show();
            if(result!=null) {
                System.out.println("Forum size: " + result.size());
                Toast.makeText(ForumActivity.this, "Successfully get subforum", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(ForumActivity.this, "Failed to get subforum", Toast.LENGTH_SHORT).show();
        }
    }
}
