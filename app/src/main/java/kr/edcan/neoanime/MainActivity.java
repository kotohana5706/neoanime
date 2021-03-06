package kr.edcan.neoanime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{


    Button next_page, prev_page;
    String url, s;
    Elements music, name, imgs;
    ArrayList<CData> arrayList;
    ListView main_ListView;
    int count, link_count, file_count, name_count, imgs_count, page;
    String[] music_file, music_link, music_name, music_imgs;
    Runnable asdf, task;
    private static Thread thread = null;
    ProgressDialog progressDialog;
    public static MaterialDialog listViewLoad, onClickLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listViewLoad= new MaterialDialog.Builder(MainActivity.this)
                .title("로드중입니다")
                .content("잠시만 기다려주세요")
                .progress(true,0)
                .show();
        next_page = (Button)findViewById(R.id.nextpage);
        prev_page = (Button)findViewById(R.id.prevpage);
        page = 1;
        next_page.setOnClickListener(this);
        prev_page.setOnClickListener(this);
        restData();
        loadMusicList();
    }

    public void restData(){
        music_file = new String[20];
        music_link = new String[20];
        music_name = new String[23];
        music_imgs = new String[23];
        count = file_count = imgs_count = link_count = name_count = 0;
    }
    public void loadMusicList() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                url = "http://www.neoanime.co.kr/index.php?mid=ost&page="+page;
                arrayList = new ArrayList<>();
                try {
                    Document doc = Jsoup.connect(url).get();
                    music = doc.select("li div a");
                    for (Element link : music) {
                        if (count % 2 == 0) {
                            music_file[file_count] = link.absUrl("href");
                            file_count += 1;
                        } else {
                            music_link[link_count] = link.absUrl("href");
                            link_count += 1;
                        }
                        count += 1;
                    }
                    name = doc.select("p>b");
                    for (name_count = 0; name_count < name.size(); name_count++) {
                        music_name[name_count] = name.get(name_count).text().toString();
                    }
                    imgs = doc.select("img.tmb[src]");
                    for (Element link : imgs) {
                        Log.e("imgs link", link.attr("abs:src"));
                        music_imgs[imgs_count] = link.attr("abs:src");
                        imgs_count+=1;
                    }
                    runOnUiThread(asdf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread = new Thread(task);
        thread.start();
        asdf = new Thread() {
            @Override
            public void run() {
                setData();
            }
        };
    }
    void setData() {
        main_ListView = (ListView) findViewById(R.id.main_listview);
        arrayList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            arrayList.add(new CData(getApplicationContext(), music_name[i]));
        }
        DataAdapter adapter = new DataAdapter(getApplicationContext(), arrayList);
        main_ListView.setAdapter(adapter);
        listViewLoad.dismiss();
        main_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onClickLoad = new MaterialDialog.Builder(MainActivity.this)
                        .title("로드중입니다")
                        .content("잠시만 기다려주세요")
                        .progress(true,0)
                        .show();
                startActivity(new Intent(getApplicationContext(), MusicLoadActivity.class)
                        .putExtra("Link", music_link[position])
                        .putExtra("ImgLink", music_imgs[position])
                        .putExtra("FileLink", music_file[position]));
            }
        });
    }

    public void Toast(String s, boolean isLong) {
        Toast.makeText(getApplicationContext(), s, (isLong == true) ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public void Toast(int s, boolean isLong) {
        Toast.makeText(getApplicationContext(), s + "", (isLong == true) ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.prevpage:
                restData();
                page--;
                loadMusicList();
                break;
            case R.id.nextpage:
                restData();
                page++;
                loadMusicList();
                break;
        }
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
