package cn.tuyucheng.taketoday.boot.unique.field.web;

import cn.tuyucheng.taketoday.boot.unique.field.dao.AssetRepository;
import cn.tuyucheng.taketoday.boot.unique.field.dao.CompanyRepository;
import cn.tuyucheng.taketoday.boot.unique.field.dao.CustomerRepository;
import cn.tuyucheng.taketoday.boot.unique.field.dao.SaleRepository;
import cn.tuyucheng.taketoday.boot.unique.field.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/unique-field")
public class UniqueFieldController {
    @Autowired
    private SaleRepository saleRepo;

    @Autowired
    private CompanyRepository companyRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private AssetRepository assetRepo;

    @PostMapping("/sale")
    public Sale post(@RequestBody Sale sale) {
        return saleRepo.insert(sale);
    }

    @GetMapping("/sale")
    public Optional<Sale> getSale(SaleId id) {
        return saleRepo.findBySaleId(id);
    }

    @PostMapping("/company")
    public Company post(@RequestBody Company company) {
        return companyRepo.insert(company);
    }

    @PutMapping("/company")
    public Company put(@RequestBody Company company) {
        return companyRepo.save(company);
    }

    @GetMapping("/company/{id}")
    public Optional<Company> getCompany(@PathVariable String id) {
        return companyRepo.findById(id);
    }

    @PostMapping("/customer")
    public Customer post(@RequestBody Customer customer) {
        return customerRepo.insert(customer);
    }

    @GetMapping("/customer/{id}")
    public Optional<Customer> getCustomer(@PathVariable String id) {
        return customerRepo.findById(id);
    }

    @PostMapping("/asset")
    public Asset post(@RequestBody Asset asset) {
        return assetRepo.insert(asset);
    }

    @GetMapping("/asset/{id}")
    public Optional<Asset> getAsset(@PathVariable String id) {
        return assetRepo.findById(id);
    }
}