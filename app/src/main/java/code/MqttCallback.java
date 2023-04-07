package code;

public interface MqttCallback {
    void onMessage(String topic, String message);
    void onConnected(Boolean status, String address);
    void onDisconnected(Throwable error);
}
