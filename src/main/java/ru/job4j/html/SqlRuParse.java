package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.Parse;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {

    private final DateTimeParser dateTimeParser;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public static void main(String[] args) throws IOException {
        String url = "https://www.sql.ru/forum/job-offers";
        for (int i = 1; i <= 5; i++) {
            Document doc = Jsoup.connect(String.format("%s/%s", url, i)).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                System.out.println(td.parent().child(5).text());
            }
        }
    }

    @Override
    public Post detail(String link) {
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String description = doc.select(".msgBody").get(1).text();
        String title = doc.select(".messageHeader").get(0).ownText();
        String createdText = doc.select(".msgFooter").get(0).text();
        createdText = createdText.substring(0, createdText.indexOf("[")).trim();
        LocalDateTime created = dateTimeParser.parse(createdText);
        return new Post(title, link, description, created);
    }

    @Override
    public List<Post> list(String link) {
        List<Post> rsl = new ArrayList<>();
        Document doc = null;
        for (int i = 1; i <= 5; i++) {
            try {
                doc = Jsoup.connect(String.format("%s/%s", link, i)).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements row = doc.select(".postslisttopic");
            for (Element element : row) {
                Post post = detail(element.child(0).attr("href"));
                if (post.getTitle().toLowerCase().contains("java")
                        && !post.getTitle().toLowerCase().contains("javascript")) {
                    rsl.add(post);
                }
            }
        }
        return rsl;
    }

}
