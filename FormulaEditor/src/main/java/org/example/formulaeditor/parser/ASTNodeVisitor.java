package org.example.formulaeditor.parser;

import org.example.formulaeditor.parser.ast.Boolean;
import org.example.formulaeditor.parser.ast.Number;
import org.example.formulaeditor.parser.ast.*;

public interface ASTNodeVisitor<T> {

    <N extends java.lang.Number> T visitNumber(Number<N> n);

    T visitCell(Cell n);

    T visitCellRange(CellRange n);

    T visitString(ExcelString n);

    T visitFunctionCall(FunctionCall n);

    T visitBoolean(Boolean n);

    T visitBinary(Binary n);

    T visitNegate(Negate n);

}
