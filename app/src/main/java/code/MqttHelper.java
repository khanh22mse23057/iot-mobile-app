package code;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;

public class MqttHelper {
    private MqttAndroidClient mqttAndroidClient;

    public static String serverUri = "io.adafruit.com";
    public static String clientId = "khanhpnp90";
    public static String secretKey = "";
    public static String feed_btn_shutdown = "/feeds/shutdown";
    public static String feed_alarm_state = "/feeds/alarm_state";
    public static String feed_message = "/feeds/message";
    public static String feed_humidity_meter = "/feeds/humidity_meter";
    public static String feed_temperature_meter = "/feeds/temperature_meter";
    public static String feed_image = "/feeds/image";
    public static String feed_fan_state = "/feeds/fan_state";
    public static String feed_led_state = "/feeds/led_state";
    public static String feed_logs = "/feeds/logs";
    public static String publishTopic = "exampleAndroidPublishTopic";
    public static String publishMessage = "Hello World!";

    private Context mContext;
    private MqttCallback mqttCallback;

    public MqttHelper(Context context) {
        mContext = context;
    }

    public MqttHelper(Context context, MqttCallback callback) {
        mContext = context;
        mqttCallback = callback;
    }

    public void setupMQTT() {
        //String URL = String.format("mqtts://%s:%s@%s:1883", clientId, secretKey, serverUri);
        String URL = String.format("tcp://%s:1883", serverUri);
        Log.i(ActivityMainMenu.class.getName(), URL);
        mqttAndroidClient = new MqttAndroidClient(mContext, URL, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    addToHistory("Reconnected to : " + serverURI);

                } else {
                    addToHistory("Connected to: " + serverURI);
                }

                if (mqttCallback != null) mqttCallback.onConnected(reconnect, serverURI);
            }

            @Override
            public void connectionLost(Throwable cause) {
                addToHistory("The Connection was lost.");
                if (mqttCallback != null) mqttCallback.onDisconnected(cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                String data = new String(message.getPayload());
                addToHistory("Incoming message: " + data);
                if (mqttCallback != null) mqttCallback.onMessage(topic, data);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(clientId);
        mqttConnectOptions.setPassword(secretKey.toCharArray());
        MQTTConnect(mqttConnectOptions);
    }

    private void MQTTConnect(MqttConnectOptions mqttConnectOptions) {
        try {
            //addToHistory("Connecting to " + serverUri);
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);

                    subscribeToTopic(feed_btn_shutdown);

                    subscribeToTopic(feed_humidity_meter);
                    subscribeToTopic(feed_temperature_meter);

                    subscribeToTopic(feed_message);
                    subscribeToTopic(feed_image);
                    subscribeToTopic(feed_fan_state);

                    subscribeToTopic(feed_led_state);
                    subscribeToTopic(feed_fan_state);
                    subscribeToTopic(feed_alarm_state);

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    addToHistory("Failed to connect to: " + serverUri);
                    Log.d(ActivityMainMenu.class.getName(), "onFailure: " + exception);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addToHistory(String mainText) {
        System.out.println("LOG: " + mainText);
    }

    public void subscribeToTopic(String subscriptionTopic) {
        subscriptionTopic = clientId + subscriptionTopic;
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    addToHistory("Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    addToHistory("Failed to subscribe");
                }
            });
        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pushDataToTopic(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setRetained(false);
        String subscriptionTopic = clientId + topic;
        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);
        try {
            mqttAndroidClient.publish(subscriptionTopic, msg);
        }catch (MqttException e){
        }
    }
}
