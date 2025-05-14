#### Dockerfile to Compile and Upload ESP32 Sketch Using Arduino CLI
# Use official Arduino CLI image
FROM arduino/arduino-cli:latest

# Install ESP32 board support and required libraries
RUN arduino-cli core update-index \
    && arduino-cli core install esp32:esp32 \
    && arduino-cli lib install "PubSubClient" \
    && arduino-cli lib install "DHT sensor library" \
    && arduino-cli lib install "ArduinoJson"

# Set working directory
WORKDIR /workspace

# Copy the sketch (assumes your sketch file is named 'sketch.ino' or adjust accordingly)
COPY sketch.ino /workspace/

# Pre-compile to verify dependencies
RUN arduino-cli compile --fqbn esp32:esp32:esp32 /workspace

# Default command: show serial monitor at 115200 baud
# To upload instead, override CMD when running the container
CMD ["arduino-cli", "monitor", "-p", "/dev/ttyUSB0", "--baudrate", "115200"]