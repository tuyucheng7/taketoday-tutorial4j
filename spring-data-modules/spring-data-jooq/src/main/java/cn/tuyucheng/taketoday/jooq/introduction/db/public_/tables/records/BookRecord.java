/*
 * This file is generated by jOOQ.
 */
package cn.tuyucheng.taketoday.jooq.introduction.db.public_.tables.records;


import cn.tuyucheng.taketoday.jooq.introduction.db.public_.tables.Book;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class BookRecord extends UpdatableRecordImpl<BookRecord> implements Record2<Integer, String> {

   private static final long serialVersionUID = 1L;

   /**
    * Setter for <code>PUBLIC.BOOK.ID</code>.
    */
   public void setId(Integer value) {
      set(0, value);
   }

   /**
    * Getter for <code>PUBLIC.BOOK.ID</code>.
    */
   public Integer getId() {
      return (Integer) get(0);
   }

   /**
    * Setter for <code>PUBLIC.BOOK.TITLE</code>.
    */
   public void setTitle(String value) {
      set(1, value);
   }

   /**
    * Getter for <code>PUBLIC.BOOK.TITLE</code>.
    */
   public String getTitle() {
      return (String) get(1);
   }

   // -------------------------------------------------------------------------
   // Primary key information
   // -------------------------------------------------------------------------

   @Override
   public Record1<Integer> key() {
      return (Record1) super.key();
   }

   // -------------------------------------------------------------------------
   // Record2 type implementation
   // -------------------------------------------------------------------------

   @Override
   public Row2<Integer, String> fieldsRow() {
      return (Row2) super.fieldsRow();
   }

   @Override
   public Row2<Integer, String> valuesRow() {
      return (Row2) super.valuesRow();
   }

   @Override
   public Field<Integer> field1() {
      return Book.BOOK.ID;
   }

   @Override
   public Field<String> field2() {
      return Book.BOOK.TITLE;
   }

   @Override
   public Integer component1() {
      return getId();
   }

   @Override
   public String component2() {
      return getTitle();
   }

   @Override
   public Integer value1() {
      return getId();
   }

   @Override
   public String value2() {
      return getTitle();
   }

   @Override
   public BookRecord value1(Integer value) {
      setId(value);
      return this;
   }

   @Override
   public BookRecord value2(String value) {
      setTitle(value);
      return this;
   }

   @Override
   public BookRecord values(Integer value1, String value2) {
      value1(value1);
      value2(value2);
      return this;
   }

   // -------------------------------------------------------------------------
   // Constructors
   // -------------------------------------------------------------------------

   /**
    * Create a detached BookRecord
    */
   public BookRecord() {
      super(Book.BOOK);
   }

   /**
    * Create a detached, initialised BookRecord
    */
   public BookRecord(Integer id, String title) {
      super(Book.BOOK);

      setId(id);
      setTitle(title);
   }
}
