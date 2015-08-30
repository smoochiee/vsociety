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
import java.util.Map;

import io.b1ackr0se.vsociety.model.Forum;
import io.b1ackr0se.vsociety.model.User;
import io.b1ackr0se.vsociety.util.Utils;

public class Parser {

    private Context context;
    private Map<String, String> cookies;

    @SuppressWarnings("unchecked")
    public Parser(Context c) {
        context = c;
        SharedPreferences pref= context.getSharedPreferences("cookies", Context.MODE_PRIVATE);
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

        System.out.println(Utils.md5("123456"));

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
        if(cookies==null || cookies.isEmpty()) return false;
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
        Document document = Jsoup.connect(url).get();
        Element subForumTable = document.select("table.tborder").get(1);
        if(!subForumTable.select("td.tcat").text().contains("Sub-Forums")) return null;
        else {
            Elements subForum = document.select("table.tborder").get(2).select("table");
            for (int i = 0; i < subForum.size()-1; i++) {
                String subForumName = subForum.select("td.alt1Active").get(i).select("a[href]").text();
                System.out.println("Forum name: " +subForumName);
                String subForumUrl = subForum.select("td.alt1Active").get(i).select("a[href]").attr("abs:href");
                Forum forum = new Forum(subForumName, subForumUrl);
                list.add(forum);
            }
            return list;
        }
    }


}
