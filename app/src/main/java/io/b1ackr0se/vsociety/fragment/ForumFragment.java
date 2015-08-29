package io.b1ackr0se.vsociety.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.vsociety.R;
import io.b1ackr0se.vsociety.adapter.ForumAdapter;
import io.b1ackr0se.vsociety.adapter.SimpleSectionedRecyclerViewAdapter;
import io.b1ackr0se.vsociety.jsoup.Parser;
import io.b1ackr0se.vsociety.model.Forum;
import io.b1ackr0se.vsociety.widget.SimpleDividerItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForumFragment extends Fragment {

    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.progressBar) ProgressBar progressBar;

    private Context context;
    private ArrayList<Forum> forumList;
    private final String BASE_URL = "https://vozforums.com/index.php";

    public ForumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        ButterKnife.bind(this, view);

        context = getActivity().getApplicationContext();

        initialiseForumList();

        return view;
    }

    private void initialiseForumList() {
        new GetForumListTask().execute();
    }


    public class GetForumListTask extends AsyncTask<Void, Void, ArrayList<Forum>> {

        @Override
        public void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        @Override
        protected ArrayList<Forum> doInBackground(Void... voids) {
            Parser parser = new Parser();
            try {
                return parser.getForumList(BASE_URL);
            }catch (IOException e) {
                return null;
            }
        }

        @Override
        public void onPostExecute(ArrayList<Forum> result) {
            progressBar.setVisibility(View.GONE);
            if(result!=null) {
                recyclerView.setVisibility(View.VISIBLE);
                forumList = result;
                setUpRecyclerView();
            } else {
                Toast.makeText(context, "Failed to retrieve", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setUpRecyclerView(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        ForumAdapter adapter = new ForumAdapter(forumList);


        List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Đại sảnh"));
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(4, "Máy tính để bàn"));
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(14, "Sản phẩm công nghệ"));
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(20, "Online Events"));
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(25, "Giao lưu Doanh nghiệp & Người dùng"));
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(38, "Khu vui chơi giải trí"));
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(41, "Khu thương mại - Mua và Bán"));

        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        SimpleSectionedRecyclerViewAdapter sectionedAdapter = new
                SimpleSectionedRecyclerViewAdapter(context, R.layout.section_item, R.id.sectionName, adapter);
        sectionedAdapter.setSections(sections.toArray(dummy));
        recyclerView.setAdapter(sectionedAdapter);
    }
}
