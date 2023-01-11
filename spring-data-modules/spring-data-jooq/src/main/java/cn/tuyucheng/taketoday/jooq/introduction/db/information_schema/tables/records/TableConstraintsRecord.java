/*
 * This file is generated by jOOQ.
 */
package cn.tuyucheng.taketoday.jooq.introduction.db.information_schema.tables.records;


import cn.tuyucheng.taketoday.jooq.introduction.db.information_schema.tables.TableConstraints;
import org.jooq.Field;
import org.jooq.Record9;
import org.jooq.Row9;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class TableConstraintsRecord extends TableRecordImpl<TableConstraintsRecord> implements Record9<String, String, String, String, String, String, String, String, String> {

	private static final long serialVersionUID = 1L;

	/**
	 * Setter for <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.CONSTRAINT_CATALOG</code>.
	 */
	public void setConstraintCatalog(String value) {
		set(0, value);
	}

	/**
	 * Getter for <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.CONSTRAINT_CATALOG</code>.
	 */
	public String getConstraintCatalog() {
		return (String) get(0);
	}

	/**
	 * Setter for <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.CONSTRAINT_SCHEMA</code>.
	 */
	public void setConstraintSchema(String value) {
		set(1, value);
	}

	/**
	 * Getter for <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.CONSTRAINT_SCHEMA</code>.
	 */
	public String getConstraintSchema() {
		return (String) get(1);
	}

	/**
	 * Setter for <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.CONSTRAINT_NAME</code>.
	 */
	public void setConstraintName(String value) {
		set(2, value);
	}

	/**
	 * Getter for <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.CONSTRAINT_NAME</code>.
	 */
	public String getConstraintName() {
		return (String) get(2);
	}

	/**
	 * Setter for <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.CONSTRAINT_TYPE</code>.
	 */
	public void setConstraintType(String value) {
		set(3, value);
	}

	/**
	 * Getter for <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.CONSTRAINT_TYPE</code>.
	 */
	public String getConstraintType() {
		return (String) get(3);
	}

	/**
	 * Setter for <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.TABLE_CATALOG</code>.
	 */
	public void setTableCatalog(String value) {
		set(4, value);
	}

	/**
	 * Getter for <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.TABLE_CATALOG</code>.
	 */
	public String getTableCatalog() {
		return (String) get(4);
	}

	/**
	 * Setter for <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.TABLE_SCHEMA</code>.
	 */
	public void setTableSchema(String value) {
		set(5, value);
	}

	/**
	 * Getter for <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.TABLE_SCHEMA</code>.
	 */
	public String getTableSchema() {
		return (String) get(5);
	}

	/**
	 * Setter for <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.TABLE_NAME</code>.
	 */
	public void setTableName(String value) {
		set(6, value);
	}

	/**
	 * Getter for <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.TABLE_NAME</code>.
	 */
	public String getTableName() {
		return (String) get(6);
	}

	/**
	 * Setter for <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.IS_DEFERRABLE</code>.
	 */
	public void setIsDeferrable(String value) {
		set(7, value);
	}

	/**
	 * Getter for <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.IS_DEFERRABLE</code>.
	 */
	public String getIsDeferrable() {
		return (String) get(7);
	}

	/**
	 * Setter for <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.INITIALLY_DEFERRED</code>.
	 */
	public void setInitiallyDeferred(String value) {
		set(8, value);
	}

	/**
	 * Getter for <code>INFORMATION_SCHEMA.TABLE_CONSTRAINTS.INITIALLY_DEFERRED</code>.
	 */
	public String getInitiallyDeferred() {
		return (String) get(8);
	}

	// -------------------------------------------------------------------------
	// Record9 type implementation
	// -------------------------------------------------------------------------

	@Override
	public Row9<String, String, String, String, String, String, String, String, String> fieldsRow() {
		return (Row9) super.fieldsRow();
	}

	@Override
	public Row9<String, String, String, String, String, String, String, String, String> valuesRow() {
		return (Row9) super.valuesRow();
	}

	@Override
	public Field<String> field1() {
		return TableConstraints.TABLE_CONSTRAINTS.CONSTRAINT_CATALOG;
	}

	@Override
	public Field<String> field2() {
		return TableConstraints.TABLE_CONSTRAINTS.CONSTRAINT_SCHEMA;
	}

	@Override
	public Field<String> field3() {
		return TableConstraints.TABLE_CONSTRAINTS.CONSTRAINT_NAME;
	}

	@Override
	public Field<String> field4() {
		return TableConstraints.TABLE_CONSTRAINTS.CONSTRAINT_TYPE;
	}

	@Override
	public Field<String> field5() {
		return TableConstraints.TABLE_CONSTRAINTS.TABLE_CATALOG;
	}

	@Override
	public Field<String> field6() {
		return TableConstraints.TABLE_CONSTRAINTS.TABLE_SCHEMA;
	}

	@Override
	public Field<String> field7() {
		return TableConstraints.TABLE_CONSTRAINTS.TABLE_NAME;
	}

	@Override
	public Field<String> field8() {
		return TableConstraints.TABLE_CONSTRAINTS.IS_DEFERRABLE;
	}

	@Override
	public Field<String> field9() {
		return TableConstraints.TABLE_CONSTRAINTS.INITIALLY_DEFERRED;
	}

	@Override
	public String component1() {
		return getConstraintCatalog();
	}

	@Override
	public String component2() {
		return getConstraintSchema();
	}

	@Override
	public String component3() {
		return getConstraintName();
	}

	@Override
	public String component4() {
		return getConstraintType();
	}

	@Override
	public String component5() {
		return getTableCatalog();
	}

	@Override
	public String component6() {
		return getTableSchema();
	}

	@Override
	public String component7() {
		return getTableName();
	}

	@Override
	public String component8() {
		return getIsDeferrable();
	}

	@Override
	public String component9() {
		return getInitiallyDeferred();
	}

	@Override
	public String value1() {
		return getConstraintCatalog();
	}

	@Override
	public String value2() {
		return getConstraintSchema();
	}

	@Override
	public String value3() {
		return getConstraintName();
	}

	@Override
	public String value4() {
		return getConstraintType();
	}

	@Override
	public String value5() {
		return getTableCatalog();
	}

	@Override
	public String value6() {
		return getTableSchema();
	}

	@Override
	public String value7() {
		return getTableName();
	}

	@Override
	public String value8() {
		return getIsDeferrable();
	}

	@Override
	public String value9() {
		return getInitiallyDeferred();
	}

	@Override
	public TableConstraintsRecord value1(String value) {
		setConstraintCatalog(value);
		return this;
	}

	@Override
	public TableConstraintsRecord value2(String value) {
		setConstraintSchema(value);
		return this;
	}

	@Override
	public TableConstraintsRecord value3(String value) {
		setConstraintName(value);
		return this;
	}

	@Override
	public TableConstraintsRecord value4(String value) {
		setConstraintType(value);
		return this;
	}

	@Override
	public TableConstraintsRecord value5(String value) {
		setTableCatalog(value);
		return this;
	}

	@Override
	public TableConstraintsRecord value6(String value) {
		setTableSchema(value);
		return this;
	}

	@Override
	public TableConstraintsRecord value7(String value) {
		setTableName(value);
		return this;
	}

	@Override
	public TableConstraintsRecord value8(String value) {
		setIsDeferrable(value);
		return this;
	}

	@Override
	public TableConstraintsRecord value9(String value) {
		setInitiallyDeferred(value);
		return this;
	}

	@Override
	public TableConstraintsRecord values(String value1, String value2, String value3, String value4, String value5, String value6, String value7, String value8, String value9) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		value8(value8);
		value9(value9);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached TableConstraintsRecord
	 */
	public TableConstraintsRecord() {
		super(TableConstraints.TABLE_CONSTRAINTS);
	}

	/**
	 * Create a detached, initialised TableConstraintsRecord
	 */
	public TableConstraintsRecord(String constraintCatalog, String constraintSchema, String constraintName, String constraintType, String tableCatalog, String tableSchema, String tableName, String isDeferrable, String initiallyDeferred) {
		super(TableConstraints.TABLE_CONSTRAINTS);

		setConstraintCatalog(constraintCatalog);
		setConstraintSchema(constraintSchema);
		setConstraintName(constraintName);
		setConstraintType(constraintType);
		setTableCatalog(tableCatalog);
		setTableSchema(tableSchema);
		setTableName(tableName);
		setIsDeferrable(isDeferrable);
		setInitiallyDeferred(initiallyDeferred);
	}
}
