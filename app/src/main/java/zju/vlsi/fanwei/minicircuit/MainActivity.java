package zju.vlsi.fanwei.minicircuit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import zju.vlsi.fanwei.component.BitmapComponentIcon;
import zju.vlsi.fanwei.component.ComponentView;
import zju.vlsi.fanwei.component.DeleteIconEvent;
import zju.vlsi.fanwei.component.DrawableComponent;
import zju.vlsi.fanwei.component.FlipHorizontallyEvent;
import zju.vlsi.fanwei.component.FlipVerticallyEvent;
import zju.vlsi.fanwei.component.ZoomIconEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnTouchListener{

    private SystemInfo info = new SystemInfo();
    private SystemUtils utils = new SystemUtils();
    private ComponentView componentView;
    private AlertDialog.Builder selectorBuilder;
    private AlertDialog selectorAlertDialog;
    private String[] modelNames;
    private TypedArray modelImages;
    private GestureDetector gestureDetector;
    private GridBackground gridBackground;
    private float orignalX = 0,orignalY = 0;
    private float dx,dy;
    private List<String> type_1_pins_horizon,type_1_pins_vertical,type_2_pins_horizon,type_2_pins_vertical
            ,type_3_pins_2_left,type_3_pins_2_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //调试用，图标拖拽
        info = utils.getScreenSize(this);
        componentView = (ComponentView) findViewById(R.id.componentView);

        type_1_pins_horizon = Arrays.asList(getResources().getStringArray(R.array.type_1_pins_horizon));
        type_1_pins_vertical = Arrays.asList(getResources().getStringArray(R.array.type_1_pins_vertical));
        type_2_pins_horizon = Arrays.asList(getResources().getStringArray(R.array.type_2_pins_horizon));
        type_2_pins_vertical = Arrays.asList(getResources().getStringArray(R.array.type_2_pins_vertical));
        type_3_pins_2_left = Arrays.asList(getResources().getStringArray(R.array.type_3_pins_2_left));
        type_3_pins_2_right = Arrays.asList(getResources().getStringArray(R.array.type_3_pins_2_right));

        BitmapComponentIcon deleteIcon = new BitmapComponentIcon(ContextCompat.getDrawable(this,
                R.drawable.sticker_ic_close_white_18dp),
                BitmapComponentIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteIconEvent());

        BitmapComponentIcon zoomIcon = new BitmapComponentIcon(ContextCompat.getDrawable(this,
                R.drawable.sticker_ic_scale_white_18dp),
                BitmapComponentIcon.RIGHT_BOTTOM);
        zoomIcon.setIconEvent(new ZoomIconEvent());

        BitmapComponentIcon flipHorizontallyIcon = new BitmapComponentIcon(ContextCompat.getDrawable(this,
                R.drawable.sticker_ic_horizontallyflip_white_18dp),
                BitmapComponentIcon.RIGHT_TOP);
        flipHorizontallyIcon.setIconEvent(new FlipHorizontallyEvent());

        BitmapComponentIcon flipVerticallyIcon = new BitmapComponentIcon(ContextCompat.getDrawable(this,
                R.drawable.sticker_ic_verticallyflip_white_18dp),
                BitmapComponentIcon.LEFT_BOTTOM);
        flipVerticallyIcon.setIconEvent(new FlipVerticallyEvent());

        componentView.setIcons(Arrays.asList(deleteIcon,zoomIcon,flipHorizontallyIcon,flipVerticallyIcon));

        componentView.setBackgroundColor(Color.WHITE);
        componentView.setLocked(false);
        componentView.setConstrained(true);
        //外部写权限
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 110);
//        }
//        component = (ComponentView) findViewById(R.id.cap);
//        component.setScreenSize(info);
//        component.setImageResource(R.mipmap.symbol_capacitor);
        //调试用，双击出listview
//        LinearLayout content_main = (LinearLayout) findViewById(R.id.content_main);
//        content_main.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showModelSelector();
//            }
//        });
        gestureDetector = new GestureDetector(new LayoutSimpleGestureListener());
        gridBackground = (GridBackground) findViewById(R.id.gridBackground);
        gridBackground.setOnTouchListener(this);
        gridBackground.setClickable(true);

//        gridBackground = (GridBackground) findViewById(R.id.gridBackground);
//        gridBackground.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                float curX,curY;
//                switch (event.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        orignalX = event.getX();
//                        orignalY = event.getY();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        curX = event.getX();
//                        curY = event.getY();
//
//                }
//                return false;
//            }
//        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showModelSelector();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @SuppressWarnings("ResourceType")
    public ArrayList<ModelInformation> getModelInformation() {
        ArrayList<ModelInformation> modelInfo = new ArrayList<>();
        ModelInformation info;
        modelNames = getResources().getStringArray(R.array.model_name);
        modelImages = getResources().obtainTypedArray(R.array.model_image);
        for(int i=0;i<modelNames.length;i++){
            info = new ModelInformation(BitmapFactory.decodeResource(getResources(),modelImages.getResourceId(i,0)), modelNames[i]);
            modelInfo.add(info);
        }
        return modelInfo;
    }

    public void showModelSelector(){
        Context context = MainActivity.this;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View selectorLayout = inflater.inflate(R.layout.model_selector_list,null);
        final ListView selectorListView = (ListView) selectorLayout.findViewById(R.id.model_list);
        ArrayList<ModelInformation> modelInfo = getModelInformation();
        final ModelSelector modelSelector = new ModelSelector(context,modelInfo);
        selectorListView.setAdapter(modelSelector);

        selectorBuilder = new AlertDialog.Builder(context);
        selectorBuilder.setView(selectorLayout);
        selectorAlertDialog = selectorBuilder.create();
        selectorAlertDialog.show();

        selectorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bitmap bitmap = modelSelector.getItem(position).getModelBitmap();
                String componentName = modelSelector.getItem(position).getModelName();
                if(type_1_pins_vertical.contains(componentName)){
                    componentView.addComponent(new DrawableComponent(new BitmapDrawable(bitmap)), 0 ,0, 1);
                }
                else if(type_1_pins_horizon.contains(componentName)){
                    componentView.addComponent(new DrawableComponent(new BitmapDrawable(bitmap)), 0 ,0, 2);
                }
                else if(type_2_pins_vertical.contains(componentName)){
                    componentView.addComponent(new DrawableComponent(new BitmapDrawable(bitmap)), 0 ,0, 3);
                }
                else if(type_2_pins_horizon.contains(componentName)){
                    componentView.addComponent(new DrawableComponent(new BitmapDrawable(bitmap)), 0 ,0, 4);
                }
                else if(type_3_pins_2_left.contains(componentName)){
                    componentView.addComponent(new DrawableComponent(new BitmapDrawable(bitmap)), 0 ,0, 5);
                }
                else if(type_3_pins_2_right.contains(componentName)){
                    componentView.addComponent(new DrawableComponent(new BitmapDrawable(bitmap)), 0 ,0, 6);
                }
                else {
                    componentView.addComponent(new DrawableComponent(new BitmapDrawable(bitmap)), 0, 0, 0);
                }
                selectorAlertDialog.dismiss();
//                Toast.makeText(MainActivity.this,modelInfo.get(position).getModelName(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    private class LayoutSimpleGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            showModelSelector();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            orignalX = e.getX();
            orignalY = e.getY();
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            dx = orignalX - e2.getX();
            dy = orignalY - e2.getY();
            gridBackground.scrollBy((int)dx, (int)dy);
            componentView.onScrollGridBackground(dx, dy);
            orignalX = e2.getX();
            orignalY = e2.getY();

//            componentView.releaseIcons();
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v     The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
