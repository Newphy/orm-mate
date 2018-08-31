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
package cn.newphy.druid.sql.dialect.oracle.ast.stmt;

import cn.newphy.druid.sql.SQLUtils;
import cn.newphy.druid.sql.ast.SQLExpr;
import cn.newphy.druid.sql.ast.SQLStatementImpl;
import cn.newphy.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import cn.newphy.druid.sql.visitor.SQLASTVisitor;
import cn.newphy.druid.util.JdbcConstants;

public class OracleRunStatement extends SQLStatementImpl implements OracleStatement {

    private SQLExpr     expr;

    public OracleRunStatement() {
        super (JdbcConstants.ORACLE);
    }
    public OracleRunStatement(SQLExpr expr) {
        super (JdbcConstants.ORACLE);
        this.setExpr(expr);
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, expr);
        }
        visitor.endVisit(this);
    }

    protected void accept0(SQLASTVisitor visitor) {
        accept0((OracleASTVisitor) visitor);
    }

    public String toString() {
        return SQLUtils.toOracleString(this);
    }

    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr expr) {
        if (expr != null) {
            expr.setParent(this);
        }
        this.expr = expr;
    }
}
