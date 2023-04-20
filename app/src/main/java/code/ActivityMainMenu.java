package code;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.gson.Gson;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.marcinorlowski.fonty.Fonty;
import com.material.components.BuildConfig;
import com.material.components.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
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

public class ActivityMainMenu extends AppCompatActivity implements TextToSpeech.OnInitListener, SerialInputOutputManager.Listener{


    public static boolean active = false;
    private SharedPref sharedPref;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private Integer news_page = 20;
    private int notification_count = -1;
    private DAO dao;
    private TriStateToggleButton _btn_FanControl;
    private TriStateToggleButton _btn_LedControl;
    private TriStateToggleButton _btn_AlarmControl;
    private AwesomeSpeedometer meterTemper;
    private AwesomeSpeedometer meterHumidity;
    private LineChart temperChart;
    private TextView txtMessage;
    private ImageView imgView;
    private MqttHelper mqttHelper;
    private TextView tvHour, tvDate, tvTemp, tvHum, tvTempTime, tvHumTime;
    private Button _btnReset;
    private RecyclerView rvDataFeed, rvImage;
    private AdapterDataFeed adapterDataFeed;
    private AdapterImage adapterImage;
    private long exitTime = 0;
    private static int ResetState = 0;
    private static boolean isLoaded = false;

    private static final String ACTION_USB_PERMISSION = "com.android.recipes.USB_PERMISSION";
    private static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
    private int DATA_CHECKING = 0;
    private TextToSpeech niceTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);
        sharedPref = new SharedPref(this);
        dao = AppDatabase.getDb(this).getDAO();

        try {
            initToolbar();
            initComponent();
            subscribeMQTTTopics();
            initTextVoice();
            initDateTime();
            initLog();
            initImage();

            Fonty.setFonts(this);
        }catch (Exception ex) {
            Log.e(TAG, "onCreate: ", ex);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        try {
            if(isLoaded == false) {
                getFeed(MqttHelper.feed_temperature_meter);
                getFeed(MqttHelper.feed_humidity_meter);
                getFeed(MqttHelper.feed_image);

                isLoaded = true;
            }

        }
        catch (Exception e) {}

        return super.onCreateView(name, context, attrs);
    }

    public void talkToMe(String sentence) {
        String speakWords = sentence;
        niceTTS.speak(speakWords, TextToSpeech.QUEUE_FLUSH, null);

    }
        private void initTextVoice() {
        Intent checkData = new Intent();
        //set it up to check for tts data
        checkData.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        //start it so that it returns the result
        startActivityForResult(checkData, DATA_CHECKING);
    }

    private void getFeed(String feedKey) {
        String username = MqttHelper.clientId;
        API apiService = RestAdapter.createApiService();
        Call<ResponseBody> call = apiService.getFeed(username, feedKey, MqttHelper.secretKey);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    assert response.body() != null;

                    try {
                        String json = response.body().string();
                        Log.i(TAG, "onResponse: " + json);
                        Gson gson = new Gson();
                        JsonDataFeedModel.Root root = gson.fromJson(json, JsonDataFeedModel.Root.class);
                        // do something with the feed object
                        String lastValue = root.last_value;
                        String lastUpdateTime = root.created_at;

                        updateUI(feedKey, lastValue, Tools.parseDateTimeFromMQTTServer(lastUpdateTime));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    // Handle error response
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                Log.e("error", t.getMessage());
            }
        });
    }

    private void updateUI(String feedKey, String lastValue, String lastUpdateTime) {
        if (feedKey.equals(MqttHelper.feed_temperature_meter)) {
            // update
            tvTemp.setText(lastValue);
            tvTempTime.setText(lastUpdateTime);

            meterTemper.speedTo(Float.parseFloat(lastValue));

            return;
        }

        if (feedKey.equals(MqttHelper.feed_humidity_meter)) {
            // update
            tvHum.setText(String.valueOf(Math.round(Float.parseFloat(lastValue))));
            tvHumTime.setText(lastUpdateTime);

            meterHumidity.speedTo(Float.parseFloat(lastValue));
            return;
        }

        if (feedKey.equals(MqttHelper.feed_image)) {
            // update
            if(lastValue.length() > 0) {
                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            adapterImage.addImage(lastValue);
                            rvImage.scrollToPosition(0);
                            adapterImage.notifyDataSetChanged();
                        }
                        catch (Exception e) {
                            Log.e(TAG, "run: set Image", e);
                        }

                    }
                });
            }

            return;
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
    }

    private void addRecordToLog(String value, String feedId) {
        runOnUiThread(new TimerTask() {
            @Override
            public void run() {
                String data = value;
               if(MqttHelper.feed_image.equals(feedId)) {
                   data = "<< Base64 Image >>";
                }
                DataFeedItem dataFeedItem = new DataFeedItem(Tools.parseCurrentDateTime(), data, feedId);
                adapterDataFeed.addData(dataFeedItem);
                rvDataFeed.scrollToPosition(adapterDataFeed.getItemCount() - 1);
            }
        });
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
        _btn_AlarmControl = findViewById(R.id._id_alarm_state);
        _btn_LedControl = findViewById(R.id._id_led_state);
        _btn_FanControl = findViewById(R.id._id_led_state);
        _btnReset = findViewById(R.id._id_btnReset);

        _btn_FanControl.setOnToggleChanged(new TriStateToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(TriStateToggleButton.ToggleStatus toggleStatus, boolean booleanToggleStatus, int toggleIntValue) {
                int status = toggleStatus == TriStateToggleButton.ToggleStatus.on ? 1 : 0;
                Log.i(ActivityMainMenu.class.getName(), "_btn_FanControl setOnToggleChanged " + " ==> " + toggleIntValue + ":" + status);
                mqttHelper.pushDataToTopic(MqttHelper.feed_fan_state, status + "");
            }
        });

        _btn_LedControl.setOnToggleChanged(new TriStateToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(TriStateToggleButton.ToggleStatus toggleStatus, boolean booleanToggleStatus, int toggleIntValue) {
                int status = toggleStatus == TriStateToggleButton.ToggleStatus.on ? 1 : 0;
                Log.i(ActivityMainMenu.class.getName(), "_btn_LedControl setOnToggleChanged " + " ==> " + toggleIntValue + ":" + status);
                mqttHelper.pushDataToTopic(MqttHelper.feed_led_state, status + "");
            }
        });

        _btn_AlarmControl.setOnToggleChanged(new TriStateToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(TriStateToggleButton.ToggleStatus toggleStatus, boolean booleanToggleStatus, int toggleIntValue) {
                int status = toggleStatus == TriStateToggleButton.ToggleStatus.on ? 1 : 0;
                Log.i(ActivityMainMenu.class.getName(), "_btn_AlarmControl setOnToggleChanged " + " ==> " + toggleIntValue + ":" + status);

                mqttHelper.pushDataToTopic(MqttHelper.feed_alarm_state, status + "");
            }
        });

        _btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mqttHelper.pushDataToTopic(MqttHelper.feed_alarm_state, ResetState + "");

                if(ResetState == 0) {
                    _btnReset.setText("Start");
                    _btnReset.setBackground(getDrawable(R.color.green_600));
                    ResetState = 1;
                    return;
                }

                if(ResetState == 1) {
                    _btnReset.setText("Reset");
                    _btnReset.setBackground(getDrawable(R.color.orange_600));
                    ResetState = 1;
                    return;
                }
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

                // add to log
                addRecordToLog(message, feedId);

                if (MqttHelper.feed_fan_state.toLowerCase().equals(feedId)) {
                    try {

                        _btn_FanControl.setToggleStatus("0".equals(message) == false);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    return;
                }

                if (MqttHelper.feed_led_state.toLowerCase().equals(feedId)) {
                    try {
                        /*status = Boolean.parseBoolean(message) ? TriStateToggleButton.ToggleStatus.on : TriStateToggleButton.ToggleStatus.off;*/
                        _btn_LedControl.setToggleStatus("0".equals(message) == false);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return;
                }

                if (MqttHelper.feed_alarm_state.toLowerCase().equals(feedId)) {
                    try {
                        /*status = Boolean.parseBoolean(message) ? TriStateToggleButton.ToggleStatus.on : TriStateToggleButton.ToggleStatus.off;*/
                        _btn_AlarmControl.setToggleStatus("0".equals(message) == false);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return;
                }

                if (MqttHelper.feed_image.toLowerCase().equals(feedId)) {
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
                                adapterImage.notifyDataSetChanged();
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

                if (MqttHelper.feed_logs.toLowerCase().equals(feedId)) {
                    String json = message.toString();
                    Log.i(TAG, "onResponse: " + json);
                    Gson gson = new Gson();
                    JsonDataFeedModel.Root root = gson.fromJson(json, JsonDataFeedModel.Root.class);
                    // do something with the feed object
                    String key = root.key;
                    String value = root.name;

                    if("ObjectDetection".equals(key)) {
                        talkToMe("Xin chào " + value);
                    }
                    return;
                }

                if (MqttHelper.feed_humidity_meter.toLowerCase().equals(feedId)) {
                    try {
                        meterHumidity.speedTo(Float.parseFloat(message));
                        tvHum.setText(String.valueOf(Math.round(Float.parseFloat(message))));
                        tvHumTime.setText(Tools.parseCurrentDateTime());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return;
                }

                if (MqttHelper.feed_temperature_meter.toLowerCase().equals(feedId)) {
                    try {
                        meterTemper.speedTo(Float.parseFloat(message));
                        tvTemp.setText(message);
                        tvTempTime.setText(Tools.parseCurrentDateTime());
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


    @Override
    public void onInit(int status) {

    }

    @Override
    public void onNewData(byte[] data) {

    }

    @Override
    public void onRunError(Exception e) {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //do they have the data
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DATA_CHECKING) {
            //yep - go ahead and instantiate
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
                niceTTS = new TextToSpeech(this, this);
                //no data, prompt to install it
            else {
                Intent promptInstall = new Intent();
                promptInstall.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(promptInstall);
            }
        }
    }
}
