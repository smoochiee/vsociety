package io.b1ackr0se.vsociety.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import io.b1ackr0se.vsociety.model.Forum;

public class Parser {

    public ArrayList<Forum> getForumList(String url) throws IOException{
            ArrayList<Forum> list = new ArrayList<>();
            Document document = Jsoup.connect(url).get();
            Elements tableForum = document.select("table.tborder").select("tr").select("td.alt1Active").select("div");
            for (Element e : tableForum) {
                String forumName = e.select("a[href]").select("strong").text();
                String forumUrl = e.select("a[href]").attr("abs:href");
                Forum forum = new Forum(forumName, forumUrl);
                list.add(forum);
            }
            return list;
    }
}
