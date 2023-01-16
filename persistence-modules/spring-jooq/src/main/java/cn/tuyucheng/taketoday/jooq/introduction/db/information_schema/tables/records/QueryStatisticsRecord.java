/*
 * This file is generated by jOOQ.
 */
package cn.tuyucheng.taketoday.jooq.introduction.db.information_schema.tables.records;


import cn.tuyucheng.taketoday.jooq.introduction.db.information_schema.tables.QueryStatistics;

import org.jooq.Field;
import org.jooq.Record12;
import org.jooq.Row12;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class QueryStatisticsRecord extends TableRecordImpl<QueryStatisticsRecord> implements Record12<String, Integer, Double, Double, Double, Double, Double, Integer, Integer, Long, Double, Double> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.SQL_STATEMENT</code>.
     */
    public void setSqlStatement(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.SQL_STATEMENT</code>.
     */
    public String getSqlStatement() {
        return (String) get(0);
    }

    /**
     * Setter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.EXECUTION_COUNT</code>.
     */
    public void setExecutionCount(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.EXECUTION_COUNT</code>.
     */
    public Integer getExecutionCount() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.MIN_EXECUTION_TIME</code>.
     */
    public void setMinExecutionTime(Double value) {
        set(2, value);
    }

    /**
     * Getter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.MIN_EXECUTION_TIME</code>.
     */
    public Double getMinExecutionTime() {
        return (Double) get(2);
    }

    /**
     * Setter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.MAX_EXECUTION_TIME</code>.
     */
    public void setMaxExecutionTime(Double value) {
        set(3, value);
    }

    /**
     * Getter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.MAX_EXECUTION_TIME</code>.
     */
    public Double getMaxExecutionTime() {
        return (Double) get(3);
    }

    /**
     * Setter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.CUMULATIVE_EXECUTION_TIME</code>.
     */
    public void setCumulativeExecutionTime(Double value) {
        set(4, value);
    }

    /**
     * Getter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.CUMULATIVE_EXECUTION_TIME</code>.
     */
    public Double getCumulativeExecutionTime() {
        return (Double) get(4);
    }

    /**
     * Setter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.AVERAGE_EXECUTION_TIME</code>.
     */
    public void setAverageExecutionTime(Double value) {
        set(5, value);
    }

    /**
     * Getter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.AVERAGE_EXECUTION_TIME</code>.
     */
    public Double getAverageExecutionTime() {
        return (Double) get(5);
    }

    /**
     * Setter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.STD_DEV_EXECUTION_TIME</code>.
     */
    public void setStdDevExecutionTime(Double value) {
        set(6, value);
    }

    /**
     * Getter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.STD_DEV_EXECUTION_TIME</code>.
     */
    public Double getStdDevExecutionTime() {
        return (Double) get(6);
    }

    /**
     * Setter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.MIN_ROW_COUNT</code>.
     */
    public void setMinRowCount(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.MIN_ROW_COUNT</code>.
     */
    public Integer getMinRowCount() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.MAX_ROW_COUNT</code>.
     */
    public void setMaxRowCount(Integer value) {
        set(8, value);
    }

    /**
     * Getter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.MAX_ROW_COUNT</code>.
     */
    public Integer getMaxRowCount() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.CUMULATIVE_ROW_COUNT</code>.
     */
    public void setCumulativeRowCount(Long value) {
        set(9, value);
    }

    /**
     * Getter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.CUMULATIVE_ROW_COUNT</code>.
     */
    public Long getCumulativeRowCount() {
        return (Long) get(9);
    }

    /**
     * Setter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.AVERAGE_ROW_COUNT</code>.
     */
    public void setAverageRowCount(Double value) {
        set(10, value);
    }

    /**
     * Getter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.AVERAGE_ROW_COUNT</code>.
     */
    public Double getAverageRowCount() {
        return (Double) get(10);
    }

    /**
     * Setter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.STD_DEV_ROW_COUNT</code>.
     */
    public void setStdDevRowCount(Double value) {
        set(11, value);
    }

    /**
     * Getter for <code>INFORMATION_SCHEMA.QUERY_STATISTICS.STD_DEV_ROW_COUNT</code>.
     */
    public Double getStdDevRowCount() {
        return (Double) get(11);
    }

    // -------------------------------------------------------------------------
    // Record12 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row12<String, Integer, Double, Double, Double, Double, Double, Integer, Integer, Long, Double, Double> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    @Override
    public Row12<String, Integer, Double, Double, Double, Double, Double, Integer, Integer, Long, Double, Double> valuesRow() {
        return (Row12) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return QueryStatistics.QUERY_STATISTICS.SQL_STATEMENT;
    }

    @Override
    public Field<Integer> field2() {
        return QueryStatistics.QUERY_STATISTICS.EXECUTION_COUNT;
    }

    @Override
    public Field<Double> field3() {
        return QueryStatistics.QUERY_STATISTICS.MIN_EXECUTION_TIME;
    }

    @Override
    public Field<Double> field4() {
        return QueryStatistics.QUERY_STATISTICS.MAX_EXECUTION_TIME;
    }

    @Override
    public Field<Double> field5() {
        return QueryStatistics.QUERY_STATISTICS.CUMULATIVE_EXECUTION_TIME;
    }

    @Override
    public Field<Double> field6() {
        return QueryStatistics.QUERY_STATISTICS.AVERAGE_EXECUTION_TIME;
    }

    @Override
    public Field<Double> field7() {
        return QueryStatistics.QUERY_STATISTICS.STD_DEV_EXECUTION_TIME;
    }

    @Override
    public Field<Integer> field8() {
        return QueryStatistics.QUERY_STATISTICS.MIN_ROW_COUNT;
    }

    @Override
    public Field<Integer> field9() {
        return QueryStatistics.QUERY_STATISTICS.MAX_ROW_COUNT;
    }

    @Override
    public Field<Long> field10() {
        return QueryStatistics.QUERY_STATISTICS.CUMULATIVE_ROW_COUNT;
    }

    @Override
    public Field<Double> field11() {
        return QueryStatistics.QUERY_STATISTICS.AVERAGE_ROW_COUNT;
    }

    @Override
    public Field<Double> field12() {
        return QueryStatistics.QUERY_STATISTICS.STD_DEV_ROW_COUNT;
    }

    @Override
    public String component1() {
        return getSqlStatement();
    }

    @Override
    public Integer component2() {
        return getExecutionCount();
    }

    @Override
    public Double component3() {
        return getMinExecutionTime();
    }

    @Override
    public Double component4() {
        return getMaxExecutionTime();
    }

    @Override
    public Double component5() {
        return getCumulativeExecutionTime();
    }

    @Override
    public Double component6() {
        return getAverageExecutionTime();
    }

    @Override
    public Double component7() {
        return getStdDevExecutionTime();
    }

    @Override
    public Integer component8() {
        return getMinRowCount();
    }

    @Override
    public Integer component9() {
        return getMaxRowCount();
    }

    @Override
    public Long component10() {
        return getCumulativeRowCount();
    }

    @Override
    public Double component11() {
        return getAverageRowCount();
    }

    @Override
    public Double component12() {
        return getStdDevRowCount();
    }

    @Override
    public String value1() {
        return getSqlStatement();
    }

    @Override
    public Integer value2() {
        return getExecutionCount();
    }

    @Override
    public Double value3() {
        return getMinExecutionTime();
    }

    @Override
    public Double value4() {
        return getMaxExecutionTime();
    }

    @Override
    public Double value5() {
        return getCumulativeExecutionTime();
    }

    @Override
    public Double value6() {
        return getAverageExecutionTime();
    }

    @Override
    public Double value7() {
        return getStdDevExecutionTime();
    }

    @Override
    public Integer value8() {
        return getMinRowCount();
    }

    @Override
    public Integer value9() {
        return getMaxRowCount();
    }

    @Override
    public Long value10() {
        return getCumulativeRowCount();
    }

    @Override
    public Double value11() {
        return getAverageRowCount();
    }

    @Override
    public Double value12() {
        return getStdDevRowCount();
    }

    @Override
    public QueryStatisticsRecord value1(String value) {
        setSqlStatement(value);
        return this;
    }

    @Override
    public QueryStatisticsRecord value2(Integer value) {
        setExecutionCount(value);
        return this;
    }

    @Override
    public QueryStatisticsRecord value3(Double value) {
        setMinExecutionTime(value);
        return this;
    }

    @Override
    public QueryStatisticsRecord value4(Double value) {
        setMaxExecutionTime(value);
        return this;
    }

    @Override
    public QueryStatisticsRecord value5(Double value) {
        setCumulativeExecutionTime(value);
        return this;
    }

    @Override
    public QueryStatisticsRecord value6(Double value) {
        setAverageExecutionTime(value);
        return this;
    }

    @Override
    public QueryStatisticsRecord value7(Double value) {
        setStdDevExecutionTime(value);
        return this;
    }

    @Override
    public QueryStatisticsRecord value8(Integer value) {
        setMinRowCount(value);
        return this;
    }

    @Override
    public QueryStatisticsRecord value9(Integer value) {
        setMaxRowCount(value);
        return this;
    }

    @Override
    public QueryStatisticsRecord value10(Long value) {
        setCumulativeRowCount(value);
        return this;
    }

    @Override
    public QueryStatisticsRecord value11(Double value) {
        setAverageRowCount(value);
        return this;
    }

    @Override
    public QueryStatisticsRecord value12(Double value) {
        setStdDevRowCount(value);
        return this;
    }

    @Override
    public QueryStatisticsRecord values(String value1, Integer value2, Double value3, Double value4, Double value5, Double value6, Double value7, Integer value8, Integer value9, Long value10, Double value11, Double value12) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached QueryStatisticsRecord
     */
    public QueryStatisticsRecord() {
        super(QueryStatistics.QUERY_STATISTICS);
    }

    /**
     * Create a detached, initialised QueryStatisticsRecord
     */
    public QueryStatisticsRecord(String sqlStatement, Integer executionCount, Double minExecutionTime, Double maxExecutionTime, Double cumulativeExecutionTime, Double averageExecutionTime, Double stdDevExecutionTime, Integer minRowCount, Integer maxRowCount, Long cumulativeRowCount, Double averageRowCount, Double stdDevRowCount) {
        super(QueryStatistics.QUERY_STATISTICS);

        setSqlStatement(sqlStatement);
        setExecutionCount(executionCount);
        setMinExecutionTime(minExecutionTime);
        setMaxExecutionTime(maxExecutionTime);
        setCumulativeExecutionTime(cumulativeExecutionTime);
        setAverageExecutionTime(averageExecutionTime);
        setStdDevExecutionTime(stdDevExecutionTime);
        setMinRowCount(minRowCount);
        setMaxRowCount(maxRowCount);
        setCumulativeRowCount(cumulativeRowCount);
        setAverageRowCount(averageRowCount);
        setStdDevRowCount(stdDevRowCount);
    }
}
