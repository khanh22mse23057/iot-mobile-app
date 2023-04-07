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

public class MqttHelper {
    private MqttAndroidClient mqttAndroidClient;

    public static String serverUri = "io.adafruit.com";
    public static String clientId = "khanhpnp90";
    public static String secretKey = "aio_rSar06WUGefTM7GcUtS0saftVTPj";
    public static String feed_btn_reset = "/feeds/btn-reset";
    public static String feed_btn_stage = "/feeds/btn-stage";
    public static String feed_message = "/feeds/message";
    public static String feed_classroom_humidity = "/feeds/classroom-humidity";
    public static String feed_classroom_temperature = "/feeds/classroom-temperature";
    public static String feed_imask = "/feeds/imask";
    public static String feed_slider_bar = "/feeds/slider-bar";

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

                    subscribeToTopic(feed_btn_reset);
                    subscribeToTopic(feed_btn_stage);
                    subscribeToTopic(feed_classroom_humidity);
                    subscribeToTopic(feed_classroom_temperature);
                    subscribeToTopic(feed_message);
                    subscribeToTopic(feed_imask);
                    subscribeToTopic(feed_slider_bar);

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
}
