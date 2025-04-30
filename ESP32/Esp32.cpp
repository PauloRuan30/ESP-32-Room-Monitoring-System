#include <WiFi.h>
#include <PubSubClient.h>
#include <DHT.h>
#include <ArduinoJson.h>

// Configurações de WiFi
const char* ssid = "SUA_REDE_WIFI";
const char* password = "SUA_SENHA_WIFI";

// Configurações do MQTT
const char* mqtt_server = "IP_DO_SERVIDOR_MQTT"; // Ex: "192.168.1.100"
const int mqtt_port = 1883;
const char* mqtt_user = "mqtt_user";         // Se necessário
const char* mqtt_password = "mqtt_password"; // Se necessário
const char* mqtt_topic = "smda/sensor/ambiente1";
const char* client_id = "ESP32_Ambiente1";   // ID único para cada ESP32

// Configuração do sensor DHT
#define DHTPIN 4          // Pino digital conectado ao sensor DHT
#define DHTTYPE DHT22     // DHT 22 (AM2302) - altere para DHT11 se estiver usando esse modelo
DHT dht(DHTPIN, DHTTYPE);

// Intervalo de leitura do sensor (em milissegundos)
const long interval = 10000;
unsigned long previousMillis = 0;

// Configuração WiFi e MQTT
WiFiClient espClient;
PubSubClient client(espClient);

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

void reconnect() {
  // Loop até reconectar
  while (!client.connected()) {
    Serial.print("Tentando conexão MQTT...");
    
    // Tentativa de conexão
    if (client.connect(client_id, mqtt_user, mqtt_password)) {
      Serial.println("conectado");
      
      // Subscrever em tópicos de controle se necessário
      // client.subscribe("smda/control/ambiente1");
    } else {
      Serial.print("falhou, rc=");
      Serial.print(client.state());
      Serial.println(" tentando novamente em 5 segundos");
      delay(5000);
    }
  }
}

void callback(char* topic, byte* payload, unsigned int length) {
  // Manipular mensagens recebidas (para controle remoto)
  Serial.print("Mensagem recebida [");
  Serial.print(topic);
  Serial.print("] ");
  
  // Converter payload para string
  String message;
  for (int i = 0; i < length; i++) {
    message += (char)payload[i];
  }
  Serial.println(message);
  
  // Processar comandos recebidos (implementar conforme necessidade)
}

void setup() {
  Serial.begin(115200);
  
  // Inicializar sensor
  dht.begin();
  
  // Configurar WiFi
  setup_wifi();
  
  // Configurar servidor MQTT
  client.setServer(mqtt_server, mqtt_port);
  client.setCallback(callback);
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  unsigned long currentMillis = millis();
  
  // Verificar se é hora de ler os sensores
  if (currentMillis - previousMillis >= interval) {
    previousMillis = currentMillis;
    
    // Ler temperatura e umidade
    float humidity = dht.readHumidity();
    float temperature = dht.readTemperature();
    
    // Verificar se as leituras são válidas
    if (isnan(humidity) || isnan(temperature)) {
      Serial.println("Falha na leitura do sensor DHT!");
      return;
    }
    
    // Criar JSON com os dados
    StaticJsonDocument<200> doc;
    doc["device_id"] = client_id;
    doc["temperature"] = temperature;
    doc["humidity"] = humidity;
    doc["timestamp"] = currentMillis / 1000; // Timestamp aproximado
    
    // Serializar JSON para string
    char buffer[200];
    serializeJson(doc, buffer);
    
    // Publicar mensagem
    Serial.print("Publicando mensagem: ");
    Serial.println(buffer);
    client.publish(mqtt_topic, buffer);
  }
}