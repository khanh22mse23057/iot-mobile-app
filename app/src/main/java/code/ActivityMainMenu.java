package code;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.anastr.speedviewlib.AwesomeSpeedometer;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.material.components.BuildConfig;
import com.material.components.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import code.data.SharedPref;
import code.room.AppDatabase;
import code.room.DAO;
import code.utils.Tools;
import it.beppi.tristatetogglebutton_library.TriStateToggleButton;

public class ActivityMainMenu extends AppCompatActivity implements TextToSpeech.OnInitListener, SerialInputOutputManager.Listener {


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

    private TextView txtStatus;
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

        initToolbar();
        initComponent();
        subscribeMQTTTopics();
        initTextVoice();
    }

    private void initTextVoice() {
        Intent checkData = new Intent();
        //set it up to check for tts data
        checkData.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        //start it so that it returns the result
        startActivityForResult(checkData, DATA_CHECKING);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        Tools.setSystemBarColorInt(this, getResources().getColor(R.color.darkHomeDark));
    }

    private void initComponent() {
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
        txtStatus = findViewById(R.id._id_tvStatus);
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

        openUART();
    }


    @Override
    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            niceTTS.setLanguage(Locale.forLanguageTag("VI"));
            talkToMe("Xin chào các bạn, tôi là hệ thống nhận diện khuôn mặt");
        }
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

    public void talkToMe(String sentence) {
        String speakWords = sentence;
        niceTTS.speak(speakWords, TextToSpeech.QUEUE_FLUSH, null);
    }

    UsbSerialPort port;

    private void connectSerial() {
        try {
// Find all available drivers from attached devices.
            UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
            List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
            if (availableDrivers.isEmpty()) {
                return;
            }

            // Open a connection to the first available driver.
            UsbSerialDriver driver = availableDrivers.get(0);
            UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
            if (connection == null) {
                // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
                return;
            }

            UsbSerialPort port = driver.getPorts().get(0); // Most devices have just one port (port 0)
            port.open(connection);
            port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            port = port;
        } catch (Exception ex) {

        }

    }

    private void openUART() {
        connectSerial();

        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);

        if (availableDrivers.isEmpty()) {
            Log.d("UART", "UART is not available");
            txtStatus.setText("UART is not available");

        } else {
            Log.d("UART", "UART is available");
            txtStatus.setText("UART is available");

            UsbSerialDriver driver = availableDrivers.get(0);
            UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
            if (connection == null) {

                PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(INTENT_ACTION_GRANT_USB), 0);
                manager.requestPermission(driver.getDevice(), usbPermissionIntent);

                manager.requestPermission(driver.getDevice(), PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0));


                return;
            } else {

                port = driver.getPorts().get(0);
                try {
                    port.open(connection);
                    //port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                    port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

                    SerialInputOutputManager usbIoManager = new SerialInputOutputManager(port, null);
/*                    Executors.newSingleThreadExecutor().submit(usbIoManager);*/
                    Log.d("UART", "UART is openned");
                    txtStatus.setText("UART is openned");

                    usbIoManager = new SerialInputOutputManager(port, this);
                    usbIoManager.start();


                } catch (Exception e) {
                    Log.d("UART", "There is error");
                    txtStatus.setText("There is error");
                }
            }
        }

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
                        byte[] decodedString = Base64.decode(message, Base64.DEFAULT);
                        // Convert the byte array to a Bitmap
                        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imgView.setImageBitmap(decodedBitmap);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return;
                }

                if (MqttHelper.feed_message.toLowerCase().equals(feedId)) {
                    txtMessage.setText(message);
                    ProcessData(message);
                    return;
                }

                if (MqttHelper.feed_classroom_humidity.toLowerCase().equals(feedId)) {
                    try {
                        meterHumidity.speedTo(Float.parseFloat(message));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return;
                }

                if (MqttHelper.feed_classroom_temperature.toLowerCase().equals(feedId)) {
                    try {
                        meterTemper.speedTo(Float.parseFloat(message));
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        doExitApp();
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "Press again to exit app", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    public static boolean active = false;

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


    private String ProcessData(String data) {
        if ("1".equals(data))
            talkToMe("Xin chào " + "Khánh Phạm");

        if("2".equals(data))
            talkToMe("Xin chào " + "Minh Phong");

        if ("3".equals(data))
            talkToMe("Xin chào " + "Minh Hiếu");

        if ("4".equals(data))
            talkToMe("Xin chào " + "Tuấn Anh");

        return data;
    }

    String buffer;
    String name;
    @Override
    public void onNewData(byte[] data) {
        Log.d("UART", "Received: " + new String(data));
        buffer += new String(data);
        if (buffer.contains("!") && buffer.contains("#")) {
            String msg = buffer.replace("!", "");
            msg = buffer.replace("#", "");
            ProcessData(msg);
            name = msg;
            runOnUiThread(() -> { txtMessage.append(new String(data)); });

            buffer = "";
        } else {

        }
    }


    @Override
    public void onRunError(Exception e) {

    }
}
