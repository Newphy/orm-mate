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
package cn.newphy.druid.sql.dialect.db2.parser;

import cn.newphy.druid.sql.ast.SQLDataType;
import cn.newphy.druid.sql.ast.SQLExpr;
import cn.newphy.druid.sql.ast.SQLStatement;
import cn.newphy.druid.sql.ast.statement.SQLAlterTableAlterColumn;
import cn.newphy.druid.sql.ast.statement.SQLColumnDefinition;
import cn.newphy.druid.sql.dialect.db2.ast.stmt.DB2ValuesStatement;
import cn.newphy.druid.sql.parser.Lexer;
import cn.newphy.druid.sql.parser.ParserException;
import cn.newphy.druid.sql.parser.SQLCreateTableParser;
import cn.newphy.druid.sql.parser.SQLParserFeature;
import cn.newphy.druid.sql.parser.SQLStatementParser;
import cn.newphy.druid.sql.parser.Token;
import cn.newphy.druid.util.FnvHash;
import java.util.List;

import cn.newphy.druid.sql.parser.*;

public class DB2StatementParser extends SQLStatementParser {
    public DB2StatementParser(String sql) {
        super (new DB2ExprParser(sql));
    }

    public DB2StatementParser(String sql, SQLParserFeature... features) {
        super (new DB2ExprParser(sql, features));
    }

    public DB2StatementParser(Lexer lexer){
        super(new DB2ExprParser(lexer));
    }
    
    public DB2SelectParser createSQLSelectParser() {
        return new DB2SelectParser(this.exprParser, selectListCache);
    }
    
    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.token() == Token.VALUES) {
            lexer.nextToken();
            DB2ValuesStatement stmt = new DB2ValuesStatement();
            stmt.setExpr(this.exprParser.expr());
            statementList.add(stmt);
            return true;
        }
        
        return false;
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new DB2CreateTableParser(this.exprParser);
    }

    protected SQLAlterTableAlterColumn parseAlterColumn() {
        if (lexer.token() == Token.COLUMN) {
            lexer.nextToken();
        }

        SQLColumnDefinition column = this.exprParser.parseColumn();

        SQLAlterTableAlterColumn alterColumn = new SQLAlterTableAlterColumn();
        alterColumn.setColumn(column);

        if (column.getDataType() == null && column.getConstraints().size() == 0) {
            if (lexer.token() == Token.SET) {
                lexer.nextToken();
                if (lexer.token() == Token.NOT) {
                    lexer.nextToken();
                    accept(Token.NULL);
                    alterColumn.setSetNotNull(true);
                } else if (lexer.token() == Token.DEFAULT) {
                    lexer.nextToken();
                    SQLExpr defaultValue = this.exprParser.expr();
                    alterColumn.setSetDefault(defaultValue);
                } else if (lexer.identifierEquals(FnvHash.Constants.DATA)) {
                    lexer.nextToken();
                    acceptIdentifier("TYPE");
                    SQLDataType dataType = this.exprParser.parseDataType();
                    alterColumn.setDataType(dataType);
                } else {
                    throw new ParserException("TODO : " + lexer.info());
                }
            } else if (lexer.token() == Token.DROP) {
                lexer.nextToken();
                if (lexer.token() == Token.NOT) {
                    lexer.nextToken();
                    accept(Token.NULL);
                    alterColumn.setDropNotNull(true);
                } else {
                    accept(Token.DEFAULT);
                    alterColumn.setDropDefault(true);
                }
            }
        }
        return alterColumn;
    }
}
