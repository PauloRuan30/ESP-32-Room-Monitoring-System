import paho.mqtt.client as mqtt
import json
import time
import requests
import random

# Backend REST API (Spring Boot)
API_BASE = "http://localhost:8080/api/devices"
MQTT_TOPIC_BASE = "smda/sensor/"

# HiveMQ Cloud config
broker = "553f2de0dc524df8977652e0f013d42a.s1.eu.hivemq.cloud"
port = 8883
username = "hivemq.webclient.1747244146252"
password = "h3.sbHn0:;PO%9a8RDqM"

# === Criação do dispositivo via REST ===
def create_device():
    payload = {
        "name": "esp32",
        "type": "multi_sensor",
        "location": "sala de reunião"
    }
    try:
        response = requests.post(API_BASE, json=payload, timeout=5)
        response.raise_for_status()
        device = response.json()
        print("Dispositivo criado:", device)
        return device["id"]
    except Exception as e:
        print("Erro ao criar dispositivo via REST:", e)
        return "default-device"  # fallback para tópico genérico

# === Simulação de dados de sensores ===
def get_sensor_data(device_id):
    return {
        "device": {"id": device_id + "_2"},
        "temperature": round(22.0 + random.uniform(-2, 2), 2),
        "humidity": round(45.0 + random.uniform(-5, 5), 2),
        "co2": round(400 + random.uniform(-50, 50), 2),
        "luminosity": round(300 + random.uniform(-100, 100), 2),
        "presence": random.choice([True, False]),
        "noise_level": round(random.uniform(35, 80), 2),  # dB - ruído ambiente
        "energy_consumption": round(random.uniform(30, 50), 2),  # mA ou mW estimado do ESP32
        "timestamp": int(time.time())
    }

# === Configura e conecta MQTT ===
def setup_mqtt():
    client = mqtt.Client()
    client.username_pw_set(username, password)
    client.tls_set()
    try:
        client.connect(broker, port)
        print("Conectado ao broker MQTT com sucesso.")
        return client
    except Exception as e:
        print("Erro ao conectar ao broker MQTT:", e)
        return None

# === Envio dos dados ===
def enviar_dados(client, device_id):
    topic = MQTT_TOPIC_BASE + device_id
    while True:
        data = get_sensor_data(device_id)
        payload = json.dumps(data)

        # Tenta enviar via MQTT
        if client:
            try:
                client.publish(topic, payload)
                print(f"[MQTT] Published to {topic}: {data}")
            except Exception as e:
                print("[MQTT] Falha ao publicar, tentando REST. Erro:", e)
                client = None  # Fallback
        # Fallback REST
        if not client:
            try:
                response = requests.post(f"{API_BASE}/data", json=data, timeout=5)
                response.raise_for_status()
                print(f"[REST] Dados enviados via REST: {data}")
            except Exception as e:
                print("[REST] Falha ao enviar dados:", e)

        time.sleep(10)

# === Execução principal ===
if __name__ == "__main__":
    device_id = create_device()
    mqtt_client = setup_mqtt()
    enviar_dados(mqtt_client, device_id)
