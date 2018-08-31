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
package cn.newphy.druid.sql.dialect.odps.visitor;

import cn.newphy.druid.sql.dialect.hive.ast.HiveInsertStatement;
import cn.newphy.druid.sql.dialect.hive.ast.HiveMultiInsertStatement;
import cn.newphy.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsAddStatisticStatement;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsAnalyzeTableStatement;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsCreateTableStatement;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsGrantStmt;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsInsertStatement;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsListStmt;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsReadStatement;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsRemoveStatisticStatement;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsSelectQueryBlock;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsSetLabelStatement;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsShowGrantsStmt;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsShowPartitionsStmt;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsShowStatisticStmt;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsStatisticClause.ColumnMax;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsStatisticClause.ColumnMin;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsStatisticClause.ColumnSum;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsStatisticClause.ExpressionCondition;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsStatisticClause.NullValue;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsStatisticClause.TableCount;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsUDTFSQLSelectItem;
import cn.newphy.druid.sql.dialect.odps.ast.OdpsValuesTableSource;
import cn.newphy.druid.sql.visitor.SQLASTVisitorAdapter;
import cn.newphy.druid.sql.dialect.hive.ast.HiveInsert;
import cn.newphy.druid.sql.dialect.odps.ast.*;

public class OdpsASTVisitorAdapter extends SQLASTVisitorAdapter implements OdpsASTVisitor {

    @Override
    public void endVisit(OdpsCreateTableStatement x) {
        
    }

    @Override
    public boolean visit(OdpsCreateTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(OdpsInsertStatement x) {
        
    }

    @Override
    public boolean visit(OdpsInsertStatement x) {
        return true;
    }

    @Override
    public void endVisit(HiveInsert x) {
        
    }

    public boolean visit(HiveCreateTableStatement x) {
        return false;
    }

    public void endVisit(HiveCreateTableStatement x) {

    }

    public boolean visit(HiveMultiInsertStatement x) {
        return false;
    }

    public void endVisit(HiveMultiInsertStatement x) {

    }

    public boolean visit(HiveInsertStatement x) {
        return false;
    }

    public void endVisit(HiveInsertStatement x) {

    }

    @Override
    public boolean visit(HiveInsert x) {
        return true;
    }

    @Override
    public void endVisit(OdpsUDTFSQLSelectItem x) {
        
    }

    @Override
    public boolean visit(OdpsUDTFSQLSelectItem x) {
        return true;
    }

    @Override
    public void endVisit(OdpsShowPartitionsStmt x) {
        
    }

    @Override
    public boolean visit(OdpsShowPartitionsStmt x) {
        return true;
    }

    @Override
    public void endVisit(OdpsShowStatisticStmt x) {
        
    }

    @Override
    public boolean visit(OdpsShowStatisticStmt x) {
        return true;
    }

    @Override
    public void endVisit(OdpsSetLabelStatement x) {
        
    }

    @Override
    public boolean visit(OdpsSetLabelStatement x) {
        return true;
    }

    @Override
    public void endVisit(OdpsSelectQueryBlock x) {
        
    }

    @Override
    public boolean visit(OdpsSelectQueryBlock x) {
        return true;
    }
    
    @Override
    public void endVisit(OdpsAnalyzeTableStatement x) {
        
    }
    
    @Override
    public boolean visit(OdpsAnalyzeTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(OdpsAddStatisticStatement x) {
        
    }

    @Override
    public boolean visit(OdpsAddStatisticStatement x) {
        return true;
    }
    
    @Override
    public void endVisit(OdpsRemoveStatisticStatement x) {
        
    }
    
    @Override
    public boolean visit(OdpsRemoveStatisticStatement x) {
        return true;
    }
    
    @Override
    public void endVisit(TableCount x) {
        
    }
    
    @Override
    public boolean visit(TableCount x) {
        return true;
    }
    
    @Override
    public void endVisit(ExpressionCondition x) {
        
    }
    
    @Override
    public boolean visit(ExpressionCondition x) {
        return true;
    }
    
    @Override
    public void endVisit(NullValue x) {
        
    }
    
    @Override
    public boolean visit(NullValue x) {
        return true;
    }
    
    @Override
    public void endVisit(ColumnSum x) {
        
    }
    
    @Override
    public boolean visit(ColumnSum x) {
        return true;
    }
    
    @Override
    public void endVisit(ColumnMax x) {
        
    }
    
    @Override
    public boolean visit(ColumnMax x) {
        return true;
    }
    
    @Override
    public void endVisit(ColumnMin x) {
        
    }
    
    @Override
    public boolean visit(ColumnMin x) {
        return true;
    }
    
    @Override
    public void endVisit(OdpsReadStatement x) {
        
    }
    
    @Override
    public boolean visit(OdpsReadStatement x) {
        return true;
    }

    @Override
    public void endVisit(OdpsShowGrantsStmt x) {
        
    }

    @Override
    public boolean visit(OdpsShowGrantsStmt x) {
        return true;
    }

    @Override
    public void endVisit(OdpsListStmt x) {
        
    }

    @Override
    public boolean visit(OdpsListStmt x) {
        return true;
    }

    @Override
    public void endVisit(OdpsGrantStmt x) {
        
    }

    @Override
    public boolean visit(OdpsGrantStmt x) {
        return true;
    }

    @Override
    public void endVisit(OdpsValuesTableSource x) {

    }

    @Override
    public boolean visit(OdpsValuesTableSource x) {
        return true;
    }
}
