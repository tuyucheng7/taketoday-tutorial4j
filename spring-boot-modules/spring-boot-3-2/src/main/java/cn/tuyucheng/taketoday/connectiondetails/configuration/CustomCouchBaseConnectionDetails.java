package cn.tuyucheng.taketoday.connectiondetails.configuration;

import cn.tuyucheng.taketoday.connectiondetails.adapter.VaultAdapter;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseConnectionDetails;

public class CustomCouchBaseConnectionDetails implements CouchbaseConnectionDetails {
   @Override
   public String getConnectionString() {
      return VaultAdapter.getSecret("couch_connection_string");
   }

   @Override
   public String getUsername() {
      return VaultAdapter.getSecret("couch_user");
   }

   @Override
   public String getPassword() {
      return VaultAdapter.getSecret("couch_secret");
   }
}