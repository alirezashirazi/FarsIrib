package ir.farsirib.utils;


import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import android.os.AsyncTask;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class Xml {
    public String  html ;
    public String  Html ;
    public String[] date_array={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    public List<Rss> parser() {

        for(int cou=10; cou<13 ; cou++){

        }


        List<Rss> data=new ArrayList<>();

        int checker_img=0;
        int j;

        URL url = null;
        try {
            url = new URL("http://www.iribnews.ir/fa/rss/27");
        }
        catch(Exception e) {
            Log.e("Xml", "error url");
        }
        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = null;
            doc = db.parse(new InputSource(url.openStream()));

            NodeList nl = doc.getElementsByTagName("item");

            for (int i = 0; i <20; i++) {
                NodeList cur = nl.item(i).getChildNodes();
                Rss rss = new Rss();
                checker_img=0;
                for ( j = 1; j <cur.getLength(); j++) {

                    if (cur.item(j).getNodeName().equals("title")) {
                        rss.setTitle(cur.item(j).getFirstChild().getNodeValue());


                    }
                    if (cur.item(j).getNodeName().equals("pubDate")) {
                        String message = cur.item(j).getFirstChild().getNodeValue();
                        String[] substrings = message.split(" ");
                        for(int fff=0;fff<date_array.length;fff++){
                            Log.e("dddddddddddfdgdfgdfg",substrings[0]+"-"+fff+1+"-"+substrings[2] );

                            if(substrings[1].equals(date_array[fff])){
                                CalendarTool ct = new CalendarTool(substrings[2],(fff+1),substrings[0]);
                                rss.setDate(ct.getIranianDate()+" "+"ساعت:"+substrings[3]);
                            }
                        }
                    }

                    else if (cur.item(j).getNodeName().equals("link")) {
                        rss.setLink(cur.item(j).getFirstChild().getNodeValue());
                    }
                    else if (cur.item(j).getNodeName().equals("enclosure")) {

                        String Img=cur.item(j).getAttributes().item(0).getTextContent();
                        rss.setImg(cur.item(j).getAttributes().item(0).getTextContent());

                        checker_img++;
                    }
                }
                if( checker_img==0)
                    rss.setImg("http://www.iribnews.ir/client/themes/fa/main/img/logo.png");

                data.add(rss);
                // Log.e("ddddddddddddddddddd", "kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
            }
        }
        catch (Exception e) {
            Log.e("Xml", "xml errorrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
        }

        return data;

    }

    public class GetHTml extends AsyncTask<String, Integer, Void> {

        public GetHTml(String nodeValue) {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... urls) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);



            try {
                org.jsoup.nodes.Document gggggggggggt = Jsoup.connect(String.valueOf(urls)).get();
                Elements metaElems = gggggggggggt.select("html > body#news > div.home_logo > div.container > div.page > div.row > div.col-md-25.col-sm-36.col_padd_l.news_general_dl > div > div.body.body_media_content_show");
                html = metaElems.html();


            } catch (Exception e) {
                Log.e("RssReader", "no internet");

            }


            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {

            super.onProgressUpdate(values);




        }


        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Html=html;

        }
    }
}

