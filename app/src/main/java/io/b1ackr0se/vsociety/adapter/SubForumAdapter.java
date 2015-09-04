package io.b1ackr0se.vsociety.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import io.b1ackr0se.vsociety.R;
import io.b1ackr0se.vsociety.model.Forum;

public class SubForumAdapter extends ArrayAdapter<Forum> {
    private Context context;
    private ArrayList<Forum> subForumList;

    public SubForumAdapter(Context c, int resId, ArrayList<Forum> list) {
        super(c, resId);
        context = c;
        subForumList = list;
    }

    @Override
    public int getCount() {
        return subForumList.size();
    }

    @Override
    public Forum getItem(int position) {
        return subForumList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup container) {
        final Forum forum = getItem(position);
        LayoutInflater vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = (convertView == null)
                ? vi.inflate(R.layout.item_subforum, container, false)
                : convertView;
        TextView subForumName = (TextView) view.findViewById(R.id.subForumName);
        if(forum.getName()!=null) subForumName.setText(forum.getName());
        return view;
    }

}
