package cn.tuyucheng.taketoday.spring.data.cassandra.test.api;

import cn.tuyucheng.taketoday.spring.data.cassandra.test.domain.Vehicle;
import cn.tuyucheng.taketoday.spring.data.cassandra.test.service.InventoryService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/api/inventory")
public class InventoryController {
    private InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/vehicles")
    public List<Vehicle> getVehicles() {
        return this.inventoryService.getVehicles();
    }

    @PostMapping("/vehicles")
    public void addVehicles(@RequestBody List<Vehicle> vehicles) {
        this.inventoryService.addVehicles(vehicles);
    }

    @PutMapping("/vehicles")
    public void updateVehicles(@RequestBody List<Vehicle> vehicles) {
        this.inventoryService.updateVehicles(vehicles);
    }

    @PutMapping("/vehicles/{vin}")
    public void updateVehicles(@PathVariable(name = "vin") String vin,
                               @RequestBody Vehicle vehicles) {
        this.inventoryService.updateVehicle(vin, vehicles);
    }

    @DeleteMapping("/vehicles/{vin}")
    public void removeVehicle(@PathVariable(name = "vin") String vin) {
        this.inventoryService.deleteVehicle(vin);
    }
}