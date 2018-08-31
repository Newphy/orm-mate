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

import cn.newphy.druid.sql.ast.SQLExpr;
import cn.newphy.druid.sql.ast.SQLStatementImpl;
import cn.newphy.druid.sql.dialect.odps.visitor.OdpsASTVisitor;
import cn.newphy.druid.sql.visitor.SQLASTVisitor;

public class OdpsShowGrantsStmt extends SQLStatementImpl {

    private SQLExpr user;

    private SQLExpr objectType;
    
    @Override
    protected void accept0(SQLASTVisitor visitor) {
        accept0((OdpsASTVisitor) visitor);
    }

    public void accept0(OdpsASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, user);
            acceptChild(visitor, objectType);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getUser() {
        return user;
    }

    public void setUser(SQLExpr user) {
        if (user != null) {
            user.setParent(this);
        }
        this.user = user;
    }

    public SQLExpr getObjectType() {
        return objectType;
    }

    public void setObjectType(SQLExpr objectType) {
        if (objectType != null) {
            objectType.setParent(this);
        }
        this.objectType = objectType;
    }
}