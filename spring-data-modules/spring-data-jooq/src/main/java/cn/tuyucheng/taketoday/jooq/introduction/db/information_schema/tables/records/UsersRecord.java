/*
 * This file is generated by jOOQ.
 */
package cn.tuyucheng.taketoday.jooq.introduction.db.information_schema.tables.records;


import cn.tuyucheng.taketoday.jooq.introduction.db.information_schema.tables.Users;
import org.jooq.Field;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class UsersRecord extends TableRecordImpl<UsersRecord> implements Record4<String, String, String, Integer> {

   private static final long serialVersionUID = 1L;

   /**
    * Setter for <code>INFORMATION_SCHEMA.USERS.NAME</code>.
    */
   public void setName(String value) {
      set(0, value);
   }

   /**
    * Getter for <code>INFORMATION_SCHEMA.USERS.NAME</code>.
    */
   public String getName() {
      return (String) get(0);
   }

   /**
    * Setter for <code>INFORMATION_SCHEMA.USERS.ADMIN</code>.
    */
   public void setAdmin(String value) {
      set(1, value);
   }

   /**
    * Getter for <code>INFORMATION_SCHEMA.USERS.ADMIN</code>.
    */
   public String getAdmin() {
      return (String) get(1);
   }

   /**
    * Setter for <code>INFORMATION_SCHEMA.USERS.REMARKS</code>.
    */
   public void setRemarks(String value) {
      set(2, value);
   }

   /**
    * Getter for <code>INFORMATION_SCHEMA.USERS.REMARKS</code>.
    */
   public String getRemarks() {
      return (String) get(2);
   }

   /**
    * Setter for <code>INFORMATION_SCHEMA.USERS.ID</code>.
    */
   public void setId(Integer value) {
      set(3, value);
   }

   /**
    * Getter for <code>INFORMATION_SCHEMA.USERS.ID</code>.
    */
   public Integer getId() {
      return (Integer) get(3);
   }

   // -------------------------------------------------------------------------
   // Record4 type implementation
   // -------------------------------------------------------------------------

   @Override
   public Row4<String, String, String, Integer> fieldsRow() {
      return (Row4) super.fieldsRow();
   }

   @Override
   public Row4<String, String, String, Integer> valuesRow() {
      return (Row4) super.valuesRow();
   }

   @Override
   public Field<String> field1() {
      return Users.USERS.NAME;
   }

   @Override
   public Field<String> field2() {
      return Users.USERS.ADMIN;
   }

   @Override
   public Field<String> field3() {
      return Users.USERS.REMARKS;
   }

   @Override
   public Field<Integer> field4() {
      return Users.USERS.ID;
   }

   @Override
   public String component1() {
      return getName();
   }

   @Override
   public String component2() {
      return getAdmin();
   }

   @Override
   public String component3() {
      return getRemarks();
   }

   @Override
   public Integer component4() {
      return getId();
   }

   @Override
   public String value1() {
      return getName();
   }

   @Override
   public String value2() {
      return getAdmin();
   }

   @Override
   public String value3() {
      return getRemarks();
   }

   @Override
   public Integer value4() {
      return getId();
   }

   @Override
   public UsersRecord value1(String value) {
      setName(value);
      return this;
   }

   @Override
   public UsersRecord value2(String value) {
      setAdmin(value);
      return this;
   }

   @Override
   public UsersRecord value3(String value) {
      setRemarks(value);
      return this;
   }

   @Override
   public UsersRecord value4(Integer value) {
      setId(value);
      return this;
   }

   @Override
   public UsersRecord values(String value1, String value2, String value3, Integer value4) {
      value1(value1);
      value2(value2);
      value3(value3);
      value4(value4);
      return this;
   }

   // -------------------------------------------------------------------------
   // Constructors
   // -------------------------------------------------------------------------

   /**
    * Create a detached UsersRecord
    */
   public UsersRecord() {
      super(Users.USERS);
   }

   /**
    * Create a detached, initialised UsersRecord
    */
   public UsersRecord(String name, String admin, String remarks, Integer id) {
      super(Users.USERS);

      setName(name);
      setAdmin(admin);
      setRemarks(remarks);
      setId(id);
   }
}
