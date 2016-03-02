package button.anim.cn.buttonanimdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.list_view);

        List<String> itemList = getItemList();

        mListView.setAdapter(new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, itemList));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = null;
                if (position == 0) {
                    i = new Intent(MainActivity.this, TweenAnimActvity.class);
                    startActivity(i);
                } else if (position == 1) {
                    i = new Intent(MainActivity.this, ViewWrapperActivity.class);
                    startActivity(i);
                } else if (position == 2) {
                    i = new Intent(MainActivity.this, ValueAnimatorActivity.class);
                    startActivity(i);
                }
            }
        });
    }

    private List<String> getItemList() {
        List<String> itemList = new ArrayList<>(100);

        itemList.add("Tween Animation");
        itemList.add("View Wrapper");
        itemList.add("Value Animator");

        return itemList;
    }
}
