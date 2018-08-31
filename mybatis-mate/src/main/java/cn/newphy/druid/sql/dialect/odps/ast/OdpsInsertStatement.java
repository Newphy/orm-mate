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
import cn.newphy.druid.sql.ast.statement.SQLTableSource;
import cn.newphy.druid.sql.dialect.hive.ast.HiveInsert;
import cn.newphy.druid.sql.dialect.odps.visitor.OdpsASTVisitor;
import cn.newphy.druid.sql.visitor.SQLASTVisitor;
import cn.newphy.druid.util.JdbcConstants;
import java.util.ArrayList;
import java.util.List;

public class OdpsInsertStatement extends SQLStatementImpl {

    private SQLTableSource from;

    private List<HiveInsert>       items = new ArrayList<HiveInsert>();
    
    public OdpsInsertStatement() {
        super (JdbcConstants.ODPS);
    }

    public void setFrom(SQLTableSource from) {
        this.from = from;
    }

    public SQLTableSource getFrom() {
        return from;
    }

    public List<HiveInsert> getItems() {
        return items;
    }
    
    public void addItem(HiveInsert item) {
        if (item != null) {
            item.setParent(this);
        }
        this.items.add(item);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        accept0((OdpsASTVisitor) visitor);
    }
    
    protected void accept0(OdpsASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, from);
            acceptChild(visitor, items);
        }
        visitor.endVisit(this);
    }
}
