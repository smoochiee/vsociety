package io.b1ackr0se.vsociety.jsoup;

import android.content.Context;
import android.content.SharedPreferences;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import io.b1ackr0se.vsociety.model.Forum;
import io.b1ackr0se.vsociety.model.User;
import io.b1ackr0se.vsociety.model.Thread;
import io.b1ackr0se.vsociety.util.Utils;

public class Parser {

    private Context context;
    private Map<String, String> cookies;

    @SuppressWarnings("unchecked")
    public Parser(Context c) {
        context = c;
        SharedPreferences pref = context.getSharedPreferences("cookies", Context.MODE_PRIVATE);
        cookies = (Map<String, String>) pref.getAll();
    }

    public String login(String username, String password) throws IOException {
        Connection.Response login = Jsoup.connect("https://vozforums.com/login.php")
                .data("do", "login")
                .data("vb_login_username", username)
                .data("vb_login_password", "")
                .data("vb_login_md5password", Utils.md5(password))
                .data("vb_login_md5password_utf", Utils.md5(password))
                .data("cookieuser", "1")
                .method(Connection.Method.POST)
                .execute();

        cookies = login.cookies();

        Document document = Jsoup.connect("https://vozforums.com/usercp.php")
                .cookies(cookies)
                .get();
        if (document.title().equals("vozForums - User Control Panel")) {
            SharedPreferences preferences = context.getSharedPreferences("cookies", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear().commit();

            for (String s : cookies.keySet()) {
                editor.putString(s, cookies.get(s));
            }
            editor.apply();
        }

        return document.title();
    }

    public User getLoggedInUser() {
        try {
            if (cookies != null) {
                Document document = Jsoup.connect("https://vozforums.com/usercp.php")
                        .cookies(cookies)
                        .get();
                User user = new User();
                if (document.title().equals("vozForums - User Control Panel")) {
                    Element element = document.select("table.tborder").select("tbody").select("td.alt2").select("div.smallfont").first();
                    user.setName(element.select("strong").select("a[href]").text());
                    user.setUrl(element.select("strong").select("a[href]").attr("abs:href"));
                }
                return user;
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public boolean isUserLoggedIn() {
        if (cookies == null || cookies.isEmpty()) return false;
        try {
            Document document = Jsoup.connect("https://vozforums.com/usercp.php")
                    .cookies(cookies)
                    .get();
            if (document.title().equals("vozForums - User Control Panel")) {
                return true;
            } else if (document.title().equals("vozForums")) {
                return false;
            } else {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public ArrayList<Forum> getForumList(String url) throws IOException {
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

    public ArrayList<Forum> getSubForumList(String url) throws IOException {
        ArrayList<Forum> list = new ArrayList<>();
        Document document = Jsoup.connect(url).cookies(cookies).get();
        Element subForumTable = document.select("table.tborder").get(1);
        if (!subForumTable.select("td.tcat").text().contains("Sub-Forums")) return null;
        else {
            Elements subForum = document.select("table.tborder").get(2).select("table");
            for (int i = 0; i < subForum.size() - 1; i++) {
                String subForumName = subForum.select("td.alt1Active").get(i).select("a[href]").text();
                System.out.println("Forum name: " + subForumName);
                String subForumUrl = subForum.select("td.alt1Active").get(i).select("a[href]").attr("abs:href");
                Forum forum = new Forum(subForumName, subForumUrl);
                list.add(forum);
            }
            return list;
        }
    }

    public ArrayList<Object> getThreadList(String url) throws IOException {
        ArrayList<Object> subForumList = new ArrayList<>();
        Document document;
        if (cookies != null)
            document = Jsoup.connect(url).cookies(cookies).get();
        else
            document = Jsoup.connect(url).get();

        //get subforum
        Element subForumTable = document.select("table.tborder").get(1);
        if (subForumTable.select("td.tcat").text().contains("Sub-Forums")) {
            Elements subForum = document.select("table.tborder").get(2).select("table");
            for (int i = 0; i < subForum.size() - 1; i++) {
                String subForumName = subForum.select("td.alt1Active").get(i).select("a[href]").text();
                String subForumUrl = subForum.select("td.alt1Active").get(i).select("a[href]").attr("abs:href");
                Forum forum = new Forum(subForumName, subForumUrl);
                subForumList.add(forum);
            }
        }

        ArrayList<Thread> list = new ArrayList<>();

        Elements titles = document.select("td.alt1 a[id^=thread_title]");
        for (Element e : titles) {
            Thread thread = new Thread();
            thread.setName(e.text());
            thread.setUrl(e.attr("abs:href"));
            list.add(thread);
        }

        Elements sticky = document.select("tbody[id^=threadbits_forum]").select("td.alt1[id^=td_threadtitle]").select("div").not("div.smallfont");

        for (int i = 0; i < list.size(); i++) {
            if (sticky.get(i).text().startsWith("Sticky:"))
                list.get(i).setSticky(true);
            else if (sticky.get(i).text().startsWith("Moved:")) {
                list.get(i).setLatestReply("Thread has been moved.");
                list.get(i).setSticky(false);
            } else list.get(i).setSticky(false);

            if (sticky.get(i).select("strong").text() != null && !sticky.get(i).select("strong").text().equals("")) {
                list.get(i).setName(sticky.get(i).select("strong").text() + " - " + list.get(i).getName());
            }
        }


        Elements author = document.select("td.alt1[id^=td_threadtitle]").select("div.smallfont").select("span[onclick]");

        for (int i = 0; i < list.size(); i++) {
            list.get(i).setStarter(author.get(i).text());
        }

        Elements latest = document.select("tbody[id^=threadbits_forum]").select("td.alt2").select("div.smallfont");

        Iterator<Element> iterator = latest.iterator();
        while (iterator.hasNext()) {
            String s = iterator.next().text();
            if(s.startsWith("Thread deleted by") || s.startsWith("Reason:"))
                iterator.remove();
        }

        for (int i = 0; i < list.size(); i++) {
            if (i<latest.size())
                list.get(i).setLatestReply(latest.get(i).text());
        }

        Elements replies = document.select("tbody[id^=threadbits_forum]").select("td.alt1").not("td.alt1[id^=td_threadtitle]").select("a[onclick]");

        for (int i = 0; i < list.size(); i++) {
            if (i<replies.size())
                list.get(i).setNoOfReplies(replies.get(i).text());
        }

        subForumList.addAll(list);

        return subForumList;
    }
}
