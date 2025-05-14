package app.esp32_rms.service;

import app.esp32_rms.model.Device;
import app.esp32_rms.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DeviceService {
    private final DeviceRepository repository;

    public DeviceService(DeviceRepository repository) {
        this.repository = repository;
    }

    public List<Device> listAll() {
        return repository.findAll();
    }

    public Device get(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public Device save(Device device) {
        return repository.save(device);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
