package code;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.anastr.speedviewlib.AwesomeSpeedometer;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.marcinorlowski.fonty.Fonty;
import com.material.components.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import code.adapter.AdapterDataFeed;
import code.adapter.AdapterImage;
import code.connection.API;
import code.connection.RestAdapter;
import code.data.SharedPref;
import code.model.DataFeedItem;
import code.model.JsonDataFeedModel;
import code.room.AppDatabase;
import code.room.DAO;
import code.utils.Tools;
import it.beppi.tristatetogglebutton_library.TriStateToggleButton;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityMainMenu extends AppCompatActivity {


    public static boolean active = false;
    private SharedPref sharedPref;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private Integer news_page = 20;
    private int notification_count = -1;
    private DAO dao;
    private TriStateToggleButton btnToggle1;
    private TriStateToggleButton btnToggle2;
    private AwesomeSpeedometer meterTemper;
    private AwesomeSpeedometer meterHumidity;
    private LineChart temperChart;
    private TextView txtMessage;
    private ImageView imgView;
    private MqttHelper mqttHelper;
    private TextView tvHour, tvDate, tvTemp, tvHum, tvTempTime, tvHumTime;
    private RecyclerView rvDataFeed, rvImage;
    private AdapterDataFeed adapterDataFeed;
    private AdapterImage adapterImage;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);
        sharedPref = new SharedPref(this);
        dao = AppDatabase.getDb(this).getDAO();

        initToolbar();
        initComponent();
        subscribeMQTTTopics();

        initDateTime();
        initLog();
        initImage();

        getFeed(MqttHelper.feed_classroom_temperature);
        getFeed(MqttHelper.feed_classroom_humidity);

        Fonty.setFonts(this);
    }

    private void getFeed(String feedKey) {
        String ioKey = MqttHelper.secretKey;
        String username = MqttHelper.clientId;
        API apiService = RestAdapter.createApiService();
        Call<JsonDataFeedModel.Root> call = apiService.getFeed(ioKey, username, feedKey);
        call.enqueue(new Callback<JsonDataFeedModel.Root>() {
            @Override
            public void onResponse(Call<JsonDataFeedModel.Root> call, Response<JsonDataFeedModel.Root> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String lastValue = response.body().details.data.last.value;
                    String lastUpdateTime = response.body().details.data.last.updated_at;

                    updateUI(feedKey, lastValue, lastUpdateTime);
                } else {
                    // Handle error response
                }
            }

            @Override
            public void onFailure(Call<JsonDataFeedModel.Root> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void updateUI(String feedKey, String lastValue, String lastUpdateTime) {
        if (feedKey.equals(MqttHelper.feed_classroom_temperature)) {
            // update
        } else if (feedKey.equals(MqttHelper.feed_classroom_humidity)) {
            // update
        }
    }

    private void initImage() {
        rvImage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterImage = new AdapterImage();
        rvImage.setAdapter(adapterImage);
    }

    private void initLog() {
        rvDataFeed.setLayoutManager(new LinearLayoutManager(this));
        adapterDataFeed = new AdapterDataFeed();
        rvDataFeed.setAdapter(adapterDataFeed);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                List<String> fakeDataValue = new ArrayList<>();
                fakeDataValue.add("unwear mask");
                fakeDataValue.add("wear mask");
                fakeDataValue.add("0");
                fakeDataValue.add("1");

                List<String> fakeDataFeedId = new ArrayList<>();
                fakeDataFeedId.add("fan_state");
                fakeDataFeedId.add("led_state");
                fakeDataFeedId.add("alarm_state");
                fakeDataFeedId.add("message");

                Random random = new Random();
                int index = random.nextInt(4);

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy:", new Locale("EN"));
                DataFeedItem dataFeedItem = new DataFeedItem(timeFormat.format(new Date()), fakeDataValue.get(index), fakeDataFeedId.get(index));
                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        adapterDataFeed.addData(dataFeedItem);
                        rvDataFeed.scrollToPosition(adapterDataFeed.getItemCount() - 1);
                    }
                });
            }
        }, 3000, 3000);
    }

    private void initDateTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", new Locale("EN"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM yyyy", new Locale("EN"));

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String currentTime = timeFormat.format(new Date());
                tvHour.setText(currentTime);

                String currentDate = dateFormat.format(new Date());
                tvDate.setText(currentDate);

                handler.postDelayed(this, 1000);
            }
        };

// Bắt đầu cập nhật TextView
        handler.post(runnable);
    }

    private void initToolbar() {
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(false);
//        actionBar.setHomeButtonEnabled(false);

        Tools.setSystemBarColorInt(this, getResources().getColor(R.color.darkHomeDark));
    }

    private void initComponent() {
        rvImage = findViewById(R.id.rvImage);
        rvDataFeed = findViewById(R.id.rvLog);
        tvHour = findViewById(R.id.tvHour);
        tvDate = findViewById(R.id.tvDate);
        tvTemp = findViewById(R.id.tvTemp);
        tvHum = findViewById(R.id.tvHum);
        tvTempTime = findViewById(R.id.tvTimeTemp);
        tvHumTime = findViewById(R.id.tvTimeHum);
        btnToggle1 = findViewById(R.id._id_btnToggle1);
        btnToggle2 = findViewById(R.id._id_btnToggle2);

        btnToggle1.setOnToggleChanged(new TriStateToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(TriStateToggleButton.ToggleStatus toggleStatus, boolean booleanToggleStatus, int toggleIntValue) {
                int status = toggleStatus == TriStateToggleButton.ToggleStatus.on ? 1 : 0;
                Log.i(ActivityMainMenu.class.getName(), "btnToggle1 setOnToggleChanged " + " ==> " + toggleIntValue + ":" + status);
                mqttHelper.pushDataToTopic(MqttHelper.feed_btn_stage, status + "");
            }
        });

        btnToggle2.setOnToggleChanged(new TriStateToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(TriStateToggleButton.ToggleStatus toggleStatus, boolean booleanToggleStatus, int toggleIntValue) {
                int status = toggleStatus == TriStateToggleButton.ToggleStatus.on ? 1 : 0;
                Log.i(ActivityMainMenu.class.getName(), "btnToggle1 setOnToggleChanged " + " ==> " + toggleIntValue + ":" + status);
                mqttHelper.pushDataToTopic(MqttHelper.feed_btn_reset, status + "");
            }
        });

        meterTemper = findViewById(R.id._id_gauge_temper);
        meterHumidity = findViewById(R.id._id_gauge_humidity);
        temperChart = findViewById(R.id._id_chart);

        imgView = findViewById(R.id._id_image);
        txtMessage = findViewById(R.id._id_image_desc);

        //temperChart.setOnChartValueSelectedListener(this);
        // no description text
        temperChart.getDescription().setEnabled(false);
        // enable touch gestures
        temperChart.setTouchEnabled(true);
        temperChart.setDragDecelerationFrictionCoef(0.9f);
        // enable scaling and dragging
        temperChart.setDragEnabled(true);
        temperChart.setScaleEnabled(true);
        temperChart.setDrawGridBackground(false);
        temperChart.setHighlightPerDragEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        temperChart.setPinchZoom(true);
        // set an alternative background color
        temperChart.setBackgroundColor(Color.TRANSPARENT);
        temperChart.setBorderColor(Color.TRANSPARENT);
        temperChart.animateX(1500);

        setData(10, 140.0f);
        setMeterHumidityValue(63.2f);
        setMeterTemper(24f);
    }

    private void subscribeMQTTTopics() {
        mqttHelper = new MqttHelper(this, new MqttCallback() {
            @Override
            public void onMessage(String topic, String message) {
                Log.i(ActivityMainMenu.class.getName(), topic + " ==> " + message);
                String feedId = topic.replace(MqttHelper.clientId, "").toString().trim().toLowerCase();
                Log.i(ActivityMainMenu.class.getName(), feedId + " ==> " + message);
                TriStateToggleButton.ToggleStatus status;
                if (MqttHelper.feed_btn_reset.toLowerCase().equals(feedId)) {
                    try {

                        btnToggle1.setToggleStatus("0".equals(message) == false);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    return;
                }

                if (MqttHelper.feed_btn_stage.toLowerCase().equals(feedId)) {
                    try {
                        status = Boolean.parseBoolean(message) ? TriStateToggleButton.ToggleStatus.on : TriStateToggleButton.ToggleStatus.off;
                        btnToggle2.setToggleStatus("0".equals(message) == false);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return;
                }

                if (MqttHelper.feed_imask.toLowerCase().equals(feedId)) {
                    try {

//                        byte[] decodedString = Base64.decode(message, Base64.DEFAULT);
                        // Convert the byte array to a Bitmap
//                        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                        imgView.setImageBitmap(decodedBitmap);


                        runOnUiThread(new TimerTask() {
                            @Override
                            public void run() {
                                adapterImage.addImage(message);
                                rvImage.scrollToPosition(0);
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return;
                }

                if (MqttHelper.feed_message.toLowerCase().equals(feedId)) {
                    txtMessage.setText(message);
                    return;
                }

                if (MqttHelper.feed_classroom_humidity.toLowerCase().equals(feedId)) {
                    try {
                        meterHumidity.speedTo(Float.parseFloat(message));
                        tvHum.setText(message);
//                        tvHumTime.setText();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return;
                }

                if (MqttHelper.feed_classroom_temperature.toLowerCase().equals(feedId)) {
                    try {
                        meterTemper.speedTo(Float.parseFloat(message));
                        tvTemp.setText(message);
//                        tvTempTime.setText();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return;
                }
            }

            @Override
            public void onConnected(Boolean status, String address) {

            }

            @Override
            public void onDisconnected(Throwable error) {

            }
        });

        mqttHelper.setupMQTT();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Tools.changeMenuIconColor(menu, Color.WHITE);
        Tools.changeOverflowMenuIconColor(toolbar, Color.WHITE);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == R.id.action_notifications) {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
/*        int new_notif_count = dao.getNotificationUnreadCount();
        if (new_notif_count != notification_count) {
            notification_count = new_notif_count;
            invalidateOptionsMenu();
        }*/
        int uiOptions =
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        if (getWindow().getDecorView().getSystemUiVisibility() != uiOptions) {
            getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        doExitApp();
    }

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "Press again to exit app", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setMeterTemper(float value) {
        meterTemper.speedTo(value);
    }

    private void setMeterHumidityValue(float value) {
        meterHumidity.speedTo(value);
    }

    private void setData(int count, float range) {

        ArrayList<Entry> values1 = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * (range / 2f)) + 50;
            values1.add(new Entry(i, val));
        }

        ArrayList<Entry> values2 = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) + 450;
            values2.add(new Entry(i, val));
        }

        ArrayList<Entry> values3 = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) + 500;
            values3.add(new Entry(i, val));
        }

        LineDataSet set1, set2, set3;
        LineChart chart = temperChart;
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set2 = (LineDataSet) chart.getData().getDataSetByIndex(1);
            set3 = (LineDataSet) chart.getData().getDataSetByIndex(2);
            set1.setValues(values1);
            set2.setValues(values2);
            set3.setValues(values3);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values1, "DataSet 1");

            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(ColorTemplate.getHoloBlue());
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setFillAlpha(65);
            set1.setFillColor(ColorTemplate.getHoloBlue());
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(false);
            //set1.setFillFormatter(new MyFillFormatter(0f));
            //set1.setDrawHorizontalHighlightIndicator(false);
            //set1.setVisible(false);
            //set1.setCircleHoleColor(Color.WHITE);

            // create a dataset and give it a type
            set2 = new LineDataSet(values2, "DataSet 2");
            set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set2.setColor(Color.RED);
            set2.setCircleColor(Color.WHITE);
            set2.setLineWidth(2f);
            set2.setCircleRadius(3f);
            set2.setFillAlpha(65);
            set2.setFillColor(Color.RED);
            set2.setDrawCircleHole(false);
            set2.setHighLightColor(Color.rgb(244, 117, 117));
            //set2.setFillFormatter(new MyFillFormatter(900f));

            set3 = new LineDataSet(values3, "DataSet 3");
            set3.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set3.setColor(Color.YELLOW);
            set3.setCircleColor(Color.WHITE);
            set3.setLineWidth(2f);
            set3.setCircleRadius(3f);
            set3.setFillAlpha(65);
            set3.setFillColor(ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
            set3.setDrawCircleHole(false);
            set3.setHighLightColor(Color.rgb(244, 117, 117));

            // create a data object with the data sets
            LineData data = new LineData(set1, set2, set3);
            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(9f);

            // set data
            chart.setData(data);
        }
    }
}
