#include <WiFi.h>
#include <PubSubClient.h>
#include <WiFiClientSecure.h>
#include <DHT.h>
#include <ArduinoJson.h>

// WiFi configuration (replace with your credentials)
const char* ssid = "YOUR_WIFI_SSID";
const char* password = "YOUR_WIFI_PASSWORD";

// HiveMQ Cloud MQTT configuration
const char* mqtt_server = "553f2de0dc524df8977652e0f013d42a.s1.eu.hivemq.cloud";
const int mqtt_port = 8883;
const char* mqtt_user = "hivemq.webclient.1747244146252";
const char* mqtt_password = "h3.sbHn0:;PO%9a8RDqM";
const char* mqtt_topic = "smda/sensor/550e8400-e29b-41d4-a716-446655440000";
const char* client_id = "ESP32_550e8400-e29b-41d4-a716-446655440000";

// DHT sensor configuration
#define DHTPIN 4
#define DHTTYPE DHT22
DHT dht(DHTPIN, DHTTYPE);

// Root CA certificate for HiveMQ Cloud (ISRG Root X1)
const char* root_ca = "-----BEGIN CERTIFICATE-----\n"
"MIIFazCCA1OgAwIBAgIRAIIQz7DSQONZRGPgu2OCiwAwDQYJKoZIhvcNAQELBQAw\n"
"TzELMAkGA1UEBhMCVVMxKTAnBgNVBAoTIEludGVybmV0IFNlY3VyaXR5IFJlc2Vh\n"
"cmNoIEdyb3VwMRUwEwYDVQQDEwxJU1JHIFJvb3QgWDEwHhcNMTUwNjA0MTEwNDM4\n"
"WhcNMzUwNjA0MTEwNDM4WjBPMQswCQYDVQQGEwJVUzEpMCcGA1UEChMgSW50ZXJu\n"
"ZXQgU2VjdXJpdHkgUmVzZWFyY2ggR3JvdXAxFTATBgNVBAMTDElTUkcgUm9vdCBY\n"
"MTCCAiIwDQYJKoZIhvcNAQEBBQADggIPADCCAgoCggIBAK3oJHP0FDfzm54rVygc\n"
"h77ct984kIxuPOZXoHj3dcKi/vVqbvYATyjb3miGbESTtrFj/RQSa78f0uoxmyF+\n"
"0TM8ukj13Xnfs7j/EvEhmkvBioZxaUpmZmyPfjxwv60pIgbz5MDmgK7iS4+3mX6U\n"
"A5/TR5d8mUgjU+g4rk8Kb4Mu0UlXjIB0ttov0DiNewNwIRt18jA8+o+u3dpjq+sW\n"
"T8KOEUt+zwvo/7V3LvSye0rgTBIlDHCNAymg4VMk7BPZ7hm/ELNKjD+Jo2FR3qyH\n"
"B5T0Y3HsLuJvW5iB4YlcNHlsdu87kGJ55tukmi8mxdAQ4Q7e2RCOFvu396j3x+UC\n"
"B5iPNgiV5+I3lg02dZ77DnKxHZu8A/lJBdiB3QW0KtZB6awBdpUKD9jf1b0 WjJOT\n"
"L92m9STJEadao0xAH0ahmbWnOlFuhjuefXKnEgV4We0+UXgVCwOPjdAvBbI+e0oc\n"
"S3MFEvzG6uBQE3xDk3SzynTn\n"
"jh8BCNAw1FtxNrQHusEwMFxIt4I7mKZ9YIqioymCzLq9gwQbooMDQaHWBfEbwrbw\n"
"qHyGO0aoSCqI3Haadr8faqU9GY/rOPNk3sgrDQoo//fb4hVC1CLQJ13hef4Y53CI\n"
"rU7m2Ys6xt0nUW7/vGT1M0NPAgMBAAGjQjBAMA4GA1UdDwEB/wQEAwIBBjAPBgNV\n"
"HRMBAf8EBTADAQH/MB0GA1UdDgQWBBR5tFnme7bl5AFzgAiIyBpY9umbbjANBgkq\n"
"hkiG9w0BAQsFAAOCAgEAVR9YqbyyqFDQDLHYGmkgJykIrGF1XIpu+ILlaS/V9lZL\n"
"ubhzEFnTIZd+50xx+7LSYK05qAvqFyFWhfFQDlnrzuBZ6brJFe+GnY+EgPbk6ZGQ\n"
"3BebYhtF8GaV0nxvwuo77x/Py9auJ/GpsMiu/X1+mvoiBOv/2X/qkSsisRcOj/KK\n"
"NFtY2PwByVS5uCbMiogziUwthDyC3+6WVwW6LLv3xLfHTjuCvjHIInNzktHCgKQ5\n"
"ORAzI4JMPJ+GslWYHb4phowim57iaztXOoJwTdwJx4nLCgdNbOhdjsnvzqvHu7Ur\n"
"TkXWStAmzOVyyghqpZXjFaH3pO3JLF+l+/+sKAIuvtd7u+Nxe5AW0wdeRlN8NwdC\n"
"jNPElpzVmbUq4JUagEiuTDkHzsxHpFKVK7q4+63SM1N95R1NbdWhscdCb+ZAJzVc\n"
"oyi3B43njTOQ5yOf+1CceWxG1bQVs5ZufpsMljq4Ui0/1lvh+wjChP4kqKOJ2qxq\n"
"4RgqsahDYVvTH9w7jXbyLeiNdd8XM2w9U/t7y0Ff/9yi0GE44Za4rF2LN9d11TPA\n"
"mRGunUHBcnWEvgJBQl9nJEiU0Zsnvgc/ubhPgXRR4Xq37Z0j4r7g1SgEEzwxA57d\n"
"emyPxgcYxn/eR44/KJ4EBs+lVDR3veyJm+kXQ99b21/+jh5Xos1AnX5iItreGCc=\n"
"-----END CERTIFICATE-----";

// Interval for sensor readings (in milliseconds)
const long interval = 10000;
unsigned long previousMillis = 0;

// Initialize secure WiFi and MQTT clients
WiFiClientSecure espClient;
PubSubClient client(espClient);

// Connect to WiFi network
void setup_wifi() {
    delay(10);
    Serial.println();
    Serial.print("Conectando a ");
    Serial.println(ssid);

    WiFi.begin(ssid, password);

    while (WiFi.status() != WL_CONNECTED) {
        delay(500);
        Serial.print(".");
    }

    Serial.println("");
    Serial.println("WiFi conectado");
    Serial.println("Endereço IP: ");
    Serial.println(WiFi.localIP());
}

// Reconnect to MQTT broker if connection is lost
void reconnect() {
    while (!client.connected()) {
        Serial.print("Tentando conexão MQTT...");
        if (client.connect(client_id, mqtt_user, mqtt_password)) {
            Serial.println("conectado");
        } else {
            Serial.print("falhou, rc=");
            Serial.print(client.state());
            Serial.println(" tentando novamente em 5 segundos");
            delay(5000);
        }
    }
}

// Handle incoming MQTT messages (if subscribed)
void callback(char* topic, byte* payload, unsigned int length) {
    Serial.print("Mensagem recebida [");
    Serial.print(topic);
    Serial.print("] ");
    String message;
    for (int i = 0; i < length; i++) {
        message += (char)payload[i];
    }
    Serial.println(message);
}

// Initial setup
void setup() {
    Serial.begin(115200);
    dht.begin();
    setup_wifi();
    espClient.setCACert(root_ca);
    client.setServer(mqtt_server, mqtt_port);
    client.setCallback(callback);
}

// Main loop: handle WiFi/MQTT connections and publish sensor data
void loop() {
    if (WiFi.status() != WL_CONNECTED) {
        Serial.println("WiFi desconectado, reconectando...");
        setup_wifi();
    }
    if (!client.connected()) {
        reconnect();
    }
    client.loop();

    unsigned long currentMillis = millis();
    if (currentMillis - previousMillis >= interval) {
        previousMillis = currentMillis;
        float humidity = dht.readHumidity();
        float temperature = dht.readTemperature();
        if (isnan(humidity) || isnan(temperature)) {
            Serial.println("Falha na leitura do sensor DHT!");
            client.publish("smda/error/550e8400-e29b-41d4-a716-446655440000", "{\"error\": \"DHT read failure\"}");
            return;
        }
        StaticJsonDocument<200> doc;
        doc["device_id"] = client_id;
        doc["temperature"] = temperature;
        doc["humidity"] = humidity;
        doc["timestamp"] = currentMillis / 1000;
        char buffer[200];
        serializeJson(doc, buffer);
        Serial.print("Publicando mensagem: ");
        Serial.println(buffer);
        client.publish(mqtt_topic, buffer);
    }
}
