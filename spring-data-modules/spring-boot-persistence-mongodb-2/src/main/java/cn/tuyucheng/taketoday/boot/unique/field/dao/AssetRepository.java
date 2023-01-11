package cn.tuyucheng.taketoday.boot.unique.field.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import cn.tuyucheng.taketoday.boot.unique.field.data.Asset;

public interface AssetRepository extends MongoRepository<Asset, String> {
}