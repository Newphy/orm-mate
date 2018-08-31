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
package cn.newphy.druid.sql.ast.statement;

import cn.newphy.druid.sql.ast.SQLCommentHint;
import cn.newphy.druid.sql.ast.SQLStatementImpl;
import cn.newphy.druid.sql.ast.SQLExpr;
import cn.newphy.druid.sql.ast.SQLObject;
import cn.newphy.druid.sql.visitor.SQLASTVisitor;

import java.util.Collections;
import java.util.List;

public class SQLSelectStatement extends SQLStatementImpl {

    protected SQLSelect select;

    public SQLSelectStatement(){

    }

    public SQLSelectStatement(String dbType){
        super (dbType);
    }

    public SQLSelectStatement(SQLSelect select){
        this.setSelect(select);
    }

    public SQLSelectStatement(SQLSelect select, String dbType){
        this(dbType);
        this.setSelect(select);
    }

    public SQLSelect getSelect() {
        return this.select;
    }

    public void setSelect(SQLSelect select) {
        if (select != null) {
            select.setParent(this);
        }
        this.select = select;
    }

    public void output(StringBuffer buf) {
        this.select.output(buf);
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.select);
        }
        visitor.endVisit(this);
    }

    public SQLSelectStatement clone() {
        SQLSelectStatement x = new SQLSelectStatement();
        if (select != null) {
            x.setSelect(select.clone());
        }
        if (headHints != null) {
            for (SQLCommentHint h : headHints) {
                SQLCommentHint h2 = h.clone();
                h2.setParent(x);
                x.headHints.add(h2);
            }
        }
        return x;
    }

    @Override
    public List<SQLObject> getChildren() {
        return Collections.<SQLObject>singletonList(select);
    }

    public boolean addWhere(SQLExpr where) {
        return select.addWhere(where);
    }
}
