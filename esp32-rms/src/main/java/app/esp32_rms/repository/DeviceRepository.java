package app.esp32_rms.repository;

import app.esp32_rms.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {
}
