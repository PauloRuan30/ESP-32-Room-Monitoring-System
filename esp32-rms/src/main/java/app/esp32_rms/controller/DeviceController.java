package app.esp32_rms.controller;

import app.esp32_rms.model.Device;
import app.esp32_rms.service.DeviceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/devices")
public class DeviceController {

    private final DeviceService service;

    public DeviceController(DeviceService service) {
        this.service = service;
    }

    @GetMapping
    public List<Device> getAll() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Device getOne(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping
    public Device create(@RequestBody Device device) {
        return service.save(device);
    }

    @PutMapping("/{id}")
    public Device update(@PathVariable UUID id, @RequestBody Device device) {
        device.setId(id);
        return service.save(device);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}