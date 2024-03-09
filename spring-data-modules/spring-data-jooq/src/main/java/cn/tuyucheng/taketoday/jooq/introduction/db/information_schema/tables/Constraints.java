/*
 * This file is generated by jOOQ.
 */
package cn.tuyucheng.taketoday.jooq.introduction.db.information_schema.tables;


import cn.tuyucheng.taketoday.jooq.introduction.db.information_schema.InformationSchema;
import cn.tuyucheng.taketoday.jooq.introduction.db.information_schema.tables.records.ConstraintsRecord;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row13;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class Constraints extends TableImpl<ConstraintsRecord> {

   private static final long serialVersionUID = 1L;

   /**
    * The reference instance of <code>INFORMATION_SCHEMA.CONSTRAINTS</code>
    */
   public static final Constraints CONSTRAINTS = new Constraints();

   /**
    * The class holding records for this type
    */
   @Override
   public Class<ConstraintsRecord> getRecordType() {
      return ConstraintsRecord.class;
   }

   /**
    * The column <code>INFORMATION_SCHEMA.CONSTRAINTS.CONSTRAINT_CATALOG</code>.
    */
   public final TableField<ConstraintsRecord, String> CONSTRAINT_CATALOG = createField(DSL.name("CONSTRAINT_CATALOG"), SQLDataType.VARCHAR(2147483647), this, "");

   /**
    * The column <code>INFORMATION_SCHEMA.CONSTRAINTS.CONSTRAINT_SCHEMA</code>.
    */
   public final TableField<ConstraintsRecord, String> CONSTRAINT_SCHEMA = createField(DSL.name("CONSTRAINT_SCHEMA"), SQLDataType.VARCHAR(2147483647), this, "");

   /**
    * The column <code>INFORMATION_SCHEMA.CONSTRAINTS.CONSTRAINT_NAME</code>.
    */
   public final TableField<ConstraintsRecord, String> CONSTRAINT_NAME = createField(DSL.name("CONSTRAINT_NAME"), SQLDataType.VARCHAR(2147483647), this, "");

   /**
    * The column <code>INFORMATION_SCHEMA.CONSTRAINTS.CONSTRAINT_TYPE</code>.
    */
   public final TableField<ConstraintsRecord, String> CONSTRAINT_TYPE = createField(DSL.name("CONSTRAINT_TYPE"), SQLDataType.VARCHAR(2147483647), this, "");

   /**
    * The column <code>INFORMATION_SCHEMA.CONSTRAINTS.TABLE_CATALOG</code>.
    */
   public final TableField<ConstraintsRecord, String> TABLE_CATALOG = createField(DSL.name("TABLE_CATALOG"), SQLDataType.VARCHAR(2147483647), this, "");

   /**
    * The column <code>INFORMATION_SCHEMA.CONSTRAINTS.TABLE_SCHEMA</code>.
    */
   public final TableField<ConstraintsRecord, String> TABLE_SCHEMA = createField(DSL.name("TABLE_SCHEMA"), SQLDataType.VARCHAR(2147483647), this, "");

   /**
    * The column <code>INFORMATION_SCHEMA.CONSTRAINTS.TABLE_NAME</code>.
    */
   public final TableField<ConstraintsRecord, String> TABLE_NAME = createField(DSL.name("TABLE_NAME"), SQLDataType.VARCHAR(2147483647), this, "");

   /**
    * The column <code>INFORMATION_SCHEMA.CONSTRAINTS.UNIQUE_INDEX_NAME</code>.
    */
   public final TableField<ConstraintsRecord, String> UNIQUE_INDEX_NAME = createField(DSL.name("UNIQUE_INDEX_NAME"), SQLDataType.VARCHAR(2147483647), this, "");

   /**
    * The column <code>INFORMATION_SCHEMA.CONSTRAINTS.CHECK_EXPRESSION</code>.
    */
   public final TableField<ConstraintsRecord, String> CHECK_EXPRESSION = createField(DSL.name("CHECK_EXPRESSION"), SQLDataType.VARCHAR(2147483647), this, "");

   /**
    * The column <code>INFORMATION_SCHEMA.CONSTRAINTS.COLUMN_LIST</code>.
    */
   public final TableField<ConstraintsRecord, String> COLUMN_LIST = createField(DSL.name("COLUMN_LIST"), SQLDataType.VARCHAR(2147483647), this, "");

   /**
    * The column <code>INFORMATION_SCHEMA.CONSTRAINTS.REMARKS</code>.
    */
   public final TableField<ConstraintsRecord, String> REMARKS = createField(DSL.name("REMARKS"), SQLDataType.VARCHAR(2147483647), this, "");

   /**
    * The column <code>INFORMATION_SCHEMA.CONSTRAINTS.SQL</code>.
    */
   public final TableField<ConstraintsRecord, String> SQL = createField(DSL.name("SQL"), SQLDataType.VARCHAR(2147483647), this, "");

   /**
    * The column <code>INFORMATION_SCHEMA.CONSTRAINTS.ID</code>.
    */
   public final TableField<ConstraintsRecord, Integer> ID = createField(DSL.name("ID"), SQLDataType.INTEGER, this, "");

   private Constraints(Name alias, Table<ConstraintsRecord> aliased) {
      this(alias, aliased, null);
   }

   private Constraints(Name alias, Table<ConstraintsRecord> aliased, Field<?>[] parameters) {
      super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
   }

   /**
    * Create an aliased <code>INFORMATION_SCHEMA.CONSTRAINTS</code> table reference
    */
   public Constraints(String alias) {
      this(DSL.name(alias), CONSTRAINTS);
   }

   /**
    * Create an aliased <code>INFORMATION_SCHEMA.CONSTRAINTS</code> table reference
    */
   public Constraints(Name alias) {
      this(alias, CONSTRAINTS);
   }

   /**
    * Create a <code>INFORMATION_SCHEMA.CONSTRAINTS</code> table reference
    */
   public Constraints() {
      this(DSL.name("CONSTRAINTS"), null);
   }

   public <O extends Record> Constraints(Table<O> child, ForeignKey<O, ConstraintsRecord> key) {
      super(child, key, CONSTRAINTS);
   }

   @Override
   public Schema getSchema() {
      return InformationSchema.INFORMATION_SCHEMA;
   }

   @Override
   public Constraints as(String alias) {
      return new Constraints(DSL.name(alias), this);
   }

   @Override
   public Constraints as(Name alias) {
      return new Constraints(alias, this);
   }

   /**
    * Rename this table
    */
   @Override
   public Constraints rename(String name) {
      return new Constraints(DSL.name(name), null);
   }

   /**
    * Rename this table
    */
   @Override
   public Constraints rename(Name name) {
      return new Constraints(name, null);
   }

   // -------------------------------------------------------------------------
   // Row13 type methods
   // -------------------------------------------------------------------------

   @Override
   public Row13<String, String, String, String, String, String, String, String, String, String, String, String, Integer> fieldsRow() {
      return (Row13) super.fieldsRow();
   }
}
