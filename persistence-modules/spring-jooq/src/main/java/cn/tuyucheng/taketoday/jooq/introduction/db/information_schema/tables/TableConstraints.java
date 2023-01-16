/*
 * This file is generated by jOOQ.
 */
package cn.tuyucheng.taketoday.jooq.introduction.db.information_schema.tables;


import cn.tuyucheng.taketoday.jooq.introduction.db.information_schema.InformationSchema;
import cn.tuyucheng.taketoday.jooq.introduction.db.information_schema.tables.records.TableConstraintsRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row9;
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
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TableConstraints extends TableImpl<TableConstraintsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS</code>
     */
    public static final TableConstraints TABLE_CONSTRAINTS = new TableConstraints();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TableConstraintsRecord> getRecordType() {
        return TableConstraintsRecord.class;
    }

    /**
     * The column <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.CONSTRAINT_CATALOG</code>.
     */
    public final TableField<TableConstraintsRecord, String> CONSTRAINT_CATALOG = createField(DSL.name("CONSTRAINT_CATALOG"), SQLDataType.VARCHAR(2147483647), this, "");

    /**
     * The column <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.CONSTRAINT_SCHEMA</code>.
     */
    public final TableField<TableConstraintsRecord, String> CONSTRAINT_SCHEMA = createField(DSL.name("CONSTRAINT_SCHEMA"), SQLDataType.VARCHAR(2147483647), this, "");

    /**
     * The column <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.CONSTRAINT_NAME</code>.
     */
    public final TableField<TableConstraintsRecord, String> CONSTRAINT_NAME = createField(DSL.name("CONSTRAINT_NAME"), SQLDataType.VARCHAR(2147483647), this, "");

    /**
     * The column <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.CONSTRAINT_TYPE</code>.
     */
    public final TableField<TableConstraintsRecord, String> CONSTRAINT_TYPE = createField(DSL.name("CONSTRAINT_TYPE"), SQLDataType.VARCHAR(2147483647), this, "");

    /**
     * The column <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.TABLE_CATALOG</code>.
     */
    public final TableField<TableConstraintsRecord, String> TABLE_CATALOG = createField(DSL.name("TABLE_CATALOG"), SQLDataType.VARCHAR(2147483647), this, "");

    /**
     * The column <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.TABLE_SCHEMA</code>.
     */
    public final TableField<TableConstraintsRecord, String> TABLE_SCHEMA = createField(DSL.name("TABLE_SCHEMA"), SQLDataType.VARCHAR(2147483647), this, "");

    /**
     * The column <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.TABLE_NAME</code>.
     */
    public final TableField<TableConstraintsRecord, String> TABLE_NAME = createField(DSL.name("TABLE_NAME"), SQLDataType.VARCHAR(2147483647), this, "");

    /**
     * The column <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.IS_DEFERRABLE</code>.
     */
    public final TableField<TableConstraintsRecord, String> IS_DEFERRABLE = createField(DSL.name("IS_DEFERRABLE"), SQLDataType.VARCHAR(2147483647), this, "");

    /**
     * The column <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.INITIALLY_DEFERRED</code>.
     */
    public final TableField<TableConstraintsRecord, String> INITIALLY_DEFERRED = createField(DSL.name("INITIALLY_DEFERRED"), SQLDataType.VARCHAR(2147483647), this, "");

    private TableConstraints(Name alias, Table<TableConstraintsRecord> aliased) {
        this(alias, aliased, null);
    }

    private TableConstraints(Name alias, Table<TableConstraintsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS</code> table reference
     */
    public TableConstraints(String alias) {
        this(DSL.name(alias), TABLE_CONSTRAINTS);
    }

    /**
     * Create an aliased <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS</code> table reference
     */
    public TableConstraints(Name alias) {
        this(alias, TABLE_CONSTRAINTS);
    }

    /**
     * Create a <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS</code> table reference
     */
    public TableConstraints() {
        this(DSL.name("TABLE_CONSTRAINTS"), null);
    }

    public <O extends Record> TableConstraints(Table<O> child, ForeignKey<O, TableConstraintsRecord> key) {
        super(child, key, TABLE_CONSTRAINTS);
    }

    @Override
    public Schema getSchema() {
        return InformationSchema.INFORMATION_SCHEMA;
    }

    @Override
    public TableConstraints as(String alias) {
        return new TableConstraints(DSL.name(alias), this);
    }

    @Override
    public TableConstraints as(Name alias) {
        return new TableConstraints(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TableConstraints rename(String name) {
        return new TableConstraints(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TableConstraints rename(Name name) {
        return new TableConstraints(name, null);
    }

    // -------------------------------------------------------------------------
    // Row9 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row9<String, String, String, String, String, String, String, String, String> fieldsRow() {
        return (Row9) super.fieldsRow();
    }
}
