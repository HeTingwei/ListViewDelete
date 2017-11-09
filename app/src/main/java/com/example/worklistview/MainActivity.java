package com.example.worklistview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
* listView子项在有CheckBox时会出现问题，无法点击，
* 获取焦点
* */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //布局对象
    private ListView listview;
    private Button deleteBt;
    private Button cancelBt;
    private CheckBox selectAllCheckbox;
    private RelativeLayout relativeLayout;

    private boolean isSelecting = false;//是否正在选择
    private List<Fruit> fruitList = new ArrayList<>();
    private FruitAdapter adapter;
    private List<Integer> deleteList = new ArrayList<>();//存储将要删除的子项们的位置
    boolean selectAll = false;//选择全部

    int choose = -1;//第一次长按的位置，让它被选定
    boolean isClosing = false;//关闭选择的一瞬间


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initView() {
        //获取布局控件对象
        deleteBt = (Button) findViewById(R.id.delete_bt);
        cancelBt = (Button) findViewById(R.id.cancel_bt);
        selectAllCheckbox = (CheckBox) findViewById(R.id.select_all_checkbox);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
//listView相关逻辑代码：
        listview = (ListView) findViewById(R.id.listview);
        adapter = new FruitAdapter(this, R.layout.item_layout, fruitList);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (isSelecting) {
                    CheckBox checkBox = view.findViewById(R.id.item_checkbox);
                    checkBox.setChecked(!checkBox.isChecked());
                    if (checkBox.isChecked() && !deleteList.contains(new Integer(position))) {
                        deleteList.add(new Integer(position));
                    } else if (!checkBox.isChecked() && deleteList.contains(new Integer(position))) {
                        for (int i = 0; i < deleteList.size(); i++) {
                            if (deleteList.get(i).equals(new Integer(position))) {
                                deleteList.remove(i);
                            }
                        }
                    }

                } else {
                    //没有选择删除时的代码逻辑
                }
            }
        });


        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (isSelecting) {
                    CheckBox checkBox = view.findViewById(R.id.item_checkbox);
                    checkBox.setChecked(!checkBox.isChecked());
                    if (checkBox.isChecked() && !deleteList.contains(new Integer(position))) {
                        deleteList.add(new Integer(position));
                    } else if (!checkBox.isChecked() && deleteList.contains(new Integer(position))) {
                        for (int i = 0; i < deleteList.size(); i++) {
                            if (deleteList.get(i).equals(new Integer(position))) {
                                deleteList.remove(i);
                            }
                        }
                    }
                } else {
                    choose = position;
                    deleteList.add(new Integer(position));
                    isSelecting = true;
                    adapter.notifyDataSetInvalidated();
                    relativeLayout.setVisibility(View.VISIBLE);
                }

                return true;
            }
        });

        //按钮：取消选择
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

//按钮：删除选择项
        deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.reverse(deleteList);
                for (int i = 0; i < deleteList.size(); i++) {
                    Log.d(TAG, "delete: " + deleteList.get(i));
                }
                for (int i = 0; i < deleteList.size(); i++) {
                    fruitList.remove(deleteList.get(i).intValue());
                }
                isClosing = true;
                adapter.notifyDataSetInvalidated();
                relativeLayout.setVisibility(View.GONE);
                isSelecting = false;
                deleteList.clear();
            }
        });

        //checkBox：全选
        selectAllCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    deleteList.clear();
                    for (int i = 0; i < fruitList.size(); i++) {
                        deleteList.add(new Integer(i));
                    }
                    selectAll = true;
                    adapter.notifyDataSetInvalidated();
                } else {
                    deleteList.clear();
                    selectAll = false;
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {//点击的是返回键
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0&&isSelecting) {
                cancel();
               return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    //点击取消或返回键的处理逻辑
    private void cancel(){
        selectAll = false;
        selectAllCheckbox.setChecked(false);
        relativeLayout.setVisibility(View.GONE);
        deleteList.clear();
        isClosing = true;
        adapter.notifyDataSetInvalidated();
        isSelecting = false;
    }

    //listView的适配器
    class FruitAdapter extends ArrayAdapter<Fruit> {
        private int resourceId;

        public FruitAdapter(Context context, int textViewResourceId,
                            List<Fruit> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Fruit fruit = getItem(position); // 获取当前项的Fruit实例
            View view;
            ViewHolder viewHolder;
            if (isClosing || convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                //引入ViewHolder提升ListView的效率
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) view.findViewById(R.id.fruit_image);
                viewHolder.textView = (TextView) view.findViewById(R.id.fruit_name);
                viewHolder.checkBox = (CheckBox) view.findViewById(R.id.item_checkbox);
                view.setTag(viewHolder);
                if (position == fruitList.size()) {
                    isClosing = false;//最后一项加载完成后变为false
                }
            } else {
                view = convertView;
                viewHolder = (ViewHolder)view.getTag();
            }


            viewHolder.imageView.setImageResource(fruit.getImageId());
            viewHolder.textView .setText(fruit.getName());

            if (isSelecting) {
                viewHolder.checkBox.setVisibility(View.VISIBLE);
            } else {
                viewHolder.checkBox.setVisibility(View.GONE);

            }
            if (selectAll)
                viewHolder.checkBox.setChecked(true);
            if (choose > -1 && position == choose) {
                viewHolder.checkBox.setChecked(true);
                choose = -1;
            }
            return view;
        }
    }

    class ViewHolder {
        TextView textView;
        ImageView imageView;
        CheckBox checkBox;
    }

    //初始化
    private void initData() {
        for (int i = 0; i < 25; i++) {
            fruitList.add(new Fruit("apple" + i, R.drawable.apple));
        }
    }


}
