/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.newphy.orm.mybatis.sql.builder;

import cn.newphy.druid.DruidRuntimeException;
import cn.newphy.druid.sql.SQLUtils;
import cn.newphy.druid.sql.SQLUtils.FormatOption;
import cn.newphy.druid.sql.ast.SQLExpr;
import cn.newphy.druid.sql.ast.SQLLimit;
import cn.newphy.druid.sql.ast.SQLObject;
import cn.newphy.druid.sql.ast.SQLOrderBy;
import cn.newphy.druid.sql.ast.SQLOver;
import cn.newphy.druid.sql.ast.SQLSetQuantifier;
import cn.newphy.druid.sql.ast.SQLStatement;
import cn.newphy.druid.sql.ast.expr.SQLAggregateExpr;
import cn.newphy.druid.sql.ast.expr.SQLAggregateOption;
import cn.newphy.druid.sql.ast.expr.SQLAllColumnExpr;
import cn.newphy.druid.sql.ast.expr.SQLBinaryOpExpr;
import cn.newphy.druid.sql.ast.expr.SQLBinaryOperator;
import cn.newphy.druid.sql.ast.expr.SQLIdentifierExpr;
import cn.newphy.druid.sql.ast.expr.SQLLiteralExpr;
import cn.newphy.druid.sql.ast.expr.SQLNumericLiteralExpr;
import cn.newphy.druid.sql.ast.expr.SQLPropertyExpr;
import cn.newphy.druid.sql.ast.expr.SQLVariantRefExpr;
import cn.newphy.druid.sql.ast.statement.SQLExprTableSource;
import cn.newphy.druid.sql.ast.statement.SQLSelect;
import cn.newphy.druid.sql.ast.statement.SQLSelectItem;
import cn.newphy.druid.sql.ast.statement.SQLSelectQuery;
import cn.newphy.druid.sql.ast.statement.SQLSelectQueryBlock;
import cn.newphy.druid.sql.ast.statement.SQLSelectStatement;
import cn.newphy.druid.sql.ast.statement.SQLSubqueryTableSource;
import cn.newphy.druid.sql.ast.statement.SQLTableSource;
import cn.newphy.druid.sql.ast.statement.SQLUnionQuery;
import cn.newphy.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import cn.newphy.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsSelectQueryBlock;
import cn.newphy.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import cn.newphy.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;
import cn.newphy.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock;
import cn.newphy.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import cn.newphy.druid.sql.dialect.sqlserver.ast.SQLServerTop;
import cn.newphy.druid.util.JdbcConstants;
import cn.newphy.druid.util.JdbcUtils;
import java.util.List;

public class PageSqlUtils {

    private static FormatOption NO_PRETTY_FORMAT = new FormatOption(true, false);

    public static String count(String sql, String dbType) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        if (stmtList.size() != 1) {
            throw new IllegalArgumentException("sql not support count : " + sql);
        }

        SQLStatement stmt = stmtList.get(0);

        if (!(stmt instanceof SQLSelectStatement)) {
            throw new IllegalArgumentException("sql not support count : " + sql);
        }

        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
        return count(selectStmt.getSelect(), dbType);
    }

    public static String limit(String sql, String dbType, String offset, String count) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        if (stmtList.size() != 1) {
            throw new IllegalArgumentException("sql not support count : " + sql);
        }

        SQLStatement stmt = stmtList.get(0);

        if (!(stmt instanceof SQLSelectStatement)) {
            throw new IllegalArgumentException("sql not support count : " + sql);
        }

        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;

        return limit(selectStmt.getSelect(), dbType, offset, count);
    }

    public static String limit(SQLSelect select, String dbType, String offset, String count) {
        doLimit(select, dbType, offset, count);

        return SQLUtils.toSQLString(select, dbType, NO_PRETTY_FORMAT);
    }

    public static void doLimit(SQLSelect select, String dbType, String offset, String count) {
        SQLSelectQuery query = select.getQuery();

        if (JdbcConstants.ORACLE.equals(dbType)) {
            limitOracle(select, dbType, offset, count);
            return;
        }

        if (JdbcConstants.DB2.equals(dbType)) {
            limitDB2(select, dbType, offset, count);
            return;
        }

        if (JdbcConstants.SQL_SERVER.equals(dbType) || JdbcUtils.JTDS.equals(dbType)) {
            limitSQLServer(select, dbType, offset, count);
            return;
        }

        limitQueryBlock(select, dbType, offset, count);
    }

    private static void limitQueryBlock(SQLSelect select, String dbType, String offset, String count) {
        SQLSelectQuery query = select.getQuery();
        if (query instanceof SQLUnionQuery) {
            SQLUnionQuery union = (SQLUnionQuery) query;
            limitUnion(union, dbType, offset, count);
            return;
        }

        SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
        if (JdbcConstants.POSTGRESQL.equals(dbType)) {
            limitPostgreSQLQueryBlock((PGSelectQueryBlock)queryBlock, dbType, offset, count);
            return;
        }
        // support other dbType

        limitMySqlQueryBlock(queryBlock, dbType, offset, count);


//        if (JdbcConstants.MYSQL.equals(dbType) || //
//            JdbcConstants.MARIADB.equals(dbType) || //
//            JdbcConstants.H2.equals(dbType)) {
//            return limitMySqlQueryBlock(queryBlock, dbType, offset, count, check);
//        }
//        throw new UnsupportedOperationException();
    }

    private static void limitPostgreSQLQueryBlock(PGSelectQueryBlock queryBlock, String dbType, String offset, String count) {
        SQLLimit limit = queryBlock.getLimit();
        if (limit != null) {
            if (offset != null) {
                limit.setOffset(new SQLVariantRefExpr(offset));
            }

            limit.setRowCount(new SQLVariantRefExpr(count));
        }

        limit = new SQLLimit();
        if (offset != null) {
            limit.setOffset(new SQLVariantRefExpr(offset));
        }
        limit.setRowCount(new SQLVariantRefExpr(count));
        queryBlock.setLimit(limit);
    }

    private static void limitDB2(SQLSelect select, String dbType, String offset, String count) {
        SQLSelectQuery query = select.getQuery();

        SQLBinaryOpExpr gt = new SQLBinaryOpExpr(new SQLIdentifierExpr("ROWNUM"), //
                                                 SQLBinaryOperator.GreaterThan, //
                                                 new SQLVariantRefExpr(offset), //
                                                 JdbcConstants.DB2);
        SQLBinaryOpExpr lteq = new SQLBinaryOpExpr(new SQLIdentifierExpr("ROWNUM"), //
                                                   SQLBinaryOperator.LessThanOrEqual, //
                                                    new SQLBinaryOpExpr(new SQLVariantRefExpr(
                                                        offset),
                                                        SQLBinaryOperator.Add,
                                                        new SQLVariantRefExpr(count),
                                                        JdbcConstants.DB2),
                                                   JdbcConstants.DB2);
        SQLBinaryOpExpr pageCondition = new SQLBinaryOpExpr(gt, SQLBinaryOperator.BooleanAnd, lteq, JdbcConstants.DB2);

        if (query instanceof SQLSelectQueryBlock) {
            DB2SelectQueryBlock queryBlock = (DB2SelectQueryBlock) query;
            if (offset == null) {
                queryBlock.setFirst(new SQLVariantRefExpr(count));
                return;
            }

            SQLAggregateExpr aggregateExpr = new SQLAggregateExpr("ROW_NUMBER");
            SQLOrderBy orderBy = select.getOrderBy();

            if (orderBy == null && select.getQuery() instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock selectQueryBlcok = (SQLSelectQueryBlock) select.getQuery();
                orderBy = selectQueryBlcok.getOrderBy();
                selectQueryBlcok.setOrderBy(null);
            } else {
                select.setOrderBy(null);
            }

            aggregateExpr.setOver(new SQLOver(orderBy));

            queryBlock.getSelectList().add(new SQLSelectItem(aggregateExpr, "ROWNUM"));

            DB2SelectQueryBlock countQueryBlock = new DB2SelectQueryBlock();
            countQueryBlock.getSelectList().add(new SQLSelectItem(new SQLAllColumnExpr()));

            countQueryBlock.setFrom(new SQLSubqueryTableSource(select.clone(), "XX"));

            countQueryBlock.setWhere(pageCondition);

            select.setQuery(countQueryBlock);
            return;
        }

        DB2SelectQueryBlock countQueryBlock = new DB2SelectQueryBlock();
        countQueryBlock.getSelectList().add(new SQLSelectItem(new SQLPropertyExpr(new SQLIdentifierExpr("XX"), "*")));
        SQLAggregateExpr aggregateExpr = new SQLAggregateExpr("ROW_NUMBER");
        SQLOrderBy orderBy = select.getOrderBy();
        aggregateExpr.setOver(new SQLOver(orderBy));
        select.setOrderBy(null);
        countQueryBlock.getSelectList().add(new SQLSelectItem(aggregateExpr, "ROWNUM"));

        countQueryBlock.setFrom(new SQLSubqueryTableSource(select.clone(), "XX"));

        if (offset == null) {
            select.setQuery(countQueryBlock);
            return;
        }

        DB2SelectQueryBlock offsetQueryBlock = new DB2SelectQueryBlock();
        offsetQueryBlock.getSelectList().add(new SQLSelectItem(new SQLAllColumnExpr()));
        offsetQueryBlock.setFrom(new SQLSubqueryTableSource(new SQLSelect(countQueryBlock), "XXX"));
        offsetQueryBlock.setWhere(pageCondition);

        select.setQuery(offsetQueryBlock);
    }

    private static void limitSQLServer(SQLSelect select, String dbType, String offset, String count) {
        SQLSelectQuery query = select.getQuery();

        SQLBinaryOpExpr gt = new SQLBinaryOpExpr(new SQLIdentifierExpr("ROWNUM"), //
                                                 SQLBinaryOperator.GreaterThan, //
                                                 new SQLVariantRefExpr(offset), //
                                                 JdbcConstants.SQL_SERVER);
        SQLBinaryOpExpr lteq = new SQLBinaryOpExpr(new SQLIdentifierExpr("ROWNUM"), //
                                                   SQLBinaryOperator.LessThanOrEqual, //
                                                    new SQLBinaryOpExpr(new SQLVariantRefExpr(
                                                        offset),
                                                        SQLBinaryOperator.Add,
                                                        new SQLVariantRefExpr(count),
                                                        JdbcConstants.SQL_SERVER),
                                                   JdbcConstants.SQL_SERVER);
        SQLBinaryOpExpr pageCondition = new SQLBinaryOpExpr(gt, SQLBinaryOperator.BooleanAnd, lteq,
                                                            JdbcConstants.SQL_SERVER);

        if (query instanceof SQLSelectQueryBlock) {
            SQLServerSelectQueryBlock queryBlock = (SQLServerSelectQueryBlock) query;
            if (offset == null) {
                SQLServerTop top = queryBlock.getTop();
                queryBlock.setTop(new SQLServerTop(new SQLVariantRefExpr(count)));
                return;
            }

            SQLAggregateExpr aggregateExpr = new SQLAggregateExpr("ROW_NUMBER");
            SQLOrderBy orderBy = select.getOrderBy();
            aggregateExpr.setOver(new SQLOver(orderBy));
            select.setOrderBy(null);

            queryBlock.getSelectList().add(new SQLSelectItem(aggregateExpr, "ROWNUM"));

            SQLServerSelectQueryBlock countQueryBlock = new SQLServerSelectQueryBlock();
            countQueryBlock.getSelectList().add(new SQLSelectItem(new SQLAllColumnExpr()));

            countQueryBlock.setFrom(new SQLSubqueryTableSource(select.clone(), "XX"));

            countQueryBlock.setWhere(pageCondition);

            select.setQuery(countQueryBlock);
            return;
        }

        SQLServerSelectQueryBlock countQueryBlock = new SQLServerSelectQueryBlock();
        countQueryBlock.getSelectList().add(new SQLSelectItem(new SQLPropertyExpr(new SQLIdentifierExpr("XX"), "*")));

        countQueryBlock.setFrom(new SQLSubqueryTableSource(select.clone(), "XX"));

        if (offset == null) {
            countQueryBlock.setTop(new SQLServerTop(new SQLVariantRefExpr(count)));

            select.setQuery(countQueryBlock);
            return;
        }

        SQLAggregateExpr aggregateExpr = new SQLAggregateExpr("ROW_NUMBER");
        SQLOrderBy orderBy = select.getOrderBy();
        aggregateExpr.setOver(new SQLOver(orderBy));
        select.setOrderBy(null);
        countQueryBlock.getSelectList().add(new SQLSelectItem(aggregateExpr, "ROWNUM"));

        SQLServerSelectQueryBlock offsetQueryBlock = new SQLServerSelectQueryBlock();
        offsetQueryBlock.getSelectList().add(new SQLSelectItem(new SQLAllColumnExpr()));
        offsetQueryBlock.setFrom(new SQLSubqueryTableSource(new SQLSelect(countQueryBlock), "XXX"));
        offsetQueryBlock.setWhere(pageCondition);

        select.setQuery(offsetQueryBlock);
    }

    private static void limitOracle(SQLSelect select, String dbType, String offset, String count) {
        SQLSelectQuery query = select.getQuery();

        if (query instanceof SQLSelectQueryBlock) {
            OracleSelectQueryBlock queryBlock = (OracleSelectQueryBlock) query;
            SQLOrderBy orderBy = select.getOrderBy();
            if (orderBy == null && queryBlock.getOrderBy() != null) {
                orderBy = queryBlock.getOrderBy();
            }

            if (queryBlock.getGroupBy() == null
                && orderBy == null && offset == null) {

                SQLExpr condition = new SQLBinaryOpExpr(new SQLIdentifierExpr("ROWNUM"), //
                    SQLBinaryOperator.LessThanOrEqual, //
                    new SQLVariantRefExpr(count), //
                    JdbcConstants.ORACLE);
                if (queryBlock.getWhere() == null) {
                    queryBlock.setWhere(condition);
                } else {
                    queryBlock.setWhere(new SQLBinaryOpExpr(queryBlock.getWhere(), //
                        SQLBinaryOperator.BooleanAnd, //
                        condition, //
                        JdbcConstants.ORACLE));
                }

                return;
            }
        }

        OracleSelectQueryBlock countQueryBlock = new OracleSelectQueryBlock();
        countQueryBlock.getSelectList().add(new SQLSelectItem(new SQLPropertyExpr(new SQLIdentifierExpr("XX"), "*")));
        countQueryBlock.getSelectList().add(new SQLSelectItem(new SQLIdentifierExpr("ROWNUM"), "RN"));

        countQueryBlock.setFrom(new SQLSubqueryTableSource(select.clone(), "XX"));
        countQueryBlock.setWhere(new SQLBinaryOpExpr(new SQLIdentifierExpr("ROWNUM"), //
            SQLBinaryOperator.LessThanOrEqual, //
            new SQLBinaryOpExpr(new SQLVariantRefExpr(
                offset),
                SQLBinaryOperator.Add,
                new SQLVariantRefExpr(count),
                JdbcConstants.ORACLE),
            JdbcConstants.ORACLE));

        select.setOrderBy(null);
        if (offset == null) {
            select.setQuery(countQueryBlock);
            return;
        }

        OracleSelectQueryBlock offsetQueryBlock = new OracleSelectQueryBlock();
        offsetQueryBlock.getSelectList().add(new SQLSelectItem(new SQLAllColumnExpr()));
        offsetQueryBlock.setFrom(new SQLSubqueryTableSource(new SQLSelect(countQueryBlock), "XXX"));
        offsetQueryBlock.setWhere(new SQLBinaryOpExpr(new SQLIdentifierExpr("RN"), //
            SQLBinaryOperator.GreaterThan, //
            new SQLVariantRefExpr(offset), //
            JdbcConstants.ORACLE));

        select.setQuery(offsetQueryBlock);
    }

    private static void limitMySqlQueryBlock(SQLSelectQueryBlock queryBlock, String dbType, String offset, String count) {
        SQLLimit limit = queryBlock.getLimit();
        if (limit != null) {
            if (offset != null) {
                limit.setOffset(new SQLVariantRefExpr(offset));
            }

            limit.setRowCount(new SQLVariantRefExpr(count));
        }

        if (limit == null) {
            limit = new SQLLimit();
            if (offset != null) {
                limit.setOffset(new SQLVariantRefExpr(offset));
            }
            limit.setRowCount(new SQLVariantRefExpr(count));
            queryBlock.setLimit(limit);
        }
    }

    private static void limitUnion(SQLUnionQuery queryBlock, String dbType, String offset, String count) {
        SQLLimit limit = queryBlock.getLimit();
        if (limit != null) {
            if (offset != null) {
                limit.setOffset(new SQLVariantRefExpr(offset));
            }
            limit.setRowCount(new SQLVariantRefExpr(count));
        }

        if (limit == null) {
            limit = new SQLLimit();
            if (offset != null) {
                limit.setOffset(new SQLVariantRefExpr(offset));
            }
            limit.setRowCount(new SQLVariantRefExpr(count));
            queryBlock.setLimit(limit);
        }
    }

    private static String count(SQLSelect select, String dbType) {
        if (select.getOrderBy() != null) {
            select.setOrderBy(null);
        }

        SQLSelectQuery query = select.getQuery();
        clearOrderBy(query);

        if (query instanceof SQLSelectQueryBlock) {
            SQLSelectItem countItem = createCountItem(dbType);

            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
            List<SQLSelectItem> selectList = queryBlock.getSelectList();

            if (queryBlock.getGroupBy() != null
                    && queryBlock.getGroupBy().getItems().size() > 0) {
                return createCountUseSubQuery(select, dbType);
            }

            int option = queryBlock.getDistionOption();
            if (option == SQLSetQuantifier.DISTINCT
                    && selectList.size() >= 1) {
                SQLAggregateExpr countExpr = new SQLAggregateExpr("COUNT", SQLAggregateOption.DISTINCT);
                for (int i = 0; i < selectList.size(); ++i) {
                    countExpr.addArgument(selectList.get(i).getExpr());
                }
                selectList.clear();
                queryBlock.setDistionOption(0);
                queryBlock.addSelectItem(countExpr);
            } else {
                selectList.clear();
                selectList.add(countItem);
            }
            return SQLUtils.toSQLString(select, dbType, NO_PRETTY_FORMAT);
        } else if (query instanceof SQLUnionQuery) {
            return createCountUseSubQuery(select, dbType);
        }

        throw new IllegalStateException();
    }

    private static String createCountUseSubQuery(SQLSelect select, String dbType) {
        SQLSelectQueryBlock countSelectQuery = createQueryBlock(dbType);

        SQLSelectItem countItem = createCountItem(dbType);
        countSelectQuery.getSelectList().add(countItem);

        SQLSubqueryTableSource fromSubquery = new SQLSubqueryTableSource(select);
        fromSubquery.setAlias("ALIAS_COUNT");
        countSelectQuery.setFrom(fromSubquery);

        SQLSelect countSelect = new SQLSelect(countSelectQuery);
        SQLSelectStatement countStmt = new SQLSelectStatement(countSelect, dbType);

        return SQLUtils.toSQLString(countStmt, dbType, NO_PRETTY_FORMAT);
    }

    private static SQLSelectQueryBlock createQueryBlock(String dbType) {
        if (JdbcConstants.MYSQL.equals(dbType)
                || JdbcConstants.MARIADB.equals(dbType)
                || JdbcConstants.ALIYUN_ADS.equals(dbType)) {
            return new MySqlSelectQueryBlock();
        }

        if (JdbcConstants.MARIADB.equals(dbType)) {
            return new MySqlSelectQueryBlock();
        }

        if (JdbcConstants.H2.equals(dbType)) {
            return new MySqlSelectQueryBlock();
        }

        if (JdbcConstants.ORACLE.equals(dbType)) {
            return new OracleSelectQueryBlock();
        }

        if (JdbcConstants.POSTGRESQL.equals(dbType)) {
            return new PGSelectQueryBlock();
        }

        if (JdbcConstants.SQL_SERVER.equals(dbType) || JdbcUtils.JTDS.equals(dbType)) {
            return new SQLServerSelectQueryBlock();
        }

        if (JdbcConstants.DB2.equals(dbType)) {
            return new DB2SelectQueryBlock();
        }

        return new SQLSelectQueryBlock();
    }

    private static SQLSelectItem createCountItem(String dbType) {
        SQLAggregateExpr countExpr = new SQLAggregateExpr("COUNT");

        countExpr.addArgument(new SQLAllColumnExpr());

        SQLSelectItem countItem = new SQLSelectItem(countExpr);
        return countItem;
    }

    private static void clearOrderBy(SQLSelectQuery query) {
        if (query instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
            if (queryBlock.getOrderBy() != null) {
                queryBlock.setOrderBy(null);
            }
            return;
        }

        if (query instanceof SQLUnionQuery) {
            SQLUnionQuery union = (SQLUnionQuery) query;
            if (union.getOrderBy() != null) {
                union.setOrderBy(null);
            }
            clearOrderBy(union.getLeft());
            clearOrderBy(union.getRight());
        }
    }

    /**
     *
     * @param sql
     * @param dbType
     * @return if not exists limit, return -1;
     */
    public static int getLimit(String sql, String dbType) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        if (stmtList.size() != 1) {
            return -1;
        }

        SQLStatement stmt = stmtList.get(0);

        if (stmt instanceof SQLSelectStatement) {
            SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
            SQLSelectQuery query = selectStmt.getSelect().getQuery();
            if (query instanceof SQLSelectQueryBlock) {
                if (query instanceof MySqlSelectQueryBlock) {
                    SQLLimit limit = ((MySqlSelectQueryBlock) query).getLimit();

                    if (limit == null) {
                        return -1;
                    }

                    SQLExpr rowCountExpr = limit.getRowCount();

                    if (rowCountExpr instanceof SQLNumericLiteralExpr) {
                        int rowCount = ((SQLNumericLiteralExpr) rowCountExpr).getNumber().intValue();
                        return rowCount;
                    }

                    return Integer.MAX_VALUE;
                }

                if (query instanceof OdpsSelectQueryBlock) {
                    SQLLimit limit = ((OdpsSelectQueryBlock) query).getLimit();
                    SQLExpr rowCountExpr = limit != null ? limit.getRowCount() : null;

                    if (rowCountExpr instanceof SQLNumericLiteralExpr) {
                        int rowCount = ((SQLNumericLiteralExpr) rowCountExpr).getNumber().intValue();
                        return rowCount;
                    }

                    return Integer.MAX_VALUE;
                }

                return -1;
            }
        }

        return -1;
    }

    public static boolean hasUnorderedLimit(String sql, String dbType) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        if (JdbcConstants.MYSQL.equals(dbType)) {

            MySqlUnorderedLimitDetectVisitor visitor = new MySqlUnorderedLimitDetectVisitor();

            for (SQLStatement stmt : stmtList) {
                stmt.accept(visitor);
            }

            return visitor.unorderedLimitCount > 0;
        }

        if (JdbcConstants.ORACLE.equals(dbType)) {

            OracleUnorderedLimitDetectVisitor visitor = new OracleUnorderedLimitDetectVisitor();

            for (SQLStatement stmt : stmtList) {
                stmt.accept(visitor);
            }

            return visitor.unorderedLimitCount > 0;
        }

        throw new DruidRuntimeException("not supported. dbType : " + dbType);
    }

    private static class MySqlUnorderedLimitDetectVisitor extends MySqlASTVisitorAdapter {
        public int unorderedLimitCount;

        @Override
        public boolean visit(MySqlSelectQueryBlock x) {
            SQLOrderBy orderBy = x.getOrderBy();
            SQLLimit limit = x.getLimit();

            if (limit != null && (orderBy == null || orderBy.getItems().size() == 0)) {
                boolean subQueryHasOrderBy = false;
                SQLTableSource from = x.getFrom();
                if (from instanceof SQLSubqueryTableSource) {
                    SQLSubqueryTableSource subqueryTabSrc = (SQLSubqueryTableSource) from;
                    SQLSelect select = subqueryTabSrc.getSelect();
                    if (select.getQuery() instanceof SQLSelectQueryBlock) {
                        SQLSelectQueryBlock subquery = (SQLSelectQueryBlock) select.getQuery();
                        if (subquery.getOrderBy() != null && subquery.getOrderBy().getItems().size() > 0) {
                            subQueryHasOrderBy = true;
                        }
                    }
                }

                if (!subQueryHasOrderBy) {
                    unorderedLimitCount++;
                }
            }
            return true;
        }
    }

    private static class OracleUnorderedLimitDetectVisitor extends OracleASTVisitorAdapter {
        public int unorderedLimitCount;

        public boolean visit(SQLBinaryOpExpr x) {
            SQLExpr left = x.getLeft();
            SQLExpr right = x.getRight();

            boolean rownum = false;
            if (left instanceof SQLIdentifierExpr
                    && ((SQLIdentifierExpr) left).getName().equalsIgnoreCase("ROWNUM")
                    && right instanceof SQLLiteralExpr) {
                rownum = true;
            } else if (right instanceof SQLIdentifierExpr
                    && ((SQLIdentifierExpr) right).getName().equalsIgnoreCase("ROWNUM")
                    && left instanceof SQLLiteralExpr) {
                rownum = true;
            }

            OracleSelectQueryBlock selectQuery = null;
            if (rownum) {
                for (SQLObject parent = x.getParent(); parent != null; parent = parent.getParent()) {
                    if (parent instanceof SQLSelectQuery) {
                        if (parent instanceof OracleSelectQueryBlock) {
                            OracleSelectQueryBlock queryBlock = (OracleSelectQueryBlock) parent;
                            SQLTableSource from = queryBlock.getFrom();
                            if (from instanceof SQLExprTableSource) {
                                selectQuery = queryBlock;
                            } else if (from instanceof SQLSubqueryTableSource) {
                                SQLSelect subSelect = ((SQLSubqueryTableSource) from).getSelect();
                                if (subSelect.getQuery() instanceof OracleSelectQueryBlock) {
                                    selectQuery = (OracleSelectQueryBlock) subSelect.getQuery();
                                }
                            }
                        }
                        break;
                    }
                }
            }


            if (selectQuery != null) {
                SQLOrderBy orderBy = selectQuery.getOrderBy();

                SQLObject parent = selectQuery.getParent();
                if (orderBy == null && parent instanceof SQLSelect) {
                    SQLSelect select = (SQLSelect) parent;
                    orderBy = select.getOrderBy();
                }

                if (orderBy == null || orderBy.getItems().size() == 0) {
                    unorderedLimitCount++;
                }
            }

            return true;
        }

        @Override
        public boolean visit(OracleSelectQueryBlock queryBlock) {
            boolean isExprTableSrc = queryBlock.getFrom() instanceof SQLExprTableSource;

            if (!isExprTableSrc) {
                return true;
            }

            boolean rownum = false;
            for (SQLSelectItem item : queryBlock.getSelectList()) {
                SQLExpr itemExpr = item.getExpr();
                if (itemExpr instanceof SQLIdentifierExpr) {
                    if (((SQLIdentifierExpr) itemExpr).getName().equalsIgnoreCase("ROWNUM")) {
                        rownum = true;
                        break;
                    }
                }
            }

            if (!rownum) {
                return true;
            }

            SQLObject parent = queryBlock.getParent();
            if (!(parent instanceof SQLSelect)) {
                return true;
            }

            SQLSelect select = (SQLSelect) parent;

            if (select.getOrderBy() == null || select.getOrderBy().getItems().size() == 0) {
                unorderedLimitCount++;
            }

            return false;
        }
    }
}
