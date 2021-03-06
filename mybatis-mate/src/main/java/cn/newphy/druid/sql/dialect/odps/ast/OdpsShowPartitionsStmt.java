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
package cn.newphy.druid.sql.dialect.odps.ast;

import cn.newphy.druid.sql.ast.SQLStatementImpl;
import cn.newphy.druid.sql.ast.statement.SQLExprTableSource;
import cn.newphy.druid.sql.dialect.odps.visitor.OdpsASTVisitor;
import cn.newphy.druid.sql.visitor.SQLASTVisitor;
import cn.newphy.druid.util.JdbcConstants;

public class OdpsShowPartitionsStmt extends SQLStatementImpl {

    private SQLExprTableSource tableSource;
    
    public OdpsShowPartitionsStmt() {
        super (JdbcConstants.ODPS);
    }

    public SQLExprTableSource getTableSource() {
        return tableSource;
    }

    public void setTableSource(SQLExprTableSource tableSource) {
        this.tableSource = tableSource;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        accept0((OdpsASTVisitor) visitor);
    }
    
    protected void accept0(OdpsASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSource);
        }
        visitor.endVisit(this);
    }
}
