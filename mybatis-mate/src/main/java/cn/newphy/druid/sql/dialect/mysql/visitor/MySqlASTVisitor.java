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
package cn.newphy.druid.sql.dialect.mysql.visitor;

import cn.newphy.druid.sql.dialect.mysql.ast.MySqlForceIndexHint;
import cn.newphy.druid.sql.dialect.mysql.ast.MySqlIgnoreIndexHint;
import cn.newphy.druid.sql.dialect.mysql.ast.MySqlKey;
import cn.newphy.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import cn.newphy.druid.sql.dialect.mysql.ast.MySqlUnique;
import cn.newphy.druid.sql.dialect.mysql.ast.MySqlUseIndexHint;
import cn.newphy.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import cn.newphy.druid.sql.dialect.mysql.ast.clause.MySqlCaseStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.clause.MySqlCaseStatement.MySqlWhenStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.clause.MySqlCursorDeclareStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.clause.MySqlDeclareConditionStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.clause.MySqlDeclareHandlerStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.clause.MySqlDeclareStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.clause.MySqlIterateStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.clause.MySqlLeaveStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.clause.MySqlRepeatStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.clause.MySqlSelectIntoStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.expr.MySqlCharExpr;
import cn.newphy.druid.sql.dialect.mysql.ast.expr.MySqlExtractExpr;
import cn.newphy.druid.sql.dialect.mysql.ast.expr.MySqlMatchAgainstExpr;
import cn.newphy.druid.sql.dialect.mysql.ast.expr.MySqlOrderingExpr;
import cn.newphy.druid.sql.dialect.mysql.ast.expr.MySqlOutFileExpr;
import cn.newphy.druid.sql.dialect.mysql.ast.expr.MySqlUserName;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.CobarShowStatus;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlAlterEventStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlAlterLogFileGroupStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlAlterServerStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableAlterColumn;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableChangeColumn;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableDiscardTablespace;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableImportTablespace;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableModifyColumn;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableOption;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlAlterTablespaceStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlAlterUserStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlAnalyzeStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlBinlogStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlChecksumTableStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlCreateAddLogFileGroupStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlCreateEventStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlCreateServerStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableSpaceStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlCreateUserStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlEventSchedule;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlExecuteStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlExplainStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlFlushStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlHelpStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlHintStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlKillStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlLoadDataInFileStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlLoadXmlStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlLockTableStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlOptimizeStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlPartitionByKey;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlPrepareStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlRenameTableStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlResetStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlSetTransactionStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowAuthorsStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowBinLogEventsStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowBinaryLogsStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowCharacterSetStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowCollationStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowColumnsStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowContributorsStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateDatabaseStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateEventStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateFunctionStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateProcedureStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateTableStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateTriggerStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateViewStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowDatabasePartitionStatusStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowDatabasesStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowEngineStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowEnginesStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowErrorsStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowEventsStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowFunctionCodeStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowFunctionStatusStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowGrantsStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowIndexesStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowKeysStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowMasterLogsStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowMasterStatusStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowOpenTablesStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowPluginsStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowPrivilegesStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowProcedureCodeStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowProcedureStatusStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowProcessListStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowProfileStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowProfilesStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowRelayLogEventsStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowSlaveHostsStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowSlaveStatusStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowStatusStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowTableStatusStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowTriggersStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowVariantsStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlShowWarningsStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlSubPartitionByKey;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlSubPartitionByList;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlUnlockTablesStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MySqlUpdateTableSource;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.MysqlDeallocatePrepareStatement;
import cn.newphy.druid.sql.dialect.mysql.ast.statement.*;
import cn.newphy.druid.sql.visitor.SQLASTVisitor;

public interface MySqlASTVisitor extends SQLASTVisitor {
    boolean visit(MySqlTableIndex x);

    void endVisit(MySqlTableIndex x);

    boolean visit(MySqlKey x);

    void endVisit(MySqlKey x);

    boolean visit(MySqlPrimaryKey x);

    void endVisit(MySqlPrimaryKey x);

    boolean visit(MySqlUnique x);

    void endVisit(MySqlUnique x);

    boolean visit(MysqlForeignKey x);

    void endVisit(MysqlForeignKey x);

    void endVisit(MySqlExtractExpr x);

    boolean visit(MySqlExtractExpr x);

    void endVisit(MySqlMatchAgainstExpr x);

    boolean visit(MySqlMatchAgainstExpr x);

    void endVisit(MySqlPrepareStatement x);

    boolean visit(MySqlPrepareStatement x);

    void endVisit(MySqlExecuteStatement x);

    boolean visit(MysqlDeallocatePrepareStatement x);

    void endVisit(MysqlDeallocatePrepareStatement x);

    boolean visit(MySqlExecuteStatement x);

    void endVisit(MySqlDeleteStatement x);

    boolean visit(MySqlDeleteStatement x);

    void endVisit(MySqlInsertStatement x);

    boolean visit(MySqlInsertStatement x);

    void endVisit(MySqlLoadDataInFileStatement x);

    boolean visit(MySqlLoadDataInFileStatement x);

    void endVisit(MySqlLoadXmlStatement x);

    boolean visit(MySqlLoadXmlStatement x);

    void endVisit(MySqlShowColumnsStatement x);

    boolean visit(MySqlShowColumnsStatement x);

    void endVisit(MySqlShowDatabasesStatement x);

    boolean visit(MySqlShowDatabasesStatement x);

    void endVisit(MySqlShowWarningsStatement x);

    boolean visit(MySqlShowWarningsStatement x);

    void endVisit(MySqlShowStatusStatement x);

    boolean visit(MySqlShowStatusStatement x);

    void endVisit(MySqlShowAuthorsStatement x);

    boolean visit(MySqlShowAuthorsStatement x);

    void endVisit(CobarShowStatus x);

    boolean visit(CobarShowStatus x);

    void endVisit(MySqlKillStatement x);

    boolean visit(MySqlKillStatement x);

    void endVisit(MySqlBinlogStatement x);

    boolean visit(MySqlBinlogStatement x);

    void endVisit(MySqlResetStatement x);

    boolean visit(MySqlResetStatement x);

    void endVisit(MySqlCreateUserStatement x);

    boolean visit(MySqlCreateUserStatement x);

    void endVisit(MySqlCreateUserStatement.UserSpecification x);

    boolean visit(MySqlCreateUserStatement.UserSpecification x);

    void endVisit(MySqlPartitionByKey x);

    boolean visit(MySqlPartitionByKey x);

    boolean visit(MySqlSelectQueryBlock x);

    void endVisit(MySqlSelectQueryBlock x);

    boolean visit(MySqlOutFileExpr x);

    void endVisit(MySqlOutFileExpr x);

    boolean visit(MySqlExplainStatement x);

    void endVisit(MySqlExplainStatement x);

    boolean visit(MySqlUpdateStatement x);

    void endVisit(MySqlUpdateStatement x);

    boolean visit(MySqlSetTransactionStatement x);

    void endVisit(MySqlSetTransactionStatement x);

    boolean visit(MySqlShowBinaryLogsStatement x);

    void endVisit(MySqlShowBinaryLogsStatement x);

    boolean visit(MySqlShowMasterLogsStatement x);

    void endVisit(MySqlShowMasterLogsStatement x);

    boolean visit(MySqlShowCharacterSetStatement x);

    void endVisit(MySqlShowCharacterSetStatement x);

    boolean visit(MySqlShowCollationStatement x);

    void endVisit(MySqlShowCollationStatement x);

    boolean visit(MySqlShowBinLogEventsStatement x);

    void endVisit(MySqlShowBinLogEventsStatement x);

    boolean visit(MySqlShowContributorsStatement x);

    void endVisit(MySqlShowContributorsStatement x);

    boolean visit(MySqlShowCreateDatabaseStatement x);

    void endVisit(MySqlShowCreateDatabaseStatement x);

    boolean visit(MySqlShowCreateEventStatement x);

    void endVisit(MySqlShowCreateEventStatement x);

    boolean visit(MySqlShowCreateFunctionStatement x);

    void endVisit(MySqlShowCreateFunctionStatement x);

    boolean visit(MySqlShowCreateProcedureStatement x);

    void endVisit(MySqlShowCreateProcedureStatement x);

    boolean visit(MySqlShowCreateTableStatement x);

    void endVisit(MySqlShowCreateTableStatement x);

    boolean visit(MySqlShowCreateTriggerStatement x);

    void endVisit(MySqlShowCreateTriggerStatement x);

    boolean visit(MySqlShowCreateViewStatement x);

    void endVisit(MySqlShowCreateViewStatement x);

    boolean visit(MySqlShowEngineStatement x);

    void endVisit(MySqlShowEngineStatement x);

    boolean visit(MySqlShowEnginesStatement x);

    void endVisit(MySqlShowEnginesStatement x);

    boolean visit(MySqlShowErrorsStatement x);

    void endVisit(MySqlShowErrorsStatement x);

    boolean visit(MySqlShowEventsStatement x);

    void endVisit(MySqlShowEventsStatement x);

    boolean visit(MySqlShowFunctionCodeStatement x);

    void endVisit(MySqlShowFunctionCodeStatement x);

    boolean visit(MySqlShowFunctionStatusStatement x);

    void endVisit(MySqlShowFunctionStatusStatement x);

    boolean visit(MySqlShowGrantsStatement x);

    void endVisit(MySqlShowGrantsStatement x);

    boolean visit(MySqlUserName x);

    void endVisit(MySqlUserName x);

    boolean visit(MySqlShowIndexesStatement x);

    void endVisit(MySqlShowIndexesStatement x);

    boolean visit(MySqlShowKeysStatement x);

    void endVisit(MySqlShowKeysStatement x);

    boolean visit(MySqlShowMasterStatusStatement x);

    void endVisit(MySqlShowMasterStatusStatement x);

    boolean visit(MySqlShowOpenTablesStatement x);

    void endVisit(MySqlShowOpenTablesStatement x);

    boolean visit(MySqlShowPluginsStatement x);

    void endVisit(MySqlShowPluginsStatement x);

    boolean visit(MySqlShowPrivilegesStatement x);

    void endVisit(MySqlShowPrivilegesStatement x);

    boolean visit(MySqlShowProcedureCodeStatement x);

    void endVisit(MySqlShowProcedureCodeStatement x);

    boolean visit(MySqlShowProcedureStatusStatement x);

    void endVisit(MySqlShowProcedureStatusStatement x);

    boolean visit(MySqlShowProcessListStatement x);

    void endVisit(MySqlShowProcessListStatement x);

    boolean visit(MySqlShowProfileStatement x);

    void endVisit(MySqlShowProfileStatement x);

    boolean visit(MySqlShowProfilesStatement x);

    void endVisit(MySqlShowProfilesStatement x);

    boolean visit(MySqlShowRelayLogEventsStatement x);

    void endVisit(MySqlShowRelayLogEventsStatement x);

    boolean visit(MySqlShowSlaveHostsStatement x);

    void endVisit(MySqlShowSlaveHostsStatement x);

    boolean visit(MySqlShowSlaveStatusStatement x);

    void endVisit(MySqlShowSlaveStatusStatement x);

    boolean visit(MySqlShowTableStatusStatement x);

    void endVisit(MySqlShowTableStatusStatement x);

    boolean visit(MySqlShowTriggersStatement x);

    void endVisit(MySqlShowTriggersStatement x);

    boolean visit(MySqlShowVariantsStatement x);

    void endVisit(MySqlShowVariantsStatement x);

    boolean visit(MySqlRenameTableStatement.Item x);

    void endVisit(MySqlRenameTableStatement.Item x);

    boolean visit(MySqlRenameTableStatement x);

    void endVisit(MySqlRenameTableStatement x);

    boolean visit(MySqlUseIndexHint x);

    void endVisit(MySqlUseIndexHint x);

    boolean visit(MySqlIgnoreIndexHint x);

    void endVisit(MySqlIgnoreIndexHint x);

    boolean visit(MySqlLockTableStatement x);

    void endVisit(MySqlLockTableStatement x);

    boolean visit(MySqlLockTableStatement.Item x);

    void endVisit(MySqlLockTableStatement.Item x);

    boolean visit(MySqlUnlockTablesStatement x);

    void endVisit(MySqlUnlockTablesStatement x);

    boolean visit(MySqlForceIndexHint x);

    void endVisit(MySqlForceIndexHint x);

    boolean visit(MySqlAlterTableChangeColumn x);

    void endVisit(MySqlAlterTableChangeColumn x);

    boolean visit(MySqlAlterTableOption x);

    void endVisit(MySqlAlterTableOption x);

    boolean visit(MySqlCreateTableStatement x);

    void endVisit(MySqlCreateTableStatement x);

    boolean visit(MySqlHelpStatement x);

    void endVisit(MySqlHelpStatement x);

    boolean visit(MySqlCharExpr x);

    void endVisit(MySqlCharExpr x);

    boolean visit(MySqlAlterTableModifyColumn x);

    void endVisit(MySqlAlterTableModifyColumn x);

    boolean visit(MySqlAlterTableDiscardTablespace x);

    void endVisit(MySqlAlterTableDiscardTablespace x);

    boolean visit(MySqlAlterTableImportTablespace x);

    void endVisit(MySqlAlterTableImportTablespace x);

    boolean visit(MySqlCreateTableStatement.TableSpaceOption x);

    void endVisit(MySqlCreateTableStatement.TableSpaceOption x);

    boolean visit(MySqlAnalyzeStatement x);

    void endVisit(MySqlAnalyzeStatement x);

    boolean visit(MySqlAlterUserStatement x);

    void endVisit(MySqlAlterUserStatement x);

    boolean visit(MySqlOptimizeStatement x);

    void endVisit(MySqlOptimizeStatement x);

    boolean visit(MySqlHintStatement x);

    void endVisit(MySqlHintStatement x);

    boolean visit(MySqlOrderingExpr x);

    void endVisit(MySqlOrderingExpr x);

    boolean visit(MySqlCaseStatement x);

    void endVisit(MySqlCaseStatement x);

    boolean visit(MySqlDeclareStatement x);

    void endVisit(MySqlDeclareStatement x);

    boolean visit(MySqlSelectIntoStatement x);

    void endVisit(MySqlSelectIntoStatement x);

    boolean visit(MySqlWhenStatement x);

    void endVisit(MySqlWhenStatement x);

    boolean visit(MySqlLeaveStatement x);

    void endVisit(MySqlLeaveStatement x);

    boolean visit(MySqlIterateStatement x);

    void endVisit(MySqlIterateStatement x);

    boolean visit(MySqlRepeatStatement x);

    void endVisit(MySqlRepeatStatement x);

    boolean visit(MySqlCursorDeclareStatement x);

    void endVisit(MySqlCursorDeclareStatement x);

    boolean visit(MySqlUpdateTableSource x);

    void endVisit(MySqlUpdateTableSource x);

    boolean visit(MySqlAlterTableAlterColumn x);

    void endVisit(MySqlAlterTableAlterColumn x);

    boolean visit(MySqlSubPartitionByKey x);

    void endVisit(MySqlSubPartitionByKey x);

    boolean visit(MySqlSubPartitionByList x);

    void endVisit(MySqlSubPartitionByList x);

    boolean visit(MySqlDeclareHandlerStatement x);

    void endVisit(MySqlDeclareHandlerStatement x);

    boolean visit(MySqlDeclareConditionStatement x);

    void endVisit(MySqlDeclareConditionStatement x);

    boolean visit(MySqlFlushStatement x);

    void endVisit(MySqlFlushStatement x);

    boolean visit(MySqlEventSchedule x);
    void endVisit(MySqlEventSchedule x);

    boolean visit(MySqlCreateEventStatement x);
    void endVisit(MySqlCreateEventStatement x);

    boolean visit(MySqlCreateAddLogFileGroupStatement x);
    void endVisit(MySqlCreateAddLogFileGroupStatement x);

    boolean visit(MySqlCreateServerStatement x);
    void endVisit(MySqlCreateServerStatement x);

    boolean visit(MySqlCreateTableSpaceStatement x);
    void endVisit(MySqlCreateTableSpaceStatement x);

    boolean visit(MySqlAlterEventStatement x);
    void endVisit(MySqlAlterEventStatement x);

    boolean visit(MySqlAlterLogFileGroupStatement x);
    void endVisit(MySqlAlterLogFileGroupStatement x);

    boolean visit(MySqlAlterServerStatement x);
    void endVisit(MySqlAlterServerStatement x);

    boolean visit(MySqlAlterTablespaceStatement x);
    void endVisit(MySqlAlterTablespaceStatement x);

    boolean visit(MySqlShowDatabasePartitionStatusStatement x);
    void endVisit(MySqlShowDatabasePartitionStatusStatement x);

    boolean visit(MySqlChecksumTableStatement x);
    void endVisit(MySqlChecksumTableStatement x);

} //
